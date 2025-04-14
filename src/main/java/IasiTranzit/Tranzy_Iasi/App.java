package IasiTranzit.Tranzy_Iasi;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import java.awt.EventQueue;
import com.formdev.flatlaf.FlatDarkLaf;

public class App {
/**
 * 
 * @param args
 * Folosire flatlat pentru design modern
 */
    public static void main(String[] args) {
        /**
         * 
         */
        try {
        	
        	
       
            UIManager.setLookAndFeel( new FlatDarkLaf() );
            UIManager.put( "TextComponent.arc", 10 );
            UIManager.put( "Button.arc", 10 );  

        } catch (UnsupportedLookAndFeelException e) {
           
             System.err.println( "Failed to initialize FlatLaf Look and Feel: " + e.getMessage() );
             e.printStackTrace();
        } catch( Exception ex ) {
            System.err.println( "Failed to initialize Look and Feel. Using default." );
            ex.printStackTrace();
        }
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
               
                    InterfataGrafica frame = new InterfataGrafica();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
