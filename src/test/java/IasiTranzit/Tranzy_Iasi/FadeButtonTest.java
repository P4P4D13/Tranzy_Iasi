package IasiTranzit.Tranzy_Iasi;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.Dimension;

public class FadeButtonTest {

    private FadeButton button;

    @BeforeEach
    void setUp() {
        button = new FadeButton("Click me");
    }

    @Test
    void testInitialAlpha() {
        assertEquals(1.0f, button.getAlpha(), 0.001);
    }

    @Test
    void testSetAlphaClampsCorrectly() {
        button.setAlpha(0.5f);
        assertEquals(0.5f, button.getAlpha(), 0.001);

        button.setAlpha(-1.0f);
        assertEquals(0.0f, button.getAlpha(), 0.001);

        button.setAlpha(1.5f);
        assertEquals(1.0f, button.getAlpha(), 0.001);
    }

    @Test
    void testContainsWhenAlphaLow() {
        button.setAlpha(0.05f);
        // Should return false for any point since alpha < 0.1
        assertFalse(button.contains(10, 10));
    }

    @Test
    void testContainsWhenAlphaHigh() {
        button.setAlpha(1.0f);
        // May depend on button size — we simulate a click inside a default-size button
        button.setSize(100, 50);
        assertTrue(button.contains(10, 10));
    }

    @Test
    void testGetPreferredSizeNotNull() {
        Dimension size = button.getPreferredSize();
        assertNotNull(size);
        assertTrue(size.width > 0);
        assertTrue(size.height > 0);
    }
}
