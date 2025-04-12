package group33.VirtualPet.src.main.ui;
import group33.VirtualPet.src.main.game.TimeRestrictionManager;
import group33.VirtualPet.src.main.game.TimeRestrictionManager.ScreenType;
import group33.VirtualPet.src.main.model.Player;
import group33.VirtualPet.src.main.model.mathBattle;
import group33.VirtualPet.src.main.ui.UIUtility.BackgroundPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;
import java.awt.event.KeyEvent;

public class MathBattleScreen extends JFrame {
    private JLabel playerSpriteLabel, opponentSpriteLabel, equationLabel, messageLabel;
    private JLabel playerHealthLabel, opponentHealthLabel;
    private JProgressBar playerHealthBar, opponentHealthBar;
    private JTextField answerField;
    private JButton submitButton;
    private JButton homeButton;
    private mathBattle battle;
    private Random random;
    private int currentAnswer;
    private String playerSpritePath;
    private String oppSpritePath;
    private String playerHappyPath;
    private String oppHappyPath;
    private String loseSpritePath;
    private String oppLosePath;
    private String playerHurtPath;
    private String oppHurtPath;
    private Player player;
    private String currentSaveFilename;

    /** Constructs a new MathBattleScreen with the specified level, player, and save file.
    * Initializes the battle interface and sets up the game components.
    *
    * @param level The difficulty level of the opponent ("Additon", "Subtraction", "Multiplication", "Division")
    * @param player The Player object representing the current user
    * @param file The filename where the game is saved
    *
    * @author Team 33 (Dhir, Kostya, Fatima, Anna)
    * @since Winter 2025
    * 
    */
    public MathBattleScreen(String level, Player player, String file) {
        setTitle("MATH BATTLE");
        setSize(1031, 849);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Check time restrictions before initializing
        if (!TimeRestrictionManager.checkAndRedirect(ScreenType.GAMEPLAY_SCREEN, this)) {
            // Redirecting to main menu, stop initialization
            return;
        }

        this.player = player;
        this.currentSaveFilename = file;

        battle = new mathBattle(level);
        random = new Random();

        // get the type of pet and opponent
        String pet = player.getCurrentPet().getType().toString().toLowerCase();
        String opp = level;

        //initilize sprite paths
        this.playerSpritePath = "group33/VirtualPet/assets/battle_states/"+ pet + "_normal.png";
        this.playerHappyPath =  "group33/VirtualPet/assets/battle_states/"+ pet + "_happy.png";
        this.playerHurtPath = "group33/VirtualPet/assets/battle_states/"+ pet + "_hurt.png";
        this.loseSpritePath = "group33/VirtualPet/assets/battle_states/"+ pet + "_lost.png";

        this.oppSpritePath = "group33/VirtualPet/assets/battle_states/"+ opp + "_normal.png";
        this.oppHappyPath =  "group33/VirtualPet/assets/battle_states/"+ opp + "_happy.png";
        this.oppHurtPath = "group33/VirtualPet/assets/battle_states/"+ opp + "_hurt.png";
        this.oppLosePath = "group33/VirtualPet/assets/battle_states/"+ opp + "_lost.png";

        BackgroundPanel backgroundPanel = new UIUtility.BackgroundPanel("group33/VirtualPet/assets/math_battle_gifs/mathbattleBG.png");
        setContentPane(backgroundPanel);
        backgroundPanel.setLayout(null);

        initializeComponents();
        setupKeyboardShortcuts();
        setupLayout();

        nextQuestion();

        submitButton.addActionListener(e -> checkAnswer());
        setVisible(true);

        homeButton.addActionListener(e -> goBack());
        
    }

    /**
     * Sets up keyboard shortcuts for the frame.
     * ENTER key submits the answer, ESC key returns to home.
     */
    private void setupKeyboardShortcuts() {
        JPanel contentPane = (JPanel) getContentPane();
        InputMap inputMap = contentPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = contentPane.getActionMap();
        addKeyBinding(inputMap, actionMap, KeyEvent.VK_ENTER, "submitAction", submitButton);
        addKeyBinding(inputMap, actionMap, KeyEvent.VK_ESCAPE, "homeAction", homeButton);
    }
    
    /**
     * Helper method to add a key binding to the frame.
     *
     * @param inputMap The InputMap to add the key binding to
     * @param actionMap The ActionMap to associate with the key binding
     * @param keyCode The key code to bind
     * @param actionName The name of the action
     * @param button The button to trigger when the key is pressed
     * 
     */
    private void addKeyBinding(InputMap inputMap, ActionMap actionMap, int keyCode, String actionName, JButton button) {
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
     * Initializes all UI components for the battle screen.
     */
    private void initializeComponents() {
        playerHealthLabel = UIUtility.createPixelatedLabel("Your Health", 16f);
        playerHealthLabel.setForeground(Color.BLACK);
        opponentHealthLabel = UIUtility.createPixelatedLabel("Opponent Health", 16f);
        opponentHealthLabel.setForeground(Color.BLACK);

        playerHealthBar = UIUtility.createStatProgressBar(battle.getpHealth());
        opponentHealthBar = UIUtility.createStatProgressBar(battle.opponent.getHealth());

        playerSpriteLabel = new JLabel(new ImageIcon(playerSpritePath));
        opponentSpriteLabel = new JLabel(new ImageIcon(oppSpritePath));

        equationLabel = UIUtility.createPixelatedLabel("Solve the equation:", 16f);
        equationLabel.setForeground(Color.BLACK);

        answerField = new JTextField();
        answerField.setFont(new Font("Monospaced", Font.BOLD, 18));
        answerField.setHorizontalAlignment(JTextField.CENTER);
        
        UIUtility.button_fontsize = 20f;
        UIUtility.button_width = 140;
        UIUtility.button_height = 30;
        submitButton = new UIUtility.PixelatedButton("Submit Answer");
        submitButton.setToolTipText("Press ENTER");

        homeButton = new UIUtility.PixelatedButton("Home");
        homeButton.setToolTipText("Press ESC");


        messageLabel = UIUtility.createPixelatedLabel("", 18f);
        messageLabel.setForeground(Color.BLACK);
    }
    
    /**
     * Sets up the layout and positioning of all components on the frame.
     */
    private void setupLayout() {
        playerHealthLabel.setBounds(520, 20, 300, 20);
        add(playerHealthLabel);
        playerHealthBar.setBounds(520, 50, 300, 20);
        add(playerHealthBar);

        opponentHealthLabel.setBounds(200, 20, 300, 20);
        add(opponentHealthLabel);
        opponentHealthBar.setBounds(200, 50, 300, 20);
        add(opponentHealthBar);

        opponentSpriteLabel.setBounds(100, 300, 200, 200);
        add(playerSpriteLabel);
        playerSpriteLabel.setBounds(700, 300, 200, 200);
        add(opponentSpriteLabel);

        equationLabel.setBounds(365, 150, 300, 30);
        equationLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(equationLabel);

        answerField.setBounds(359, 400, 300, 30);
        add(answerField);

        homeButton.setBounds(50, 720, UIUtility.button_width, UIUtility.button_height);
        add(homeButton);

        submitButton.setBounds(359, 450, 300, 30);
        add(submitButton);

        messageLabel.setBounds(214, 600, 600, 30);
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(messageLabel);

    }

    /**
     * Generates and displays the next math question in the battle.
     */
    private void nextQuestion() {
        int[][] currentQuestions = battle.opponent.getStat();
        int randomIndex = random.nextInt(currentQuestions.length);
        int[] question = currentQuestions[randomIndex];
        equationLabel.setText(question[0] + " " + getOperator(currentQuestions) + " " + question[1] + " = ?");
        currentAnswer = question[2];
    }

    /**
     * Determines the operator symbol based on the current question set.
     *
     * @param questions The 2D array containing the current set of questions
     * @return The operator symbol as a String ("+", "-", "*", or "/")
     */
    private String getOperator(int[][] questions) {
        // Return operator based on the opponent's math type
        if (questions == battle.addition) return "+";
        if (questions == battle.subtraction) return "-";
        if (questions == battle.multiplication) return "*";
        return "/"; // Division
    }

     /**
     * Checks the player's answer against the correct answer and updates the game state accordingly.
     * Handles correct/incorrect answers, updates health bars, and checks for win/lose conditions.
     */
    private void checkAnswer() {
        try {
            int playerAnswer = Integer.parseInt(answerField.getText());
            if (playerAnswer == currentAnswer) {
                // Show correct GIF for a brief moment
                updateSprite(opponentSpriteLabel, oppHurtPath);
                updateSprite(playerSpriteLabel, playerHappyPath);
                battle.oLose();  // Opponent loses health
                opponentHealthBar.setValue(battle.opponent.getHealth());
                opponentHealthBar.setString(battle.opponent.getHealth() + "");
                opponentHealthBar.setForeground(UIUtility.getColorForProgressBar(battle.opponent.getHealth()));
                messageLabel.setText("Correct! Opponent loses 25 HP.");
            } else {
                // Show wrong GIF for a brief moment
                updateSprite(opponentSpriteLabel, oppHappyPath);
                updateSprite(playerSpriteLabel, playerHurtPath);
                battle.pLose();  // Player loses health
                playerHealthBar.setValue(battle.getpHealth());
                playerHealthBar.setString(battle.getpHealth() + "");
                playerHealthBar.setForeground(UIUtility.getColorForProgressBar(battle.getpHealth()));
                messageLabel.setText("Wrong! You lose 25 HP.");
                
                // Update the Player's pet health when player loses
                // This ensures health changes are maintained when returning to other screens

                int currentHealth = player.getCurrentPet().getHealth();
                // Reduce pet health by 10 for wrong answers
                player.getCurrentPet().setHealth(Math.max(0, currentHealth - 10));
                

            }
            // Clear the answer field for next question
            answerField.setText("");
            
            // Wait for the GIF to change, then switch back to the default
            Timer timer = new Timer(500, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    updateSprite(opponentSpriteLabel, oppSpritePath);//rest the sprites
                    updateSprite(playerSpriteLabel, playerSpritePath);
                }
            });
            timer.setRepeats(false);
            timer.start();

            // Check for win/lose condition
            if (battle.getpHealth() <= 0) {
                messageLabel.setText("You Lost :(");
                updateSprite(opponentSpriteLabel, oppHappyPath);
                updateSprite(playerSpriteLabel, loseSpritePath);
                playerSpritePath = loseSpritePath;
                oppSpritePath = oppHappyPath;
                // Update pet stats on loss
                if (player.getCurrentPet() != null) {
                    int currentHealth = player.getCurrentPet().getHealth();
                    player.getCurrentPet().setHealth(Math.max(0, currentHealth - 10));
                    player.getCurrentPet().setHappiness(Math.max(0, player.getCurrentPet().getHappiness() - 15));
                }
                
                endBattle();
            } else if (battle.opponent.getHealth() <= 0) {
                messageLabel.setText("You Win!");
                updateSprite(opponentSpriteLabel, oppLosePath);
                updateSprite(playerSpriteLabel, playerHappyPath);
                playerSpritePath = playerHappyPath;
                oppSpritePath = oppLosePath;
                // Add score and update pet stats on win
                player.setScore(player.getScore() + 50);
                
                // Update pet stats on win
                if (player.getCurrentPet() != null) {
                    player.getCurrentPet().setHappiness(Math.min(100, player.getCurrentPet().getHappiness() + 15));
                }
                
                endBattle();
            } else {
                // Continue the battle with the next question
                nextQuestion();
            }
        } catch (NumberFormatException ex) {
            messageLabel.setText("Please enter a valid number.");
        }
    }

    
    /**
     * Updates the sprite image displayed in a JLabel.
     *
     * @param label The JLabel to update
     * @param path The path to the new image
     */
    private void updateSprite(JLabel label, String path) {
        label.setIcon(new ImageIcon(path));
    }


    /**
     * Handles the end of the battle, showing a victory or defeat message
     * and prompting the user to return to battle selection.
     *
     * @param isVictory True if the player won the battle, false otherwise
     * 
     */
    private void endBattle() {
        // Show a styled dialog stating that player would return to selection screen
        JOptionPane.showMessageDialog(this, "Battle Over! Taking You Back To The Selection Screen");

        //return player to selection screen
        dispose();
        new PetSelectionBattleScreen(this.player, this.currentSaveFilename);
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
