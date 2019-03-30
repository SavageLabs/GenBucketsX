package net.prosavage.genbucket.hooks;

import net.prosavage.genbucket.GenBucket;

public interface PluginHook<T> {

   T setup(GenBucket genBucket);

   String getName();

}
