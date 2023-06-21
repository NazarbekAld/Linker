package me.nazarxexe.free.linker.network.check;

import com.google.gson.Gson;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class GenerateQuote implements NetworkChecking{
    @Override
    public NetworkCheckingResponse check() {
        try {

            // Build the request -
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("https://programming-quotesapi.vercel.app/api/random"))
                    .GET()
                    .timeout(Duration.ofMinutes(1))
                    .build();
            // Send the request
            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Create gson object
            Gson gson = new Gson();
            // Parse the quote
            return gson.fromJson(response.body(), Quote.class);
        } catch (Exception  e) {
            return null;
        }
    }
}
