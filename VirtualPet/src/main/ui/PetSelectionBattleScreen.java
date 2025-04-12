package group33.VirtualPet.src.main.ui;


import javax.swing.*;

import group33.VirtualPet.src.main.game.TimeRestrictionManager;
import group33.VirtualPet.src.main.game.TimeRestrictionManager.ScreenType;
import group33.VirtualPet.src.main.model.Player;
import group33.VirtualPet.src.main.ui.UIUtility.PixelatedButton;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;


/**
 * A screen for selecting a math battle opponent (operation) in the Virtual Pet game.
 * Displays four opponents (Addition, Subtraction, Multiplication, Division) as clickable images.
 * Allows the player to choose an opponent and start a math battle.
 * 
 * 
 * @author Team 33 (Dhir, Kostya, Fatima, Anna)
 * @since Winter 2025
 * 
 */
public class PetSelectionBattleScreen extends JFrame {
    // Labels for opponent images
    private JLabel[] imageLabels;
    // Names of math operations
    private String[] operations = {"Addition   ", "Subtract  ", "Multiply  ", "Division  "};
    // Internal names for opponents
    private String[] opp = {"jellyfish", "frog", "deer", "penguin"};
    private String selectedOperation = null; // Stores the selected opponent's name
    private JButton fightButton;             // Button to start the battle
    private Player player;                   // Reference to the Player object
    private String currentSaveFilename;      // Current save file name
    
    /**
     * Constructs the opponent selection screen.
     *
     * @param player       The Player object representing the user.
     * @param saveFilename  The filename of the current save file.
     */
    public PetSelectionBattleScreen(Player player, String saveFilename) {
        setTitle("SELECT YOUR OPPONENT");
        setSize(1031, 849);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        // Check time restrictions before initializing
        if (!TimeRestrictionManager.checkAndRedirect(ScreenType.GAMEPLAY_SCREEN, this)) {
            // Redirecting to main menu, stop initialization
            return;
        }

        this.player = player;
        this.currentSaveFilename = saveFilename;

        // Load and Resize GIF Background
        ImageIcon originalBgIcon = new ImageIcon("group33/VirtualPet/assets/math_battle_gifs/background.png");
        Image resizedBgImage = originalBgIcon.getImage().getScaledInstance(1031, 849, Image.SCALE_DEFAULT);
        ImageIcon resizedBgIcon = new ImageIcon(resizedBgImage);
        // Create and add the background label
        JLabel backgroundLabel = new JLabel(resizedBgIcon);
        backgroundLabel.setBounds(0, 0, 1031, 849);
        add(backgroundLabel);

        // Title Label with Pixelated Font
        JLabel titleLabel = UIUtility.createPixelatedLabel("SELECT YOUR OPPONENT", 30);
        titleLabel.setForeground(Color.BLACK);
        titleLabel.setBounds(175, 110, 700, 30);
        backgroundLabel.add(titleLabel);

        // Initialize image labels for opponents
        imageLabels = new JLabel[4];
        int startX = 145;     // Starting X position for images
        int imageY = 300;      // Y position for images
        int textY = 450;       // Y position for text labels
        int imageWidth = 150;  // Width of each image
        int imageHeight = 150; // Height of each image
        int spacing = 200;     // Space between images
        int textLeftOffset = -15; // Offset for text labels

        // Paths to opponent images
        String[] imagePaths = {
            "group33/VirtualPet/assets/math_battle_gifs/image1.png",
            "group33/VirtualPet/assets/math_battle_gifs/image2.png",
            "group33/VirtualPet/assets/math_battle_gifs/image3.png",
            "group33/VirtualPet/assets/math_battle_gifs/image4.png"
        };

        for (int i = 0; i < 4; i++) {
            // Calculate current X position
            int currentX = startX + (i * spacing);
            
            // Load and resize image
            ImageIcon originalIcon = new ImageIcon(imagePaths[i]);
            Image resizedImage = originalIcon.getImage().getScaledInstance(imageWidth, imageHeight, Image.SCALE_SMOOTH);
            ImageIcon resizedIcon = new ImageIcon(resizedImage);

            // Create Image Label
            imageLabels[i] = new JLabel(resizedIcon);
            imageLabels[i].setBounds(currentX, imageY, imageWidth, imageHeight);
            imageLabels[i].setCursor(new Cursor(Cursor.HAND_CURSOR));

            // Add Click Listener
            final int selectedIndex = i;
            imageLabels[i].addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    selectImage(selectedIndex);
                }
            });
            backgroundLabel.add(imageLabels[i]);

            // Create Text Label with centered alignment
            JLabel textLabel = UIUtility.createPixelatedLabel(operations[i], 16); // Increased font size
            textLabel.setForeground(Color.BLACK);
            textLabel.setHorizontalAlignment(SwingConstants.CENTER);
            
            // Adjust text position with left offset
            int textX = currentX + textLeftOffset -12;
            int textWidth = 250; // Fixed width that works for all labels
            
            textLabel.setBounds(textX, textY, textWidth, 30);
            backgroundLabel.add(textLabel);
        }

        // Configure button settings
        UIUtility.button_fontsize = 20f;
        UIUtility.button_width = 140;
        UIUtility.button_height = 30;

        // "FIGHT" Button (Pixelated)
        fightButton = new UIUtility.PixelatedButton("FIGHT");
        fightButton.setBounds(853, 700, UIUtility.button_width, UIUtility.button_height);
        fightButton.setEnabled(false);
        fightButton.addActionListener(e -> startMathBattle());
        backgroundLabel.add(fightButton);

        // "BACK" Button (Pixelated)
        PixelatedButton backButton = new UIUtility.PixelatedButton("BACK");
        backButton.setBounds(40, 700, UIUtility.button_width, UIUtility.button_height);
        backButton.addActionListener(e -> goBack());
        backgroundLabel.add(backButton);

        setVisible(true);
    }

    /**
     * Handles selection of an opponent image.
     * Highlights the selected image and enables the FIGHT button.
     *
     * @param index The index of the selected opponent.
     */
    private void selectImage(int index) {
        selectedOperation = opp[index]; // Store selection
        fightButton.setEnabled(true); // Enable FIGHT button

        // Reset all borders, then highlight the selected one
        for (int i = 0; i < imageLabels.length; i++) {
            if (i == index) {
                imageLabels[i].setBorder(BorderFactory.createLineBorder(Color.YELLOW, 5)); // Glow effect
            } else {
                // Removes border from others
                imageLabels[i].setBorder(null);
            }
        }
    }

    /**
     * Starts a math battle with the selected opponent.
     * Launches the MathBattleScreen and closes this selection screen.
     */
    private void startMathBattle() {
        if (selectedOperation != null) {
            new MathBattleScreen(selectedOperation,this.player,this.currentSaveFilename); // Pass the selected operation as a string
            // Close current screen
            dispose(); 
        }
    }

    /**
     * Returns to the gameplay screen.
     * Launches the GameplayScreen and closes this selection screen.
     */
    private void goBack(){
        new GameplayScreen(this.player,this.currentSaveFilename).setVisible(true);
        // Close the current screen
        dispose(); 
    }
}
