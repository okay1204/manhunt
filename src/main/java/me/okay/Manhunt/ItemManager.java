package me.okay.Manhunt;

import java.util.Iterator;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class ItemManager implements Listener {
    private Manhunt manhunt;
    private NamespacedKey key;

    public ItemManager(Manhunt manhunt) {
        this.manhunt = manhunt;
        key = new NamespacedKey(manhunt, "isTrackingCompass");
    }

    public void startGame() {
        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            player.getInventory().clear();

            if (!player.getUniqueId().equals(manhunt.getTrackedPlayer().getUniqueId())) {
                player.getInventory().addItem(newTrackingCompass());
            }
            
            player.updateInventory();
            player.setHealth(20);
            player.setSaturation(20);
        }
    }

    private boolean notTrackedPlayer(Player player) {
        return !player.getUniqueId().equals(manhunt.getTrackedPlayer().getUniqueId());
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        if (!manhunt.getGameActive()) {
            return;
        }

        Player player = event.getPlayer();

        // make sure hunters respawn with compass
        if (notTrackedPlayer(player)) {
            // remove tracking compass in case player respawned without dying
            Iterator<ItemStack> iterator = player.getInventory().iterator();
            while (iterator.hasNext()) {
                ItemStack item = iterator.next();
                if (isTrackingCompass(item)) {
                    iterator.remove();
                }
            }

            player.getInventory().addItem(newTrackingCompass());
            player.updateInventory();
        }
    }

    @EventHandler
    public void onDropItem(PlayerDropItemEvent event) {
        if (!manhunt.getGameActive()) {
            return;
        }
            
        // make sure hunters cant drop compasses
        if (notTrackedPlayer(event.getPlayer()) && isTrackingCompass(event.getItemDrop().getItemStack())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        if (!manhunt.getGameActive()) {
            return;
        }

        Player player = event.getEntity();

        // make sure compasses dont drop on death
        if (notTrackedPlayer(player)) {
            Iterator<ItemStack> iterator = event.getDrops().iterator();
            while (iterator.hasNext()) {
                ItemStack item = iterator.next();
                if (isTrackingCompass(item)) {
                    iterator.remove();
                }
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (!manhunt.getGameActive()) {
            return;
        }
            
        // make sure hunters get compass on join
        if (notTrackedPlayer(event.getPlayer())) {
            boolean hasCompass = false;
            for (ItemStack item : event.getPlayer().getInventory()) {
                if (isTrackingCompass(item)) {
                    hasCompass = true;
                    break;
                }
            }
            if (!hasCompass) {
                event.getPlayer().getInventory().addItem(newTrackingCompass());
                event.getPlayer().updateInventory();
            }
        }
    }

    public ItemStack newTrackingCompass() {
        ItemStack compass = new ItemStack(Material.COMPASS);
        ItemMeta meta = compass.getItemMeta();
        meta.getPersistentDataContainer().set(key, PersistentDataType.BYTE, (byte) 1);
        compass.setItemMeta(meta);

        return compass;
    }

    public boolean isTrackingCompass(ItemStack item) {
        return item == null ? false : item.getItemMeta().getPersistentDataContainer().has(key, PersistentDataType.BYTE);
    }
}
