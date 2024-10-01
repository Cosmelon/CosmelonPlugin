package me.cosmelon.cosmelonplugin.listeners;

import me.cosmelon.cosmelonplugin.CosmelonPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Firework;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

/**
 * disable firework damage
 */
public class EntityDamageByEntityEventListener implements Listener {

    public EntityDamageByEntityEventListener(CosmelonPlugin plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Firework) {
            event.setCancelled(true);
        }
    }
}
