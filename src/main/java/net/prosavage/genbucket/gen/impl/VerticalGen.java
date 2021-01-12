package net.prosavage.genbucket.gen.impl;

import com.cryptomorin.xseries.XMaterial;
import net.prosavage.genbucket.GenBucket;
import net.prosavage.genbucket.config.Config;
import net.prosavage.genbucket.config.Message;
import net.prosavage.genbucket.gen.GenData;
import net.prosavage.genbucket.gen.GenDirection;
import net.prosavage.genbucket.gen.Generator;
import net.prosavage.genbucket.hooks.impl.CoreProtectHook;
import net.prosavage.genbucket.utils.ChatUtils;
import net.prosavage.genbucket.utils.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

public class VerticalGen extends Generator {

    protected BlockFace pDir;
    private int blockDataValue = 0;
    private GenData genData;
    private BlockFace direction;

    public VerticalGen(GenBucket plugin, Player player, Block block, BlockFace face, GenData genData) {
        super(plugin, player, block, genData);
        this.genData = genData;
        if (GenBucket.getServerVersion() < 13)
            this.blockDataValue = genData.getItem().getDurability();
        ChatUtils.debug("face=" + face);
        ChatUtils.debug("direction=" + genData.getDirection());
        if (genData.getDirection() == GenDirection.AUTO) {
            if (face == BlockFace.DOWN) {
                direction = BlockFace.DOWN;
            } else {
                if (Config.DEFAULT_AUTO_VERTICAL.getString().equalsIgnoreCase("UP"))
                    direction = BlockFace.UP;
                else
                    direction = BlockFace.DOWN;
            }
        } else if (genData.getDirection() == GenDirection.UP) {
            direction = BlockFace.UP;
        } else {
            direction = BlockFace.DOWN;
        }
        if (!Config.ALLOW_GRAVITY_DOWN.getOption() && getMaterial().hasGravity() && direction == BlockFace.DOWN) {
            direction = BlockFace.UP;
        }
        if (GenBucket.getServerVersion() > 13) {
            this.pDir = player.getFacing();
        } else {
            this.pDir = ItemUtils.yawToFace(player.getLocation().getYaw(), false);
        }
        setIndex(getIndex() + (direction == BlockFace.UP ? 1 : -1));
        if (isValidLocation(block)) {
            if (!Config.USE_SOURCEBLOCK.getOption()) {
                this.setSourceMaterial(getMaterial());
                if (Config.ALLOW_GRAVITY_DOWN.getOption() && getMaterial().hasGravity() && direction == BlockFace.DOWN) {
                    Block underGen = block.getRelative(BlockFace.DOWN);
                    if (underGen.getY() > 0 && (underGen.getType() == XMaterial.AIR.parseMaterial()
                            || (Config.REPLACE_LIQUIDS.getOption() && underGen.isLiquid()))) {
                        ChatUtils.debug("Placing support block under...");
                        Material orUnderGenType = underGen.getType();
                        underGen.setType(XMaterial.GLASS.parseMaterial(), false);
                        Bukkit.getScheduler().runTaskLater(GenBucket.get(), () -> {
                            if (underGen.getType() == XMaterial.GLASS.parseMaterial())
                                underGen.setType(orUnderGenType, false);
                        }, Config.GENERATION_DELAY.getInt() + 5L);
                    }
                }
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

        Block gen = getBlock().getWorld().getBlockAt(getBlock().getX(), getBlock().getY() + getIndex(), getBlock().getZ());
        if (getSourceMaterial() == null) {
            ChatUtils.debug("getSourceMaterial is null for some reason! Patching it for now..");
            if (!Config.USE_SOURCEBLOCK.getOption()) {
                this.setSourceMaterial(getBlock().getType());
            } else {
                this.setSourceMaterial(ItemUtils.parseMaterial(Config.SOURCEBLOCK_MATERIAL.getString()));
            }
            if (getSourceMaterial() == null)
                ChatUtils.debug("Something went wrong so that SourceMaterial returns null. check your Material in config!");
        }

        setIndex(getIndex() + (direction == BlockFace.UP ? 1 : -1));

        if (!isValidLocation(gen)) {
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

        if (!isNearSponge(gen, 3) && (getBlock().getY() + getIndex()) >= 0 && (getBlock().getY() + getIndex()) < 256) {
            if (Config.ALLOW_GRAVITY_DOWN.getOption() && getMaterial().hasGravity() && direction == BlockFace.DOWN) {
                Block underGen = gen.getRelative(BlockFace.DOWN);
                if (underGen.getY() > 0 && (underGen.getType() == XMaterial.AIR.parseMaterial()
                        || (Config.REPLACE_LIQUIDS.getOption() && underGen.isLiquid()))) {
                    ChatUtils.debug("Placing support block under...");
                    Material orUnderGenType = underGen.getType();
                    underGen.setType(XMaterial.GLASS.parseMaterial(), false);
                    Bukkit.getScheduler().runTaskLater(GenBucket.get(), () -> {
                        if (underGen.getType() == XMaterial.GLASS.parseMaterial())
                            underGen.setType(orUnderGenType, false);
                    }, Config.GENERATION_DELAY.getInt() + 5L);
                }
            }
            gen.setType(getMaterial(), false);
            ItemUtils.setBlockData(gen, blockDataValue);
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

    public boolean isNearSponge(Block block, int radius) {
        if (!Config.USE_SPONGE_CHECK.getOption())
            return false;
        for (int x = block.getX() - radius; x <= block.getX() + radius; x++) {
            for (int z = block.getZ() - radius; z <= block.getZ() + radius; z++) {
                if (block.getWorld().getBlockAt(x, block.getY() - radius, z).getType() == Material.SPONGE)
                    return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return this.getMaterial() + "," + getLocation(this.getBlock().getLocation()) + "," + getIndex() + "," + direction + "," + isPseudo();
    }

    public String getLocation(Location loc) {
        return loc.getWorld().getName() + ":" + loc.getBlockX() + ":" + loc.getBlockY() + ":" + loc.getBlockZ();
    }

}
