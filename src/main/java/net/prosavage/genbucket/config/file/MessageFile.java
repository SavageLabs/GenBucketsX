package net.prosavage.genbucket.config.file;

import net.prosavage.genbucket.config.Message;
import pro.dracarys.configlib.config.CustomFile;

public class MessageFile extends CustomFile {

    public MessageFile() {
        super("");
        for (Message message : Message.values()) {
            getConfig().addDefault(message.getConfig(), message.getMessage());
        }
        getConfig().options().copyDefaults(true);
        saveConfig();
    }

    public MessageFile init() {
        reloadConfig();
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
