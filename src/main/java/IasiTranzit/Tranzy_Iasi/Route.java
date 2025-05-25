package IasiTranzit.Tranzy_Iasi;

import org.json.JSONException;
import org.json.JSONObject;


public class Route {
    String id;
    String shortName;
    String longName;
    String color;
    int type;
    String desc;

    static Route fromJson(JSONObject json) throws JSONException {
        Route r = new Route();
        r.id = String.valueOf(json.getInt("route_id"));
        r.shortName = json.optString("route_short_name", "");
        r.longName = json.optString("route_long_name", "");
        r.type = json.optInt("route_type", -1);
        r.desc = json.optString("route_desc", "");
        return r;
    }
}

