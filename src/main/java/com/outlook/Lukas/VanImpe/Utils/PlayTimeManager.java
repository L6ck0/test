package com.outlook.Lukas.VanImpe.Utils;

import com.outlook.Lukas.VanImpe.Main;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayTimeManager {
    private final Main plugin;

    public PlayTimeManager(Main plugin) {
        this.plugin = plugin;
        startPlaytimeUpdateTask();
    }

    private void startPlaytimeUpdateTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : plugin.getServer().getOnlinePlayers()) {
                    long joinTimestamp = player.getMetadata("joinTimestamp").get(0).asLong();
                    long currentTimestamp = System.currentTimeMillis();
                    long minutesOnline = (currentTimestamp - joinTimestamp) / 60000; // Calculate minutes

                    // Update player playtime in the config
                    long totalPlaytime = getPlaytimeFromConfig(player.getName()) + minutesOnline;
                    setPlaytimeInConfig(player.getName(), totalPlaytime);

                    // Update the joinTimestamp for next update
                    player.setMetadata("joinTimestamp", new FixedMetadataValue(plugin, currentTimestamp));
                }
            }
        }.runTaskTimer(plugin, 0L, 1200L); // 1200 ticks = 1 minute
    }

    private long getPlaytimeFromConfig(String playerName) {
        return plugin.getPlayerDataConfig().getConfig().getLong("players." + playerName + ".playtime", 0L);
    }

    private void setPlaytimeInConfig(String playerName, long playtime) {
        plugin.getPlayerDataConfig().getConfig().set("players." + playerName + ".playtime", playtime);
        plugin.getPlayerDataConfig().save();
    }
}
