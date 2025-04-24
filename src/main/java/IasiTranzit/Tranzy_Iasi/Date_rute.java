package IasiTranzit.Tranzy_Iasi;

import java.io.BufferedReader;
//import java.io.FileWriter; // Removed
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
// Keep org.json imports if you need them elsewhere
// import org.json.*;


public class Date_rute {
    // Consider moving these to a config file or constants class
    private static final String API_URL = "https://api.tranzy.ai/v1/opendata/routes";
    private static final String API_KEY = "7DgYhGzTQc5Nn8FfFeuFmhCAWcbadYQEShUjwu3e"; // Replace with your key
    private static final String AGENCY_ID = "1"; // ID for CTP Iasi

    /**
     * Fetches the route data from the Tranzy API.
     * (Currently unused in the main tracking flow but kept for potential future use)
     * @return JSON String containing route data.
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
            conn.setConnectTimeout(5000); // 5 seconds
            conn.setReadTimeout(10000);  // 10 seconds
            conn.setRequestProperty("User-Agent", "TranzyIasiApp/1.0 (Java; Swing)");

            int responseCode = conn.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                 String errorDetails = "";
                 try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getErrorStream()))) {
                     String line;
                     while ((line = br.readLine()) != null) {
                         errorDetails += line;
                     }
                 } catch (IOException e) { /* Ignore */ }
                 throw new RuntimeException("HTTP error code : " + responseCode + ". Details: " + errorDetails);
            }

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