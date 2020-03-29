package net.prosavage.genbucket.command;

import net.prosavage.genbucket.GenBucket;
import net.prosavage.genbucket.command.impl.CommandGive;
import net.prosavage.genbucket.command.impl.CommandHelp;
import net.prosavage.genbucket.command.impl.CommandMain;
import net.prosavage.genbucket.command.impl.CommandReload;
import net.prosavage.genbucket.utils.ChatUtils;
import net.prosavage.genbucket.utils.Message;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class GenBucketCommand implements CommandExecutor {

    private Map<Class, AbstractCommand> subcommands = new HashMap<>();
    private GenBucket plugin;

    public GenBucketCommand(GenBucket plugin) {
        this.plugin = plugin;
        addCommand(new CommandHelp(this, plugin));
        addCommand(new CommandMain(plugin));
        addCommand(new CommandGive(plugin));
        addCommand(new CommandReload(plugin));
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String string, String[] args) {
        if (args.length == 0) {
            if (!(commandSender instanceof Player)) { // If Console, send help instead of GUI
                subcommands.get(CommandHelp.class).execute(commandSender, args);
            } else if (commandSender.hasPermission(subcommands.get(CommandMain.class).getPermission())) {
                subcommands.get(CommandMain.class).execute(commandSender, args);
            } else {
                commandSender.sendMessage(ChatUtils.color(Message.NO_PERMISSION.getMessage()));
            }
            return false;
        }

        for (AbstractCommand abstractCommand : subcommands.values()) {

            if (!args[0].equalsIgnoreCase(abstractCommand.getLabel())) continue;

            if (!(commandSender instanceof Player) && abstractCommand.isPlayerRequired()) {
                commandSender.sendMessage(ChatUtils.color(Message.PLAYER_REQUIRED.getMessage()));
                return false;
            }

            if (!commandSender.isOp() && abstractCommand.getPermission() != null && !commandSender.hasPermission(abstractCommand.getPermission())) {
                commandSender.sendMessage(ChatUtils.color(Message.NO_PERMISSION.getMessage()));
                return false;
            }
            if (args[0].equalsIgnoreCase(abstractCommand.getLabel()) || abstractCommand.alias.contains(args[0])) {
                return abstractCommand.execute(commandSender, args);
            }
        }
        return false;
    }

    public void addCommand(AbstractCommand command) {
        this.subcommands.put(command.getClass(), command);
    }

    public Collection<AbstractCommand> getCommands() {
        return Collections.unmodifiableCollection(subcommands.values());
    }

}

