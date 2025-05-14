package IasiTranzit.Tranzy_Iasi;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONArray;

public class TransportDataFetcher {
    // API URLs
    private static final String API_URL_AGENCY = "https://api.tranzy.ai/v1/opendata/agency";
    private static final String API_URL_ROUTES = "https://api.tranzy.ai/v1/opendata/routes";
    private static final String API_URL_STOP_TIMES = "https://api.tranzy.ai/v1/opendata/stop_times";
    private static final String API_URL_STOPS = "https://api.tranzy.ai/v1/opendata/stops";
    private static final String API_URL_TRIPS = "https://api.tranzy.ai/v1/opendata/trips";
    private static final String API_URL_VEHICLES = "https://api.tranzy.ai/v1/opendata/vehicles";
    private static final String API_URL_SHAPES = "https://api.tranzy.ai/v1/opendata/shapes";
    
    // Authentication and identification
    private static final String API_KEY = "7DgYhGzTQc5Nn8FfFeuFmhCAWcbadYQEShUjwu3e";
    private static final String AGENCY_ID = "1";
    
    // Folder to save data
    private static final String OUTPUT_FOLDER = "resources/";
    
    /**
     * Extrage toate datele de transport într-o singură metodă
     */
    public static void fetchAllData() {
        try {
            // Descarcă și salvează datele despre agenție
            String agencyData = fetchApiData(API_URL_AGENCY, false);
            saveToFile(agencyData, "date_agentie.json");
            
            // Descarcă și salvează datele despre rute
            String routesData = fetchApiData(API_URL_ROUTES, true);
            saveToFile(routesData, "date_rute.json");
            
            // Descarcă și salvează datele despre opriri programate
            String stopTimesData = fetchApiData(API_URL_STOP_TIMES, true);
            saveToFile(stopTimesData, "date_stops_times.json");
            
            // Descarcă și salvează datele despre stații
            String stopsData = fetchApiData(API_URL_STOPS, true);
            saveToFile(stopsData, "date_stops.json");
            
            // Descarcă și salvează datele despre călătorii
            String tripsData = fetchApiData(API_URL_TRIPS, true);
            saveToFile(tripsData, "date_trips.json");
            
            // Descarcă și salvează datele despre vehicule
            String vehiclesData = fetchApiData(API_URL_VEHICLES, true);
            saveToFile(vehiclesData, "date_vehicule.json");
            
            // Descarcă și salvează datele despre forme
            String shapesData = fetchApiData(API_URL_SHAPES, true);
            saveToFile(shapesData, "date_shapes.json");
            
            System.out.println("Toate datele au fost descărcate cu succes!");
            
        } catch (Exception e) {
            System.err.println("Eroare la descărcarea datelor: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Metoda pentru apelul API și extragerea datelor
     */
    private static String fetchApiData(String apiUrl, boolean needsAgencyId) throws IOException {
        URL url = new URL(apiUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/json");
        conn.setRequestProperty("X-API-KEY", API_KEY);
        
        if (needsAgencyId) {
            conn.setRequestProperty("X-Agency-Id", AGENCY_ID);
        }
        
        conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)");
        
        if (conn.getResponseCode() != 200) {
            throw new IOException("HTTP error code: " + conn.getResponseCode() + " pentru " + apiUrl);
        }
        
        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            response.append(line);
        }
        conn.disconnect();
        return response.toString();
    }
    
    /**
     * Salvează conținutul JSON într-un fișier
     */
    private static void saveToFile(String jsonContent, String filename) throws IOException {
        // Crează directorul de ieșire dacă nu există
        java.io.File directory = new java.io.File(OUTPUT_FOLDER);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        
        try (FileWriter file = new FileWriter(OUTPUT_FOLDER + filename)) {
            JSONArray json = new JSONArray(jsonContent);
            file.write(json.toString(4));
            System.out.println("Datele au fost scrise în fișierul: " + filename);
        }
    }
    //Functia aceasta preaia din fisierele locale date pentru a intocmi raportul
    public String fetchData(String fileName) throws IOException {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(fileName);
        if (inputStream == null) {
            throw new IOException("Fisierul nu a fost gasit: " + fileName);
        }
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }
        }
        return content.toString();
    }    
    
    
}