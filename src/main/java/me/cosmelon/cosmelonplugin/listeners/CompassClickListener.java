package me.cosmelon.cosmelonplugin.listeners;

import java.util.ArrayList;
import java.util.UUID;
import me.cosmelon.cosmelonplugin.CosmelonPlugin;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

public class CompassClickListener implements Listener {

    private final CosmelonPlugin pl;
    private final ArrayList<String> other_players = new ArrayList<>();
    private int selector;


    public CompassClickListener(CosmelonPlugin pl) {
        this.pl = pl;
        Bukkit.getPluginManager().registerEvents(this, pl);
    }

    @EventHandler
    public void compass_click(PlayerInteractEvent event) {
        if (event.getHand() == EquipmentSlot.OFF_HAND) { // ignore off hand
            return;
        }

        Player player = event.getPlayer();
        if (event.getAction() == org.bukkit.event.block.Action.RIGHT_CLICK_AIR ||
            event.getAction() == org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK) {

            ItemStack itemInHand = event.getPlayer().getInventory().getItemInMainHand();

            if (itemInHand != null && itemInHand.getType() == Material.COMPASS && player.getScoreboardTags().contains("player")) {
                getPlayersOnSameTeam(player);

                // Increment selector and handle wrap-around
                selector = (selector + 1) % (other_players.size());
            }
        }
    }

//    int max_selector;

    public void getPlayersOnSameTeam(Player player) {
        other_players.clear();

        ScoreboardManager mgr = Bukkit.getScoreboardManager();
        if (mgr == null) return;

        Scoreboard board = mgr.getMainScoreboard();
        Team playerteam = board.getEntryTeam(player.getName());

        if (playerteam != null) {
            for (String entry : playerteam.getEntries()) {
                if (entry.contains(String.valueOf('-'))) {
                    Entity ent = Bukkit.getEntity(UUID.fromString(entry));
                    if (ent instanceof ArmorStand) {
                        other_players.add(entry);
                        continue;
                    }
                }

                Player member = Bukkit.getPlayer(entry);
                if (member != null && member != player) {
                    other_players.add(member.getName());
                }
            }
        }

        other_players.sort(String::compareToIgnoreCase);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (other_players.isEmpty()) return;
        if (player.getInventory().getItemInMainHand().getType() != Material.COMPASS) return;
        if (!player.getScoreboardTags().contains("player")) return;

        String hud = "Tracking: ";

        Player target_p = null;
        Entity target_e = null;
        int dist = 0;

        // Try to get the target player or entity
        try {
            target_p = Bukkit.getPlayer(other_players.get(selector));
            target_e = Bukkit.getEntity(UUID.fromString(other_players.get(selector)));
        } catch (Exception e) {
            // do nothing
        }

        if (target_p != null) {
            // is player
            player.setCompassTarget(target_p.getLocation());
            hud += target_p.getName();
            dist = distance(player.getLocation(), target_p.getLocation());
        } else if (target_e instanceof ArmorStand) {
            // is entity
            player.setCompassTarget(target_e.getLocation());
            if (!target_e.getName().contains(String.valueOf('-'))) { // avoid UUID
                hud += "CENTER";
                dist = distance(player.getLocation(), target_e.getLocation());
            }
        } else {
            // idfk
            hud += "NOT FOUND";
        }
        hud += "    Distance: ";
        hud += dist;

        Component comp = Component.text(hud);
        player.sendActionBar(comp);
    }

    private int distance(Location p1, Location p2) {
        int delta_x = p1.getBlockX() - p2.getBlockX();
        int delta_y = p1.getBlockY() - p2.getBlockY();
        int delta_z = p1.getBlockZ() - p2.getBlockZ();

        delta_x = delta_x * delta_x;
        delta_y = delta_y * delta_y;
        delta_z = delta_z * delta_z;

        return (int) Math.sqrt(delta_x + delta_y + delta_z);
    }

}
