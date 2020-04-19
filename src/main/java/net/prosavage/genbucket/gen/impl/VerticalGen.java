package net.prosavage.genbucket.gen.impl;

import com.cryptomorin.xseries.XMaterial;
import net.prosavage.genbucket.GenBucket;
import net.prosavage.genbucket.gen.GenType;
import net.prosavage.genbucket.gen.Generator;
import net.prosavage.genbucket.hooks.impl.CoreProtectHook;
import net.prosavage.genbucket.utils.Message;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

public class VerticalGen extends Generator {

    protected String direction = "up";

    public VerticalGen(GenBucket plugin, Player player, Material material, Block block, BlockFace face, boolean pseudo) {
        super(plugin, player, material, block, GenType.VERTICAL, pseudo);
        direction = GenBucket.get().getConfig().getString("VERTICAL." + getMaterial().toString() + ".direction", getMaterial().hasGravity() ? "up" : "down");
        if (direction.endsWith("automatic")) {
            direction = face == BlockFace.UP ? "up" : "down";
        }
        setIndex(getIndex() + (direction.equalsIgnoreCase("up") ? 1 : -1));
        if (isValidLocation(block)) {
            if (GenBucket.get().getConfig().getBoolean("sourceblock.no-source")) {
                this.setSourceMaterial(getMaterial());
                block.setType(getMaterial(), false);
            } else {
                this.setSourceMaterial(XMaterial.valueOf(GenBucket.get().getConfig().getString("sourceblock.item-name")).parseMaterial());
                block.setType(getSourceMaterial(), false);
            }
        } else {
            player.sendMessage(Message.GEN_CANT_PLACE.getMessage());
        }
    }

    public VerticalGen(String data) {
        super(GenBucket.get(), null, Material.valueOf(data.split(",")[0]), getBlockFromString(data.split(",")[1]), GenType.VERTICAL, Boolean.parseBoolean(data.split(",")[4]));
        setIndex(Integer.parseInt(data.split(",")[2]));
        this.direction = data.split(",")[3];
        setData(true);
    }

    public void run() {

        Block gen = getBlock().getWorld().getBlockAt(getBlock().getX(), getBlock().getY() + getIndex(), getBlock().getZ());

        if (!(getMaterial().hasGravity() && direction.equalsIgnoreCase("down")))
            setIndex(getIndex() + (direction.equalsIgnoreCase("up") ? 1 : -1));

        if (!isValidLocation(gen)) {
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

        if (!isNearSponge(gen, 3) && (getBlock().getY() + getIndex()) >= 0 && (getBlock().getY() + getIndex()) < 256) {
            gen.setType(getMaterial(), false);
            CoreProtectHook.logPlacement(getPlayer().getName(), gen);
        } else {
            getBlock().setType(getMaterial(), false);
            setFinished(true);
        }
    }

    public boolean isNearSponge(Block block, int radius) {
        if (!GenBucket.get().getConfig().getBoolean("sponge-check")) {
            return false;
        }
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
