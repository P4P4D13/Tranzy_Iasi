package IasiTranzit.Tranzy_Iasi;

import java.io.BufferedReader;
// import java.io.FileWriter; // Removed
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
// Keep org.json imports if you need them elsewhere, but they aren't used *in this class* anymore
// import org.json.*;

public class Date_vehicule {
    // Consider moving these to a config file or constants class
    private static final String API_URL = "https://api.tranzy.ai/v1/opendata/vehicles";
    private static final String API_KEY = "7DgYhGzTQc5Nn8FfFeuFmhCAWcbadYQEShUjwu3e"; // Replace with your key
    private static final String AGENCY_ID = "1"; // ID for CTP Iasi

    /**
     * Fetches the latest vehicle data from the Tranzy API.
     * @return JSON String containing vehicle data.
     * @throws Exception If there's an HTTP error or network issue.
     */
    public static String getTransportData() throws Exception {
        URL url = new URL(API_URL);
        HttpURLConnection conn = null;
        StringBuilder response = new StringBuilder();

        try {
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("X-API-KEY", API_KEY);
            conn.setRequestProperty("X-Agency-Id", AGENCY_ID);
            // It's good practice to set timeouts
            conn.setConnectTimeout(5000); // 5 seconds
            conn.setReadTimeout(10000);  // 10 seconds
            // Adding a User-Agent can sometimes help avoid being blocked
            conn.setRequestProperty("User-Agent", "TranzyIasiApp/1.0 (Java; Swing)");


            int responseCode = conn.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) { // Check for 200 OK
                 // Read error stream for more details if possible
                 String errorDetails = "";
                 try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getErrorStream()))) {
                     String line;
                     while ((line = br.readLine()) != null) {
                         errorDetails += line;
                     }
                 } catch (IOException e) {
                     // Ignore if error stream can't be read
                 }
                 throw new RuntimeException("HTTP error code: " + responseCode + ". Details: " + errorDetails);
            }

            // Read the response
            try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                String output;
                while ((output = br.readLine()) != null) {
                    response.append(output);
                }
            }

        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        return response.toString();
    }

    // Removed writeToFile method
    // Removed main method
}