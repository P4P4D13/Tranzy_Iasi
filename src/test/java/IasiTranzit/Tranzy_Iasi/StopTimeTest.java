package IasiTranzit.Tranzy_Iasi;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Clasa de test pentru clasa StopTime care gestionează orele de oprire ale traseelor.
 * Această clasă testează funcționalitatea de parsare a datelor JSON în obiecte StopTime.
 */
public class StopTimeTest {

    /**
     * Testează crearea unui obiect StopTime din JSON cu date valide.
     * Verifică dacă toate câmpurile sunt parsate corect.
     */
    @Test
    void testFromJson_withValidData() {
        JSONObject json = new JSONObject();
        json.put("trip_id", "T1");
        json.put("stop_id", "S1");
        json.put("stop_sequence", 5);

        StopTime stopTime = StopTime.fromJson(json);

        assertEquals("T1", stopTime.tripId);
        assertEquals("S1", stopTime.stopId);
        assertEquals(5, stopTime.stopSequence);
    }

    /**
     * Testează crearea unui obiect StopTime din JSON fără câmpuri.
     * Verifică dacă valorile implicite sunt setate corect (șiruri goale și 0).
     */
    @Test
    void testFromJson_withMissingFields() {
        JSONObject json = new JSONObject(); // fără câmpuri

        StopTime stopTime = StopTime.fromJson(json);

        assertEquals("", stopTime.tripId);
        assertEquals("", stopTime.stopId);
        assertEquals(0, stopTime.stopSequence);
    }

    /**
     * Testează crearea unui obiect StopTime din JSON cu câmpuri parțiale.
     * Verifică dacă doar câmpurile prezente sunt parsate, iar celelalte au valori implicite.
     */
    @Test
    void testFromJson_withPartialFields() {
        JSONObject json = new JSONObject();
        json.put("trip_id", "T2");

        StopTime stopTime = StopTime.fromJson(json);

        assertEquals("T2", stopTime.tripId);
        assertEquals("", stopTime.stopId);
        assertEquals(0, stopTime.stopSequence);
    }

    /**
     * Testează crearea unui obiect StopTime din JSON cu o secvență de oprire invalidă (non-numerică).
     * Verifică dacă valoarea implicită (0) este setată pentru câmpuri invalide.
     */
    @Test
    void testFromJson_withNonIntegerStopSequence() {
        JSONObject json = new JSONObject();
        json.put("trip_id", "T3");
        json.put("stop_id", "S2");
        json.put("stop_sequence", "not_a_number"); // Tip de date invalid

        StopTime stopTime = StopTime.fromJson(json);

        assertEquals("T3", stopTime.tripId);
        assertEquals("S2", stopTime.stopId);
        assertEquals(0, stopTime.stopSequence); // Valoarea implicită trebuie să fie 0 pentru numere invalide
    }
}