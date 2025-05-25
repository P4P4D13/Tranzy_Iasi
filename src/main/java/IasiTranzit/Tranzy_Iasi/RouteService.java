
package IasiTranzit.Tranzy_Iasi;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Serviciu pentru gestionarea rutelor și determinarea stațiilor apropiate de vehicule.
 * 
 * Această clasă oferă funcționalități pentru calcularea celei mai apropiate stații
 * sau a următoarei stații pentru un vehicul dat, pe baza traseului și poziției sale.
 * 
 * @author Echipa Tranzy Iași
 * @version 1.0
 * @since 2024
 */
public class RouteService {

    /**
     * Găsește numele celei mai apropiate stații sau a următoarei stații pentru un vehicul dat,
     * pe baza traseului și locației sale.
     * 
     * Această metodă este statică deoarece nu depinde de nicio stare a unei instanțe RouteService.
     * Folosește formula Haversine pentru a calcula distanțele geografice și determină
     * stația cea mai apropiată fizic de vehicul.
     *
     * @param vehicle Vehiculul pentru care se verifică stația. Nu poate fi null.
     * @param stopsMap Mapă cu toate stațiile, având stop_id ca și cheie. Nu poate fi null.
     * @param stopTimesList Lista cu toate timpii de oprire la stații. Nu poate fi null.
     * @return String formatat care descrie stația curentă sau următoarea stație.
     *         Poate returna mesaje precum:
     *         - "În stație: [nume stație]" - dacă vehiculul este foarte aproape (sub 25m)
     *         - "Următoarea stație: [nume stație]" - pentru următoarea stație programată
     *         - "Spre ultima stație: [nume stație]" - dacă se apropie de capătul traseului
     *         - "Depou / Poziție necunoscută" - dacă lipsesc informații despre vehicul
     *         - "Fără stații definite pe acest traseu." - dacă traseul nu are stații
     *         - "Nu s-au găsit stații valide pe traseu." - dacă stațiile nu au coordonate valide
     * 
     * @throws NullPointerException dacă vehicle, stopsMap sau stopTimesList sunt null
     * 
     * @see #haversine(double, double, double, double)
     */
    public static String findClosestStopName(Vehicle vehicle, Map<String, Stop> stopsMap, List<StopTime> stopTimesList) {
        if (vehicle.tripId == null || vehicle.latitude == null || vehicle.longitude == null) {
            return "Depou / Poziție necunoscută";
        }

        // Filtrează și sortează stațiile pentru traseul curent al vehiculului
        List<StopTime> stopsForTrip = stopTimesList.stream()
                .filter(st -> vehicle.tripId.equals(st.tripId))
                .sorted(Comparator.comparingInt(st -> st.stopSequence))
                .toList();

        if (stopsForTrip.isEmpty()) {
            return "Fără stații definite pe acest traseu.";
        }

        Stop closestStop = null;
        double minDistance = Double.MAX_VALUE;
        int closestStopIndex = -1;

        // Găsește stația de pe traseu care este fizic cea mai apropiată de vehicul
        for (int i = 0; i < stopsForTrip.size(); i++) {
            StopTime st = stopsForTrip.get(i);
            Stop stop = stopsMap.get(st.stopId);
            if (stop == null || stop.latitude == null || stop.longitude == null) {
                continue;
            }

            double distance = haversine(vehicle.latitude, vehicle.longitude, stop.latitude, stop.longitude);
            if (distance < minDistance) {
                minDistance = distance;
                closestStop = stop;
                closestStopIndex = i;
            }
        }

        if (closestStop == null) {
            return "Nu s-au găsit stații valide pe traseu.";
        }

        // Dacă vehiculul este foarte aproape de o stație, anunță-o ca stația curentă (Prag: 25 metri)
        if (minDistance < 0.025) {
            return "În stație: " + closestStop.name;
        }

        // Altfel, anunță următoarea stație din program după cea mai apropiată fizic
        if (closestStopIndex + 1 < stopsForTrip.size()) {
            StopTime nextStopTime = stopsForTrip.get(closestStopIndex + 1);
            Stop nextStop = stopsMap.get(nextStopTime.stopId);
            if (nextStop != null) {
                return "Următoarea stație: " + nextStop.name;
            }
        }

        // Dacă stația cea mai apropiată este ultima de pe traseu
        if (closestStopIndex == stopsForTrip.size() - 1) {
            return "Spre ultima stație: " + closestStop.name;
        }

        return "Traseu necunoscut"; // Fallback
    }

    /**
     * Calculează distanța dintre două puncte geografice în kilometri folosind formula Haversine.
     * 
     * Formula Haversine este utilizată pentru calcularea distanțelor pe sfera terestră,
     * luând în considerare curbura Pământului. Este precisă pentru distanțe mici și medii.
     * 
     * @param lat1 Latitudinea primului punct în grade decimale. Intervalul valid: [-90, 90]
     * @param lon1 Longitudinea primului punct în grade decimale. Intervalul valid: [-180, 180]
     * @param lat2 Latitudinea celui de-al doilea punct în grade decimale. Intervalul valid: [-90, 90]
     * @param lon2 Longitudinea celui de-al doilea punct în grade decimale. Intervalul valid: [-180, 180]
     * @return Distanța în kilometri între cele două puncte geografice (valoare pozitivă)
     * 
     * @see <a href="https://en.wikipedia.org/wiki/Haversine_formula">Formula Haversine pe Wikipedia</a>
     */
    private static double haversine(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Raza Pământului în kilometri
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
}