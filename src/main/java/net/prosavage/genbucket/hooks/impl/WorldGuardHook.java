package net.prosavage.genbucket.hooks.impl;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import net.prosavage.genbucket.GenBucket;
import net.prosavage.genbucket.hooks.PluginHook;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class WorldGuardHook implements PluginHook<WorldGuardHook> {

   private WorldGuardPlugin worldGuardPlugin;

   @Override
   public WorldGuardHook setup() {
      if (GenBucket.get().getServer().getPluginManager().getPlugin("WorldGuard") == null) {
         this.worldGuardPlugin = null;
         return this;
      }
      this.worldGuardPlugin = WorldGuardPlugin.inst();
      return this;
   }

   public boolean canBuild(Player player, Block block) {
      if (worldGuardPlugin == null) {
         return true;
      }
      return worldGuardPlugin.canBuild(player, block);
   }

   @Override
   public String getName() {
      return "WorldGuard";
   }

}
