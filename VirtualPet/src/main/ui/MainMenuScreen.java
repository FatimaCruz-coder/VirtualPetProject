package group33.VirtualPet.src.main.ui;

import group33.VirtualPet.src.main.game.TimeRestrictionManager;
import group33.VirtualPet.src.main.game.TimeRestrictionManager.ScreenType;

import group33.VirtualPet.src.main.game.GameSaveManager;
import group33.VirtualPet.src.main.model.Player;
import group33.VirtualPet.src.main.ui.UIUtility.BackgroundPanel;
import group33.VirtualPet.src.main.ui.UIUtility.PixelatedButton;
import java.awt.*;
import java.io.IOException;
import java.util.List;
import javax.swing.*;

/**
 * The main menu screen for the Pixel Pals virtual pet game.
 * Provides navigation to all major game functions including new game,
 * load game, instructions, parental controls, and exit options.
 * 
 * @author Team 33 (Dhir, Kostya, Fatima, Anna)
 * @since Winter 2025
 */
public class MainMenuScreen extends JFrame {
    private PixelatedButton newGameButton;
    private PixelatedButton loadGameButton;
    private PixelatedButton instructionsButton;
    private PixelatedButton parentalControlsButton;
    private PixelatedButton exitButton;
    
    /**
     * Constructs the main menu screen with all UI components.
     * Initializes the background, title, and navigation buttons.
     */
    public MainMenuScreen() {
        // Set up the frame
        setTitle("Pixel Pals");
        setSize(1031, 849);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create a custom background panel
        BackgroundPanel backgroundPanel = new BackgroundPanel("group33/VirtualPet/assets/images/main.gif");

        setContentPane(backgroundPanel);
        backgroundPanel.setLayout(new GridBagLayout());
        
        // Create constraints for layout
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(30, 50, 10, 50);
        
        // Create pixelated game title
        JLabel titleLabel = UIUtility.createPixelatedLabel("Pixel Pals",57);
        titleLabel.setForeground(Color.BLACK);
        gbc.anchor = GridBagConstraints.PAGE_START;
        gbc.insets = new Insets(100, 50, 50, 50);
        backgroundPanel.add(titleLabel, gbc);

        gbc.gridx = 0;
        gbc.gridy = GridBagConstraints.RELATIVE;
        gbc.weighty = 1;
        gbc.insets = new Insets(-20, 50, -20, 50);  // Reduced vertical padding between buttons
        gbc.ipady = 12;  // Reduced internal padding within buttons 

        // Reset insets for buttons
        gbc.insets = new Insets(9, 100, 9, 100);
        
        // Create pixelated buttons
        UIUtility.button_fontsize = 20f;
        UIUtility.button_width = 180;
        UIUtility.button_height = 38;
        newGameButton = new UIUtility.PixelatedButton("New Game");
        loadGameButton = new UIUtility.PixelatedButton("Load Game");
        instructionsButton = new UIUtility.PixelatedButton("Instructions");
        parentalControlsButton = new UIUtility.PixelatedButton("Parental Controls");
        exitButton = new UIUtility.PixelatedButton("Exit");
        
        // Add action listeners
        newGameButton.addActionListener(e -> openNewGameScreen());
        loadGameButton.addActionListener(e -> openLoadGameScreen());
        instructionsButton.addActionListener(e -> openInstructionsScreen());
        parentalControlsButton.addActionListener(e -> openParentalControlsScreen());
        exitButton.addActionListener(e -> System.exit(0));
        
        // Add buttons to the panel
        backgroundPanel.add(newGameButton, gbc);
        backgroundPanel.add(loadGameButton, gbc);
        backgroundPanel.add(instructionsButton, gbc);
        backgroundPanel.add(parentalControlsButton, gbc);
        backgroundPanel.add(exitButton, gbc);
        
        // Add team and project info
        JLabel infoLabel = new JLabel(
            "<html><div style='text-align: center;'>" +
            "<>Created by: Team 33 (Dhir, Kostya, Fatima, Anna) </b><br>" +
            "<b>CS2212 - Western University</b><br>" +
            "<b>Winter 2025</b>" +
            "</div></html>", 
            SwingConstants.CENTER
        );
        infoLabel.setForeground(Color.WHITE);
        infoLabel.setFont(new Font(Font.MONOSPACED, Font.BOLD, 14));
        
        gbc.gridx = 0;
        gbc.gridy = GridBagConstraints.RELATIVE;
        gbc.weighty = 1;
        gbc.anchor = GridBagConstraints.PAGE_END;
        gbc.insets = new Insets(0, 50, 5, 50);
        backgroundPanel.add(infoLabel, gbc);

        
    }

     /**
     * Opens the new game screen when the New Game button is clicked.
     * Hides the main menu and displays the new game creation screen.
     */
    private void openNewGameScreen() {
        // Check time restrictions before loading a game
        if (!TimeRestrictionManager.enforceTimeRestrictions(ScreenType.GAMEPLAY_SCREEN, this)) {
            // If time restrictions prevent loading, just return
            return;
        }

        // OptionPane.showMessageDialog(this, "New Game Screen - Not Implemented Yet");

        // hide MainMenuScreen window
        this.setVisible(false);
        
        NewGameScreen newGameScreen = new NewGameScreen();
        newGameScreen.setVisible(true);

        this.dispose();
    }

/**
 * Opens the load game dialog showing available save files with pet information.
 * Displays pet name, type, and stats instead of filenames. Shows error message
 * if no save files are found.
 */
private void openLoadGameScreen() {
    if (!TimeRestrictionManager.enforceTimeRestrictions(ScreenType.GAMEPLAY_SCREEN, this)) {
        return;
    }

    List<String> saveFiles = GameSaveManager.listSaveFiles();
    if (saveFiles.isEmpty()) {
        JOptionPane.showMessageDialog(this, 
            "No save files found. Please start a new game.", 
            "Load Game", 
            JOptionPane.INFORMATION_MESSAGE);
        return;
    }
    
    JDialog loadDialog = new JDialog(this, "Load Game", true);
    loadDialog.setSize(1031, 849);
    loadDialog.setLocationRelativeTo(this);
    
    // Background setup
    BackgroundPanel backgroundPanel = new BackgroundPanel(
        "group33/VirtualPet/assets/images/stripe_greenbg.png");
    backgroundPanel.setLayout(new BorderLayout());
    loadDialog.setContentPane(backgroundPanel);
    
    // Main content panel
    JPanel contentPanel = new JPanel(new BorderLayout());
    contentPanel.setOpaque(false);
    contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    
    // Title label
    JLabel titleLabel = UIUtility.createPixelatedLabel("SELECT YOUR PET", 24f);
    titleLabel.setForeground(Color.BLACK);
    titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
    contentPanel.add(titleLabel, BorderLayout.NORTH);
    
    // Slots panel
    JPanel slotsPanel = new JPanel();
    slotsPanel.setLayout(new BoxLayout(slotsPanel, BoxLayout.Y_AXIS));
    slotsPanel.setOpaque(false);
    
    JScrollPane scrollPane = new JScrollPane(slotsPanel);
    scrollPane.setOpaque(false);
    scrollPane.getViewport().setOpaque(false);
    scrollPane.setBorder(BorderFactory.createEmptyBorder());
    
    // Load slots
    for (String saveFile : saveFiles) {
        try {
            Player player = GameSaveManager.loadGame(saveFile);
            slotsPanel.add(createGameSlotPanel(player, saveFile, loadDialog));
            slotsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    contentPanel.add(scrollPane, BorderLayout.CENTER);
    
    // Cancel button
    UIUtility.button_fontsize = 20f;
    UIUtility.button_width = 150;
    UIUtility.button_height = 40;
    PixelatedButton cancelButton = new PixelatedButton("Cancel");
    cancelButton.addActionListener(e -> loadDialog.dispose());
    
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    buttonPanel.setOpaque(false);
    buttonPanel.add(cancelButton);
    contentPanel.add(buttonPanel, BorderLayout.SOUTH);
    
    backgroundPanel.add(contentPanel, BorderLayout.CENTER);
    loadDialog.setVisible(true);
}
/**
 * Creates a visual game slot panel showing pet information.
 * 
 * @param player The loaded player object
 * @param saveFile The filename of the save
 * @param parentDialog The parent dialog for reference
 * @return A JPanel representing the game slot
 */
private JPanel createGameSlotPanel(Player player, String saveFile, JDialog parentDialog) {
    JPanel slotPanel = new JPanel(new BorderLayout(10, 0));
    slotPanel.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(Color.GRAY, 1),
        BorderFactory.createEmptyBorder(5, 5, 5, 5)
    ));
    slotPanel.setMaximumSize(new Dimension(600, 80));
    
    // Pet image
    String petType = player.getCurrentPet().getType().toString().toLowerCase();
    ImageIcon petIcon = new ImageIcon("group33/VirtualPet/assets/pets/" + petType + ".png");
    JLabel petImage = new JLabel(petIcon);
    petImage.setPreferredSize(new Dimension(70, 70));
    slotPanel.add(petImage, BorderLayout.WEST);
    
    // Pet info
    JPanel infoPanel = new JPanel(new GridLayout(2, 1, 0, 5));
    infoPanel.setOpaque(false);
    
    JLabel nameLabel = UIUtility.createPixelatedLabel(
        player.getCurrentPet().getName() + " (" + petType + ")", 
        16f
    );

    // Get the pet score of each game
    int score = player.getScore(); 
    // Display the score on the panel
    JLabel scoreLabel = UIUtility.createPixelatedLabel(
        "Score: " + score + " pts",
        14f
    );
    infoPanel.add(nameLabel);
    infoPanel.add(scoreLabel);
    slotPanel.add(infoPanel, BorderLayout.CENTER);
    
    // Load button
    PixelatedButton loadButton = new PixelatedButton("Load");
    loadButton.addActionListener(e -> {
        parentDialog.dispose();
        this.setVisible(false);
        new GameplayScreen(player, saveFile).setVisible(true);
        this.dispose();
    });
    slotPanel.add(loadButton, BorderLayout.EAST);
    
    return slotPanel;
}


    /**
     * Opens the game instructions/tutorial screen.
     * Hides the main menu and displays the tutorial information.
     */
    private void openInstructionsScreen() {
        // JOptionPane.showMessageDialog(this, "Instructions Screen - Not Implemented Yet");
        this.setVisible(false);

        TutorialScreen tutorialScreen = new TutorialScreen();
        tutorialScreen.setVisible(true);

        this.dispose();

    }
    
    /**
     * Opens the parental controls configuration screen.
     * Hides the main menu and displays parental control settings.
     */
    private void openParentalControlsScreen() {
        // JOptionPane.showMessageDialog(this, "Parental Controls Screen - Not Implemented Yet");
        // hide MainMenuScreen window
        this.setVisible(false);

        ParentalControlsScreen parentalControl = new ParentalControlsScreen();
        parentalControl.setVisible(true);

        this.dispose();
    }

    /**
     * Main entry point for testing the MainMenuScreen independently.
     * 
     * @param args Command line arguments
     */    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainMenuScreen mainMenu = new MainMenuScreen();
            mainMenu.setVisible(true);
        });
    }
}
    