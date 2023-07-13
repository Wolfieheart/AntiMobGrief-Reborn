package io.github.wolfstorm.antimobgrief;

import io.github.wolfstorm.antimobgrief.enums.mobList;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.Set;

public class CommandEx implements CommandExecutor {

    AntiMobGriefPlugin plugin;

    //------- Commands
    final String VERSION   = ChatColor.YELLOW + "/amg version";
    final String RELOAD    = ChatColor.YELLOW + "/amg reload";
    final String LISTGRIEF = ChatColor.YELLOW + "/amg listGrief";
    final String SETGRIEF  = ChatColor.YELLOW + "/amg set <" + Util.getEnumList(mobList.class) + "> [true/false]";
    //final String UPDATE    = ChatColor.YELLOW + "/amg update";

    //------- MESSAGES - FOR NOW
    final String NOPERMS    = ChatColor.RED    + "Sorry you do not have permission to use this!";
    final String RELOADED   = ChatColor.GREEN  + "Configuration for AntiMobGrief has been reloaded!";
    final String LISTINIT   = ChatColor.YELLOW + "Here are the list of Mobs along with their Grief Status: ";
    final String USAGEWRONG = ChatColor.RED    + "The Usage for that command should look like /amg set CreeperGrief false";

    //------ STATICS
    final static int noArgs = 0;

    public CommandEx (AntiMobGriefPlugin antiMobGriefPlugin) { this.plugin = antiMobGriefPlugin; }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(sender instanceof ConsoleCommandSender){
            if(args.length == noArgs){
                sender.sendMessage(VERSION);
                sender.sendMessage(RELOAD);
                sender.sendMessage(LISTGRIEF);
                sender.sendMessage(SETGRIEF);
            } else{
                switch(args[0].toLowerCase()){
                    case "reload" -> commandReloadConsole(sender);
                    case "version" -> commandVersionConsole(sender);
                    case "listgrief" -> consoleListCommand(sender);
                    case "set" -> consoleSetCommand(sender, args);
                    default -> {
                        sender.sendMessage(VERSION);
                        sender.sendMessage(RELOAD);
                    }
                }
            }
            return true;
        } else if(sender instanceof Player && !getPermissionBasic((Player) sender)){
            sender.sendMessage(NOPERMS);
            return true;
        } else{
            Player player = (Player) sender;
            if (args.length == noArgs) {
                player.sendMessage(VERSION);
                //player.sendMessage(UPDATE);
                player.sendMessage(RELOAD);
                player.sendMessage(LISTGRIEF);
                return true;
            }
            switch (args[0].toLowerCase()) {
                case "version" -> commandVersion(player);
                //case "update" -> commandUpdate(player);
                case "reload" -> commandReload(player);
                case "listgrief" -> commandList(player);
                case "set" -> commandSet(player, args);
                default -> {
                    sender.sendMessage(VERSION);
                    //sender.sendMessage(UPDATE);
                    sender.sendMessage(RELOAD);
                }
            }
            return true;
        }

    }


    //------- PLAYER COMMANDS

    private void commandReload(Player player) {
        if(!(getPermissionReload(player))){ player.sendMessage(NOPERMS); return; }
        plugin.performReload();
        player.sendMessage(RELOADED);
    }

    private void commandVersion(Player player) {
        if (!(getPermissionBasic(player))){ player.sendMessage(NOPERMS); return; }
        String verString = plugin.getAntiMobGriefVersion();
        player.sendMessage(ChatColor.YELLOW + "[AntiMobGrief] Version: " + verString);
    }

    private void commandList(Player player) {
        if(!(getPermissionList(player))){ player.sendMessage(NOPERMS); return; }

        player.sendMessage(LISTINIT);
        Set<String> confSet = plugin.getConfig().getConfigurationSection("griefSettings").getKeys(false);

        for (String myString : confSet){
            String message = ChatColor.YELLOW + myString;
            if (plugin.getConfig().getBoolean("griefSettings." + myString)) {
                message += ChatColor.GREEN + " is allowed";
            } else {
                message += ChatColor.RED + " is denied";
            }
            player.sendMessage(message);
        }
    }

    private void commandSet(Player player, String[] args) {
        if(!(getPermissionSet(player))) { player.sendMessage(NOPERMS); return; }
        String message = ChatColor.YELLOW + args[1];
        String confOptionName = args[1];

        if(args[2].equalsIgnoreCase("true")){
            player.sendMessage(message + ChatColor.GREEN + " is set to true - Meaning it is OK to Grief Items/Blocks");
            plugin.getConfig().set("griefSettings.allow"+confOptionName, true);
        } else if(args[2].equalsIgnoreCase("false")){
            player.sendMessage(message + ChatColor.RED + " is now turned false - Meaning it will not grief Items/Blocks");
            plugin.getConfig().set("griefSettings.allow"+confOptionName, false);
        } else{
            player.sendMessage(USAGEWRONG);
        }
        plugin.saveConfig();
    }

    //------- CONSOLE COMMANDS

    private void commandVersionConsole(CommandSender sender) {
        String verString = plugin.getAntiMobGriefVersion();
        sender.sendMessage(ChatColor.YELLOW + "[AntiMobGrief] Version: " + verString);
    }


    private void commandReloadConsole(CommandSender sender) {
        plugin.performReload();
        sender.sendMessage(RELOADED);
    }

    private void consoleListCommand(CommandSender sender) {
        sender.sendMessage(LISTINIT);
        Set<String> confSet = plugin.getConfig().getConfigurationSection("griefSettings").getKeys(false);

        for (String myString : confSet){
            String message = ChatColor.YELLOW + myString + ": ";
            if (plugin.getConfig().getBoolean("griefSettings." + myString)) {
                message += ChatColor.GREEN + "enabled";
            } else {
                message += ChatColor.RED + "disabled";
            }
            sender.sendMessage(message);
        }
    }

    private void consoleSetCommand(CommandSender sender, String[] args) {
        String message = ChatColor.YELLOW + args[1];
        String confOptionName = args[1];

        if(args[2].equalsIgnoreCase("true")){
            sender.sendMessage(message + ChatColor.GREEN + " is set to true - Meaning it is OK to Grief Items/Blocks");
            plugin.getConfig().set("griefSettings.allow"+confOptionName, true);
        } else if(args[2].equalsIgnoreCase("false")){
            sender.sendMessage(message + ChatColor.RED + " is now turned false - Meaning it will not grief Items/Blocks");
            plugin.getConfig().set("griefSettings.allow"+confOptionName, false);
        } else{
            sender.sendMessage(USAGEWRONG);
        }
        plugin.saveConfig();
    }


    //------- Permission checker

    private boolean checkPermission(Player player, String permName,  boolean sendMessageOnInvalidation) {
        if (player.hasPermission("amg." + permName.toLowerCase())) {
            return true;
        } else {
            if (sendMessageOnInvalidation) {
                player.sendMessage(NOPERMS);
            }
            return false;
        }
    }

    private boolean getPermissionBasic(Player player) {
        return checkPermission(player, "basic", false);
    }
    private boolean getPermissionReload(Player player) {
        return checkPermission(player, "reload", false);
    }
    private boolean getPermissionList(Player player) {
        return checkPermission(player, "listGrief", false);
    }
    private boolean getPermissionSet(Player player) { return checkPermission(player, "setGrief", false); }
}
