package IasiTranzit.Tranzy_Iasi;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONArray;
/*
Date_agentie.main(args);
Date_rute.main(args);
Date_stop_times.main(args);
Date_stops.main(args);
Date_trips.main(args);
Date_vehicule.main(args);
*/
public class DateTransport {
	private static final String API_URL_agency = "https://api.tranzy.ai/v1/opendata/agency";
	private static final String API_URL_routes = "https://api.tranzy.ai/v1/opendata/routes";
	private static final String API_URL_stop_times = "https://api.tranzy.ai/v1/opendata/stop_times";
	private static final String API_URL_stops = "https://api.tranzy.ai/v1/opendata/stops";
	private static final String API_URL_trips = "https://api.tranzy.ai/v1/opendata/trips";
	private static final String API_URL_vehicles = "https://api.tranzy.ai/v1/opendata/vehicles";
	private static final String API_KEY = "7DgYhGzTQc5Nn8FfFeuFmhCAWcbadYQEShUjwu3e"; // Înlocuiește cu cheia API
//	private static final String TRIP_ID = "1"; 
//	private static final String ST_ID = "1"; // ID-ul agenției
//	private static final String STOP_ID = "1"; // ID-ul agenției
	private static final String AGENCY_ID = "1"; // ID-ul agenției

	public static String getTransportData_agency() throws Exception {	
	URL url = new URL(API_URL_agency);
	HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	conn.setRequestMethod("GET");
	conn.setRequestProperty("Accept", "application/json");
	conn.setRequestProperty("X-API-KEY", API_KEY); // Modificat corect
	conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)"); // Evităm blocarea cererii

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
	
	public static String getTransportData_routes() throws Exception {	
		URL url = new URL(API_URL_routes);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Accept", "application/json");
		conn.setRequestProperty("X-API-KEY", API_KEY); // Modificat corect
		conn.setRequestProperty("X-Agency-Id", AGENCY_ID);
		conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)"); // Evităm blocarea cererii

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
	
	public static String getTransportData_stop_times() throws Exception {	
		URL url = new URL(API_URL_stop_times);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Accept", "application/json");
		conn.setRequestProperty("X-API-KEY", API_KEY); // Modificat corect
		conn.setRequestProperty("X-Agency-Id", AGENCY_ID); // Modificat corect
		conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)"); // Evităm blocarea cererii

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
	public static String getTransportData_stops() throws Exception {	
		URL url = new URL(API_URL_stops);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Accept", "application/json");
		conn.setRequestProperty("X-API-KEY", API_KEY); // Modificat corect
		conn.setRequestProperty("X-Agency-Id", AGENCY_ID); // Modificat corect
		conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)"); // Evităm blocarea cererii

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

	public static String getTransportData_trips() throws Exception {	
		URL url = new URL(API_URL_trips);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Accept", "application/json");
		conn.setRequestProperty("X-API-KEY", API_KEY); // Modificat corect
		conn.setRequestProperty("X-Agency-Id", AGENCY_ID);
		conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)"); // Evităm blocarea cererii

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

	public static String getTransportData_vehicles() throws Exception {	
		URL url = new URL(API_URL_vehicles);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Accept", "application/json");
		conn.setRequestProperty("X-API-KEY", API_KEY); // Modificat corect
		conn.setRequestProperty("X-Agency-Id", AGENCY_ID); // Modificat corect
		conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)"); // Evităm blocarea cererii

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
			String jsonResponse_agency = getTransportData_agency();
			System.out.println("Response from API:\n" + jsonResponse_agency);
			writeToFile(jsonResponse_agency, "resources/date_agentie.json"); 
			
			String jsonResponse_routes = getTransportData_routes();
			System.out.println("Response from API:\n" + jsonResponse_routes);
			writeToFile(jsonResponse_routes, "resources/date_trips.json"); 
			
			String jsonResponse_stop_times = getTransportData_stop_times();
			System.out.println("Response from API:\n" + jsonResponse_stop_times);
			writeToFile(jsonResponse_stop_times, "resources/date_stops_times.json"); 
			
			String jsonResponse_stops = getTransportData_stops();
			System.out.println("Response from API:\n" + jsonResponse_stops);
			writeToFile(jsonResponse_stops, "resources/date_stops.json"); 
			
			String jsonResponse_trips = getTransportData_trips();
			System.out.println("Response from API:\n" + jsonResponse_trips);
			writeToFile(jsonResponse_trips, "resources/date_trips.json"); 
			
			String jsonResponse_vehicles = getTransportData_vehicles();
			System.out.println("Response from API:\n" + jsonResponse_vehicles);
			writeToFile(jsonResponse_vehicles, "resources/date_vehicule.json"); 
			

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
