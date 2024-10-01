package me.cosmelon.cosmelonplugin.listeners;

import me.cosmelon.cosmelonplugin.CosmelonPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.ArmorStand;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;

public class OnInteractAtEntity implements Listener {
    public OnInteractAtEntity(CosmelonPlugin plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void OnInteractAtEntity(PlayerInteractAtEntityEvent e) {
        if (e.getRightClicked() instanceof ArmorStand) {
            if (e.getPlayer().getScoreboardTags().contains("debug")) {
                e.getPlayer().sendMessage("[Debug] Armor stand clicked. Event ignored.");
            }
            e.setCancelled(true);
        }
    }
}
