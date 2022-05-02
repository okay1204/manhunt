package me.okay.Manhunt;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

public class Commands implements CommandExecutor, TabCompleter {
    TrackCompass trackCompass;
    ItemManager itemManager;
    Main main;
    private String helpMessage;
    private static final String noPermsMessage = ChatColor.RED + "You do not have permission to use this comannd. (manhunt.setup)";
    
    Commands(Main main) {
        this.main = main;

        // setting helpMessage
        Map<String, String> commands = new HashMap<>();
        commands.put("track [<username>]", "Display the player currently being tracked (username sets the player being tracked)");
        commands.put("start", "Starts the game of manhunt");
        commands.put("stop", "Ends the game of manhunt");
        commands.put("settings <distance|ylevel> <on|off>", "Toggles different tracking features that compasses get.");
        commands.put("help", "Displays this help menu.");
        
        helpMessage = "&7------[&bManhunt&7]------\n";
    
        for (Map.Entry<String, String> entry : commands.entrySet()) {
            helpMessage += "&7- &b/manhunt " + entry.getKey() + " &7- " + entry.getValue() + ".\n";
        }
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (args.length == 0) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', helpMessage));
        }
        else if (args[0].equalsIgnoreCase("track")) {
            if (args.length == 1) {
                // Should send current tracked player
                Player trackedPlayer = (Player) main.getTrackedPlayer();

                if (trackedPlayer != null) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&bThe current tracked player is &3" + main.getTrackedPlayer().getName() + "&b."));
                }
                else {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&bThere is no player being tracked. Use &9/manhunt track <username> &bto set a player to track."));
                }
            }
            else {
                if (sender.hasPermission("manhunt.setup")) {
                    Player trackedPlayer = Bukkit.getPlayer(args[1]);
        
                    if (trackedPlayer == null) {
                        sender.sendMessage(ChatColor.RED + "That player is not online.");
                    }
                    else {
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&bTracked player is now &3" + trackedPlayer.getName() + "&b."));
                        main.setTrackedPlayer(trackedPlayer);
                    }
                } else {
                    sender.sendMessage(noPermsMessage);
                }
            }
        }
        else if (args[0].equalsIgnoreCase("start")) {
            if (sender.hasPermission("manhunt.setup")) {
                if (main.getTrackedPlayer() == null) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&bYou must set a player to track first with &9/manhunt track <username>&b."));
                }
                else {
                    main.setGameActive(true);
                    Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&bThe game of manhunt has begun! &3" + main.getTrackedPlayer().getName() + " &bis being tracked."));
                }
            }
            else {
                sender.sendMessage(noPermsMessage);
            }
        }
        else if (args[0].equalsIgnoreCase("stop")) {
            if (sender.hasPermission("manhunt.setup")) {
                main.setGameActive(false);
                Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&bThe game of manhunt has been stopped by &3" + sender.getName() + "&b."));
            }
            else {
                sender.sendMessage(noPermsMessage);
            }
        }
        else if (args[0].equalsIgnoreCase("settings")) {

            String invalidUsageMessage = ChatColor.translateAlternateColorCodes('&', "&4Invalid usage. Correct usage: &c/manhunt settings <distance|ylevel> <on|off>");

            if (sender.hasPermission("manhunt.setup")) {

                if (args.length >= 3) {

                    // determining whether on or off
                    boolean newValue;
                    if (args[2].equalsIgnoreCase("on")) {
                        newValue = true;
                    }
                    else if (args[2].equalsIgnoreCase("off")) {
                        newValue = false;
                    }
                    else {
                        sender.sendMessage(invalidUsageMessage);
                        return true;
                    }

                    // checking which setting to apply to
                    if (args[1].equalsIgnoreCase("distance")) {
                        main.config.set("track.distance", newValue);
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&bDistance tracking " + (newValue ? "&aenabled&b." : "&cdisabled&b.")));
                    }
                    else if (args[1].equalsIgnoreCase("ylevel")) {
                        main.config.set("track.ylevel", newValue);
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&bY-Level tracking " + (newValue ? "&aenabled&b." : "&cdisabled&b.")));
                    }
                    else {
                        sender.sendMessage(invalidUsageMessage);
                        return true;
                    }

                    try {
                        main.config.save(main.configFile);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    sender.sendMessage(invalidUsageMessage);
                }
            }
            else {
                sender.sendMessage(noPermsMessage);
            }
        }
        else {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', helpMessage));
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return StringUtil.copyPartialMatches(args[0], List.of("track", "start", "stop", "settings", "help"), new ArrayList<>());
        }
        else if (args.length >= 2 && args[0].equalsIgnoreCase("settings")) {
            if (args.length == 2) {
                return StringUtil.copyPartialMatches(args[1], List.of("distance", "ylevel"), new ArrayList<>());
            }
            else if (args.length == 3) {
                return StringUtil.copyPartialMatches(args[2], List.of("on", "off"), new ArrayList<>());
            }
        }
        
        return null;
    }
}
