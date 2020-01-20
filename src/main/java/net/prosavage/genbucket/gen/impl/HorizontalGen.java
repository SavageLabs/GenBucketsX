package net.prosavage.genbucket.gen.impl;

import net.prosavage.genbucket.GenBucket;
import net.prosavage.genbucket.gen.GenType;
import net.prosavage.genbucket.gen.Generator;
import net.prosavage.genbucket.hooks.impl.FactionHook;
import net.prosavage.genbucket.utils.Message;
import net.prosavage.genbucket.utils.XMaterial;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;

public class HorizontalGen extends Generator {

    private BlockFace blockFace;

    public HorizontalGen(GenBucket plugin, Player player, Material material, Block block, BlockFace blockFace) {
        super(plugin, player, material, block, GenType.HORIZONTAL);
        this.blockFace = blockFace;
        if (isValidLocation(block)) {
            if (GenBucket.get().getConfig().getBoolean("sourceblock.no-source")) {
                this.setSourceMaterial(getMaterial());
                block.setType(getMaterial());
            } else {
                this.setSourceMaterial(XMaterial.valueOf(GenBucket.get().getConfig().getString("sourceblock.item-name")).parseMaterial());
                block.setType(getSourceMaterial());
            }
        } else {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', Message.GEN_CANT_PLACE.getMessage()));
        }

    }

    public HorizontalGen(String data) {
        super(GenBucket.get(), null, Material.valueOf(data.split(",")[0]), getBlockFromString(data.split(",")[1]), GenType.VERTICAL);
        setIndex(Integer.parseInt(data.split(",")[2]));
        blockFace = BlockFace.valueOf(data.split(",")[3]);
        setData(true);
    }

    public void run() {
        try {
            if (isDataGen() || ((FactionHook) getPlugin().getHookManager().getPluginMap().get("Factions")).canBuild(getBlock(), getPlayer())) {
                setIndex(getIndex() + 1);
                Block gen = getBlock().getWorld().getBlockAt(getBlock().getX() + getIndex() * blockFace.getModX(), getBlock().getY(), getBlock().getZ() + getIndex() * blockFace.getModZ());

                if (!isDataGen() && !isValidLocation(gen)) {
                    getBlock().setType(getMaterial(), false);
                    setFinished(true);
                    return;
                }

                if (getPlayer() == null || !((FactionHook) getPlugin().getHookManager().getPluginMap().get("Factions")).canBuild(gen, getPlayer())) {
                    getBlock().setType(getMaterial(), false);
                    setFinished(true);
                    return;
                }

                if (getBlock().getType() != getSourceMaterial() && getPlayer() != null) {
                    getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', Message.GEN_CANCELLED.getMessage()));
                    getBlock().setType(getMaterial(), false);
                    setFinished(true);
                    return;
                }

                if (getIndex() < getPlugin().getConfig().getInt("distance")) {
                    gen.setType(getMaterial(), false);
                } else {
                    getBlock().setType(getMaterial(), false);
                    setFinished(true);
                }
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return this.getMaterial() + "," + getLocation(this.getBlock().getLocation()) + "," + getIndex() + "," + blockFace.name();
    }

    public String getLocation(Location loc) {
        return loc.getWorld().getName() + ":" + loc.getBlockX() + ":" + loc.getBlockY() + ":" + loc.getBlockZ();
    }

}
