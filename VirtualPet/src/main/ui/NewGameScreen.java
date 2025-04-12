package group33.VirtualPet.src.main.ui;

import group33.VirtualPet.src.main.game.GameSaveManager;
import group33.VirtualPet.src.main.game.TimeRestrictionManager;
import group33.VirtualPet.src.main.game.TimeRestrictionManager.ScreenType;
import group33.VirtualPet.src.main.model.Pet;
import group33.VirtualPet.src.main.model.Player;
import group33.VirtualPet.src.main.ui.UIUtility.BackgroundPanel;
import group33.VirtualPet.src.main.ui.UIUtility.PixelatedButton;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


/**
 * The NewGameScreen class provides a user interface for creating a new virtual pet game.
 * It allows players to select a pet type, name their pet, and start a new game session.
 * The screen displays visual representations of available pets along with descriptions
 * of their characteristics and behaviors.
 * 
 * @author Team 33 (Dhir, Kostya, Fatima, Anna)
 * @since Winter 2025
 */
public class NewGameScreen extends JFrame {
    private Pet.PetType selectedPetType;          // Currently selected pet type
    private JTextField nameField;                 // Text field for pet name input
    private Map<Pet.PetType, ImageIcon> petIcons; // Map of pet types to their icons
    private JPanel petSelectionPanel;             // Panel for pet type selection
    private JPanel rightPanel;                    // Panel for pet details and name input
    private JLabel titleLabel;                    // Main title label
    private JLabel titleLabel2;                   // Secondary title label

    /**
     * Descriptions for each pet type.
     */
    private static final Map<Pet.PetType, String> LONG_DESCRIPTIONS = Map.of(
        Pet.PetType.DOG, "A faithful friend who loves to play fetch and cuddle. This pet has balanced stats with moderate decline rates.",
        Pet.PetType.DEER, "A graceful creature from the forest, calm and serene. Loves nature and gentle walks. This pet has high energy (sleep) but gets hungry faster.",
        Pet.PetType.FROG, "A unique and whimsical creature with unpredictable personality. Never a dull moment! This pet has lower health but slow hunger rate.",
        Pet.PetType.JELLYFISH, "A marine marvel with tentacles of curiosity. Intelligent and full of underwater surprises. This pet has high health but needs more attention (happiness declines faster).",
        Pet.PetType.PENGUIN, "A tuxedo-wearing friend from the arctic. Loves sliding, swimming, and staying cool. This pet gets tired quickly but stays happy longer."
    );

    /**
     * Constructs a new NewGameScreen with all UI components initialized.
     * Sets up the window properties, background, pet selection interface,
     * and navigation controls.
     */
    public NewGameScreen() {
        // Set up the frame properties
        setTitle("Pixel Pals - New Game");

        setSize(1031, 849);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Check time restrictions before initializing
        if (!TimeRestrictionManager.checkAndRedirect(ScreenType.GAMEPLAY_SCREEN, this)) {
            // Redirecting to main menu, stop initialization
            return;
        }

        // Create a custom background panel with image
        BackgroundPanel backgroundPanel = new UIUtility.BackgroundPanel("group33/VirtualPet/assets/images/new_game/selectpet_bg4.png");
        setContentPane(backgroundPanel);
        backgroundPanel.setLayout(new GridBagLayout());

        // Add resize listener to handle component scaling
        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                resizeComponents();
            }
        });
        
        // Create constraints for layout management
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 50, 10, 50);
        gbc.weighty = 2;

        // Create and configure main title label
        titleLabel = UIUtility.createPixelatedLabel("New Game",26);
        titleLabel.setForeground(Color.WHITE);
        titleLabel2 = UIUtility.createPixelatedLabel("Select a virtual pet",13);
        titleLabel2.setForeground(Color.WHITE);
        try {
            titleLabel.setFont(UIUtility.createPixelatedFont().deriveFont(26f));
            titleLabel2.setFont(UIUtility.createPixelatedFont().deriveFont(14f));
        } catch (Exception e) {
            e.printStackTrace();
        }
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        // Title Spacing
        gbc.insets = new Insets(120,-40,0,40);
        backgroundPanel.add(titleLabel, gbc);
        gbc.insets = new Insets(-520,-40,0,40);
        backgroundPanel.add(titleLabel2, gbc);

        // Load pet sprites from assets
        petIcons = loadPetSprites();

        // Create panel for pet selection
        petSelectionPanel = new JPanel();
        petSelectionPanel.setLayout(new BoxLayout(petSelectionPanel, BoxLayout.Y_AXIS)); // Vertical layout
        petSelectionPanel.setOpaque(false);
        petSelectionPanel.setBorder(BorderFactory.createEmptyBorder(10, 70, 20, 70)); // Changed left padding to 20px

        // Create and configure secondary title label
        JPanel petTitlePanel = new JPanel();
        petTitlePanel.setLayout(new BoxLayout(petTitlePanel, BoxLayout.Y_AXIS));
        petTitlePanel.setOpaque(false);
        petTitlePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Add the title panel to the pet selection panel
        petSelectionPanel.add(petTitlePanel);
        petSelectionPanel.add(Box.createRigidArea(new Dimension(0, 20))); // Add space after title
        
        // Add pet selection panel to main frame
        GridBagConstraints gbcPet = new GridBagConstraints();
        gbcPet.gridx = 0; // Align to left
        gbcPet.gridy = 1; // Position in grid
        gbcPet.anchor = GridBagConstraints.NORTHWEST; // Anchor to northwest
        gbcPet.fill = GridBagConstraints.NONE;        // Don't fill
        gbcPet.weightx = 0.3; // Take some horizontal space
        gbcPet.weighty = 1.0; // Take vertical space
        gbcPet.insets = new Insets(0, 50, 0, 50); // Left margin of 20 pixels
        backgroundPanel.add(petSelectionPanel, gbcPet);
    
        // Create radio buttons for pet selection
        createPetSelectionRadioButtons();

        // Right panel for description
        GridBagConstraints gbcRight = new GridBagConstraints();
        gbcRight.gridx = 1;
        gbcRight.gridy = 1;
        gbcRight.gridheight = 2;
        gbcRight.weightx = 1.0;
        gbcRight.weighty = 1.0;
        gbcRight.fill = GridBagConstraints.BOTH;
        gbcRight.insets = new Insets(0, -100, 0, 50);
        rightPanel = new JPanel(new BorderLayout());
        // Empty pancel before pet selecting
        rightPanel.setOpaque(false);
        backgroundPanel.add(rightPanel, gbcRight);

        // Bottom panel with buttons (now includes name input)
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.X_AXIS));
        bottomPanel.setOpaque(false);
        //Postion of panel
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 40, -62));

        // Left spacer
        bottomPanel.add(Box.createHorizontalGlue());

        // Name input panel
        JPanel namePanel = new JPanel();
        namePanel.setLocation(300, 40);
        namePanel.setOpaque(true);
        // Semi-transparent white background
        namePanel.setBackground(new Color(255, 255, 255, 200)); 
        namePanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.BLACK, 2),
            BorderFactory.createEmptyBorder(5, 0, 4, 40)
        ));
        
        JLabel nameLabel = new JLabel("Enter Pet Name:");
        try {
            nameLabel.setFont(UIUtility.createPixelatedFont().deriveFont(14f));
        } catch (Exception e) {
            e.printStackTrace();
        }
        nameLabel.setForeground(Color.BLACK);
        
        nameField = new JTextField(15);
        nameField.setFont(new Font("Monospaced", Font.PLAIN, 14));
        nameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.BLACK, 1),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        
        namePanel.add(nameLabel);
        namePanel.add(nameField);
        bottomPanel.add(namePanel);

        // Spacing
        bottomPanel.add(Box.createRigidArea(new Dimension(60, 0)));
        UIUtility.button_fontsize = 14f;
        UIUtility.button_width = 162;
        UIUtility.button_height = 30;

        // Create Pet button
        PixelatedButton createButton = new PixelatedButton("Create Pet");
        createButton.addActionListener(new CreatePetListener());
        
        bottomPanel.add(createButton);

        // Spacing
        bottomPanel.add(Box.createRigidArea(new Dimension(20, 0)));

        // Main Menu button
        PixelatedButton backButton = new PixelatedButton("Main Menu");
        backButton.addActionListener(e -> openMainMenuScreen());
        bottomPanel.add(backButton);

        // Right spacer
        bottomPanel.add(Box.createHorizontalGlue());

        // Bottom panel constraints
        GridBagConstraints gbcBottom = new GridBagConstraints();
        gbcBottom.gridx = 0;
        gbcBottom.gridy = 3;
        gbcBottom.gridwidth = 2;
        gbcBottom.weightx = 1.0;
        gbcBottom.weighty = 0.1;
        gbcBottom.anchor = GridBagConstraints.SOUTH;
        gbcBottom.fill = GridBagConstraints.HORIZONTAL;
        gbcBottom.insets = new Insets(0, -80, -7, 80);
        backgroundPanel.add(bottomPanel, gbcBottom);

        // Scale images to consistent size
        int imageWidth = 400; // Adjust as needed
        int imageHeight = 400; // Adjust as needed
        for (Pet.PetType type : petIcons.keySet()) {
            ImageIcon icon = petIcons.get(type);
            Image scaledImage = icon.getImage().getScaledInstance(
                imageWidth, imageHeight, Image.SCALE_SMOOTH);
            petIcons.put(type, new ImageIcon(scaledImage));
        }
        
        // For the right panel that will hold everything
        rightPanel.setLayout(new BorderLayout());
        // Adjust the right panel borders to position content better (panel with pet ans description)
        rightPanel.setBorder(BorderFactory.createEmptyBorder(30, 120, 0, 120));
    }
    
    
    /**
     * Creates radio buttons for each available pet type and adds them to the selection panel.
     * Each button includes the pet's icon and name, and handles selection events.
     */
    private void createPetSelectionRadioButtons() {
        petSelectionPanel.removeAll(); // Clear previous buttons if any
        
        // Add back the title panel that was removed when clearing
        JPanel petTitlePanel = new JPanel();
        petTitlePanel.setLayout(new BoxLayout(petTitlePanel, BoxLayout.Y_AXIS));
        petTitlePanel.setOpaque(false);
        petTitlePanel.setBorder(BorderFactory.createEmptyBorder(0, -90, 0, 0)); 
        
        JLabel selectLabel = UIUtility.createPixelatedLabel("Select a", 12f);
        JLabel virtualPetLabel = UIUtility.createPixelatedLabel("Virtual Pet", 12f);
        
        selectLabel.setForeground(Color.BLACK);
        virtualPetLabel.setForeground(Color.BLACK);
        selectLabel.setOpaque(true);
        virtualPetLabel.setOpaque(true);
        selectLabel.setBackground(new Color(255, 255, 255, 220));
        virtualPetLabel.setBackground(new Color(255, 255, 255, 220));
        selectLabel.setBorder(BorderFactory.createEmptyBorder(3, 5, 3, 5));
        virtualPetLabel.setBorder(BorderFactory.createEmptyBorder(3, 5, 3, 5));
        
        try {
            Font petTitleFont = UIUtility.createPixelatedFont().deriveFont(Font.BOLD, 14f);
            selectLabel.setFont(petTitleFont);
            virtualPetLabel.setFont(petTitleFont);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        petTitlePanel.add(selectLabel);
        petTitlePanel.add(virtualPetLabel);
        
        // petSelectionPanel.add(petTitlePanel);
        // icons and label panel
        petSelectionPanel.add(Box.createRigidArea(new Dimension(240, 40))); 
        petSelectionPanel.setOpaque(false);
        
        Pet.PetType[] petTypes = Pet.PetType.values();
    
        for (Pet.PetType petType : petTypes) {
            // Create a panel for each pet (icon + label)
            JPanel petPanel = new JPanel();
            petPanel.setLayout(new BoxLayout(petPanel, BoxLayout.Y_AXIS));
            petPanel.setOpaque(false);
    
            // Get and resize pet icon
            ImageIcon originalIcon = petIcons.get(petType);
            Image scaledImage = originalIcon.getImage().getScaledInstance(55, 55, Image.SCALE_SMOOTH);
            ImageIcon smallIcon = new ImageIcon(scaledImage);
    
            // Create JLabel for pet icon
            JLabel petLabel = new JLabel();
            petLabel.setIcon(smallIcon); // Set resized sprite
            petLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
    
            // Create JLabel for pet name
            JLabel nameLabel = new JLabel(petType.name());
            nameLabel.setFont(new Font("Monospaced", Font.BOLD, 12));
            nameLabel.setForeground(Color.BLACK);
            nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
    
            // Click event to select pet
            petLabel.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    selectPet(petType);
                }
            });
    
            // Add components to pet panel
            petPanel.add(petLabel);
            // Small spacing
            petPanel.add(Box.createRigidArea(new Dimension(40, 3)));
            petPanel.add(nameLabel);
            petPanel.setBounds(100,50,0,0);
            petPanel.setOpaque(false);
    
            // Add pet panel to selection panel
            // icon spacing, vertical spacing
            petSelectionPanel.add(petPanel);
            petSelectionPanel.add(Box.createRigidArea(new Dimension(10, 10)));
        }
    
        petSelectionPanel.revalidate();
        petSelectionPanel.repaint();
    }
    
    /**
     * Handles the selection of a pet type by the user.
     * Updates the display to show detailed information about the selected pet.
     *
     * @param petType The PetType that was selected by the user
     */
    private void selectPet(Pet.PetType petType) {
        selectedPetType = petType;
        
        // Clear right panel
        rightPanel.removeAll();
        rightPanel.setLayout(new BorderLayout());
        // (pet and description panel1)
        rightPanel.setLocation(100, 150);
        // rightPanel.setPreferredSize(new Dimension(600,100));
        
        rightPanel.setOpaque(false);
    
        // Layout for the selected pet view (pet and description panel2)
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setLocation(-100, 150);
        // contentPanel.setPreferredSize(new Dimension(500,50));
        contentPanel.setOpaque(false);
        
        GridBagConstraints c = new GridBagConstraints();
        
        // Add the description at top and to the right
        JPanel descriptionPanel = new JPanel(new BorderLayout());
        descriptionPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.BLACK, 2),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        descriptionPanel.setBackground(Color.WHITE);
        descriptionPanel.setOpaque(true);
        
        JTextArea description = new JTextArea(LONG_DESCRIPTIONS.get(petType));
        description.setWrapStyleWord(true);
        description.setLineWrap(true);
        description.setOpaque(false);
        description.setEditable(false);
        description.setFont(new Font("Monospaced", Font.PLAIN, 13));
        descriptionPanel.add(description, BorderLayout.CENTER);
        
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 2;
        c.weightx = 1.0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(10, 10, 20, 10);
        contentPanel.add(descriptionPanel, c);
        
        // Add the pet image below and slightly to the left
        JLabel petImageLabel = new JLabel();
        // Resize the image
        ImageIcon icon = petIcons.get(petType);
        Image scaledImage = icon.getImage().getScaledInstance(290, 290, Image.SCALE_SMOOTH);
        petImageLabel.setIcon(new ImageIcon(scaledImage));
        
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 1;
        c.weightx = 0.5;
        c.weighty = 1.0;
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.CENTER;
        c.insets = new Insets(0, 0, 0, 0);
        contentPanel.add(petImageLabel, c);
        
        // Add the content panel to the right panel
        rightPanel.add(contentPanel, BorderLayout.CENTER);
    
        rightPanel.revalidate();
        rightPanel.repaint();
    }

/**
 * ActionListener implementation for the Create Pet button.
 * Validates user input, creates a new game save file, and transitions to gameplay.
 */
private class CreatePetListener implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent e) {
        // Validate pet selection
        if (selectedPetType == null) {
            JOptionPane.showMessageDialog(NewGameScreen.this, "Please select a pet type.");
            return;
        }

        // Validate pet name input
        String petName = nameField.getText().trim();
        if (petName.isEmpty()) {
            JOptionPane.showMessageDialog(NewGameScreen.this, "Please enter a pet name.");
            return;
        }

        // Create filename from pet name
        String saveFileName = petName + "_save.csv";

        // Create new player and pet
        Player player = new Player(petName + "'s Owner");
        Pet pet = new Pet(petName, selectedPetType);
        player.adoptPet(pet);

        try {
            // Save the new game
            GameSaveManager.saveGame(player, saveFileName);
            
            // Load the newly created game
            Player loadedPlayer = GameSaveManager.loadGame(saveFileName);
            
            // Open gameplay screen with loaded data
            GameplayScreen gameplayScreen = new GameplayScreen(loadedPlayer, saveFileName);
            gameplayScreen.setVisible(true);
            
            // Close the current window
            dispose();
            
        } catch (IOException ioException) {
            JOptionPane.showMessageDialog(NewGameScreen.this, 
                "Error saving/loading game: " + ioException.getMessage());
        }
    }
}

/**
 * Opens the main menu screen and closes the current screen.
 */
private void openMainMenuScreen() {
    this.setVisible(false);
    MainMenuScreen MainMenu = new MainMenuScreen();
    MainMenu.setVisible(true);
    this.dispose();
}

    /**
     * Loads pet sprites from the assets folder.
     * 
     * @return A map of pet types to their corresponding ImageIcons
     */
    private Map<Pet.PetType, ImageIcon> loadPetSprites() {
        Map<Pet.PetType, ImageIcon> petSprites = new HashMap<>();
        for (Pet.PetType petType : Pet.PetType.values()) {
            String imagePath = "group33/VirtualPet/assets/images/" + petType.name().toLowerCase() + ".png";
            petSprites.put(petType, new ImageIcon(imagePath));
        }
        return petSprites;
    }

    /**
     * Adjusts component sizes and fonts when the window is resized.
     */
    private void resizeComponents() {
        int width = getWidth();
        float baseFontSize = Math.max(16f, width / 55f);
        float titleFontSize = Math.max(26f, width / 33f);
    
        try {
            // Adjust font sizes for various components
            titleLabel.setFont(UIUtility.createPixelatedFont().deriveFont(titleFontSize));
            
            // Adjust other components as needed
            for (Component comp : petSelectionPanel.getComponents()) {
                if (comp instanceof JRadioButton) {
                    ((JRadioButton) comp).setFont(UIUtility.createPixelatedFont().deriveFont(baseFontSize * 0.8f));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    
        // Scale pet sprites to new size
        scalePetSprites();
    }

    /**
     * Scales pet sprites based on current window size.
     */
    private void scalePetSprites() {
        // Calculate new icon size
        int newSize = Math.max(300, 300); 
        
        // Scale each pet icon
        for (Pet.PetType petType : petIcons.keySet()) {
            ImageIcon originalIcon = petIcons.get(petType);
            Image scaledImage = originalIcon.getImage().getScaledInstance(newSize, newSize, Image.SCALE_SMOOTH);
            petIcons.put(petType, new ImageIcon(scaledImage));
        }
    
        // Update radio button icons
        for (Component comp : petSelectionPanel.getComponents()) {
            if (comp instanceof JRadioButton) {
                JRadioButton btn = (JRadioButton) comp;
                btn.setIcon(petIcons.get(Pet.PetType.valueOf(btn.getText())));
            }
        }
    
        // Refresh the panel
        petSelectionPanel.revalidate();
        petSelectionPanel.repaint();
    }

    /**
     * Main method for testing the NewGameScreen independently.
     * 
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new NewGameScreen().setVisible(true));
    }
}