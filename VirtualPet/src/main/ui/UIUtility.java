package group33.VirtualPet.src.main.ui;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;



/**
 * This utility class provides UI components and styling for the Virtual Pet game.
 * <p>
 * This class offers pre-styled UI elements with a consistent pixelated theme, including:
 * <ul>
 *   <li>Custom pixelated labels with specialized fonts</li>
 *   <li>Pixel-styled buttons with hover effects</li>
 *   <li>Image background panels</li>
 *   <li>Color-coded progress bars for status display</li>
 *   <li>Pre-configured stat panels combining labels and progress bars</li>
 * </ul>
 * <p>
 * All components are designed to maintain visual consistency across the application
 * and automatically handle resource loading from the assets folder.
 * 
 * @author Team 33 (Dhir, Kostya, Fatima, Anna)
 * @since Winter 2025
 * 
 */
public class UIUtility {
    public static float button_fontsize = 13;
    public static int button_width = 180;
    public static int button_height = 40;

    /**
     * Creates a pixelated label with custom styling
     * @param text Label text
     * @param fontSize Font size
     * @return Styled JLabel
     */
    public static JLabel createPixelatedLabel(String text, float fontSize) {
        return createPixelatedLabel(text, fontSize, Color.BLACK);
    }

    /**
     * Creates a pixelated label with custom styling and color
     * @param text Label text
     * @param fontSize Font size
     * @param color Text color
     * @return Styled JLabel
     */
    public static JLabel createPixelatedLabel(String text, float fontSize, Color color) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        
        try {
            Font pixelatedFont = createPixelatedFont().deriveFont(fontSize);
            label.setFont(pixelatedFont);
        } catch (Exception e) {
            label.setFont(new Font("Monospaced", Font.BOLD, (int)fontSize));
        }
    
        label.setForeground(color);
        label.setOpaque(false);
        label.setBackground(new Color(0, 0, 0, 0));
        label.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    
        return label;
    }

    /**
     * Creates a pixelated font
     * @return Pixelated Font
     * @throws Exception If font loading fails
     */
    public static Font createPixelatedFont() throws Exception {
        try (InputStream is = new FileInputStream(new File("group33/VirtualPet/assets/fonts/PressStart2P-Regular.ttf"))) {
            Font font = Font.createFont(Font.TRUETYPE_FONT, is);
            return font.deriveFont(Font.PLAIN, 24f);
        } catch (Exception e) {
            System.err.println("Error loading font: " + e.getMessage());
            e.printStackTrace();
            throw new Exception("Failed to load pixelated font.");
        }
    }

    /**
     * Custom Pixelated Button with hover effects
     */
    public static class PixelatedButton extends JButton {
        private boolean isHovered = false;
        
        public PixelatedButton(String text) {
            //this(text, new Dimension(225, 35));
            this(text, new Dimension(button_width, button_height));
        }

        public PixelatedButton(String text, Dimension size) {
            super(text);
            
            // Set transparent button properties
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setPreferredSize(size);
            
            // Add custom mouse listener for hover effects
            addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseEntered(java.awt.event.MouseEvent e) {
                    isHovered = true;
                    repaint();
                }
                
                @Override
                public void mouseExited(java.awt.event.MouseEvent e) {
                    isHovered = false;
                    repaint();
                }
            });
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
            
            // Draw off-white background box
            g2d.setColor(new Color(240, 240, 240));
            g2d.fillRect(0, 0, getWidth(), getHeight());
            
            // Draw black border with hover effect
            g2d.setColor(Color.BLACK);
            int borderWidth = isHovered ? 3 : 2;
            g2d.setStroke(new BasicStroke(borderWidth));
            
            // Adjust border opacity on hover
            if (isHovered) {
                g2d.setColor(new Color(0, 0, 0, 180));
            }
            g2d.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
            
            // Draw pixelated text
            try {
                Font pixelatedFont = createPixelatedFont();
                Font font = pixelatedFont.deriveFont(isHovered ? Font.BOLD : Font.PLAIN, button_fontsize);
                g2d.setFont(font);
            } catch (Exception e) {
                g2d.setFont(new Font("Monospaced", isHovered ? Font.BOLD : Font.PLAIN, 16));
            }
            
            g2d.setColor(Color.BLACK);
            FontMetrics fm = g2d.getFontMetrics();
            String text = getText();
            int x = (getWidth() - fm.stringWidth(text)) / 2;
            int y = (getHeight() + fm.getHeight()) / 2 - fm.getDescent();
            g2d.drawString(text, x, y);
            
            g2d.dispose();
        }

    }

    public static class BorderedLabel extends JLabel {
        private static final int OUTLINE_THICKNESS = 2; // Thickness of the border
    
        public BorderedLabel(String text, float fontSize, Color textColor) {
            super(text, SwingConstants.CENTER);
            setForeground(textColor);
            setOpaque(false);
    
            try {
                Font pixelatedFont = UIUtility.createPixelatedFont().deriveFont(fontSize);
                setFont(pixelatedFont);
            } catch (Exception e) {
                setFont(new Font("Monospaced", Font.BOLD, (int) fontSize));
            }
        }
    
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            String text = getText();
            FontMetrics fm = g2d.getFontMetrics();
            int x = (getWidth() - fm.stringWidth(text)) / 2;
            int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
    
            // Draw black border (outline)
            g2d.setColor(Color.BLACK);
            for (int dx = -OUTLINE_THICKNESS; dx <= OUTLINE_THICKNESS; dx++) {
                for (int dy = -OUTLINE_THICKNESS; dy <= OUTLINE_THICKNESS; dy++) {
                    if (dx != 0 || dy != 0) { // Avoid drawing over the main text
                        g2d.drawString(text, x + dx, y + dy);
                    }
                }
            }
    
            // Draw white text
            g2d.setColor(getForeground());
            g2d.drawString(text, x, y);
            
            g2d.dispose();
        }
    }
    public static JLabel createPixelatedLabel2(String text, float fontSize, Color color) {
        return new BorderedLabel(text, fontSize, color);
    }
    
    /**
     * Creates a background panel with a specified image
     */
    public static class BackgroundPanel extends JPanel {
        private Image backgroundImage;
        
        public BackgroundPanel(String imagePath) {
            try {
                // Load background image from assets folder
                backgroundImage = new ImageIcon(imagePath).getImage();
            } catch (Exception e) {
                System.err.println("Could not load background image: " + e.getMessage());
                // Fallback to a default background color if image loading fails
                setBackground(new Color(135, 206, 235)); // Sky blue
            }
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (backgroundImage != null) {
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
        }
    }

    /**
     * Creates a progress bar with color coded status
     * @param value Current value of the progress bar
     * @return Configured JProgressBar
     */
    public static JProgressBar createStatProgressBar(int value) {
        JProgressBar progressBar = new JProgressBar(0, 100);
        progressBar.setValue(value);
        progressBar.setStringPainted(true);
        progressBar.setString(value + "");
        progressBar.setPreferredSize(new Dimension(100, 20));
        
        // Use a more consistent color scheme
        progressBar.setForeground(getColorForProgressBar(value));
        progressBar.setBackground(new Color(220, 220, 220));
        
        return progressBar;
    }

    /**
     * Determines the color for progress bars based on value
     * @param value Current value
     * @return Color representing the status
     */
    public static Color getColorForProgressBar(int value) {
        if (value > 75) return new Color(0, 200, 0);   // Green
        else if (value > 50) return new Color(255, 165, 0);  // Orange
        else if (value > 25) return new Color(255, 69, 0);  // Dark Orange
        else return new Color(200, 0, 0);  // Red
    }

    /**
     * Creates a panel with a label and progress bar
     * @param statLabel Label for the stat
     * @param progressBar Progress bar for the stat
     * @return JPanel containing the label and progress bar
     */
    public static JPanel createStatPanel(JLabel statLabel, JProgressBar progressBar) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
    
        // Center the label above the progress bar
        statLabel.setHorizontalAlignment(JLabel.CENTER);
        panel.add(statLabel, BorderLayout.NORTH);
        panel.add(progressBar, BorderLayout.CENTER);
    
        return panel;
    }
}