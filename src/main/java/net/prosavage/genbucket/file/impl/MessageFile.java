package net.prosavage.genbucket.file.impl;

import net.prosavage.genbucket.GenBucket;
import net.prosavage.genbucket.file.CustomFile;
import net.prosavage.genbucket.utils.Message;

public class MessageFile extends CustomFile {

   public MessageFile() {
      super(GenBucket.get(), "", "messages");
      for (Message message : Message.values()) {
         getConfig().addDefault(message.getConfig(), message.getMessage().replace("�", "&"));
      }
      getConfig().options().copyDefaults(true);
      saveConfig();
   }


   public void init() {
      for (Message message : Message.values()) {
         message.setMessage(getConfig().getString(message.getConfig()).replace("&", "�"));
      }
   }
}
