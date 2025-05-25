package IasiTranzit.Tranzy_Iasi;

import org.json.JSONObject;

/**
 * Clasa care reprezinta un moment de oprire pentru o cursa.
 * Contine id-ul cursei, id-ul statiei si ordinea opririi.
 */
public class StopTime {
	
	/**
	 * Constructor implicit gol, instantele StopTime sunt create in general prin metoda fromJson().
	 */
	public StopTime() {
	    // constructor implicit gol
	}
	
	/** Id-ul cursei. */
    String tripId;
    
    /** Id-ul statiei. */
    String stopId;
    
    /** Ordinea opririi in cursa. */
    int stopSequence;

    /**
     * Creeaza un obiect StopTime dintr-un JSON.
     * 
     * @param json Obiect JSON cu datele pentru momentul de oprire.
     * @return Instanta StopTime populata cu datele din JSON.
     */
    static StopTime fromJson(JSONObject json) {
        StopTime st = new StopTime();
        st.tripId = json.optString("trip_id");
        st.stopId = json.optString("stop_id");
        st.stopSequence = json.optInt("stop_sequence");
        return st;
    }
}
