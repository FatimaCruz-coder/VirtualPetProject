package group33.VirtualPet.src.main.ui;

import group33.VirtualPet.src.main.game.TimeRestrictionManager;
import group33.VirtualPet.src.main.game.TimeRestrictionManager.ScreenType;

import group33.VirtualPet.src.main.game.GameSaveManager;
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
 * The ExerciseScreen class provides the exercise activity interface for the game.
 * This screen allows players to exercise their pets, which affects multiple pet statistics such as health, sleepiness, and hunger.
 * 
 * <p>Key Features:
 * <ul>
 *   <li>Pet-specific exercise backgrounds with animated GIFs</li>
 *   <li>Statistic Tracker (health, hunger, sleepiness)</li>
 *   <li>Score reward system for exercising</li>
 *   <li>Visual feedback through progress bars</li>
 *   <li>Automatic game saving after exercise</li>
 * </ul>
 * 
 * <p>Exercise Effects:
 * <ul>
 *   <li>Health: +20 points (capped at 100)</li>
 *   <li>Hunger: -10 points (minimum 0)</li>
 *   <li>Sleepiness: -10 points (minimum 0)</li>
 *   <li>Score: +15 points</li>
 * </ul>
 * 
 * @author Team 33 (Dhir, Kostya, Fatima, Anna)
 * @since Winter 2025
 * 
 */
public class ExerciseScreen extends JFrame {
    private Player player;
    private Pet currentPet;
    private String currentSaveFilename;

    // UI Components
    private JLabel petNameLabel;
    private JLabel healthLabel, hungerLabel, sleepinessLabel;
    private JProgressBar healthProgressBar, hungerProgressBar, sleepinessProgressBar;
    private JLabel costLabel;
    private PixelatedButton exerciseButton;
    private PixelatedButton returnButton;
    private JLabel exerciseGifLabel;

    // Constants
    private final int SCORE_INCREASE = 15;
    private final int HEALTH_INCREASE = 20;
    private final int HUNGER_DECREASE = 10;
    private final int SLEEPINESS_DECREASE = 10;
    
    // Background images for different pet types
    private final Map<Pet.PetType, String> exerciseBackgrounds = loadExerciseBackgrounds();

    /**
     * Constructs an ExerciseScreen with the specified player and save file.
     * Initializes all UI components and loads pet-specific background.
     * 
     * @param player The Player object containing game state
     * @param saveFilename The filename of the current save (empty string for new games)
     */
    public ExerciseScreen(Player player, String saveFilename) {
        this.player = player;
        this.currentPet = player.getCurrentPet();
        this.currentSaveFilename = saveFilename;

        setTitle("Pixel Pals - Exercise Park");
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
        String backgroundPath = exerciseBackgrounds.getOrDefault(currentPet.getType(), 
            "group33/VirtualPet/assets/images/exercise_park.png");
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
     * Loads pet-specific exercise background images.
     * Each pet type has a custom animated GIF for the exercise activity.
     * 
     * @return Map associating each PetType with its exercise background path
     */
    private Map<Pet.PetType, String> loadExerciseBackgrounds() {
        Map<Pet.PetType, String> backgrounds = new HashMap<>();
        backgrounds.put(Pet.PetType.DEER, "group33/VirtualPet/assets/images/action_gifs/deer_exercise.gif");
        backgrounds.put(Pet.PetType.DOG, "group33/VirtualPet/assets/images/action_gifs/dog_exercise.gif");
        backgrounds.put(Pet.PetType.FROG, "group33/VirtualPet/assets/images/action_gifs/frog_exercise.gif");
        backgrounds.put(Pet.PetType.PENGUIN, "group33/VirtualPet/assets/images/action_gifs/penguin_exercise.gif");
        backgrounds.put(Pet.PetType.JELLYFISH, "group33/VirtualPet/assets/images/action_gifs/jellyfish_exercise.gif");
        return backgrounds;
    }

    /**
     * Sets up keyboard shortcuts for all action buttons.
     * Maps keys ENTER for excerise, ESC for exit
     */
    private void setupKeyboardShortcuts() {
        JPanel contentPane = (JPanel) getContentPane();
        InputMap inputMap = contentPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = contentPane.getActionMap();
        // Using lambda expressions
        addKeyBinding(inputMap, actionMap, KeyEvent.VK_ENTER, "exerciseAction", exerciseButton);
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
     * Initializes all UI components including:
     * - Pet name and type label
     * - Stat progress bars (health, hunger, sleepiness)
     * - Exercise reward label
     * - Action buttons (Exercise and Exit)
     * - GIF animation label
     */
    private void initializeComponents() {
        // Create pet information labels
        petNameLabel = UIUtility.createPixelatedLabel(currentPet.getName() + " (" + currentPet.getType() + ")", 16);
        petNameLabel.setForeground(Color.DARK_GRAY);

        // Create status components
        healthLabel = UIUtility.createPixelatedLabel("Health", 14);
        healthLabel.setForeground(Color.DARK_GRAY);
        healthProgressBar = UIUtility.createStatProgressBar(currentPet.getHealth());

        hungerLabel = UIUtility.createPixelatedLabel("Hunger", 14);
        hungerLabel.setForeground(Color.DARK_GRAY);
        hungerProgressBar = UIUtility.createStatProgressBar(currentPet.getFullness());

        sleepinessLabel = UIUtility.createPixelatedLabel("Sleepiness", 14);
        sleepinessLabel.setForeground(Color.DARK_GRAY);
        sleepinessProgressBar = UIUtility.createStatProgressBar(currentPet.getSleep());

        // Changed from cost label to reward label
        costLabel = UIUtility.createPixelatedLabel("Exercise Reward: +" + SCORE_INCREASE + " points", 12);
        costLabel.setForeground(Color.DARK_GRAY);

        // Create buttons
        UIUtility.button_fontsize = 12f;
        UIUtility.button_width = 40;
        UIUtility.button_height = 30;
        exerciseButton = new UIUtility.PixelatedButton("Exercise (ENTER)");
        returnButton = new UIUtility.PixelatedButton("Exit (ESC)");

        // Initialize GIF label
        exerciseGifLabel = new JLabel();
        exerciseGifLabel.setHorizontalAlignment(JLabel.CENTER);
        exerciseGifLabel.setVisible(false);

        setupButtonListeners();
    }
    
    /**
     * Sets up button action listeners for:
     * - Exercise button: Applies stat changes and rewards
     * - Exit button: Returns to gameplay screen
     */
    private void setupButtonListeners() {
        exerciseButton.addActionListener((ActionEvent e) -> {
            // Give reward after updating stats
            player.setScore(player.getScore() + SCORE_INCREASE);

            // Update pet stats first
            int newHealth = Math.min(100, currentPet.getHealth() + HEALTH_INCREASE);
            currentPet.setHealth(newHealth);
            
            int newHunger = Math.max(0, currentPet.getFullness() - HUNGER_DECREASE);
            currentPet.setFullness(newHunger);
            
            int newSleepiness = Math.max(0, currentPet.getSleep() - SLEEPINESS_DECREASE);
            currentPet.setSleep(newSleepiness);
            
            updateStatus();
            exerciseButton.setEnabled(false);
            exerciseButton.setText("Exercising...");
        
            if (!currentSaveFilename.isEmpty()) {
                try {
                    GameSaveManager.saveGame(player, currentSaveFilename);
                    showMessage("Exercise complete!\n+" + SCORE_INCREASE + " points" + 
                              "\nHealth +" + HEALTH_INCREASE + 
                              "\nHunger -" + HUNGER_DECREASE + 
                              "\nSleepiness -" + SLEEPINESS_DECREASE + "\nYour current score : " + player.getScore());
                } catch (Exception ex) {
                    showMessage("Error saving game: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                showMessage("Exercise complete!\n+" + SCORE_INCREASE + " points" + 
                          "\nHealth +" + HEALTH_INCREASE + 
                          "\nHunger -" + HUNGER_DECREASE + 
                          "\nSleepiness -" + SLEEPINESS_DECREASE +
                          "\nYour current score : " + player.getScore());
            }
        });

        returnButton.addActionListener((ActionEvent e) -> {
            new GameplayScreen(player, currentSaveFilename).setVisible(true);
            dispose();
        });
    }

    /**
     * Arranges all UI components in the window layout with:
     * - Top panel: Pet info and stats
     * - Center panel: Animation and reward info
     * - Bottom panel: Action buttons
     * 
     * @param backgroundPanel The background panel to add components to
     */
    private void setupLayout(BackgroundPanel backgroundPanel) {
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setOpaque(false);

        JPanel namePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        namePanel.setOpaque(false);
        namePanel.add(petNameLabel);

        // Create status panels
        JPanel healthPanel = UIUtility.createStatPanel(healthLabel, healthProgressBar);
        JPanel hungerPanel = UIUtility.createStatPanel(hungerLabel, hungerProgressBar);
        JPanel sleepinessPanel = UIUtility.createStatPanel(sleepinessLabel, sleepinessProgressBar);
        
        healthPanel.setOpaque(false);
        hungerPanel.setOpaque(false);
        sleepinessPanel.setOpaque(false);
        
        JPanel statsPanel = new JPanel();
        statsPanel.setLayout(new BoxLayout(statsPanel, BoxLayout.Y_AXIS));
        statsPanel.setOpaque(false);
        statsPanel.add(healthPanel);
        statsPanel.add(hungerPanel);
        statsPanel.add(sleepinessPanel);
        statsPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));

        topPanel.add(namePanel);
        topPanel.add(statsPanel);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setOpaque(false);
        centerPanel.add(exerciseGifLabel, BorderLayout.CENTER);
        centerPanel.add(costLabel, BorderLayout.SOUTH);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.add(exerciseButton);
        buttonPanel.add(returnButton);

        backgroundPanel.add(topPanel, BorderLayout.NORTH);
        backgroundPanel.add(centerPanel, BorderLayout.CENTER);
        backgroundPanel.add(buttonPanel, BorderLayout.SOUTH);
    }

    /**
     * Updates all status displays including:
     * - Health progress bar
     * - Hunger progress bar
     * - Sleepiness progress bar
     * - Score reward label
     */
    private void updateStatus() {
        // Update health
        healthProgressBar.setValue(currentPet.getHealth());
        healthProgressBar.setString(currentPet.getHealth() + "");
        healthProgressBar.setForeground(UIUtility.getColorForProgressBar(currentPet.getHealth()));
        
        // Update hunger
        hungerProgressBar.setValue(currentPet.getFullness());
        hungerProgressBar.setString(currentPet.getFullness() + "");
        hungerProgressBar.setForeground(UIUtility.getColorForProgressBar(currentPet.getFullness()));
        
        // Update sleepiness
        sleepinessProgressBar.setValue(currentPet.getSleep());
        sleepinessProgressBar.setString(currentPet.getSleep() + "");
        sleepinessProgressBar.setForeground(UIUtility.getColorForProgressBar(currentPet.getSleep()));

        costLabel.setText("Score Increase: " + SCORE_INCREASE + " points (Your Score: " + player.getScore() + ")");
    }

    /**
     * Shows an informational message dialog with exercise results.
     * 
     * @param message The message to display
     */
    private void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Exercise Complete", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Shows a customizable message dialog.
     * 
     * @param message The message content
     * @param title The dialog title
     * @param messageType The JOptionPane message type constant
     */
    private void showMessage(String message, String title, int messageType) {
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }

    /**
     * Overloaded constructor for games without a save file
     * 
     * @param player 
     */
    public ExerciseScreen(Player player) {
        this(player, "");
    }
}