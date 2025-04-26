package IasiTranzit.Tranzy_Iasi;

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
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
//tot nu functioneaza aici
import java.util.TimeZone; // <<< FIX: Added this import
import java.util.concurrent.ExecutionException;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class InterfataGrafica extends JFrame {
	//logica buna aici
    private static final long serialVersionUID = 1L;

    private static final String API_KEY = "7DgYhGzTQc5Nn8FfFeuFmhCAWcbadYQEShUjwu3e";
    private static final String AGENCY_ID = "1";

    private static final String API_BASE_URL = "https://api.tranzy.ai/v1/opendata/";
    private static final String VEHICLES_ENDPOINT = API_BASE_URL + "vehicles";
    private static final String ROUTES_ENDPOINT = API_BASE_URL + "routes";
    private static final String TRIPS_ENDPOINT = API_BASE_URL + "trips";
    //nefunctional nuj de ce 
    //la stops e https://api.tranzy.ai/v1/opendata/stops si
    //https://api.tranzy.ai/v1/opendata/stop_times
    private static final String STOPS_ENDPOINT = API_BASE_URL + "stops";
    private static final String STOP_TIMES_ENDPOINT = API_BASE_URL + "stop_times";


    private JPanel contentPane;
    private JTextField vehicleIdInput;
    private final ButtonGroup grupFont = new ButtonGroup();
    private final ButtonGroup grupTheme = new ButtonGroup();
    private JButton trackButton;
    private JRadioButton b12, b14, b16;
    private JRadioButton rbLight, rbDark;
    private Timer animationTimer;
    //aici modificam viteza la animatie dupa buton
    private final int ANIMATION_DURATION_MS = 500;
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
    private JLabel statusLabel;
//Route==Trip ??? sau nu?
    private Map<String, Route> routesMap = new HashMap<>();
    private Map<String, Trip> tripsMap = new HashMap<>();
/**
 * Configurare de baza a proiectului setare titlu dimensiuni,layout, content
 */
    public InterfataGrafica() {
        setTitle("Tranzy Iasi");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(450, 650));
        setResizable(true);

        setupMenuBar();

        contentPane = new JPanel(new BorderLayout());
        setContentPane(contentPane);

        inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBorder(new EmptyBorder(30, 30, 30, 30));
        contentPane.add(inputPanel, BorderLayout.CENTER);
        /**
         * gbc= layout ul gridbag pentru pozitonare elemente
         */
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
        trackButton.setEnabled(false);
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.ipady = 10;
        gbc.insets = new Insets(20, 5, 10, 5);
        inputPanel.add(trackButton, gbc);

        statusLabel = new JLabel("Loading initial data...", SwingConstants.CENTER);
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.ipady = 0;
        inputPanel.add(statusLabel, gbc);

//ca sa putem da scroll, e ok 
        resultsPanel = new JPanel();
        resultsPanel.setLayout(new BoxLayout(resultsPanel, BoxLayout.Y_AXIS));
        resultsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        resultsScrollPane = new JScrollPane(resultsPanel);
        resultsScrollPane.setBorder(BorderFactory.createEmptyBorder());
        resultsScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        resultsScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);


        topPanel = new JPanel(new GridBagLayout());
        topPanel.setBorder(new EmptyBorder(5, horizontalPadding, 5, horizontalPadding));

        trackButton.addActionListener(e -> startAnimation());

        pack();
        setLocationRelativeTo(null);

        loadStaticData();
    }
// de revizuit logica, prea multe exceptii care nu pot sa apara,
    private String fetchData(String endpointUrl) throws IOException {
        URL url = new URL(endpointUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", "application/json");
        connection.setRequestProperty("X-API-KEY", API_KEY);
        connection.setRequestProperty("X-Agency-Id", AGENCY_ID);
        connection.setConnectTimeout(10000);
        connection.setReadTimeout(15000);
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
             } catch (IOException e) {
                 System.err.println("Error reading error stream details: " + e.getMessage());
             }
            throw new IOException("HTTP Error: " + responseCode + " " + connection.getResponseMessage() + " - Details: " + errorDetails);
        }
        connection.disconnect();
        return response.toString();
    }
    /**
     * Inacarca date statice, rute si trasee, folosind SwingWorker
     * Actualizeaza interfata in functie de succesul sau esecul operatiei
     */
    
    private void loadStaticData() {
        statusLabel.setText("Loading Routes and Trips...");
        trackButton.setEnabled(false);

        SwingWorker<Boolean, Void> staticDataLoader = new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                try {
                	 routesMap = loadRoutes();
                	 tripsMap=loadTrips();
                    return true;
                } catch (IOException | JSONException e) {
                    System.err.println("Error during static data loading: " + e.getMessage());
                    e.printStackTrace();
                    throw e;
                }
            }
            
            @Override
            protected void done() {
                try {
                    Boolean success = get();
                    if (success) {
                    	loadingSuccess();
                    } else {
                    	loadingFail("Failed to load necessary route/trip data.\\nPlease check configuration or network.");
                    }
                } catch (InterruptedException e) {
                     Thread.currentThread().interrupt();
                     loadingWarning("Data loading interrupted");
                } catch (ExecutionException e) {
                	onLoadingException(e.getCause());
                }
            }
        };
        staticDataLoader.execute();
    }
    

    //fct pt incarcarea rutelor,clarifica ce se intampla in doInBackground
/**
 * 
 * @return o harta a ID-urilor de ruta catre obiectele Route
 * @throws IOException daca apare o eroare de retea, conexiunea nu merge sau serverul nu raspunde
 * @throws JSONException daca datele JSON nu sunt valide(alt format)
 */
    private Map<String,Route> loadRoutes() throws IOException,JSONException{
    	String routesJson = fetchData(ROUTES_ENDPOINT);
    	JSONArray routesArray = new JSONArray(routesJson);
    	Map<String, Route> tempRoutesMap = new HashMap<>();
    	for (int i = 0; i < routesArray.length(); i++) {
    		Route route = Route.fromJson(routesArray.getJSONObject(i));
    		tempRoutesMap.put(route.id, route);
    	}
    return tempRoutesMap;
    }
    
    //fct pt incarcarea traseelor,luat codul din functia doInBackground
    /**
     * Incarca datele despre trasee de la un anumit endpoint
     * 
     * @return o harta a ID-urilor de traseu catre obiectele Trip
     * @throws IOException aca apare o eroare de retea, conexiunea nu merge sau serverul nu raspunde
     * @throws JSONException daca datele JSON nu sunt valide(alt format)
     */
    private Map<String,Trip> loadTrips()throws IOException,JSONException{
    	String tripsJson = fetchData(TRIPS_ENDPOINT);
        JSONArray tripsArray = new JSONArray(tripsJson);
        Map<String, Trip> tempTripsMap = new HashMap<>();
        for (int i = 0; i < tripsArray.length(); i++) {
            Trip trip = Trip.fromJson(tripsArray.getJSONObject(i));
            tempTripsMap.put(trip.id, trip);
        }
		return tempTripsMap;
    }
    
    //fct mici pt a nu fi incarcat codul din fct principala
    /**
     * Actualizeaza interfata pentru a indica faptul ca datele au fost incarcate cu succes
     */
    private void loadingSuccess() {
    	 statusLabel.setText("Data loaded. Ready.");
         trackButton.setEnabled(true);
    }
    
    /**
     * Afiseaza un mesaj de avertizare daca incarcarea datelor e intrerupta
     * @param message mesajul de avertizare care va fi afisat
     */
    private void loadingWarning(String message) {
    	statusLabel.setText("Data loading interrupted.");
    	JOptionPane.showMessageDialog(this, message,"Warning", JOptionPane.WARNING_MESSAGE);
    }
    
    /**
     * Afiseaza un mesaj de eroare in cazul esuarii incarcarii datelor statice 
     * @param message mesajul de eroare care va fi afisat
     */
    private void loadingFail(String message) {
    	statusLabel.setText("Failed to load static data.");
    	JOptionPane.showMessageDialog(this, message,"Data Loading Error", JOptionPane.ERROR_MESSAGE);
    }
    
    /**
     * Gestioneaza eorile aparute in timpul incarcarii datelor statice
     * afiseaza un mesaj de eroare detaliat
     * @param cause cauza exceptiei aparute
     */
    private void onLoadingException(Throwable cause) {
    	statusLabel.setText("Error loading static data!");
    	 String errorMsg = "Failed to load necessary route/trip data.\n";
         if (cause instanceof IOException) {
             errorMsg += "Network or API Error: " + cause.getMessage();
         } else if (cause instanceof JSONException) {
              errorMsg += "Error parsing data from API. Check JSON format.";
              System.err.println("JSON Parsing Error: " + cause.getMessage());
          } else {
              errorMsg += "An unexpected error occurred: " + cause.getMessage();
          }
         JOptionPane.showMessageDialog(InterfataGrafica.this, errorMsg, "Data Loading Error", JOptionPane.ERROR_MESSAGE);
    }
    
    
//folosit la animatie ca sa incetineasca cand ajunge aprope de bara de sus
    //din nou mult slop aici
    /**
     * Aplica functia pentru a obtine o tranzitie cat mai lina in animatie
     * @param t progresul liniar al animatiei
     * @return progresul ajustat conform curbei de easing
     */
    private float easeOutSine(float t) {
        return (float) Math.sin(t * Math.PI / 2.0);
    }

    /**
     * Porneste animatia pentru campul de input si butonul de tracking
     */
     private void startAnimation() {
        String vehicleId = vehicleIdInput.getText().trim();
        if (vehicleId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a Bus/Tram Label or Route.", "Input Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!trackButton.isEnabled()) {
            JOptionPane.showMessageDialog(this, "Static data is still loading or failed to load. Please wait.", "Please Wait", JOptionPane.WARNING_MESSAGE);
            return;
        }

        trackButton.setEnabled(false);
        prepareAnimation();
        
        animationStartTime = System.currentTimeMillis();
        if (animationTimer != null && animationTimer.isRunning()) {
            animationTimer.stop();
        }

        animationTimer = new Timer(TIMER_DELAY_MS, new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) {
                long now = System.currentTimeMillis();
                long elapsed = now - animationStartTime;
                float linearProgress = Math.min(1.0f, (float) elapsed / ANIMATION_DURATION_MS);
                float easedProgress = easeOutSine(linearProgress);

                updateInputAnimation(easedProgress);
                updateButtonAnimation(easedProgress);
                inputPanel.repaint();

                if (linearProgress >= 1.0f) {
                    animationTimer.stop();
                    onAnimationFinished();
                }
             }
        });
        animationTimer.start();
    }
     /**
      * Pregateste parametrii initiali necesari pentru animatie
      */
     private void prepareAnimation() {

         initialPosInput = vehicleIdInput.getLocation();
         initialSizeInput = vehicleIdInput.getSize();
         targetWidth = inputPanel.getWidth() - (2 * horizontalPadding);

         inputPanel.setLayout(null);
         vehicleIdInput.setBounds(initialPosInput.x, initialPosInput.y, initialSizeInput.width, initialSizeInput.height);

         Point initialPosButton = trackButton.getLocation();
         Dimension initialSizeButton = trackButton.getSize();
         trackButton.setBounds(initialPosButton.x, initialPosButton.y, initialSizeButton.width, initialSizeButton.height);

         inputPanel.add(vehicleIdInput);
         inputPanel.add(trackButton);
         inputPanel.revalidate();
         inputPanel.repaint();
     }
     
     /**
      * Actualizeaza pozitia si dimensiunea campului de input in functie de progresul animatiei
      * @param progress progresul animatiei, intre 0=start si 1=final
      */
     private void updateInputAnimation(float progress) {
    	 
         int targetYInput = targetYText;
         int newYInput = Math.round(initialPosInput.y + (targetYInput - initialPosInput.y) * progress);
         int newWidth = Math.round(initialSizeInput.width + (targetWidth - initialSizeInput.width) * progress);
         int centeredTargetX = (inputPanel.getWidth() - newWidth) / 2;
         int newXInput = Math.round(initialPosInput.x + (centeredTargetX - initialPosInput.x) * progress);
         int currentHeightInput = initialSizeInput.height;
         
         vehicleIdInput.setBounds(newXInput, newYInput, newWidth, currentHeightInput);
     }
     
     /**
      * Actualizeaza transparenta si vizibilitatea butonului de tracking in functie de progresul animatiei
      * @param progress progresul animatiei, intre 0=start si 1=final
      */
     private void updateButtonAnimation(float progress) {
    	 float newAlpha = initialButtonAlpha + (targetButtonAlpha - initialButtonAlpha) * progress;
         if (trackButton instanceof FadeButton) {
             ((FadeButton) trackButton).setAlpha(newAlpha);
         } else {
             trackButton.setVisible(newAlpha > 0.05f);
         }
     }
    private void onAnimationFinished() {
    	resetInputPanel();
    	resetTopPanelLayout();
    	resetButtonState();
    	refreshUI();
    	
        fetchAndDisplayVehicleData();
    }
    
    //fiecare fct verifica conditia de null -> se reduc sansele de aparitie a erorilor
    
    private void resetInputPanel() {
    	if(inputPanel != null) {
    		inputPanel.remove(vehicleIdInput);
            inputPanel.remove(trackButton);
    	}
    	if(contentPane != null) {
    		contentPane.remove(inputPanel);
    	}
    }
    
    private void resetTopPanelLayout() {
    	if(topPanel!= null) {
    		topPanel.removeAll();
            GridBagConstraints gbcTop = new GridBagConstraints();
            gbcTop.gridx = 0; gbcTop.gridy = 0;
            gbcTop.fill = GridBagConstraints.HORIZONTAL;
            gbcTop.weightx = 1.0;
            gbcTop.insets = new Insets(0, 0, 5, 0);
            topPanel.add(vehicleIdInput, gbcTop);
    	}
    	if(contentPane != null) {
    		 contentPane.add(topPanel, BorderLayout.NORTH);
    	     contentPane.add(resultsScrollPane, BorderLayout.CENTER);
    	}
    }
    
    private void resetButtonState() {
    	if (trackButton instanceof FadeButton) {
            ((FadeButton) trackButton).setAlpha(initialButtonAlpha);
        }
    	if(trackButton != null) {
        trackButton.setVisible(false);
    	}
    }
    
    private void refreshUI() {
    	if(contentPane != null) {
    		 contentPane.revalidate();
    	     contentPane.repaint();
    	}
    }

    private void fetchAndDisplayVehicleData() {
        String targetId = vehicleIdInput.getText().trim().toLowerCase();
        resultsPanel.removeAll();
        resultsPanel.add(new JLabel("Fetching data for '" + vehicleIdInput.getText().trim() + "'..."));
        resultsPanel.revalidate();
        resultsPanel.repaint();

        SwingWorker<List<DisplayVehicleInfo>, String> worker = new SwingWorker<List<DisplayVehicleInfo>, String>() {
            @Override
            protected List<DisplayVehicleInfo> doInBackground() throws Exception {
                List<DisplayVehicleInfo> foundVehicles = new ArrayList<>();
                try {
                    publish("Fetching vehicle data...");
                    String vehiclesJson = fetchData(VEHICLES_ENDPOINT);
                    JSONArray vehiclesArray = new JSONArray(vehiclesJson);
                    publish("Processing " + vehiclesArray.length() + " vehicles...");

                    for (int i = 0; i < vehiclesArray.length(); i++) {
                        JSONObject vehicleJson = vehiclesArray.getJSONObject(i);
                        try {
                            Vehicle vehicle = Vehicle.fromJson(vehicleJson);

                            Route route = (vehicle.routeId != null) ? routesMap.get(vehicle.routeId) : null;
                            String routeShortName = (route != null) ? route.shortName.toLowerCase() : "";

                            Trip trip = (vehicle.tripId != null) ? tripsMap.get(vehicle.tripId) : null;
                            String tripHeadsign = (trip != null) ? trip.headsign : "N/A";

                            boolean labelMatch = vehicle.label.toLowerCase().equals(targetId);
                            boolean routeMatch = !routeShortName.isEmpty() && routeShortName.equals(targetId);

                            if (labelMatch || routeMatch) {
                                DisplayVehicleInfo displayInfo = new DisplayVehicleInfo();
                                displayInfo.vehicle = vehicle;
                                displayInfo.routeShortName = (route != null) ? route.shortName : "N/A";
                                displayInfo.tripHeadsign = tripHeadsign;
                                foundVehicles.add(displayInfo);
                            }
                        } catch (JSONException jsonEx) {
                             System.err.println("Skipping vehicle due to JSON parsing error: " + jsonEx.getMessage() + " in JSON: " + vehicleJson.toString(2));
                        }
                    }
                } catch (IOException | JSONException e) {
                     System.err.println("Error fetching or parsing vehicle data: " + e.getMessage());
                     e.printStackTrace();
                     throw e;
                }
                return foundVehicles;
            }

             @Override
            protected void process(List<String> chunks) {
                if (!chunks.isEmpty()) {
                     resultsPanel.removeAll();
                     resultsPanel.add(new JLabel(chunks.get(chunks.size() - 1)));
                     resultsPanel.revalidate();
                     resultsPanel.repaint();
                }
            }


            @Override
            protected void done() {
                resultsPanel.removeAll();
                try {
                    List<DisplayVehicleInfo> vehicles = get();
                    if (vehicles.isEmpty()) {
                        resultsPanel.add(new JLabel("No active vehicles found matching '" + vehicleIdInput.getText().trim() + "'."));
                    } else {
                        JLabel headerLabel = new JLabel("Found " + vehicles.size() + " vehicle(s) matching '" + vehicleIdInput.getText().trim() + "':");
                        headerLabel.setFont(headerLabel.getFont().deriveFont(Font.BOLD));
                         resultsPanel.add(headerLabel);
                         resultsPanel.add(Box.createRigidArea(new Dimension(0, 15)));

                        for (DisplayVehicleInfo info : vehicles) {
                            JPanel vehiclePanel = createVehiclePanel(info);
                            resultsPanel.add(vehiclePanel);
                            resultsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
                         }
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.err.println("Vehicle data fetch interrupted: " + e.getMessage());
                    resultsPanel.add(new JLabel("Vehicle data fetch was interrupted."));
                } catch (ExecutionException e) {
                    e.printStackTrace();
                    resultsPanel.add(new JLabel("Error fetching vehicle data:"));
                    Throwable cause = e.getCause();
                    String errorMsg = (cause != null) ? cause.getMessage() : e.getMessage();

                    JTextArea errorArea = new JTextArea(errorMsg);
                    errorArea.setEditable(false);
                    errorArea.setLineWrap(true);
                    errorArea.setWrapStyleWord(true);
                    errorArea.setBackground(resultsPanel.getBackground());
                    errorArea.setFont(UIManager.getFont("Label.font"));
                    errorArea.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));

                    JScrollPane errorScrollPane = new JScrollPane(errorArea);
                    errorScrollPane.setBorder(null);
                    resultsPanel.add(errorScrollPane);

                     System.err.println("ExecutionException Cause: " + cause);
                } finally {
                     resultsPanel.revalidate();
                     resultsPanel.repaint();
                     trackButton.setEnabled(true);
                }
            }
        };
        worker.execute();
    }
//functie importanta, nu alterati prea tare
    
    private JPanel createVehiclePanel(DisplayVehicleInfo info) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(10, 5, 10, 5)
        ));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel titleLabel = new JLabel(String.format("Vehicle: %s (Route: %s)", info.vehicle.label, info.routeShortName));
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, titleLabel.getFont().getSize() + 1f));
        panel.add(titleLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));

        panel.add(new JLabel(String.format("Destination: %s", info.tripHeadsign)));
        panel.add(Box.createRigidArea(new Dimension(0, 8)));
        
        JPanel detailsPanel = new JPanel(new GridBagLayout());
        detailsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(1, 0, 1, 10);
        
        //ciudat, poate incercam sa vedem ce inseamna N/A (not applicable) poate indica ca trebuei sa mearga in DEPOU
        //de discutat
        String locationStr = "N/A";
        if (info.vehicle.latitude != null && info.vehicle.longitude != null) {
            locationStr = String.format("%.5f, %.5f", info.vehicle.latitude, info.vehicle.longitude);
        }

        String speedStr = "N/A";
        if (info.vehicle.speedKmH != null) {
            speedStr = String.format("%.1f km/h", info.vehicle.speedKmH);
            //aveam probleme aici, nu imi dau seama daca era problema de ziua de paste dar aveam viteze de peste 130 la ora la unele autobuze
            //posibila eroare la Tranzy.ai de testat in cod
            if (info.vehicle.speedKmH > 100.0) { 
                speedStr += "viteza neprecisa";
            }
        }
        //recomandat aici, 
        //sectiunile: Last known position, sau Last stop
        gbc.gridx = 0; gbc.gridy = 0;
        detailsPanel.add(new JLabel("Location:"), gbc);
        gbc.gridx++;
        gbc.weightx = 1.0;
        detailsPanel.add(new JLabel(locationStr), gbc);

        gbc.gridx = 0; gbc.gridy++; gbc.weightx = 0.0;
        detailsPanel.add(new JLabel("Speed:"), gbc);
        gbc.gridx++;
        detailsPanel.add(new JLabel(speedStr), gbc);

        gbc.gridx = 0; gbc.gridy++;
        detailsPanel.add(new JLabel("Last Update:"), gbc);
        gbc.gridx++;
        detailsPanel.add(new JLabel(info.vehicle.getFormattedTimestamp()), gbc);

        gbc.gridx = 0; gbc.gridy++;
        detailsPanel.add(new JLabel("Type:"), gbc);
        gbc.gridx++;
        detailsPanel.add(new JLabel(info.vehicle.getVehicleTypeString()), gbc);

        panel.add(detailsPanel);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, panel.getPreferredSize().height));

        return panel;
    }
//basic aici
    private void setupMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        JMenu mnFont = new JMenu("Font");
        menuBar.add(mnFont);
        ActionListener fontListener = e -> updateFont();

        b12 = new JRadioButton("12");
        b12.setActionCommand("12");
        b12.setSelected(true);
        grupFont.add(b12);
        mnFont.add(b12);
        b12.addActionListener(fontListener);

        b14 = new JRadioButton("14");
        b14.setActionCommand("14");
        grupFont.add(b14);
        mnFont.add(b14);
        b14.addActionListener(fontListener);

        b16 = new JRadioButton("16");
        b16.setActionCommand("16");
        grupFont.add(b16);
        mnFont.add(b16);
        b16.addActionListener(fontListener);

        JMenu mnTheme = new JMenu("Theme");
        menuBar.add(mnTheme);
        ActionListener themeListener = this::updateTheme;

        rbLight = new JRadioButton("Light");
        rbLight.setActionCommand("Light");
        grupTheme.add(rbLight);
        mnTheme.add(rbLight);
        rbLight.addActionListener(themeListener);

        rbDark = new JRadioButton("Dark");
        rbDark.setActionCommand("Dark");
        grupTheme.add(rbDark);
        mnTheme.add(rbDark);
        rbDark.addActionListener(themeListener);

         try {
             LookAndFeel currentLaF = UIManager.getLookAndFeel();
             if (currentLaF instanceof FlatDarkLaf) {
                 rbDark.setSelected(true);
             } else {
                 rbLight.setSelected(true);
                 if (!(currentLaF instanceof FlatLightLaf)) {
                     UIManager.setLookAndFeel(new FlatLightLaf());
                 }
             }
             UIManager.put("TextComponent.arc", 10);
             UIManager.put("Button.arc", 10);

         } catch (UnsupportedLookAndFeelException e) {
             System.err.println("Failed to set initial theme state: " + e.getMessage());
             rbLight.setSelected(true);
         }

        SwingUtilities.invokeLater(this::updateFont);
    }
//fonturile 
    private void updateFont() {
        if (grupFont.getSelection() == null) return;

        String selectedSize = grupFont.getSelection().getActionCommand();
        int fontSize = Integer.parseInt(selectedSize);

        Font baseFont = UIManager.getFont("Label.font");
        if (baseFont == null) {
            baseFont = new Font(Font.SANS_SERIF, Font.PLAIN, 12);
        }

        Font newFont = baseFont.deriveFont((float)fontSize);

        updateComponentFont(this, newFont);

        SwingUtilities.updateComponentTreeUI(this);
    }

    private void updateComponentFont(java.awt.Component comp, Font font) {
        comp.setFont(font);
        if (comp instanceof java.awt.Container) {
            for (java.awt.Component child : ((java.awt.Container) comp).getComponents()) {
                updateComponentFont(child, font);
            }
        }
         if (comp instanceof JScrollPane) {
            JScrollPane scrollPane = (JScrollPane) comp;
             if (scrollPane.getViewport() != null && scrollPane.getViewport().getView() != null) {
                 updateComponentFont(scrollPane.getViewport().getView(), font);
             }
         }
    }

    private void updateTheme(ActionEvent e) {
        String theme = e.getActionCommand();
        try {
            if ("Dark".equals(theme)) {
                UIManager.setLookAndFeel(new FlatDarkLaf());
            } else {
                UIManager.setLookAndFeel(new FlatLightLaf());
            }
            UIManager.put("TextComponent.arc"	, 10);
            UIManager.put("Button.arc", 10);

            SwingUtilities.updateComponentTreeUI(this);

        } catch (UnsupportedLookAndFeelException ex) {
            System.err.println("Failed to set theme: " + ex.getMessage());
        }
    }

    private static class FadeButton extends JButton {
        private static final long serialVersionUID = 1L;
        private float alpha = 1.0f;

        public FadeButton(String text) {
            super(text);
            setOpaque(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
        }

        public void setAlpha(float alpha) {
            this.alpha = Math.max(0.0f, Math.min(1.0f, alpha));
            repaint();
        }

        public float getAlpha() {
            return alpha;
        }

        //BUTON CU FADE, functia pare ok nu e incarcata
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g.create();
            if (this.alpha < 1.0f) {
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
            }

            boolean originalContentAreaFilled = isContentAreaFilled();
            boolean originalBorderPainted = isBorderPainted();
            setContentAreaFilled(true);
            setBorderPainted(true);

            super.paintComponent(g2d);

            setContentAreaFilled(originalContentAreaFilled);
            setBorderPainted(originalBorderPainted);

            g2d.dispose();
        }

        //probabil e pentru bara de sus, nu sunt sigur
        @Override
        public Dimension getPreferredSize() {
             boolean ocf = isContentAreaFilled();
             boolean obp = isBorderPainted();
             setContentAreaFilled(true);
             setBorderPainted(true);
             Dimension size = super.getPreferredSize();
             setContentAreaFilled(ocf);
             setBorderPainted(obp);
             return size;
        }

        @Override
        public boolean contains(int x, int y) {
            return alpha > 0.1f && super.contains(x, y);
        }
    }

    private static class Vehicle {
        String id;
        String label;
        Double latitude;
        Double longitude;
        long timestampEpochSeconds;
        int vehicleType;
        //consider inutil momentan adaugam poate mai tarziu
//        String bikeAccessible;
//        String wheelchairAccessible;
        Double speedKmH;
        String routeId;
        String tripId;

        static Vehicle fromJson(JSONObject json) throws JSONException {
            Vehicle v = new Vehicle();

            Object idObj = json.opt("id");
            v.id = (idObj == null) ? null : idObj.toString();
            if (v.id == null) {
                throw new JSONException("Vehicle ID is missing or null in JSON");
            }

            v.label = json.optString("label", v.id);

            if (json.has("latitude") && !json.isNull("latitude")) {
                v.latitude = json.optDouble("latitude", Double.NaN);
                if (Double.isNaN(v.latitude)) v.latitude = null;
            } else {
                v.latitude = null;
            }

             if (json.has("longitude") && !json.isNull("longitude")) {
                v.longitude = json.optDouble("longitude", Double.NaN);
                 if (Double.isNaN(v.longitude)) v.longitude = null;
            } else {
                v.longitude = null;
            }
             //aici sunt probleme, nu merge
            try {
                v.timestampEpochSeconds = json.getLong("timestamp");
            } catch (JSONException e) {
                String tsString = json.optString("timestamp", null);
                if (tsString != null) {
                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                        sdf.setTimeZone(TimeZone.getTimeZone("UTC")); // <<< Uses TimeZone here
                        Date date = sdf.parse(tsString);
                        v.timestampEpochSeconds = date.getTime() / 1000L;
                    } catch (ParseException pe) {
                        System.err.println("Could not parse timestamp string: " + tsString + " - " + pe.getMessage());
                        v.timestampEpochSeconds = 0;
                    }
                } else {
                    System.err.println("Timestamp missing or not a recognized number/string in JSON: " + json.opt("timestamp"));
                    v.timestampEpochSeconds = 0;
                }
            }

            v.vehicleType = json.optInt("vehicle_type", -1);
//            v.bikeAccessible = json.optString("bike_accessible", "UNKNOWN");
//            v.wheelchairAccessible = json.optString("wheelchair_accessible", "UNKNOWN");
            // aici viteza e transformata in km/h teoretic corect din nou nu sunt sigur de ce sunt erori in viteza 
            if (json.has("speed") && !json.isNull("speed")) {
                double speedMs = json.optDouble("speed", Double.NaN);
                if (!Double.isNaN(speedMs)) {
                     v.speedKmH = speedMs * 3.6;
                } else {
                    v.speedKmH = null;
                }
            } else {
                v.speedKmH = null;
            }
//aici se repeta astea doua 
            //nu cred ca e bine trebuie sa clarificam diferenta dintre route si trip
            //1
            Object routeIdObj = json.opt("route_id");
             if (routeIdObj == null || routeIdObj == JSONObject.NULL) {
                 v.routeId = null;
             } else {
                 String routeIdStr = routeIdObj.toString();
                 if ("0".equals(routeIdStr)) {
                     v.routeId = null;
                 } else {
                     v.routeId = routeIdStr;
                 }
             }
            //2
            Object tripIdObj = json.opt("trip_id");
              if (tripIdObj == null || tripIdObj == JSONObject.NULL) {
                  v.tripId = null;
              } else {
                   v.tripId = tripIdObj.toString();
              }

            return v;
        }
        String getFormattedTimestamp() {
            if (timestampEpochSeconds <= 0) return "N/A";
            Date date = new Date(timestampEpochSeconds * 1000L);
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            return sdf.format(date);
        }

        String getVehicleTypeString() {
            switch (vehicleType) {
                case 0: return "Tram";
                case 3: return "Bus";
                default: return "Unknown (" + vehicleType + ")";
            }
        }
    }

    private static class Route {
        String id;
        String shortName;
        String longName;
        String color;
        int type;
        String desc;

        static Route fromJson(JSONObject json) throws JSONException {
            Route r = new Route();
            r.id = String.valueOf(json.getInt("route_id"));
            r.shortName = json.optString("route_short_name", "");
            r.longName = json.optString("route_long_name", "");
            r.color = json.optString("route_color", "FFFFFF");
            r.type = json.optInt("route_type", -1);
            r.desc = json.optString("route_desc", "");
            return r;
        }
    }

    private static class Trip {
        String id;
        String routeId;
        String headsign;
        int directionId;

        static Trip fromJson(JSONObject json) throws JSONException {
            Trip t = new Trip();
             Object tripIdObj = json.opt("trip_id");
             t.id = (tripIdObj == null) ? null : tripIdObj.toString();
            if (t.id == null) {
                 throw new JSONException("Trip ID is missing or null in JSON");
            }

            t.routeId = String.valueOf(json.getInt("route_id"));

            t.headsign = json.optString("trip_headsign", "N/A");
            t.directionId = json.optInt("direction_id", -1);
            return t;
        }
    }

    private static class DisplayVehicleInfo {
        Vehicle vehicle;
        String routeShortName;
        String tripHeadsign;
    }


    public static void main(String[] args) {
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