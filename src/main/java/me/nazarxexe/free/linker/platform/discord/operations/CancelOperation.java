package me.nazarxexe.free.linker.platform.discord.operations;

import me.nazarxexe.free.linker.platform.discord.command.DiscordCMD;
import org.bukkit.entity.Player;

public class CancelOperation implements Operation<String>{

    private final DiscordCMD cmd;
    private final Player player;
    private String output;

    public CancelOperation(DiscordCMD cmd, Player player) {
        this.cmd = cmd;
        this.player = player;
    }

    @Override
    public void execute() {
        if (!(cmd.getIncomingRequest().containsKey(player.getUniqueId()))){
            output = "NOT_EXIST";
            return;
        }
        output = "OK";
    }

    @Override
    public String output() {
        return output;
    }
}
