package net.prosavage.genbucket.gen.impl;

import net.prosavage.genbucket.GenBucket;
import net.prosavage.genbucket.config.Config;
import net.prosavage.genbucket.config.Message;
import net.prosavage.genbucket.gen.GenData;
import net.prosavage.genbucket.gen.Generator;
import net.prosavage.genbucket.hooks.impl.CoreProtectHook;
import net.prosavage.genbucket.hooks.impl.FactionHook;
import net.prosavage.genbucket.utils.ChatUtils;
import net.prosavage.genbucket.utils.ItemUtils;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

public class HorizontalGen extends Generator {

    private BlockFace blockFace;
    protected BlockFace pDir;
    private GenData genData;

    public HorizontalGen(GenBucket plugin, Player player, Block block, BlockFace blockFace, GenData genData) {
        super(plugin, player, block, genData);
        this.blockFace = blockFace;
        this.genData = genData;
        if (GenBucket.getServerVersion() > 13) {
            this.pDir = player.getFacing();
        } else {
            this.pDir = ItemUtils.yawToFace(player.getLocation().getYaw(), false);
        }
        if (isValidLocation(block)) {
            if (!Config.USE_SOURCEBLOCK.getOption()) {
                this.setSourceMaterial(getMaterial());
                block.setType(getMaterial(), false);
                if (Config.USE_FACING.getOption()) ItemUtils.setFacing(block, pDir);
            } else {
                this.setSourceMaterial(ItemUtils.parseMaterial(Config.SOURCEBLOCK_MATERIAL.getString()));
                block.setType(getSourceMaterial(), false);
            }
        } else {
            ChatUtils.debug("cant-place validlocation failed");
            player.sendMessage(Message.GEN_CANT_PLACE.getMessage());
            setFinished(true);
            return;
        }
    }

    public void run() {
        if (((FactionHook) getPlugin().getHookManager().getPluginMap().get("Factions")).canBuild(getBlock(), getPlayer())) {
            setIndex(getIndex() + 1);
            Block gen = getBlock().getWorld().getBlockAt(getBlock().getX() + getIndex() * blockFace.getModX(), getBlock().getY(), getBlock().getZ() + getIndex() * blockFace.getModZ());

            if (!isValidLocation(gen)) {
                getBlock().setType(getMaterial(), false);
                if (Config.USE_FACING.getOption()) ItemUtils.setFacing(getBlock(), pDir);
                setFinished(true);
                return;
            }

            if (getPlayer() == null || !((FactionHook) getPlugin().getHookManager().getPluginMap().get("Factions")).canBuild(gen, getPlayer())) {
                getBlock().setType(getMaterial(), false);
                if (Config.USE_FACING.getOption()) ItemUtils.setFacing(getBlock(), pDir);
                setFinished(true);
                return;
            }

            if (getBlock().getType() != getSourceMaterial() && getPlayer() != null) {
                ChatUtils.debug("getBlock().getType() != getSourceMaterial() "
                        + getBlock().getType().name() +
                        " is not "
                        + getSourceMaterial().name());
                getPlayer().sendMessage(Message.GEN_CANCELLED.getMessage());
                getBlock().setType(getMaterial(), false);
                if (Config.USE_FACING.getOption()) ItemUtils.setFacing(getBlock(), pDir);
                setFinished(true);
                return;
            }

            if (getIndex() < genData.getHorizontalDistance()) {
                gen.setType(getMaterial(), false);
                ItemUtils.setBlockData(gen, genData.getItem().getDurability());
                if (Config.USE_FACING.getOption()) ItemUtils.setFacing(gen, pDir);
                try {
                    CoreProtectHook.logPlacement(getPlayer().getName(), gen);
                } catch (NullPointerException ignored) {
                    // ignored
                }
            } else {
                getBlock().setType(getMaterial(), false);
                if (Config.USE_FACING.getOption()) ItemUtils.setFacing(getBlock(), pDir);
                setFinished(true);
            }
        }
    }

    @Override
    public String toString() {
        return this.getMaterial() + "," + getLocation(this.getBlock().getLocation()) + "," + getIndex() + "," + blockFace.name() + "," + isPseudo();
    }

    public String getLocation(Location loc) {
        return loc.getWorld().getName() + ":" + loc.getBlockX() + ":" + loc.getBlockY() + ":" + loc.getBlockZ();
    }

}
