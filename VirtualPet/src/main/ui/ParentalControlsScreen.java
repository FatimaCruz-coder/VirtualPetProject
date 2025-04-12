package group33.VirtualPet.src.main.ui;

import group33.VirtualPet.src.main.game.GameSaveManager;
import group33.VirtualPet.src.main.model.ParentalSettings;
import group33.VirtualPet.src.main.ui.UIUtility.BackgroundPanel;
import group33.VirtualPet.src.main.ui.UIUtility.PixelatedButton;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
/**
 * A Parental control screen for the Virtual Pet game that provides:
 * <ul>
 *   <li>Password-protected access to parental controls</li>
 *   <li>Time restriction settings (allowed play hours)</li>
 *   <li>Player statistics tracking (play time, sessions)</li>
 *   <li>Pet revival functionality for dead pets in save files</li>
 * </ul>
 * 
 * <p>The screen features three tabbed panels with custom pixelated UI elements
 * and background images. It integrates with {@link GameSaveManager} to persist
 * settings and statistics.
 * 
 * <p>Key Features:
 * <ul>
 *   <li><b>Login Panel</b>: Password authentication (default: "1234")</li>
 *   <li><b>Time Restrictions</b>: Set allowed play hours with 24-hour spinners</li>
 *   <li><b>Statistics</b>: Tracks total play time, session count, and averages</li>
 *   <li><b>Pet Revival</b>: Select save files to revive dead pets</li>
 * </ul>
 * 
 * <p>Example Usage:
 * <pre>
 * ParentalControlsScreen controls = new ParentalControlsScreen();
 * controls.setVisible(true);
 * </pre>
 * 
 * @author Team 33 (Dhir, Kostya, Fatima, Anna)
 * @since Winter 2025
 * 
 */
public class ParentalControlsScreen extends JFrame {
    private static final String PASSWORD = "1234"; // Hardcoded password
    private JPanel loginPanel;
    private JPanel controlsPanel;
    
    // Content panels for different sections
    private JPanel timeRestrictionsPanel;
    private JPanel statisticsPanel;
    private JPanel revivePetPanel;
    private JPanel activePanel; // Tracks the currently displayed panel
    
    // Navigation buttons
    private PixelatedButton timeRestrictionsButton;
    private PixelatedButton statisticsButton;
    private PixelatedButton revivePetButton;
    
    // Time restriction components
    private JCheckBox enableTimeRestrictionCheckbox;
    private JSpinner startTimeSpinner;
    private JSpinner endTimeSpinner;
    private JButton saveTimeRestrictionButton;
    
    // Statistics components
    private JLabel totalPlayTimeLabel;
    private JLabel averageSessionTimeLabel;
    private JLabel sessionCountLabel;
    private JLabel currentSessionTimeLabel;
    private JButton resetStatsButton;
    
    // Pet revival components
    private JComboBox<String> nameDropdown;
    private JButton revivePetActionButton;
    
    // Return to main menu
    private JButton returnButton;
    private JButton loginButton;
    private JButton backButton;
    
    // Timer for updating session time
    private Timer sessionTimer;
    private int currentSessionSeconds = 0;
    private ParentalSettings currentSettings;
    
    /**
     * Constructs the parental controls screen with:
     * - 1031x849 pixel window
     * - Animated background
     * - Initial login panel
     * - Automatic session tracking
     * 
     */
    public ParentalControlsScreen() {
        // Set up the frame
        setTitle("Parental Controls - Pixel Pals");
        setSize(1031, 849);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Create a custom background panel
        BackgroundPanel backgroundPanel = new BackgroundPanel("group33/VirtualPet/assets/images/new_game_background.gif");
        setContentPane(backgroundPanel);
        backgroundPanel.setLayout(new BorderLayout());
        
        // Create the login panel (initially visible)
        createLoginPanel();
        
        // Create the controls panel (initially invisible)
        createControlsPanel();
        
        // Start with login panel visible
        backgroundPanel.add(loginPanel, BorderLayout.CENTER);

        // Enable key inputs
        setupKeyboardShortcuts();
        
        // Load existing parental settings
        loadParentalSettings();
        
        // Start tracking session time
        startSessionTracking();
        
        // Set up session timer to update every second
        setupSessionTimer();
    }
    
    /**
     * Starts tracking a new play session by loading or creating parental settings.
     * Initializes currentSettings if none exists and begins a new session.
     */
    private void startSessionTracking() {
        try {
            // Load or create parental settings
            currentSettings = GameSaveManager.loadParentalSettings();
            if (currentSettings == null) {
                currentSettings = new ParentalSettings();
            }
            
            // Start the session
            currentSettings.startSession();
            
        } catch (IOException e) {
            System.err.println("Error starting session tracking: " + e.getMessage());
        }
    }

    /**
     * Sets up a timer that updates the current session display every second.
     * The timer increments currentSessionSeconds and refreshes the UI.
     */
    private void setupSessionTimer() {
        sessionTimer = new Timer(1000, e -> {
            currentSessionSeconds++;
            updateCurrentSessionDisplay();
        });
        sessionTimer.start();
    }
    
    /**
     * Updates the current session time display label.
     * Uses either ParentalSettings duration or fallback to local timer.
     */
    private void updateCurrentSessionDisplay() {
        if (currentSessionTimeLabel != null) {
            Duration sessionDuration;
            
            if (currentSettings != null) {
                // Get duration from ParentalSettings
                sessionDuration = currentSettings.getCurrentSessionDuration();
            } else {
                // Fallback to timer if settings aren't available
                sessionDuration = Duration.ofSeconds(currentSessionSeconds);
            }
            
            currentSessionTimeLabel.setText(formatDuration(sessionDuration));
        }
    }
    

    /**
     * Creates the password entry panel with:
     * - Title label
     * - Password field
     * - Login/return buttons
     * 
     * On successful login (password "1234"), switches to controls panel.
     */
    private void createLoginPanel() {
        loginPanel = new JPanel(new GridBagLayout());
        loginPanel.setOpaque(false);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(50, 50, 50, 50);
        
        // Title
        JLabel titleLabel = UIUtility.createPixelatedLabel2("Parental Controls", 25f, Color.WHITE);
        gbc.insets = new Insets(90, 50, 90, 50);
        loginPanel.add(titleLabel, gbc);
        
        // Reset insets for other components
        gbc.insets = new Insets(30, 0, 0, 80);
        // Password field
        JLabel passwordLabel = UIUtility.createPixelatedLabel2("Enter Parent Password: ", 15, Color.WHITE);
        loginPanel.add(passwordLabel, gbc);

        // Reset insets for other component
        gbc.insets = new Insets(15, 45, 0, 60);
        JPasswordField passwordField = new JPasswordField(6);
        loginPanel.add(passwordField, gbc);

        // Login button
        UIUtility.button_fontsize = 16f;
        UIUtility.button_width = 150;
        UIUtility.button_height = 40;
        loginButton = new UIUtility.PixelatedButton("Login");
        loginButton.setToolTipText("Press Enter");
        
        // Back button
        backButton = new UIUtility.PixelatedButton("Exit");
        backButton.setToolTipText("Press ESC");
                
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setOpaque(false);
        buttonPanel.add(loginButton);
        buttonPanel.add(backButton);
        
        gbc.insets = new Insets(30, 50, 10, 50);
        loginPanel.add(buttonPanel, gbc);
        
        // Add action listeners
        setupKeyboardShortcuts();
        loginButton.addActionListener(e -> {
            String enteredPassword = new String(passwordField.getPassword());
            if (enteredPassword.equals(PASSWORD)) {
                switchToControlsPanel();
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Incorrect password. Please try again.", 
                    "Login Failed", 
                    JOptionPane.ERROR_MESSAGE);
                passwordField.setText("");
            }
        });
        
        backButton.addActionListener(e -> returnToMainMenu());
    }
    
    /**
     * Creates the password entry panel with:
     * - Title label
     * - Password field
     * - Login/return buttons
     * 
     * On successful login (password "1234"), switches to controls panel.
     */
    private void createControlsPanel() {
        controlsPanel = new JPanel();
        controlsPanel.setOpaque(false);
        controlsPanel.setLayout(new BorderLayout());

        // Title panel
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        titlePanel.setOpaque(false);
        JLabel titleLabel = UIUtility.createPixelatedLabel2("Parental Controls", 22, Color.white);
        titlePanel.add(titleLabel);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));

        // Create navigation panel with buttons
        JPanel navigationPanel = createNavigationPanel();
        
        // Create content panels
        timeRestrictionsPanel = createTimeRestrictionsPanel();
        statisticsPanel = createStatisticsPanel();
        revivePetPanel = createRevivePetPanel();
        
        // Content panel to hold the active panel
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setOpaque(false);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        // Set initial active panel
        activePanel = timeRestrictionsPanel;
        contentPanel.add(activePanel, BorderLayout.CENTER);
        
        // Return to main menu button
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        
        UIUtility.button_fontsize = 16f;
        UIUtility.button_width = 200;
        UIUtility.button_height = 40;
        returnButton = new UIUtility.PixelatedButton("Main Menu");
        returnButton.setToolTipText("Press ESC");
        returnButton.addActionListener(e -> {
            saveCurrentSessionTime();
            returnToMainMenu();
        });
        bottomPanel.add(returnButton);
        
        // Add components to the main panel
        controlsPanel.add(titlePanel, BorderLayout.NORTH);
        controlsPanel.add(navigationPanel, BorderLayout.PAGE_START);
        controlsPanel.add(contentPanel, BorderLayout.CENTER);
        controlsPanel.add(bottomPanel, BorderLayout.SOUTH);
    }
    
    /**
     * Makes a panel for navigating to the Time Restrictions, Statistics, and Revive Pet options.
     */
    private JPanel createNavigationPanel() {
        JPanel navigationPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        navigationPanel.setOpaque(false);
        navigationPanel.setBorder(BorderFactory.createEmptyBorder(170, 0, 10, 0));
        
        UIUtility.button_fontsize = 12f;
        UIUtility.button_width = 280;
        UIUtility.button_height = 35;
        
        // Create navigation buttons
        timeRestrictionsButton = new UIUtility.PixelatedButton("Time Restrictions");
        UIUtility.button_fontsize = 12f;
        UIUtility.button_width = 200;
        UIUtility.button_height = 35;
        statisticsButton = new UIUtility.PixelatedButton("Statistics");
        revivePetButton = new UIUtility.PixelatedButton("Revive Pet");
        
        // Style the active button differently
        timeRestrictionsButton.setBackground(new Color(70, 130, 180)); // Set initial active button color
        
        // Add action listeners for navigation
        timeRestrictionsButton.addActionListener(e -> switchActivePanel(timeRestrictionsPanel, timeRestrictionsButton));
        statisticsButton.addActionListener(e -> switchActivePanel(statisticsPanel, statisticsButton));
        revivePetButton.addActionListener(e -> switchActivePanel(revivePetPanel, revivePetButton));
        
        // Add buttons to the panel
        navigationPanel.add(timeRestrictionsButton);
        navigationPanel.add(statisticsButton);
        navigationPanel.add(revivePetButton);
        
        return navigationPanel;
    }
    
    /**
     * Switches from login panel to controls panel after successful authentication.
     * Updates the content pane and refreshes statistics display.
     */
    private void switchActivePanel(JPanel newPanel, PixelatedButton activeButton) {
        // Remove the old panel
        Container parent = activePanel.getParent();
        parent.remove(activePanel);
        
        // Add the new panel
        activePanel = newPanel;
        parent.add(activePanel, BorderLayout.CENTER);
        
        // Update button styling
        timeRestrictionsButton.setBackground(UIManager.getColor("Button.background"));
        statisticsButton.setBackground(UIManager.getColor("Button.background"));
        revivePetButton.setBackground(UIManager.getColor("Button.background"));
        activeButton.setBackground(new Color(70, 130, 180)); // Highlight active button
        
        // Update statistics if that panel is selected
        if (newPanel == statisticsPanel) {
            updateStatisticsDisplay();
        }
        
        // Update save file list if revive panel is selected
        if (newPanel == revivePetPanel) {
            refreshSaveFileList();
        }
        
        // Refresh the layout
        parent.revalidate();
        parent.repaint();
    }

    /**
     * Creates the time restrictions tab panel containing:
     * - Enable/disable checkbox
     * - Start/end time spinners (24-hour format)
     * - Save button that link to {@link ParentalSettings}
     */
    private JPanel createTimeRestrictionsPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.insets = new Insets(0, 80, 0, 80);
        
        // Panel title
        JLabel statsTitle = UIUtility.createPixelatedLabel2("Time Restrictions", 15f, Color.WHITE);
        panel.add(statsTitle, gbc);

        gbc.insets = new Insets(50, 50, 10, 50);

        // Enable/disable time restrictions
        enableTimeRestrictionCheckbox = new JCheckBox("Enable Time Restrictions");
        enableTimeRestrictionCheckbox.setOpaque(true);
        enableTimeRestrictionCheckbox.setFont(new Font(Font.MONOSPACED, Font.BOLD, 16));
        enableTimeRestrictionCheckbox.setForeground(Color.BLACK);
        panel.add(enableTimeRestrictionCheckbox, gbc);
        
        // Time settings
        JPanel timePanel = new JPanel(new GridLayout(2, 2, 10, 10));
        timePanel.setOpaque(false);
        
        JLabel startTimeLabel = new JLabel("Start Time (HH:MM):");
        startTimeLabel.setFont(new Font(Font.MONOSPACED, Font.BOLD, 16));
        startTimeLabel.setForeground(Color.WHITE);
        
        JLabel endTimeLabel = new JLabel("End Time (HH:MM):");
        endTimeLabel.setFont(new Font(Font.MONOSPACED, Font.BOLD, 16));
        endTimeLabel.setForeground(Color.WHITE);
        
        // Create time model for 24-hour format
        SpinnerDateModel startModel = new SpinnerDateModel();
        startTimeSpinner = new JSpinner(startModel);
        JSpinner.DateEditor startEditor = new JSpinner.DateEditor(startTimeSpinner, "HH:mm");
        startTimeSpinner.setEditor(startEditor);
        
        SpinnerDateModel endModel = new SpinnerDateModel();
        endTimeSpinner = new JSpinner(endModel);
        JSpinner.DateEditor endEditor = new JSpinner.DateEditor(endTimeSpinner, "HH:mm");
        endTimeSpinner.setEditor(endEditor);
        
        timePanel.add(startTimeLabel);
        timePanel.add(startTimeSpinner);
        timePanel.add(endTimeLabel);
        timePanel.add(endTimeSpinner);
        
        gbc.insets = new Insets(10, 50, 20, 50);
        panel.add(timePanel, gbc);
        
        // Description
        JTextArea descriptionArea = new JTextArea(
            "Set the time range when the player is allowed to play the game. " +
            "If enabled, the player will not be able to play the game outside of these hours, " +
            "but they can still access the main menu and other screens."
        );
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setLineWrap(true);
        descriptionArea.setOpaque(true);
        descriptionArea.setEditable(false);
        descriptionArea.setFont(new Font(Font.MONOSPACED, Font.BOLD, 13));
        descriptionArea.setForeground(Color.black);
        
        gbc.insets = new Insets(0, 60, 10, 50);
        panel.add(descriptionArea, gbc);
        
        // Save button
        UIUtility.button_fontsize = 16f;
        UIUtility.button_width = 250;
        UIUtility.button_height = 40;

        saveTimeRestrictionButton = new UIUtility.PixelatedButton("Save Settings");
        saveTimeRestrictionButton.addActionListener(e -> saveTimeRestrictions());
        
        gbc.insets = new Insets(0, 150, 0, 150);
        panel.add(saveTimeRestrictionButton, gbc);
        
        return panel;
    }
    
    /**
     * Creates the statistics tab panel displaying:
     *  - Total play time
     *  - Session count
     *  - Average session duration
     *  - Current session timer
     *  - Reset statistics button
     * 
     */
    private JPanel createStatisticsPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(30, 80, 0, 80);
        
        // Panel title
        JLabel statsTitle = UIUtility.createPixelatedLabel2("Player Statistics", 15f, Color.WHITE);
        panel.add(statsTitle, gbc);
        
        // Total play time
        JPanel totalTimePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        totalTimePanel.setOpaque(false);
        JLabel totalTimeTextLabel = UIUtility.createPixelatedLabel2("Total Play Time: ", 11f, Color.WHITE);
        totalPlayTimeLabel = new JLabel("0h 0m 0s");
        totalPlayTimeLabel.setFont(new Font(Font.MONOSPACED, Font.BOLD, 16));
        totalPlayTimeLabel.setForeground(Color.WHITE);
        totalTimePanel.add(totalTimeTextLabel);
        totalTimePanel.add(totalPlayTimeLabel);
        
        // Session count
        JPanel sessionCountPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        sessionCountPanel.setOpaque(false);
        JLabel sessionCountTextLabel = UIUtility.createPixelatedLabel2("Total Sessions: ", 11f, Color.WHITE);
        sessionCountLabel = new JLabel("0");
        sessionCountLabel.setFont(new Font(Font.MONOSPACED, Font.BOLD, 16));
        sessionCountLabel.setForeground(Color.WHITE);
        sessionCountPanel.add(sessionCountTextLabel);
        sessionCountPanel.add(sessionCountLabel);
        
        // Average session time
        JPanel avgTimePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        avgTimePanel.setOpaque(false);
        JLabel avgTimeTextLabel = UIUtility.createPixelatedLabel2("Average Session Time: ", 11f, Color.WHITE);
        averageSessionTimeLabel = new JLabel("0h 0m 0s");
        averageSessionTimeLabel.setFont(new Font(Font.MONOSPACED, Font.BOLD, 16));
        averageSessionTimeLabel.setForeground(Color.WHITE);
        avgTimePanel.add(avgTimeTextLabel);
        avgTimePanel.add(averageSessionTimeLabel);
        
        // Current session time
        JPanel currentSessionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        currentSessionPanel.setOpaque(false);
        JLabel currentSessionTextLabel = UIUtility.createPixelatedLabel2("Current Session Time: ", 11f, Color.WHITE);
        currentSessionTimeLabel = new JLabel("0h 0m 0s");
        currentSessionTimeLabel.setFont(new Font(Font.MONOSPACED, Font.BOLD, 16));
        currentSessionTimeLabel.setForeground(Color.WHITE);
        currentSessionPanel.add(currentSessionTextLabel);
        currentSessionPanel.add(currentSessionTimeLabel);
        
        panel.add(totalTimePanel, gbc);
        gbc.insets = new Insets(5, 80, 0, 80);
        panel.add(sessionCountPanel, gbc);
        panel.add(avgTimePanel, gbc);
        panel.add(currentSessionPanel, gbc);
        
        // Description
        JTextArea descriptionArea = new JTextArea(
            "These statistics track the player's total time spent playing and their average session length. " +
            "A session is the time between starting and exiting the application. The current session time updates in real-time."
        );
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setLineWrap(true);
        descriptionArea.setOpaque(true);
        descriptionArea.setForeground(Color.black);
        descriptionArea.setEditable(false);
        descriptionArea.setFont(new Font(Font.MONOSPACED, Font.BOLD, 14));
        
        gbc.insets = new Insets(20, 70, 30, 70);
        panel.add(descriptionArea, gbc);

        UIUtility.button_fontsize = 10f;
        UIUtility.button_width = 330;
        UIUtility.button_height = 40;
        
        // Reset button
        resetStatsButton = new UIUtility.PixelatedButton("Reset All Statistics");
        resetStatsButton.addActionListener(e -> resetStatistics());
        
        gbc.insets = new Insets(20, 150, 20, 150);
        panel.add(resetStatsButton, gbc);
        
        return panel;
    }
    
    /**
     * Creates the pet revival tab panel with:
     * - Save file dropdown (populated via {@link GameSaveManager})
     * - Revive button to restore dead pets
     * - Refresh button to reload save files
     * 
     */
    private JPanel createRevivePetPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(20, 55, 10, 55);
        
        // Panel title
        JLabel reviveTitle = UIUtility.createPixelatedLabel2("Revive Pet", 15f, Color.WHITE);
        panel.add(reviveTitle, gbc);
        
        // Description
        JTextArea descriptionArea = new JTextArea(
            "Select a save file from the dropdown menu below to revive a pet that has died or is in poor condition. " +
            "This will restore all of the pet's stats to their maximum values."
        );
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setLineWrap(true);
        descriptionArea.setOpaque(true);
        descriptionArea.setForeground(Color.BLACK);
        descriptionArea.setEditable(false);
        descriptionArea.setFont(new Font(Font.MONOSPACED, Font.BOLD, 14));
        gbc.insets = new Insets(20, 150, 10, 150);
        panel.add(descriptionArea, gbc);
        
        // Save file selection
        JPanel selectionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        selectionPanel.setOpaque(false);
        
        JLabel selectLabel = UIUtility.createPixelatedLabel2("Select Pet: ", 13f, Color.WHITE);
    
        // Create the combo box
        nameDropdown = new JComboBox<>();
        nameDropdown.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
        
        // Add listeners to close dropdown
        nameDropdown.addActionListener(e -> {
            if (e.getSource() == nameDropdown) {
                nameDropdown.setPopupVisible(false);
            }
        });
        
        // Custom renderer to show only the same in the dropdown list
        nameDropdown.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, 
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value != null) {
                    String displayName = value.toString().replace("_save.csv", "").replace(".csv", "").replace("_", " ").trim();
                    setText(displayName.isEmpty() ? value.toString() : displayName);
                }
                return this;
            }
        });
        
        // Add components to selection panel
        selectionPanel.add(selectLabel);
        selectionPanel.add(nameDropdown);
        
        gbc.insets = new Insets(20, 50, 20, 50);
        panel.add(selectionPanel, gbc);
        
        UIUtility.button_fontsize = 16f;
        UIUtility.button_width = 420;
        UIUtility.button_height = 40;
    
        // Revive button
        revivePetActionButton = new UIUtility.PixelatedButton("Revive Selected Pet");
        revivePetActionButton.addActionListener(e -> reviveSelectedPet());
        
        gbc.insets = new Insets(20, 150, 20, 150);
        panel.add(revivePetActionButton, gbc);
        
        // Refresh button
        PixelatedButton refreshButton = new UIUtility.PixelatedButton("Refresh Pet List");
        refreshButton.addActionListener(e -> refreshSaveFileList());
        
        gbc.insets = new Insets(10, 150, 20, 150);
        panel.add(refreshButton, gbc);
        
        // Initialize dropdown
        refreshSaveFileList();
        
        return panel;
    }

    /**
     * Switches from login panel to controls panel after successful authentication.
     * Updates the content pane and refreshes statistics display.
     */
    private void switchToControlsPanel() {
        getContentPane().removeAll();
        getContentPane().add(controlsPanel, BorderLayout.CENTER);
        getContentPane().revalidate();
        getContentPane().repaint();
        
        // Update statistics display
        updateStatisticsDisplay();
        
        // Make sure the first panel is selected and properly highlighted
        switchActivePanel(timeRestrictionsPanel, timeRestrictionsButton);
    }
    

    /**
     * Returns to the main menu screen.
     * Stops session timer, saves current session time, and disposes this window.
     */
    private void returnToMainMenu() {
        // Stop the session timer before returning to main menu
        if (sessionTimer != null && sessionTimer.isRunning()) {
            sessionTimer.stop();
        }
        
        // Save current session time before exiting
        saveCurrentSessionTime();
        
        this.setVisible(false);
        MainMenuScreen mainMenu = new MainMenuScreen();
        mainMenu.setVisible(true);
        this.dispose();
    }
    

    /**
     * Saves the current session duration and updates total play time statistics.
     * Called when returning to main menu or closing the application.
     */
    private void saveCurrentSessionTime() {
        try {
            if (currentSettings != null) {
                // End the current session
                currentSettings.endSession();
                
                // Save updated settings
                GameSaveManager.saveParentalSettings(currentSettings);
                
                // Reset current session counter
                currentSessionSeconds = 0;
                updateCurrentSessionDisplay();
            }
        } catch (IOException e) {
            System.err.println("Error saving session time: " + e.getMessage());
        }
    }
    
    /**
     * Saves time restriction settings from UI to ParentalSettings.
     * Validates time format and preserves existing statistics.
     * Shows error messages for invalid inputs or save failures.
     */
    private void saveTimeRestrictions() {
        try {
            // Load existing settings to preserve statistics
            ParentalSettings settings = GameSaveManager.loadParentalSettings();
            if (settings == null) {
                settings = new ParentalSettings();
            }
            
            // Get time values from spinners
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
            String startTimeText = ((JSpinner.DateEditor) startTimeSpinner.getEditor()).getTextField().getText();
            String endTimeText = ((JSpinner.DateEditor) endTimeSpinner.getEditor()).getTextField().getText();
            
            LocalTime startTime = LocalTime.parse(startTimeText, formatter);
            LocalTime endTime = LocalTime.parse(endTimeText, formatter);
            
            // Update time restriction settings
            settings.setTimeRestrictionEnabled(enableTimeRestrictionCheckbox.isSelected());
            settings.setAllowedStartTime(startTime);
            settings.setAllowedEndTime(endTime);
            
            // Save to file
            GameSaveManager.saveParentalSettings(settings);
            
            // Update our current settings reference
            currentSettings = settings;
            
            JOptionPane.showMessageDialog(this, 
                "Time restrictions saved successfully.", 
                "Settings Saved", 
                JOptionPane.INFORMATION_MESSAGE);
                
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, 
                "Invalid time format. Please use HH:MM format.", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, 
                "Error saving settings: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Loads parental settings from file and updates UI components.
     * Sets default times if no settings exist (current time to +2 hours).
     * Handles file loading errors by creating default settings.
     */
    private void loadParentalSettings() {
        try {
            currentSettings = GameSaveManager.loadParentalSettings();
            if (currentSettings != null) {
                // Apply loaded settings to UI
                enableTimeRestrictionCheckbox.setSelected(currentSettings.isTimeRestrictionEnabled());
                
                // Set time spinners
                if (currentSettings.getAllowedStartTime() != null) {
                    startTimeSpinner.setValue(java.sql.Time.valueOf(currentSettings.getAllowedStartTime()));
                } else {
                    // Default to current time if not set
                    startTimeSpinner.setValue(java.sql.Time.valueOf(LocalTime.now()));
                }
                
                if (currentSettings.getAllowedEndTime() != null) {
                    endTimeSpinner.setValue(java.sql.Time.valueOf(currentSettings.getAllowedEndTime()));
                } else {
                    // Default to current time + 2 hours if not set
                    LocalTime endTime = LocalTime.now().plusHours(2);
                    if (endTime.isBefore(LocalTime.now())) {
                        // If we wrap around to the next day, just use 11:59 PM
                        endTime = LocalTime.of(23, 59);
                    }
                    endTimeSpinner.setValue(java.sql.Time.valueOf(endTime));
                }
                
                updateStatisticsDisplay();
            } else {
                // Create default settings with current time
                currentSettings = new ParentalSettings();
                
                // Set default times in UI
                LocalTime now = LocalTime.now();
                startTimeSpinner.setValue(java.sql.Time.valueOf(now));
                
                LocalTime endTime = now.plusHours(2);
                if (endTime.isBefore(now)) {
                    // If we wrap around to the next day, just use 11:59 PM
                    endTime = LocalTime.of(23, 59);
                }
                endTimeSpinner.setValue(java.sql.Time.valueOf(endTime));
            }
        } catch (IOException e) {
            // Just use default values if loading fails
            System.err.println("Error loading parental settings: " + e.getMessage());
            
            // Create default settings
            currentSettings = new ParentalSettings();
            
            // Set default times in UI
            LocalTime now = LocalTime.now();
            startTimeSpinner.setValue(java.sql.Time.valueOf(now));
            
            LocalTime endTime = now.plusHours(2);
            if (endTime.isBefore(now)) {
                // If we wrap around to the next day, just use 11:59 PM
                endTime = LocalTime.of(23, 59);
            }
            endTimeSpinner.setValue(java.sql.Time.valueOf(endTime));
        }
    }

    /**
     * Updates all statistics display labels with current values.
     * Formats durations for display and handles null cases.
     */
    private void updateStatisticsDisplay() {
        try {
            if (currentSettings != null) {
                Duration totalPlayTime = currentSettings.getTotalPlayTime();
                Duration avgSessionTime = currentSettings.getAverageSessionTime();
                int sessionCount = currentSettings.getSessionCount();
                Duration currentSessionDuration = currentSettings.getCurrentSessionDuration();
                
                // Format durations as readable strings
                String totalTimeStr = formatDuration(totalPlayTime);
                String avgTimeStr = formatDuration(avgSessionTime);
                String currentSessionStr = formatDuration(currentSessionDuration);
                
                // Update labels
                totalPlayTimeLabel.setText(totalTimeStr);
                sessionCountLabel.setText(String.valueOf(sessionCount));
                averageSessionTimeLabel.setText(avgTimeStr);
                currentSessionTimeLabel.setText(currentSessionStr);
            }
        } catch (Exception e) {
            System.err.println("Error updating statistics display: " + e.getMessage());
        }
    }

    /**
     * Formats a Duration object into "Xh Ym Zs" string.
     * @param duration The duration to format (can be null)
     * @return Formatted time string or "0h 0m 0s" for null
     */
    private String formatDuration(Duration duration) {
        if (duration == null) {
            return "0h 0m 0s";
        }
        
        long hours = duration.toHours();
        long minutes = duration.toMinutesPart();
        long seconds = duration.toSecondsPart();
        
        return String.format("%dh %dm %ds", hours, minutes, seconds);
    }
    
    private void resetStatistics() {
        int response = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to reset all player statistics?",
            "Confirm Reset",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
            
        if (response == JOptionPane.YES_OPTION) {
            try {
                // Preserve current time restriction settings
                boolean timeRestrictionEnabled = false;
                LocalTime allowedStartTime = LocalTime.now();
                LocalTime allowedEndTime = LocalTime.now().plusHours(2);
                
                if (currentSettings != null) {
                    timeRestrictionEnabled = currentSettings.isTimeRestrictionEnabled();
                    allowedStartTime = currentSettings.getAllowedStartTime();
                    allowedEndTime = currentSettings.getAllowedEndTime();
                    
                    // Reset statistics in the current settings
                    currentSettings.resetPlayTimeStatistics();
                    
                    // Make sure time restrictions are preserved
                    currentSettings.setTimeRestrictionEnabled(timeRestrictionEnabled);
                    currentSettings.setAllowedStartTime(allowedStartTime);
                    currentSettings.setAllowedEndTime(allowedEndTime);
                    
                    // Save the updated settings
                    GameSaveManager.saveParentalSettings(currentSettings);
                } else {
                    // Create a new settings object if none exists
                    currentSettings = new ParentalSettings();
                    currentSettings.setTimeRestrictionEnabled(timeRestrictionEnabled);
                    currentSettings.setAllowedStartTime(allowedStartTime);
                    currentSettings.setAllowedEndTime(allowedEndTime);
                    
                    GameSaveManager.saveParentalSettings(currentSettings);
                }
                
                // Reset current session timer but start a new session
                currentSessionSeconds = 0;
                currentSettings.startSession();
                
                // Update display
                updateStatisticsDisplay();
                updateCurrentSessionDisplay();
                
                JOptionPane.showMessageDialog(this,
                    "All statistics have been reset successfully.",
                    "Reset Complete",
                    JOptionPane.INFORMATION_MESSAGE);
                    
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this,
                    "Error resetting statistics: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * Refreshes the save file dropdown list from GameSaveManager.
     * Disables revive button if no save files found.
     */
     private void refreshSaveFileList() {
         nameDropdown.removeAllItems();
         List<String> saveFiles = GameSaveManager.listSaveFiles();
         
         if (saveFiles.isEmpty()) {
             nameDropdown.addItem("No pets found");
             revivePetActionButton.setEnabled(false);
         } else {
             // Sort files alphabetically by pet name
             saveFiles.sort((f1, f2) -> {
                 String name1 = f1.replace("_save.csv", "").replace(".csv", "");
                 String name2 = f2.replace("_save.csv", "").replace(".csv", "");
                 return name1.compareToIgnoreCase(name2);
             });
             
             saveFiles.forEach(nameDropdown::addItem);
             revivePetActionButton.setEnabled(true);
         }
     }
    
    /**
     * Attempts to revive pet in selected save file.
     * Shows success/error messages and refreshes the file list.
     */
    private void reviveSelectedPet() {
        String selectedFile = (String) nameDropdown.getSelectedItem();
        
        if (selectedFile == null || selectedFile.equals("No save files found")) {
            JOptionPane.showMessageDialog(this,
                "Please select a valid save file.",
                "No File Selected",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            boolean success = GameSaveManager.revivePet(selectedFile);
            // Convert file name to string to split it.
            String displayName = selectedFile.toString().replace("_save.csv", "").replace(".csv", "").replace("_", " ").trim();
            setTitle(displayName.isEmpty() ? selectedFile.toString() : displayName);

            if (success) {
                JOptionPane.showMessageDialog(this,
                    displayName + " has successfully been revived!",
                    "Pet Revived",
                    JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                    "Could not find a pet to revive in the selected save file.",
                    "Revival Failed",
                    JOptionPane.ERROR_MESSAGE);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                "Error reviving pet: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    

    
        /**
     * Sets up keyboard shortcuts for all action buttons.
     * Maps keys ENTER for login, ESC for exit
     */
    private void setupKeyboardShortcuts() {
        JPanel contentPane = (JPanel) getContentPane();
        InputMap inputMap = contentPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = contentPane.getActionMap();
        // Using lambda expressions
        addKeyBinding(inputMap, actionMap, KeyEvent.VK_ENTER, "loginAction", loginButton);
        addKeyBinding(inputMap, actionMap, KeyEvent.VK_ESCAPE, "exitAction", returnButton);
    }
    
    /**
     * Binds a keyboard key to trigger a button click action when pressed.
     * This creates a direct mapping between a physical key press and a button's action.
     * 
     * @param inputMap The InputMap that stores the key-to-action mapping, typically from a JComponent
     * @param actionMap The ActionMap that stores the action implementations, typically from a JComponent
     * @param keyCode The virtual key code (from KeyEvent) to bind (e.g., KeyEvent.VK_ENTER)
     * @param actionName The unique identifier string for this action binding
     * @param button The JButton that will be programmatically clicked when the key is pressed
     */
    private void addKeyBinding(InputMap inputMap, ActionMap actionMap, 
                             int keyCode, String actionName, JButton button) {
        inputMap.put(KeyStroke.getKeyStroke(keyCode, 0), actionName);
        actionMap.put(actionName, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (button.isEnabled()) {
                    button.doClick();
                }
            }
        });
    }
    
    /**
     * Main method for testing the parental controls screen.
     * Launches the UI in the Swing event dispatch thread.
     * @param args Command line arguments (unused)
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ParentalControlsScreen parentalControls = new ParentalControlsScreen();
            parentalControls.setVisible(true);
        });
    }
}