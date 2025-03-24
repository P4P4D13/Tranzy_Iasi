package IasiTranzit.Tranzy_Iasi;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class DateTransport {
    private static final String API_URL = "https://api.tranzy.ai/v1/opendata/vehicles"; // URL-ul API-ului Tranzy
    private static final String API_KEY = "oQh5WQTQi6aYXGNe9tl0II0AtMvAdaYVrYd27hvO"; // Înlocuiește cu cheia API obținută

    public static String getTransportData() throws Exception {
        URL url = new URL(API_URL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/json");
        conn.setRequestProperty("Authorization", "Bearer " + API_KEY);

        if (conn.getResponseCode() != 200) {
            throw new RuntimeException("HTTP error code : " + conn.getResponseCode());
        }

        BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
        StringBuilder response = new StringBuilder();
        String output;
        while ((output = br.readLine()) != null) {
            response.append(output);
        }
        conn.disconnect();
        return response.toString();
    }
    
    public static void main(String[] args) {
        try {
            String jsonResponse = getTransportData();
            System.out.println("Response from API:\n" + jsonResponse);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
   
}