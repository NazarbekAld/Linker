package me.nazarxexe.free.linker;

import me.nazarxexe.free.linker.network.check.GenerateQuote;
import me.nazarxexe.free.linker.network.check.NetworkCheckingResponse;
import me.nazarxexe.free.linker.platform.PlatformPool;
import me.nazarxexe.free.linker.platform.discord.Discord;
import me.nazarxexe.free.linker.platform.discord.command.DiscordCMD;
import me.nazarxexe.free.linker.platform.discord.command.subcommand.Accept;
import me.nazarxexe.free.linker.platform.discord.command.subcommand.Decline;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.logging.Level;

public final class LINKER extends JavaPlugin {

    private PlatformPool platformPool;

    @Override
    public void onLoad() {
        network(this);
    }

    @Override
    public void onEnable() {

        saveDefaultConfig();
        // Register the platforms -
        platformPool = new PlatformPool(this);
        discord(platformPool);
        platformPool.runPlatforms();

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    private void log(String... message) {
        for (String i : message) {
            getLogger().log(Level.INFO, i);
        }
    }



    private void discord(PlatformPool pool) {
        File discordf = new File(getDataFolder(), "discord");
        discordf.mkdir();
        DiscordCMD cmd = new DiscordCMD(this);
        Discord platform = new Discord(this.getConfig().getConfigurationSection("discord"), discordf, cmd);

        cmd.getDiscordSub().add(new Accept(cmd, platform, this));
        cmd.getDiscordSub().add(new Decline(cmd, this));
        this.getCommand("discord").setExecutor(cmd);
        this.getCommand("discord").setTabCompleter(cmd);

        platformPool.registerPlatform(platform);
    }

    private void network(Plugin plugin) {
        GenerateQuote generateQuote = new GenerateQuote();
        NetworkCheckingResponse quote = generateQuote.check();
        if (quote == null) {
            getLogger().log(Level.SEVERE, "Network is unavailable.");
            getLogger().log(Level.SEVERE, "Shutting down the plugin.");
            Bukkit.getScheduler().runTask(this, () -> Bukkit.getPluginManager().disablePlugin(plugin));
            return;
        }
        log(quote.response());
    }

}
