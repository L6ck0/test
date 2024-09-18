package com.outlook.Lukas.VanImpe.Commands;

import com.outlook.Lukas.VanImpe.Main;
import com.outlook.Lukas.VanImpe.Utils.PlayerDataManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class SetPlayTimeCommand implements CommandExecutor {

    private final PlayerDataManager dataManager;

    public SetPlayTimeCommand(Main plugin) {
        this.dataManager = new PlayerDataManager(plugin.getPlayerDataConfig());
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player) && !(sender instanceof ConsoleCommandSender)) {
            sender.sendMessage("This command can only be used by players or the console.");
            return false;
        }

        if (args.length < 2) {
            sender.sendMessage("Usage: /setplaytime <player> <playtime_in_minutes>");
            return false;
        }

        Player target = sender.getServer().getPlayer(args[0]);
        if (target == null && !(sender instanceof ConsoleCommandSender)) {
            sender.sendMessage("Player not found.");
            return false;
        }

        double playtimeMinutes;
        try {
            playtimeMinutes = Double.parseDouble(args[1]);
        } catch (NumberFormatException e) {
            sender.sendMessage("Invalid playtime format. Please enter a number.");
            return false;
        }

        if (playtimeMinutes < 0 || playtimeMinutes > 10000) {
            sender.sendMessage("Playtime must be between 0 and 10000 minutes.");
            return false;
        }

        long playtimeMillis = (long) (playtimeMinutes * 60 * 1000);
        UUID targetId = (target != null) ? target.getUniqueId() : sender.getServer().getOfflinePlayer(args[0]).getUniqueId();

        dataManager.setPlaytime(targetId, playtimeMillis);
        String message = (target != null) ? "Set playtime for " + target.getName() + " to " + playtimeMinutes + " minutes."
                : "Set playtime for " + args[0] + " to " + playtimeMinutes + " minutes.";
        sender.sendMessage(message);

        return true;
    }
}
