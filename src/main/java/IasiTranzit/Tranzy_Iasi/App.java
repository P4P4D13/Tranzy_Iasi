package IasiTranzit.Tranzy_Iasi;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.formdev.flatlaf.FlatLightLaf;

public class App {

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
            TransportDataFetcher.fetchAllApiData();;
        }).start();
    
        // Configurează aspect UI
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
            UIManager.put("TextComponent.arc", 10);
            UIManager.put("Button.arc", 10);
        } catch (UnsupportedLookAndFeelException e) {
            System.err.println("Eroare inițializare FlatLaf:");
            e.printStackTrace();
        }

        // Pornește interfața grafică
        SwingUtilities.invokeLater(() -> {
            InterfataGrafica frame = new InterfataGrafica();
            frame.setVisible(true);
        });
    }

}
