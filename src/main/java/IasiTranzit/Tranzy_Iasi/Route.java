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
    /**
     * 
     * @param son-primeste un fisier json ca parametru pentru procesare
     * @return returneaza datele despre ruta pentru a face legatura cu id mijlocului de transport
     */
    static Route fromJson(JSONObject json) {
        Route r = new Route();
        r.id = String.valueOf(json.getInt("route_id"));
        r.shortName = json.optString("route_short_name", "");
        r.longName = json.optString("route_long_name", "");
        r.color = json.optString("route_color", "FFFFFF");
        r.type = json.optInt("route_type", -1);
        r.desc = json.optString("route_desc", "");
        return r;
    }
}
