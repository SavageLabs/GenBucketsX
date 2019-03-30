package net.prosavage.genbucket.command;

import org.bukkit.command.CommandSender;

public interface Executable {

    boolean execute(CommandSender sender, String[] args);

    String getDescription();

    String getPermission();

}
