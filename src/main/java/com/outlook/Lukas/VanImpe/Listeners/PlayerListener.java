package com.outlook.Lukas.VanImpe.Listeners;

import com.outlook.Lukas.VanImpe.Main;
import com.outlook.Lukas.VanImpe.Data.PlayerDataManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.UUID;

public class PlayerListener implements Listener {

    private final PlayerDataManager dataManager;
    private final Main plugin;

    public PlayerListener(Main plugin) {
        this.dataManager = new PlayerDataManager(plugin.getPlayerDataConfig());
        this.plugin = plugin;

        // Start periodic task to update playtime for display purposes
        startPlaytimeDisplayTask();
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player deceased = event.getEntity();
        UUID deceasedId = deceased.getUniqueId();

        // Record playtime from join to death before removing metadata
        if (deceased.hasMetadata("joinTimestamp")) {
            List<MetadataValue> metadataValues = deceased.getMetadata("joinTimestamp");
            if (!metadataValues.isEmpty()) {
                long joinTimestamp = metadataValues.get(0).asLong();
                long playtimeSinceJoin = System.currentTimeMillis() - joinTimestamp;

                // Add playtime from join to death
                dataManager.addPlaytime(deceasedId, playtimeSinceJoin);

                // Log the playtime
                plugin.getLogger().info(deceased.getName() + " died. Total playtime: " + formatPlaytime(playtimeSinceJoin));
            }
            // Remove joinTimestamp metadata after recording playtime
            deceased.removeMetadata("joinTimestamp", plugin);
        }

        // Add death and set ban logic
        dataManager.incrementDeaths(deceasedId);
        long banExpiration = System.currentTimeMillis() + 600_000; // 10 minutes
        dataManager.setBanExpiration(deceasedId, banExpiration);

        // Kick player if online
        if (deceased.isOnline()) {
            Component kickMessage = Component.text("You have been banned for 10 minutes due to a death timeout.").color(TextColor.color(255, 0, 0)); // Red color
            deceased.kick(kickMessage); // Corrected method for Adventure API
            plugin.getLogger().info(deceased.getName() + " has been banned for 10 minutes. Expiration time: " + banExpiration);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();
        long currentTime = System.currentTimeMillis();

        if (dataManager.isBanned(playerId)) {
            long banExpiration = dataManager.getBanExpiration(playerId);
            long remainingBanTime = banExpiration - currentTime;

            if (remainingBanTime > 0) {
                long minutes = remainingBanTime / 60000;
                long seconds = (remainingBanTime % 60000) / 1000;
                Component kickMessage = Component.text("You are banned for " + minutes + " minutes and " + seconds + " seconds.").color(TextColor.color(255, 0, 0)); // Red color
                player.kick(kickMessage); // Corrected method for Adventure API

                plugin.getLogger().info(player.getName() + " attempted to join but is still banned. Remaining ban time: " + minutes + " minutes and " + seconds + " seconds.");
                return;
            }
        }

        // Set join timestamp metadata
        long joinTime = System.currentTimeMillis();
        player.setMetadata("joinTimestamp", new FixedMetadataValue(plugin, joinTime));

        // Log player join
        plugin.getLogger().info(player.getName() + " joined the server. Join timestamp: " + joinTime);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        long joinTimestamp = 0;

        // Safely access player metadata
        List<MetadataValue> metadataValues = player.getMetadata("joinTimestamp");
        if (!metadataValues.isEmpty()) {
            MetadataValue metadataValue = metadataValues.get(0);
            if (metadataValue instanceof FixedMetadataValue) {
                joinTimestamp = ((FixedMetadataValue) metadataValue).asLong();
            }
        }

        if (joinTimestamp > 0) {
            long playtimeSinceJoin = System.currentTimeMillis() - joinTimestamp;

            // Add playtime since the player joined
            dataManager.addPlaytime(player.getUniqueId(), playtimeSinceJoin);

            // Log player quit and playtime
            plugin.getLogger().info(player.getName() + " left the server. Total playtime: " + formatPlaytime(playtimeSinceJoin));
        } else {
            plugin.getLogger().warning(player.getName() + " left the server but no join timestamp was found.");
        }

        // Remove joinTimestamp metadata
        player.removeMetadata("joinTimestamp", plugin);
    }

    private String formatPlaytime(long playtimeMillis) {
        long totalSeconds = playtimeMillis / 1000;
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;
        return hours + " hours, " + minutes + " minutes, and " + seconds + " seconds";
    }

    private void startPlaytimeDisplayTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : plugin.getServer().getOnlinePlayers()) {
                    long joinTimestamp = 0;
                    List<MetadataValue> metadataValues = player.getMetadata("joinTimestamp");
                    if (!metadataValues.isEmpty()) {
                        MetadataValue metadataValue = metadataValues.get(0);
                        if (metadataValue instanceof FixedMetadataValue) {
                            joinTimestamp = ((FixedMetadataValue) metadataValue).asLong();
                        }
                    }

                    if (joinTimestamp > 0) {
                        long playtimeSinceJoin = System.currentTimeMillis() - joinTimestamp;
                        player.sendMessage("Current playtime: " + formatPlaytime(playtimeSinceJoin));
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 8520L); // 6 minutes and 66 seconds in ticks (8520 ticks)
    }
}
