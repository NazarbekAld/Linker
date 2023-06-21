package me.nazarxexe.free.linker.platform.discord.command;

import org.bukkit.command.CommandSender;

public interface DiscordSubCMD {

    String name();

    void execute(CommandSender sender, String[] args);

}
