package com.outlook.Lukas.VanImpe.Utils;

import org.bukkit.configuration.file.FileConfiguration;

import java.util.UUID;

public class PlayerDataManager {

    private final CustomConfig customConfig;
    private final FileConfiguration dataConfig;

    public PlayerDataManager(CustomConfig customConfig) {
        this.customConfig = customConfig;
        this.dataConfig = customConfig.getConfig();
    }

    public void incrementKills(UUID playerId) {
        int kills = dataConfig.getInt("players." + playerId + ".kills", 0);
        dataConfig.set("players." + playerId + ".kills", kills + 1);
        customConfig.save(); // Save changes to file
    }

    public void incrementDeaths(UUID playerId) {
        int deaths = dataConfig.getInt("players." + playerId + ".deaths", 0);
        dataConfig.set("players." + playerId + ".deaths", deaths + 1);
        customConfig.save(); // Save changes to file
    }

    public int getKills(UUID playerId) {
        return dataConfig.getInt("players." + playerId + ".kills", 0);
    }

    public int getDeaths(UUID playerId) {
        return dataConfig.getInt("players." + playerId + ".deaths", 0);
    }

    public long getPlaytime(UUID playerId) {
        return dataConfig.getLong("players." + playerId + ".playtime", 0);
    }

    public void addPlaytime(UUID playerId, long playtimeMillis) {
        long totalPlaytime = getPlaytime(playerId);
        dataConfig.set("players." + playerId + ".playtime", totalPlaytime + playtimeMillis);
        customConfig.save(); // Save changes to file
    }

    public long getBanExpiration(UUID playerId) {
        return dataConfig.getLong("players." + playerId + ".banExpiration", 0);
    }

    public void setBanExpiration(UUID playerId, long expirationTimeMillis) {
        dataConfig.set("players." + playerId + ".banExpiration", expirationTimeMillis);
        customConfig.save(); // Save changes to file
    }

    public boolean isBanned(UUID playerId) {
        long expirationTime = getBanExpiration(playerId);
        return expirationTime > System.currentTimeMillis();
    }
    public void setPlaytime(UUID playerId, long playtimeMillis) {
        dataConfig.set("players." + playerId + ".playtime", playtimeMillis);
        customConfig.save(); // Save changes to file
    }

}
