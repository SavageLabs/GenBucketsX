package net.prosavage.genbucket.gen.impl;

import net.prosavage.genbucket.GenBucket;
import net.prosavage.genbucket.gen.GenType;
import net.prosavage.genbucket.gen.Generator;
import net.prosavage.genbucket.utils.Message;
import net.prosavage.genbucket.utils.MultiversionMaterials;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.WorldBorder;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;


public class VerticalGen extends Generator {

   protected String direction;

   public VerticalGen(GenBucket plugin, Player player, Material material, Block block) {
      super(plugin, player, material, block, GenType.VERTICAL);
      direction = GenBucket.get().getConfig().getString("VERTICAL." + getMaterial().toString() + ".direction", getMaterial().hasGravity() ? "up" : "down");
      setIndex(getIndex() + (direction.equalsIgnoreCase("up") ? 1 : -1));
      if (isValidLocation(block)) {
         if (GenBucket.get().getConfig().getBoolean("sourceblock.no-source")) {
            this.setSourceMaterial(getMaterial());
            block.setType(getMaterial());
         } else {
            this.setSourceMaterial(MultiversionMaterials.valueOf(GenBucket.get().getConfig().getString("sourceblock.item-name")).parseMaterial());
            block.setType(getSourceMaterial());
         }
      } else {
         player.sendMessage(Message.GEN_CANT_PLACE.getMessage());
      }
   }

   public VerticalGen(String data) {
      super(GenBucket.get(), null, Material.valueOf(data.split(",")[0]), getBlockfromString(data.split(",")[1]), GenType.VERTICAL);
      setIndex(Integer.valueOf(data.split(",")[2]));
      setData(true);
   }

   @Override
   public boolean isValidLocation(Block block) {
      Location loc = block.getLocation();
      WorldBorder wb = loc.getWorld().getWorldBorder();
      double size = wb.getSize() / 2.0;

      if (loc.getBlockX() >= size || -loc.add(1, 0, 0).getX() >= size || loc.getBlockZ() >= size || -loc.add(0, 0, 1).getBlockZ() >= size) {
         setFinished(true);
         return false;
      }

      if (GenBucket.get().getConfig().getBoolean("psuedo") && getMaterial().equals(block.getType())) {
         return true;
      }


      return GenBucket.get().getReplacements().contains(block.getType());

   }


   public void run() {
      Block gen = getBlock().getWorld().getBlockAt(getBlock().getX(), getBlock().getY() + getIndex(), getBlock().getZ());

      setIndex(getIndex() + (direction.equalsIgnoreCase("up") ? 1 : -1));


      getBlock().getChunk().load();

      if (!isDataGen() && !isValidLocation(gen)) {
         getBlock().setType(getMaterial());
         setFinished(true);
         return;
      }
      if (getBlock().getType() != getSourceMaterial() && getPlayer() != null) {
         getPlayer().sendMessage(Message.GEN_CANCELLED.getMessage());
         getBlock().setType(getMaterial());
         setFinished(true);
         return;
      }
      if (!isNearSponge(gen, 3) && (getBlock().getY() + getIndex()) >= 0 && (getBlock().getY() + getIndex()) < 256) {
         gen.setType(getMaterial());
      } else {

         getBlock().setType(getMaterial());
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
      return this.getMaterial() + "," + getLocation(this.getBlock().getLocation()) + "," + getIndex();
   }

   public String getLocation(Location loc) {
      return loc.getWorld().getName() + ":" + loc.getBlockX() + ":" + loc.getBlockY() + ":" + loc.getBlockZ();
   }

}
