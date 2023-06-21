package me.nazarxexe.free.linker.platform.discord;

import net.dv8tion.jda.api.entities.Message;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class DiscordLinkedEvent extends Event {
    public final Player player;
    public final Message message;
    private static final HandlerList handlerList = new HandlerList();

    public DiscordLinkedEvent(Player player, Message message) {
        this.player = player;
        this.message = message;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlerList;
    }
}
