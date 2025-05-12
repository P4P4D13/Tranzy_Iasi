package IasiTranzit.Tranzy_Iasi;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.json.JSONException;
import org.json.JSONObject;


public class Vehicle {
    String id;
    String label;
    Double latitude;
    Double longitude;
    long timestampEpochSeconds;
    int vehicleType;
    //consider inutil momentan adaugam poate mai tarziu
//    String bikeAccessible;
//    String wheelchairAccessible;
    Double speedKmH;
    String routeId;
    String tripId;

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
         //aici sunt probleme, nu merge
        try {
            v.timestampEpochSeconds = json.getLong("timestamp");
        } catch (JSONException e) {
            String tsString = json.optString("timestamp", null);
            if (tsString != null) {
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                    sdf.setTimeZone(TimeZone.getTimeZone("UTC")); // <<< Uses TimeZone here
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
//        v.bikeAccessible = json.optString("bike_accessible", "UNKNOWN");
//        v.wheelchairAccessible = json.optString("wheelchair_accessible", "UNKNOWN");
        // aici viteza e transformata in km/h teoretic corect din nou nu sunt sigur de ce sunt erori in viteza 
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
//aici se repeta astea doua 
        //nu cred ca e bine trebuie sa clarificam diferenta dintre route si trip
        //1
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
        //2
        Object tripIdObj = json.opt("trip_id");
          if (tripIdObj == null || tripIdObj == JSONObject.NULL) {
              v.tripId = null;
          } else {
               v.tripId = tripIdObj.toString();
          }

        return v;
    }
    String getFormattedTimestamp() {
        if (timestampEpochSeconds <= 0) return "N/A";
        Date date = new Date(timestampEpochSeconds * 1000L);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        return sdf.format(date);
    }

    String getVehicleTypeString() {
        switch (vehicleType) {
            case 0: return "Tram";
            case 3: return "Bus";
            default: return "Unknown (" + vehicleType + ")";
        }
    }
}