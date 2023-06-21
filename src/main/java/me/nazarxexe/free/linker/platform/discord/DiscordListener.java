package me.nazarxexe.free.linker.platform.discord;

import me.nazarxexe.free.linker.platform.discord.command.DiscordCMD;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

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
        System.out.println(Arrays.toString(content));
        if (!(content[0].equals("!link"))) return;

        if (Arrays.stream(content).count() == 1) {
            reply(event.getMessage(), "Please type player nickname in the server.");
            return;
        }
        String nickname = content[1];

        Player player = Bukkit.getServer().getPlayer(nickname);

        if (player == null) {
            reply(event.getMessage(), "Player is not online.");
            return;
        }
        if (cmd.getIncomingRequest().containsKey(player.getUniqueId())) {
            reply(event.getMessage(), "Player already has a request.");
            return;
        }

        reply(event.getMessage(), "Verify message sent to " + content[1] + ". Waiting for confirmation...");

        player.sendMessage(
                ChatColor.translateAlternateColorCodes('&',
                        "&e" + event.getMessage().getAuthor().getName() + " is trying to link to this account - \n" +
                                "&a/discord accept - &fAccept \n" +
                                "&c/discord deny - &fDeny"
                )
        );

        cmd.getIncomingRequest().put(player.getUniqueId(), event.getMessage());

    }

    private void reply(Message to, String content) {
        to.getChannel().sendMessage(content)
                .setMessageReference(to)
                .setAllowedMentions(List.of(Message.MentionType.USER))
                .queue();
    }
}
