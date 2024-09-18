package com.outlook.Lukas.VanImpe;

import com.outlook.Lukas.VanImpe.Commands.BanCommand;
import com.outlook.Lukas.VanImpe.Commands.SetPlayTimeCommand;
import com.outlook.Lukas.VanImpe.Commands.StatsCommand;
import com.outlook.Lukas.VanImpe.Listeners.PlayerListener;
import com.outlook.Lukas.VanImpe.Utils.CustomConfig;
import com.outlook.Lukas.VanImpe.Utils.PlayTimeManager;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class Main extends JavaPlugin {

    private CustomConfig playerDataConfig;
    private PlayTimeManager playTimeManager;

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

        // Initialize PlaytimeManager
        playTimeManager = new PlayTimeManager(this);

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
}
