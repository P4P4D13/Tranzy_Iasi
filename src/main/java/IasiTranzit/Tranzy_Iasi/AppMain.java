package IasiTranzit.Tranzy_Iasi;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.formdev.flatlaf.FlatLightLaf;

public class AppMain {
    /** 
     * Metoda principala care porneste aplicatia.
     * Initializeaza tema vizuala FlatLaf Light si configureaza aspectul componentelor
     * grafice, apoi lanseaza interfata grafica {@link InterfataGrafica}
     *
     * @param args argumentele din linia de comanda (neutilizate)
     */
	public static void main(String[] args) {
		Date_agentie.main(args);
		Date_rute.main(args);
		Date_stop_times.main(args);
		Date_stops.main(args);
		Date_trips.main(args);
		Date_vehicule.main(args);
		try {
			UIManager.setLookAndFeel(new FlatLightLaf());
			UIManager.put( "TextComponent.arc", 10 );
			UIManager.put( "Button.arc", 10 );
		} catch (UnsupportedLookAndFeelException e) {
			System.err.println("FlatLaf Light initialization failed:");
			e.printStackTrace();
		}

		SwingUtilities.invokeLater(() -> {
			InterfataGrafica frame = new InterfataGrafica();
			frame.setVisible(true);
		});
	}
}

