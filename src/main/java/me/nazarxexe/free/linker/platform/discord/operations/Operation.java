package me.nazarxexe.free.linker.platform.discord.operations;

public interface Operation<T> {

    void execute();

    T output();

}
