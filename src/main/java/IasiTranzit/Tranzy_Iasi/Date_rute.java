package IasiTranzit.Tranzy_Iasi;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.*;


public class Date_rute {
private static final String API_URL = "https://api.tranzy.ai/v1/opendata/routes";
private static final String API_KEY = "7DgYhGzTQc5Nn8FfFeuFmhCAWcbadYQEShUjwu3e"; // Inlocuieste cu cheia API
private static final String AGENCY_ID = "1"; // ID-ul agentiei

public static String getTransportData() throws Exception {	
URL url = new URL(API_URL);
HttpURLConnection conn = (HttpURLConnection) url.openConnection();
conn.setRequestMethod("GET");
conn.setRequestProperty("Accept", "application/json");
conn.setRequestProperty("X-API-KEY", API_KEY); // Modificat corect
conn.setRequestProperty("X-Agency-Id", AGENCY_ID); // Modificat corect
conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)"); // Evitam blocarea cererii

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

public static void writeToFile(String jsonContent, String filename) {
    try (FileWriter file = new FileWriter(filename)) {
    	JSONArray json=new JSONArray(jsonContent);
        file.write(json.toString(4)); 
        System.out.println("Datele au fost scrise în fișierul: " + filename);
    } catch (IOException e) {
        e.printStackTrace();
    }
}

public static void main(String[] args) {
try {
String jsonResponse = getTransportData();
System.out.println("Response from API:\n" + jsonResponse);
writeToFile(jsonResponse, "resources/date_rute.json"); 
} catch (Exception e) {
e.printStackTrace();
}
}
}