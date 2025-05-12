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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * Clasa {@code InterfataGrafica} reprezinta fereastra pricipala a aplicatiei,
 * ce gestioneaza interfata grafica si comunicarea cu API-ul Tranzy OpenData.
 * Aceasta clasa extinde {@link JFrame} si include componente pentru introducerea datelor,
 * afisarea rezultatelor, selectarea temei si a dimensiunii fontului, precum si elemente 
 * pentru animatii simple ale interfetei.
 * Utilizeaza un API extern pentru a prelua informatii despre vehicule, rute, curse si statii.
 */

public class InterfataGrafica extends JFrame {
    /** Serial version UID generat automat pentru clasa JFrame */
    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    
    /** Camp text unde utilizatorul introduce ID-ul vehiculului de urmarit */
    private JTextField vehicleIdInput;
    
    /** 
     * Grup de butoane pentru a asigura selectarea exclusiva a unui singur radio button
     * pentru dimensiunea fontului
     */
    private final ButtonGroup grupFont = new ButtonGroup();
    
    /**
     * Grup de butoane pentru a asigura selectarea exclusiva a unui singur radio button
     * pentru tema interfetei
     */
    private final ButtonGroup grupTheme = new ButtonGroup();
    
    /** Buton pentru initierea urmaririi vehiculului */
    private JButton trackButton;
    
    /** Radio buttons pentru selectarea dimensiunii fontului */
    private JRadioButton b12, b14, b16;
    
    /** Radio buttons pentru selectarea temei (luminoasa/intunecata) */
    private JRadioButton rbLight, rbDark;

    /** Initializare buton pentru revenire la landing page */
    private JButton backButton;

    /** Timer folosit pentru animarea tranzitiilor UI */
    private Timer animationTimer;
    
    /** Durata totala a unei animatii, in milisecunde */
    private final int ANIMATION_DURATION_MS = 500;
    
    /** Intervalul de timp intre doua actualizari ale animatiei, in milisecunde */
    private final int TIMER_DELAY_MS = 10;
    
    /** Momentul de start al animatiei, in milisecunde */
    private long animationStartTime;
    
    /** Pozitia initiala a campului de introducere */
    private Point initialPosInput;
    
    /** Dimensiunea initiala a campului de introducere */
    private Dimension initialSizeInput;
    
    /** Pozitia tinta pe axa Y pentru textul de input */
    private int targetYText = 10;
    
    /** Latimea tinta pentru campul de input */
    private int targetWidth;
    
    /** Spatiul orizontal folosit in layout/animatie */
    private int horizontalPadding = 10;
    
    /** Opacitatea initiala a butonului de urmarire */
    private float initialButtonAlpha = 1.0f;
    
    /** Opacitatea tinta a butonului de urmarire dupa animatie */
    private float targetButtonAlpha = 0.0f;
    
    /** Panou pentru campul de introducere */
    private JPanel inputPanel;
    
    /** Panou superior pentru titlu si selectii */
    private JPanel topPanel;
    
    /** Panou destinat afisarii rezultatelor */
    private JPanel resultsPanel;
    
    /** ScrollPane pentru lista de rezultate */
    private JScrollPane resultsScrollPane;
    
    /** Eticheta pentru afisarea statusului aplicatiei */
    private JLabel statusLabel;
    
    /** Initializare panel buton back pentru pozitionarea sa in interfata  */
    private JPanel backButtonPanel;
    
    /** Layout ul gridbag pentru pozitonare elemente */
    private GridBagConstraints gbc = new GridBagConstraints();
    
    /** Manager pentru operațiunile cu date */
    private DataManager dataManager;
    
/**
 * Constructorul {@code InterfataGrafica} initializeaza si configureaza 
 * interfata grafica a aplicatiei.
 * Seteaza titlul, dimensiunile, layout-ul, content-ul. 
 * 
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
        
        // Inițializare DataManager și încărcare date
        dataManager = new DataManager();
        loadStaticData();
    }
    
    /**
     * Incarca date statice, rute si trasee, folosind SwingWorker
     * Actualizeaza interfata in functie de succesul sau esecul operatiei
     */
    private void loadStaticData() {
        statusLabel.setText("Loading Data...");
        trackButton.setEnabled(false);

        SwingWorker<Boolean, Void> staticDataLoader = new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                try {
                    dataManager.loadRoutes();
                    dataManager.loadTrips();
                    dataManager.loadStops();
                    dataManager.loadStopTimes();
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
                        loadingFail("Failed to load necessary route/trip data.\nPlease check configuration or network.");
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
     
     /**
      * Metoda apelata la finalizarea animatiei.
      * Reseteaza panourile de input si top, reseteaza starea butonului, 
      * actualizeaza interfata grafica si initiaza incarcarea datelor despre vehicule.
      */
    private void onAnimationFinished() {
        resetInputPanel();
        resetTopPanelLayout();
        resetButtonState();
        refreshUI();
        
        fetchAndDisplayVehicleData();
    }
    
    /**
     * Elimina componentele de input si butonul de urmarire din panoul de input si din cel principal.
     * Verifica daca nu sunt null pentru a evita eventualele erori la rulare.
     */
    private void resetInputPanel() {
        if(inputPanel != null) {
            inputPanel.remove(vehicleIdInput);
            inputPanel.remove(trackButton);
        }
        if(contentPane != null) {
            contentPane.remove(inputPanel);
        }
    }
    
    /**
     * Reseteaza panoul superior si rearanjeaza componentele principale.
     * Elimina componentele asociate panoului de sus si adauga un nou imput pentru ID-ul vehiculului.
     */
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
    
    /**
     * Reseteaza starea vizuala a butonului de urmarire.
	 */
    private void resetButtonState() {
    	if (trackButton instanceof FadeButton) {
            ((FadeButton) trackButton).setAlpha(initialButtonAlpha);
        }
    	if(trackButton != null) {
        trackButton.setVisible(false);
    	}
    }
    
    /**
     * Actualizeaza interfata grafica prin revalidarea si reimprospatarea panoului principal.
	 */
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
                    String vehiclesJson = dataManager.fetchData("date_vehicule.json");
                    JSONArray vehiclesArray = new JSONArray(vehiclesJson);
                    publish("Processing " + vehiclesArray.length() + " vehicles...");

                    for (int i = 0; i < vehiclesArray.length(); i++) {
                        JSONObject vehicleJson = vehiclesArray.getJSONObject(i);
                        try {
                            Vehicle vehicle = Vehicle.fromJson(vehicleJson);

                            Route route = (vehicle.routeId != null) ? 
                                dataManager.getRoutesMap().get(vehicle.routeId) : null;
                            String routeShortName = (route != null) ? route.shortName.toLowerCase() : "";

                            Trip trip = (vehicle.tripId != null) ? 
                                dataManager.getTripsMap().get(vehicle.tripId) : null;
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
                     //fct noua pt buton de back
                     addBackButton();
                }
            }
        };
        worker.execute();
    }
    
    //adaug butonul in dreapta jos
    /**
     * adauga butonul in coltul din dreapta sus
     * se verifica existenta sa pentru a evita crearea multipla
     */
    
    private void addBackButton() {
    	
    	
    	backButtonPanel = new JPanel();
        backButtonPanel.setLayout(null);  // layout null pentru a plasa butonul manual       
        backButtonPanel.setPreferredSize(new Dimension(450, 67));

        if(backButton ==null) {
        
        // creare buton Back
        backButton = new JButton("Back");
        backButton.setBounds(335, 37, 75, 30); 
        backButton.setFocusable(false);
        backButton.setVisible(true);  
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                goBackToInitial();
               
            }
        });

        
        backButtonPanel.add(backButton); 
        contentPane.add(backButtonPanel, BorderLayout.NORTH); 

        contentPane.revalidate();
        contentPane.repaint();  
    	
        }
    }
 

//pt claritate, mai pot fi facute modificari
//posibil sa poata fi apelata si sus la generarea initiala????
    /**
     * opreste animatiile si reconstruieste interfata
     */
    private void goBackToInitial() {
    	if(animationTimer != null && animationTimer.isRunning()) {
    		animationTimer.stop();
    	}
    	
    	//golesc contentPane
    	if(contentPane != null) {
    		contentPane.removeAll();
    	}
    	
    	//sterg butonul de back
    	if (backButtonPanel != null) {
            contentPane.remove(backButtonPanel);
            backButtonPanel = null;
            backButton = null;
            contentPane.revalidate();
            contentPane.repaint();
        }
    	
    	// reconstruire
        inputPanel.removeAll();
        inputPanel.setLayout(new GridBagLayout());
        inputPanel.setBorder(new EmptyBorder(30, 30, 30, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 5, 10, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        gbc.gridx = 0;
        gbc.gridy = 0;
        inputPanel.add(vehicleIdInput, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.ipady = 10;
        gbc.insets = new Insets(20, 5, 10, 5);
        inputPanel.add(trackButton, gbc);

        if (trackButton instanceof FadeButton) {
            ((FadeButton) trackButton).setAlpha(1.0f);
        }
        trackButton.setVisible(true);
        trackButton.setEnabled(true);

        statusLabel.setText("Ready to search.");
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.ipady = 0;
        gbc.insets = new Insets(10, 5, 10, 5);
        inputPanel.add(statusLabel, gbc);

        contentPane.add(inputPanel, BorderLayout.CENTER);

        SwingUtilities.updateComponentTreeUI(this);
        contentPane.revalidate();
        contentPane.repaint();
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
                speedStr += " (viteza neprecisa)";
            }
        }
        
        // Sectiunea de informatii despre locatie si date ale vehiculului
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
        
        // Adaugare sectiune pentru Last Stop
        JPanel stopInfoPanel = new JPanel(new BorderLayout());
        stopInfoPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        stopInfoPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        
        // Utilizare dataManager pentru a afla informatia despre cea mai apropiata statie
        String stopInfo = dataManager.findClosestStopName(info.vehicle);
        JLabel stopLabel = new JLabel(stopInfo);
        stopLabel.setFont(stopLabel.getFont().deriveFont(Font.ITALIC));
        stopInfoPanel.add(stopLabel, BorderLayout.CENTER);
        
        panel.add(stopInfoPanel);
        
        //ATENTIE
        //maximum size trebuie pus dupa ce sunt puse toate componentele
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, panel.getPreferredSize().height));
        
        return panel;
    }
    
    //functii pentru functionarea grafica a interfetei
    /**
     * 
     */
    private void updateFont() {
        if (grupFont.getSelection() == null) return;

        String selectedSize = grupFont.getSelection().getActionCommand();
        int fontSize = Integer.parseInt(selectedSize);

        Font baseFont = UIManager.getFont("Label.font");
        if (baseFont == null) {
            baseFont = new Font(Font.SANS_SERIF, Font.PLAIN, 12);
        }

        Font newFont = baseFont.deriveFont((float) fontSize);

        // Aplică fontul recursiv
        updateComponentFont(this, newFont);

        // Doar o singură dată UI refresh
        SwingUtilities.updateComponentTreeUI(this);
        this.revalidate();
        this.repaint();
    }
    
    /**
     * 
     * @param comp
     * @param font
     */
    private void updateComponentFont(Component comp, Font font) {
        comp.setFont(font);

        // Tratează JScrollPane separat, o singură dată
        if (comp instanceof JScrollPane scrollPane) {
            Component view = scrollPane.getViewport().getView();
            if (view != null) {
                updateComponentFont(view, font);
            }
        }

        // Recursiv pentru containere
        if (comp instanceof Container container) {
            for (Component child : container.getComponents()) {
                updateComponentFont(child, font);
            }
        }
    }
    /**
     * Actualizeaza tema aplicatiei in functie de alegerea utilizatorului
     * @param e evenimentul generat de selectia temei
     */
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
                
                //revalidare completa
                refreshUI();
                resetBackgrounds();

            } catch (UnsupportedLookAndFeelException ex) {
                System.err.println("Failed to set theme: " + ex.getMessage());
            }
        }
        
        /**
         * Reseteaza culoarea de fundal a tuturor componentelor pentru a se potrivi cu tema
         * Asigura uniformitatea culorii de fundal dupa schimbarea temei
         */
        private void resetBackgrounds() {
        	Color defaultBg=UIManager.getColor("Panel.background");
        	if(contentPane != null) contentPane.setBackground(defaultBg);
        	if(inputPanel != null) inputPanel.setBackground(defaultBg);
        	if(topPanel != null) topPanel.setBackground(defaultBg);
        	if(resultsPanel != null) resultsPanel.setBackground(defaultBg);
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
};