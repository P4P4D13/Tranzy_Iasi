package IasiTranzit.Tranzy_Iasi;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.formdev.flatlaf.FlatLightLaf;

/**
 * Clasa principala a aplicatiei Tranzy Iasi.
 * Contine metoda {@code main} care serveste ca punct de intrare in aplicatie.
 */

public class App {
	
	/**
     * Creeaza o noua instanța a clasei {@code App}.
     * Constructorul nu are functionalitate, deoarece clasa este utilizata doar pentru metoda {@code main}.
     */
    public App() {
        // Constructor gol
    }

    /** 
     * Metoda principala care porneste aplicatia.
     * Initializeaza tema vizuala FlatLaf Light si configureaza aspectul componentelor
     * grafice, apoi lanseaza interfata grafica {@link InterfataGrafica}
     *
     * @param args argumentele din linia de comanda (neutilizate)
     */
    public static void main(String[] args) {
        // Extrage datele transport într-un thread separat
        new Thread(() -> {
            TransportDataFetcher.fetchAllApiData();
        }).start();
    
        // Configureaza aspect UI
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
            UIManager.put("TextComponent.arc", 10);
            UIManager.put("Button.arc", 10);
        } catch (UnsupportedLookAndFeelException e) {
            System.err.println("Eroare inițializare FlatLaf:");
            e.printStackTrace();
        }

        // Porneste interfata grafica
        SwingUtilities.invokeLater(() -> {
            InterfataGrafica frame = new InterfataGrafica();
            frame.setVisible(true);
        });
    }

}
