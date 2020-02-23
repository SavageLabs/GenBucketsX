package net.prosavage.genbucket.command.impl;

import net.prosavage.genbucket.GenBucket;
import net.prosavage.genbucket.command.AbstractCommand;
import net.prosavage.genbucket.command.GenBucketCommand;
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
        for (AbstractCommand commands : command.getCommands()) {
            if (commandSender.isOp() || commandSender.hasPermission(commands.getPermission())) {
                commandSender.sendMessage(ChatUtils.color("&c/genbucket " + commands.getLabel() + " &8> &7" + commands.getDescription()));
            }
        }
        return false;
    }

    @Override
    public String getDescription() {
        return "Basic help command.";
    }

    @Override
    public String getPermission() {
        return "genbucket.command.help";
    }

}
