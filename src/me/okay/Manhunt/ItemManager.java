package me.okay.Manhunt;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

public class ItemManager implements Listener {
    Main main;

    public ItemManager(Main main) {
        this.main = main;
    }

    public void startGame() {
        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            player.getInventory().clear();

            if (!player.getUniqueId().equals(main.getTrackedPlayer().getUniqueId())) {
                player.getInventory().addItem(new ItemStack(Material.COMPASS, 1));
            }
            
            player.updateInventory();
            player.setHealth(20);
            player.setSaturation(20);
        }
    }

    private Boolean notTrackedPlayer(Player player) {
        return !player.getUniqueId().equals(main.getTrackedPlayer().getUniqueId());
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        if (main.getGameActive()) {
            Player player = event.getPlayer();
    
            // make sure hunters respawn with compass
            if (notTrackedPlayer(player)) {
                player.getInventory().addItem(new ItemStack(Material.COMPASS, 1));
                player.updateInventory();
            }
        }
    }

    @EventHandler
    public void onDropItem(PlayerDropItemEvent event) {
        if (main.getGameActive()) {
            
            // make sure hunters cant drop compasses
            if (notTrackedPlayer(event.getPlayer()) && event.getItemDrop().getItemStack().getType().equals(Material.COMPASS)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        if (main.getGameActive()) {
            Player player = event.getEntity();
    
            // make sure compasses dont drop on death
            if (notTrackedPlayer(player)) {
                event.getDrops().remove(new ItemStack(Material.COMPASS));

                for (int i = 0; i < event.getDrops().size(); i++) {
                    ItemStack item = event.getDrops().get(i);

                    if (item.getType().equals(Material.COMPASS)) {
                        event.getDrops().remove(item);
                        i--;
                    }
                }
            }
        }
    }
}
