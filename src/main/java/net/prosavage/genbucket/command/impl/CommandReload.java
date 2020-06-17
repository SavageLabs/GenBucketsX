package net.prosavage.genbucket.command.impl;

import net.prosavage.genbucket.GenBucket;
import net.prosavage.genbucket.command.AbstractCommand;
import net.prosavage.genbucket.config.Config;
import net.prosavage.genbucket.config.Message;
import net.prosavage.genbucket.utils.ChatUtils;
import org.bukkit.command.CommandSender;

public class CommandReload extends AbstractCommand {

    public CommandReload(GenBucket plugin) {
        super(plugin, "reload", false);
        this.alias.add("r");
    }

    @Override
    public boolean execute(CommandSender commandSender, String[] args) {
        getPlugin().loadConfig();
        commandSender.sendMessage(ChatUtils.color(Message.CMD_RELOAD_SUCCESS.getMessage()));
        return false;
    }

    @Override
    public String getDescription() {
        return Message.CMD_RELOAD_DESC.getMessage();
    }

    @Override
    public String getPermission() {
        return Config.PERMISSION_RELOAD.getString();
    }

}
