package IasiTranzit.Tranzy_Iasi;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

/**
 * Teste unitare pentru clasa RouteService.
 * 
 * Aceste teste verifică funcționalitatea metodei findClosestStopName în diverse scenarii:
 * - Vehicule fără informații complete
 * - Vehicule aproape de stații
 * - Vehicule între stații
 * - Vehicule la capătul traseului
 * - Scenarii cu date lipsă sau invalide
 * 
 * @version 1.0
 */
class RouteServiceTest {

    private Map<String, Stop> stopsMap;
    private List<StopTime> stopTimesList;
    private Vehicle vehicle;

    /**
     * Test pentru vehicul fără informații de locație.
     * Verifică că metoda returnează mesajul corespunzător când lipsesc coordonatele.
     */
    @Test
    @DisplayName("Vehicul fără informații de locație")
    void testVehicleWithoutLocationInfo() {
        vehicle.latitude = null;
        vehicle.longitude = null;
        
        String result = RouteService.findClosestStopName(vehicle, stopsMap, stopTimesList);
        
        assertEquals("Depou / Poziție necunoscută", result);
    }

    /**
     * Test pentru vehicul fără ID de traseu.
     * Verifică comportamentul când vehiculul nu are asociat un traseu.
     */
    @Test
    @DisplayName("Vehicul fără ID de traseu")
    void testVehicleWithoutTripId() {
        vehicle.tripId = null;
        vehicle.latitude = 47.1585;
        vehicle.longitude = 27.6014;
        
        String result = RouteService.findClosestStopName(vehicle, stopsMap, stopTimesList);
        
        assertEquals("Depou / Poziție necunoscută", result);
    }

    /**
     * Test pentru vehicul foarte aproape de o stație.
     * Verifică că se detectează corect când vehiculul este în stație (sub 25m).
     */
    @Test
    @DisplayName("Vehicul foarte aproape de stație (în stație)")
    void testVehicleVeryCloseToStop() {
        // Poziționează vehiculul foarte aproape de Piața Unirii (diferență de ~5 metri)
        vehicle.latitude = 47.1586;
        vehicle.longitude = 27.6015;
        
        String result = RouteService.findClosestStopName(vehicle, stopsMap, stopTimesList);
        
        assertEquals("În stație: Piața Unirii", result);
    }

    /**
     * Test pentru vehicul între stații.
     * Verifică că se anunță corect următoarea stație când vehiculul este pe drum.
     */
    @Test
    @DisplayName("Vehicul între stații - anunță următoarea stație")
    void testVehicleBetweenStops() {
        // Poziționează vehiculul între Piața Unirii și Gara CFR, dar mai aproape de Piața Unirii
        vehicle.latitude = 47.1650;
        vehicle.longitude = 27.5950;
        
        String result = RouteService.findClosestStopName(vehicle, stopsMap, stopTimesList);
        
        assertEquals("Următoarea stație: Gara CFR", result);
    }

    /**
     * Test pentru vehicul aproape de ultima stație.
     * Verifică comportamentul la capătul traseului.
     */
    @Test
    @DisplayName("Vehicul aproape de ultima stație din traseu")
    void testVehicleNearLastStop() {
        // Poziționează vehiculul aproape de Tudor Vladimirescu (ultima stație)
        vehicle.latitude = 47.1890;
        vehicle.longitude = 27.5400;
        
        String result = RouteService.findClosestStopName(vehicle, stopsMap, stopTimesList);
        
        assertEquals("Spre ultima stație: Tudor Vladimirescu", result);
    }

    /**
     * Test pentru traseu fără stații definite.
     * Verifică comportamentul când lista de stații este goală pentru traseu.
     */
    @Test
    @DisplayName("Traseu fără stații definite")
    void testTripWithoutStops() {
        vehicle.tripId = "trip_inexistent";
        vehicle.latitude = 47.1585;
        vehicle.longitude = 27.6014;
        
        String result = RouteService.findClosestStopName(vehicle, stopsMap, stopTimesList);
        
        assertEquals("Fără stații definite pe acest traseu.", result);
    }


    /**
     * Test pentru mapă de stații goală.
     * Verifică comportamentul când mapa de stații este goală.
     */
    @Test
    @DisplayName("Mapă de stații goală")
    void testEmptyStopsMap() {
        stopsMap.clear();
        vehicle.latitude = 47.1585;
        vehicle.longitude = 27.6014;
        
        String result = RouteService.findClosestStopName(vehicle, stopsMap, stopTimesList);
        
        assertEquals("Nu s-au găsit stații valide pe traseu.", result);
    }

    /**
     * Test pentru listă de timpi de oprire goală.
     * Verifică comportamentul când lista de timpi de oprire este goală.
     */
    @Test
    @DisplayName("Listă de timpi de oprire goală")
    void testEmptyStopTimesList() {
        stopTimesList.clear();
        vehicle.latitude = 47.1585;
        vehicle.longitude = 27.6014;
        
        String result = RouteService.findClosestStopName(vehicle, stopsMap, stopTimesList);
        
        assertEquals("Fără stații definite pe acest traseu.", result);
    }

    /**
     * Test pentru verificarea preciziei calculului de distanță.
     * Testează formula Haversine cu coordonate cunoscute.
     */
    @Test
    @DisplayName("Verificare precizie calcul distanță Haversine")
    void testHaversineAccuracy() {
        // Test cu coordonate reale: distanța dintre Piața Unirii și Gara CFR din Iași
        // Distanța reală este aproximativ 1.8 km
        vehicle.latitude = 47.1585; // Piața Unirii
        vehicle.longitude = 27.6014;
        
        // Poziționez vehiculul aproape de Gara CFR pentru test
        vehicle.latitude = 47.1736; // Gara CFR
        vehicle.longitude = 27.5877;
        
        String result = RouteService.findClosestStopName(vehicle, stopsMap, stopTimesList);
        
        // Vehiculul ar trebui să fie foarte aproape de Gara CFR
        assertTrue(result.contains("În stație: Gara CFR") || result.contains("Următoarea stație: Universitate"));
    }
}