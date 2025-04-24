package IasiTranzit.Tranzy_Iasi; // Keep your package structure

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.ExecutionException;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class InterfataGrafica extends JFrame {
    private static final long serialVersionUID = 1L;

    // --- API Configuration ---
    // Updated with provided values
    private static final String API_KEY = "7DgYhGzTQc5Nn8FfFeuFmhCAWcbadYQEShUjwu3e";
    private static final String AGENCY_ID = "1"; // ID-ul agenției CTP Iași
    // --- End API Configuration ---

    private static final String API_BASE_URL = "https://api.tranzy.ai/v1/opendata/";
    private static final String VEHICLES_ENDPOINT = API_BASE_URL + "vehicles";
    private static final String ROUTES_ENDPOINT = API_BASE_URL + "routes";
    private static final String TRIPS_ENDPOINT = API_BASE_URL + "trips";
    // Added definitions for Stops and Stop Times in case needed later
    private static final String STOPS_ENDPOINT = API_BASE_URL + "stops";
    private static final String STOP_TIMES_ENDPOINT = API_BASE_URL + "stop_times";


    // --- UI Components (mostly unchanged) ---
    private JPanel contentPane;
    private JTextField vehicleIdInput;
    private final ButtonGroup grupFont = new ButtonGroup();
    private final ButtonGroup grupTheme = new ButtonGroup();
    private JButton trackButton; // Will be initialized as FadeButton
    private JRadioButton b12, b14, b16;
    private JRadioButton rbLight, rbDark;
    private Timer animationTimer;
    private final int ANIMATION_DURATION_MS = 800;
    private final int TIMER_DELAY_MS = 10;
    private long animationStartTime;
    private Point initialPosInput;
    private Dimension initialSizeInput;
    private int targetYText = 10;
    private int targetWidth;
    private int horizontalPadding = 10;
    private float initialButtonAlpha = 1.0f;
    private float targetButtonAlpha = 0.0f;
    private JPanel inputPanel;
    private JPanel topPanel;
    private JPanel resultsPanel;
    private JScrollPane resultsScrollPane;
    private JLabel statusLabel; // Label to show loading status

    // --- Data Storage ---
    private Map<String, Route> routesMap = new HashMap<>(); // Key: route_id (as String)
    private Map<String, Trip> tripsMap = new HashMap<>();   // Key: trip_id
    // Add Maps for Stops and StopTimes if you implement features needing them
    // private Map<String, Stop> stopsMap = new HashMap<>(); // Key: stop_id (as String)
    // private Map<String, List<StopTime>> stopTimesMap = new HashMap<>(); // Key: trip_id

    public InterfataGrafica() {
        setTitle("Tranzy Iasi - Vehicle Tracker");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(450, 650)); // Slightly larger
        setResizable(true);

        setupMenuBar();

        contentPane = new JPanel(new BorderLayout());
        setContentPane(contentPane);

        // Panel for initial input setup
        inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBorder(new EmptyBorder(30, 30, 30, 30));
        contentPane.add(inputPanel, BorderLayout.CENTER);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 5, 10, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        vehicleIdInput = new JTextField(20);
        vehicleIdInput.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Enter Bus/Tram Label or Route (e.g., 3b, 7, 123)");
        gbc.gridx = 0; gbc.gridy = 0;
        inputPanel.add(vehicleIdInput, gbc);

        trackButton = new FadeButton("Track Vehicle");
        trackButton.putClientProperty("JButton.buttonType", "roundRect");
        trackButton.setEnabled(false); // Initially disabled until static data loads
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.ipady = 10;
        gbc.insets = new Insets(20, 5, 10, 5);
        inputPanel.add(trackButton, gbc);

        // Status Label for loading messages
        statusLabel = new JLabel("Loading initial data...", SwingConstants.CENTER);
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.ipady = 0; // Reset padding
        inputPanel.add(statusLabel, gbc);


        // --- Results Panel Setup (similar to before) ---
        resultsPanel = new JPanel();
        resultsPanel.setLayout(new BoxLayout(resultsPanel, BoxLayout.Y_AXIS));
        resultsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        resultsScrollPane = new JScrollPane(resultsPanel);
        resultsScrollPane.setBorder(BorderFactory.createEmptyBorder());

        // --- Top Panel Setup (similar to before) ---
        topPanel = new JPanel(new GridBagLayout());
        topPanel.setBorder(new EmptyBorder(5, horizontalPadding, 5, horizontalPadding));

        trackButton.addActionListener(e -> startAnimation());

        pack();
        setLocationRelativeTo(null);

        // Load static data in the background after UI is set up
        loadStaticData();
    }

    // --- Data Fetching Helper ---
    private String fetchData(String endpointUrl) throws IOException {
        // Removed API_KEY check as it's now hardcoded
        URL url = new URL(endpointUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", "application/json");
        connection.setRequestProperty("X-API-KEY", API_KEY);
        // AGENCY_ID is now always added as it's set to "1"
        connection.setRequestProperty("X-Agency-Id", AGENCY_ID);

        connection.setConnectTimeout(10000); // 10 seconds
        connection.setReadTimeout(15000);   // 15 seconds

        int responseCode = connection.getResponseCode();
        StringBuilder response = new StringBuilder();

        if (responseCode == HttpURLConnection.HTTP_OK) {
            try (InputStream inputStream = connection.getInputStream();
                 BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
            }
        } else {
            String errorDetails = "";
             try (InputStream errorStream = connection.getErrorStream();
                  BufferedReader reader = (errorStream != null) ? new BufferedReader(new InputStreamReader(errorStream)) : null) {
                 if (reader != null) {
                     String line;
                     while ((line = reader.readLine()) != null) {
                         errorDetails += line;
                     }
                 }
             } catch (IOException e) { /* Ignore error reading error stream */ }
            throw new IOException("HTTP Error: " + responseCode + " " + connection.getResponseMessage() + " - " + errorDetails);
        }
        connection.disconnect();
        return response.toString();
    }

    // --- Static Data Loading ---
    private void loadStaticData() {
        statusLabel.setText("Loading Routes and Trips...");
        trackButton.setEnabled(false); // Disable search while loading static data

        SwingWorker<Boolean, Void> staticDataLoader = new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                try {
                    // Fetch Routes
                    String routesJson = fetchData(ROUTES_ENDPOINT);
                    JSONArray routesArray = new JSONArray(routesJson);
                    Map<String, Route> tempRoutesMap = new HashMap<>();
                    for (int i = 0; i < routesArray.length(); i++) {
                        Route route = Route.fromJson(routesArray.getJSONObject(i));
                        tempRoutesMap.put(route.id, route);
                    }
                    routesMap = tempRoutesMap;

                    // Fetch Trips
                    String tripsJson = fetchData(TRIPS_ENDPOINT);
                    JSONArray tripsArray = new JSONArray(tripsJson);
                    Map<String, Trip> tempTripsMap = new HashMap<>();
                    for (int i = 0; i < tripsArray.length(); i++) {
                        Trip trip = Trip.fromJson(tripsArray.getJSONObject(i));
                        tempTripsMap.put(trip.id, trip);
                    }
                    tripsMap = tempTripsMap;

                    // Optionally load Stops and StopTimes here if needed later
                    // ...

                    return true; // Success
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                    throw e; // Re-throw to be caught in done()
                }
            }

            @Override
            protected void done() {
                try {
                    Boolean success = get();
                    if (success) {
                        statusLabel.setText("Data loaded. Ready.");
                        trackButton.setEnabled(true);
                    } else {
                        statusLabel.setText("Failed to load static data (unexpected).");
                         JOptionPane.showMessageDialog(InterfataGrafica.this,
                                "Failed to load necessary route/trip data.\nPlease check configuration or network.",
                                "Data Loading Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                    statusLabel.setText("Error loading static data!");
                    Throwable cause = e.getCause();
                     String errorMsg = "Failed to load necessary route/trip data.\n";
                    if (cause instanceof IOException) {
                        errorMsg += "Network or API Error: " + cause.getMessage();
                    } else if (cause instanceof JSONException) {
                         errorMsg += "Error parsing data from API.";
                     } else {
                         errorMsg += "An unexpected error occurred.";
                     }
                    JOptionPane.showMessageDialog(InterfataGrafica.this, errorMsg, "Data Loading Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        staticDataLoader.execute();
    }

    // --- Animation Logic (mostly unchanged, calls fetchAndDisplayVehicleData) ---
    private float easeOutSine(float t) { /* ... unchanged ... */
        return (float) Math.sin(t * Math.PI / 2.0);
    }
     private void startAnimation() { /* ... unchanged ... */
        String vehicleId = vehicleIdInput.getText().trim();
        if (vehicleId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a Bus/Tram Label or Route.", "Input Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!trackButton.isEnabled()) {
            JOptionPane.showMessageDialog(this, "Static data is still loading or failed to load.", "Please Wait", JOptionPane.WARNING_MESSAGE);
            return;
        }
        trackButton.setEnabled(false);
        inputPanel.revalidate();
        initialPosInput = vehicleIdInput.getLocation();
        initialSizeInput = vehicleIdInput.getSize();
        targetWidth = inputPanel.getWidth() - (2 * horizontalPadding);
        inputPanel.setLayout(null);
        vehicleIdInput.setBounds(initialPosInput.x, initialPosInput.y, initialSizeInput.width, initialSizeInput.height);
        Point initialPosButton = trackButton.getLocation();
        Dimension initialSizeButton = trackButton.getSize();
        trackButton.setBounds(initialPosButton.x, initialPosButton.y, initialSizeButton.width, initialSizeButton.height);
        inputPanel.add(vehicleIdInput); inputPanel.add(trackButton);
        inputPanel.revalidate(); inputPanel.repaint();
        animationStartTime = System.currentTimeMillis();
        if (animationTimer != null && animationTimer.isRunning()) animationTimer.stop();
        animationTimer = new Timer(TIMER_DELAY_MS, new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) { /* Animation logic unchanged */
                long now = System.currentTimeMillis(); long elapsed = now - animationStartTime;
                float linearProgress = Math.min(1.0f, (float) elapsed / ANIMATION_DURATION_MS);
                float easedProgress = easeOutSine(linearProgress);
                int targetYInput = targetYText; int newYInput = Math.round(initialPosInput.y + (targetYInput - initialPosInput.y) * easedProgress);
                int newWidth = Math.round(initialSizeInput.width + (targetWidth - initialSizeInput.width) * easedProgress);
                int centeredTargetX = (inputPanel.getWidth() - newWidth) / 2; int newXInput = Math.round(initialPosInput.x + (centeredTargetX - initialPosInput.x) * easedProgress);
                int currentHeightInput = initialSizeInput.height;
                vehicleIdInput.setBounds(newXInput, newYInput, newWidth, currentHeightInput);
                float newAlpha = initialButtonAlpha + (targetButtonAlpha - initialButtonAlpha) * easedProgress;
                if (trackButton instanceof FadeButton) ((FadeButton) trackButton).setAlpha(newAlpha); else trackButton.setVisible(newAlpha > 0.05f);
                inputPanel.repaint();
                if (linearProgress >= 1.0f) { animationTimer.stop(); onAnimationFinished(); }
             }
        });
        animationTimer.start();
    }

    private void onAnimationFinished() { /* ... unchanged setup for topPanel, resultsScrollPane ... */
        inputPanel.remove(vehicleIdInput); inputPanel.remove(trackButton);
        contentPane.remove(inputPanel);
        topPanel.removeAll();
        GridBagConstraints gbcTop = new GridBagConstraints();
        gbcTop.gridx = 0; gbcTop.gridy = 0; gbcTop.fill = GridBagConstraints.HORIZONTAL; gbcTop.weightx = 1.0; gbcTop.insets = new Insets(0, 0, 5, 0);
        topPanel.add(vehicleIdInput, gbcTop);
        contentPane.add(topPanel, BorderLayout.NORTH);
        contentPane.add(resultsScrollPane, BorderLayout.CENTER);
        contentPane.revalidate(); contentPane.repaint();
        fetchAndDisplayVehicleData();
    }

    // --- Real Data Fetching and Display ---
    private void fetchAndDisplayVehicleData() {
        String targetId = vehicleIdInput.getText().trim().toLowerCase();
        resultsPanel.removeAll();
        resultsPanel.add(new JLabel("Fetching data for '" + targetId + "'..."));
        resultsPanel.revalidate(); resultsPanel.repaint();

        SwingWorker<List<DisplayVehicleInfo>, Void> worker = new SwingWorker<List<DisplayVehicleInfo>, Void>() {
            @Override
            protected List<DisplayVehicleInfo> doInBackground() throws Exception {
                List<DisplayVehicleInfo> foundVehicles = new ArrayList<>();
                try {
                    String vehiclesJson = fetchData(VEHICLES_ENDPOINT);
                    JSONArray vehiclesArray = new JSONArray(vehiclesJson);

                    for (int i = 0; i < vehiclesArray.length(); i++) {
                        JSONObject vehicleJson = vehiclesArray.getJSONObject(i);
                        Vehicle vehicle = Vehicle.fromJson(vehicleJson);

                        // Ensure vehicle has a route ID before looking up
                        Route route = (vehicle.routeId != null) ? routesMap.get(vehicle.routeId) : null;
                        String routeShortName = (route != null) ? route.shortName.toLowerCase() : "";

                        // Ensure vehicle has a trip ID before looking up
                        Trip trip = (vehicle.tripId != null) ? tripsMap.get(vehicle.tripId) : null;
                        String tripHeadsign = (trip != null) ? trip.headsign : "N/A";

                        boolean labelMatch = vehicle.label.toLowerCase().equals(targetId);
                        boolean routeMatch = !routeShortName.isEmpty() && routeShortName.equals(targetId);

                        if (labelMatch || routeMatch) {
                            DisplayVehicleInfo displayInfo = new DisplayVehicleInfo();
                            displayInfo.vehicle = vehicle;
                            displayInfo.routeShortName = (route != null) ? route.shortName : "N/A"; // Use original case for display
                            displayInfo.tripHeadsign = tripHeadsign;
                            foundVehicles.add(displayInfo);
                        }
                    }
                } catch (IOException | JSONException e) {
                    e.printStackTrace(); throw e;
                }
                return foundVehicles;
            }

            @Override
            protected void done() {
                resultsPanel.removeAll();
                try {
                    List<DisplayVehicleInfo> vehicles = get();
                    if (vehicles.isEmpty()) {
                        resultsPanel.add(new JLabel("No active vehicles found matching '" + targetId + "'."));
                    } else {
                        resultsPanel.add(new JLabel("Found " + vehicles.size() + " vehicle(s) matching '" + targetId + "':"));
                        resultsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
                        for (DisplayVehicleInfo info : vehicles) {
                            JPanel vehiclePanel = createVehiclePanel(info);
                            resultsPanel.add(vehiclePanel);
                            resultsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
                        }
                    }
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                    resultsPanel.add(new JLabel("Error fetching vehicle data:"));
                     Throwable cause = e.getCause();
                     String errorMsg = (cause != null) ? cause.getMessage() : e.getMessage();
                    JTextArea errorArea = new JTextArea(errorMsg);
                    errorArea.setEditable(false); errorArea.setLineWrap(true); errorArea.setWrapStyleWord(true);
                    errorArea.setBackground(resultsPanel.getBackground());
                     resultsPanel.add(new JScrollPane(errorArea));
                } finally {
                    resultsPanel.revalidate(); resultsPanel.repaint();
                    trackButton.setEnabled(true);
                    if (trackButton instanceof FadeButton) ((FadeButton) trackButton).setAlpha(initialButtonAlpha);
                    trackButton.setVisible(true);
                }
            }
        };
        worker.execute();
    }

    // Helper to create the panel for displaying a single vehicle's info
    private JPanel createVehiclePanel(DisplayVehicleInfo info) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, Color.GRAY),
                BorderFactory.createEmptyBorder(5, 0, 10, 0)
        ));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel titleLabel = new JLabel(String.format("Vehicle: %s (Route: %s)", info.vehicle.label, info.routeShortName));
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD));
        panel.add(titleLabel);

        panel.add(new JLabel(String.format("Destination: %s", info.tripHeadsign)));
        panel.add(Box.createRigidArea(new Dimension(0, 5)));

        panel.add(new JLabel(String.format("Location: %.5f, %.5f", info.vehicle.latitude, info.vehicle.longitude)));
        panel.add(new JLabel(String.format("Speed: %.1f km/h", info.vehicle.speedKmH)));
        panel.add(new JLabel(String.format("Last Update: %s", info.vehicle.getFormattedTimestamp())));
        panel.add(new JLabel(String.format("Type: %s", info.vehicle.getVehicleTypeString())));

        return panel;
    }


    // --- Menu Bar, Theme/Font methods (unchanged) ---
    private void setupMenuBar() { /* ... unchanged ... */
        JMenuBar menuBar = new JMenuBar(); setJMenuBar(menuBar);
        JMenu mnFont = new JMenu("Font"); menuBar.add(mnFont);
        ActionListener fontListener = e -> updateFont();
        b12 = new JRadioButton("12"); b12.setActionCommand("12"); b12.setSelected(true); grupFont.add(b12); mnFont.add(b12); b12.addActionListener(fontListener);
        b14 = new JRadioButton("14"); b14.setActionCommand("14"); grupFont.add(b14); mnFont.add(b14); b14.addActionListener(fontListener);
        b16 = new JRadioButton("16"); b16.setActionCommand("16"); grupFont.add(b16); mnFont.add(b16); b16.addActionListener(fontListener);
        JMenu mnTheme = new JMenu("Theme"); menuBar.add(mnTheme);
        ActionListener themeListener = e -> updateTheme(e);
        rbLight = new JRadioButton("Light"); rbLight.setActionCommand("Light"); grupTheme.add(rbLight); mnTheme.add(rbLight); rbLight.addActionListener(themeListener);
        rbDark = new JRadioButton("Dark"); rbDark.setActionCommand("Dark"); grupTheme.add(rbDark); mnTheme.add(rbDark); rbDark.addActionListener(themeListener);
        try { boolean isCurrentlyDark = UIManager.getLookAndFeel() instanceof FlatDarkLaf; if (isCurrentlyDark) { rbDark.setSelected(true); UIManager.setLookAndFeel(new FlatDarkLaf()); } else { rbLight.setSelected(true); UIManager.setLookAndFeel(new FlatLightLaf()); } UIManager.put( "TextComponent.arc", 10 ); UIManager.put( "Button.arc", 10 ); } catch (UnsupportedLookAndFeelException e) { System.err.println("Failed to set initial theme: " + e.getMessage()); rbLight.setSelected(true); } SwingUtilities.updateComponentTreeUI(this); updateFont();
    }
    private void updateFont() { /* ... unchanged ... */
        String selectedSize = grupFont.getSelection().getActionCommand(); int fontSize = Integer.parseInt(selectedSize); Font baseFont = UIManager.getFont("Label.font"); if (baseFont == null) baseFont = new Font(Font.SANS_SERIF, Font.PLAIN, 12); Font newFont = baseFont.deriveFont((float)fontSize); updateComponentFont(this, newFont); SwingUtilities.updateComponentTreeUI(this);
    }
    private void updateComponentFont(java.awt.Component comp, Font font) { /* ... unchanged ... */
        comp.setFont(font); if (comp instanceof java.awt.Container) { for (java.awt.Component child : ((java.awt.Container) comp).getComponents()) { updateComponentFont(child, font); } }
    }
    private void updateTheme(ActionEvent e) { /* ... unchanged ... */
        String theme = e.getActionCommand(); try { if ("Dark".equals(theme)) UIManager.setLookAndFeel(new FlatDarkLaf()); else UIManager.setLookAndFeel(new FlatLightLaf()); UIManager.put( "TextComponent.arc", 10 ); UIManager.put( "Button.arc", 10 ); SwingUtilities.updateComponentTreeUI(this); } catch (UnsupportedLookAndFeelException ex) { System.err.println("Failed to set theme: " + ex.getMessage()); }
    }

    // --- FadeButton inner class (unchanged) ---
    private static class FadeButton extends JButton { /* ... unchanged ... */
        private static final long serialVersionUID = 1L; private float alpha = 1.0f; public FadeButton(String text) { super(text); setOpaque(false); setContentAreaFilled(false); setBorderPainted(false); } public void setAlpha(float alpha) { this.alpha = Math.max(0.0f, Math.min(1.0f, alpha)); repaint(); } public float getAlpha() { return alpha; }
        @Override protected void paintComponent(Graphics g) { Graphics2D g2d = (Graphics2D) g.create(); if (this.alpha < 1.0f) g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha)); boolean ocf = isContentAreaFilled(); boolean obp = isBorderPainted(); setContentAreaFilled(true); setBorderPainted(true); super.paintComponent(g2d); setContentAreaFilled(ocf); setBorderPainted(obp); g2d.dispose(); }
        @Override public Dimension getPreferredSize() { boolean ocf = isContentAreaFilled(); boolean obp = isBorderPainted(); setContentAreaFilled(true); setBorderPainted(true); Dimension size = super.getPreferredSize(); setContentAreaFilled(ocf); setBorderPainted(obp); return size; }
        @Override public boolean contains(int x, int y) { return alpha > 0.1f && super.contains(x, y); }
    }

    // --- Data Classes (POJOs) ---
    // Minor update: Ensure routeId and tripId in Vehicle are handled if null from JSON
    private static class Vehicle {
        String id; String label; double latitude; double longitude; long timestampEpochSeconds;
        int vehicleType; String bikeAccessible; String wheelchairAccessible; double speedKmH;
        String routeId; // Can be null if not on a route
        String tripId;  // Can be null if not on a trip

        static Vehicle fromJson(JSONObject json) throws JSONException {
            Vehicle v = new Vehicle();
            v.id = json.getString("id");
            v.label = json.optString("label", v.id);
            v.latitude = json.getDouble("latitude");
            v.longitude = json.getDouble("longitude");
            try { v.timestampEpochSeconds = json.getLong("timestamp"); }
            catch (JSONException e) { /* Handle string timestamp or error as before */
                String tsString = json.optString("timestamp", null);
                if (tsString != null) { try { SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); Date date = sdf.parse(tsString); v.timestampEpochSeconds = date.getTime() / 1000L; } catch (ParseException pe) { System.err.println("Could not parse timestamp string: " + tsString); v.timestampEpochSeconds = 0; } }
                else { v.timestampEpochSeconds = 0; }
            }
            v.vehicleType = json.optInt("vehicle_type", -1);
            v.bikeAccessible = json.optString("bike_accessible", "UNKNOWN");
            v.wheelchairAccessible = json.optString("wheelchair_accessible", "UNKNOWN");
            double speedMs = json.optDouble("speed", 0.0);
            v.speedKmH = speedMs * 3.6;
            // Use optString with fallback to null if key doesn't exist or is JSON null
            v.routeId = json.optString("route_id", null);
             if ("null".equals(v.routeId)) v.routeId = null; // Handle string "null" case if API sends it
            v.tripId = json.optString("trip_id", null);
             if ("null".equals(v.tripId)) v.tripId = null; // Handle string "null" case

            // Handle cases where route_id or trip_id might be an integer 0 instead of null/missing
            if (v.routeId == null && json.has("route_id") && json.optInt("route_id", -1) == 0) {
                 // Decide if route_id 0 means "no route" or a specific route "0"
                 // Assuming 0 might mean "no route" for now, setting to null. Adjust if route "0" exists.
                 // v.routeId = "0"; // If route "0" is valid
                 v.routeId = null;
            }
             // Similar logic could apply to trip_id if 0 means no trip


            return v;
        }
        String getFormattedTimestamp() { /* ... unchanged ... */
            if (timestampEpochSeconds == 0) return "N/A"; Date date = new Date(timestampEpochSeconds * 1000L); SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss"); return sdf.format(date);
        }
        String getVehicleTypeString() { /* ... unchanged ... */
            switch (vehicleType) { case 0: return "Tram"; case 3: return "Bus"; default: return "Unknown (" + vehicleType + ")"; } // Adjusted based on common GTFS types
        }
    }

    private static class Route { /* ... unchanged ... */
        String id; String shortName; String longName; String color; int type; String desc;
        static Route fromJson(JSONObject json) throws JSONException { Route r = new Route(); r.id = String.valueOf(json.getInt("route_id")); r.shortName = json.optString("route_short_name", ""); r.longName = json.optString("route_long_name", ""); r.color = json.optString("route_color", "FFFFFF"); r.type = json.optInt("route_type", -1); r.desc = json.optString("route_desc", ""); return r; }
    }

    private static class Trip { /* ... unchanged ... */
        String id; String routeId; String headsign; int directionId;
        static Trip fromJson(JSONObject json) throws JSONException { Trip t = new Trip(); t.id = json.getString("trip_id"); t.routeId = String.valueOf(json.getInt("route_id")); t.headsign = json.optString("trip_headsign", "N/A"); t.directionId = json.optInt("direction_id", -1); return t; }
    }

    // Helper class to combine info for display
    private static class DisplayVehicleInfo { /* ... unchanged ... */
        Vehicle vehicle; String routeShortName; String tripHeadsign;
    }


    // Main method (unchanged)
    public static void main(String[] args) {
       try { UIManager.setLookAndFeel(new FlatLightLaf()); UIManager.put( "TextComponent.arc", 10 ); UIManager.put( "Button.arc", 10 ); } catch (UnsupportedLookAndFeelException e) { System.err.println("FlatLaf initialization failed:"); e.printStackTrace(); }
       SwingUtilities.invokeLater(() -> { InterfataGrafica frame = new InterfataGrafica(); frame.setVisible(true); });
    }
}