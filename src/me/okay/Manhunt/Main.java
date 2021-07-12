package me.okay.Manhunt;

import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
    TrackCompass trackCompass = new TrackCompass();
    Commands commands = new Commands(trackCompass);

    @Override
    public void onEnable() {
        getCommand("manhunt").setExecutor(commands);
        getServer().getPluginManager().registerEvents(trackCompass, this);;
    }

    @Override
    public void onDisable() {
    }
}