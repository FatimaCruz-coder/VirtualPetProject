package group33.VirtualPet.src.main.ui;

import group33.VirtualPet.src.main.ui.UIUtility.PixelatedButton;
import java.awt.*;
import java.io.File;
import javax.swing.*;
import javax.swing.text.*;

/**
 * The TutorialScreen class provides an interactive tutorial/guide for the Pixel Pals virtual pet game.
 * It displays instructional content with sections covering all of the major game features and how they work.
 * 
 * <p>Key Features:
 * <ul>
 *   <li>Styled text display with custom "Press Start 2P" pixel font</li>
 *   <li>Color-coded sections with emoji icons for visual organization</li>
 *   <li>Scrollable content area for lengthy tutorial text</li>
 *   <li>Return button to navigate back to main menu</li>
 *   <li>Light blue background theme matching game aesthetic</li>
 * </ul>
 * 
 * <p>The tutorial covers:
 * <ul>
 *   <li>Game introduction and basic concepts</li>
 *   <li>Main menu navigation</li>
 *   <li>Parental controls system</li>
 *   <li>Pet selection and naming</li>
 *   <li>Core gameplay mechanics and activities</li>
 *   <li>Pet status indicators and states</li>
 *   <li>Inventory management</li>
 *   <li>Scoring system</li>
 * </ul>
 * 
 * @author Team 33 (Dhir, Kostya, Fatima, Anna)
 * @since Winter 2025
 * 
 */

public class TutorialScreen extends JFrame {
    /**
     * Button to return to the main menu
     */
    private PixelatedButton mainmenuButton;

    /**
     * Constructs and initializes the tutorial screen with all UI components.
     * Sets up:
     * - Window properties (title, size, close operation)
     * - Light blue background color scheme
     * - Styled text pane with tutorial content
     * - Custom pixel font loading (falling back to monospace)
     * - Scrollable content area
     * - Return to main menu button
     * 
     * @author Team 33 (Dhir, Kostya, Fatima, Anna)
     * @since Winter 2025
     * 
     */
    public TutorialScreen() {
        setTitle("Tutorial Screen");
        setSize(1031, 849);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Define a light blue background color.
        Color lightBlue = new Color(173, 216, 230);
        getContentPane().setBackground(lightBlue);
        
        // Create a JTextPane for styled text.
        JTextPane textPane = new JTextPane();
        textPane.setEditable(false);
        textPane.setBackground(lightBlue);
        
        // Load the "Press Start 2P" font.
        Font pressStart2P;
        try {
            // Replace the path below with the actual location of your PressStart2P-Regular.ttf file.
            pressStart2P = Font.createFont(Font.TRUETYPE_FONT, new File("path/PressStart2P-Regular.ttf"))
                              .deriveFont(Font.PLAIN, 18f);
        } catch (Exception e) {
            System.err.println("Press Start 2P font not loaded, using default monospaced font.");
            pressStart2P = new Font("Monospaced", Font.PLAIN, 18);
        }
        
        // Get the StyledDocument to allow styling of text.
        StyledDocument doc = textPane.getStyledDocument();
        
        // Title style: larger font, bold, yellow background.
        Style titleStyle = textPane.addStyle("TitleStyle", null);
        StyleConstants.setFontFamily(titleStyle, pressStart2P.getFamily());
        StyleConstants.setFontSize(titleStyle, 24);
        StyleConstants.setBold(titleStyle, true);
        StyleConstants.setBackground(titleStyle, Color.YELLOW);
        StyleConstants.setForeground(titleStyle, Color.BLACK); // Makes text readable on yellow background


        
        // Heading style: using Press Start 2P with bold formatting.
        Style headingStyle = textPane.addStyle("HeadingStyle", null);
        StyleConstants.setFontFamily(headingStyle, pressStart2P.getFamily());
        StyleConstants.setFontSize(headingStyle, 18);
        StyleConstants.setBold(headingStyle, true);
        
        // Default style for regular text.
        Style defaultStyle = textPane.addStyle("DefaultStyle", null);
        StyleConstants.setFontFamily(defaultStyle, pressStart2P.getFamily());
        StyleConstants.setFontSize(defaultStyle, 18);
        StyleConstants.setBold(defaultStyle, false);
        
        try {
            // Insert Title.
            doc.insertString(doc.getLength(), "🎮 # PixelPal: A Playful Guide for the  Digital Pet Enthusiast\n\n", titleStyle);
            
            // Insert Intro Paragraph.
            doc.insertString(doc.getLength(), "🐾 Welcome to PixelPal—a virtual pet experience that's as quirky as it is charming. Whether you're here to unwind after a long day or explore a world where care meets a hint of strategy, this guide has you covered. Let’s dive into the details!\n\n", defaultStyle);
            
            // Insert Separator.
            doc.insertString(doc.getLength(), "✨ ---\n\n", defaultStyle);
            
            // Insert Heading: Introduction.
            doc.insertString(doc.getLength(), "## 🌟 Introduction\n", headingStyle);
            
            // Insert Introduction Content.
            doc.insertString(doc.getLength(), "PixelPal isn’t just another game—it's a digital pet that evolves based on how you nurture it. Your choices shape its appearance and behavior, offering endless possibilities for growth. This guide breaks down everything from game controls to hidden mechanics, all with a dash of wit.\n\n", defaultStyle);
            
            // Insert Separator.
            doc.insertString(doc.getLength(), "🎮 ---\n\n", defaultStyle);
            
            // Insert Heading: The Main Menu.
            doc.insertString(doc.getLength(), "## 🎛️ The Main Menu: Your Command Center\n", headingStyle);
            
            // Insert Main Menu Content.
            doc.insertString(doc.getLength(), "The main menu is your launchpad into PixelPal. Here’s what you’ll find:\n\n", defaultStyle);
            doc.insertString(doc.getLength(), "1. Start a New Game: Begin your adventure by selecting a brand-new pet. 🐶\n", defaultStyle);
            doc.insertString(doc.getLength(), "2. Load a Saved Game: Continue the journey with a pet from your previous sessions. 💾\n", defaultStyle);
            doc.insertString(doc.getLength(), "3. Tutorial/Instructions: This guide in action—everything you need to know. 📖\n", defaultStyle);
            doc.insertString(doc.getLength(), "4. Parental Controls: A dedicated area for setting playtime limits and monitoring game statistics (yep, even virtual pets need rules). 🔒\n", defaultStyle);
            doc.insertString(doc.getLength(), "5. Exit: Log off when you’re ready to step away. 🚪\n\n", defaultStyle);
            
            // Insert Separator.
            doc.insertString(doc.getLength(), "✨ ---\n\n", defaultStyle);
            
            // Insert Heading: Parental Controls.
            doc.insertString(doc.getLength(), "## 🔐 Parental Controls: Balancing Fun with Responsibility\n", headingStyle);
            
            // Insert Parental Controls Content.
            doc.insertString(doc.getLength(), "Even in a playful world, balance is key. The parental controls are a secure, password-protected section designed for those who appreciate order:\n\n", defaultStyle);
            doc.insertString(doc.getLength(), "- Set Play Times: Define specific periods when gameplay is allowed. ⏰\n", defaultStyle);
            doc.insertString(doc.getLength(), "- Monitor Statistics: Track total playtime and session averages, with an option to reset if needed. 📊\n", defaultStyle);
            doc.insertString(doc.getLength(), "- Revive Pet: Should your pet need a digital pick-me-up, restore it to full health and vitality with a simple command. 💉\n\n", defaultStyle);
            
            // Insert Separator.
            doc.insertString(doc.getLength(), "✨ ---\n\n", defaultStyle);
            
            // Insert Heading: New Game & Pet Selection.
            doc.insertString(doc.getLength(), "## 🐾 New Game & Pet Selection: Choose Your Champion\n", headingStyle);
            
            // Insert New Game Content.
            doc.insertString(doc.getLength(), "Starting a new game brings you to a selection screen showcasing at least five unique pet types. Each comes with its own image and a brief description to help you decide:\n\n", defaultStyle);
            doc.insertString(doc.getLength(), "- Pick Your Pet: Choose the companion that resonates with you. 🐱\n", defaultStyle);
            doc.insertString(doc.getLength(), "- Name Your Pet: Personalize your new friend with a fitting name. 📝\n", defaultStyle);
            doc.insertString(doc.getLength(), "- Start the Adventure: Once your pet is chosen and named, the main game screen awaits. 🚀\n\n", defaultStyle);
            
            // Insert Separator.
            doc.insertString(doc.getLength(), "✨ ---\n\n", defaultStyle);
            
            // Insert Heading: Main Game Screen & Save Functionality.
            doc.insertString(doc.getLength(), "## 🎮 Main Game Screen & Save Functionality\n", headingStyle);
            
            // Insert Main Game Screen Content.
            doc.insertString(doc.getLength(), "This is your in-game hub where the magic happens. Your pet’s state, inventory, and progress are all visible here. And remember—the save feature ensures that every quirky moment is preserved:\n\n", defaultStyle);
            doc.insertString(doc.getLength(), "- Save Game: Capture the current state of your pet, including vital stats, inventory, and your overall score. 💾\n", defaultStyle);
            doc.insertString(doc.getLength(), "- Load Game: Revisit your saved adventures exactly as you left them. 🔄\n\n", defaultStyle);
            
            // Insert Separator.
            doc.insertString(doc.getLength(), "✨ ---\n\n", defaultStyle);
            
            // Insert Heading: Activities.
            doc.insertString(doc.getLength(), "## 🎮 Activities: Mastering Pet Care\n\n", headingStyle);
            
            // Insert Activities Content.
            doc.insertString(doc.getLength(), "### 😴 Sleep\n", headingStyle);
            doc.insertString(doc.getLength(), "- Recharge Time: Click the sleep button to send your pet to bed. 🛏️\n", defaultStyle);
            doc.insertString(doc.getLength(), "- Cooldown Period: A short timer prevents consecutive sleep commands. ⏳\n", defaultStyle);
            doc.insertString(doc.getLength(), "- Health Boost: A well-rested pet enjoys improved vitality. 💪\n\n", defaultStyle);
            
            doc.insertString(doc.getLength(), "### 🍔 Feed\n", headingStyle);
            doc.insertString(doc.getLength(), "- Kitchen Adventures: Head to the kitchen to select from a variety of food items. 🍽️\n", defaultStyle);
            doc.insertString(doc.getLength(), "- Nutritious Choices: Different foods provide unique boosts to your pet’s health and come with varying point values. 🥗\n\n", defaultStyle);
            
            doc.insertString(doc.getLength(), "### 🎁 Gift\n", headingStyle);
            doc.insertString(doc.getLength(), "- Shop for Joy: Visit the shop room to pick out gifts. 🛍️\n", defaultStyle);
            doc.insertString(doc.getLength(), "- Happiness Boost: Gifts elevate your pet’s mood and add to your score. 😊\n\n", defaultStyle);
            
            doc.insertString(doc.getLength(), "### 👩‍⚕️ Vet\n", headingStyle);
            doc.insertString(doc.getLength(), "- Doctor’s Orders: When your pet’s health dips, click on the vet button. 🏥\n", defaultStyle);
            doc.insertString(doc.getLength(), "- Quick Fix: A visit to the vet restores health, though a cooldown period applies afterward. ⚕️\n\n", defaultStyle);
            
            doc.insertString(doc.getLength(), "### 🎉 Play\n", headingStyle);
            doc.insertString(doc.getLength(), "- Fun & Games: Engage in playful activities to lift your pet’s spirits. 🎊\n", defaultStyle);
            doc.insertString(doc.getLength(), "- Strategic Cooldown: Just like sleep, some activities require a brief waiting period before repeating. ⏲️\n\n", defaultStyle);
            
            doc.insertString(doc.getLength(), "### 🚶 Explore\n", headingStyle);
            doc.insertString(doc.getLength(), "- Take a Walk: Click the “Take for a Walk” button for a refreshing adventure. 🚶‍♂️\n", defaultStyle);
            doc.insertString(doc.getLength(), "- Balanced Impact: Walking boosts health but may temporarily affect sleepiness and hunger levels. 🌳\n\n", defaultStyle);
            
            doc.insertString(doc.getLength(), "### ⚔️ Battle\n", headingStyle);
            doc.insertString(doc.getLength(), "- Challenge Mode: Enter a fun math battle challenge where winning at least four rounds makes your pet proud. 🏆\n", defaultStyle);
            doc.insertString(doc.getLength(), "- Victory Matters: Each win boosts your pet’s hapinness by 15 points but if you lose it hurts your health bar by 20 points core. 🎖️\n\n", defaultStyle);
            
            // Insert Separator.
            doc.insertString(doc.getLength(), "✨ ---\n\n", defaultStyle);
            
            // Insert Heading: Pet Status.
            doc.insertString(doc.getLength(), "## 🐾 Pet Status: Monitoring the Essentials\n", headingStyle);
            doc.insertString(doc.getLength(), "Your game screen displays four key stats, each represented by numbers and progress bars:\n\n", defaultStyle);
            doc.insertString(doc.getLength(), "- Health: The lifeline of your pet—zero means game over. ❤️\n", defaultStyle);
            doc.insertString(doc.getLength(), "- Sleep: Reflects alertness; low levels mean it’s time for a nap. 😴\n", defaultStyle);
            doc.insertString(doc.getLength(), "- Fullness: Indicates hunger; a low value means your pet is starving. 🍽️\n", defaultStyle);
            doc.insertString(doc.getLength(), "- Happiness: Measures contentment; low happiness may lead to a grumpy pet. 🙂\n\n", defaultStyle);
            doc.insertString(doc.getLength(), "A warning will pop up if any stat falls below 25% of its maximum, ensuring you’re always in the loop. ⚠️\n\n", defaultStyle);
            
            // Insert Separator.
            doc.insertString(doc.getLength(), "✨ ---\n\n", defaultStyle);
            
            // Insert Heading: Pet States.
            doc.insertString(doc.getLength(), "## 🐾 Pet States: Understanding Your Pet’s Mood\n", headingStyle);
            doc.insertString(doc.getLength(), "Your pet’s current state influences which actions you can take:\n\n", defaultStyle);
            doc.insertString(doc.getLength(), "- Normal: All commands are at your disposal. 😊\n", defaultStyle);
            doc.insertString(doc.getLength(), "- Hungry: Feeding becomes the top priority. 🍔\n", defaultStyle);
            doc.insertString(doc.getLength(), "- Angry: Your pet might only accept gifts or play to cheer up. 😡\n", defaultStyle);
            doc.insertString(doc.getLength(), "- Sleeping: Interaction is paused until your pet wakes up. 😴\n", defaultStyle);
            doc.insertString(doc.getLength(), "- Dead: If health reaches zero, the only options are to start a new or load a previous save. 💀\n\n", defaultStyle);
            
            // Insert Separator.
            doc.insertString(doc.getLength(), "✨ ---\n\n", defaultStyle);
            
            // Insert Heading: Inventory.
            doc.insertString(doc.getLength(), "## 📦 Inventory: Your Digital Treasure Chest\n", headingStyle);
            doc.insertString(doc.getLength(), "Keep track of your supplies in the inventory, which includes:\n\n", defaultStyle);
            doc.insertString(doc.getLength(), "- Food Items: Essential for keeping hunger at bay. 🍕\n", defaultStyle);
            doc.insertString(doc.getLength(), "- Gift Items: Boost your pet’s happiness with a thoughtful present. 🎁\n", defaultStyle);
            doc.insertString(doc.getLength(), "- Item Counts: Monitor how many of each item you have—it’s your strategic resource. 📊\n\n", defaultStyle);
            
            // Insert Separator.
            doc.insertString(doc.getLength(), "✨ ---\n\n", defaultStyle);
            
            // Insert Heading: Keeping Score.
            doc.insertString(doc.getLength(), "## 🎯 Keeping Score: Every Action Counts\n", headingStyle);
            doc.insertString(doc.getLength(), "Every positive action, from feeding to playing, increases your score, while some choices (like a vet visit) might subtract a few points. Your score is a reflection of your pet’s well-being and your careful attention. 🏅\n\n", defaultStyle);
            doc.insertString(doc.getLength(), "PixelPal combines playful interaction with smart management, inviting you to care for your digital companion with both humor and precision. Enjoy every moment of nurturing your pixelated friend, and may your journey be as delightful as it is strategic! 🎉", defaultStyle);
            

            // Initialize all buttons
            UIUtility.button_fontsize = 13f;
            UIUtility.button_width = 260;
            UIUtility.button_height = 35;
            mainmenuButton = new UIUtility.PixelatedButton("Return to Main Menu");

            mainmenuButton.addActionListener(e -> {
                new MainMenuScreen().setVisible(true);
                dispose();
            });
            // Create a panel to hold the button at the bottom
            JPanel buttonPanel = new JPanel();
            buttonPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
            buttonPanel.add(mainmenuButton);

            setLayout(new BorderLayout());

            // Wrap the text pane in a scroll pane.
            JScrollPane scrollPane = new JScrollPane(textPane);
            scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
            scrollPane.setBorder(null);
            scrollPane.getViewport().setBackground(lightBlue);
            
            add(scrollPane);
            add(buttonPanel, BorderLayout.SOUTH);

        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    /**
     * Main method for standalone testing of the TutorialScreen.
     * 
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            TutorialScreen frame = new TutorialScreen();
            frame.setVisible(true);
        });
    }
}
