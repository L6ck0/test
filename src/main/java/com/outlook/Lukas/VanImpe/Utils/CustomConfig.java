package com.outlook.Lukas.VanImpe.Utils;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

public class CustomConfig {

    private final File dataFile;
    private FileConfiguration dataConfig;
    private final Logger logger;

    public CustomConfig(File dataFile, Logger logger) {
        this.dataFile = dataFile;
        this.logger = logger;
        this.dataConfig = YamlConfiguration.loadConfiguration(dataFile);

        // Ensure parent directories exist
        File parentDir = dataFile.getParentFile();
        if (!parentDir.exists()) {
            logger.info("Attempting to create directories at: " + parentDir.getAbsolutePath());
            boolean created = parentDir.mkdirs();
            if (created) {
                logger.info("Directories created successfully.");
            } else {
                logger.warning("Failed to create directories. They may already exist or there may be a permissions issue.");
            }
        } else {
            logger.info("Directories already exist at: " + parentDir.getAbsolutePath());
        }
    }

    public void load() {
        if (!dataFile.exists()) {
            try {
                boolean created = dataFile.createNewFile();
                if (created) {
                    logger.info("Created new player data file.");
                } else {
                    logger.warning("Player data file already exists.");
                }
            } catch (IOException e) {
                logger.severe("Failed to create player data file: " + e.getMessage());
            }
        }
        dataConfig = YamlConfiguration.loadConfiguration(dataFile);
        logger.info("Loaded player data.");
    }

    public void save() {
        try {
            dataConfig.save(dataFile);
            logger.info("Saved player data.");
        } catch (IOException e) {
            logger.severe("Failed to save player data: " + e.getMessage());
        }
    }

    public FileConfiguration getConfig() {
        return dataConfig;
    }
}