package me.okay.Manhunt;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World.Environment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CompassMeta;

public class TrackCompass implements Listener {
    Player trackedPlayer;

    // save last coordinates in each dimensions so hunters know where to go to reach that dimension
    Location lastOverWorld;
    Location lastNether;
    Location lastEnd;

    private boolean isTrackedPlayer(Player player) {
        return player.getName().equals(trackedPlayer.getName());
    }

    public Player getTrackedPlayer() {
        return trackedPlayer;
    }
    
    public void setTrackedPlayer(Player trackedPlayer) {
        this.trackedPlayer = trackedPlayer;
    }

    private void setLodestone(Player player, Location location) {
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.getType().equals(Material.COMPASS)){
                CompassMeta compassMeta = (CompassMeta) item.getItemMeta();
                compassMeta.setLodestone(location);
                compassMeta.setLodestoneTracked(false);
                item.setItemMeta(compassMeta);
            }
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (trackedPlayer != null) {

            Location trackedPlayerLocation = (Location) getTrackedPlayer().getLocation();

            for (Player player : Bukkit.getServer().getOnlinePlayers()) {
                // set compass target to the tracked player or last dimension location, otherwise if player is the tracked player just set it in front of them
                Environment dimension = (Environment) player.getWorld().getEnvironment();
                
                if (!isTrackedPlayer(player)) {
                    
                    // if the hunter and the runner are in the same dimension, just set compass direction to the runner
                    if (dimension.equals(getTrackedPlayer().getWorld().getEnvironment())) {
                        setLodestone(player, trackedPlayerLocation);
                    }

                    // otherwise, set compass direction to the last location they have been in that dimension
                    else {
                        if (dimension.equals(Environment.NORMAL)) {
                            setLodestone(player, lastOverWorld);
                        }
                        else if (dimension.equals(Environment.NETHER)) {
                            setLodestone(player, lastNether);
                        }
                        else if (dimension.equals(Environment.THE_END)) {
                            setLodestone(player, lastEnd);
                        }
                    }
                    
                }
                else {
            
                    if (dimension.equals(Environment.NORMAL)) {
                        lastOverWorld = trackedPlayerLocation;
                    }
                    else if (dimension.equals(Environment.NETHER)) {
                        lastNether = trackedPlayerLocation;
                    }
                    else if (dimension.equals(Environment.THE_END)) {
                        lastEnd = trackedPlayerLocation;
                    }

                    setLodestone(player, player.getLocation().add(player.getEyeLocation().getDirection().multiply(10)));
                }
            }
        }
    }
}
