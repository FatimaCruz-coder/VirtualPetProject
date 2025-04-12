
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
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;

/**
 * The VetScreen class provides the veterinary treatment interface for the Pixel Pals virtual pet game.
 * This screen allows players to heal their pets at a cost of in-game points.
 * 
 * <p>Key Features:
 * <ul>
 *   <li>Pet-specific veterinary clinic backgrounds</li>
 *   <li>Health treatment system with point cost</li>
 *   <li>Real-time health status tracking</li>
 *   <li>Visual feedback through progress bars</li>
 *   <li>Automatic game saving after treatment</li>
 * </ul>
 * 
 * <p>Treatment Effects:
 * <ul>
 *   <li>Health: +25 points (capped at 100)</li>
 *   <li>Score: -50 points (treatment cost)</li>
 * </ul>
 * 
 * @author Team 33 (Dhir, Kostya, Fatima, Anna)
 * @since Winter 2025
 * 
 */

public class VetScreen extends JFrame {

    // Game state references
    private Player player;              // Current player data
    private Pet currentPet;             // Pet being treated
    private String currentSaveFilename; // Current save file name

    // UI Components
    private JLabel petNameLabel;        // Displays pet name and type
    private JLabel healthLabel;         // "Health" label
    private JProgressBar healthProgressBar; // Visual health indicator
    private JLabel costLabel;           // Treatment cost information
    private PixelatedButton treatButton; // Button to perform treatment
    private PixelatedButton returnButton; // Button to return to gameplay
    private JLabel petGifLabel;         // Optional pet animation display

    // Game constants
    private final int TREATMENT_COST = 50;  // Points required for treatment
    private final int HEALTH_INCREASE = 25; // Health points gained per treatment


    // Background images for different pet types
    private final Map<Pet.PetType, String> vetBackgrounds = loadVetBackgrounds();

    /**
     * Constructs a VetScreen with player data and save file information.
     * 
     * @param player The current player object
     * @param saveFilename Name of the save file (empty for new games)
     * 
     */
    public VetScreen(Player player, String saveFilename) {
        this.player = player;
        this.currentPet = player.getCurrentPet();
        this.currentSaveFilename = saveFilename;

        // Configure window properties
        setTitle("Pixel Pals - Vet Clinic");
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
        String backgroundPath = vetBackgrounds.getOrDefault(currentPet.getType(), 
            "group33/VirtualPet/assets/images/vet.png");
        BackgroundPanel backgroundPanel = new UIUtility.BackgroundPanel(backgroundPath);
        setContentPane(backgroundPanel);
        backgroundPanel.setLayout(new BorderLayout(10, 10));
        backgroundPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        initializeComponents();
        setupLayout(backgroundPanel);
        updateHealthStatus();
    }

    /**
     * Loads vet room backgrounds for different pet types
     * @return A map of pet types to their corresponding background paths
     */
    private Map<Pet.PetType, String> loadVetBackgrounds() {
        Map<Pet.PetType, String> backgrounds = new HashMap<>();
        // Deer veterinary background
        backgrounds.put(Pet.PetType.DEER, "group33/VirtualPet/assets/images/action_gifs/deer_vet.gif");
        // Dog veterinary background
        backgrounds.put(Pet.PetType.DOG, "group33/VirtualPet/assets/images/action_gifs/dog_vet.gif");
        // Frog veterinary background
        backgrounds.put(Pet.PetType.FROG, "group33/VirtualPet/assets/images/action_gifs/frog_vet.gif");
        // Penguin veterinary background
        backgrounds.put(Pet.PetType.PENGUIN, "group33/VirtualPet/assets/images/action_gifs/penguin_vet.gif");
        // Jellyfish veterinary background
        backgrounds.put(Pet.PetType.JELLYFISH, "group33/VirtualPet/assets/images/action_gifs/jellyfish_vet.gif");
        return backgrounds;
    }


    /**
     * Alternate constructor for new games without save files.
     * @param player The current player object
     */
    public VetScreen(Player player) {
        this(player, "");
    }

    /**
     * Initializes all UI components including:
     * - Pet info labels
     * - Health status display
     * - Cost information
     * - Action buttons
     * - Optional GIF animation label
     */
    private void initializeComponents() {

        petGifLabel = new JLabel();
        petGifLabel.setHorizontalAlignment(JLabel.CENTER);
        petGifLabel.setVisible(false);
        // Create pet information labels
        petNameLabel = UIUtility.createPixelatedLabel(currentPet.getName() + " (" + currentPet.getType() + ")", 16);
        petNameLabel.setForeground(Color.DARK_GRAY);

        // Create health status components
        healthLabel = UIUtility.createPixelatedLabel("Health", 14);
        healthLabel.setForeground(Color.DARK_GRAY);

        // Reuse the progress bar style from GameplayScreen
        healthProgressBar = UIUtility.createStatProgressBar(currentPet.getHealth());

        // Create cost label
        costLabel = UIUtility.createPixelatedLabel("Treatment Cost: " + TREATMENT_COST + " points", 12);
        costLabel.setForeground(Color.DARK_GRAY);

        // Create buttons
        UIUtility.button_fontsize = 12f;
        UIUtility.button_width = 40;
        UIUtility.button_height = 30;

        // Create action buttons
        treatButton = new UIUtility.PixelatedButton("Treat Pet");
        returnButton = new UIUtility.PixelatedButton("Exit");

        // Add action listeners
        setupButtonListeners();
    }

    /**
     * Configures button action listeners for:
     * - Treat Pet: Handles treatment logic and validation
     * - Exit: Returns to gameplay screen
     */
    private void setupButtonListeners() {
        // Treatment button handler
        treatButton.addActionListener((ActionEvent e) -> {
                // Check if pet already has full health
                if (currentPet.getHealth() == 100){
                    JOptionPane.showMessageDialog(VetScreen.this,
                    "Unable to treat because your pet's health is full!",
                    "Healthy Pet",
                    JOptionPane.INFORMATION_MESSAGE);
                } else {
                    // Check if player has enough points
                    if(player.getScore() >= TREATMENT_COST) {
                        // Deduct treatment cost
                        player.setScore(player.getScore() - TREATMENT_COST);
                        
                        // Increase pet health (capped at 100)
                        int newHealth = Math.min(100, currentPet.getHealth() + HEALTH_INCREASE);
                        currentPet.setHealth(newHealth);
                        updateHealthStatus();
        
                        // Save and show message if file exists
                        if (!currentSaveFilename.isEmpty()) {
                            try {
                                GameSaveManager.saveGame(player, currentSaveFilename);
                                JOptionPane.showMessageDialog(VetScreen.this,
                                        "Treatment successful! Your pet is feeling better.",
                                        "Treatment Complete",
                                        JOptionPane.INFORMATION_MESSAGE);
                                        // Disable button during cooldown
                                        treatButton.setEnabled(false);
                                        treatButton.setText("Cooling Down...");
                            } catch (Exception ex) {
                                JOptionPane.showMessageDialog(VetScreen.this,
                                        "Error saving game: " + ex.getMessage(),
                                        "Save Error",
                                        JOptionPane.ERROR_MESSAGE);
                            }
                        } else {
                            // Show success message for unsaved games
                            JOptionPane.showMessageDialog(VetScreen.this,
                                    "Treatment successful! Your pet is feeling better.",
                                    "Treatment Complete",
                                    JOptionPane.INFORMATION_MESSAGE);
                        }
                    } else {
                        // Show insufficient points message
                        JOptionPane.showMessageDialog(VetScreen.this,
                                "Not enough points! You need " + TREATMENT_COST + " points for treatment.",
                                "Insufficient Points",
                                JOptionPane.WARNING_MESSAGE);
                    } 
                }
        });
        // Exit button handler
        returnButton.addActionListener((ActionEvent e) -> {
            new GameplayScreen(player, currentSaveFilename).setVisible(true);
            dispose();
        });
    }

    /**
     * Arranges UI components in the window layout with:
     * - Top panel: Pet info and health status
     * - Center panel: Animation and cost info
     * - Bottom panel: Action buttons
     * 
     * @param backgroundPanel The background panel to add components to
     */
    private void setupLayout(BackgroundPanel backgroundPanel) {
        // Top panel with pet name and health status
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setOpaque(false);

        // Pet name panel
        JPanel namePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        namePanel.setOpaque(false);
        namePanel.add(petNameLabel);

        // Health status panel
        JPanel statusPanel = UIUtility.createStatPanel(healthLabel, healthProgressBar);
        statusPanel.setOpaque(false);
        statusPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));

        // Assemble top panel
        topPanel.add(namePanel);
        topPanel.add(statusPanel);

        // Center panel for animation and cost info
        JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        centerPanel.setOpaque(false);
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.add(petGifLabel);
        centerPanel.add(costLabel); // Keep the cost label

        // Button panel at bottom
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.add(treatButton);
        buttonPanel.add(returnButton);

        // Add all panels to main background
        backgroundPanel.add(topPanel, BorderLayout.NORTH);
        backgroundPanel.add(centerPanel, BorderLayout.CENTER);
        backgroundPanel.add(buttonPanel, BorderLayout.SOUTH);
    }
    /**
     * Updates the health status display including:
     * - Health progress bar value and color
     * - Treatment cost label with current score
     */
    private void updateHealthStatus() {
        // Update health bar
        healthProgressBar.setValue(currentPet.getHealth());
        healthProgressBar.setString(currentPet.getHealth() + "");
        healthProgressBar.setForeground(UIUtility.getColorForProgressBar(currentPet.getHealth()));
        // Update cost label with current score
        costLabel.setText("Treatment Cost: " + TREATMENT_COST + " points (Your Score: " + player.getScore() + ")");
    }
}