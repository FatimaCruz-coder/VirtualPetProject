package group33.VirtualPet.src.test.game;

import group33.VirtualPet.src.main.model.Inventory;
import group33.VirtualPet.src.main.model.Pet;
import group33.VirtualPet.src.main.model.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for Player functionality.
 * Tests various scenarios and methods in the Player class to make sure the we have a proper implementation.
 * 
 * @author Team 33
 */
class PlayerTest {
    
    private Player player;
    private Pet pet;
    
    @BeforeEach
    void setUp() {
        player = new Player("John");
        pet = new Pet("Doggo", Pet.PetType.DOG);
    }
    
    @AfterEach
    void tearDown() {
        player = null;
        pet = null;
    }
    
    @Test
    void testConstructor() {
        // Test initial values set by constructor
        assertEquals("John", player.getName());
        assertEquals(0, player.getScore());
        assertNotNull(player.getInventory());
        assertEquals(Duration.ZERO, player.getTotalPlayTime());
        assertNull(player.getCurrentPet());
        
        // Test timestamp initialization
        long currentTime = System.currentTimeMillis();
        // long cooldownPeriod = 300000;
        
        // Timestamps should be initialized to current time minus cooldown
        assertTrue(player.getLastP() <= currentTime);
        assertTrue(player.getLastV() <= currentTime);
        assertTrue(currentTime - player.getLastP() >= 0);
        assertTrue(currentTime - player.getLastV() >= 0);
    }
    
    @Test
    void testAdoptPet() {
        // Test adopting a pet
        player.adoptPet(pet);
        assertEquals(pet, player.getCurrentPet());
        
        // Test changing to a different pet
        Pet newPet = new Pet("Penelope", Pet.PetType.DEER);
        player.adoptPet(newPet);
        assertEquals(newPet, player.getCurrentPet());
        assertNotEquals(pet, player.getCurrentPet());
        
        // Test abandoning pet (setting to null)
        player.adoptPet(null);
        assertNull(player.getCurrentPet());
    }
    
    @Test
    void testIncrementScore() {
        // Test incrementing from zero
        player.incrementScore(50);
        assertEquals(50, player.getScore());
        
        // Test multiple increments
        player.incrementScore(30);
        assertEquals(80, player.getScore());
        
        // Test increment by zero
        player.incrementScore(0);
        assertEquals(80, player.getScore());
        
        // Test increment by large number
        player.incrementScore(Integer.MAX_VALUE - 80);
        assertEquals(Integer.MAX_VALUE, player.getScore());
        
        // Test potential overflow (should not exceed Integer.MAX_VALUE)
        player.incrementScore(10);
        assertEquals(Integer.MAX_VALUE, player.getScore()); // Should not overflow
    }
    
    @Test
    void testDecrementScore() {
        // Set up initial score
        player.setScore(100);
        
        // Test basic decrement
        player.decrementScore(40);
        assertEquals(60, player.getScore());
        
        // Test multiple decrements
        player.decrementScore(30);
        assertEquals(30, player.getScore());
        
        // Test decrement by zero
        player.decrementScore(0);
        assertEquals(30, player.getScore());
        
        // Test decrement below zero (should stop at zero)
        player.decrementScore(50);
        assertEquals(0, player.getScore());
        
        // Test decrement when already at zero
        player.decrementScore(10);
        assertEquals(0, player.getScore());
        
        // Test with large number
        player.setScore(100);
        player.decrementScore(Integer.MAX_VALUE);
        assertEquals(0, player.getScore());
    }
    
    @Test
    void testStartAndEndSession() {
        // Test starting a session
        player.startSession();
        
        // Sleep for a brief time to ensure measurable duration
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            fail("Test interrupted");
        }
        
        // End session and check duration
        player.endSession();
        assertTrue(player.getTotalPlayTime().toMillis() > 0);
        
        // Check if session duration was added to the total
        Duration firstSessionDuration = player.getTotalPlayTime();
        
        // Start and end another session
        player.startSession();
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            fail("Test interrupted");
        }
        player.endSession();
        
        // Check if total was updated correctly
        assertTrue(player.getTotalPlayTime().compareTo(firstSessionDuration) > 0);
    }
    
    @Test
    void testEndSessionWithoutStarting() {
        // End a session that was never started
        player.endSession();
        
        // Total play time should still be zero
        assertEquals(Duration.ZERO, player.getTotalPlayTime());
    }
    
    @Test
    void testSetTotalPlayTime() {
        // Test setting play time
        Duration testDuration = Duration.ofMinutes(45);
        player.setTotalPlayTime(testDuration);
        assertEquals(testDuration, player.getTotalPlayTime());
        
        // Test setting to zero
        player.setTotalPlayTime(Duration.ZERO);
        assertEquals(Duration.ZERO, player.getTotalPlayTime());
    }
    
    @Test
    void testSetScore() {
        // Test setting to positive value
        player.setScore(500);
        assertEquals(500, player.getScore());
        
        // Test setting to zero
        player.setScore(0);
        assertEquals(0, player.getScore());
        
        // Test setting to negative value (should be prevented)
        player.setScore(-100);
        assertEquals(0, player.getScore());
        
        // Test setting to max value
        player.setScore(Integer.MAX_VALUE);
        assertEquals(Integer.MAX_VALUE, player.getScore());
    }
    
    @Test
    void testSetInventory() {
        // Create a new inventory
        Inventory newInventory = new Inventory();
        Inventory.FoodItem foodItem = new Inventory.FoodItem("TestFood", 10);
        newInventory.addFoodItem(foodItem, 5);
        
        // Set the inventory
        player.setInventory(newInventory);
        
        // Check if inventory was set correctly
        assertEquals(newInventory, player.getInventory());
        
        // Check if inventory contains the added items
        assertEquals(5, player.getInventory().getFoodItems().get(foodItem));
    }

    @Test
    void testResetPlayTimeStatistics() {
        // Record some play time
        player.startSession();
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            fail("Test interrupted");
        }
        player.endSession();
        
        assertTrue(player.getTotalPlayTime().toMillis() > 0);
        /// assertTrue(player.getAveragePlayTime() > 0);

        // Reset statistics
        player.resetPlayTimeStatistics();
        
        // Check if statistics were reset
        assertEquals(Duration.ZERO, player.getTotalPlayTime());
        assertEquals(0, player.getAveragePlayTime());
    }
    
    @Test
    void testSetAndGetLastP() {
        // Test setting and getting last play time
        long testTime = System.currentTimeMillis();
        player.setLastP(testTime);
        assertEquals(testTime, player.getLastP());
        
        // Test with zero
        player.setLastP(0);
        assertEquals(0, player.getLastP());
        
        // Test with future time
        long futureTime = System.currentTimeMillis() + 1000000;
        player.setLastP(futureTime);
        assertEquals(futureTime, player.getLastP());
    }
    
    @Test
    void testSetAndGetLastV() {
        // Test setting and getting last vet time
        long testTime = System.currentTimeMillis();
        player.setLastV(testTime);
        assertEquals(testTime, player.getLastV());
        
        // Test with zero
        player.setLastV(0);
        assertEquals(0, player.getLastV());
        
        // Test with future time
        long futureTime = System.currentTimeMillis() + 1000000;
        player.setLastV(futureTime);
        assertEquals(futureTime, player.getLastV());
    }
    
    @Test
    void testComplexSessionScenario() {
        // Simulate a more complex usage scenario
        
        // First session
        player.startSession();
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            fail("Test interrupted");
        }
        player.endSession();
        Duration firstDuration = player.getTotalPlayTime();
        
        // Second session
        player.startSession();
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            fail("Test interrupted");
        }
        player.endSession();
        Duration secondTotalDuration = player.getTotalPlayTime();
        
        // Third session with overlapping start (without ending previous)
        player.startSession();
        player.startSession(); // This should override the previous start
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            fail("Test interrupted");
        }
        player.endSession();
        Duration thirdTotalDuration = player.getTotalPlayTime();
        
        // Verify total durations
        assertTrue(secondTotalDuration.compareTo(firstDuration) > 0);
        assertTrue(thirdTotalDuration.compareTo(secondTotalDuration) > 0);
    }
    
    @Test
    void testMultipleEndSessionWithoutStart() {
        // Initial time should be zero
        assertEquals(Duration.ZERO, player.getTotalPlayTime());
        
        // End multiple times without starting
        player.endSession();
        player.endSession();
        player.endSession();
        
        // Time should still be zero
        assertEquals(Duration.ZERO, player.getTotalPlayTime());
    }
    
    @Test
    void testConcurrentPlayAndPetActions() {
        // Adopt a pet
        player.adoptPet(pet);
        
        // Start a session
        player.startSession();
        
        // Use Thread.sleep for a more reliable test
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            fail("Test interrupted");
        }
        
        // Perform pet actions
        pet.setHappiness(50);
        pet.setHealth(60);
        
        // End session
        player.endSession();
        
        // Verify pet state
        assertEquals(50, pet.getHappiness());
        assertEquals(60, pet.getHealth());
        
        // Verify there is some play time recorded
        assertTrue(player.getTotalPlayTime().toMillis() > 0);
    }
    
    @Test
    void testInventoryOperationsDuringSession() {
        // Start a session
        player.startSession();
        
        // Use Thread.sleep for a more reliable test
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            fail("Test interrupted");
        }
        
        // Add items to inventory
        Inventory inventory = player.getInventory();
        Inventory.FoodItem foodItem = new Inventory.FoodItem("TestFood", 10);
        inventory.addFoodItem(foodItem, 5);
        
        // End session
        player.endSession();
        
        // Verify inventory contents
        assertEquals(5, player.getInventory().getFoodItems().get(foodItem));
        
        // Verify there is some play time recorded
        assertTrue(player.getTotalPlayTime().toMillis() > 0);
    }
    
    @Test
    void testScoreManipulationEdgeCases() {
        // Test initial score
        assertEquals(0, player.getScore());
        
        // Test increment with negative value
        player.incrementScore(-10);
        assertEquals(0, player.getScore());
        
        // Reset score
        player.setScore(50);
        
        // Test decrement with negative value
        player.decrementScore(-10);
        // Player implementation likely treats this as subtracting a negative number (adding)
        assertEquals(60, player.getScore());
    }
}