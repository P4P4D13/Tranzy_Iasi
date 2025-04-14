package IasiTranzit.Tranzy_Iasi;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Arrays;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class InterfataGrafica extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTextField statie_plecare;
    private JTextField statie_sosire;


    private JButton nextButton;
    private JButton backButton;
    private JButton settingsButton;
    private JRadioButton b12, b14, b16;
    private JRadioButton rbLight, rbDark;
  

    // --- Animation Variables ---
    private Timer animationTimer;
    private final int ANIMATION_DURATION_MS = 800; // Keep it slow
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
    private JPanel footerPanel;

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
        
        createFooterPanel();
        contentPane.add(footerPanel, BorderLayout.SOUTH);

        nextButton.addActionListener(e -> startAnimation());
        
        pack();
        setLocationRelativeTo(null);
    }

    private void createFooterPanel() {
      footerPanel = new JPanel(new BorderLayout());
      footerPanel.setBorder(new EmptyBorder(5, 10, 10, 10));
      
      settingsButton = new JButton("Settings");
      settingsButton.putClientProperty("JButton.buttonType", "roundRect");
      settingsButton.putClientProperty(FlatClientProperties.BUTTON_TYPE, "toolBarButton");
      settingsButton.setFocusPainted(false);
      
      backButton=new JButton("<");
      backButton.putClientProperty("JButton.buttonType", "roundRect");
      backButton.putClientProperty(FlatClientProperties.BUTTON_TYPE, "toolBarButton");
      backButton.setFocusPainted(false);
      
      // Set a preferred size for the button to make it smaller
      settingsButton.setPreferredSize(new Dimension(100, 30));
      backButton.setPreferredSize(new Dimension(50, 30));
      
      // Add action listener to the settings button
      settingsButton.addActionListener(e -> openSettingsDialog());

      backButton.addActionListener(e -> backFunc());

      
      // Add the button to the right side of the footer panel
      footerPanel.add(settingsButton, BorderLayout.WEST);
      footerPanel.add(backButton,BorderLayout.EAST);
    }
    
    private void backFunc() {
    	
    	// Creates a new window
    	InterfataGrafica newFrame = new InterfataGrafica();
    	newFrame.setVisible(true);
    	
    	// Close the initial window
    	this.dispose();
    }
    
    private void openSettingsDialog() {
      JDialog settingsDialog = new JDialog(this, "Settings", true);
      JFrame settingsFrame = new JFrame("Settings");
      settingsFrame.setSize(300, 200);
      settingsFrame.setLocationRelativeTo(this);  // Center the new window relative to the main window
      settingsFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

      // Create a panel for the content of the settings window
      JPanel settingsPanel = new JPanel();
      settingsPanel.setLayout(new BoxLayout(settingsPanel, BoxLayout.Y_AXIS));  // Use vertical box layout

      // Theme settings
      JLabel themeLabel = new JLabel("Choose Theme:");
      JRadioButton lightTheme = new JRadioButton("Light Theme");
      JRadioButton darkTheme = new JRadioButton("Dark Theme");
      ButtonGroup themeGroup = new ButtonGroup();
      themeGroup.add(lightTheme);
      themeGroup.add(darkTheme);

      // Set the initial theme based on current LookAndFeel
      if (UIManager.getLookAndFeel().getName().toLowerCase().contains("dark")) {
          darkTheme.setSelected(true);
      } else {
          lightTheme.setSelected(true);
      }

      // Font size settings
      JLabel fontSizeLabel = new JLabel("Choose Font Size:");
      JRadioButton font12 = new JRadioButton("12");
      JRadioButton font14 = new JRadioButton("14");
      JRadioButton font16 = new JRadioButton("16");
      ButtonGroup fontSizeGroup = new ButtonGroup();
      fontSizeGroup.add(font12);
      fontSizeGroup.add(font14);
      fontSizeGroup.add(font16);

      // Set the initial font size based on the current font
      int currentFontSize = getCurrentFontSize();
      if (currentFontSize == 12) {
          font12.setSelected(true);
      } else if (currentFontSize == 14) {
          font14.setSelected(true);
      } else if (currentFontSize == 16) {
          font16.setSelected(true);
      }

      // Add components to the settings panel
      settingsPanel.add(themeLabel);
      settingsPanel.add(lightTheme);
      settingsPanel.add(darkTheme);
      settingsPanel.add(fontSizeLabel);
      settingsPanel.add(font12);
      settingsPanel.add(font14);
      settingsPanel.add(font16);

      // Create Save and Cancel buttons
      JPanel buttonPanel = new JPanel();
      JButton saveButton = new JButton("Save");
      JButton cancelButton = new JButton("Cancel");

      // Save Button ActionListener
      saveButton.addActionListener(new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
              // Change theme based on user selection
              if (lightTheme.isSelected()) {
                  try {
                      UIManager.setLookAndFeel(new FlatLightLaf());
                  } catch (UnsupportedLookAndFeelException ex) {
                      ex.printStackTrace();
                  }
              } else if (darkTheme.isSelected()) {
                  try {
                      UIManager.setLookAndFeel(new FlatDarkLaf());
                  } catch (UnsupportedLookAndFeelException ex) {
                      ex.printStackTrace();
                  }
              }

              // Change font size based on user selection
              int selectedFontSize = 12;
              if (font14.isSelected()) {
                  selectedFontSize = 14;
              } else if (font16.isSelected()) {
                  selectedFontSize = 16;
              }

              updateFontSize(selectedFontSize);

              // Revalidate and repaint the main frame to apply changes
              SwingUtilities.updateComponentTreeUI(InterfataGrafica.this); // Refresh main window
              settingsFrame.dispose(); // Close the settings window
          }
      });

      // Cancel Button ActionListener
      cancelButton.addActionListener(new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
              settingsFrame.dispose(); // Close the settings frame without making changes
          }
      });

      // Add buttons to the button panel
      buttonPanel.add(saveButton);
      buttonPanel.add(cancelButton);
      settingsFrame.add(buttonPanel, BorderLayout.SOUTH);

      // Add settings panel to the frame
      settingsFrame.add(settingsPanel, BorderLayout.CENTER);

      // Make the settings window visible
      settingsFrame.setVisible(true);
  }

  // Helper method to get the current font size
  private int getCurrentFontSize() {
      Font currentFont = statie_plecare.getFont(); // Get the font of any component (like text field)
      return currentFont.getSize();
  }

  // Helper method to update the font size for all components
  private void updateFontSize(int newSize) {
      Font currentFont = new Font("Arial", Font.PLAIN, newSize); // Update to new font size
      statie_plecare.setFont(currentFont);
      statie_sosire.setFont(currentFont);
      nextButton.setFont(currentFont);
      // Update other components' font size as needed
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
