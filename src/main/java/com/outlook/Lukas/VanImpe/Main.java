package com.outlook.Lukas.VanImpe;

import com.outlook.Lukas.VanImpe.Commands.BanCommand;
import com.outlook.Lukas.VanImpe.Commands.SetPlayTimeCommand;
import com.outlook.Lukas.VanImpe.Commands.StatsCommand;
import com.outlook.Lukas.VanImpe.Listeners.PlayerListener;
import com.outlook.Lukas.VanImpe.Utils.CustomConfig;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;


import java.io.File;

public class Main extends JavaPlugin {

    private CustomConfig playerDataConfig;

    @Override
    public void onEnable() {
        // Create data folder if it doesn't exist
        File dataFolder = getDataFolder();
        if (!dataFolder.exists()) {
            boolean created = dataFolder.mkdirs();
            if (created) {
                getLogger().info("Data folder created successfully.");
            } else {
                getLogger().warning("Failed to create data folder. It might already exist or there may be a permissions issue.");
            }
        } else {
            getLogger().info("Data folder already exists.");
        }

        // Initialize custom config for player data
        playerDataConfig = new CustomConfig(new File(dataFolder, "playerdata.yml"), getLogger());
        playerDataConfig.load();

        // Register event listeners
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);

        // Register commands
        PluginCommand statsCommand = getCommand("stats");
        if (statsCommand != null) {
            statsCommand.setExecutor(new StatsCommand(this));
        } else {
            getLogger().warning("Command 'stats' is not registered in plugin.yml");
        }

        PluginCommand banCommand = getCommand("ban");
        if (banCommand != null) {
            banCommand.setExecutor(new BanCommand(this));
        } else {
            getLogger().warning("Command 'ban' is not registered in plugin.yml");
        }

        // Start scheduled task for updating playtime every 7 minutes and 28 seconds
        startPlaytimeUpdateTask();

        PluginCommand setPlaytimeCommand = getCommand("setplaytime");
        if (setPlaytimeCommand != null) {
            setPlaytimeCommand.setExecutor(new SetPlayTimeCommand(this));
        } else {
            getLogger().warning("Command 'setplaytime' is not registered in plugin.yml");
        }
    }

    @Override
    public void onDisable() {
        // Save player data on disable
        if (playerDataConfig != null) {
            playerDataConfig.save();
        } else {
            getLogger().warning("PlayerDataConfig is not initialized.");
        }
    }

    public CustomConfig getPlayerDataConfig() {
        return playerDataConfig;
    }

    // Schedule the playtime update task
    private void startPlaytimeUpdateTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : getServer().getOnlinePlayers()) {
                    long joinTimestamp = player.getMetadata("joinTimestamp").get(0).asLong();
                    long playtimeSinceLastCheck = System.currentTimeMillis() - joinTimestamp;
                    // Display playtime update for the player (optional)
                    player.sendMessage("Playtime: " + formatPlaytime(playtimeSinceLastCheck));
                    // Update the joinTimestamp for next update
                    player.setMetadata("joinTimestamp", new FixedMetadataValue(Main.this, System.currentTimeMillis()));
                }
            }
        }.runTaskTimer(this, 0L, 8960L); // 7 minutes and 28 seconds in ticks (8960 ticks)
    }

    private String formatPlaytime(long playtimeMillis) {
        long totalSeconds = playtimeMillis / 1000;
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;
        return hours + " hours, " + minutes + " minutes, and " + seconds + " seconds";
    }
}
