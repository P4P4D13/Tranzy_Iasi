package IasiTranzit.Tranzy_Iasi;

import org.json.JSONObject;

/**
 * Clasa care reprezinta o statie de transport.
 * Contine informatii despre id, nume si pozitia geografica (latitudine, longitudine).
 */
public class Stop {
	
	/**
	 * Constructor implicit gol, instantele Stop sunt create in general prin metoda fromJson().
	 */
	public Stop() {
	    // constructor implicit gol
	}
	
	/** ID-ul statiei */
    String id;
    
    /** Numele statiei */
    String name;
    
    /** Latitudinea statiei */
    Double latitude;
    
    /** Longitudinea statiei */
    Double longitude;

    /**
     * Creeaza un obiect Stop dintr-un JSONObject.
     * 
     * @param json obiect JSON cu datele statiei
     * @return obiect Stop populat cu date din JSON
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