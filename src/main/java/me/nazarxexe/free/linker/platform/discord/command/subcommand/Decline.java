package me.nazarxexe.free.linker.platform.discord.command.subcommand;

import me.nazarxexe.free.linker.platform.discord.command.DiscordCMD;
import me.nazarxexe.free.linker.platform.discord.command.DiscordSubCMD;
import me.nazarxexe.free.linker.platform.discord.operations.CancelOperation;
import net.dv8tion.jda.api.entities.Message;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.List;

public class Decline implements DiscordSubCMD {

    final DiscordCMD parent;
    final Plugin plugin;

    public Decline(DiscordCMD parent, Plugin plugin) {
        this.parent = parent;
        this.plugin = plugin;
    }

    @Override
    public String name() {
        return "decline";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            Player player = (Player) sender;
            Message message = parent.getIncomingRequest().get(player.getUniqueId());

            CancelOperation cancelOperation = new CancelOperation(parent, player);

            if (!(cancelOperation.output().equals("OK"))) {
                player.sendMessage(ChatColor.RED + "No incoming requests!");
                return;
            }
            message.reply("Link request is declined.")
                            .setAllowedMentions(List.of(Message.MentionType.USER))
                            .setMessageReference(message)
                                    .queue();
            player.sendMessage(ChatColor.GREEN + "Request is declined!");
        });

    }
}
