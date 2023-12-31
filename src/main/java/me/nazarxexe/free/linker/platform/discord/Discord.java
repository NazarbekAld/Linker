package me.nazarxexe.free.linker.platform.discord;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Getter;
import me.nazarxexe.free.linker.LINKER;
import me.nazarxexe.free.linker.platform.Platform;
import me.nazarxexe.free.linker.platform.discord.command.DiscordCMD;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

public class Discord implements Platform {

    final ConfigurationSection section;
    private @Getter JDA jda;
    private @Getter Logger logger;
    private @Getter final HikariDataSource dataSource = LINKER.getDataSource();
    final DiscordCMD cmd;
    public Discord(ConfigurationSection section, DiscordCMD cmd) {
        this.section = section;
        this.cmd = cmd;
    }

    @Override
    public String name() {
        return "Discord";
    }

    @Override
    public void start() {
        if (section == null) throw new RuntimeException("Section is null");

        logger = LoggerFactory.getLogger("Linker-Discord");

        if(section.getBoolean("enable", false)) {
            logger.info("Disabled.");
            return;
        }

        try {
            DSL.using(dataSource.getConnection())
                    .createTableIfNotExists("discord")
                    .column("discord_user_id", SQLDataType.BIGINT)
                    .column("minecraft_user_uuid", SQLDataType.VARCHAR(36))
                    .column("link_date", SQLDataType.TIMESTAMP)
                    .constraints(
                            DSL.unique("minecraft_user_uuid"),
                            DSL.unique("discord_user_id")
                    )
                    .execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            DSL.using(dataSource.getConnection())
                    .createTableIfNotExists("discord_ignore_list")
                    .column("minecraft_user_uuid", SQLDataType.VARCHAR(36))
                    .execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        List<String> activ = (List<String>) section.get("activity", List.of("WATCHING", "Breaking bad"));
        jda = JDABuilder
                .createLight(section.getString("token"), GatewayIntent.GUILD_MESSAGES,
                        GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MEMBERS, GatewayIntent.DIRECT_MESSAGES)
                .disableCache(CacheFlag.MEMBER_OVERRIDES, CacheFlag.VOICE_STATE)
                .setBulkDeleteSplittingEnabled(false)
                .setActivity(Activity.of(Activity.ActivityType.valueOf(activ.get(0)), activ.get(1)))
                .build();

        jda.addEventListener(new DiscordListener(this, cmd));

        try {
            jda.awaitReady();
        } catch (InterruptedException e) {
            logger.info("Failed to connect DiscordAPI.");
        }
    }

    /**
        Should execute async.

        @return success
     */
    public boolean reload() {

        try {
            jda.awaitShutdown();
            jda.awaitReady();
            return true;
        } catch (InterruptedException e) {
            return false;
        }
    }

    public void reply(Message to, String content) {
        to.getChannel().sendMessage(content)
                .setMessageReference(to)
                .setAllowedMentions(List.of(Message.MentionType.USER))
                .queue();
    }

}
