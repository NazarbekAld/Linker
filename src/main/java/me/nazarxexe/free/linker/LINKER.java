package me.nazarxexe.free.linker;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Getter;
import me.nazarxexe.free.linker.network.check.GenerateQuote;
import me.nazarxexe.free.linker.network.check.NetworkCheckingResponse;
import me.nazarxexe.free.linker.platform.PlatformPool;
import me.nazarxexe.free.linker.platform.discord.Discord;
import me.nazarxexe.free.linker.platform.discord.command.DiscordCMD;
import me.nazarxexe.free.linker.platform.discord.command.subcommand.Accept;
import me.nazarxexe.free.linker.platform.discord.command.subcommand.Decline;
import me.nazarxexe.free.linker.platform.discord.command.subcommand.Ignore;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;

public final class LINKER extends JavaPlugin {

    @Getter
    private static PlatformPool platformPool;

    @Getter
    private static HikariDataSource dataSource;

    @Override
    public void onLoad() {
        network(this);
        database();
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
        DiscordCMD cmd = new DiscordCMD(this);
        Discord platform = new Discord(this.getConfig().getConfigurationSection("discord"), cmd);

        cmd.getDiscordSub().add(new Accept(cmd, platform, this));
        cmd.getDiscordSub().add(new Decline(cmd, this));
        cmd.getDiscordSub().add(new Ignore(this));
        this.getCommand("discord").setExecutor(cmd);
        this.getCommand("discord").setTabCompleter(cmd);

        platformPool.registerPlatform(platform);
    }

    private void database() {

        File dbconf = new File(this.getDataFolder(), "database.properties");
        try {
            boolean nonExist = dbconf.createNewFile();
            if (nonExist) {
                FileWriter writer = new FileWriter(dbconf);
                writer.write("dataSourceClassName=org.postgresql.ds.PGSimpleDataSource\n" +
                        "dataSource.user=test\n" +
                        "dataSource.password=test\n" +
                        "dataSource.databaseName=mydb\n" +
                        "dataSource.portNumber=5432\n" +
                        "dataSource.serverName=localhost");
                writer.flush();
                writer.close();
            }
        } catch (IOException e) {
            getLogger().log(Level.SEVERE, "Failed to created db file.", e);
        }

        dataSource = new HikariDataSource(new HikariConfig(dbconf.getAbsolutePath()));
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
