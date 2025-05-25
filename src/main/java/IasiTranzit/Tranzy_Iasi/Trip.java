package IasiTranzit.Tranzy_Iasi;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Clasa reprezinta un traseu (Trip).
 * Contine informatii despre ID-ul traseului, ruta asociata, denumirea destinatiei si directia traseului.
 */
public class Trip {
	
	/**
	 * Constructor implicit gol, instantele Trip sunt create in general prin metoda fromJson().
	 */
	public Trip() {
	    // constructor implicit gol
	}
	
	/** ID-ul unic al traseului */
    String id;
    
    /** ID-ul rutei asociate traseului */
    String routeId;
    
    /** Denumirea destinatiei sau indicatorul traseului. */
    String headsign;
    
    /** Directia traseului */
    int directionId;

    /**
     * Creeaza un obiect Trip dintr-un JSONObject.
     * 
     * @param json obiect JSON ce contine datele traseului
     * @return un obiect Trip initializat din JSON
     * @throws JSONException daca lipseste campul obligatoriu trip_id sau formatul e invalid
     */
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