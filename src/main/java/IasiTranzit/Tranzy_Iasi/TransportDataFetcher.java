package IasiTranzit.Tranzy_Iasi;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TransportDataFetcher {
    private static final String API_URL_ROUTES = "https://api.tranzy.ai/v1/opendata/routes";
    private static final String API_URL_STOP_TIMES = "https://api.tranzy.ai/v1/opendata/stop_times";
    private static final String API_URL_STOPS = "https://api.tranzy.ai/v1/opendata/stops";
    private static final String API_URL_TRIPS = "https://api.tranzy.ai/v1/opendata/trips";
    private static final String API_URL_VEHICLES = "https://api.tranzy.ai/v1/opendata/vehicles";
    
    private static final String API_KEY = "7DgYhGzTQc5Nn8FfFeuFmhCAWcbadYQEShUjwu3e"; // Use your actual key
    private static final String AGENCY_ID = "1";
    private static final String OUTPUT_FOLDER = "src/main/resources/"; // Correct path for resources folder

    public static void fetchAllApiData() {
        try {
            System.out.println("Downloading all data from Tranzy API...");
            System.out.println("This will completely overwrite existing JSON files with latest data.");
            
            // Fetch and overwrite each file with latest data
            String routesData = fetchApiData(API_URL_ROUTES, true);
            saveToFileWithOverwrite(routesData, "date_rute.json");
            
            String stopTimesData = fetchApiData(API_URL_STOP_TIMES, true);
            saveToFileWithOverwrite(stopTimesData, "date_stops_times.json");
            
            String stopsData = fetchApiData(API_URL_STOPS, true);
            saveToFileWithOverwrite(stopsData, "date_stops.json");
            
            String tripsData = fetchApiData(API_URL_TRIPS, true);
            saveToFileWithOverwrite(tripsData, "date_trips.json");
            
            String vehiclesData = fetchApiData(API_URL_VEHICLES, true);
            saveToFileWithOverwrite(vehiclesData, "date_vehicule.json");
            
            System.out.println("All data downloaded and files completely overwritten with latest results!");
        } catch (Exception e) {
            System.err.println("Error downloading data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Incarca date statice, rute si trasee, folosind SwingWorker Actualizeaza
     * interfata in functie de succesul sau esecul operatiei
     */
    private static String fetchApiData(String apiUrl, boolean needsAgencyId) throws IOException {
        System.out.println("Fetching latest data from: " + apiUrl);
        URL url = new URL(apiUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/json");
        conn.setRequestProperty("X-API-KEY", API_KEY);
        if (needsAgencyId) {
            conn.setRequestProperty("X-Agency-Id", AGENCY_ID);
        }
        conn.setRequestProperty("User-Agent", "Mozilla/5.0");

        if (conn.getResponseCode() != 200) {
            throw new IOException("HTTP error code: " + conn.getResponseCode() + " for " + apiUrl);
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line);
            }
            System.out.println("Successfully fetched " + response.length() + " characters from API");
            return response.toString();
        } finally {
            conn.disconnect();
        }
    }
    
    /**
     * Saves JSON content to file, completely overwriting any existing file.
     * Uses modern Java NIO for more reliable file operations.
     */
    private static void saveToFileWithOverwrite(String jsonContent, String filename) throws IOException, JSONException {
        // Ensure output directory exists
        Path outputDir = Paths.get(OUTPUT_FOLDER);
        if (!Files.exists(outputDir)) {
            Files.createDirectories(outputDir);
        }
        
        Path filePath = outputDir.resolve(filename);
        
        // Delete existing file if it exists to ensure complete overwrite
        if (Files.exists(filePath)) {
            Files.delete(filePath);
            System.out.println("Deleted existing file: " + filename);
        }
        
        try {
            // Parse and format JSON for better readability
            Object json = new org.json.JSONTokener(jsonContent).nextValue();
            String formattedJson;
            
            if (json instanceof JSONArray) {
                formattedJson = ((JSONArray) json).toString(4); // Pretty print with 4-space indent
            } else {
                formattedJson = json.toString();
            }
            
            // Write new content to file (CREATE_NEW ensures it's a fresh file)
            Files.write(filePath, formattedJson.getBytes(), 
                       StandardOpenOption.CREATE, 
                       StandardOpenOption.WRITE, 
                       StandardOpenOption.TRUNCATE_EXISTING);
            
            System.out.println("Successfully overwrote " + filename + " with latest data (" + 
                             formattedJson.length() + " characters)");
            
        } catch (JSONException e) {
            System.err.println("Invalid JSON received for " + filename + ": " + e.getMessage());
            // Save raw content for debugging
            Files.write(filePath, jsonContent.getBytes(), 
                       StandardOpenOption.CREATE, 
                       StandardOpenOption.WRITE, 
                       StandardOpenOption.TRUNCATE_EXISTING);
            System.out.println("Saved raw content to " + filename + " for debugging");
            throw e;
        }
    }
    
    /**
     * Alternative method using the original approach but with explicit overwrite confirmation
     */
    private static void saveToFile(String jsonContent, String filename) throws IOException, JSONException {
        File directory = new File(OUTPUT_FOLDER);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        
        File targetFile = new File(OUTPUT_FOLDER + filename);
        if (targetFile.exists()) {
            System.out.println("Overwriting existing file: " + filename);
        }
        
        // FileWriter with default constructor truncates the file (overwrites completely)
        try (FileWriter file = new FileWriter(targetFile, false)) { // false = overwrite mode
            // Assume the API might return a single object or an array
            Object json = new org.json.JSONTokener(jsonContent).nextValue();
            if (json instanceof JSONArray) {
                file.write(((JSONArray) json).toString(4));
            } else {
                file.write(json.toString());
            }
            System.out.println("Data completely overwritten in: " + filename);
        }
    }

    // --- PART 2: Loading Data From Files for the GUI ---
    
    /**
     * Loads all static data needed by the application from local files.
     * This is the main method the GUI will call.
     */
    public StaticData loadAllStaticData() throws IOException, JSONException {
        Map<String, Route> routes = loadRoutes();
        Map<String, Trip> trips = loadTrips();
        Map<String, Stop> stops = loadStops();
        List<StopTime> stopTimes = loadStopTimes();
        return new StaticData(routes, trips, stops, stopTimes);
    }

    /**
     * Loads vehicle data from the local file.
     */
    public List<Vehicle> loadVehicles() throws IOException, JSONException {
        String vehiclesJson = readFromResources("date_vehicule.json");
        JSONArray vehiclesArray = new JSONArray(vehiclesJson);
        List<Vehicle> vehicleList = new ArrayList<>();
        for (int i = 0; i < vehiclesArray.length(); i++) {
            vehicleList.add(Vehicle.fromJson(vehiclesArray.getJSONObject(i)));
        }
        return vehicleList;
    }
    
    /**
     * fct pt incarcarea rutelor,clarifica ce se intampla in doInBackground
     * 
     * @return o harta a ID-urilor de ruta catre obiectele Route
     * @throws IOException   daca apare o eroare de retea, conexiunea nu merge sau
     *                       serverul nu raspunde
     * @throws JSONException daca datele JSON nu sunt valide(alt format)
     */
    private Map<String, Route> loadRoutes() throws IOException, JSONException {
        String routesJson = readFromResources("date_rute.json");
        JSONArray routesArray = new JSONArray(routesJson);
        Map<String, Route> tempRoutesMap = new HashMap<>();
        for (int i = 0; i < routesArray.length(); i++) {
            Route route = Route.fromJson(routesArray.getJSONObject(i));
            tempRoutesMap.put(route.id, route);
        }
        return tempRoutesMap;
    }
    
    /**
     * Incarca datele despre trasee de la un anumit endpoint fct pt incarcarea
     * traseelor,luat codul din functia doInBackground
     * 
     * @return o harta a ID-urilor de traseu catre obiectele Trip
     * @throws IOException   aca apare o eroare de retea, conexiunea nu merge sau
     *                       serverul nu raspunde
     * @throws JSONException daca datele JSON nu sunt valide(alt format)
     */
    private Map<String, Trip> loadTrips() throws IOException, JSONException {
        String tripsJson = readFromResources("date_trips.json");
        JSONArray tripsArray = new JSONArray(tripsJson);
        Map<String, Trip> tempTripsMap = new HashMap<>();
        for (int i = 0; i < tripsArray.length(); i++) {
            Trip trip = Trip.fromJson(tripsArray.getJSONObject(i));
            tempTripsMap.put(trip.id, trip);
        }
        return tempTripsMap;
    }

    private Map<String, Stop> loadStops() throws IOException, JSONException {
        String stopsJson = readFromResources("date_stops.json");
        JSONArray stopsArray = new JSONArray(stopsJson);
        Map<String, Stop> tempStopsMap = new HashMap<>();
        for (int i = 0; i < stopsArray.length(); i++) {
            Stop stop = Stop.fromJson(stopsArray.getJSONObject(i));
            tempStopsMap.put(stop.id, stop);
        }
        return tempStopsMap;
    }

    private List<StopTime> loadStopTimes() throws IOException, JSONException {
        String stopTimesJson = readFromResources("date_stops_times.json");
        JSONArray stopTimesArray = new JSONArray(stopTimesJson);
        List<StopTime> tempList = new ArrayList<>();
        for (int i = 0; i < stopTimesArray.length(); i++) {
            StopTime st = StopTime.fromJson(stopTimesArray.getJSONObject(i));
            tempList.add(st);
        }
        return tempList;
    }

    // Renamed your 'fetchData' to be more specific about its source.
    private String readFromResources(String fileName) throws IOException {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(fileName);
        if (inputStream == null) {
            throw new IOException("Resource file not found: " + fileName);
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
    
    /**
     * Method to refresh all data - fetches latest from API and completely overwrites local files
     */
    public static void refreshAllData() {
        System.out.println("=== REFRESHING ALL DATA ===");
        System.out.println("Fetching latest data from Tranzy API and completely overwriting local files...");
        fetchAllApiData();
        System.out.println("=== DATA REFRESH COMPLETE ===");
    }
}