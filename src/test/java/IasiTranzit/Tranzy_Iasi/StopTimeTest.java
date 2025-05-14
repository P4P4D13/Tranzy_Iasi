package IasiTranzit.Tranzy_Iasi;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class StopTimeTest {

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

    @Test
    void testFromJson_withMissingFields() {
        JSONObject json = new JSONObject(); // no fields

        StopTime stopTime = StopTime.fromJson(json);

        assertEquals("", stopTime.tripId);
        assertEquals("", stopTime.stopId);
        assertEquals(0, stopTime.stopSequence);
    }

    @Test
    void testFromJson_withPartialFields() {
        JSONObject json = new JSONObject();
        json.put("trip_id", "T2");

        StopTime stopTime = StopTime.fromJson(json);

        assertEquals("T2", stopTime.tripId);
        assertEquals("", stopTime.stopId);
        assertEquals(0, stopTime.stopSequence);
    }

    @Test
    void testFromJson_withNonIntegerStopSequence() {
        JSONObject json = new JSONObject();
        json.put("trip_id", "T3");
        json.put("stop_id", "S2");
        json.put("stop_sequence", "not_a_number"); // Invalid data type

        StopTime stopTime = StopTime.fromJson(json);

        assertEquals("T3", stopTime.tripId);
        assertEquals("S2", stopTime.stopId);
        assertEquals(0, stopTime.stopSequence); // Should default to 0 for invalid integer
    }
}
