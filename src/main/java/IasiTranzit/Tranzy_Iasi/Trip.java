package IasiTranzit.Tranzy_Iasi;

import org.json.JSONException;
import org.json.JSONObject;

public class Trip {
    String id;
    String routeId;
    String headsign;
    int directionId;

    static Trip fromJson(JSONObject json) throws JSONException {
        Trip t = new Trip();
         Object tripIdObj = json.opt("trip_id");
         t.id = (tripIdObj == null) ? null : tripIdObj.toString();
        if (t.id == null) {
             throw new JSONException("Trip ID is missing or null in JSON");
        }

        t.routeId = String.valueOf(json.getInt("route_id"));

        t.headsign = json.optString("trip_headsign", "N/A");
        t.directionId = json.optInt("direction_id", -1);
        return t;
    }
}
