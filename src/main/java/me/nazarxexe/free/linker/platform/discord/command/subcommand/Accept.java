package me.nazarxexe.free.linker.platform.discord.command.subcommand;

import me.nazarxexe.free.linker.platform.discord.Discord;
import me.nazarxexe.free.linker.platform.discord.DiscordLinkedEvent;
import me.nazarxexe.free.linker.platform.discord.command.DiscordCMD;
import me.nazarxexe.free.linker.platform.discord.command.DiscordSubCMD;
import me.nazarxexe.free.linker.platform.discord.operations.LinkOperation;
import net.dv8tion.jda.api.entities.Message;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

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

            LinkOperation linkOperation = new LinkOperation(message, player);

            linkOperation.execute();

            if (linkOperation.output().equals("DB_UNIQUE")) {
                player.sendMessage(ChatColor.RED + "You or Discord user already linked.");
                platform.reply(message, "You or the player is already linked.");
                return;
            }
            if (linkOperation.output().equals("DB_ERROR")) {
                player.sendMessage(ChatColor.RED + "Something went wrong.");
                platform.reply(message, "Something went wrong send linking request later.");
                return;
            }
            Bukkit.getScheduler().runTask(plugin, () -> Bukkit.getPluginManager().callEvent(new DiscordLinkedEvent(player, message)));
            player.sendMessage(ChatColor.GREEN + "Successfully linked!");
            platform.reply(message, "Successfully linked!");

        });



    }

}
