package net.prosavage.genbucket.hooks;

public interface PluginHook<T> {

   T setup();

   String getName();

}
