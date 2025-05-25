package IasiTranzit.Tranzy_Iasi;

import java.util.List;
import java.util.Map;

// This class holds all the static data loaded at startup.
public class StaticData {
    public final Map<String, Route> routesMap;
    public final Map<String, Trip> tripsMap;
    public final Map<String, Stop> stopsMap;
    public final List<StopTime> stopTimesList;

    public StaticData(Map<String, Route> routesMap, Map<String, Trip> tripsMap, Map<String, Stop> stopsMap, List<StopTime> stopTimesList) {
        this.routesMap = routesMap;
        this.tripsMap = tripsMap;
        this.stopsMap = stopsMap;
        this.stopTimesList = stopTimesList;
    }
}