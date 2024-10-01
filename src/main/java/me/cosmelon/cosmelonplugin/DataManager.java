package me.cosmelon.cosmelonplugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 * Handles config access and parsing.
 */
public class DataManager {
    private CosmelonPlugin plugin ;
    private File config_file = null;
    private FileConfiguration config = null;
    public DataManager(CosmelonPlugin plugin) {
        this.plugin = plugin;
        saveDefaultConfig();
    }

    public FileConfiguration getConfig() {
        if (this.config == null) reloadConfig();
        return this.config;
    }

    public void reloadConfig() {
        if (this.config_file == null)
            this.config_file = new File(this.plugin.getDataFolder(), "config.yml");

        this.config = YamlConfiguration.loadConfiguration(this.config_file);
        InputStream defaultStream = this.plugin.getResource("config.yml");
        if (defaultStream != null) {
            YamlConfiguration defaultconfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defaultStream));
            this.config.setDefaults(defaultconfig);
        }
    }

    public void saveConfig() {
        if (this.config == null || this.config_file == null)
            return;
        try {
            this.getConfig().save(this.config_file);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not save config to " + this.config_file, e);
        }
    }

    public void saveDefaultConfig() {
        if (this.config_file == null) {
            this.config_file = new File(this.plugin.getDataFolder(), "config.yml");
        }

        if (!this.config_file.exists()) {
            this.plugin.saveResource("config.yml", false);
        }
    }

}
