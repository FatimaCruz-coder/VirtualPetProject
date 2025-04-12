package group33.VirtualPet.src.main.ui;

import group33.VirtualPet.src.main.game.GameSaveManager;
import group33.VirtualPet.src.main.game.TimeRestrictionManager;
import group33.VirtualPet.src.main.game.TimeRestrictionManager.ScreenType;
import group33.VirtualPet.src.main.model.Pet;
import group33.VirtualPet.src.main.model.Player;
import group33.VirtualPet.src.main.ui.UIUtility.BackgroundPanel;
import group33.VirtualPet.src.main.ui.UIUtility.PixelatedButton;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;

/**
 * The PlayScreen class represents a window that allows players to interact with their virtual pet
 * through play activities. It displays the pet's happiness level, provides a play button to
 * increase happiness, and shows the player's current score.
 * 
 * @author Team 33 (Dhir, Kostya, Fatima, Anna)
 * @since Winter 2025
 * 
 */
public class PlayScreen extends JFrame {
    // Player data and current pet reference
    private Player player;
    private Pet currentPet;
    private String currentSaveFilename;

    // UI Components
    private JLabel petNameLabel;              // Displays pet name and type
    private JLabel happinessLabel;           // Label for happiness stat
    private JProgressBar happinessProgressBar; // Visual representation of happiness
    private JLabel rewardLabel;              // Shows play reward information
    private PixelatedButton playButton;      // Button to initiate play action
    private PixelatedButton returnButton;    // Button to return to gameplay screen
    private JLabel playGifLabel;             // Label for displaying play animation

    // Constants for gameplay mechanics
    private final int HAPPINESS_INCREASE = 20; // Amount happiness increases per play
    private final int SCORE_INCREASE = 10;     // Points awarded for playing    
    // Background images for different pet types
    private final Map<Pet.PetType, String> playBackgrounds = loadPlayBackgrounds();

    /**
     * Constructs a PlayScreen with the specified player and save filename.
     *
     * @param player the Player object containing pet and score data
     * @param saveFilename the filename to use for saving game progress
     */
    public PlayScreen(Player player, String saveFilename) {
        this.player = player;
        this.currentPet = player.getCurrentPet();
        this.currentSaveFilename = saveFilename;

        // Configure window properties
        setTitle("Pixel Pals - Play Time");
        setSize(1031, 849);
        setResizable(false);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Check time restrictions before initializing
        if (!TimeRestrictionManager.checkAndRedirect(ScreenType.GAMEPLAY_SCREEN, this)) {
            // Redirecting to main menu, stop initialization
            return;
        }

        // Set background based on pet type
        String backgroundPath = playBackgrounds.getOrDefault(currentPet.getType(), 
            "group33/VirtualPet/assets/images/play_room.png");
        BackgroundPanel backgroundPanel = new UIUtility.BackgroundPanel(backgroundPath);
        setContentPane(backgroundPanel);
        backgroundPanel.setLayout(new BorderLayout(10, 10));
        backgroundPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        initializeComponents();
        setupKeyboardShortcuts();
        setupLayout(backgroundPanel);
        updateStatus();
    }

    /**
     * Loads and returns a map of playing background images for different pet types.
     *
     * @return a Map associating each PetType with its corresponding background image path
     */

    private Map<Pet.PetType, String> loadPlayBackgrounds() {
        Map<Pet.PetType, String> backgrounds = new HashMap<>();
        // Deer background
        backgrounds.put(Pet.PetType.DEER, "group33/VirtualPet/assets/images/action_gifs/deer_play.gif");
        // Dog background
        backgrounds.put(Pet.PetType.DOG, "group33/VirtualPet/assets/images/action_gifs/dog_play.gif");
        // Frog background
        backgrounds.put(Pet.PetType.FROG, "group33/VirtualPet/assets/images/action_gifs/frog_play.gif");
        // Penguin background
        backgrounds.put(Pet.PetType.PENGUIN, "group33/VirtualPet/assets/images/action_gifs/penguin_play.gif");
        // Jellyfish background
        backgrounds.put(Pet.PetType.JELLYFISH, "group33/VirtualPet/assets/images/action_gifs/jellyfish_play.gif");
        return backgrounds;
    }

    /**
     * Sets up keyboard shortcuts for all action buttons.
     * Maps keys Enter for excerise, ESC for exit
     */
    private void setupKeyboardShortcuts() {
        JPanel contentPane = (JPanel) getContentPane();
        InputMap inputMap = contentPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = contentPane.getActionMap();
        // Using lambda expressions
        addKeyBinding(inputMap, actionMap, KeyEvent.VK_ENTER, "playAction", playButton);
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
     * Initializes all UI components including labels, progress bars, and buttons.
     */
    private void initializeComponents() {
        // Create pet information labels
        petNameLabel = UIUtility.createPixelatedLabel(currentPet.getName() + " (" + currentPet.getType() + ")", 16);
        petNameLabel.setForeground(Color.DARK_GRAY);

        // Create happiness components
        happinessLabel = UIUtility.createPixelatedLabel("Happiness", 14);
        happinessLabel.setForeground(Color.DARK_GRAY);
        happinessProgressBar = UIUtility.createStatProgressBar(currentPet.getHappiness());

        // Create reward label
        rewardLabel = UIUtility.createPixelatedLabel("Play Reward: +" + SCORE_INCREASE + " points", 12);
        rewardLabel.setForeground(Color.DARK_GRAY);

        // Create buttons
        UIUtility.button_fontsize = 12f;
        UIUtility.button_width = 40;
        UIUtility.button_height = 30;
        playButton = new UIUtility.PixelatedButton("Play (ENTER)");
        returnButton = new UIUtility.PixelatedButton("Exit (ESC)");

        // Initialize GIF label
        playGifLabel = new JLabel();
        playGifLabel.setHorizontalAlignment(JLabel.CENTER);
        playGifLabel.setVisible(false);

        setupButtonListeners();
    }

    /**
     * Sets up action listeners for the play and return buttons.
     */
    private void setupButtonListeners() {
        playButton.addActionListener((ActionEvent e) -> {
            // Award points for playing
            player.setScore(player.getScore() + SCORE_INCREASE);

            // Increase pet happiness
            int newHappiness = Math.min(100, currentPet.getHappiness() + HAPPINESS_INCREASE);
            currentPet.setHappiness(newHappiness);
            updateStatus();

            // Diable button when cooling down
            playButton.setEnabled(false);
            playButton.setText("Cooling down...");

            // Save game if there is a file
            if (!currentSaveFilename.isEmpty()) {
                try {
                    GameSaveManager.saveGame(player, currentSaveFilename);
                    showMessage("Play time complete!\n+" + SCORE_INCREASE + " points\n" +
                               "Happiness +" + HAPPINESS_INCREASE + 
                               "\nYour current score: " + player.getScore());
                } catch (Exception ex) {
                    showMessage("Error saving game: " + ex.getMessage(), 
                              "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                showMessage("Play time complete!\n+" + SCORE_INCREASE + " points\n" +
                "Happiness +" + HAPPINESS_INCREASE + 
                "\nYour current score: " + player.getScore());
            }
        });

        // Return button goes back to gameplay screen
        returnButton.addActionListener((ActionEvent e) -> {
            new GameplayScreen(player, currentSaveFilename).setVisible(true);
            dispose();
        });
    }

    /**
     * Organizes components within the frame using BorderLayout.
     *
     * @param backgroundPanel the panel containing the background image
     */
    private void setupLayout(BackgroundPanel backgroundPanel) {
        // Top panel contains pet name and status
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setOpaque(false);

        JPanel namePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        namePanel.setOpaque(false);
        namePanel.add(petNameLabel);

        JPanel statusPanel = UIUtility.createStatPanel(happinessLabel, happinessProgressBar);
        statusPanel.setOpaque(false);
        statusPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));

        topPanel.add(namePanel);
        topPanel.add(statusPanel);
        // Center panel contains play animation and reward info
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setOpaque(false);
        centerPanel.add(playGifLabel, BorderLayout.CENTER);
        centerPanel.add(rewardLabel, BorderLayout.SOUTH);
        // Bottom panel contains action buttons
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.add(playButton);
        buttonPanel.add(returnButton);
        // Add all panels to background
        backgroundPanel.add(topPanel, BorderLayout.NORTH);
        backgroundPanel.add(centerPanel, BorderLayout.CENTER);
        backgroundPanel.add(buttonPanel, BorderLayout.SOUTH);
    }

    /**
     * Updates the UI to reflect current pet happiness and player score.
     */
    private void updateStatus() {
        happinessProgressBar.setValue(currentPet.getHappiness());
        happinessProgressBar.setString(currentPet.getHappiness() + "");
        happinessProgressBar.setForeground(UIUtility.getColorForProgressBar(currentPet.getHappiness()));

        rewardLabel.setText("Play Reward: +" + SCORE_INCREASE + " points (Your Score: " + player.getScore() + ")");
    }

    /**
     * Displays a message dialog with default title and type.
     *
     * @param message the message to display
     */
    private void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Play Time Complete", JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
    * Displays a message dialog with custom title and type.
    *
    * @param message the message to display
    * @param title the dialog title
    * @param messageType the JOptionPane message type constant
    */
    private void showMessage(String message, String title, int messageType) {
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }

    /**
     * Constructs a PlayScreen without a save file (for unsaved games).
     *
     * @param player the Player object containing pet and score data
     */
    public PlayScreen(Player player) {
        this(player, "");
    }
}