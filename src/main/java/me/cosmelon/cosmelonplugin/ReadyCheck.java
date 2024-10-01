package me.cosmelon.cosmelonplugin;

import mc.obliviate.inventory.Gui;
import mc.obliviate.inventory.Icon;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;


public class ReadyCheck extends Gui {

    ReadyCheck(Player player) {
        super(player, "ready-check",ChatColor.DARK_AQUA + "Are you ready to start?",3);
    }

    @Override
    public void onOpen(InventoryOpenEvent event) {
        ItemStack check = new ItemStack(Material.CARROT_ON_A_STICK);
        ItemMeta check_meta = check.getItemMeta();
        check_meta.setCustomModelData(9);
        check_meta.setDisplayName(ChatColor.GREEN + "I'm ready to play!");
        check.setItemMeta(check_meta);

        ItemStack cross = new ItemStack(Material.CARROT_ON_A_STICK);
        ItemMeta cross_meta = cross.getItemMeta();
        cross_meta.setCustomModelData(10);
        cross_meta.setDisplayName(ChatColor.RED + "Hold on! I'm not ready!");
        cross.setItemMeta(cross_meta);

        Icon yes = new Icon(check);
        Icon no = new Icon(cross);

        yes.onClick(e -> {
            player.closeInventory(InventoryCloseEvent.Reason.PLUGIN);
            Bukkit.dispatchCommand(player, "trigger br_rcyes");
        });

        no.onClick(e -> {
            player.closeInventory(InventoryCloseEvent.Reason.PLUGIN);
            Bukkit.dispatchCommand(player, "trigger br_rcno");
        });

        addItem(11, yes);
        addItem(15, no);

        new BukkitRunnable() {
            @Override
            public void run() {
                player.closeInventory(InventoryCloseEvent.Reason.PLUGIN);
            }
        }.runTaskLater(this.getPlugin(), 200L);
    }

    @Override
    public void onClose(InventoryCloseEvent event) {
        super.onClose(event);
        if (event.getReason() != InventoryCloseEvent.Reason.PLUGIN) {
            Bukkit.getScheduler().runTask(getPlugin(), this::open);
        }
    }

}
