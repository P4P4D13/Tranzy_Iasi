package IasiTranzit.Tranzy_Iasi;

import java.net.http.*;
import java.net.URI;
import java.io.IOException;

public class TranzyAPIClient {
    private static final String API_URL = "https://api.tranzy.ai/..."; // Replace with actual endpoint
    private static final String API_KEY = "dPybUGYHxZd1potOXjSxssAcDI9J1JOw7262Zzi8"; // If required

    public static String getBusData() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(API_URL))
            .header("Authorization", "Bearer " + API_KEY) // If needed
            .GET()
            .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        String data = getBusData();
        System.out.println(data);
    }
}
