package net.prosavage.genbucket.file.impl;

import net.prosavage.genbucket.config.Config;
import net.prosavage.genbucket.file.CustomFile;
import org.bukkit.plugin.java.JavaPlugin;

public class ConfigFile extends CustomFile {

    public ConfigFile(JavaPlugin instance) {
        super(instance, "");
        for (Config message : Config.values()) {
            if (message.getStrings() != null) {
                getConfig().addDefault(message.getConfig(), message.getStrings());
            } else if (message.getString() != null) {
                getConfig().addDefault(message.getConfig(), message.getString());
            } else if (message.getInt() != null) {
                getConfig().addDefault(message.getConfig(), message.getInt());
            } else if (message.getDouble() != null) {
                getConfig().addDefault(message.getConfig(), message.getDouble());
            } else {
                getConfig().addDefault(message.getConfig(), message.getOption());
            }
        }
        getConfig().options().copyDefaults(true);
        saveConfig();
    }

    public net.prosavage.genbucket.file.impl.ConfigFile init() {
        this.reloadConfig();
        for (Config message : Config.values()) {
            if (message.getStrings() != null) {
                if (message.getConfig().equalsIgnoreCase("Info")
                        && !message.getStringList().equals(getConfig().getStringList(message.getConfig()))) {
                    getConfig().set(message.getConfig(), message.getStrings());
                    saveConfig();
                    continue;
                }
                message.setStrings(getConfig().getStringList(message.getConfig()));
            } else if (message.getInt() != null) {
                message.setInt(getConfig().getInt(message.getConfig()));
            } else if (message.getDouble() != null) {
                message.setDouble(getConfig().getDouble(message.getConfig()));
            } else if (message.getString() != null) {
                message.setString(getConfig().getString(message.getConfig()));
            } else {
                message.setOption(getConfig().getBoolean(message.getConfig()));
            }
        }
        return this;
    }

    @Override
    public String getName() {
        return "config";
    }
}