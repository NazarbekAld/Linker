package me.nazarxexe.free.linker.platform.discord.operations;

import me.nazarxexe.free.linker.LINKER;
import org.bukkit.entity.Player;
import org.jooq.impl.DSL;

import java.sql.SQLException;

public class IgnoreModeOperation implements Operation<String[]>{

    private final Player player;
    private String[] output;

    public IgnoreModeOperation(Player player) {
        this.player = player;
    }

    @Override
    public void execute() {

        if (isIgnored(player)) {
            try {
                DSL.using(LINKER.getDataSource().getConnection())
                        .deleteFrom(DSL.table("discord_ignore_list"))
                        .where(DSL.field("minecraft_user_uuid").eq(player.getUniqueId().toString()))
                        .execute();
                output = new String[] { "OK", "DISABLE" };
            } catch (SQLException e) {
                e.printStackTrace();
                output = new String[] {"ERROR", "DB_ERROR"};
            }
            return;
        }
        try {
            DSL.using(LINKER.getDataSource().getConnection())
                    .insertInto(DSL.table("discord_ignore_list"), DSL.field("minecraft_user_uuid"))
                    .values(player.getUniqueId().toString())
                    .execute();
            output = new String[] { "OK", "ENABLE" };
        } catch (SQLException e) {
            e.printStackTrace();
            output = new String[] { "ERROR", "DB_ERROR" };
        }

    }

    @Override
    public String[] output() {
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
