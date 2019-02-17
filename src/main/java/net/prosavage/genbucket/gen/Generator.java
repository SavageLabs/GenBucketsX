package net.prosavage.genbucket.gen;

import net.prosavage.genbucket.GenBucket;
import net.prosavage.genbucket.hooks.HookManager;
import net.prosavage.genbucket.hooks.impl.WorldGuardHook;
import net.prosavage.genbucket.utils.MultiversionMaterials;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;


public abstract class Generator {

   private GenBucket plugin;
   private Player player;
   private Material material;
   private Block block;
   private int index = 0;
   private boolean finished, data = false;
   private GenType type;
   private Material sourceMaterial;

   public Generator(GenBucket plugin, Player player, Material material, Block block, GenType genType) {
      this.plugin = plugin;
      this.player = player;
      this.material = material;
      this.block = block;
      this.type = genType;

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
      return material;
   }

   public int getIndex() {
      return index;
   }

   public void setIndex(int index) {
      this.index = index;
   }

   public GenType getType() {
      return type;
   }

   public boolean isDataGen() {
      return this.data;
   }

   public void setData(boolean data) {
      this.data = data;
   }

   public abstract void run();

   public boolean isValidLocation(Block block) {
      Location loc = block.getLocation();
      WorldBorder wb = loc.getWorld().getWorldBorder();
      double size = wb.getSize() / 2.0;
      if (loc.getBlockX() >= size || -loc.add(1, 0, 0).getX() >= size || loc.getBlockZ() >= size || -loc.add(0, 0, 1).getBlockZ() >= size) {
         setFinished(true);
         return false;
      }

      WorldGuardHook wgHook = ((WorldGuardHook) HookManager.getPluginMap().get("WorldGuard"));

      if (wgHook != null && !wgHook.canBuild(player, block)) {
         return false;
      }

      return GenBucket.get().getReplacements().contains(block.getType());
   }

   public Material getSourceMaterial() {
      return sourceMaterial;
   }

   public void setSourceMaterial(Material sourceMaterial) {
      this.sourceMaterial = sourceMaterial;
   }
}
