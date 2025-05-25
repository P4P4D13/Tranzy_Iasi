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

/**
 * Clasa responsabila pentru descarcarea si incarcarea datelor statice si dinamice
 * din API-ul Tranzy si pentru salvarea acestora local.
 */
public class TransportDataFetcher {
    private static final String API_URL_ROUTES = "https://api.tranzy.ai/v1/opendata/routes";
    private static final String API_URL_STOP_TIMES = "https://api.tranzy.ai/v1/opendata/stop_times";
    private static final String API_URL_STOPS = "https://api.tranzy.ai/v1/opendata/stops";
    private static final String API_URL_TRIPS = "https://api.tranzy.ai/v1/opendata/trips";
    private static final String API_URL_VEHICLES = "https://api.tranzy.ai/v1/opendata/vehicles";
    
    private static final String API_KEY = "7DgYhGzTQc5Nn8FfFeuFmhCAWcbadYQEShUjwu3e"; // Use your actual key
    private static final String AGENCY_ID = "1";
    private static final String OUTPUT_FOLDER = "src/main/resources/"; // Correct path for resources folder

    /**
	 * Constructor implicit, nu este folosit
	 */
	public TransportDataFetcher() {
	    // constructor implicit gol
	}
	
    /**
     * Descarca toate seturile de date disponibile de la API si suprascrie complet
     * fisierele locale cu datele actualizate.
     */
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
     * Incarca date statice, rute si trasee, folosind SwingWorker 
     * Actualizeaza interfata in functie de succesul sau esecul operatiei
     * 
     * @param apiUrl URL-ul API-ului de unde se descarca datele.
     * @param needsAgencyId Daca e true, adauga header-ul X-Agency-Id cu valoarea corespunzatoare.
     * @return Raspunsul de la API ca String JSON.
     * @throws IOException In caz de eroare de retea sau cod HTTP diferit de 200.
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
     * Salveaza continutul JSON intr-un fisier, suprascriind complet orice fisier existent.
     * Creeaza directorul de iesire daca nu exista.
     * Formateaza JSON-ul pentru o mai buna lizibilitate inainte de salvare.
     * 
     * @param jsonContent continutul JSON ca String ce trebuie salvat in fisier
     * @param filename numele fisierului in care se va salva continutul JSON
     * @throws IOException in cazul in care apar erori la operatiile de citire/scriere fisier
     * @throws JSONException in cazul in care continutul JSON este invalid si nu poate fi procesat
     */
    private static void saveToFileWithOverwrite(String jsonContent, String filename) throws IOException, JSONException {
    	// Asigurare ca directorul de iesire exista
        Path outputDir = Paths.get(OUTPUT_FOLDER);
        if (!Files.exists(outputDir)) {
            Files.createDirectories(outputDir);
        }
        
        Path filePath = outputDir.resolve(filename);
        
        // Sterge fisierul existent daca exista pentru a asigura suprascriere completa
        if (Files.exists(filePath)) {
            Files.delete(filePath);
            System.out.println("Deleted existing file: " + filename);
        }
        
        try {
        	// Parcurge si formateaza JSON-ul pentru o mai buna lizibilitate
            Object json = new org.json.JSONTokener(jsonContent).nextValue();
            String formattedJson;
            
            if (json instanceof JSONArray) {
                formattedJson = ((JSONArray) json).toString(4); // Afisare cu indentare de 4 spatii
            } else {
                formattedJson = json.toString();
            }
            
            // Scrie continutul nou in fisier (CREATE_NEW asigura ca e un fisier nou)
            Files.write(filePath, formattedJson.getBytes(), 
                       StandardOpenOption.CREATE, 
                       StandardOpenOption.WRITE, 
                       StandardOpenOption.TRUNCATE_EXISTING);
            
            System.out.println("Successfully overwrote " + filename + " with latest data (" + 
                             formattedJson.length() + " characters)");
            
        } catch (JSONException e) {
            System.err.println("Invalid JSON received for " + filename + ": " + e.getMessage());
         // Salveaza continutul brut pentru debugging
            Files.write(filePath, jsonContent.getBytes(), 
                       StandardOpenOption.CREATE, 
                       StandardOpenOption.WRITE, 
                       StandardOpenOption.TRUNCATE_EXISTING);
            System.out.println("Saved raw content to " + filename + " for debugging");
            throw e;
        }
    }
    
    /**
     * Salveaza continutul JSON intr-un fisier, suprascriind fisierul existent.
     *
     * @param jsonContent continutul JSON de salvat
     * @param filename numele fisierului tinta
     * @throws IOException daca apare o eroare la scriere
     * @throws JSONException daca JSON-ul este invalid
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
        
        // FileWriter cu constructorul implicit trunchiaza fisierul (suprascrie complet)
        try (FileWriter file = new FileWriter(targetFile, false)) { // false = modul de suprascriere
        	// Se presupune ca API-ul poate returna un singur obiect sau un array
            Object json = new org.json.JSONTokener(jsonContent).nextValue();
            if (json instanceof JSONArray) {
                file.write(((JSONArray) json).toString(4));
            } else {
                file.write(json.toString());
            }
            System.out.println("Data completely overwritten in: " + filename);
        }
    }

    /**
     * Incarca toate datele statice necesare aplicatiei din fisiere locale.
     * Metoda principala apelata de GUI.
     * 
     * @return obiect StaticData cu toate datele incarcate
     * @throws IOException in caz de eroare la citirea fisierelor
     * @throws JSONException daca datele JSON sunt invalide
     */
    public StaticData loadAllStaticData() throws IOException, JSONException {
        Map<String, Route> routes = loadRoutes();
        Map<String, Trip> trips = loadTrips();
        Map<String, Stop> stops = loadStops();
        List<StopTime> stopTimes = loadStopTimes();
        return new StaticData(routes, trips, stops, stopTimes);
    }

    /**
     * Incarca datele despre vehicule din fisierul local.
     * @return lista cu obiecte Vehicle
     * @throws IOException la eroare de citire fisier
     * @throws JSONException la date JSON invalide
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
     * Incarca rutele din fisierul local, clarifica ce se intampla in metoda doInBackground.
     * 
     * @return o harta a ID-urilor de ruta catre obiectele Route
     * @throws IOException daca apare o eroare de retea, conexiunea nu merge sau
     * serverul nu raspunde
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
     * Incarca datele despre trasee de la un anumit endpoint, cod luat din functia doInBackground
     * 
     * @return o harta a ID-urilor de traseu catre obiectele Trip
     * @throws IOException daca apare o eroare de retea, conexiunea nu merge sau
     * serverul nu raspunde
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

    /**
     * Incarca statii din fisierul local JSON.
     * 
     * @return o harta a ID-urilor de statie catre obiectele Stop
     * @throws IOException daca apare o eroare de retea, conexiunea nu merge sau
     * serverul nu raspunde
     * @throws JSONException daca datele JSON nu sunt valide(alt format)
     */
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

    /**
     * Incarca timpii de oprire din fisierul local JSON.
     * 
     * @return lista cu obiecte StopTime
     * @throws IOException daca apare o eroare la citirea fisierului
     * @throws JSONException daca datele JSON nu sunt valide
     */
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

    /**
     * Citeste continutul unui fisier din resurse
     * 
     * @param fileName numele fisierului din resurse
     * @return continutul fisierului ca string
     * @throws IOException daca fisierul nu exista sau apare o eroare
     */
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
     * Metoda pentru reimprospatarea tuturor datelor - preia ultimele informatii de la API
     * si suprascrie complet fisierele locale.
     */
    public static void refreshAllData() {
        System.out.println("=== REFRESHING ALL DATA ===");
        System.out.println("Fetching latest data from Tranzy API and completely overwriting local files...");
        fetchAllApiData();
        System.out.println("=== DATA REFRESH COMPLETE ===");
    }
}