package group33.VirtualPet.src.main.ui;

import group33.VirtualPet.src.main.model.Inventory;
import group33.VirtualPet.src.main.model.Pet;
import group33.VirtualPet.src.main.model.Player;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Map;
import java.util.HashMap;

/**
 * A dialog window that displays and manages the player's inventory items (food or gifts).
 * Allows the player to view available items and use them on their pet.
 *
 * @author Team 33 (Dhir, Kostya, Fatima, Anna)
 * @since Winter 2025 
 *
 */
public class InventoryDialog extends JDialog {
    private Pet pet;                      // The pet that items will be used on
    private Inventory inventory;          // The inventory to display
    private JTable itemTable;             // Table displaying inventory items
    private JButton useItemButton;        // Button to use selected item
    private boolean isFoodInventory;      // Flag indicating if this is food inventory (true) or gift inventory (false)
    private HashMap<String, ImageIcon> itemImages;  // Map of item names to their icons
    private Player player;                // Reference to the player object

    /**
     * Constructs an InventoryDialog.
     *
     * @param parent          The parent JFrame for this dialog
     * @param pet            The pet that items will be used on
     * @param inventory      The inventory to display
     * @param isFoodInventory True for food inventory, false for gift inventory
     * @param player         The player who owns the inventory
     */
    public InventoryDialog(JFrame parent, Pet pet, Inventory inventory, boolean isFoodInventory, Player player) {
        super(parent, isFoodInventory ? "Food Inventory" : "Gift Inventory", true);
        this.pet = pet;
        this.inventory = inventory;
        this.isFoodInventory = isFoodInventory;
        this.itemImages = new HashMap<>();
        this.player = player;
        
        loadItemImages();
        setSize(400, 300);
        setLocationRelativeTo(parent);
        
        initComponents();
    }
    
    /**
     * Loads images for all inventory items based on inventory type.
     * Images are scaled to 24x24 pixels and stored in itemImages map.
     */
    private void loadItemImages() {
        // Load food item images
        if (isFoodInventory) {
            loadImage("Apple", "group33/VirtualPet/assets/images/food/apple.png");
            loadImage("Ramen", "group33/VirtualPet/assets/images/food/ramen.png");
            loadImage("Smoothie", "group33/VirtualPet/assets/images/food/smoothie.png");
            loadImage("Taco", "group33/VirtualPet/assets/images/food/taco.png");
        } 
        // Load gift item images
        else {
            loadImage("Ball", "group33/VirtualPet/assets/images/gifts/ball.png");
            loadImage("Cards", "group33/VirtualPet/assets/images/gifts/cards.png");
            loadImage("Flowers", "group33/VirtualPet/assets/images/gifts/flowers.png");
            loadImage("Hat", "group33/VirtualPet/assets/images/gifts/hat.png");
        }
    }
    
    /**
     * Loads and scales an image for an inventory item.
     *
     * @param itemName The name of the item (key for the image map)
     * @param path     The file path to the image
     */
    private void loadImage(String itemName, String path) {
        try {
            ImageIcon originalIcon = new ImageIcon(path);
            Image scaledImage = originalIcon.getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH);
            itemImages.put(itemName, new ImageIcon(scaledImage));
        } catch (Exception e) {
            System.err.println("Error loading image for " + itemName + ": " + e.getMessage());
        }
    }

    /**
     * Initializes the dialog components including:
     * - Table for displaying inventory items
     * - Scroll pane for the table
     * - "Use Item" button
     */
    private void initComponents() {
        setLayout(new BorderLayout());

        // Create table model with non-editable cells
        String[] columnNames = {"Item", "Quantity", "Effect"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Populate table based on inventory type
        if (isFoodInventory) {
            populateFoodItems(model);
        } else {
            populateGiftItems(model);
        }

        // Configure table appearance and behavior
        itemTable = new JTable(model);
        itemTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        itemTable.setRowHeight(32); // Increased row height to fit images
        
        // Set custom renderer for the item column to show images
        itemTable.getColumnModel().getColumn(0).setCellRenderer(new ItemCellRenderer());
        
        JScrollPane scrollPane = new JScrollPane(itemTable);
        add(scrollPane, BorderLayout.CENTER);

        // Configure and add "Use Item" button
        useItemButton = new JButton("Use Item");
        useItemButton.addActionListener(e -> useSelectedItem());
        add(useItemButton, BorderLayout.SOUTH);
    }
    
    /**
     * Custom cell renderer that displays item images alongside their names.
     */
    class ItemCellRenderer extends DefaultTableCellRenderer {
        /**
         * Returns a component configured to display the cell's value with appropriate icon.
         */
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                    boolean isSelected, boolean hasFocus,
                                                    int row, int column) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column);
            
            String itemName = (String) value;
            if (itemImages.containsKey(itemName)) {
                label.setIcon(itemImages.get(itemName));
                label.setHorizontalTextPosition(JLabel.RIGHT);
                label.setIconTextGap(10);
            } else {
                label.setIcon(null);
            }
            
            return label;
        }
    }

    /**
     * Populates the table model with food items from inventory.
     *
     * @param model The table model to populate
     */
    private void populateFoodItems(DefaultTableModel model) {
        for (Map.Entry<Inventory.FoodItem, Integer> entry : inventory.getFoodItems().entrySet()) {
            Inventory.FoodItem item = entry.getKey();
            Integer quantity = entry.getValue();
            
            model.addRow(new Object[]{
                item.getName(), 
                quantity, 
                "Fullness: +" + item.getFullnessValue()
            });
        }
    }

    /**
     * Populates the table model with gift items from inventory.
     *
     * @param model The table model to populate
     */
    private void populateGiftItems(DefaultTableModel model) {
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

    /**
     * Attempts to use the currently selected item on the pet.
     * Shows appropriate messages if no item is selected, inventory is empty,
     * or if the item is successfully used.
     */
    private void useSelectedItem() {
        int selectedRow = itemTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Please select an item to use.", 
                "No Item Selected", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        String itemName = (String) itemTable.getValueAt(selectedRow, 0);
        int quantity = (int) itemTable.getValueAt(selectedRow, 1);

        if (quantity <= 0) {
            JOptionPane.showMessageDialog(this, 
                "No more items left.", 
                "Empty Inventory", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            boolean used = false;
            if (isFoodInventory) {
                Inventory.FoodItem foodItem = findFoodItem(itemName);
                if (foodItem != null) {
                    used = inventory.useFoodItem(foodItem);
                    if (used) {
                        pet.feed(foodItem.getFullnessValue());
                        player.setScore(player.getScore() + 10);
                    }
                }
            } else {
                Inventory.GiftItem giftItem = findGiftItem(itemName);
                if (giftItem != null) {
                    used = inventory.useGiftItem(giftItem);
                    if (used) {
                        pet.giveGift(giftItem.getHappinessValue());
                        player.setScore(player.getScore() + 20);
                    }
                }
            }

            if (used) {
                // Refresh table to show updated quantities
                DefaultTableModel model = (DefaultTableModel) itemTable.getModel();
                model.setRowCount(0);
                if (isFoodInventory) {
                    populateFoodItems(model);
                } else {
                    populateGiftItems(model);
                }

                // Show success message
                String effectType = isFoodInventory ? "Fullness" : "Happiness";
                JOptionPane.showMessageDialog(this, 
                    itemName + " used. " + effectType + " increased!", 
                    "Item Used", 
                    JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error using item: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Finds a food item by name in the inventory.
     *
     * @param name The name of the food item to find
     * @return The matching FoodItem, or null if not found
     */
    private Inventory.FoodItem findFoodItem(String name) {
        for (Inventory.FoodItem item : inventory.getFoodItems().keySet()) {
            if (item.getName().equals(name)) {
                return item;
            }
        }
        return null;
    }

    /**
     * Finds a gift item by name in the inventory.
     *
     * @param name The name of the gift item to find
     * @return The matching GiftItem, or null if not found
     */
    private Inventory.GiftItem findGiftItem(String name) {
        for (Inventory.GiftItem item : inventory.getGiftItems().keySet()) {
            if (item.getName().equals(name)) {
                return item;
            }
        }
        return null;
    }
}