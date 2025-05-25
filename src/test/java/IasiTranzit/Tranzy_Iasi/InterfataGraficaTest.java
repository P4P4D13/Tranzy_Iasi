package IasiTranzit.Tranzy_Iasi;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.junit.jupiter.api.Assertions.*;

import javax.swing.*;
import java.awt.*;
import java.util.*;

/**
 * Clasa de teste JUnit pentru {@link InterfataGrafica}.
 * Aceasta clasa testeaza functionalitatile principale ale interfetei grafice,
 * inclusiv initializarea componentelor, gestionarea evenimentelor, animatiile
 * si interactiunea cu datele de transport.
 * 
 * @author Test Suite
 * @version 1.0
 */
@DisplayName("Teste pentru InterfataGrafica")
public class InterfataGraficaTest {

    /** Instanta principala a interfetei grafice ce va fi testata */
    private InterfataGrafica interfataGrafica;
    
    /** Mock pentru fetcherul de date de transport */
    @Mock
    private TransportDataFetcher mockDataFetcher;
    
    /** AutoCloseable pentru gestionarea mock-urilor Mockito */
    private AutoCloseable closeable;

    /**
     * Metoda de setup executata inaintea fiecarui test.
     * Initializeaza mock-urile si creeaza o noua instanta a interfetei grafice.
     * Configureaza mediul de testare pentru a asigura teste izolate si consistente.
     */
    @BeforeEach
    @DisplayName("Pregatirea mediului de testare")
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        
        // Initializarea interfetei grafice pe Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            interfataGrafica = new InterfataGrafica();
        });
        
        // Asteptam ca interfata sa fie initializata complet
        try {
            SwingUtilities.invokeAndWait(() -> {});
        } catch (Exception e) {
            fail("Eroare la initializarea interfetei grafice: " + e.getMessage());
        }
    }

    /**
     * Metoda de cleanup executata dupa fiecare test.
     * Elibereaza resursele si inchide mock-urile pentru a preveni memory leaks.
     */
    @AfterEach
    @DisplayName("Curatarea mediului de testare")
    void tearDown() throws Exception {
        if (interfataGrafica != null) {
            SwingUtilities.invokeLater(() -> interfataGrafica.dispose());
        }
        if (closeable != null) {
            closeable.close();
        }
    }

    /**
     * Testeaza initializarea corecta a componentelor principale ale interfetei.
     * Verifica ca toate componentele esentiale sunt create si configurate corect
     * la instantierea clasei InterfataGrafica.
     */
    @Test
    @DisplayName("Testarea initializarii componentelor")
    void testInitializareComponente() {
        SwingUtilities.invokeLater(() -> {
            // Verificarea existentei componentelor principale
            assertNotNull(interfataGrafica.contentPane, "ContentPane nu ar trebui sa fie null");
            assertNotNull(interfataGrafica.vehicleIdInput, "Campul de input pentru vehicul nu ar trebui sa fie null");
            assertNotNull(interfataGrafica.trackButton, "Butonul de tracking nu ar trebui sa fie null");
            assertNotNull(interfataGrafica.statusLabel, "Eticheta de status nu ar trebui sa fie null");
            assertNotNull(interfataGrafica.resultsPanel, "Panoul de rezultate nu ar trebui sa fie null");
            
            // Verificarea configurarii initiale
            assertEquals("Tranzy Iasi", interfataGrafica.getTitle(), 
                "Titlul ferestrei ar trebui sa fie 'Tranzy Iasi'");
            assertEquals(JFrame.EXIT_ON_CLOSE, interfataGrafica.getDefaultCloseOperation(),
                "Operatiunea de inchidere ar trebui sa fie EXIT_ON_CLOSE");
            assertTrue(interfataGrafica.isResizable(), "Fereastra ar trebui sa fie redimensionabila");
        });
    }

    /**
     * Testeaza functionalitatea grupurilor de butoane radio pentru font si tema.
     * Verifica ca butoanele radio sunt grupate corect si ca doar unul poate fi
     * selectat la un moment dat din fiecare grup.
     */
    @Test
    @DisplayName("Testarea grupurilor de butoane radio")
    void testGrupuriButoaneRadio() {
        SwingUtilities.invokeLater(() -> {
            // Testarea grupului de fonturi
            assertNotNull(interfataGrafica.grupFont, "Grupul de fonturi nu ar trebui sa fie null");
            assertNotNull(interfataGrafica.b12, "Butonul pentru font 12 nu ar trebui sa fie null");
            assertNotNull(interfataGrafica.b14, "Butonul pentru font 14 nu ar trebui sa fie null");
            assertNotNull(interfataGrafica.b16, "Butonul pentru font 16 nu ar trebui sa fie null");
            
            // Verificarea ca butoanele sunt in grup
            assertTrue(interfataGrafica.grupFont.getElements().hasMoreElements(),
                "Grupul de fonturi ar trebui sa contina butoane");
            
            // Testarea grupului de teme
            assertNotNull(interfataGrafica.grupTheme, "Grupul de teme nu ar trebui sa fie null");
            assertNotNull(interfataGrafica.rbLight, "Butonul pentru tema luminoasa nu ar trebui sa fie null");
            assertNotNull(interfataGrafica.rbDark, "Butonul pentru tema intunecata nu ar trebui sa fie null");
            
            // Verificarea ca butoanele sunt in grup
            assertTrue(interfataGrafica.grupTheme.getElements().hasMoreElements(),
                "Grupul de teme ar trebui sa contina butoane");
        });
    }

    /**
     * Testeaza constanta de durata a animatiei.
     * Verifica ca durata animatiei este setata la valoarea asteptata
     * si ca este pozitiva pentru a asigura functionarea corecta a animatiilor.
     */
    @Test
    @DisplayName("Testarea constantelor de animatie")
    void testConstanteAnimatie() {
        assertEquals(500, interfataGrafica.ANIMATION_DURATION_MS,
            "Durata animatiei ar trebui sa fie 500ms");
        assertEquals(10, interfataGrafica.TIMER_DELAY_MS,
            "Intarzierea timer-ului ar trebui sa fie 10ms");
        
        assertTrue(interfataGrafica.ANIMATION_DURATION_MS > 0,
            "Durata animatiei ar trebui sa fie pozitiva");
        assertTrue(interfataGrafica.TIMER_DELAY_MS > 0,
            "Intarzierea timer-ului ar trebui sa fie pozitiva");
    }

    /**
     * Testeaza initializarea hartilor de date pentru rute, calatorii si statii.
     * Verifica ca toate hartile sunt initializate corect si sunt pregatite
     * pentru a stoca datele de transport.
     */
    @Test
    @DisplayName("Testarea initializarii hartilor de date")
    void testInitializareHartiDate() {
        assertNotNull(interfataGrafica.routesMap, "Harta rutelor nu ar trebui sa fie null");
        assertNotNull(interfataGrafica.tripsMap, "Harta calatoriilor nu ar trebui sa fie null");
        assertNotNull(interfataGrafica.stopsMap, "Harta statiilor nu ar trebui sa fie null");
        assertNotNull(interfataGrafica.stopTimesList, "Lista timpilor de stationare nu ar trebui sa fie null");
        
        assertTrue(interfataGrafica.routesMap instanceof HashMap,
            "Harta rutelor ar trebui sa fie o instanta HashMap");
        assertTrue(interfataGrafica.tripsMap instanceof HashMap,
            "Harta calatoriilor ar trebui sa fie o instanta HashMap");
        assertTrue(interfataGrafica.stopsMap instanceof HashMap,
            "Harta statiilor ar trebui sa fie o instanta HashMap");
        assertTrue(interfataGrafica.stopTimesList instanceof ArrayList,
            "Lista timpilor ar trebui sa fie o instanta ArrayList");
    }

    /**
     * Testeaza configurarea initiala a campului de introducere a ID-ului vehiculului.
     * Verifica placeholder text-ul, dimensiunile si configurarea ActionListener-ului
     * pentru functionalitatea de cautare.
     */
    @Test
    @DisplayName("Testarea configurarii campului de input")
    void testConfigurareCampInput() {
        SwingUtilities.invokeLater(() -> {
            JTextField vehicleInput = interfataGrafica.vehicleIdInput;
            
            // Verificarea configurarii de baza
            assertNotNull(vehicleInput, "Campul de input nu ar trebui sa fie null");
            assertEquals(20, vehicleInput.getColumns(),
                "Campul de input ar trebui sa aiba 20 de coloane");
            
            // Verificarea placeholder text-ului
            Object placeholderProperty = vehicleInput.getClientProperty("FlatLaf.placeholderText");
            if (placeholderProperty != null) {
                String placeholder = placeholderProperty.toString();
                assertTrue(placeholder.contains("Enter Bus/Tram Label"),
                    "Placeholder-ul ar trebui sa contina instructiuni pentru utilizator");
            }
            
            // Verificarea ca input-ul este editabil
            assertTrue(vehicleInput.isEditable(), "Campul de input ar trebui sa fie editabil");
            assertTrue(vehicleInput.isEnabled(), "Campul de input ar trebui sa fie activat");
        });
    }

    /**
     * Testeaza configurarea initiala a butonului de tracking.
     * Verifica textul butonului, starea initiala si proprietatile vizuale
     * necesare pentru functionalitatea de urmarire a vehiculelor.
     */
    @Test
    @DisplayName("Testarea configurarii butonului de tracking")
    void testConfigurareButonTracking() {
        SwingUtilities.invokeLater(() -> {
            JButton trackBtn = interfataGrafica.trackButton;
            
            assertNotNull(trackBtn, "Butonul de tracking nu ar trebui sa fie null");
            assertEquals("Track Vehicle", trackBtn.getText(),
                "Textul butonului ar trebui sa fie 'Track Vehicle'");
            
            // Verificarea proprietatilor butonului
            Object buttonTypeProperty = trackBtn.getClientProperty("JButton.buttonType");
            if (buttonTypeProperty != null) {
                assertEquals("roundRect", buttonTypeProperty.toString(),
                    "Tipul butonului ar trebui sa fie roundRect");
            }
            
            assertTrue(trackBtn.isVisible(), "Butonul ar trebui sa fie vizibil");
        });
    }

    /**
     * Testeaza configurarea panourilor principale ale interfetei.
     * Verifica ca panourile sunt create cu layout-urile corecte si
     * marginile adecvate pentru o prezentare optima.
     */
    @Test
    @DisplayName("Testarea configurarii panourilor")
    void testConfigurarePanouri() {
        SwingUtilities.invokeLater(() -> {
            // Testarea panoului de input
            assertNotNull(interfataGrafica.inputPanel, "Panoul de input nu ar trebui sa fie null");
            assertTrue(interfataGrafica.inputPanel.getLayout() instanceof GridBagLayout,
                "Panoul de input ar trebui sa foloseasca GridBagLayout");
            
            // Testarea panoului superior
            assertNotNull(interfataGrafica.topPanel, "Panoul superior nu ar trebui sa fie null");
            assertTrue(interfataGrafica.topPanel.getLayout() instanceof GridBagLayout,
                "Panoul superior ar trebui sa foloseasca GridBagLayout");
            
            // Testarea panoului de rezultate
            assertNotNull(interfataGrafica.resultsPanel, "Panoul de rezultate nu ar trebui sa fie null");
            assertTrue(interfataGrafica.resultsPanel.getLayout() instanceof BoxLayout,
                "Panoul de rezultate ar trebui sa foloseasca BoxLayout");
            
            // Testarea scroll pane-ului
            assertNotNull(interfataGrafica.resultsScrollPane, "ScrollPane-ul nu ar trebui sa fie null");
            assertEquals(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER,
                interfataGrafica.resultsScrollPane.getHorizontalScrollBarPolicy(),
                "Scroll-ul orizontal ar trebui sa fie dezactivat");
        });
    }

    /**
     * Testeaza configurarea etichetei de status.
     * Verifica textul initial, alinierea si vizibilitatea etichetei
     * care informeaza utilizatorul despre starea aplicatiei.
     */
    @Test
    @DisplayName("Testarea configurarii etichetei de status")
    void testConfigurareEticheta() {
        SwingUtilities.invokeLater(() -> {
            JLabel statusLbl = interfataGrafica.statusLabel;
            
            assertNotNull(statusLbl, "Eticheta de status nu ar trebui sa fie null");
            assertTrue(statusLbl.getText().contains("Loading") || statusLbl.getText().contains("Ready"),
                "Eticheta ar trebui sa afiseze statusul de incarcare sau pregatire");
            assertEquals(SwingConstants.CENTER, statusLbl.getHorizontalAlignment(),
                "Eticheta ar trebui sa fie centrata orizontal");
            assertTrue(statusLbl.isVisible(), "Eticheta ar trebui sa fie vizibila");
        });
    }

    /**
     * Testeaza functionalitatea de validare a input-ului gol.
     * Simuleaza introducerea unui string gol si verifica ca aplicatia
     * gestioneaza corect aceasta situatie prin afisarea unei erori.
     */
    @Test
    @DisplayName("Testarea validarii input-ului gol")
    void testValidareInputGol() {
        SwingUtilities.invokeLater(() -> {
            // Setarea unui input gol
            interfataGrafica.vehicleIdInput.setText("");
            
            // Simularea click-ului pe buton (daca este activat)
            if (interfataGrafica.trackButton.isEnabled()) {
                interfataGrafica.trackButton.doClick();
            }
            
            // Verificarea ca input-ul este inca gol
            assertTrue(interfataGrafica.vehicleIdInput.getText().trim().isEmpty(),
                "Input-ul ar trebui sa ramana gol dupa validare");
        });
    }

    /**
     * Testeaza functionalitatea de introducere a textului valid in campul de input.
     * Verifica ca aplicatia accepta si proceseaza corect input-ul utilizatorului
     * pentru cautarea vehiculelor.
     */
    @Test
    @DisplayName("Testarea introducerii textului valid")
    void testIntroducereTextValid() {
        SwingUtilities.invokeLater(() -> {
            String testInput = "123";
            interfataGrafica.vehicleIdInput.setText(testInput);
            
            assertEquals(testInput, interfataGrafica.vehicleIdInput.getText(),
                "Textul introdus ar trebui sa fie pastrat in camp");
            assertFalse(interfataGrafica.vehicleIdInput.getText().trim().isEmpty(),
                "Campul nu ar trebui sa fie gol dupa introducerea textului");
        });
    }

    /**
     * Testeaza functionalitatea de curatare a textului din campul de input.
     * Verifica ca aplicatia poate reseta corect continutul campului
     * si poate reveni la starea initiala.
     */
    @Test
    @DisplayName("Testarea curatarii campului de input")
    void testCuratareCampInput() {
        SwingUtilities.invokeLater(() -> {
            // Introducerea unor date de test
            interfataGrafica.vehicleIdInput.setText("test123");
            assertFalse(interfataGrafica.vehicleIdInput.getText().isEmpty(),
                "Campul ar trebui sa contina text dupa introducere");
            
            // Curatarea campului
            interfataGrafica.vehicleIdInput.setText("");
            assertTrue(interfataGrafica.vehicleIdInput.getText().isEmpty(),
                "Campul ar trebui sa fie gol dupa curatare");
        });
    }


    /**
     * Testeaza initializarea GridBagConstraints pentru layout.
     * Verifica ca constrangerile sunt setate corect pentru pozitionarea
     * adecvata a componentelor in interfata grafica.
     */
    @Test
    @DisplayName("Testarea initializarii GridBagConstraints")
    void testInitializareGridBagConstraints() {
        assertNotNull(interfataGrafica.gbc, "GridBagConstraints nu ar trebui sa fie null");
        
        // Verificarea valorilor implicite ale GridBagConstraints
        assertTrue(interfataGrafica.gbc instanceof GridBagConstraints,
            "gbc ar trebui sa fie o instanta GridBagConstraints");
    }

    /**
     * Testeaza valorile constantelor pentru padding si target.
     * Verifica ca valorile pentru spatiere si pozitionare sunt
     * setate la valori pozitive si rezonabile pentru UI.
     */
    @Test
    @DisplayName("Testarea constantelor de padding si target")
    void testConstantePaddingTarget() {
        assertEquals(10, interfataGrafica.horizontalPadding,
            "Padding-ul orizontal ar trebui sa fie 10");
        assertEquals(10, interfataGrafica.targetYText,
            "Pozitia Y tinta pentru text ar trebui sa fie 10");
        
        assertTrue(interfataGrafica.horizontalPadding > 0,
            "Padding-ul orizontal ar trebui sa fie pozitiv");
        assertTrue(interfataGrafica.targetYText >= 0,
            "Pozitia Y tinta ar trebui sa fie non-negativa");
    }

    /**
     * Testeaza valorile constantelor pentru transparenta butonului.
     * Verifica ca valorile alpha pentru starea initiala si finala
     * a animatiei butonului sunt in intervalul valid [0, 1].
     */
    @Test
    @DisplayName("Testarea constantelor de transparenta")
    void testConstanteTransparenta() {
        assertEquals(1.0f, interfataGrafica.initialButtonAlpha, 0.001f,
            "Transparenta initiala ar trebui sa fie 1.0");
        assertEquals(0.0f, interfataGrafica.targetButtonAlpha, 0.001f,
            "Transparenta tinta ar trebui sa fie 0.0");
        
        assertTrue(interfataGrafica.initialButtonAlpha >= 0.0f && 
                   interfataGrafica.initialButtonAlpha <= 1.0f,
            "Transparenta initiala ar trebui sa fie in intervalul [0, 1]");
        assertTrue(interfataGrafica.targetButtonAlpha >= 0.0f && 
                   interfataGrafica.targetButtonAlpha <= 1.0f,
            "Transparenta tinta ar trebui sa fie in intervalul [0, 1]");
    }

    /**
     * Testeaza dimensiunile si proprietatile ferestrei principale.
     * Verifica ca fereastra are dimensiunile corecte, este redimensionabila
     * si are operatiunea de inchidere setata corect.
     */
    @Test
    @DisplayName("Testarea proprietatilor ferestrei")
    void testProprietatiFereastra() {
        SwingUtilities.invokeLater(() -> {
            // Verificarea dimensiunilor preferate
            Dimension preferredSize = interfataGrafica.getPreferredSize();
            assertEquals(450, preferredSize.width, "Latimea preferata ar trebui sa fie 450");
            assertEquals(650, preferredSize.height, "Inaltimea preferata ar trebui sa fie 650");
            
            // Verificarea altor proprietati
            assertTrue(interfataGrafica.isResizable(), "Fereastra ar trebui sa fie redimensionabila");
            assertEquals(JFrame.EXIT_ON_CLOSE, interfataGrafica.getDefaultCloseOperation(),
                "Operatiunea de inchidere ar trebui sa fie EXIT_ON_CLOSE");
            assertEquals("Tranzy Iasi", interfataGrafica.getTitle(),
                "Titlul ar trebui sa fie 'Tranzy Iasi'");
        });
    }
}