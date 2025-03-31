package IasiTranzit.Tranzy_Iasi;

<<<<<<< HEAD
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class App {
private static final String API_URL = "https://api.tranzy.ai/v1/opendata/vehicles";
private static final String API_KEY = "oQh5WQTQi6aYXGNe9tl0II0AtMvAdaYVrYd27hvOI"; // Înlocuiește cu cheia API
private static final String API_ID = "1";

public static String getTransportData() throws Exception {
URL url = new URL(API_URL);
HttpURLConnection conn = (HttpURLConnection) url.openConnection();
conn.setRequestMethod("GET");
conn.setRequestProperty("Accept", "application/json");
conn.setRequestProperty("Authorization", "Bearer " + API_KEY);
conn.setRequestProperty("X-API-ID", API_ID);
conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)"); // Adăugăm User-Agent

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
=======
/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) {
        System.out.println("Hello World!");
        System.out.println("AlexL");
    }
}
>>>>>>> 08a0bc43436f62e5221f6de523d684b12d03a153
