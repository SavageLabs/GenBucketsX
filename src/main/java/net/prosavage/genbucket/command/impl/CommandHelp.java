package net.prosavage.genbucket.command.impl;

import net.prosavage.genbucket.GenBucket;
import net.prosavage.genbucket.command.AbstractCommand;
import net.prosavage.genbucket.command.GenBucketCommand;
import net.prosavage.genbucket.config.Config;
import net.prosavage.genbucket.config.Message;
import net.prosavage.genbucket.utils.ChatUtils;
import org.bukkit.command.CommandSender;

public class CommandHelp extends AbstractCommand {

    private GenBucketCommand command;

    public CommandHelp(GenBucketCommand command, GenBucket plugin) {
        super(plugin, "help", true);
        this.command = command;
    }

    @Override
    public boolean execute(CommandSender commandSender, String[] args) {
        commandSender.sendMessage(Message.CMD_HELP_HEADER.getMessage()
                .replace("%version%", GenBucket.get().getDescription().getVersion()));
        for (AbstractCommand commands : command.getCommands()) {
            if (commandSender.isOp() || commandSender.hasPermission(commands.getPermission())) {
                commandSender.sendMessage(ChatUtils.color(
                        Message.CMD_HELP_FORMAT.getMessage()
                                .replace("%command%", "/genbucket")
                                .replace("%args%", commands.getLabel())
                                .replace("%description%", commands.getDescription())));
            }
        }
        return false;
    }

    @Override
    public String getDescription() {
        return Message.CMD_HELP_DESC.getMessage();
    }

    @Override
    public String getPermission() {
        return Config.PERMISSION_HELP.getString();
    }

}
