package me.okay.Manhunt;

import org.bukkit.permissions.Permission;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
    TrackCompass trackCompass = new TrackCompass();
    Commands commands = new Commands(trackCompass);

    @Override
    public void onEnable() {
        getCommand("manhunt").setExecutor(commands);
        getServer().getPluginManager().registerEvents(trackCompass, this);;
        getServer().getPluginManager().addPermission(new Permission("manhunt.setup"));
    }

    @Override
    public void onDisable() {
    }
}