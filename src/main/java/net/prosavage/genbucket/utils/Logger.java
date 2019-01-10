package net.prosavage.genbucket.utils;

import net.prosavage.genbucket.GenBucket;
import org.bukkit.ChatColor;

public class Logger {

   public static void print(String message, PrefixType type) {
      GenBucket.get().getServer().getConsoleSender().sendMessage(type.getPrefix() + message);
   }

   public enum PrefixType {

      WARNING(ChatColor.RED + "WARNING: "), NONE(""), DEFAULT(ChatColor.GOLD + "[GENBUCKET] "), FAILED(ChatColor.RED + "FAILED: ");

      private String prefix;

      PrefixType(String prefix) {
         this.prefix = prefix;
      }

      public String getPrefix() {
         return this.prefix;
      }

   }

}
