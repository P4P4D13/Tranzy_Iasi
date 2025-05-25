package IasiTranzit.Tranzy_Iasi;

import org.json.JSONObject;


public class Stop {
    String id, name;
    Double latitude, longitude;

    static Stop fromJson(JSONObject json) {
        Stop s = new Stop();
        s.id = json.optString("stop_id");
        s.name = json.optString("stop_name");
        s.latitude = json.optDouble("stop_lat");
        s.longitude = json.optDouble("stop_lon");
        return s;
    }
}