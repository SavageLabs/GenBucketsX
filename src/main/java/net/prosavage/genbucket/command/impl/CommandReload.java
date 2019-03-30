package net.prosavage.genbucket.command.impl;

import net.md_5.bungee.api.ChatColor;
import net.prosavage.genbucket.GenBucket;
import net.prosavage.genbucket.command.AbstractCommand;
import net.prosavage.genbucket.command.GenBucketCommand;
import net.prosavage.genbucket.utils.Message;
import net.prosavage.genbucket.utils.MultiversionMaterials;
import org.bukkit.command.CommandSender;

public class CommandReload extends AbstractCommand {

    public CommandReload(GenBucket plugin) {
        super(plugin, "reload", false);
        this.alias.add("r");
    }

    @Override
    public boolean execute(CommandSender commandSender, String[] args) {
        getPlugin().reloadConfig();
        getPlugin().getReplacements().clear();
        getPlugin().getConfig().getStringList("replace-blocks").forEach(s -> getPlugin().materials.add(MultiversionMaterials.valueOf(s).parseMaterial()));
        commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', Message.PLUGIN_RELOAD.getMessage()));
        return false;
    }

    @Override
    public String getDescription() {
        return "Reloads the plugin's configs.";
    }

    @Override
    public String getPermission() {
        return "genbucket.command.admin";
    }

}
