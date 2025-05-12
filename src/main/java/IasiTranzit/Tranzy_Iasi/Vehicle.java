package IasiTranzit.Tranzy_Iasi;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Reprezintă un vehicul de transport public, cu detalii precum ID, coordonate geografice,
 * tipul vehiculului, viteză, și timestamp.
 * Acesta este folosit pentru a manipula datele vehiculului extrase dintr-un fișier JSON.
 * 
 * @author 
 */
public class Vehicle {
    
    /** ID-ul vehiculului */
    String id;
    
    /** Eticheta vehiculului */
    String label;
    
    /** Latitudinea vehiculului */
    Double latitude;
    
    /** Longitudinea vehiculului */
    Double longitude;
    
    /** Timpul în format Epoch (secunde) al vehiculului */
    long timestampEpochSeconds;
    
    /** Tipul vehiculului (ex: tramvai, autobuz) */
    int vehicleType;
    
    /** Viteza vehiculului în km/h */
    Double speedKmH;
    
    /** ID-ul rutei vehiculului */
    String routeId;
    
    /** ID-ul cursei vehiculului */
    String tripId;

    /**
     * Creează un obiect de tip Vehicle dintr-un obiect JSON.
     * Acesta preia datele vehiculului din fișierul JSON și le convertește în proprietăți ale vehiculului.
     * 
     * @param json Obiectul JSON care conține datele vehiculului.
     * @return Un obiect de tip Vehicle cu detaliile vehiculului extrase din JSON.
     * @throws JSONException Dacă există o eroare la prelucrarea JSON-ului.
     */
    static Vehicle fromJson(JSONObject json) throws JSONException {
        Vehicle v = new Vehicle();
      
        Object idObj = json.opt("id");
        v.id = (idObj == null) ? null : idObj.toString();
        if (v.id == null) {
            throw new JSONException("Vehicle ID is missing or null in JSON");
        }
        
        // Setează eticheta vehiculului
        v.label = json.optString("label", v.id);

        // Setează latitudinea vehiculului
        if (json.has("latitude") && !json.isNull("latitude")) {
            v.latitude = json.optDouble("latitude", Double.NaN);
            if (Double.isNaN(v.latitude)) v.latitude = null;
        } else {
            v.latitude = null;
        }

        // Setează longitudinea vehiculului
         if (json.has("longitude") && !json.isNull("longitude")) {
            v.longitude = json.optDouble("longitude", Double.NaN);
             if (Double.isNaN(v.longitude)) v.longitude = null;
        } else {
            v.longitude = null;
        }
       
        // Setează timestamp-ul vehiculului
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

        // Setează tipul vehiculului
        v.vehicleType = json.optInt("vehicle_type", -1);

        // Setează viteza vehiculului în km/h
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

        // Setează ID-ul rutei vehiculului
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

        // Setează ID-ul cursei vehiculului
        Object tripIdObj = json.opt("trip_id");
          if (tripIdObj == null || tripIdObj == JSONObject.NULL) {
              v.tripId = null;
          } else {
               v.tripId = tripIdObj.toString();
          }

        return v;
    }

    /**
     * Returnează timestamp-ul vehiculului în format oră:minute:secunde (HH:mm:ss).
     * Dacă timestamp-ul este invalid sau 0, va returna "N/A".
     * 
     * @return Timestamp-ul vehiculului în format HH:mm:ss sau "N/A" dacă nu este valid.
     */
    String getFormattedTimestamp() {
        if (timestampEpochSeconds <= 0) return "N/A";
        Date date = new Date(timestampEpochSeconds * 1000L);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        return sdf.format(date);
    }

    /**
     * Returnează tipul vehiculului sub formă de șir de caractere (ex: "Tram", "Bus").
     * Dacă tipul nu este recunoscut, va returna "Unknown" urmat de valoarea tipului.
     * 
     * @return Tipul vehiculului sub formă de șir de caractere.
     */
    String getVehicleTypeString() {
        switch (vehicleType) {
            case 0: return "Tram";
            case 3: return "Bus";
            default: return "Unknown (" + vehicleType + ")";
        }
    }
}
