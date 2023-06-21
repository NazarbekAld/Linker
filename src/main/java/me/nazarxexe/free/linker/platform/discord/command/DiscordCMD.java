package me.nazarxexe.free.linker.platform.discord.command;

import lombok.Getter;
import me.nazarxexe.free.linker.platform.discord.Discord;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class DiscordCMD implements CommandExecutor, TabCompleter {

    @Getter final List<DiscordSubCMD> discordSub;
    @Getter final HashMap<UUID, Message> incomingRequest;

    @Getter final Plugin plugin;

    public DiscordCMD(Plugin plugin) {
        this.plugin = plugin;

        incomingRequest = new HashMap<>();
        this.discordSub = new ArrayList<>();


    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        discordSub.forEach((subcommand) -> {
            if (!(subcommand.name().equals(args[0]))) return;
            subcommand.execute(sender, args);
        });

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return discordSub.stream().map(DiscordSubCMD::name).collect(Collectors.toList());
    }
}
