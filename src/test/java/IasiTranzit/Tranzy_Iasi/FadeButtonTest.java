package IasiTranzit.Tranzy_Iasi;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.Dimension;

/**
 * Clasa de test JUnit pentru verificarea funcționalității butonului FadeButton.
 */
public class FadeButtonTest {

    private FadeButton button;

    /**
     * Inițializarea obiectului FadeButton înainte de fiecare test.
     */
    @BeforeEach
    void setUp() {
        button = new FadeButton("Click me");
    }

    /**
     * Testează dacă valoarea inițială a lui alpha este 1.0.
     */
    @Test
    void testInitialAlpha() {
        assertEquals(1.0f, button.getAlpha(), 0.001);
    }

    /**
     * Verifică dacă metoda setAlpha funcționează corect, limitând valoarea între 0.0 și 1.0.
     */
    @Test
    void testSetAlphaClampsCorrectly() {
        button.setAlpha(0.5f);
        assertEquals(0.5f, button.getAlpha(), 0.001);

        button.setAlpha(-1.0f);
        assertEquals(0.0f, button.getAlpha(), 0.001);

        button.setAlpha(1.5f);
        assertEquals(1.0f, button.getAlpha(), 0.001);
    }

    /**
     * Testează dacă metoda contains returnează false atunci când valoarea alpha este prea mică.
     */
    @Test
    void testContainsWhenAlphaLow() {
        button.setAlpha(0.05f);
        assertFalse(button.contains(10, 10));
    }

    /**
     * Testează dacă metoda contains returnează true atunci când valoarea alpha este maximă.
     */
    @Test
    void testContainsWhenAlphaHigh() {
        button.setAlpha(1.0f);
        button.setSize(100, 50); // Setăm o dimensiune pentru buton
        assertTrue(button.contains(10, 10));
    }

    /**
     * Verifică dacă metoda getPreferredSize returnează o dimensiune validă.
     */
    @Test
    void testGetPreferredSizeNotNull() {
        Dimension size = button.getPreferredSize();
        assertNotNull(size);
        assertTrue(size.width > 0);
        assertTrue(size.height > 0);
    }
}
