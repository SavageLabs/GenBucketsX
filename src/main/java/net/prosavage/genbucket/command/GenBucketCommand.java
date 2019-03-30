package net.prosavage.genbucket.command;

import net.prosavage.genbucket.GenBucket;
import net.prosavage.genbucket.command.impl.CommandGive;
import net.prosavage.genbucket.command.impl.CommandHelp;
import net.prosavage.genbucket.command.impl.CommandMain;
import net.prosavage.genbucket.command.impl.CommandReload;
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

    // REDO message and plugin hook

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String string, String[] args) {
        if (args.length == 0) {
            AbstractCommand helpCommand = subcommands.get(CommandHelp.class);
            if (helpCommand.getPermission() != null && commandSender.hasPermission(helpCommand.getPermission())) {
                subcommands.get(CommandHelp.class).execute(commandSender, args);
            } else {
                subcommands.get(CommandMain.class).execute(commandSender, args);
            }
            return false;
        }

        for (AbstractCommand abstractCommand : subcommands.values()) {
            if (!(commandSender instanceof Player) && abstractCommand.isPlayerRequired()) {
                commandSender.sendMessage(Message.PLAYER_REQUIRED.getMessage());
                return false;
            }
            if (abstractCommand.getPermission() != null && !commandSender.hasPermission(abstractCommand.getPermission()) || !commandSender.isOp()) {
                commandSender.sendMessage(Message.NO_PERMISSION.getMessage());
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

