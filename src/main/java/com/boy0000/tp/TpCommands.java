package com.boy0000.tp;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public class TpCommands implements CommandExecutor {

    static HashMap<UUID, UUID> tpMap = new HashMap<>();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player) {
            if (label.equalsIgnoreCase("tpa") && !args[0].isEmpty()) {
                Player player = Bukkit.getPlayer(args[0]);
                final Player requester = (Player) sender;
                if (Bukkit.getOnlinePlayers().contains(player)) {
                    if (requester.getUniqueId() == player.getUniqueId()) {
                        requester.sendMessage("You cannot teleport to yourself!");
                        return true;
                    } else if (tpMap.containsKey(requester.getUniqueId())) {
                        requester.sendMessage("You already sent a request to another player!");
                        return true;
                    }

                    requester.sendMessage(ChatColor.GREEN + "Sent a teleport request to " + player.getName());
                    player.sendMessage(ChatColor.GREEN + requester.getName() + " asked to teleport to you");
                    tpMap.put(requester.getUniqueId(), player.getUniqueId());

                } else sender.sendMessage(ChatColor.RED + "This player is not online!");
                return true;
            } else if (label.equalsIgnoreCase("tpaccept")) {
                final Player player = (Player) sender;
                if (!tpMap.containsValue(player.getUniqueId())) {
                    player.sendMessage(ChatColor.RED + "You have no pending teleport requests!");
                    return true;
                }

                for (Map.Entry<UUID, UUID> entry : tpMap.entrySet()) {
                    if (entry.getValue() == player.getUniqueId()) {
                        Player requester = Bukkit.getPlayer(entry.getKey());
                        if (Bukkit.getOnlinePlayers().contains(requester)) {
                            PlayerTeleportEvent event = new PlayerTeleportEvent(requester, requester.getLocation(), player.getLocation());
                            event.callEvent();
                            if (event.isCancelled()) {
                                requester.sendMessage(ChatColor.RED + "Could not teleport you to " + player.getName());
                                player.sendMessage(ChatColor.RED + "Could not teleport " + requester.getName() + " to you");
                                tpMap.remove(requester.getUniqueId(), player.getUniqueId());
                                return true;
                            }

                            requester.teleport(player);
                            requester.sendMessage(ChatColor.GREEN + "Teleporting to " + player.getName() + "...");
                            player.sendMessage(ChatColor.GREEN + "You accepted a teleport request from " + requester.getName());
                        } else player.sendMessage(ChatColor.RED + "This player is not online!");

                        tpMap.remove(requester.getUniqueId(), player.getUniqueId());
                        break;
                    }
                }
            } else if (label.equalsIgnoreCase("tpdeny")) {
                final Player player = (Player) sender;
                if (!tpMap.containsValue(player.getUniqueId())) {
                    player.sendMessage(ChatColor.RED + "You have no pending teleport requests!");
                    return true;
                }

                for (Map.Entry<UUID, UUID> entry : tpMap.entrySet()) {
                    if (entry.getValue() == player.getUniqueId()) {
                        Player requester = Bukkit.getPlayer(entry.getKey());
                        tpMap.remove(requester.getUniqueId(), player.getUniqueId());
                        requester.sendMessage(ChatColor.RED + player.getName() + " denied your teleport request");
                        player.sendMessage(ChatColor.RED + "You denied a teleport request from " + requester.getName());
                    }
                }
            } else sender.sendMessage(ChatColor.RED + "Only players can execute this command!");
            return true;
        } return true;
    }
}
