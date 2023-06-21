package me.nazarxexe.free.linker.platform.discord.command.subcommand;

import me.nazarxexe.free.linker.LINKER;
import me.nazarxexe.free.linker.platform.discord.command.DiscordSubCMD;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jooq.Table;
import org.jooq.impl.DSL;

import java.sql.SQLException;

public class Ignore implements DiscordSubCMD {

    private final Plugin plugin;

    public Ignore(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public String name() {
        return "ignore";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if (!(sender instanceof Player)) return;

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {

            Player player = (Player) sender;
           if (isIgnored(player)) {
               try {
                   DSL.using(LINKER.getDataSource().getConnection())
                           .deleteFrom(DSL.table("discord_ignore_list"))
                           .where(DSL.field("minecraft_user_uuid").eq(player.getUniqueId().toString()))
                           .execute();
                   player.sendMessage(ChatColor.RED + "Ignore mode disabled.");
               } catch (SQLException e) {
                   e.printStackTrace();
                   player.sendMessage(ChatColor.RED + "Something went wrong.");
               }
               return;
           }
            try {
                DSL.using(LINKER.getDataSource().getConnection())
                        .insertInto(DSL.table("discord_ignore_list"), DSL.field("minecraft_user_uuid"))
                        .values(player.getUniqueId().toString())
                        .execute();
                player.sendMessage(ChatColor.GREEN + "Ignore mode enabled.");
            } catch (SQLException e) {
                e.printStackTrace();
                player.sendMessage(ChatColor.RED + "Something went wrong.");
            }

        });

    }

    private boolean isIgnored(Player player) {

        try {
            return !(DSL.using(LINKER.getDataSource().getConnection())
                    .select()
                    .from("discord_ignore_list")
                    .where(DSL.field("minecraft_user_uuid").eq(player.getUniqueId().toString()))
                    .fetch().isEmpty());
        } catch (SQLException e) {
            e.printStackTrace();
            return true;
        }

    }
}
