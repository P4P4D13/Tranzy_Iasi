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
	 * Clasa {@code DataManager} se ocupa cu obtinerea si procesarea datelor pentru aplicatia Tranzy Iasi.
	 * Ofera metode pentru incarcarea rutelor, traseelor, statiilor, si vehiculelor din fisiere locale.
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
	     * Obtine continutul unui fisier din resursele aplicatiei
	     * 
	     * @param fileName numele fisierului care trebuie citit
	     * @return continutul fisierului ca string
	     * @throws IOException daca fisierul nu poate fi citit
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
	     * Incarcam informatiile despre rute din fisierul JSON
	     * 
	     * @return o harta cu ID-urile de ruta si obiectele Route corespunzatoare
	     * @throws IOException daca apare o eroare la citirea fisierului
	     * @throws JSONException daca formatul JSON este invalid
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
	     * Incara informatiile despre trasee din fisierul JSON
	     * 
	     * @return o harta cu ID-urile de traseu si obiectele Trip corespunzatoare
	     * @throws IOException daca apare o eroare la citirea fisierului
	     * @throws JSONException daca formatul JSON este invalid
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
	     * Incarcam informatiile despre statii din fisierul JSON
	     * 
	     * @return o harta cu ID-urile statiilor si obiectele Stop corespunzatoare
	     * @throws IOException daca apare o eroare la citirea fisierului
	     * @throws JSONException daca formatul JSON este invalid
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
	     * Incarcam informatiile despre orarele statiilor din fisierul JSON
	     * 
	     * @return o lista cu obiectele StopTime
	     * @throws IOException daca apare o eroare la citirea fisierului
	     * @throws JSONException daca formatul JSON este invalid
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
	     * Incarcam informatiile despre vehicule din fisierul JSON
	     * 
	     * @return JSONArray cu toate vehiculele disponibile
	     * @throws IOException daca apare o eroare la citirea fisierului
	     * @throws JSONException daca formatul JSON este invalid
	     */
	    public JSONArray loadVehicles() throws IOException, JSONException {
	        String vehiclesJson = fetchData("date_vehicule.json");
	        return new JSONArray(vehiclesJson);
	    }
	    
	    /**
	     * Cauta vehicule care se potrivesc cu ID-ul sau ruta specificata
	     * 
	     * @param targetId ID-ul sau ruta vehiculului cautat
	     * @return lista de informatii despre vehiculele gasite
	     * @throws IOException daca apare o eroare la citirea fisierului
	     * @throws JSONException daca formatul JSON este invalid
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
	     * Calculeaza distanta intre doua puncte folosind formula Haversine
	     * 
	     * @param lat1 latitudinea primului punct
	     * @param lon1 longitudinea primului punct
	     * @param lat2 latitudinea celui de-al doilea punct
	     * @param lon2 longitudinea celui de-al doilea punct
	     * @return distana in kilometri
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
	     * Gaseste numele celei mai apropiate statii pentru un vehicul
	     * 
	     * @param vehicle vehiculul pentru care se cauta statia cea mai apropiata
	     * @return un string care descrie statia curenta sau urmatoarea statie
	     */
	    public String findClosestStopName(Vehicle vehicle) {
	        if (vehicle.tripId == null || vehicle.latitude == null || vehicle.longitude == null)
	            return "Depou / Poziție necunoscută";
	        
	        // Filtreaza lista de StopTime pentru a include doar opririle pentru acest trip
	        List<StopTime> stopsForTrip = new ArrayList<>();
	        for (StopTime st : stopTimesList) {
	            if (vehicle.tripId.equals(st.tripId)) {
	                stopsForTrip.add(st);
	            }
	        }
	        
	        // Sorteaza opririle dupa secventa lor
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
	            return "Fara statii pe traseu";
	        
	        if (minDistance < 0.01) // Aproape de o statie (10 metri)
	            return "Statia curenta: " + closestStop.name;
	        
	        if (currentIndex + 1 < stopsForTrip.size()) {
	            StopTime nextStopTime = stopsForTrip.get(currentIndex + 1);
	            Stop nextStop = stopsMap.get(nextStopTime.stopId);
	            if (nextStop != null)
	                return "Urmatoarea statie: " + nextStop.name;
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