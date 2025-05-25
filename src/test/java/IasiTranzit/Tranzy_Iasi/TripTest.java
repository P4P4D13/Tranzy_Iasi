package IasiTranzit.Tranzy_Iasi;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Clasa de test pentru clasa Trip care reprezintă o călătorie/traseu în sistemul de transport.
 * Testează funcționalitatea de conversie din JSON în obiecte Trip.
 */
class TripTest {

    /**
     * Testează crearea unui obiect Trip din JSON cu date complete și valide.
     * Verifică parsarea corectă a tuturor câmpurilor: ID, ID ruta, capăt de linie și direcție.
     */
    @Test
    @DisplayName("Test creare Trip din JSON valid")
    void testFromJson_withValidData() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("trip_id", "T123");
        json.put("route_id", 5);
        json.put("trip_headsign", "Podu Ros");
        json.put("direction_id", 1);

        Trip trip = Trip.fromJson(json);

        assertEquals("T123", trip.id);
        assertEquals("5", trip.routeId);
        assertEquals("Podu Ros", trip.headsign);
        assertEquals(1, trip.directionId);
    }

    /**
     * Testează crearea unui obiect Trip când lipsește ID-ul călătoriei.
     * Verifică dacă se aruncă corect excepția JSONException.
     */
    @Test
    @DisplayName("Test lipsă trip_id în JSON")
    void testFromJson_missingTripId() {
        JSONObject json = new JSONObject();
        json.put("route_id", 5);
        json.put("trip_headsign", "Podu Ros");

        assertThrows(JSONException.class, () -> Trip.fromJson(json));
    }

    /**
     * Testează crearea unui obiect Trip cu valori implicite pentru câmpurile opționale.
     * Verifică dacă valorile implicite sunt setate corect când câmpurile lipsesc.
     */
    @Test
    @DisplayName("Test valori implicite pentru câmpuri opționale")
    void testFromJson_withOptionalFields() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("trip_id", "T456");
        json.put("route_id", 8);
        // trip_headsign și direction_id lipsesc intenționat

        Trip trip = Trip.fromJson(json);

        assertEquals("T456", trip.id);
        assertEquals("8", trip.routeId);
        assertEquals("N/A", trip.headsign); // Valoare implicită
        assertEquals(-1, trip.directionId); // Valoare implicită
    }

    /**
     * Testează conversia corectă a route_id din număr în șir de caractere.
     * Verifică dacă ID-ul rutei este convertit corect chiar și când este primit ca integer.
     */
    @Test
    @DisplayName("Test conversie route_id numeric")
    void testFromJson_numericRouteId() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("trip_id", "T789");
        json.put("route_id", 42); // Valoare numerică

        Trip trip = Trip.fromJson(json);

        assertEquals("42", trip.routeId); // Verifică conversia la String
    }

    /**
     * Testează comportamentul când direction_id este furnizat în JSON.
     * Verifică dacă valoarea direcției este parsată corect.
     */
    @Test
    @DisplayName("Test preluare direction_id")
    void testFromJson_withDirectionId() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("trip_id", "T101");
        json.put("route_id", 3);
        json.put("direction_id", 0); // Valoare explicită

        Trip trip = Trip.fromJson(json);

        assertEquals(0, trip.directionId);
    }
}