package me.cosmelon.cosmelonplugin;

import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

public class Whitelist implements Listener {
    private CosmelonPlugin plugin;
    Whitelist(CosmelonPlugin plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
        configure();
    }

    void whitelistcmd(CommandSender sender, Command cmd, String label, String[] args) {
        if (lists.contains(args[0])) {
            switch (args[1]) {
            case "add":
                // add the list
                sender.sendMessage(add_list(args[0]));
                break;
            case "remove":
                // remove the list
                sender.sendMessage(rem_list(args[0]));
                break;
            case "list":
                // list player names on specified list
            default:
                sender.sendMessage("Invalid arguments!");
                break;
            }
        } else if (args[0].equalsIgnoreCase("list")) {
            StringBuilder temp = new StringBuilder();
            temp.append(active_lists.size() + " groups active: ");
            for (int i = 0; i < active_lists.size(); i++) {
                temp.append(active_lists.get(i) + ", ");
            }
            sender.sendMessage(temp.toString());

            temp.delete(0,temp.length());
            temp.append(lists.size() + " groups available: ");
            for (int i = 0; i < lists.size(); i++) {
                temp.append(lists.get(i) + ", ");
            }
            sender.sendMessage(temp.toString());

        } else if (args[0].equalsIgnoreCase("tempplayer")) {
            // usage: /whitelist tempplayer <name>
            //add_temp(args[1]);
            sender.sendMessage(ChatColor.AQUA + "This feature is currently WIP. Use /whitelist off for now and turn it back on when done.");

        } else if (args[0].equals("on")) {
            // enforce the whitelist
            if (enforce_whitelist) {
                sender.sendMessage(ChatColor.GREEN + "Whitelist is already on!");
                return;
            }
            sender.sendMessage(ChatColor.GREEN + "Whitelist is now on!");
            enforce_whitelist = true;

        } else if (args[0].equals("off")) {
            // disable the whitelist
            if (!enforce_whitelist) {
                sender.sendMessage(ChatColor.GREEN + "Whitelist is already off!");
                return;
            }
            sender.sendMessage(ChatColor.GREEN + "Whitelist is now off.");
            enforce_whitelist = false;

        } else {
            sender.sendMessage("Invalid arguments!");
        }
    }

    /**
     * @param list list of players to add
     * @return status message to player
     */
    private String add_list(String list) {
        if (active_lists.contains(list)) {
            return ChatColor.GREEN + "This list is already added.";
        }

        if (!lists.contains(list)) {
            return ChatColor.RED + "This list does not exist.";
        }

        active_lists.add(list);
        Bukkit.getLogger().info(list + " UUID list added.");
        return ChatColor.GREEN + list + " added to whitelist";
    }

    /**
     * @param list list of players to remove
     * @return status message to player
     */
    private String rem_list(String list) {
        if (list.equals("admin") || list.equals("temp")) {
            return ChatColor.RED + "This is a protected group & cannot be removed!";
        }

        if (!active_lists.contains(list) || list == null) {
            return ChatColor.RED + "This list does not exist or is inactive.";
        }

        active_lists.remove(list);
        Bukkit.getLogger().info(list + "UUID list removed.");
        return ChatColor.RED + list + " removed!";
    }

    /**
     * This player won't be able to log back in after the server restarts
     * @param temp_player
     */
    ArrayList<PlayerID> temp_players = new ArrayList<>();
    private void add_temp(String temp_player_name) {
        try {
            // Create the connection
            HttpURLConnection connection = (HttpURLConnection) new URL("https://api.mojang.com/users/profiles/minecraft/" + temp_player_name).openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/json");

            // Read the response
            try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                String jsonResponse = br.lines().reduce("", String::concat);
                Bukkit.getLogger().warning("jsonresponse from API: " + jsonResponse);
                // Parse JSON
                PlayerID data = new Gson().fromJson(jsonResponse, PlayerID.class);
                temp_players.add(data);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Listen for login attempt and dispatch UUID checks
     * @param event
     */
    boolean enforce_whitelist;
    @EventHandler
    public void on_login(PlayerLoginEvent event) {
        if (!enforce_whitelist) {
            return;
        }

        UUID uuid = event.getPlayer().getUniqueId();
        Bukkit.getConsoleSender().sendMessage("Debugging player ID upon pre_login: " + uuid);

        // check the temp list first
        for (PlayerID check_temp : temp_players) {
            if(check_temp.getId() == uuid) {
                Bukkit.getLogger().info("Player " + check_temp.getName() + " exists on temp list... accepting.");
                return;
            }
        }

        // check the rest of the lists
        for (String checking_list : active_lists) {
            // go through every list that is active at the moment
            try {
                Scanner searching_file = new Scanner(new File(this.plugin.getDataFolder(), checking_list + ".txt"));
                while (searching_file.hasNextLine()) {
                    String line = searching_file.nextLine();
                    if (line.equals(uuid.toString())) {
                        return;
                    }
                }
            } catch (FileNotFoundException e) {
                event.disallow(PlayerLoginEvent.Result.KICK_OTHER, "java.io.FileNotFoundException@Whitelist.java: This is a bug!\n\n\nReport it to get a personal high five from heavy_fortress2.");
                Bukkit.getLogger().warning("Player " + event.getPlayer().getName() + " was kicked due to a FileNotFoundException");
                return;
            }
        }
        event.disallow(PlayerLoginEvent.Result.KICK_WHITELIST, "Bigrat couldn't find you on the whitelist!\n\n\nContact an admin if you believe this to be an error!");
    }

    String[] fuck_this_shit = {"admin","alpha_tester","tester","player","temp","friends"};
    List<String> lists = Arrays.asList(fuck_this_shit);
    ArrayList<String> active_lists = new ArrayList<>();
    private void configure() {
        enforce_whitelist = true;
        for (String group : lists) {
            File group_file = new File(this.plugin.getDataFolder(), group + ".txt");
            if (group.equals("temp")) {
                if (group_file.delete()) Bukkit.getLogger().info("Deleted temp whitelist.");
            }
            try {
                if(group_file.createNewFile()) Bukkit.getLogger().info("Created new whitelist file: " + group_file.getName());
            } catch (IOException e) {
                Bukkit.getLogger().info("IOException: Failed to create new whitelist group file for group " + group_file.getName());
            }
        }
        add_list("admin");
        add_list("temp");

        Bukkit.getLogger().info("active lists:");
        for (String name : active_lists) {
            Bukkit.getLogger().info(name);
        }
    }
}