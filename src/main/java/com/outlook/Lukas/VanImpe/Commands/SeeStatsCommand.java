package com.outlook.Lukas.VanImpe.Commands;

import com.outlook.Lukas.VanImpe.Main;
import com.outlook.Lukas.VanImpe.Data.PlayerDataManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class SeeStatsCommand implements CommandExecutor {

    private final Main plugin;

    public SeeStatsCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player player) {
            UUID playerId = player.getUniqueId();
            PlayerDataManager dataManager = new PlayerDataManager(plugin.getPlayerDataConfig());

            int kills = dataManager.getKills(playerId);
            int deaths = dataManager.getDeaths(playerId);
            long playtimeMillis = dataManager.getPlaytime(playerId);
            String playtime = formatPlaytime(playtimeMillis);

            player.sendMessage("Your Stats:");
            player.sendMessage("Kills: " + kills);
            player.sendMessage("Deaths: " + deaths);
            player.sendMessage("Playtime: " + playtime);

            return true;
        } else {
            sender.sendMessage("This command can only be used by players.");
            return false;
        }
    }

    private String formatPlaytime(long playtimeMillis) {
        long seconds = playtimeMillis / 1000;
        long minutes = seconds / 60;
        seconds %= 60;
        return minutes + " minutes and " + seconds + " seconds";
    }
}
