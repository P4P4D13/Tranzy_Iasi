package IasiTranzit.Tranzy_Iasi;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class RouteService {

    /**
     * Finds the name of the closest or next stop for a given vehicle based on its trip and location.
     * This method is static because it doesn't depend on any state of a RouteService instance.
     *
     * @param vehicle The vehicle to check.
     * @param stopsMap A map of all stops, with stop_id as the key.
     * @param stopTimesList A list of all stop times.
     * @return A formatted string describing the current or next stop.
     */
    public static String findClosestStopName(Vehicle vehicle, Map<String, Stop> stopsMap, List<StopTime> stopTimesList) {
        if (vehicle.tripId == null || vehicle.latitude == null || vehicle.longitude == null) {
            return "Depou / Poziție necunoscută";
        }

        // Filter and sort the stops for the vehicle's current trip
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

        // Find the stop on the route that is physically closest to the vehicle
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

        // If the vehicle is very close to a stop, announce it as the current stop. (Threshold: 25 meters)
        if (minDistance < 0.025) { 
            return "În stație: " + closestStop.name;
        }

        // Otherwise, announce the next stop on the schedule after the physically closest one.
        if (closestStopIndex + 1 < stopsForTrip.size()) {
            StopTime nextStopTime = stopsForTrip.get(closestStopIndex + 1);
            Stop nextStop = stopsMap.get(nextStopTime.stopId);
            if (nextStop != null) {
                return "Următoarea stație: " + nextStop.name;
            }
        }
        
        // If the closest stop is the last one on the route
        if (closestStopIndex == stopsForTrip.size() - 1) {
            return "Spre ultima stație: " + closestStop.name;
        }

        return "Traseu necunoscut"; // Fallback
    }

    /**
     * Calculates the distance between two geographic points in kilometers using the Haversine formula.
     * This is a private helper method for this service.
     */
    private static double haversine(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Earth's radius in kilometers
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                 + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                 * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
}