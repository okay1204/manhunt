package me.okay.Manhunt;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class TrackCompass implements Listener {
    Player trackedPlayer;

    private boolean isTrackedPlayer(Player player) {
        return player.getName().equals(trackedPlayer.getName());
    }

    public Player getTrackedPlayer() {
        return trackedPlayer;
    }
    
    public void setTrackedPlayer(Player trackedPlayer) {
        this.trackedPlayer = trackedPlayer;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (trackedPlayer != null) {
            if (isTrackedPlayer(event.getPlayer())) {
                for (Player player : Bukkit.getServer().getOnlinePlayers()) {
                    // set compass target to the tracked player, otherwise if player is the tracked player just set it in front of them
                    if (!isTrackedPlayer(player)) {
                        player.setCompassTarget(trackedPlayer.getLocation());
                    }
                    else {
                        player.setCompassTarget(player.getLocation().add(player.getEyeLocation().getDirection().multiply(10)));
                    }
                }
            }
        }
    }
}
