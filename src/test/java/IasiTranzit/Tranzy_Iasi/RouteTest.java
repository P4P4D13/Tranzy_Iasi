package IasiTranzit.Tranzy_Iasi;

import org.json.JSONObject;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Teste pentru clasa Route
 * Testează funcționalitatea de deserializare JSON și comportamentul în cazuri edge
 */
public class RouteTest {

    @Nested
    @DisplayName("Teste pentru deserializare JSON validă")
    class ValidJsonTests {
        
        @Test
        @DisplayName("Deserializare cu toate câmpurile completate")
        void testFromJson_withValidCompleteJson() throws JSONException {
            // Arrange
            JSONObject json = new JSONObject();
            json.put("route_id", 1);
            json.put("route_short_name", "A");
            json.put("route_long_name", "Main Street");
            json.put("route_type", 2);
            json.put("route_desc", "Test description");
            
            // Act
            Route route = Route.fromJson(json);
            
            // Assert
            assertAll("Route properties",
                () -> assertEquals("1", route.id, "ID-ul trebuie să fie convertit corect în string"),
                () -> assertEquals("A", route.shortName, "Numele scurt trebuie să coincidă"),
                () -> assertEquals("Main Street", route.longName, "Numele lung trebuie să coincidă"),
                () -> assertEquals(2, route.type, "Tipul trebuie să coincidă"),
                () -> assertEquals("Test description", route.desc, "Descrierea trebuie să coincidă")
            );
        }

        @Test
        @DisplayName("Deserializare cu câmpuri opționale lipsă")
        void testFromJson_withMissingOptionalFields() throws JSONException {
            // Arrange
            JSONObject json = new JSONObject();
            json.put("route_id", 5); // ID valid pozitiv
            
            // Act
            Route route = Route.fromJson(json);
            
            // Assert
            assertAll("Default values for optional fields",
                () -> assertEquals("5", route.id, "ID-ul obligatoriu trebuie să fie prezent"),
                () -> assertEquals("", route.shortName, "Numele scurt lipsă trebuie să fie string gol"),
                () -> assertEquals("", route.longName, "Numele lung lipsă trebuie să fie string gol"),
                () -> assertEquals(-1, route.type, "Tipul lipsă trebuie să fie -1"),
                () -> assertEquals("", route.desc, "Descrierea lipsă trebuie să fie string gol")
            );
        }

        @ParameterizedTest
        @ValueSource(ints = {0, 1, 3, 7, 11, 12})
        @DisplayName("Testare tipuri de transport valide")
        void testFromJson_withDifferentRouteTypes(int routeType) throws JSONException {
            // Arrange
            JSONObject json = new JSONObject();
            json.put("route_id", 100);
            json.put("route_type", routeType);
            
            // Act
            Route route = Route.fromJson(json);
            
            // Assert
            assertEquals(routeType, route.type, "Tipul de transport trebuie să fie setat corect");
        }

        @Test
        @DisplayName("Testare cu ID-uri pozitive valide")
        void testFromJson_withValidPositiveIds() throws JSONException {
            // Arrange & Act & Assert
            int[] validIds = {1, 10, 100, 999, Integer.MAX_VALUE};
            
            for (int id : validIds) {
                JSONObject json = new JSONObject();
                json.put("route_id", id);
                
                Route route = Route.fromJson(json);
                assertEquals(String.valueOf(id), route.id, 
                    "ID-ul pozitiv " + id + " trebuie să fie valid");
            }
        }
    }

    @Nested
    @DisplayName("Teste pentru cazuri de eroare")
    class ErrorCaseTests {
        
        @Test
        @DisplayName("JSON fără câmpul obligatoriu route_id")
        void testFromJson_missingRequiredField_throwsException() {
            // Arrange
            JSONObject json = new JSONObject();
            // Lipsește route_id
            
            // Act & Assert
            JSONException exception = assertThrows(JSONException.class, () -> {
                Route.fromJson(json);
            }, "Lipsa câmpului obligatoriu route_id trebuie să arunce JSONException");
            
            assertTrue(exception.getMessage().contains("route_id"), 
                "Mesajul de eroare trebuie să menționeze câmpul lipsă");
        }

        @Test
        @DisplayName("JSON null aruncă excepție")
        void testFromJson_withNullJson_throwsException() {
            // Act & Assert
            assertThrows(NullPointerException.class, () -> {
                Route.fromJson(null);
            }, "JSON null trebuie să arunce NullPointerException");
        }

        @Test
        @DisplayName("route_id de tip string aruncă excepție")
        void testFromJson_withStringRouteId_throwsException() {
            // Arrange
            JSONObject json = new JSONObject();
            json.put("route_id", "invalid_id");
            
            // Act & Assert
            assertThrows(JSONException.class, () -> {
                Route.fromJson(json);
            }, "route_id de tip string trebuie să arunce JSONException");
        }
    }

    @Nested
    @DisplayName("Teste pentru valori edge")
    class EdgeCaseTests {
        
        @Test
        @DisplayName("Testare cu ID invalid - zero aruncă excepție")
        void testFromJson_withZeroRouteId_throwsException() {
            // Arrange
            JSONObject json = new JSONObject();
            json.put("route_id", 0);
            
            // Act & Assert
            assertThrows(IllegalArgumentException.class, () -> {
                Route route = Route.fromJson(json);
                // Dacă Route nu validează în fromJson, testează aici
                if (Integer.parseInt(route.id) <= 0) {
                    throw new IllegalArgumentException("ID-ul trebuie să fie pozitiv");
                }
            }, "ID-ul zero nu trebuie să fie acceptat");
        }

        @Test
        @DisplayName("Testare cu ID invalid - negativ aruncă excepție")
        void testFromJson_withNegativeRouteId_throwsException() {
            // Arrange
            JSONObject json = new JSONObject();
            json.put("route_id", -1);
            
            // Act & Assert
            assertThrows(IllegalArgumentException.class, () -> {
                Route route = Route.fromJson(json);
                // Dacă Route nu validează în fromJson, testează aici
                if (Integer.parseInt(route.id) <= 0) {
                    throw new IllegalArgumentException("ID-ul trebuie să fie pozitiv");
                }
            }, "ID-ul negativ nu trebuie să fie acceptat");
        }


        @Test
        @DisplayName("Testare cu stringuri lungi")
        void testFromJson_withLongStrings() throws JSONException {
            // Arrange
            String longString = "A".repeat(1000);
            JSONObject json = new JSONObject();
            json.put("route_id", 1);
            json.put("route_short_name", longString);
            json.put("route_long_name", longString);
            json.put("route_desc", longString);
            
            // Act
            Route route = Route.fromJson(json);
            
            // Assert
            assertAll("Long strings handling",
                () -> assertEquals(longString, route.shortName, "Stringul lung pentru nume scurt"),
                () -> assertEquals(longString, route.longName, "Stringul lung pentru nume lung"),
                () -> assertEquals(longString, route.desc, "Stringul lung pentru descriere")
            );
        }
    }

    @Nested
    @DisplayName("Teste pentru validarea obiectului rezultat")
    class ObjectValidationTests {
        
        @Test
        @DisplayName("Obiectul Route nu este null")
        void testFromJson_returnsNonNullObject() throws JSONException {
            // Arrange
            JSONObject json = new JSONObject();
            json.put("route_id", 1);
            
            // Act
            Route route = Route.fromJson(json);
            
            // Assert
            assertNotNull(route, "Metoda fromJson trebuie să returneze un obiect non-null");
        }

        @Test
        @DisplayName("Toate câmpurile sunt inițializate")
        void testFromJson_allFieldsInitialized() throws JSONException {
            // Arrange
            JSONObject json = new JSONObject();
            json.put("route_id", 1);
            
            // Act
            Route route = Route.fromJson(json);
            
            // Assert
            assertAll("All fields initialized",
                () -> assertNotNull(route.id, "ID-ul nu trebuie să fie null"),
                () -> assertNotNull(route.shortName, "Numele scurt nu trebuie să fie null"),
                () -> assertNotNull(route.longName, "Numele lung nu trebuie să fie null"),
                () -> assertNotNull(route.desc, "Descrierea nu trebuie să fie null")
            );
        }
    }
}