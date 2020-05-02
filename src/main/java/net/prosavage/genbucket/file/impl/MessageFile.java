package net.prosavage.genbucket.file.impl;

import net.prosavage.genbucket.file.CustomFile;
import net.prosavage.genbucket.utils.Message;
import org.bukkit.plugin.java.JavaPlugin;

public class MessageFile extends CustomFile {

    public MessageFile(JavaPlugin plugin) {
        super(plugin, "");
        for (Message message : Message.values()) {
            getConfig().addDefault(message.getConfig(), message.getMessage());
        }
        getConfig().options().copyDefaults(true);
        saveConfig();
    }

    public MessageFile init() {
        this.reloadConfig();
        for (Message message : Message.values()) {
            if (message.getMessages() != null) {
                message.setMessages(getConfig().getStringList(message.getConfig()));
            } else {
                message.setMessage(getConfig().getString(message.getConfig()));
            }
        }
        return this;
    }

    @Override
    public String getName() {
        return "messages";
    }
}
