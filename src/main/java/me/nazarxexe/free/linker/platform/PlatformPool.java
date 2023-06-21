package me.nazarxexe.free.linker.platform;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;

public class PlatformPool {
    private HashMap<String , Platform> platforms;
    private final Plugin plugin;

    public PlatformPool(Plugin plugin) {
        this.platforms = new HashMap<>();
        this.plugin = plugin;
    }

    public void registerPlatform(Platform platform) {
        platforms.put(platform.name(), platform);
    }

    public void runPlatforms() {
        platforms.forEach((name, platform) -> {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, platform::start);
        });
    }

    public Platform getPlatform(String name) {
        return platforms.get(name);
    }

}
