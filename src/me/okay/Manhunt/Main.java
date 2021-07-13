package me.okay.Manhunt;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
    TrackCompass trackCompass = new TrackCompass(this);
    ItemManager itemManager = new ItemManager(this);
    Commands commands = new Commands(this);
    
    File configFile;
    FileConfiguration config;
    
    
    Player trackedPlayer;
    boolean gameActive = false;
    boolean trackDistance;
    boolean trackYLevel;
    
    @Override
    public void onEnable() {
        
        // adding plugin files
        
        getCommand("manhunt").setExecutor(commands);
        
        PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(trackCompass, this);
        pluginManager.registerEvents(itemManager, this);
        
        // setting up permissions
        
        final String permissionName = "manhunt.setup";
        Permission permission = pluginManager.getPermission(permissionName);
        
        if (permission == null) {
            pluginManager.addPermission(new Permission(permissionName));
        }

        // setting up config

        final String fileName = "config.yml";

        configFile = new File(getDataFolder(), fileName);

        if (!configFile.exists()) {
            configFile.getParentFile().mkdirs();
            saveResource(fileName, false);
        }

        config = new YamlConfiguration();

        try {
            config.load(configFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void onDisable() {
    }

    public Player getTrackedPlayer() {
        return trackedPlayer;
    }

    public void setTrackedPlayer(Player player) {
        trackedPlayer = player;
    }

    public boolean getGameActive() {
        return gameActive;
    }

    public void setGameActive(boolean newGameActive) {
        gameActive = newGameActive;

        if (gameActive == true) {
            itemManager.startGame();
        }
    }

}