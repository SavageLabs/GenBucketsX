package net.prosavage.genbucket.file.impl;

import net.prosavage.genbucket.GenBucket;
import net.prosavage.genbucket.file.CustomFile;
import net.prosavage.genbucket.utils.Message;

public class MessageFile extends CustomFile {

    public MessageFile(GenBucket plugin) {
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
            message.setMessages(getConfig().getStringList(message.getConfig()));
        }
        return this;
    }

    @Override
    public String getName() {
        return "messages";
    }
}
