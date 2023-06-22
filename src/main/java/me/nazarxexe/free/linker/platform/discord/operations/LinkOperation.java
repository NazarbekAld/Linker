package me.nazarxexe.free.linker.platform.discord.operations;

import me.nazarxexe.free.linker.LINKER;
import net.dv8tion.jda.api.entities.Message;
import org.bukkit.entity.Player;
import org.jooq.exception.DataAccessException;
import org.jooq.impl.DSL;

import java.sql.SQLException;

public class LinkOperation implements Operation<String> {

    private final Message message;
    private final Player player;

    private String output;

    public LinkOperation(Message message, Player player) {
        this.message = message;
        this.player = player;
    }
    @Override
    public void execute() {
        try {
            DSL.using(LINKER.getDataSource().getConnection())
                    .insertInto(DSL.table("discord"), DSL.field("discord_user_id"), DSL.field("minecraft_user_uuid"), DSL.field("link_date"))
                    .values(Long.parseLong(message.getAuthor().getId()), player.getUniqueId().toString(), DSL.currentTimestamp())
                    .execute();
            output = "OK";
        }catch (SQLException e) {
            output = "DB_ERROR";
        }catch (DataAccessException e){
            output = "DB_UNIQUE";
        }
    }

    @Override
    public String output() {
        return output;
    }
}
