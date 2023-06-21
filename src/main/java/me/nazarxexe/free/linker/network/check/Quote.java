package me.nazarxexe.free.linker.network.check;

import lombok.Data;

@Data
public class Quote implements NetworkCheckingResponse {
    String author;
    String quote;

    @Override
    public String[] response() {
        return new String[] {
                "", quote, "\t\t - " + author, ""
        };
    }
}
