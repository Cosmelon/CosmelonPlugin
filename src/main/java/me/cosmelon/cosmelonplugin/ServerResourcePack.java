package me.cosmelon.cosmelonplugin;

import java.util.UUID;

/**
 * Store data for a specific resource pack.
 */
public class ServerResourcePack {
    private String name;
    private int port;
    private String location;
    private UUID id;

    ServerResourcePack(String name, int port, String location) {
        this.name = name;
        this.port = port;
        this.location = location;
    }

    public String getName() {
        return this.name;
    }

    public int getPort() {
        return this.port;
    }

    public String getLocation() {
        return this.location;
    }

    public UUID getUUID() {
        return this.id;
    }

    @Override
    public String toString() {
        return "Pack: " + getName() + "@" + getPort() + " " + getLocation();
    }
}
