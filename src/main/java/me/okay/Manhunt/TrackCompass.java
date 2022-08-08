package me.okay.Manhunt;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World.Environment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CompassMeta;
public class TrackCompass implements Listener {
    Manhunt manhunt;

    // save last coordinates in each dimension so hunters know where to go to reach that dimension

    // coord maps are maps that constantly save the players location whenever they move
    Map<UUID, Location> overworldCoords = new HashMap<>();
    Map<UUID, Location> netherCoords = new HashMap<>();
    Map<UUID, Location> endCoords = new HashMap<>();

    // portal maps are maps that save the last location recorded once player leaves that dimension
    Map<UUID, Location> overworldPortal = new HashMap<>();
    Map<UUID, Location> netherPortal = new HashMap<>();
    Map<UUID, Location> endPortal = new HashMap<>();

    public TrackCompass(Manhunt manhunt) {
        this.manhunt = manhunt;
    }

    private boolean isTrackedPlayer(Player player) {
        return player.getUniqueId().equals(manhunt.getTrackedPlayer().getUniqueId());
    }

    private String getEnvironmentName(Environment environment) {
        if (environment.equals(Environment.NORMAL)) {
            return "&aOverworld";
        }
        else if (environment.equals(Environment.NETHER)) {
            return "&cNether";
        }
        else if (environment.equals(Environment.THE_END)) {
            return "&fEnd";
        }
        else {
            return "&7Unknown";
        }
    }

    private void saveCoords(Player player) {
        Environment dimension = player.getWorld().getEnvironment();

        if (dimension.equals(Environment.NORMAL)) {
            overworldCoords.put(player.getUniqueId(), player.getLocation());
        }
        else if (dimension.equals(Environment.NETHER)) {
            netherCoords.put(player.getUniqueId(), player.getLocation());
        }
        else if (dimension.equals(Environment.THE_END)) {
            endCoords.put(player.getUniqueId(), player.getLocation());
        }
    }

    // this function is for setting the lodestone of a player's compass to a specific location
    private void setCompass(Player player, Location location) {
        for (ItemStack item : player.getInventory().getContents()) {
            if (manhunt.getItemManager().isTrackingCompass(item)) {
                CompassMeta compassMeta = (CompassMeta) item.getItemMeta();

                compassMeta.setLodestone(location);
                compassMeta.setLodestoneTracked(false);

                Environment playerDimension = player.getWorld().getEnvironment();
                Environment trackedPlayerDimension = manhunt.getTrackedPlayer().getWorld().getEnvironment();
                String compassName = "";

                boolean distanceTracking = manhunt.getConfig().getBoolean("track.distance");
                int distance = (int) Math.sqrt(Math.pow(player.getLocation().getX() - location.getX(), 2) + Math.pow(player.getLocation().getY() - location.getY(), 2) + Math.pow(player.getLocation().getZ() - location.getZ(), 2));

                boolean yLevelTracking = manhunt.getConfig().getBoolean("track.ylevel");
                int ylevel = (int) location.getY();

                String compassNameSuffix = " &7- " + 
                (distanceTracking ? "&6Distance: &c" + Integer.toString(distance) + "m &7- " : "") +
                (yLevelTracking ? "&6Y: &c" + Integer.toString(ylevel) + " &7- " : "")
                + "&bIn the " + getEnvironmentName(playerDimension);

                // if they are in the same dimension
                if (playerDimension.equals(manhunt.getTrackedPlayer().getWorld().getEnvironment())) {
                    compassName = ChatColor.translateAlternateColorCodes('&', "&bTracking &3&l" + manhunt.getTrackedPlayer().getName() + compassNameSuffix);
                }
                // otherwise, different names
                else if (
                        (
                            (playerDimension.equals(Environment.NORMAL) || playerDimension.equals(Environment.NETHER))
                            && (trackedPlayerDimension.equals(Environment.NORMAL) || trackedPlayerDimension.equals(Environment.NETHER))
                        ) ||
                        (
                            playerDimension.equals(Environment.NETHER) && trackedPlayerDimension.equals(Environment.THE_END)
                        )
                    ) {
                    compassName = ChatColor.translateAlternateColorCodes('&', "&bTracking &5&lNether Portal" + compassNameSuffix);
                }
                else if ((playerDimension.equals(Environment.NORMAL) && trackedPlayerDimension.equals(Environment.THE_END))) {
                    compassName = ChatColor.translateAlternateColorCodes('&', "&bTracking &f&lEnd Portal" + compassNameSuffix);
                }
                else if ((playerDimension.equals(Environment.THE_END) && trackedPlayerDimension.equals(Environment.NORMAL))) {
                    compassName = ChatColor.translateAlternateColorCodes('&', "&3&l" + manhunt.getTrackedPlayer().getName() + " &bis in the &aOverworld&b???");
                }

                compassMeta.setDisplayName(compassName);

                item.setItemMeta(compassMeta);
            }
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {

        if (!manhunt.getGameActive()) {
            return;
        }

        if (manhunt.getTrackedPlayer() != null) {

            for (Player player : Bukkit.getServer().getOnlinePlayers()) {
                // set compass target to the tracked player or last dimension location, otherwise if player is the tracked player just set it in front of them
                saveCoords(player);
                
                if (!isTrackedPlayer(player)) {
                    Environment dimension = player.getWorld().getEnvironment();

                    
                    // if the hunter and the runner are in the same dimension, just set compass direction to the runner
                    if (dimension.equals(manhunt.getTrackedPlayer().getWorld().getEnvironment())) {
                        setCompass(player, manhunt.getTrackedPlayer().getLocation());
                    }
                    
                    // otherwise, set compass direction to the last location they have been in that dimension
                    else {
                        UUID trackedPlayerId = manhunt.getTrackedPlayer().getUniqueId();
                        UUID playerId = player.getUniqueId();

                        // initialize to null so vscode wouldn't annoy me with the squiggle
                        Location coords = null;

                        if (dimension.equals(Environment.NORMAL)) {
                            coords = overworldPortal.containsKey(trackedPlayerId) == true ? overworldPortal.get(trackedPlayerId) : overworldPortal.get(playerId);
                        }
                        else if (dimension.equals(Environment.NETHER)) {
                            coords = netherPortal.containsKey(trackedPlayerId) == true ? netherPortal.get(trackedPlayerId) : netherPortal.get(playerId);
                        }
                        else if (dimension.equals(Environment.THE_END)) {
                            coords = endPortal.containsKey(trackedPlayerId) == true ? endPortal.get(trackedPlayerId) : endPortal.get(playerId);
                        }

                        setCompass(player, coords);
                    }
                    
                }
                else {
                    setCompass(player, player.getLocation().add(player.getEyeLocation().getDirection().multiply(10)));
                }
            }
        }
    }

    @EventHandler
    public void onChangedWorld(PlayerChangedWorldEvent event) {

        if (!manhunt.getGameActive()) {
            return;
        }

        Player player = event.getPlayer();
        Environment fromDimension = event.getFrom().getEnvironment();
        Environment toDimension = player.getWorld().getEnvironment();
        UUID playerId = player.getUniqueId();

        // saves location from previous dimension based on saved previous values
        if (fromDimension.equals(Environment.NORMAL)) {
            overworldPortal.put(playerId, overworldCoords.get(playerId));
        }
        else if (fromDimension.equals(Environment.NETHER)) {
            netherPortal.put(playerId, netherCoords.get(playerId));
        }
        else if (fromDimension.equals(Environment.THE_END)) {
            endPortal.put(playerId, endCoords.get(playerId));
        }

        // saves new location in the new dimension
        if (toDimension.equals(Environment.NORMAL)) {
            overworldPortal.put(playerId, player.getLocation());
        }
        else if (toDimension.equals(Environment.NETHER)) {
            netherPortal.put(playerId, player.getLocation());   
        }
        else if (toDimension.equals(Environment.THE_END)) {
            endPortal.put(playerId, player.getLocation());
        }
        
        saveCoords(player);
    }
}
