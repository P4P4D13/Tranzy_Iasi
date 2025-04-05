package IasiTranzit.Tranzy_Iasi;

<<<<<<< HEAD
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
=======
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
>>>>>>> adaugare_animatie
import javax.swing.border.EmptyBorder;

public class InterfataGrafica extends JFrame {

<<<<<<< HEAD
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;

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
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
	}

}
=======
    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTextField statie_plecare;
    private JTextField statie_sosire;
    private final ButtonGroup grupFont = new ButtonGroup();
    private final ButtonGroup grupTheme = new ButtonGroup();

    private JButton nextButton;
    private JRadioButton b12, b14, b16;
    private JRadioButton rbLight, rbDark;

    // --- Animation Variables ---
    private Timer animationTimer;
    private final int ANIMATION_DURATION_MS = 500; // Keep it slow
    private final int TIMER_DELAY_MS = 10;
    private long animationStartTime;
    private Point initialPosPlecare, initialPosSosire;
    private Dimension initialSizePlecare, initialSizeSosire; // Store initial size
    private int targetYText = 10;
    private int targetWidth; // Target width for text fields
    private int horizontalPadding = 10; // Padding from panel edges for target width
    private float initialButtonAlpha = 1.0f;
    private float targetButtonAlpha = 0.0f;
    private LayoutManager originalInputLayout;
    private JPanel inputPanel;

    // --- Results Panel ---
    private JPanel resultsPanel;

    public InterfataGrafica() {
        setTitle("Tranzy Iasi");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(350, 500));
        setResizable(false);

        setupMenuBar();

        contentPane = new JPanel(new BorderLayout());
        setContentPane(contentPane);

        inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBorder(new EmptyBorder(30, 30, 30, 30));
        contentPane.add(inputPanel, BorderLayout.CENTER);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 5, 10, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        statie_plecare = new JTextField(20);
        statie_plecare.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Starting point");
        gbc.gridx = 0; gbc.gridy = 0;
        inputPanel.add(statie_plecare, gbc);

        statie_sosire = new JTextField(20);
        statie_sosire.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Destination");
        gbc.gridx = 0; gbc.gridy = 1;
        inputPanel.add(statie_sosire, gbc);

        nextButton = new FadeButton("Find Route");
        nextButton.putClientProperty("JButton.buttonType", "roundRect");
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.ipady = 10;
        gbc.insets = new Insets(20, 5, 10, 5);
        inputPanel.add(nextButton, gbc);

        resultsPanel = new JPanel();
        resultsPanel.add(new JLabel("Search results will appear here..."));

        nextButton.addActionListener(e -> startAnimation());

        pack();
        setLocationRelativeTo(null);
    }

    private float easeOutSine(float t) {
        return (float) Math.sin(t * Math.PI / 2.0);
    }

    private void startAnimation() {
        String start = statie_plecare.getText().trim();
        String destination = statie_sosire.getText().trim();

        if (start.isEmpty() || destination.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both starting point and destination.", "Input Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        nextButton.setEnabled(false);

        // Ensure sizes are calculated based on current layout before switching
        inputPanel.revalidate(); // Make sure layout is calculated

        // Store initial positions and sizes relative to inputPanel
        initialPosPlecare = statie_plecare.getLocation();
        initialPosSosire = statie_sosire.getLocation();
        initialSizePlecare = statie_plecare.getSize();
        initialSizeSosire = statie_sosire.getSize();

        // Calculate target width based on the inputPanel's current width
        targetWidth = inputPanel.getWidth() - (2 * horizontalPadding);

        // --- Temporarily use Null Layout ---
        originalInputLayout = inputPanel.getLayout();
        inputPanel.setLayout(null);

        // Set initial bounds explicitly for null layout
        statie_plecare.setBounds(initialPosPlecare.x, initialPosPlecare.y, initialSizePlecare.width, initialSizePlecare.height);
        statie_sosire.setBounds(initialPosSosire.x, initialPosSosire.y, initialSizeSosire.width, initialSizeSosire.height);
        // Button position might shift slightly when layout changes, reposition it
        Point initialPosButton = nextButton.getLocation();
        Dimension initialSizeButton = nextButton.getSize();
        nextButton.setBounds(initialPosButton.x, initialPosButton.y, initialSizeButton.width, initialSizeButton.height);

        // Re-add components (might not be strictly necessary but safe)
        inputPanel.add(statie_plecare);
        inputPanel.add(statie_sosire);
        inputPanel.add(nextButton);
        inputPanel.revalidate();
        inputPanel.repaint();
        // --- End Null Layout Setup ---

        animationStartTime = System.currentTimeMillis();

        if (animationTimer != null && animationTimer.isRunning()) {
            animationTimer.stop();
        }

        animationTimer = new Timer(TIMER_DELAY_MS, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                long now = System.currentTimeMillis();
                long elapsed = now - animationStartTime;
                float linearProgress = Math.min(1.0f, (float) elapsed / ANIMATION_DURATION_MS);
                float easedProgress = easeOutSine(linearProgress);

                // --- Animate Text Fields Position AND Size ---
                int targetYPlecare = targetYText;
                int targetYSosire = targetYText + initialSizePlecare.height + 5; // Use initial height for spacing

                // Interpolate Y position
                int newYPlecare = Math.round(initialPosPlecare.y + (targetYPlecare - initialPosPlecare.y) * easedProgress);
                int newYSosire = Math.round(initialPosSosire.y + (targetYSosire - initialPosSosire.y) * easedProgress);

                // Interpolate Width
                int newWidth = Math.round(initialSizePlecare.width + (targetWidth - initialSizePlecare.width) * easedProgress);

                // Interpolate X position to keep centered
                // Initial center X = initialPosPlecare.x + initialSizePlecare.width / 2
                // New center X should be inputPanel.getWidth() / 2
                // New X = (inputPanel.getWidth() / 2) - (newWidth / 2)
                // Let's interpolate X from initial X towards the centered X
                int centeredTargetX = (inputPanel.getWidth() / 2) - (targetWidth / 2); // Target X for full width
                int newXPlecare = Math.round(initialPosPlecare.x + (centeredTargetX - initialPosPlecare.x) * easedProgress);
                int newXSosire = Math.round(initialPosSosire.x + (centeredTargetX - initialPosSosire.x) * easedProgress);


                // Keep Height constant
                int currentHeightPlecare = initialSizePlecare.height;
                int currentHeightSosire = initialSizeSosire.height;

                // Update bounds (location and size)
                statie_plecare.setBounds(newXPlecare, newYPlecare, newWidth, currentHeightPlecare);
                statie_sosire.setBounds(newXSosire, newYSosire, newWidth, currentHeightSosire); // Assuming same width target

                // --- Animate Button Fade ---
                float newAlpha = initialButtonAlpha + (targetButtonAlpha - initialButtonAlpha) * easedProgress;
                if (nextButton instanceof FadeButton) {
                     ((FadeButton) nextButton).setAlpha(newAlpha);
                } else {
                    nextButton.setVisible(easedProgress < 0.95f);
                }

                inputPanel.repaint();

                // --- Check if Animation Finished ---
                if (linearProgress >= 1.0f) {
                    animationTimer.stop();
                    onAnimationFinished();
                }
            }
        });
        animationTimer.start();
    }

     private void onAnimationFinished() {
        // Final state setup is the same, GridBagLayout in topPanel will handle final sizing

        inputPanel.remove(statie_plecare);
        inputPanel.remove(statie_sosire);
        inputPanel.remove(nextButton);
        contentPane.remove(inputPanel); // Remove the panel used for animation

        JPanel topPanel = new JPanel(new GridBagLayout());
        topPanel.setBorder(new EmptyBorder(5, horizontalPadding, 5, horizontalPadding)); // Use padding for border
        GridBagConstraints gbcTop = new GridBagConstraints();
        gbcTop.gridx = 0;
        gbcTop.fill = GridBagConstraints.HORIZONTAL;
        gbcTop.weightx = 1.0;
        gbcTop.insets = new Insets(0, 0, 5, 0);

        gbcTop.gridy = 0;
        topPanel.add(statie_plecare, gbcTop);

        gbcTop.gridy = 1;
        gbcTop.insets = new Insets(0, 0, 0, 0);
        topPanel.add(statie_sosire, gbcTop);

        contentPane.add(topPanel, BorderLayout.NORTH);
        contentPane.add(resultsPanel, BorderLayout.CENTER);

        contentPane.revalidate();
        contentPane.repaint();

        findRouteLogicPlaceholder();
    }

    // --- Placeholder for route logic (unchanged) ---
    private void findRouteLogicPlaceholder() {
        String start = statie_plecare.getText().trim();
        String destination = statie_sosire.getText().trim();
        System.out.println("Animation finished. Finding route from: " + start + " to: " + destination);
        resultsPanel.removeAll();
        resultsPanel.add(new JLabel("Searching for routes from '" + start + "' to '" + destination + "'..."));

        // ************************************************************
        // *** START API CALL AND RESULT PROCESSING LOGIC HERE ***
        // ************************************************************
         Timer resultTimer = new Timer(1500, ae -> {
             SwingUtilities.invokeLater(() -> {
                 resultsPanel.removeAll();
                 resultsPanel.setLayout(new BoxLayout(resultsPanel, BoxLayout.Y_AXIS));
                 resultsPanel.add(new JLabel("Route Option 1: Bus 3b"));
                 resultsPanel.add(Box.createRigidArea(new Dimension(0, 5)));
                 resultsPanel.add(new JLabel("Route Option 2: Tram 7 -> Bus 20"));
                 resultsPanel.add(Box.createRigidArea(new Dimension(0, 5)));
                 resultsPanel.add(new JLabel("Route Option 3: Walk (if applicable)"));
                  resultsPanel.revalidate();
                  resultsPanel.repaint();
             });
         });
         resultTimer.setRepeats(false);
         resultTimer.start();
        // ************************************************************
    }

    // --- Menu Bar, Theme/Font methods (unchanged) ---
    private void setupMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);
        JMenu mnFont = new JMenu("Font");
        menuBar.add(mnFont);
        ActionListener fontListener = e -> updateFont();
        b12 = new JRadioButton("12"); b12.setActionCommand("12"); b12.setSelected(true); grupFont.add(b12); mnFont.add(b12); b12.addActionListener(fontListener);
        b14 = new JRadioButton("14"); b14.setActionCommand("14"); grupFont.add(b14); mnFont.add(b14); b14.addActionListener(fontListener);
        b16 = new JRadioButton("16"); b16.setActionCommand("16"); grupFont.add(b16); mnFont.add(b16); b16.addActionListener(fontListener);

        JMenu mnTheme = new JMenu("Theme");
        menuBar.add(mnTheme);
        ActionListener themeListener = e -> updateTheme(e);
        rbLight = new JRadioButton("Light"); rbLight.setActionCommand("Light"); grupTheme.add(rbLight); mnTheme.add(rbLight); rbLight.addActionListener(themeListener);
        rbDark = new JRadioButton("Dark"); rbDark.setActionCommand("Dark"); grupTheme.add(rbDark); mnTheme.add(rbDark); rbDark.addActionListener(themeListener);
        LookAndFeel currentLaF = UIManager.getLookAndFeel();
        if (currentLaF != null && currentLaF.getName().toLowerCase().contains("dark")) {
             rbDark.setSelected(true);
        } else {
             rbLight.setSelected(true);
        }
    }

    private void updateFont() { /* ... unchanged ... */ }
    private void updateTheme(ActionEvent e) { /* ... unchanged ... */ }

    // --- FadeButton inner class (unchanged) ---
    private static class FadeButton extends JButton {
        private static final long serialVersionUID = 1L;
		private float alpha = 1.0f;
        public FadeButton(String text) { super(text); setOpaque(false); setContentAreaFilled(false); setBorderPainted(false); }
        public void setAlpha(float alpha) { this.alpha = Math.max(0.0f, Math.min(1.0f, alpha)); repaint(); }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
            setContentAreaFilled(true); setBorderPainted(true); // Needed for FlatLaf paint
            super.paintComponent(g2d);
            setContentAreaFilled(false); setBorderPainted(false);
            g2d.dispose();
        }
    }
}
>>>>>>> adaugare_animatie
