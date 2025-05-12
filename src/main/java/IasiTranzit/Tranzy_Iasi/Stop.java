package IasiTranzit.Tranzy_Iasi;

import org.json.JSONObject;

public class Stop {
    String id, name;
    double latitude, longitude;
    /**
     * 
     * @param json-primeste un fisier json ca parametru pentru procesare
     * @return returneaza un obiect de tip Stop pentru a ne ajuta la calculare de distante fata de pozitia mijlocului de transport in comun
     */
    static Stop fromJson(JSONObject json) {
        Stop s = new Stop();
        s.id = json.optString("stop_id");
        s.name = json.optString("stop_name");
        s.latitude = json.optDouble("stop_lat");
        s.longitude = json.optDouble("stop_lon");
        return s;
    }
}