package me.okay.Manhunt;

import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
    TrackCompass trackCompass = new TrackCompass(this);
    ItemManager itemManager = new ItemManager(this);
    Commands commands = new Commands(this);

    Player trackedPlayer;
    Boolean gameActive = false;

    public Player getTrackedPlayer() {
        return trackedPlayer;
    }

    public void setTrackedPlayer(Player player) {
        trackedPlayer = player;
    }

    public Boolean getGameActive() {
        return gameActive;
    }

    public void setGameActive(Boolean newGameActive) {
        gameActive = newGameActive;

        if (gameActive == true) {
            itemManager.startGame();
        }
    }

    @Override
    public void onEnable() {
        getCommand("manhunt").setExecutor(commands);

        PluginManager pluginManager = getServer().getPluginManager();

        pluginManager.registerEvents(trackCompass, this);
        pluginManager.registerEvents(itemManager, this);
        pluginManager.addPermission(new Permission("manhunt.setup"));
    }

    @Override
    public void onDisable() {
    }
}