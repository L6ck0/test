package com.outlook.Lukas.VanImpe.Commands;

import com.outlook.Lukas.VanImpe.Main;
import com.outlook.Lukas.VanImpe.Data.PlayerDataManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class OpBanCommand implements CommandExecutor {

    private final PlayerDataManager dataManager;

    public OpBanCommand(Main plugin) {
        this.dataManager = new PlayerDataManager(plugin.getPlayerDataConfig());
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        // Check if the sender is neither player nor console
        if (!(sender instanceof Player) && !(sender instanceof ConsoleCommandSender)) {
            sender.sendMessage("This command can only be used by players or the console.");
            return false;
        }

        // Check permission if the sender is a player
        if (sender instanceof Player player && !player.hasPermission("death.timeout.ban")) {
            player.sendMessage("You do not have permission to use this command.");
            return false;
        }

        // Check argument length
        if (args.length < 2) {
            sender.sendMessage("Usage: /ban <player> <time_in_minutes>");
            return false;
        }

        // Get the target player (null if not online)
        Player target = sender.getServer().getPlayer(args[0]);
        if (target == null && !(sender instanceof ConsoleCommandSender)) {
            sender.sendMessage("Player not found.");
            return false;
        }

        // Parse the ban time
        double banTime;
        try {
            banTime = Double.parseDouble(args[1]);
        } catch (NumberFormatException e) {
            sender.sendMessage("Invalid time format. Please enter a number.");
            return false;
        }

        // Validate the ban time
        if (banTime <= 0 || banTime > 1000) {
            sender.sendMessage("Ban time must be between 0.1 and 1000 minutes.");
            return false;
        }

        // Set the ban expiration
        long banExpiration = System.currentTimeMillis() + (long) (banTime * 60 * 1000);
        if (target != null) {
            dataManager.setBanExpiration(target.getUniqueId(), banExpiration);
            Component kickMessage = Component.text("You have been banned for " + banTime + " minutes due to a death timeout.")
                    .color(TextColor.color(255, 0, 0)); // Red color
            target.kick(kickMessage); // Use kick(Component message) instead of kickPlayer(String message)
            sender.sendMessage("Banned " + target.getName() + " for " + banTime + " minutes.");
        } else {
            // If the target is null and sender is console, handle offline banning
            dataManager.setBanExpiration(sender.getServer().getOfflinePlayer(args[0]).getUniqueId(), banExpiration);
            sender.sendMessage("Player " + args[0] + " has been banned for " + banTime + " minutes.");
        }

        return true;
    }
}
