package IasiTranzit.Tranzy_Iasi;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Clasa care reprezinta o ruta de transport (ex: autobuz, tramvai).
 * Contine informatii precum id, nume scurt/lung, culoare, tip si descriere.
 */
public class Route {
	
	/**
	 * Constructor implicit gol, instantele Route sunt create in general prin metoda fromJson().
	 */
	public Route() {
	    // constructor implicit gol
	}
	
	/** ID-ul unic al rutei */
    String id;
    
    /** Numele scurt al rutei */
    String shortName;
    
    /** Numele complet al rutei */
    String longName;
    
    /** Tipul rutei */
    int type;
    
    /** Descriere aditionala a rutei */
    String desc;

    /**
     * Creeaza un obiect Route pe baza unui obiect JSON.
     *
     * @param json obiectul JSON ce contine datele rutei
     * @return obiect Route populat
     * @throws JSONException daca lipsesc campuri obligatorii sau formatul este invalid
     */
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

