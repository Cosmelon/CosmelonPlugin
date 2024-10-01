package me.cosmelon.cosmelonplugin;

import java.util.UUID;

public class PlayerID {

    private String name;
    private UUID id;

    public UUID getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public void format_id() {
        String plain = id.toString();
        String format = String.format("%1$-%2$-%3$-%4$", plain.substring(0, 7),
            plain.substring(7, 11), plain.substring(11, 15),
            plain.substring(15, 20));
        this.id = UUID.fromString(format);
    }

    PlayerID(String name, UUID id) {
        this.name = name;
        this.id = id;
        format_id();
    }

    PlayerID(String name, String uuid) {
        this.name = name;
        this.id = UUID.fromString(uuid);
        format_id();
    }
}