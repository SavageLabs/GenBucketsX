package net.prosavage.genbucket.gen;

import net.prosavage.genbucket.GenBucket;
import net.prosavage.genbucket.config.Config;
import net.prosavage.genbucket.hooks.impl.WorldBorderHook;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public abstract class Generator {

    private GenBucket plugin;
    private Player player;
    private Block block;
    private int index = 0;
    private boolean finished, data = false;
    private Material sourceMaterial;
    private GenData genData;

    public Generator(GenBucket plugin, Player player, Block block, GenData genData) {
        this.plugin = plugin;
        this.player = player;
        this.block = block;
        this.genData = genData;
    }

    public static Block getBlockFromString(String s) {
        if (s == null || s.trim().isEmpty()) {
            return null;
        }
        String[] parts = s.split(":");
        if (parts.length == 4) {
            World w = Bukkit.getServer().getWorld(parts[0]);
            int x = Integer.parseInt(parts[1]);
            int y = Integer.parseInt(parts[2]);
            int z = Integer.parseInt(parts[3]);
            return new Location(w, x, y, z).getBlock();
        }
        return null;
    }

    public Block getBlock() {
        return block;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public GenBucket getPlugin() {
        return plugin;
    }

    public Player getPlayer() {
        return player;
    }

    public Material getMaterial() {
        return genData.getItem().getType();
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public GenType getType() {
        return genData.getType();
    }

    public abstract void run();

    public boolean isValidLocation(Block block) {
        if (isOutsideBorder(block.getLocation())) {
            setFinished(true);
            return false;
        }

        if (GenBucket.get().hasWorldGuard() && !GenBucket.get().getWorldGuard().hasBuildPermission(player, block)) {
            return false;
        }

        if (Config.REPLACE_LIQUIDS.getOption() && block.isLiquid()) return true;

        return GenBucket.get().getReplacements().contains(block.getType()) || (isPseudo() && getMaterial() == block.getType());
    }

    public Material getSourceMaterial() {
        return sourceMaterial;
    }

    public void setSourceMaterial(Material sourceMaterial) {
        this.sourceMaterial = sourceMaterial;
    }

    public boolean isPseudo() {
        return genData.isPseudo();
    }

    public boolean isOutsideBorder(Location location) {
        if (!Config.HOOK_VANILLA_BORDER.getOption()) return false;
        if (Config.HOOK_BORDER_CHECK.getOption() && WorldBorderHook.isSetup() && WorldBorderHook.isOutside(location))
            return true;
        WorldBorder border = location.getWorld().getWorldBorder();
        if (GenBucket.getServerVersion() >= 11) {
            return !border.isInside(location);
        } else {
            double borderSize = border.getSize() / 2;
            Location offset = location.clone().subtract(border.getCenter());
            return offset.getX() < -borderSize || offset.getX() > borderSize || offset.getZ() < -borderSize || offset.getZ() > borderSize;
        }
    }

}
