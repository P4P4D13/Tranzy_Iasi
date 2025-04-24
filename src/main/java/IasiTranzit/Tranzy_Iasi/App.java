package IasiTranzit.Tranzy_Iasi;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import java.awt.EventQueue;
import com.formdev.flatlaf.FlatDarkLaf;

public class App {
    /**
     * Main entry point for the application.
     * Sets the Look and Feel and launches the main interface.
     * @param args Command line arguments (not used).
     */
    public static void main(String[] args) {
        // Set FlatLaf Dark Look and Feel
        try {
            UIManager.setLookAndFeel( new FlatDarkLaf() );
            // Optional: Set rounded corners for components
            UIManager.put( "TextComponent.arc", 10 );
            UIManager.put( "Button.arc", 10 );

        } catch (UnsupportedLookAndFeelException e) {
            System.err.println( "Failed to initialize FlatLaf Look and Feel: " + e.getMessage() );
            e.printStackTrace();
             // Continue with default L&F if FlatLaf fails
        } catch( Exception ex ) {
            System.err.println( "Failed to initialize Look and Feel. Using default." );
            ex.printStackTrace();
        }

        // Launch the GUI on the Event Dispatch Thread (EDT)
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    InterfataGrafica frame = new InterfataGrafica();
                    frame.setVisible(true);
                } catch (Exception e) {
                    System.err.println("Error creating or showing the main application window:");
                    e.printStackTrace();
                }
            }
        });
    }
}