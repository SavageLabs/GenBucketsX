package net.prosavage.genbucket.hooks.impl;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import net.prosavage.genbucket.GenBucket;
import net.prosavage.genbucket.hooks.PluginHook;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class WorldGuardHook implements PluginHook<WorldGuardHook> {

   private WorldGuardPlugin worldGuardPlugin;

   private static boolean instantiated = false;

   public WorldGuardHook() {
      super();
   }

   @Override
   public WorldGuardHook setup(GenBucket plugin) {
      this.worldGuardPlugin = WorldGuardPlugin.inst();
      instantiated = true;
      return this;
   }

   public boolean canBuild(Player player, Block block) {
      if (!instantiated) {
         return true;
      }
      return worldGuardPlugin.canBuild(player, block);
   }


   @Override
   public String getName() {
      return "WorldGuard";
   }

}
