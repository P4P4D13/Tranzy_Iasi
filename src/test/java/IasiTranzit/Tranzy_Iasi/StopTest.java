package IasiTranzit.Tranzy_Iasi;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class StopTest {

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

    @Test
    void testFromJson_withMissingFields() {
        JSONObject json = new JSONObject(); // no fields

        Stop stop = Stop.fromJson(json);

        assertEquals("", stop.id);
        assertEquals("", stop.name);
        assertEquals(0.0, stop.latitude, 0.0001);
        assertEquals(0.0, stop.longitude, 0.0001);
    }

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
