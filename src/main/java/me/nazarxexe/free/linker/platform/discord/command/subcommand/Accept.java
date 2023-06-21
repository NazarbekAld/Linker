package me.nazarxexe.free.linker.platform.discord.command.subcommand;

import me.nazarxexe.free.linker.platform.discord.Discord;
import me.nazarxexe.free.linker.platform.discord.DiscordLinkedEvent;
import me.nazarxexe.free.linker.platform.discord.command.DiscordCMD;
import me.nazarxexe.free.linker.platform.discord.command.DiscordSubCMD;
import net.dv8tion.jda.api.entities.Message;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jooq.exception.DataAccessException;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public class Accept implements DiscordSubCMD {

    final DiscordCMD parent;
    final Discord platform;
    final Plugin plugin;

    public Accept(DiscordCMD parent, Discord platform, Plugin plugin) {
        this.parent = parent;
        this.platform = platform;
        this.plugin = plugin;
    }


    @Override
    public String name() {
        return "accept";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if (!(sender instanceof Player)) return;

        Player player = (Player) sender;

        if (!(parent.getIncomingRequest().containsKey(player.getUniqueId()))) {
            sender.sendMessage(ChatColor.RED + "No incoming requests!");
            return;
        }

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            Message message = parent.getIncomingRequest().get(player.getUniqueId());
            try {
                DSL.using(platform.getDataSource().getConnection())
                        .insertInto(DSL.table("linker"), DSL.field("discord_user_id"), DSL.field("minecraft_user_uuid"), DSL.field("link_date"))
                        .values(Long.parseLong(message.getAuthor().getId()), player.getUniqueId().toString(), DSL.currentTimestamp())
                        .execute();
                message.reply("Player successfully linked.")
                        .setMessageReference(message)
                        .setAllowedMentions(List.of(Message.MentionType.USER))
                        .queue();
                Bukkit.getScheduler().runTask(plugin, () -> Bukkit.getPluginManager().callEvent(new DiscordLinkedEvent(player, message)));
                parent.getIncomingRequest().remove(player.getUniqueId());
                player.sendMessage(ChatColor.GREEN + "Discord account successfully linked!");
            } catch (SQLException e) {
                message.reply("Failed to link. Something went wrong!")
                        .setMessageReference(message)
                        .setAllowedMentions(List.of(Message.MentionType.USER))
                        .queue();
                player.sendMessage(ChatColor.RED + "Failed to link. Something went wrong!");
                parent.getIncomingRequest().remove(player.getUniqueId());
            }catch (DataAccessException e) {
                message.reply("Failed to link. You or the player is already linked!")
                        .setMessageReference(message)
                        .setAllowedMentions(List.of(Message.MentionType.USER))
                        .queue();
                player.sendMessage(ChatColor.RED + "Failed to link. You or the discord user is already linked!");
                parent.getIncomingRequest().remove(player.getUniqueId());
            }

        });



    }

}
