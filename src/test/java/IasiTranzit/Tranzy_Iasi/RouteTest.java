package IasiTranzit.Tranzy_Iasi;

import org.json.JSONObject;
import org.json.JSONException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RouteTest {

    @Test
    void testFromJson_withValidJson() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("route_id", 1);
        json.put("route_short_name", "A");
        json.put("route_long_name", "Main Street");
        json.put("route_color", "123456");
        json.put("route_type", 2);
        json.put("route_desc", "Test description");

        Route route = Route.fromJson(json);

        assertEquals("1", route.id);
        assertEquals("A", route.shortName);
        assertEquals("Main Street", route.longName);
        assertEquals("123456", route.color);
        assertEquals(2, route.type);
        assertEquals("Test description", route.desc);
    }

    @Test
    void testFromJson_withMissingOptionalFields() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("route_id", 2);

        Route route = Route.fromJson(json);

        assertEquals("2", route.id);
        assertEquals("", route.shortName);
        assertEquals("", route.longName);
        assertEquals("FFFFFF", route.color);
        assertEquals(-1, route.type);
        assertEquals("", route.desc);
    }

    @Test
    void testFromJson_missingRequiredField_throwsException() {
        JSONObject json = new JSONObject(); // missing route_id

        assertThrows(JSONException.class, () -> {
            Route.fromJson(json);
        });
    }
}
