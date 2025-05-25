package IasiTranzit.Tranzy_Iasi;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
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
            saveToFile(fetchApiData(API_URL_ROUTES, true), "date_rute.json");
            saveToFile(fetchApiData(API_URL_STOP_TIMES, true), "date_stops_times.json");
            saveToFile(fetchApiData(API_URL_STOPS, true), "date_stops.json");
            saveToFile(fetchApiData(API_URL_TRIPS, true), "date_trips.json");
            saveToFile(fetchApiData(API_URL_VEHICLES, true), "date_vehicule.json");
            System.out.println("All data downloaded successfully!");
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
            return response.toString();
        } finally {
            conn.disconnect();
        }
    }
    
    private static void saveToFile(String jsonContent, String filename) throws IOException, JSONException {
        java.io.File directory = new java.io.File(OUTPUT_FOLDER);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        try (FileWriter file = new FileWriter(OUTPUT_FOLDER + filename)) {
            // Assume the API might return a single object or an array
            Object json = new org.json.JSONTokener(jsonContent).nextValue();
            if (json instanceof JSONArray) {
                 file.write(((JSONArray) json).toString(4));
            } else {
                 file.write(json.toString());
            }
            System.out.println("Data written to: " + filename);
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
}