package IasiTranzit.Tranzy_Iasi;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JRadioButton;
import javax.swing.JComboBox;
import javax.swing.JCheckBox;
import javax.swing.JLabel;

public class InterfataGrafica extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField statie_plecare;
	private JTextField statie_sosire;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
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

	/**
	 * Create the frame.
	 */
	public InterfataGrafica() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu mnFont = new JMenu("Font");
		menuBar.add(mnFont);
		
		JRadioButton b12 = new JRadioButton("12");
		mnFont.add(b12);
		
		JRadioButton b14 = new JRadioButton("14");
		mnFont.add(b14);
		
		JRadioButton b16 = new JRadioButton("16");
		mnFont.add(b16);
		
		JMenu mnTheme = new JMenu("Theme");
		menuBar.add(mnTheme);
		
		JRadioButton rbLight = new JRadioButton("Light");
		mnTheme.add(rbLight);
		
		JRadioButton rbDark = new JRadioButton("Dark");
		mnTheme.add(rbDark);
		
		JMenuBar menuBar_1 = new JMenuBar();
		menuBar.add(menuBar_1);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JButton next = new JButton(">");
		next.setBounds(195, 161, 52, 27);
		contentPane.add(next);
		
		statie_plecare = new JTextField();
		statie_plecare.setBounds(161, 63, 96, 19);
		contentPane.add(statie_plecare);
		statie_plecare.setColumns(10);
		
		statie_sosire = new JTextField();
		statie_sosire.setColumns(10);
		statie_sosire.setBounds(161, 109, 96, 19);
		contentPane.add(statie_sosire);
		
		JLabel lbStart = new JLabel("Starting point:");
		lbStart.setBounds(75, 66, 76, 13);
		contentPane.add(lbStart);
		
		JLabel lbStop = new JLabel("Destination:");
		lbStop.setBounds(75, 110, 59, 16);
		contentPane.add(lbStop);
	}
}
