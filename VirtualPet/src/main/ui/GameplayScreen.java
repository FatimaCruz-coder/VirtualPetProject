package group33.VirtualPet.src.main.ui;

import group33.VirtualPet.src.main.game.TimeRestrictionManager;
import group33.VirtualPet.src.main.game.TimeRestrictionManager.ScreenType;
import group33.VirtualPet.src.main.game.GameSaveManager;
import group33.VirtualPet.src.main.model.Inventory;
import group33.VirtualPet.src.main.model.ParentalSettings;
import group33.VirtualPet.src.main.model.Pet;
import group33.VirtualPet.src.main.model.Player;
import group33.VirtualPet.src.main.ui.UIUtility.BackgroundPanel;
import group33.VirtualPet.src.main.ui.UIUtility.PixelatedButton;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalTime;
import java.time.Duration;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

/**
 * The GameplayScreen class represents the main game interface where players interact with their virtual pet.
 * It displays the pet's status, provides action buttons, and manages game state including:
 * - Pet statistics (health, sleep, fullness, happiness)
 * - Parental control time restrictions
 * - Inventory management
 * - Game saving/loading
 * - Pet interactions (feeding, playing, taking to vet, and going to sleep)
 * 
 * Key Features:
 * - Visual representation of pet with state-based sprites
 * - Interactive buttons with keyboard shortcuts
 * - Automatic sleep recovery system
 * - Parental control timer display
 * - Inventory viewing system
 * 
 * Sprites are used from https://www.spriters-resource.com
 * 
 * @author Team 33 (Dhir, Kostya, Fatima, Anna)
 * @since Winter 2025
 */

public class GameplayScreen extends JFrame {
    private Player player;
    private Pet currentPet;
    private Inventory inventory;
    // New field to track current save filename
    private String currentSaveFilename; 
    private int cool = 300000;
    private JLabel vetCooldownLabel;
    private JLabel playCooldownLabel;
    private Timer cooldownTimer;

    // Parental Controls Components
    private JLabel parentalTimerLabel;
    private Timer parentalControlTimer;
    private ParentalSettings parentalSettings;

    // UI Components
    private JLabel petNameLabel;
    private JLabel scoreLabel;
    private JLabel stateLabel;
    private JLabel petImageLabel;

    // Stat Labels with Progress Bars
    public static JLabel healthStatLabel;
    public static JProgressBar healthProgressBar;
    public static JLabel sleepStatLabel;
    public static JProgressBar sleepProgressBar;
    public static JLabel fullnessStatLabel;
    public static JProgressBar fullnessProgressBar;
    public static JLabel happinessStatLabel;
    public static JProgressBar happinessProgressBar;

    // Command Buttons
    private PixelatedButton feedButton;
    private PixelatedButton giftButton;
    private PixelatedButton vetButton;
    private PixelatedButton battleButton;
    private PixelatedButton exerciseButton;
    private PixelatedButton bedButton;
    private PixelatedButton playButton;
    private PixelatedButton saveButton;
    private PixelatedButton mainMenuButton;
    private PixelatedButton viewInventoryButton;
    // Initialize variables for flipping pet function.
    private Timer flipTimer;
    private boolean isFlipped = false;
    private Timer gameTickTimer;
    private boolean alertShown = false; // Prevent repeated alerts

    /**
     * Constructs a GameplayScreen with the specified player and save file.
     * Initializes all UI components, sets up timers, and loads game state.
     * 
     * @param player The Player object containing game state
     * @param saveFilename The filename of the current save (empty string for new games)
     */
    public GameplayScreen(Player player, String saveFilename) {
        this.player = player;
        this.currentPet = player.getCurrentPet();
        this.inventory = player.getInventory();
        this.currentSaveFilename = saveFilename; // Store the save filename
        
        // Load parental settings
        loadParentalSettings();
        
        setTitle("Pixel Pals - Gameplay");
        setSize(1031, 849);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Use custom background panel
        BackgroundPanel backgroundPanel = new UIUtility.BackgroundPanel("group33/VirtualPet/assets/images/gameplay_house_v2.png");
        setContentPane(backgroundPanel);
        backgroundPanel.setLayout(new BorderLayout());
        
        // Calling the methods
        initializeComponents();
        setupLayout();
        setupKeyboardShortcuts();
        updatePetStatus();
        updateCooldowns();
        updateCommandAvailability();
        setupGameTickTimer();
        setupCooldownTimer();
        
        // Flip image every 2 seconds
        flipTimer = new Timer(2000, e -> { 
            isFlipped = !isFlipped;
            updatePetImage();
        });
        flipTimer.start();
        
        // Add window listener to clean up timer
        addWindowListener((WindowListener) new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                flipTimer.stop();
            }
        });
        
        // Start sleep timer and parental controls timer
        sleep();
        if (parentalSettings != null && parentalSettings.isTimeRestrictionEnabled()) {
            startParentalControlTimer();
        }
    }

    /**
     * Sets up keyboard shortcuts for all action buttons.
     * Maps keys F,G,B,P,E,V,S,ESC,Q,X to their respective actions.
     */
    private void setupKeyboardShortcuts() {
        JPanel contentPane = (JPanel) getContentPane();
        InputMap inputMap = contentPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = contentPane.getActionMap();
        
        // Using lambda expressions
        addKeyBinding(inputMap, actionMap, KeyEvent.VK_F, "feedAction", feedButton);
        addKeyBinding(inputMap, actionMap, KeyEvent.VK_G, "giftAction", giftButton);
        addKeyBinding(inputMap, actionMap, KeyEvent.VK_B, "bedAction", bedButton);
        addKeyBinding(inputMap, actionMap, KeyEvent.VK_P, "playAction", playButton);
        addKeyBinding(inputMap, actionMap, KeyEvent.VK_E, "exerciseAction", exerciseButton);
        addKeyBinding(inputMap, actionMap, KeyEvent.VK_V, "vetAction", vetButton);
        addKeyBinding(inputMap, actionMap, KeyEvent.VK_S, "saveAction", saveButton);
        addKeyBinding(inputMap, actionMap, KeyEvent.VK_ESCAPE, "mainMenuAction", mainMenuButton);
        addKeyBinding(inputMap, actionMap, KeyEvent.VK_Q, "viewInventoryAction", viewInventoryButton);
        addKeyBinding(inputMap, actionMap, KeyEvent.VK_X, "battleAction", battleButton);
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

    private void setupGameTickTimer() {
        // Create a timer that ticks every 5 seconds (5000ms)
        gameTickTimer = new Timer(10000, e -> {
            // Update pet statistics
            currentPet.updateStatistics();
            
            // Update UI to reflect new values
            updatePetStatus();
            updateCommandAvailability();
            
            // Check if the pet died during this update
            if (currentPet.isDead()) {
                gameTickTimer.stop();
                JOptionPane.showMessageDialog(this, 
                    currentPet.getName() + " has died! Game over.", 
                    "Game Over", 
                    JOptionPane.WARNING_MESSAGE);
            }
        });
        gameTickTimer.start();
    }
    // Also make sure to stop the timer when the screen is closed
    @Override
    public void dispose() {
        if (gameTickTimer != null) {
            gameTickTimer.stop();
        }
        if (cooldownTimer != null) {
            cooldownTimer.stop();
        }
        super.dispose();
    }

    private void updateCooldowns() {
        long currentTime = System.currentTimeMillis();
        
        // Calculate remaining cooldown for vet
        long vetTimeRemaining = Math.max(0, (player.getLastV() + cool) - currentTime);
        if (vetTimeRemaining > 0) {
            int vetSecondsRemaining = (int)(vetTimeRemaining / 1000);
            vetCooldownLabel.setText("Vet: " + vetSecondsRemaining + "s");
            vetCooldownLabel.setForeground(Color.RED);
        } else {
            vetCooldownLabel.setText("Vet: Ready");
            vetCooldownLabel.setForeground(Color.GREEN);
        }
        
        // Calculate remaining cooldown for play
        long playTimeRemaining = Math.max(0, (player.getLastP() + cool) - currentTime);
        if (playTimeRemaining > 0) {
            int playSecondsRemaining = (int)(playTimeRemaining / 1000);
            playCooldownLabel.setText("Play: " + playSecondsRemaining + "s");
            playCooldownLabel.setForeground(Color.RED);
        } else {
            playCooldownLabel.setText("Play: Ready");
            playCooldownLabel.setForeground(Color.GREEN);
        }
    }

    private void setupCooldownTimer() {
        // Update cooldowns every 1 second (1000ms)
        cooldownTimer = new Timer(1000, e -> {
            updateCooldowns();
        });
        cooldownTimer.start();
    }

    /**
     * Loads parental control settings from persistent storage.
     * Called during screen initialization.
     */
    private void loadParentalSettings() {
        // Check time restrictions before initializing the screen
        // If restrictions fail, this will redirect to main menu
        if (!TimeRestrictionManager.checkAndRedirect(ScreenType.GAMEPLAY_SCREEN, this)) {
            // If we reach here, we're being redirected to main menu
            return;
        }

        try {
            parentalSettings = GameSaveManager.loadParentalSettings();
        } catch (IOException e) {
            System.err.println("Error loading parental settings: " + e.getMessage());
            parentalSettings = null;
        }
    }

    /**
     * Starts the parental control timer that enforces play time restrictions.
     * Updates display every second and checks time limits.
     */
    private void startParentalControlTimer() {
        if (parentalSettings == null || !parentalSettings.isTimeRestrictionEnabled()) {
            //updateParentalTimerDisplay();
            return;
        }

        parentalControlTimer = new Timer(1000, e -> {
            updateParentalTimerDisplay();
            // redirect to main menu
        });
        parentalControlTimer.start();
    }

    /**
     * Updates the parental control timer display with remaining time.
     * Formats time as HH:MM:SS or shows "Play time ended!" when expired.
     */
    private void updateParentalTimerDisplay() {
        if (parentalSettings == null || !parentalSettings.isTimeRestrictionEnabled() || parentalTimerLabel == null) {
            return;
        }
    
        LocalTime now = LocalTime.now();
        LocalTime endTime = parentalSettings.getAllowedEndTime();
    
        if (endTime != null && now.isBefore(endTime)) {
            Duration timeLeft = Duration.between(now, endTime);
            long hours = timeLeft.toHours();
            long minutes = timeLeft.toMinutesPart();
            long seconds = timeLeft.toSecondsPart();
    
            parentalTimerLabel.setText(String.format("Time left: %02d:%02d:%02d", hours, minutes, seconds));
        } else {
            parentalTimerLabel.setText("Play time ended!");
    
            // Ensure the alert is shown only once
            if (!alertShown) {
                alertShown = true; // Prevent multiple alerts
    
                int option = JOptionPane.showOptionDialog(this,
                    "You are not allowed to play at this time.\n" +
                    "Please try again during your allowed play hours.",
                    "Parental Controls",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.WARNING_MESSAGE,
                    null, new Object[]{"OK"}, "OK");
    
                if (option == 0) {
                    // Stop the parental control timer before switching screens
                    if (parentalControlTimer != null) {
                        parentalControlTimer.stop();
                    }
    
                    this.setVisible(false); // Hide the current screen
                    MainMenuScreen mainMenu = new MainMenuScreen();
                    mainMenu.setVisible(true);
                    this.dispose(); // Close the current screen
                }
            }
        }
    }
    
    /**
     * Starts the sleep timer that gradually recovers pet's sleep stat.
     * Increases sleep by 1% per second until fully rested (100%).
     * Wakes pet automatically when fully rested.
     */
    private void sleep(){
        Timer sleepTimer = new Timer(1000, e -> {
            if (currentPet != null && currentPet.isSleeping()) {
                int sleep = currentPet.getSleep();
                if (sleep < 100) {
                    updatePetImage();
                    currentPet.setSleep(sleep + 1);
                    sleepProgressBar.setValue(currentPet.getSleep());
                    sleepProgressBar.setString(currentPet.getSleep() + "");
                    sleepProgressBar.setForeground(UIUtility.getColorForProgressBar(currentPet.getSleep()));
                } else {
                    // Pet is fully rested – wake up and restore controls
                    currentPet.wakeUp();
                    updatePetImage();
                    // Refresh UI
                    updatePetStatus();
                    updateCommandAvailability();

                    // Optionally set score to 100
                    player.setScore(100);
                    scoreLabel.setText("Score: 100");
                }
            }
        });
        sleepTimer.start();
    }
    /**
     * Initializes all UI components including:
     * - Status labels and progress bars
     * - Action buttons
     * - Pet image display
     * - Parental control display (if enabled)
     */
    private void initializeComponents() {
        // Status Labels with pixelated font and smaller size for top labels

        petNameLabel = UIUtility.createPixelatedLabel(currentPet.getName().toUpperCase() + " (" + currentPet.getType() + ")", 16);
        petNameLabel.setForeground(Color.DARK_GRAY);
        scoreLabel = UIUtility.createPixelatedLabel("Score: " + player.getScore(), 14);
        scoreLabel.setForeground(Color.DARK_GRAY);
        stateLabel = UIUtility.createPixelatedLabel("Status: ", 14);
        stateLabel.setForeground(Color.DARK_GRAY);
        vetCooldownLabel = UIUtility.createPixelatedLabel2("Vet: Ready", 11f, Color.green);
        playCooldownLabel = UIUtility.createPixelatedLabel2("Play: Ready", 11f, Color.green);

        // Initialize parental timer label if enabled
        if (parentalSettings != null && parentalSettings.isTimeRestrictionEnabled()) {
            parentalTimerLabel = UIUtility.createPixelatedLabel("Time left: --:--:--", 14);
            parentalTimerLabel.setForeground(new Color(128, 0, 0)); // Dark red color for emphasis
        }

        // Create stat labels and progress bars
        healthStatLabel = UIUtility.createPixelatedLabel("Health ", 11f);
        healthStatLabel.setForeground(Color.WHITE);
        healthProgressBar = UIUtility.createStatProgressBar(currentPet.getHealth());
        
        sleepStatLabel = UIUtility.createPixelatedLabel("Sleep ", 11f);
        sleepStatLabel.setForeground(Color.WHITE);
        sleepProgressBar = UIUtility.createStatProgressBar(currentPet.getSleep());
        
        fullnessStatLabel = UIUtility.createPixelatedLabel("Fullness ", 11f);
        fullnessStatLabel.setForeground(Color.WHITE);
        fullnessProgressBar = UIUtility.createStatProgressBar(currentPet.getFullness());
        
        happinessStatLabel = UIUtility.createPixelatedLabel("Happiness ", 11f);
        happinessStatLabel.setForeground(Color.WHITE);
        happinessProgressBar = UIUtility.createStatProgressBar(currentPet.getHappiness());

        // Pet image setup
        petImageLabel = new JLabel();
        updatePetImage(); // New method to set the appropriate pet image based on state

        // Initialize all buttons
        UIUtility.button_fontsize = 10f;
        UIUtility.button_width = 30;
        UIUtility.button_height = 25;

        feedButton = new UIUtility.PixelatedButton("Feed (F)");
        giftButton = new UIUtility.PixelatedButton("Give Gift (G)");
        vetButton = new UIUtility.PixelatedButton("Take to Vet (V)");
        exerciseButton = new UIUtility.PixelatedButton("Exercise (E)");
        battleButton = new UIUtility.PixelatedButton("Battle Game (X)");
        bedButton = new UIUtility.PixelatedButton("Go to Bed (B)");
        playButton = new UIUtility.PixelatedButton("Play (P)");
        saveButton = new UIUtility.PixelatedButton("Save Game (S)");
        mainMenuButton = new UIUtility.PixelatedButton("Main Menu (ESC)");
        viewInventoryButton = new UIUtility.PixelatedButton("View Inventory (Q)");

        // Add action listeners
        setupButtonListeners();
    }
    
/**
 * Updates the pet's image based on current state.
 * Loads appropriate sprite for: normal, dead, sleeping, hungry, or angry states.
 * Handles horizontal flipping animation when enabled.
 */
private void updatePetImage() {
    String basePath = "group33/VirtualPet/assets/images/";
    String stateFolder = currentPet.getType().toString().toLowerCase() + "_pet_states/";
    String imageFileName;
    
    // Determine image file name based on pet state
    if (currentPet.isDead()) {
        imageFileName = currentPet.getType().toString().toLowerCase() + "_dead.png";
    } else if (currentPet.isSleeping()) {
        imageFileName = currentPet.getType().toString().toLowerCase() + "_sleep.png";
    } else if (currentPet.isHungry()) {
        imageFileName = currentPet.getType().toString().toLowerCase() + "_hungry.png";
    } else if (currentPet.isAngry()) {
        imageFileName = currentPet.getType().toString().toLowerCase() + "_angry.png";
    } else {
        imageFileName = currentPet.getType().toString().toLowerCase() + "_normal.png";
    }
    
    String fullImagePath = basePath + stateFolder + imageFileName;
    
    try {
        // Load image using ImageIO which is more reliable than Toolkit
        BufferedImage originalImage;
        try (InputStream in = getClass().getClassLoader().getResourceAsStream(fullImagePath)) {
            if (in == null) {
                throw new FileNotFoundException("Image not found: " + fullImagePath);
            }
            originalImage = ImageIO.read(in);
        }
        
        if (originalImage == null) {
            throw new IOException("ImageIO.read returned null");
        }
        
        // Scale the image
        Image scaledImage = originalImage.getScaledInstance(130, 130, Image.SCALE_SMOOTH);
        
        // Create a new BufferedImage to hold the scaled version
        BufferedImage bufferedScaled = new BufferedImage(130, 130, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = bufferedScaled.createGraphics();
        g2d.drawImage(scaledImage, 0, 0, null);
        g2d.dispose();
        
        // Create flipped version if needed
        Image displayImage = isFlipped ? createFlippedImage(bufferedScaled) : bufferedScaled;
        
        // Create new ImageIcon and set it
        ImageIcon icon = new ImageIcon(displayImage);
        petImageLabel.setIcon(icon);
        
        
    } catch (Exception e) {
        System.err.println("Failed to load state image: " + fullImagePath);
        e.printStackTrace();
        loadFallbackImage();
    }
}


/**
 * Loads a fallback pet image when the state-specific image cannot be loaded.
 * This method attempts to load a generic image for the current pet type when the
 * state image (hungry, sleeping, angry) could not to load. 
 * If the fallback image also fails to load, displays an "Image Error" text message instead.
 * 
 */
private void loadFallbackImage() {
    String fallbackPath = "group33/VirtualPet/assets/images/" + 
                        currentPet.getType().toString().toLowerCase() + ".png";
    
    try {
        // Load fallback image using ImageIO
        BufferedImage originalImage;
        try (InputStream in = getClass().getClassLoader().getResourceAsStream(fallbackPath)) {
            if (in == null) {
                throw new FileNotFoundException("Fallback image not found: " + fallbackPath);
            }
            originalImage = ImageIO.read(in);
        }
        
        if (originalImage == null) {
            throw new IOException("ImageIO.read returned null for fallback");
        }
        
        Image scaledImage = originalImage.getScaledInstance(130, 130, Image.SCALE_SMOOTH);
        BufferedImage bufferedScaled = new BufferedImage(130, 130, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = bufferedScaled.createGraphics();
        g2d.drawImage(scaledImage, 0, 0, null);
        g2d.dispose();
        
        Image displayImage = isFlipped ? createFlippedImage(bufferedScaled) : bufferedScaled;
        petImageLabel.setIcon(new ImageIcon(displayImage));
    } catch (Exception ex) {
        petImageLabel.setText("Image Error");
        System.err.println("Failed to load fallback image: " + fallbackPath);
        ex.printStackTrace();
    }
}
 
    /**
     * Creates a horizontally flipped version of an image.
     * Used for the pet sprite flipping animation.
     * 
     * @param original The source image to flip
     * @return A new BufferedImage that is horizontally mirrored
     */
    private Image createFlippedImage(Image original) {
        BufferedImage flipped = new BufferedImage(
            original.getWidth(null), 
            original.getHeight(null), 
            BufferedImage.TYPE_INT_ARGB
        );
        
        Graphics2D g = flipped.createGraphics();
        g.drawImage(original, original.getWidth(null), 0, -original.getWidth(null), original.getHeight(null), null);
        g.dispose();
        
        return flipped;
    }
    
    /**
     * Constructs a GameplayScreen for a new game without a save file.
     * 
     * @param player The Player object containing game state
     */
    public GameplayScreen(Player player) {
        // Call the other constructor with an empty filename
        this(player, ""); 
    }

    /**
     * Handles pet interactions, screen transitions, and game management.
     * Sets up action listeners for all interactive buttons:
     * - feedButton
     * - giftButton
     * - vetButton
     * - battleButton
     * - bedButton
     * - saveButton
     * - playButton
     * - exerciseButton
     * - mainMenuButton
     * - viewInventoryButton
     * Whenever the user clicks on the button the respective action will exceute.
     */
    private void setupButtonListeners() {
        // Check restrictions before transitioning to a gameplay screen
        if (!TimeRestrictionManager.enforceTimeRestrictions(ScreenType.GAMEPLAY_SCREEN, this)) {return;}
        feedButton.addActionListener(e -> {
            InventoryDialog foodDialog = new InventoryDialog(
                this, 
                currentPet, 
                inventory, 
                // Food inventory
                true,
                player
            );
            foodDialog.setVisible(true);
            updatePetStatus();
            updateCommandAvailability();
        });
        giftButton.addActionListener(e -> {
            InventoryDialog giftDialog = new InventoryDialog(
                this, 
                currentPet, 
                inventory, 
                // Gift inventory
                false,  
                player
            );
            giftDialog.setVisible(true);
            updatePetStatus();
            updateCommandAvailability();
        });

        vetButton.addActionListener(e -> {
            // Increase health
            updateCommandAvailability();
            // Apply cooldown after use
            long curr = System.currentTimeMillis();
            if(curr-player.getLastV() <cool)
            {
                JOptionPane.showMessageDialog(this, "Wait for Cooldown to expire");
            }
            else{
                updatePetStatus();
                new VetScreen(player,this.currentSaveFilename).setVisible(true);
                dispose();
                player.setLastV(curr); 
            }
        });

        battleButton.addActionListener(e -> {
            // Implements battle logic
            // Increase happiness, apply cooldown
            new PetSelectionBattleScreen(this.player,this.currentSaveFilename);
            dispose();
        });

        exerciseButton.addActionListener(e -> {
            // Implements exercise logic
            // Decrease sleepiness, hunger, increase health
            updateCommandAvailability();
            updatePetStatus();
            new ExerciseScreen(player,this.currentSaveFilename).setVisible(true);
            dispose();
            });

        bedButton.addActionListener(e -> {
            // Implements sleep logic
            // Force pet to sleep, update sleep status
            currentPet.sleep();
            sleep();
        });

        playButton.addActionListener(e -> {
            // Increase happiness
            updateCommandAvailability();
            // Apply cooldown after use
            long curr = System.currentTimeMillis();
            if(curr-player.getLastP() < cool) // Change from player.getLastV()
            {
                JOptionPane.showMessageDialog(this, "Wait for Cooldown to expire");
            }
            else{
                updatePetStatus();
                new PlayScreen(player,this.currentSaveFilename).setVisible(true);
                dispose();
                player.setLastP(curr); // Change from player.setLastV()
            }
        });

        saveButton.addActionListener(e -> {
            // If no save filename exists, prompt user to create one
            if (currentSaveFilename.isEmpty()) {
                currentSaveFilename = JOptionPane.showInputDialog(
                    this, 
                    "Enter a name for your save file:", 
                    "Save Game", 
                    JOptionPane.PLAIN_MESSAGE
                );
                
                // Validates the filename
                if (currentSaveFilename == null || currentSaveFilename.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(
                        this, 
                        "Save cancelled or invalid filename.", 
                        "Save Game", 
                        JOptionPane.INFORMATION_MESSAGE
                    );
                    return;
                }
            }
            
            try {
                GameSaveManager.saveGame(player, currentSaveFilename);
                JOptionPane.showMessageDialog(this, "Game saved successfully to " + currentSaveFilename);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error saving game: " + ex.getMessage());
            }
        });

        mainMenuButton.addActionListener(e -> {
            new MainMenuScreen().setVisible(true);
            dispose();
        });

        viewInventoryButton.addActionListener(e -> {
            // Create a custom multi-inventory dialog with shopping functionality
            JDialog multiInventoryDialog = new JDialog(this, "Complete Inventory & Shop", true);
            multiInventoryDialog.setLayout(new BorderLayout());
            multiInventoryDialog.setSize(700, 400);
            multiInventoryDialog.setLocationRelativeTo(this);
            
            // Current score display
            JLabel scoreDisplay = UIUtility.createPixelatedLabel("Current Score: " + player.getScore(), 14);
            scoreDisplay.setForeground(Color.DARK_GRAY);
            JPanel scorePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            scorePanel.add(scoreDisplay);
            
            // Create tabbed pane for inventory and shop
            JTabbedPane tabbedPane = new JTabbedPane();
            
            // First tab: Current inventory
            JPanel inventoryPanel = new JPanel(new GridLayout(1, 2));
            
            JPanel foodPanel = new JPanel(new BorderLayout());
            foodPanel.setBorder(BorderFactory.createTitledBorder("Food Inventory"));
            
            JPanel giftPanel = new JPanel(new BorderLayout());
            giftPanel.setBorder(BorderFactory.createTitledBorder("Gift Inventory"));
            
            // Add tables to panels
            DefaultTableModel foodModel = createInventoryTableModel(true);
            JTable foodTable = new JTable(foodModel);
            foodPanel.add(new JScrollPane(foodTable), BorderLayout.CENTER);
            
            DefaultTableModel giftModel = createInventoryTableModel(false);
            JTable giftTable = new JTable(giftModel);
            giftPanel.add(new JScrollPane(giftTable), BorderLayout.CENTER);
            
            inventoryPanel.add(foodPanel);
            inventoryPanel.add(giftPanel);
            
            // Second tab: Shop
            JPanel shopPanel = new JPanel(new GridLayout(1, 2));
            
            // Food shop panel
            JPanel foodShopPanel = new JPanel(new BorderLayout());
            foodShopPanel.setBorder(BorderFactory.createTitledBorder("Buy Food"));
            
            // Create shop items table
            String[] shopColumnNames = {"Item", "Effect", "Price", "Action"};
            DefaultTableModel foodShopModel = new DefaultTableModel(shopColumnNames, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return column == 3; // Only the action column is editable
                }
            };
            
            // Add food items to shop
            addFoodItemToShop(foodShopModel, "Apple", 5, 10);
            addFoodItemToShop(foodShopModel, "Smoothie", 10, 20);
            addFoodItemToShop(foodShopModel, "Taco", 20, 40);
            addFoodItemToShop(foodShopModel, "Ramen", 30, 60);
            
            JTable foodShopTable = new JTable(foodShopModel);
            
            // Add button renderer/editor to action column
            foodShopTable.getColumnModel().getColumn(3).setCellRenderer(new ButtonRenderer());
            foodShopTable.getColumnModel().getColumn(3).setCellEditor(
                new ButtonEditor(new JCheckBox(), this, scoreDisplay, "food"));
            
            foodShopPanel.add(new JScrollPane(foodShopTable), BorderLayout.CENTER);
            
            // Gift shop panel
            JPanel giftShopPanel = new JPanel(new BorderLayout());
            giftShopPanel.setBorder(BorderFactory.createTitledBorder("Buy Gifts"));
            
            // Create gift shop table
            DefaultTableModel giftShopModel = new DefaultTableModel(shopColumnNames, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return column == 3; // Only the action column is editable
                }
            };
            
            // Add gift items to shop
            addGiftItemToShop(giftShopModel, "Ball", 5, 10);
            addGiftItemToShop(giftShopModel, "Cards", 10, 20);
            addGiftItemToShop(giftShopModel, "Flowers", 20, 40);
            addGiftItemToShop(giftShopModel, "Hat", 30, 60);
            
            JTable giftShopTable = new JTable(giftShopModel);
            
            // Add button renderer/editor to action column
            giftShopTable.getColumnModel().getColumn(3).setCellRenderer(new ButtonRenderer());
            giftShopTable.getColumnModel().getColumn(3).setCellEditor(
                new ButtonEditor(new JCheckBox(), this, scoreDisplay, "gift"));
            
            giftShopPanel.add(new JScrollPane(giftShopTable), BorderLayout.CENTER);
            
            shopPanel.add(foodShopPanel);
            shopPanel.add(giftShopPanel);
            
            // Add tabs
            tabbedPane.addTab("Current Inventory", inventoryPanel);
            tabbedPane.addTab("Shop", shopPanel);
            
            // Layout components
            multiInventoryDialog.add(scorePanel, BorderLayout.NORTH);
            multiInventoryDialog.add(tabbedPane, BorderLayout.CENTER);
            
            // Add close button at bottom
            UIUtility.button_fontsize = 10f;
            UIUtility.button_width = 90;
            UIUtility.button_height = 25;
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            PixelatedButton closeButton = new UIUtility.PixelatedButton("Close");
            closeButton.addActionListener(event -> multiInventoryDialog.dispose());
            buttonPanel.add(closeButton);
            multiInventoryDialog.add(buttonPanel, BorderLayout.SOUTH);
            
            multiInventoryDialog.setVisible(true);
        });
    }

    /**
     * Creates an inventory table model for displaying food or gift items.
     * 
     * @param isFoodInventory True for food items, false for gift items
     * @return Configured DefaultTableModel with item data
     */
    private DefaultTableModel createInventoryTableModel(boolean isFoodInventory) {
        String[] columnNames = {"Item", "Quantity", "Effect"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        if (isFoodInventory) {
            for (Map.Entry<Inventory.FoodItem, Integer> entry : inventory.getFoodItems().entrySet()) {
                Inventory.FoodItem item = entry.getKey();
                Integer quantity = entry.getValue();
                
                model.addRow(new Object[]{
                    item.getName(), 
                    quantity, 
                    "Fullness: +" + item.getFullnessValue()
                });
            }
        } else {
            for (Map.Entry<Inventory.GiftItem, Integer> entry : inventory.getGiftItems().entrySet()) {
                Inventory.GiftItem item = entry.getKey();
                Integer quantity = entry.getValue();
                
                model.addRow(new Object[]{
                    item.getName(), 
                    quantity, 
                    "Happiness: +" + item.getHappinessValue()
                });
            }
        }

        return model;
    }


    // Helper method to add food items to the shop table
    private void addFoodItemToShop(DefaultTableModel model, String name, int fullnessValue, int price) {
        model.addRow(new Object[]{
            name,
            "Fullness: +" + fullnessValue,
            price,
            "Buy"
        });
    }

    // Helper method to add gift items to the shop table
    private void addGiftItemToShop(DefaultTableModel model, String name, int happinessValue, int price) {
        model.addRow(new Object[]{
            name,
            "Happiness: +" + happinessValue,
            price,
            "Buy"
        });
    }

    // Custom button renderer for tables
    class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
        }
        
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                    boolean isSelected, boolean hasFocus, 
                                                    int row, int column) {
            setText((value == null) ? "Buy" : value.toString());
            return this;
        }
    }

    // Custom button editor for tables
    class ButtonEditor extends DefaultCellEditor {
        protected JButton button;
        private String label;
        private boolean isPushed;
        private GameplayScreen gameScreen;
        private JLabel scoreDisplay;
        private String itemType;
        
        public ButtonEditor(JCheckBox checkBox, GameplayScreen screen, JLabel scoreLabel, String type) {
            super(checkBox);
            gameScreen = screen;
            scoreDisplay = scoreLabel;
            itemType = type;
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(e -> fireEditingStopped());
        }
        
        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                                                    boolean isSelected, int row, int column) {
            label = (value == null) ? "Buy" : value.toString();
            button.setText(label);
            isPushed = true;
            return button;
        }
        
        @Override
        public Object getCellEditorValue() {
            if (isPushed) {
                // Get the item details from the row
                JTable table = (JTable)button.getParent();
                String itemName = (String)table.getValueAt(table.getSelectedRow(), 0);
                int price = Integer.parseInt(table.getValueAt(table.getSelectedRow(), 2).toString());
                
                // Check if player has enough score
                if (player.getScore() >= price) {
                    // Purchase the item
                    if (itemType.equals("food")) {
                        Inventory.FoodItem newItem = new Inventory.FoodItem(
                            itemName, 
                            extractValue(table.getValueAt(table.getSelectedRow(), 1).toString())
                        );
                        inventory.addFoodItem(newItem, 1);
                    } else {
                        Inventory.GiftItem newItem = new Inventory.GiftItem(
                            itemName, 
                            extractValue(table.getValueAt(table.getSelectedRow(), 1).toString())
                        );
                        inventory.addGiftItem(newItem, 1);
                    }
                    
                    // Deduct the score
                    player.setScore(player.getScore() - price);
                    
                    // Update the score display
                    scoreDisplay.setText("Current Score: " + player.getScore());
                    
                    JOptionPane.showMessageDialog(gameScreen, 
                        "You purchased " + itemName + " for " + price + " points!");
                } else {
                    JOptionPane.showMessageDialog(gameScreen, 
                        "Not enough points! You need " + price + " points to buy this item.");
                }
            }
            isPushed = false;
            return label;
        }
        
        // Extract numeric value from strings like "Fullness: +5" or "Happiness: +10"
        private int extractValue(String text) {
            // Find the position of the plus sign
            int plusIndex = text.indexOf("+");
            if (plusIndex != -1) {
                // Extract the number after the plus sign
                String valueStr = text.substring(plusIndex + 1).trim();
                try {
                    return Integer.parseInt(valueStr);
                } catch (NumberFormatException e) {
                    return 0;
                }
            }
            return 0;
        }
        
        @Override
        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }
    }


    private void setupLayout() {
        setLayout(new BorderLayout());
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setOpaque(false);
        
        // Create a sub-panel for pet name
        JPanel namePanel = new JPanel();
        namePanel.setOpaque(false);
        namePanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
        namePanel.add(petNameLabel);
        
        // Wrapper Panel for Score & State
        JPanel wrapperPanel = new JPanel(new GridLayout(2, 1));
        wrapperPanel.setOpaque(false);
        
        // Score Row Panel
        JPanel scoreRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        scoreRow.setOpaque(false);
        
        // State Row Panel
        JPanel stateRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        stateRow.setOpaque(false);
        
        // Create fixed-width panels for score and state labels
        JPanel scoreLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        scoreLabelPanel.setOpaque(false);
        scoreLabelPanel.setPreferredSize(new Dimension(250, 30));
        scoreLabelPanel.add(scoreLabel);
        
        JPanel stateLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        stateLabelPanel.setOpaque(false);
        stateLabelPanel.setPreferredSize(new Dimension(250, 30)); // Same width as score panel
        stateLabelPanel.add(stateLabel);
        
        // Add the labels to their rows
        scoreRow.add(scoreLabelPanel);
        scoreRow.add(playCooldownLabel);
        stateRow.add(stateLabelPanel);
        stateRow.add(vetCooldownLabel);
        
        // Add Rows to Wrapper
        wrapperPanel.add(scoreRow);
        wrapperPanel.add(stateRow);
        
        // Add the panels to the topPanel
        topPanel.add(namePanel);
        topPanel.add(wrapperPanel);
        // topPanel.add(scorePanel);
        // topPanel.add(statePanel);

        // Add parental control timer if enabled
        if (parentalSettings != null && parentalSettings.isTimeRestrictionEnabled() && parentalTimerLabel != null) {
            JPanel timerPanel = new JPanel();
            timerPanel.setOpaque(false);
            timerPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 3));
            timerPanel.add(parentalTimerLabel);
            topPanel.add(timerPanel);
        }

        // Add topPanel to the main layout
        add(topPanel, BorderLayout.NORTH);

        // Center Panel with Pet Image
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(50, 100, 30, 90);
        gbc.gridx = 8;
        gbc.gridy = 0;
        centerPanel.add(petImageLabel, gbc);
        add(centerPanel, BorderLayout.WEST);

        // Bottom Panel with Buttons and Stats
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);
        
        // Add padding to bottom panel
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Stat Panel
        JPanel statPanel = new JPanel(new GridLayout(2, 2, 10, 15));
        statPanel.setOpaque(false);
        
        // Create stat panels with labels centered above progress bars
        statPanel.add(UIUtility.createStatPanel(healthStatLabel, healthProgressBar));
        statPanel.add(UIUtility.createStatPanel(sleepStatLabel, sleepProgressBar));
        statPanel.add(UIUtility.createStatPanel(fullnessStatLabel, fullnessProgressBar));
        statPanel.add(UIUtility.createStatPanel(happinessStatLabel, happinessProgressBar));
        
        // Command Buttons in Two Rows
        JPanel commandPanel = new JPanel(new GridLayout(2, 5, 5, 5));
        commandPanel.setOpaque(false);
        commandPanel.add(feedButton);
        commandPanel.add(giftButton);
        commandPanel.add(vetButton);
        commandPanel.add(battleButton);
        commandPanel.add(exerciseButton);
        commandPanel.add(bedButton);
        commandPanel.add(playButton);
        commandPanel.add(saveButton);
        commandPanel.add(mainMenuButton);
        commandPanel.add(viewInventoryButton);

        bottomPanel.add(commandPanel, BorderLayout.SOUTH);
        bottomPanel.add(statPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }


    /**
     * Updates all pet status displays including:
     * - Progress bars (health, sleep, fullness, happiness)
     * - Score label
     * - State label
     * - Pet image
     */
    private void updatePetStatus() {
        // Update progress bars
        healthProgressBar.setValue(currentPet.getHealth());
        healthProgressBar.setString(currentPet.getHealth() + "");
        healthProgressBar.setForeground(UIUtility.getColorForProgressBar(currentPet.getHealth()));
        // Explicitly check if health is 0 and update pet state if needed
        if (currentPet.getHealth() <= 0 && !currentPet.isDead()) {
            currentPet.setDead(true);
        }

        sleepProgressBar.setValue(currentPet.getSleep());
        sleepProgressBar.setString(currentPet.getSleep() + "");
        sleepProgressBar.setForeground(UIUtility.getColorForProgressBar(currentPet.getSleep()));

        fullnessProgressBar.setValue(currentPet.getFullness());
        fullnessProgressBar.setString(currentPet.getFullness() + "");
        fullnessProgressBar.setForeground(UIUtility.getColorForProgressBar(currentPet.getFullness()));

        happinessProgressBar.setValue(currentPet.getHappiness());
        happinessProgressBar.setString(currentPet.getHappiness() + "");
        happinessProgressBar.setForeground(UIUtility.getColorForProgressBar(currentPet.getHappiness()));

        // Update score and state labels
        scoreLabel.setText("Score: " + player.getScore());
        updateStateLabel();
        
        // Update pet image based on current state
        updatePetImage();
    }

     /**
     * Updates the pet's status label based on current conditions.
     * Shows states like: Dead, Sleeping, Hungry, Angry, or Alive.
     */
    private void updateStateLabel() {
        StringBuilder states = new StringBuilder("Status: ");
        if (currentPet.isDead()) states.append("Dead ");
        else if (currentPet.isSleeping()) states.append("Sleeping ");
        else if (currentPet.isHungry()) states.append("Hungry ");
        else if (currentPet.isAngry()) states.append("Angry ");
        else states.append("Alive ");
        
        stateLabel.setText(states.toString().trim());
    }

    /**
     * Enables/disables action buttons based on pet's current state.
     * Prevents invalid actions (like feeding a sleeping pet).
     */
    private void updateCommandAvailability() {
        // Disable/enable buttons based on pet's state
        boolean isDead = currentPet.isDead();
        boolean isSleeping = currentPet.isSleeping();
        boolean isAngry = currentPet.isAngry();
        boolean isHungry = currentPet.isHungry();

        feedButton.setEnabled(!isDead && !isSleeping);
        giftButton.setEnabled(!isDead && (isAngry || (!isSleeping && !isHungry)));
        vetButton.setEnabled(!isDead && !isSleeping && !isAngry);
        battleButton.setEnabled(!isDead && (isAngry || (!isSleeping && !isHungry)));
        exerciseButton.setEnabled(!isDead && !isSleeping && !isAngry);
        bedButton.setEnabled(!isDead && !isSleeping);
        playButton.setEnabled(!isDead && !isSleeping && !isAngry);
    }
}