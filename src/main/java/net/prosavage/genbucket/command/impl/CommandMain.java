package net.prosavage.genbucket.command.impl;

import com.boydti.fawe.util.EditSessionBuilder;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.blocks.BaseBlock;
import net.prosavage.genbucket.GenBucket;
import net.prosavage.genbucket.command.AbstractCommand;
import net.prosavage.genbucket.command.GenBucketCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.material.MaterialData;

public class CommandMain extends AbstractCommand {

    public CommandMain(GenBucket plugin) {
        super(plugin, "menu", true);
    }

    @Override
    public boolean execute(CommandSender commandSender, String[] args) {
        Player player = (Player) commandSender;
        player.openInventory(getPlugin().generationShopGUI.init().getInventory());
        return false;
    }

    @Override
    public String getDescription() {
        return "Displays GenBucket menu.";
    }

    @Override
    public String getPermission() {
        return null;
    }

}
