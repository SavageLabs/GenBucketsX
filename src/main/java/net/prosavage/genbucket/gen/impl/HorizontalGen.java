package net.prosavage.genbucket.gen.impl;

import com.cryptomorin.xseries.XMaterial;
import net.prosavage.genbucket.GenBucket;
import net.prosavage.genbucket.gen.GenType;
import net.prosavage.genbucket.gen.Generator;
import net.prosavage.genbucket.hooks.impl.CoreProtectHook;
import net.prosavage.genbucket.hooks.impl.FactionHook;
import net.prosavage.genbucket.utils.Message;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;

public class HorizontalGen extends Generator {

    private BlockFace blockFace;

    public HorizontalGen(GenBucket plugin, Player player, Material material, Block block, BlockFace blockFace, boolean pseudo) {
        super(plugin, player, material, block, GenType.HORIZONTAL, pseudo);
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
            player.sendMessage(Message.GEN_CANT_PLACE.getMessage());
        }

    }

    public HorizontalGen(String data) {
        super(GenBucket.get(), null, Material.valueOf(data.split(",")[0]), getBlockFromString(data.split(",")[1]), GenType.VERTICAL, Boolean.parseBoolean(data.split(",")[4]));
        String[] parsedData = data.split(",");
        setIndex(Integer.parseInt(parsedData[2]));
        blockFace = BlockFace.valueOf(parsedData[3]);
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
                    getPlayer().sendMessage(Message.GEN_CANCELLED.getMessage());
                    getBlock().setType(getMaterial(), false);
                    setFinished(true);
                    return;
                }

                if (getIndex() < getPlugin().getConfig().getInt("distance")) {
                    gen.setType(getMaterial(), false);
                    CoreProtectHook.logPlacement(getPlayer().getName(), gen);
                } else {
                    getBlock().setType(getMaterial(), false);
                    setFinished(true);
                }
            }
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            Bukkit.getServer().getLogger().log(Level.SEVERE, "Contact the author and send this error: " + e);
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
