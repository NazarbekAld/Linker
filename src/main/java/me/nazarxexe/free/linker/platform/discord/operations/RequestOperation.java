package me.nazarxexe.free.linker.platform.discord.operations;

import me.nazarxexe.free.linker.LINKER;
import me.nazarxexe.free.linker.platform.discord.command.DiscordCMD;
import net.dv8tion.jda.api.entities.Message;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jooq.impl.DSL;

import java.sql.SQLException;

public class RequestOperation implements Operation<String>{

    final Message message;
    final DiscordCMD cmd;
    String output;


    public RequestOperation(Message message, DiscordCMD cmd) {
        this.message = message;
        this.cmd = cmd;
    }


    @Override
    public void execute() {

        String[] requestBody = message.getContentRaw().split(" ");

        Player player = Bukkit.getServer().getPlayer(requestBody[1]);

        if (player == null) {
            output = "OFFLINE";
            return;
        }
        if (isIgnored(player)) {
            output = "IGNORE";
            return;
        }
        if (cmd.getIncomingRequest().containsKey(player.getUniqueId())) {
            output = "BLOCKED";
            return;
        }
        cmd.getIncomingRequest().put(player.getUniqueId(), message);
        output = "OK";
    }

    @Override
    public String output() {
        return output;
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
