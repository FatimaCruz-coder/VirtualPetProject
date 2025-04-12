package group33.VirtualPet.src.test.game;

import group33.VirtualPet.src.main.model.Inventory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for Inventory functionality.
 * Tests various scenarios and methods in the Inventory class to make sure it is implemented properly.
 * 
 * @author Team 33
 */
class InventoryTest {
    
    private Inventory inventory;
    private Inventory.FoodItem apple;
    private Inventory.FoodItem banana;
    private Inventory.GiftItem ball;
    private Inventory.GiftItem car;
    
    @BeforeEach
    void setUp() {
        inventory = new Inventory();
        apple = new Inventory.FoodItem("Apple", 5);
        banana = new Inventory.FoodItem("Banana", 8);
        ball = new Inventory.GiftItem("Ball", 5);
        car = new Inventory.GiftItem("Car", 15);
    }
    
    @AfterEach
    void tearDown() {
        inventory = null;
        apple = null;
        banana = null;
        ball = null;
        car = null;
    }
    
    @Test
    void testConstructor() {
        // Test that constructor initializes with default items
        inventory = new Inventory();
        
        // Make sure default food items exist with correct amounts
        Map<Inventory.FoodItem, Integer> foodItems = inventory.getFoodItems();
        boolean hasApples = false;
        boolean hasSmoothies = false;
        boolean hasTacos = false;
        boolean hasRamen = false;
        
        for (Map.Entry<Inventory.FoodItem, Integer> entry : foodItems.entrySet()) {
            Inventory.FoodItem item = entry.getKey();
            Integer quantity = entry.getValue();
            
            if (item.getName().equals("Apple") && item.getFullnessValue() == 5 && quantity == 4) {
                hasApples = true;
            } else if (item.getName().equals("Smoothie") && item.getFullnessValue() == 10 && quantity == 3) {
                hasSmoothies = true;
            } else if (item.getName().equals("Taco") && item.getFullnessValue() == 20 && quantity == 2) {
                hasTacos = true;
            } else if (item.getName().equals("Ramen") && item.getFullnessValue() == 30 && quantity == 1) {
                hasRamen = true;
            }
        }
        
        assertTrue(hasApples, "Default Apple items not found or incorrect");
        assertTrue(hasSmoothies, "Default Smoothie items not found or incorrect");
        assertTrue(hasTacos, "Default Taco items not found or incorrect");
        assertTrue(hasRamen, "Default Ramen items not found or incorrect");
        
        // Mkae sure the default gift items exist with correct quantities
        Map<Inventory.GiftItem, Integer> giftItems = inventory.getGiftItems();
        boolean hasBalls = false;
        boolean hasCards = false;
        boolean hasFlowers = false;
        boolean hasHat = false;
        
        for (Map.Entry<Inventory.GiftItem, Integer> entry : giftItems.entrySet()) {
            Inventory.GiftItem item = entry.getKey();
            Integer quantity = entry.getValue();
            
            if (item.getName().equals("Ball") && item.getHappinessValue() == 5 && quantity == 4) {
                hasBalls = true;
            } else if (item.getName().equals("Cards") && item.getHappinessValue() == 10 && quantity == 3) {
                hasCards = true;
            } else if (item.getName().equals("Flowers") && item.getHappinessValue() == 20 && quantity == 2) {
                hasFlowers = true;
            } else if (item.getName().equals("Hat") && item.getHappinessValue() == 30 && quantity == 1) {
                hasHat = true;
            }
        }
        
        assertTrue(hasBalls, "Default Ball items not found or incorrect");
        assertTrue(hasCards, "Default Cards items not found or incorrect");
        assertTrue(hasFlowers, "Default Flowers items not found or incorrect");
        assertTrue(hasHat, "Default Hat items not found or incorrect");
    }
    
    @Test
    void testAddFoodItem() {
        // Add food item that already exists in inventory
        Map<Inventory.FoodItem, Integer> initialFood = inventory.getFoodItems();
        int initialAppleCount = 0;
        
        for (Map.Entry<Inventory.FoodItem, Integer> entry : initialFood.entrySet()) {
            if (entry.getKey().getName().equals("Apple") && entry.getKey().getFullnessValue() == 5) {
                initialAppleCount = entry.getValue();
                break;
            }
        }
        
        inventory.addFoodItem(apple, 5);
        
        Map<Inventory.FoodItem, Integer> updatedFood = inventory.getFoodItems();
        int updatedAppleCount = 0;
        
        for (Map.Entry<Inventory.FoodItem, Integer> entry : updatedFood.entrySet()) {
            if (entry.getKey().getName().equals("Apple") && entry.getKey().getFullnessValue() == 5) {
                updatedAppleCount = entry.getValue();
                break;
            }
        }
        
        assertEquals(initialAppleCount + 5, updatedAppleCount, "Apple count should be increased by 5");
    }
    
    @Test
    void testAddNewFoodItem() {
        // Add completely new food item
        assertFalse(containsFoodItem(inventory.getFoodItems(), banana));
        
        inventory.addFoodItem(banana, 3);
        
        assertTrue(containsFoodItem(inventory.getFoodItems(), banana));
        assertEquals(3, getFoodItemQuantity(inventory.getFoodItems(), banana));
    }
    
    @Test
    void testAddFoodItemWithZeroQuantity() {
        int initialSize = inventory.getFoodItems().size();
        inventory.addFoodItem(banana, 0);
        
        assertTrue(containsFoodItem(inventory.getFoodItems(), banana));
        assertEquals(0, getFoodItemQuantity(inventory.getFoodItems(), banana));
        assertEquals(initialSize + 1, inventory.getFoodItems().size());
    }
    
    @Test
    void testAddFoodItemWithNegativeQuantity() {
        int initialSize = inventory.getFoodItems().size();
        inventory.addFoodItem(banana, -5);
        
        // Implementation might handle negatives differently, but most likely:
        assertTrue(containsFoodItem(inventory.getFoodItems(), banana));
        assertEquals(-5, getFoodItemQuantity(inventory.getFoodItems(), banana));
        assertEquals(initialSize + 1, inventory.getFoodItems().size());
    }
    
    @Test
    void testAddGiftItem() {
        // Add gift item that already exists in inventory
        Map<Inventory.GiftItem, Integer> initialGifts = inventory.getGiftItems();
        int initialBallCount = 0;
        
        for (Map.Entry<Inventory.GiftItem, Integer> entry : initialGifts.entrySet()) {
            if (entry.getKey().getName().equals("Ball") && entry.getKey().getHappinessValue() == 5) {
                initialBallCount = entry.getValue();
                break;
            }
        }
        
        inventory.addGiftItem(ball, 5);
        
        Map<Inventory.GiftItem, Integer> updatedGifts = inventory.getGiftItems();
        int updatedBallCount = 0;
        
        for (Map.Entry<Inventory.GiftItem, Integer> entry : updatedGifts.entrySet()) {
            if (entry.getKey().getName().equals("Ball") && entry.getKey().getHappinessValue() == 5) {
                updatedBallCount = entry.getValue();
                break;
            }
        }
        
        assertEquals(initialBallCount + 5, updatedBallCount, "Ball count should be increased by 5");
    }
    
    @Test
    void testAddNewGiftItem() {
        // Add completely new gift item
        assertFalse(containsGiftItem(inventory.getGiftItems(), car));
        
        inventory.addGiftItem(car, 3);
        
        assertTrue(containsGiftItem(inventory.getGiftItems(), car));
        assertEquals(3, getGiftItemQuantity(inventory.getGiftItems(), car));
    }
    
    @Test
    void testAddGiftItemWithZeroQuantity() {
        int initialSize = inventory.getGiftItems().size();
        inventory.addGiftItem(car, 0);
        
        assertTrue(containsGiftItem(inventory.getGiftItems(), car));
        assertEquals(0, getGiftItemQuantity(inventory.getGiftItems(), car));
        assertEquals(initialSize + 1, inventory.getGiftItems().size());
    }
    
    @Test
    void testAddGiftItemWithNegativeQuantity() {
        int initialSize = inventory.getGiftItems().size();
        inventory.addGiftItem(car, -5);
        
        // Implementation might handle negatives differently, but most likely:
        assertTrue(containsGiftItem(inventory.getGiftItems(), car));
        assertEquals(-5, getGiftItemQuantity(inventory.getGiftItems(), car));
        assertEquals(initialSize + 1, inventory.getGiftItems().size());
    }
    
    @Test
    void testUseFoodItem() {
        // Use existing food item
        int initialCount = getFoodItemQuantity(inventory.getFoodItems(), apple);
        assertTrue(initialCount > 0, "Test requires at least one apple in inventory");
        
        boolean wasUsed = inventory.useFoodItem(apple);
        
        assertTrue(wasUsed, "Should return true when item was successfully used");
        assertEquals(initialCount - 1, getFoodItemQuantity(inventory.getFoodItems(), apple), 
                "Count should decrease by 1 after using item");
    }
    
    @Test
    void testUseFoodItemNotInInventory() {
        // Try to use a non-existent food item
        assertFalse(containsFoodItem(inventory.getFoodItems(), banana), "Test requires banana to not be in inventory");
        
        boolean wasUsed = inventory.useFoodItem(banana);
        
        assertFalse(wasUsed, "Should return false when item doesn't exist");
    }
    
    @Test
    void testUseFoodItemWithZeroQuantity() {
        // Add item with zero quantity then try to use it
        inventory.addFoodItem(banana, 0);
        assertTrue(containsFoodItem(inventory.getFoodItems(), banana), "Banana should be in inventory");
        assertEquals(0, getFoodItemQuantity(inventory.getFoodItems(), banana), "Should have zero bananas");
        
        boolean wasUsed = inventory.useFoodItem(banana);
        
        assertFalse(wasUsed, "Should return false when item quantity is zero");
    }
    
    @Test
    void testUseLastFoodItem() {
        // Use the last remaining item of a type
        Inventory.FoodItem ramen = null;
        for (Inventory.FoodItem item : inventory.getFoodItems().keySet()) {
            if (item.getName().equals("Ramen")) {
                ramen = item;
                break;
            }
        }
        
        assertNotNull(ramen, "Ramen should exist in default inventory");
        assertEquals(1, getFoodItemQuantity(inventory.getFoodItems(), ramen), "Should have exactly 1 ramen");
        
        boolean wasUsed = inventory.useFoodItem(ramen);
        
        assertTrue(wasUsed, "Should return true when last item was used");
        assertEquals(0, getFoodItemQuantity(inventory.getFoodItems(), ramen), "Should have 0 ramen after using");
    }
    
    @Test
    void testUseGiftItem() {
        // Use existing gift item
        int initialCount = getGiftItemQuantity(inventory.getGiftItems(), ball);
        assertTrue(initialCount > 0, "Test requires at least one ball in inventory");
        
        boolean wasUsed = inventory.useGiftItem(ball);
        
        assertTrue(wasUsed, "Should return true when item was successfully used");
        assertEquals(initialCount - 1, getGiftItemQuantity(inventory.getGiftItems(), ball), 
                "Count should decrease by 1 after using item");
    }
    
    @Test
    void testUseGiftItemNotInInventory() {
        // Try to use a non-existent gift item
        assertFalse(containsGiftItem(inventory.getGiftItems(), car), "Test requires car to not be in inventory");
        
        boolean wasUsed = inventory.useGiftItem(car);
        
        assertFalse(wasUsed, "Should return false when item doesn't exist");
    }
    
    @Test
    void testUseGiftItemWithZeroQuantity() {
        // Add item with zero quantity then try to use it
        inventory.addGiftItem(car, 0);
        assertTrue(containsGiftItem(inventory.getGiftItems(), car), "Car should be in inventory");
        assertEquals(0, getGiftItemQuantity(inventory.getGiftItems(), car), "Should have zero cars");
        
        boolean wasUsed = inventory.useGiftItem(car);
        
        assertFalse(wasUsed, "Should return false when item quantity is zero");
    }
    
    @Test
    void testUseLastGiftItem() {
        // Use the last remaining item of a type
        Inventory.GiftItem hat = null;
        for (Inventory.GiftItem item : inventory.getGiftItems().keySet()) {
            if (item.getName().equals("Hat")) {
                hat = item;
                break;
            }
        }
        
        assertNotNull(hat, "Hat should exist in default inventory");
        assertEquals(1, getGiftItemQuantity(inventory.getGiftItems(), hat), "Should have exactly 1 hat");
        
        boolean wasUsed = inventory.useGiftItem(hat);
        
        assertTrue(wasUsed, "Should return true when last item was used");
        assertEquals(0, getGiftItemQuantity(inventory.getGiftItems(), hat), "Should have 0 hats after using");
    }
    
    @Test
    void testGetFoodItemsReturnsCopy() {
        // Verify that getFoodItems returns a copy, not the original map
        Map<Inventory.FoodItem, Integer> foodItems1 = inventory.getFoodItems();
        Map<Inventory.FoodItem, Integer> foodItems2 = inventory.getFoodItems();
        
        assertNotSame(foodItems1, foodItems2, "Should return different map instances");
        
        // Modifying the returned map should not affect the inventory
        Inventory.FoodItem newItem = new Inventory.FoodItem("TestFood", 50);
        foodItems1.put(newItem, 10);
        
        assertFalse(containsFoodItem(inventory.getFoodItems(), newItem), 
                "Inventory should not be affected by changes to returned map");
    }
    
    @Test
    void testGetGiftItemsReturnsCopy() {
        // Verify that getGiftItems returns a copy, not the original map
        Map<Inventory.GiftItem, Integer> giftItems1 = inventory.getGiftItems();
        Map<Inventory.GiftItem, Integer> giftItems2 = inventory.getGiftItems();
        
        assertNotSame(giftItems1, giftItems2, "Should return different map instances");
        
        // Modifying the returned map should not affect the inventory
        Inventory.GiftItem newItem = new Inventory.GiftItem("TestGift", 50);
        giftItems1.put(newItem, 10);
        
        assertFalse(containsGiftItem(inventory.getGiftItems(), newItem), 
                "Inventory should not be affected by changes to returned map");
    }
    
    @Test
    void testFoodItemEquality() {
        // Test FoodItem.equals() and hashCode()
        Inventory.FoodItem apple1 = new Inventory.FoodItem("Apple", 5);
        Inventory.FoodItem apple2 = new Inventory.FoodItem("Apple", 5);
        Inventory.FoodItem appleDiffValue = new Inventory.FoodItem("Apple", 10);
        Inventory.FoodItem orange = new Inventory.FoodItem("Orange", 5);
        
        assertEquals(apple1, apple2, "Equal FoodItems should be equal");
        assertNotEquals(apple1, appleDiffValue, "FoodItems with different values should not be equal");
        assertNotEquals(apple1, orange, "FoodItems with different names should not be equal");
        assertNotEquals(apple1, new Object(), "FoodItem should not equal other objects");
        assertEquals(apple1.hashCode(), apple2.hashCode(), "Equal objects should have equal hash codes");
    }
    
    @Test
    void testGiftItemEquality() {
        // Test GiftItem.equals() and hashCode()
        Inventory.GiftItem ball1 = new Inventory.GiftItem("Ball", 5);
        Inventory.GiftItem ball2 = new Inventory.GiftItem("Ball", 5);
        Inventory.GiftItem ballDiffValue = new Inventory.GiftItem("Ball", 10);
        Inventory.GiftItem toy = new Inventory.GiftItem("Toy", 5);
        
        assertEquals(ball1, ball2, "Equal GiftItems should be equal");
        assertNotEquals(ball1, ballDiffValue, "GiftItems with different values should not be equal");
        assertNotEquals(ball1, toy, "GiftItems with different names should not be equal");
        assertNotEquals(ball1, new Object(), "GiftItem should not equal other objects");
        assertEquals(ball1.hashCode(), ball2.hashCode(), "Equal objects should have equal hash codes");
    }
    
    @Test
    void testAddLargeQuantityFoodItem() {
        // Test adding a very large quantity of an item
        inventory.addFoodItem(banana, Integer.MAX_VALUE - 5);
        assertEquals(Integer.MAX_VALUE - 5, getFoodItemQuantity(inventory.getFoodItems(), banana));
        
        // Add 10 more - this should not cause integer overflow issues
        inventory.addFoodItem(banana, 10);
        assertEquals(Integer.MAX_VALUE, getFoodItemQuantity(inventory.getFoodItems(), banana), 
                "Should cap at Integer.MAX_VALUE or handle overflow gracefully");
    }
    
    @Test
    void testAddLargeQuantityGiftItem() {
        // Test adding a very large quantity of an item
        inventory.addGiftItem(car, Integer.MAX_VALUE - 5);
        assertEquals(Integer.MAX_VALUE - 5, getGiftItemQuantity(inventory.getGiftItems(), car));
        
        // Add 10 more - this should not cause integer overflow issues
        inventory.addGiftItem(car, 10);
        assertEquals(Integer.MAX_VALUE, getGiftItemQuantity(inventory.getGiftItems(), car), 
                "Should cap at Integer.MAX_VALUE or handle overflow gracefully");
    }
    
    @Test
    void testUseItemMultipleTimes() {
        // Use the same item multiple times until it's gone
        int initialCount = getFoodItemQuantity(inventory.getFoodItems(), apple);
        
        // Use the item exactly as many times as we have
        for (int i = 0; i < initialCount; i++) {
            boolean result = inventory.useFoodItem(apple);
            assertTrue(result, "Should return true when item is available");
        }
        
        // Now the item should be gone or have quantity 0
        assertEquals(0, getFoodItemQuantity(inventory.getFoodItems(), apple));
        
        // Try to use it one more time
        boolean result = inventory.useFoodItem(apple);
        assertFalse(result, "Should return false when item is no longer available");
    }
    
    // Helper methods for testing
    
    private boolean containsFoodItem(Map<Inventory.FoodItem, Integer> items, Inventory.FoodItem target) {
        for (Inventory.FoodItem item : items.keySet()) {
            if (item.equals(target)) {
                return true;
            }
        }
        return false;
    }
    
    private boolean containsGiftItem(Map<Inventory.GiftItem, Integer> items, Inventory.GiftItem target) {
        for (Inventory.GiftItem item : items.keySet()) {
            if (item.equals(target)) {
                return true;
            }
        }
        return false;
    }
    
    private int getFoodItemQuantity(Map<Inventory.FoodItem, Integer> items, Inventory.FoodItem target) {
        for (Map.Entry<Inventory.FoodItem, Integer> entry : items.entrySet()) {
            if (entry.getKey().equals(target)) {
                return entry.getValue();
            }
        }
        return 0;
    }
    
    private int getGiftItemQuantity(Map<Inventory.GiftItem, Integer> items, Inventory.GiftItem target) {
        for (Map.Entry<Inventory.GiftItem, Integer> entry : items.entrySet()) {
            if (entry.getKey().equals(target)) {
                return entry.getValue();
            }
        }
        return 0;
    }
}