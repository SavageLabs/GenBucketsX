package net.prosavage.genbucket.utils;

import org.bukkit.ChatColor;

public enum Message {
   NO_PERMISSION("no-permission", "&cYou do not have permission."),
   GEN_CANCELLED("gen-cancelled", "&cYou have cancelled your generation."),
   GEN_HAS_ALREADY("gen-has-already", "&cYou already have this gen bucket!"),
   GEN_CANT_PLACE("gen-cant-place", "&cYou can't place gen buckets here!"),
   GEN_CANT_AFFORD("gen-cant-afford", "&cYou do not have sufficient funds."),
   GEN_CHARGED("gen-charged", "&cYou have been charged ${amount}"),
   GEN_ENEMY_NEARBY("gen-enemy-nearby", "&cYou cannot place gen buckets with enemies nearby!");

   String config, message;

   Message(String config, String message) {
      this.config = config;
      this.message = ChatColor.translateAlternateColorCodes('&', message);
   }

   public String getConfig() {
      return config;
   }

   public String getMessage() {
      return message;
   }

   public void setMessage(String message) {
      this.message = message;
   }

}