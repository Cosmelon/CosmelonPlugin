package me.cosmelon.cosmelonplugin.listeners;

import me.cosmelon.cosmelonplugin.CosmelonPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;

/**
 * Listen for resource pack denial by player.
 */

public class ResourceDenyListener implements Listener {

    public ResourceDenyListener(CosmelonPlugin plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onStatus(PlayerResourcePackStatusEvent event) {
        Player player = event.getPlayer();
        if (player.getScoreboardTags().contains("br_pack_bypass")) {
            player.sendMessage("NOTE: pack event " + event.getStatus() + " happened but was ignored because you have 'br_pack_bypass'" + ChatColor.DARK_RED);
            return;
        }

        if (event.getStatus() == PlayerResourcePackStatusEvent.Status.INVALID_URL) {
            player.kickPlayer(ChatColor.GOLD + "Invalid URL: Contact a server admin ASAP!");
        }

        if (event.getStatus() == PlayerResourcePackStatusEvent.Status.DISCARDED
            || event.getStatus() == PlayerResourcePackStatusEvent.Status.DECLINED
            || event.getStatus() == PlayerResourcePackStatusEvent.Status.FAILED_DOWNLOAD
            || event.getStatus() == PlayerResourcePackStatusEvent.Status.FAILED_RELOAD) {
            player.kickPlayer(ChatColor.GOLD + "Enable 'Server Resource Packs' in server settings to join!");
        }
    }
}
