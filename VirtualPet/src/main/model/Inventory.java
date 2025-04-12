package group33.VirtualPet.src.main.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents an inventory system for a virtual pet game.
 * Manages two types of items: Food (restores pet's fullness) and Gifts (boosts pet's happiness).
 * Tracks quantities of each item and provides methods for adding/using items.
 *
 * @author Team 33 (Dhir, Kostya, Fatima, Anna)
 * @since Winter 2025 
 *
 */
public class Inventory {
    // Separate maps for food and gift items
    private Map<FoodItem, Integer> foodItems;  // Maps food items to their quantities
    private Map<GiftItem, Integer> giftItems;  // Maps gift items to their quantities
    
    /**
     * Represents a food item that can be fed to pets to increase their fullness.
     * Each item has a name and a fullness restoration value.
     */
    public static class FoodItem {
        private String name;          // Name of the food item
        private int fullnessValue;    // How much fullness this item restores
        
        /**
         * Creates a new FoodItem.
         * @param name The display name of the food item
         * @param fullnessValue How much fullness this item restores when used
         */
        public FoodItem(String name, int fullnessValue) {
            this.name = name;
            this.fullnessValue = fullnessValue;
        }
        
        /**
         * @return The name of this food item
         */
        public String getName() { return name; }
        
        /**
         * @return The amount of fullness this item restores
         */
        public int getFullnessValue() { return fullnessValue; }
        
        /**
         * Compares this FoodItem to another object for equality.
         * @param obj The object to compare with
         * @return True if objects are equal FoodItems with same name and value
         */
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            FoodItem other = (FoodItem) obj;
            return name.equals(other.name) && fullnessValue == other.fullnessValue;
        }
        
        /**
         * Generates a hash code for this FoodItem.
         * @return A hash code based on name and fullnessValue
         */
        @Override
        public int hashCode() {
            return 31 * name.hashCode() + fullnessValue;
        }
    }
    
    /**
     * Represents a gift item that can be given to pets to increase their happiness.
     * Each item has a name and a happiness boost value.
     */
    public static class GiftItem {
        private String name;          // Name of the gift item
        private int happinessValue;   // How much happiness this item provides
        
        /**
         * Creates a new GiftItem.
         * @param name The display name of the gift item
         * @param happinessValue How much happiness this item provides when used
         */
        public GiftItem(String name, int happinessValue) {
            this.name = name;
            this.happinessValue = happinessValue;
        }
        
        /**
         * @return The name of this gift item
         */
        public String getName() { return name; }
        
        /**
         * @return The amount of happiness this item provides
         */
        public int getHappinessValue() { return happinessValue; }
        
        /**
         * Compares this GiftItem to another object for equality.
         * @param obj The object to compare with
         * @return True if objects are equal GiftItems with same name and value
         */
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            GiftItem other = (GiftItem) obj;
            return name.equals(other.name) && happinessValue == other.happinessValue;
        }
        
        /**
         * Generates a hash code for this GiftItem.
         * @return A hash code based on name and happinessValue
         */
        @Override
        public int hashCode() {
            return 31 * name.hashCode() + happinessValue;
        }
    }
    
    /**
     * Creates a new Inventory with default items.
     * Initializes with:
     * - 4 Apples, 3 Smoothies, 2 Tacos, 1 Ramen (food)
     * - 4 Balls, 3 Cards, 2 Flowers, 1 Hat (gifts)
     */
    public Inventory() {
        foodItems = new HashMap<>();
        giftItems = new HashMap<>();
        
        // Initialize with some default items
        initializeDefaultItems();
    }
    
    /**
     * Populates the inventory with default items and quantities.
     * Called automatically during construction.
     */
    private void initializeDefaultItems() {
        // Default food items
        foodItems.put(new FoodItem("Apple", 5), 4);
        foodItems.put(new FoodItem("Smoothie", 10), 3);
        foodItems.put(new FoodItem("Taco", 20), 2);
        foodItems.put(new FoodItem("Ramen", 30), 1);
        
        // Default gift items
        giftItems.put(new GiftItem("Ball", 5), 4);
        giftItems.put(new GiftItem("Cards", 10), 3);
        giftItems.put(new GiftItem("Flowers", 20), 2);
        giftItems.put(new GiftItem("Hat", 30), 1);
    }
    
    /**
     * Adds food items to the inventory.
     * If the item already exists, increases its quantity.
     * @param item The food item to add
     * @param quantity How many of this item to add
     */
    public void addFoodItem(FoodItem item, int quantity) {
        Integer currentQuantity = foodItems.getOrDefault(item, 0);
        // Check for potential overflow and cap at Integer.MAX_VALUE
        if (quantity > 0 && currentQuantity > Integer.MAX_VALUE - quantity) {
            foodItems.put(item, Integer.MAX_VALUE);
        } else {
            foodItems.merge(item, quantity, Integer::sum);
        }
    }
    /**
     * Adds gift items to the inventory.
     * If the item already exists, increases its quantity.
     * @param item The gift item to add
     * @param quantity How many of this item to add
     */
    public void addGiftItem(GiftItem item, int quantity) {
        Integer currentQuantity = giftItems.getOrDefault(item, 0);
        // Check for potential overflow and cap at Integer.MAX_VALUE
        if (quantity > 0 && currentQuantity > Integer.MAX_VALUE - quantity) {
            giftItems.put(item, Integer.MAX_VALUE);
        } else {
            giftItems.merge(item, quantity, Integer::sum);
        }
    }
    /**
     * Attempts to use one food item from inventory.
     * @param item The food item to use
     * @return True if item was available and used, false otherwise
     */
    public boolean useFoodItem(FoodItem item) {
        return useItem(foodItems, item);
    }
    
    /**
     * Attempts to use one gift item from inventory.
     * @param item The gift item to use
     * @return True if item was available and used, false otherwise
     */
    public boolean useGiftItem(GiftItem item) {
        return useItem(giftItems, item);
    }
    
    /**
     * Helper method to use an item from a specified inventory map.
     * @param <T> The type of item (FoodItem or GiftItem)
     * @param itemMap The inventory map to use the item from
     * @param item The item to use
     * @return True if item was available and used, false otherwise
     */
    private <T> boolean useItem(Map<T, Integer> itemMap, T item) {
        Integer currentQuantity = itemMap.getOrDefault(item, 0);
        if (currentQuantity > 0) {
            itemMap.put(item, currentQuantity - 1);
            return true;
        }
        return false;
    }
    
    /**
     * Gets a copy of all food items in inventory and their quantities.
     * @return A new Map containing all food items and their counts
     */
    public Map<FoodItem, Integer> getFoodItems() {
        return new HashMap<>(foodItems);
    }
    
    /**
     * Gets a copy of all gift items in inventory and their quantities.
     * @return A new Map containing all gift items and their counts
     */
    public Map<GiftItem, Integer> getGiftItems() {
        return new HashMap<>(giftItems);
    }
}