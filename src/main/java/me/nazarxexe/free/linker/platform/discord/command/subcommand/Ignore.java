package me.nazarxexe.free.linker.platform.discord.command.subcommand;

import me.nazarxexe.free.linker.LINKER;
import me.nazarxexe.free.linker.platform.discord.command.DiscordSubCMD;
import me.nazarxexe.free.linker.platform.discord.operations.IgnoreModeOperation;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jooq.impl.DSL;

import java.sql.SQLException;

public class Ignore implements DiscordSubCMD {

    private final Plugin plugin;

    public Ignore(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public String name() {
        return "ignore";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if (!(sender instanceof Player)) return;

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {

            Player player = (Player) sender;
            IgnoreModeOperation ignoreModeOperation = new IgnoreModeOperation(player);

            ignoreModeOperation.execute();

            if (!(ignoreModeOperation.output()[0].equals("OK"))) {
                sender.sendMessage( ChatColor.RED + "Something went wrong!");
                return;
            }

            if (ignoreModeOperation.output()[1].equals("ENABLE")){
                sender.sendMessage(ChatColor.GREEN + "Ignore mode enabled!");
                return;
            }
            if (ignoreModeOperation.output()[1].equals("DISABLE")){
                sender.sendMessage(ChatColor.RED + "Ignore mode disabled!");
                return;
            }

        });

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
