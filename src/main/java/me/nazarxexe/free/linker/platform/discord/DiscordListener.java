package me.nazarxexe.free.linker.platform.discord;

import me.nazarxexe.free.linker.platform.discord.command.DiscordCMD;
import me.nazarxexe.free.linker.platform.discord.operations.RequestOperation;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class DiscordListener extends ListenerAdapter implements EventListener {

    final Discord platform;
    final DiscordCMD cmd;

    public DiscordListener(Discord platform, DiscordCMD cmd) {
        this.platform = platform;
        this.cmd = cmd;
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {

        if (!(platform.section.getStringList("link_channels").contains(event.getMessage().getChannel().getId()))) return;

        String[] content = event.getMessage().getContentRaw().split(" ");
        if (!(content[0].equals("!link"))) return;

        RequestOperation operation = new RequestOperation(event.getMessage(), cmd);
        operation.execute();

        if (operation.output().equals("OFFLINE")) {
            platform.reply(event.getMessage(), "Player is offline.");
            return;
        }
        if (operation.output().equals("IGNORE")) {
            platform.reply(event.getMessage(), "Player is on ignore mode.");
            return;
        }
        if (operation.output().equals("BLOCKED")) {
            platform.reply(event.getMessage(), "Player already has a request.");
            return;
        }
        Player player = Bukkit.getPlayer(content[1]);
        platform.reply(event.getMessage(), "Sending request to the player...");
        player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                event.getAuthor().getName() + " is trying to link this account.\n" +
                        "&a/discord accept &f- Confirm the linking request.\n" +
                        "&c/discord decline &f- Decline the linking request."
                )
        );



    }

}
