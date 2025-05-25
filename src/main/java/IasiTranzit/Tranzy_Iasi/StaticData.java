package IasiTranzit.Tranzy_Iasi;

import java.util.List;
import java.util.Map;

/**
 * Clasa ce contine date statice incarcate la pornirea aplicatiei.
 */
public class StaticData {
	/** Harta rutelor */
    public final Map<String, Route> routesMap;
    
    /** Harta calatoriilor */
    public final Map<String, Trip> tripsMap;
    
    /** Harta statiilor */
    public final Map<String, Stop> stopsMap;
    
    /** Lista cu orarele statiilor. */
    public final List<StopTime> stopTimesList;

    /**
     * Constructor care initializeaza toate datele statice.
     * 
     * @param routesMap harta rutelor
     * @param tripsMap harta calatoriilor
     * @param stopsMap harta statiilor
     * @param stopTimesList lista cu orarele statiilor
     */
    public StaticData(Map<String, Route> routesMap, Map<String, Trip> tripsMap, Map<String, Stop> stopsMap, List<StopTime> stopTimesList) {
        this.routesMap = routesMap;
        this.tripsMap = tripsMap;
        this.stopsMap = stopsMap;
        this.stopTimesList = stopTimesList;
    }
}