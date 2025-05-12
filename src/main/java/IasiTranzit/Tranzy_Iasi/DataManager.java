	package IasiTranzit.Tranzy_Iasi;
	
	import org.json.JSONArray;
	import org.json.JSONException;
	import org.json.JSONObject;
	
	import java.io.BufferedReader;
	import java.io.IOException;
	import java.io.InputStream;
	import java.io.InputStreamReader;
	import java.util.ArrayList;
	import java.util.HashMap;
	import java.util.List;
	import java.util.Map;
	
	/**
	 * Clasa {@code DataManager} se ocupă cu obținerea și procesarea datelor pentru aplicația Tranzy Iasi.
	 * Oferă metode pentru încărcarea rutelor, traseelor, stațiilor, și vehiculelor din fișiere locale.
	 */
	public class DataManager {
	    private Map<String, Route> routesMap = new HashMap<>();
	    private Map<String, Trip> tripsMap = new HashMap<>();
	    private Map<String, Stop> stopsMap = new HashMap<>();
	    private List<StopTime> stopTimesList = new ArrayList<>();
	    
	    /**
	     * Constructor implicit pentru DataManager.
	     */
	    public DataManager() {
	        // Constructorul implicit
	    }
	    
	    /**
	     * Obține conținutul unui fișier din resursele aplicației
	     * 
	     * @param fileName numele fișierului care trebuie citit
	     * @return conținutul fișierului ca string
	     * @throws IOException dacă fișierul nu poate fi citit
	     */
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
	    
	    /**
	     * Încarcă informațiile despre rute din fișierul JSON
	     * 
	     * @return o hartă cu ID-urile de rută și obiectele Route corespunzătoare
	     * @throws IOException dacă apare o eroare la citirea fișierului
	     * @throws JSONException dacă formatul JSON este invalid
	     */
	    public Map<String, Route> loadRoutes() throws IOException, JSONException {
	        String routesJson = fetchData("date_rute.json");
	        JSONArray routesArray = new JSONArray(routesJson);
	        Map<String, Route> tempRoutesMap = new HashMap<>();
	        for (int i = 0; i < routesArray.length(); i++) {
	            Route route = Route.fromJson(routesArray.getJSONObject(i));
	            tempRoutesMap.put(route.id, route);
	        }
	        this.routesMap = tempRoutesMap;
	        return tempRoutesMap;
	    }
	    
	    /**
	     * Încarcă informațiile despre trasee din fișierul JSON
	     * 
	     * @return o hartă cu ID-urile de traseu și obiectele Trip corespunzătoare
	     * @throws IOException dacă apare o eroare la citirea fișierului
	     * @throws JSONException dacă formatul JSON este invalid
	     */
	    public Map<String, Trip> loadTrips() throws IOException, JSONException {
	        String tripsJson = fetchData("date_trips.json");
	        JSONArray tripsArray = new JSONArray(tripsJson);
	        Map<String, Trip> tempTripsMap = new HashMap<>();
	        for (int i = 0; i < tripsArray.length(); i++) {
	            Trip trip = Trip.fromJson(tripsArray.getJSONObject(i));
	            tempTripsMap.put(trip.id, trip);
	        }
	        this.tripsMap = tempTripsMap;
	        return tempTripsMap;
	    }
	    
	    /**
	     * Încarcă informațiile despre stații din fișierul JSON
	     * 
	     * @return o hartă cu ID-urile stațiilor și obiectele Stop corespunzătoare
	     * @throws IOException dacă apare o eroare la citirea fișierului
	     * @throws JSONException dacă formatul JSON este invalid
	     */
	    public Map<String, Stop> loadStops() throws IOException, JSONException {
	        String stopsJson = fetchData("date_stops.json");
	        JSONArray stopsArray = new JSONArray(stopsJson);
	        Map<String, Stop> tempStopsMap = new HashMap<>();
	        for (int i = 0; i < stopsArray.length(); i++) {
	            Stop stop = Stop.fromJson(stopsArray.getJSONObject(i));
	            tempStopsMap.put(stop.id, stop);
	        }
	        this.stopsMap = tempStopsMap;
	        return tempStopsMap;
	    }
	    
	    /**
	     * Încarcă informațiile despre orarele stațiilor din fișierul JSON
	     * 
	     * @return o listă cu obiectele StopTime
	     * @throws IOException dacă apare o eroare la citirea fișierului
	     * @throws JSONException dacă formatul JSON este invalid
	     */
	    public List<StopTime> loadStopTimes() throws IOException, JSONException {
	        String stopTimesJson = fetchData("date_stops_times.json");
	        JSONArray stopTimesArray = new JSONArray(stopTimesJson);
	        List<StopTime> tempList = new ArrayList<>();
	        for (int i = 0; i < stopTimesArray.length(); i++) {
	            StopTime st = StopTime.fromJson(stopTimesArray.getJSONObject(i));
	            tempList.add(st);
	        }
	        this.stopTimesList = tempList;
	        return tempList;
	    }
	    
	    /**
	     * Încarcă informațiile despre vehicule din fișierul JSON
	     * 
	     * @return JSONArray cu toate vehiculele disponibile
	     * @throws IOException dacă apare o eroare la citirea fișierului
	     * @throws JSONException dacă formatul JSON este invalid
	     */
	    public JSONArray loadVehicles() throws IOException, JSONException {
	        String vehiclesJson = fetchData("date_vehicule.json");
	        return new JSONArray(vehiclesJson);
	    }
	    
	    /**
	     * Caută vehicule care se potrivesc cu ID-ul sau ruta specificată
	     * 
	     * @param targetId ID-ul sau ruta vehiculului căutat
	     * @return lista de informații despre vehiculele găsite
	     * @throws IOException dacă apare o eroare la citirea fișierului
	     * @throws JSONException dacă formatul JSON este invalid
	     */
	    public List<DisplayVehicleInfo> findVehicles(String targetId) throws IOException, JSONException {
	        List<DisplayVehicleInfo> foundVehicles = new ArrayList<>();
	        String targetIdLower = targetId.toLowerCase();
	        JSONArray vehiclesArray = loadVehicles();
	        
	        for (int i = 0; i < vehiclesArray.length(); i++) {
	            JSONObject vehicleJson = vehiclesArray.getJSONObject(i);
	            try {
	                Vehicle vehicle = Vehicle.fromJson(vehicleJson);
	                
	                Route route = (vehicle.routeId != null) ? routesMap.get(vehicle.routeId) : null;
	                String routeShortName = (route != null) ? route.shortName.toLowerCase() : "";
	                
	                Trip trip = (vehicle.tripId != null) ? tripsMap.get(vehicle.tripId) : null;
	                String tripHeadsign = (trip != null) ? trip.headsign : "N/A";
	                
	                boolean labelMatch = vehicle.label.toLowerCase().equals(targetIdLower);
	                boolean routeMatch = !routeShortName.isEmpty() && routeShortName.equals(targetIdLower);
	                
	                if (labelMatch || routeMatch) {
	                    DisplayVehicleInfo displayInfo = new DisplayVehicleInfo();
	                    displayInfo.vehicle = vehicle;
	                    displayInfo.routeShortName = (route != null) ? route.shortName : "N/A";
	                    displayInfo.tripHeadsign = tripHeadsign;
	                    foundVehicles.add(displayInfo);
	                }
	            } catch (JSONException jsonEx) {
	                System.err.println("Skipping vehicle due to JSON parsing error: " + jsonEx.getMessage() + 
	                        " in JSON: " + vehicleJson.toString(2));
	            }
	        }
	        return foundVehicles;
	    }
	    
	    /**
	     * Calculează distanța între două puncte folosind formula Haversine
	     * 
	     * @param lat1 latitudinea primului punct
	     * @param lon1 longitudinea primului punct
	     * @param lat2 latitudinea celui de-al doilea punct
	     * @param lon2 longitudinea celui de-al doilea punct
	     * @return distanța în kilometri
	     */
	    public double haversine(double lat1, double lon1, double lat2, double lon2) {
	        final int R = 6371; // Raza Pământului în km
	        double latDistance = Math.toRadians(lat2 - lat1);
	        double lonDistance = Math.toRadians(lon2 - lon1);
	        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
	                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
	                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
	        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
	        return R * c;
	    }
	    
	    /**
	     * Găsește numele celei mai apropiate stații pentru un vehicul
	     * 
	     * @param vehicle vehiculul pentru care se caută stația cea mai apropiată
	     * @return un string care descrie stația curentă sau următoarea stație
	     */
	    public String findClosestStopName(Vehicle vehicle) {
	        if (vehicle.tripId == null || vehicle.latitude == null || vehicle.longitude == null)
	            return "Depou / Poziție necunoscută";
	        
	        // Filtrează lista de StopTime pentru a include doar opririle pentru acest trip
	        List<StopTime> stopsForTrip = new ArrayList<>();
	        for (StopTime st : stopTimesList) {
	            if (vehicle.tripId.equals(st.tripId)) {
	                stopsForTrip.add(st);
	            }
	        }
	        
	        // Sortează opririle după secvența lor
	        stopsForTrip.sort((st1, st2) -> Integer.compare(st1.stopSequence, st2.stopSequence));
	        
	        Stop closestStop = null;
	        double minDistance = Double.MAX_VALUE;
	        int currentIndex = -1;
	        
	        for (int i = 0; i < stopsForTrip.size(); i++) {
	            StopTime st = stopsForTrip.get(i);
	            Stop stop = stopsMap.get(st.stopId);
	            if (stop == null) continue;
	            
	            double distance = haversine(vehicle.latitude, vehicle.longitude, stop.latitude, stop.longitude);
	            if (distance < minDistance) {
	                minDistance = distance;
	                closestStop = stop;
	                currentIndex = i;
	            }
	        }
	        
	        if (closestStop == null)
	            return "Fără stații pe traseu";
	        
	        if (minDistance < 0.01) // Aproape de o stație (10 metri)
	            return "Stația curentă: " + closestStop.name;
	        
	        if (currentIndex + 1 < stopsForTrip.size()) {
	            StopTime nextStopTime = stopsForTrip.get(currentIndex + 1);
	            Stop nextStop = stopsMap.get(nextStopTime.stopId);
	            if (nextStop != null)
	                return "Următoarea stație: " + nextStop.name;
	        }
	        
	        return "Traseu necunoscut";
	    }
	    
	    /**
	     * @return the current routesMap
	     */
	    public Map<String, Route> getRoutesMap() {
	        return routesMap;
	    }
	    
	    /**
	     * @return the current tripsMap
	     */
	    public Map<String, Trip> getTripsMap() {
	        return tripsMap;
	    }
	    
	    /**
	     * @return the current stopsMap
	     */
	    public Map<String, Stop> getStopsMap() {
	        return stopsMap;
	    }
	    
	    /**
	     * @return the current stopTimesList
	     */
	    public List<StopTime> getStopTimesList() {
	        return stopTimesList;
	    }
	}