package IasiTranzit.Tranzy_Iasi;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Clasa reprezinta un vehicul cu date precum pozitia, tipul, viteza si traseul asociat.
 */
public class Vehicle {
	
	/**
	 * Constructor implicit gol, instantele Vehicle sunt create in general prin metoda fromJson().
	 */
	public Vehicle() {
	    // constructor implicit gol
	}
	
	/** ID-ul vehiculului */
    String id;
    
    /** Eticheta/denumirea vehiculului */
    String label;
    
    /** Latitudinea curenta */
    Double latitude;
    
    /** Longitudinea curenta */
    Double longitude;
    
    /** Timestamp-ul in secunde (epoch) */
    long timestampEpochSeconds;
    
    /** Tipul vehiculului*/
    int vehicleType;
    
    /** Viteza in km/h */
    Double speedKmH;
    
    /** ID ruta asociata */
    String routeId;
    
    /** ID traseu asociat */
    String tripId;

    /**
     * Creeaza un obiect Vehicle dintr-un JSONObject.
     * 
     * @param json obiect JSON cu datele vehiculului
     * @return obiect Vehicle initializat
     * @throws JSONException daca lipseste ID-ul vehiculului sau datele sunt invalide
     */
    static Vehicle fromJson(JSONObject json) throws JSONException {
        Vehicle v = new Vehicle();

        Object idObj = json.opt("id");
        v.id = (idObj == null) ? null : idObj.toString();
        if (v.id == null) {
            throw new JSONException("Vehicle ID is missing or null in JSON");
        }

        v.label = json.optString("label", v.id);

        if (json.has("latitude") && !json.isNull("latitude")) {
            v.latitude = json.optDouble("latitude", Double.NaN);
            if (Double.isNaN(v.latitude)) v.latitude = null;
        } else {
            v.latitude = null;
        }

         if (json.has("longitude") && !json.isNull("longitude")) {
            v.longitude = json.optDouble("longitude", Double.NaN);
             if (Double.isNaN(v.longitude)) v.longitude = null;
        } else {
            v.longitude = null;
        }
         
        try {
            v.timestampEpochSeconds = json.getLong("timestamp");
        } catch (JSONException e) {
            String tsString = json.optString("timestamp", null);
            if (tsString != null) {
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                    sdf.setTimeZone(TimeZone.getTimeZone("UTC")); 
                    Date date = sdf.parse(tsString);
                    v.timestampEpochSeconds = date.getTime() / 1000L;
                } catch (ParseException pe) {
                    System.err.println("Could not parse timestamp string: " + tsString + " - " + pe.getMessage());
                    v.timestampEpochSeconds = 0;
                }
            } else {
                System.err.println("Timestamp missing or not a recognized number/string in JSON: " + json.opt("timestamp"));
                v.timestampEpochSeconds = 0;
            }
        }

        v.vehicleType = json.optInt("vehicle_type", -1);

        // aici viteza e transformata in km/h teoretic corect, sunt erori in viteza din cauza la TranzyAI
        if (json.has("speed") && !json.isNull("speed")) {
            double speedMs = json.optDouble("speed", Double.NaN);
            if (!Double.isNaN(speedMs)) {
                 v.speedKmH = speedMs * 3.6;
            } else {
                v.speedKmH = null;
            }
        } else {
            v.speedKmH = null;
        }

        Object routeIdObj = json.opt("route_id");
         if (routeIdObj == null || routeIdObj == JSONObject.NULL) {
             v.routeId = null;
         } else {
             String routeIdStr = routeIdObj.toString();
             if ("0".equals(routeIdStr)) {
                 v.routeId = null;
             } else {
                 v.routeId = routeIdStr;
             }
         }
        
        Object tripIdObj = json.opt("trip_id");
          if (tripIdObj == null || tripIdObj == JSONObject.NULL) {
              v.tripId = null;
          } else {
               v.tripId = tripIdObj.toString();
          }

        return v;
    }
    
    /**
     * Returneaza timestamp-ul formatat (HH:mm:ss) sau "N/A" daca nu este valid.
     * 
     * @return timestamp formatat ca ora
     */
    String getFormattedTimestamp() {
        if (timestampEpochSeconds <= 0) return "N/A";
        Date date = new Date(timestampEpochSeconds * 1000L);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        return sdf.format(date);
    }

    /**
     * Returneaza tipul vehiculului ca text.
     * 
     * @return tip vehicul sau Unknown cu codul numeric
     */
    String getVehicleTypeString() {
        switch (vehicleType) {
            case 0: return "Tram";
            case 3: return "Bus";
            default: return "Unknown (" + vehicleType + ")";
        }
    }
}