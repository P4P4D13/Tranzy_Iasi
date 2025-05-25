package IasiTranzit.Tranzy_Iasi;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Clasa de test pentru clasa Stop care reprezintă o stație de transport public.
 * Această clasă testează funcționalitatea de conversie din JSON în obiecte Stop.
 */
public class StopTest {

    /**
     * Testează crearea unui obiect Stop din JSON cu date complete și valide.
     * Verifică dacă toate câmpurile (ID, nume, latitudine, longitudine) sunt parsate corect.
     */
    @Test
    void testFromJson_withValidData() {
        JSONObject json = new JSONObject();
        json.put("stop_id", "S1");
        json.put("stop_name", "Central Station");
        json.put("stop_lat", 47.166);
        json.put("stop_lon", 27.574);

        Stop stop = Stop.fromJson(json);

        assertEquals("S1", stop.id);
        assertEquals("Central Station", stop.name);
        assertEquals(47.166, stop.latitude, 0.0001);
        assertEquals(27.574, stop.longitude, 0.0001);
    }

    /**
     * Testează crearea unui obiect Stop din JSON fără niciun câmp specificat.
     * Verifică dacă valorile implicite sunt setate corect:
     * - șiruri de caractere goale pentru ID și nume
     * - 0.0 pentru coordonatele geografice
     */
    @Test
    void testFromJson_withMissingFields() {
        JSONObject json = new JSONObject(); // fără câmpuri

        Stop stop = Stop.fromJson(json);

        assertEquals("", stop.id);
        assertEquals("", stop.name);
        assertEquals(0.0, stop.latitude, 0.0001);
        assertEquals(0.0, stop.longitude, 0.0001);
    }

    /**
     * Testează crearea unui obiect Stop din JSON cu doar unele câmpuri specificate.
     * Verifică dacă:
     * - câmpurile prezente sunt parsate corect
     * - câmpurile lipsă primesc valori implicite
     */
    @Test
    void testFromJson_withPartialFields() {
        JSONObject json = new JSONObject();
        json.put("stop_id", "S2");

        Stop stop = Stop.fromJson(json);

        assertEquals("S2", stop.id);
        assertEquals("", stop.name);
        assertEquals(0.0, stop.latitude, 0.0001);
        assertEquals(0.0, stop.longitude, 0.0001);
    }
}