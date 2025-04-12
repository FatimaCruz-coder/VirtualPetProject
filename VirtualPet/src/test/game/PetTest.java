package group33.VirtualPet.src.test.game;

import group33.VirtualPet.src.main.model.Pet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for Pet functionality.
 * Tests various scenarios and methods in the Pet class to make sure we have a proper implementation.
 * Covers all pet types, edge cases, and complex interaction scenarios.
 * 
 * @author Team 33
 */
class PetTest {
    
    private Pet pet;
    
    @BeforeEach
    void setUp() {
        pet = new Pet("Doggo", Pet.PetType.DOG);
    }
    
    @AfterEach
    void tearDown() {
        pet = null;
    }
    
    @Test
    void testConstructor() {
        // Test initial values set by constructor for dog type
        assertEquals("Doggo", pet.getName());
        assertEquals(Pet.PetType.DOG, pet.getType());
        assertEquals(100, pet.getHealth());
        assertEquals(100, pet.getSleep());
        assertEquals(100, pet.getFullness());
        assertEquals(100, pet.getHappiness());
        assertFalse(pet.isDead());
        assertFalse(pet.isSleeping());
        assertFalse(pet.isHungry());
        assertFalse(pet.isAngry());
        
        // Test initial values for a different pet type
        Pet deer = new Pet("Bambi", Pet.PetType.DEER);
        assertEquals("Bambi", deer.getName());
        assertEquals(Pet.PetType.DEER, deer.getType());
        assertEquals(90, deer.getHealth());
        assertEquals(120, deer.getSleep());
        assertEquals(90, deer.getFullness());
        assertEquals(100, deer.getHappiness());
    }
    
    @ParameterizedTest
    @EnumSource(Pet.PetType.class)
    void testConstructorAllPetTypes(Pet.PetType type) {
        // Test constructor with all pet types
        Pet testPet = new Pet("TestPet", type);
        assertEquals("TestPet", testPet.getName());
        assertEquals(type, testPet.getType());
        assertFalse(testPet.isDead());
        assertFalse(testPet.isSleeping());
        assertFalse(testPet.isHungry());
        assertFalse(testPet.isAngry());
    }
    
    @Test
    void testUpdateStatistics() {
        // Store initial values
        int initialSleep = pet.getSleep();
        int initialFullness = pet.getFullness();
        int initialHappiness = pet.getHappiness();
        
        // Expected values after update
        int expectedSleep = initialSleep - 2; // DOG sleep decline rate is 2
        int expectedFullness = initialFullness - 3; // DOG fullness decline rate is 3
        int expectedHappiness = initialHappiness - 2; // DOG happiness decline rate is 2
        
        // Update statistics
        pet.updateStatistics();
        
        // Verify values
        assertEquals(expectedSleep, pet.getSleep());
        assertEquals(expectedFullness, pet.getFullness());
        assertEquals(expectedHappiness, pet.getHappiness());
        assertEquals(100, pet.getHealth()); // Health shouldn't change in normal update
    }
    
    @Test
    void testUpdateStatisticsWhenDead() {
        // Set pet as dead
        pet.setDead(true);
        
        // Store initial values
        int initialSleep = pet.getSleep();
        int initialFullness = pet.getFullness();
        int initialHappiness = pet.getHappiness();
        
        // Update statistics
        pet.updateStatistics();
        
        // Values should remain unchanged
        assertEquals(initialSleep, pet.getSleep());
        assertEquals(initialFullness, pet.getFullness());
        assertEquals(initialHappiness, pet.getHappiness());
    }
    
    @Test
    void testUpdateStatisticsWhenSleeping() {
        // Set pet to sleep
        pet.sleep();
        assertTrue(pet.isSleeping());
        
        // Store initial values
        int initialSleep = pet.getSleep();
        int initialFullness = pet.getFullness();
        int initialHappiness = pet.getHappiness();
        
        // Update statistics
        pet.updateStatistics();
        
        // Values should remain unchanged
        assertEquals(initialSleep, pet.getSleep());
        assertEquals(initialFullness, pet.getFullness());
        assertEquals(initialHappiness, pet.getHappiness());
    }
    
    @Test
    void testUpdateStatisticsCriticalSleep() {
        // Set sleep to critical level
        pet.setSleep(1);
        pet.updateStatistics();
        
        // Pet should be sleeping and have health penalty
        assertEquals(0, pet.getSleep());
        assertEquals(90, pet.getHealth()); // 100 - 10 health penalty
        assertTrue(pet.isSleeping());
    }
    
    @Test
    void testUpdateStatisticsCriticalFullness() {
        // Set fullness to critical level
        pet.setFullness(1);
        pet.updateStatistics();
        
        // Pet should be hungry and have penalties
        assertEquals(0, pet.getFullness());
        assertTrue(pet.isHungry());
        assertEquals(95, pet.getHealth()); // 100 - 5 health penalty
        assertEquals(96, pet.getHappiness()+2); // 100 - (2 * 2) happiness penalty
    }
    
    @Test
    void testUpdateStatisticsCriticalHappiness() {
        // Set happiness to critical level
        pet.setHappiness(1);
        pet.updateStatistics();
        
        // Pet should be angry
        assertEquals(0, pet.getHappiness());
        assertTrue(pet.isAngry());
    }
    
    @Test
    void testUpdateStatisticsMultipleUpdates() {
        // Multiple updates to verify stats decline correctly
        for (int i = 0; i < 10; i++) {
            pet.updateStatistics();
        }
        
        // Expected values after 10 updates
        assertEquals(100 - (10 * 2), pet.getSleep()); // 80
        assertEquals(100 - (10 * 3), pet.getFullness()); // 70
        assertEquals(100 - (10 * 2), pet.getHappiness()); // 80
        assertEquals(100, pet.getHealth()); // Health unchanged
    }
    
    @Test
    void testCheckHealthState() {
        // Test normal health
        pet.setHealth(50);
        pet.checkHealthState();
        assertFalse(pet.isDead());
        
        // Test critical health
        pet.setHealth(0);
        pet.checkHealthState();
        assertTrue(pet.isDead());
        
        // Test negative health (should be considered dead)
        pet.setHealth(-10);
        pet.checkHealthState();
        assertTrue(pet.isDead());
    }
    
    @Test
    void testFeed() {
        // Normal feeding
        pet.setFullness(50);
        pet.feed(30);
        assertEquals(80, pet.getFullness());
        assertFalse(pet.isHungry());
        
        // Feeding when hungry
        pet.setFullness(0);
        pet.checkFullnessState();
        assertTrue(pet.isHungry()); // Pet should be hungry after checkFullnessState()
        pet.feed(40);
        assertEquals(40, pet.getFullness());
        assertFalse(pet.isHungry());
        
        // Feeding beyond max
        pet.setFullness(90);
        pet.feed(20); // Max is 100
        assertEquals(100, pet.getFullness()); // Should be capped at max
    }
    
    @Test
    void testFeedWhenDeadOrSleeping() {
        // Feeding when dead
        pet.setDead(true);
        pet.setFullness(50);
        pet.feed(20);
        assertEquals(50, pet.getFullness()); // Should not change
        
        // Feeding when sleeping
        pet.setDead(false);
        pet.sleep();
        pet.feed(20);
        assertEquals(50, pet.getFullness()); // Should not change
    }
    
    @Test
    void testGiveGift() {
        // Normal gift
        pet.setHappiness(60);
        pet.giveGift(20);
        assertEquals(80, pet.getHappiness());
        assertFalse(pet.isAngry());
        
        // Giving gift when angry
        pet.setHappiness(0);
        pet.checkHappinessState();
        assertTrue(pet.isAngry()); // Pet should be angry after checkHappinessState()
        pet.giveGift(50);
        assertEquals(50, pet.getHappiness());
        assertFalse(pet.isAngry());
        
        // Giving gift beyond max
        pet.setHappiness(90);
        pet.giveGift(20); // Max is 100
        assertEquals(100, pet.getHappiness()); // Should be capped at max
    }
    
    @Test
    void testGiveGiftWhenDeadOrSleeping() {
        // Gift when dead
        pet.setDead(true);
        pet.setHappiness(50);
        pet.giveGift(20);
        assertEquals(50, pet.getHappiness()); // Should not change
        
        // Gift when sleeping
        pet.setDead(false);
        pet.sleep();
        pet.giveGift(20);
        assertEquals(50, pet.getHappiness()); // Should not change
    }
    
    @Test
    void testSleep() {
        // Normal sleep
        assertFalse(pet.isSleeping());
        pet.sleep();
        assertTrue(pet.isSleeping());
        
        // Sleep when already sleeping
        pet.sleep(); // Should have no adverse effects
        assertTrue(pet.isSleeping());
    }
    
    @Test
    void testSleepWhenDead() {
        // Sleep when dead
        pet.setDead(true);
        pet.sleep();
        assertFalse(pet.isSleeping()); // Cannot sleep when dead
    }
    
    @Test
    void testWakeUp() {
        // Wake up normal
        pet.setSleep(50);
        pet.sleep();
        pet.wakeUp();
        assertEquals(100, pet.getSleep()); // Sleep should be restored to max
        assertFalse(pet.isSleeping());
        
        // Wake up when not sleeping
        pet.setSleep(60);
        pet.wakeUp();
        assertEquals(100, pet.getSleep()); // Sleep should still be restored to max
    }
    
    @Test
    void testWakeUpWhenDead() {
        // Wake up when dead
        pet.setDead(true);
        pet.setSleep(50);
        pet.wakeUp();
        assertEquals(50, pet.getSleep()); // Should not change
    }
    
    @Test
    void testExercise() {
        // Normal exercise
        pet.setHealth(80);
        pet.setSleep(100);
        pet.setFullness(100);
        pet.exercise();
        
        assertEquals(90, pet.getSleep()); // 100 - 10
        assertEquals(95, pet.getFullness()); // 100 - 5
        assertEquals(85, pet.getHealth()); // 80 + 5
    }
    
    @Test
    void testExerciseWithEdgeCases() {
        // Exercise with low values
        pet.setHealth(95);
        pet.setSleep(5);
        pet.setFullness(3);
        pet.exercise();
        
        assertEquals(0, pet.getSleep()); // Bottoms out at 0
        assertEquals(0, pet.getFullness()); // Bottoms out at 0
        assertEquals(100, pet.getHealth()); // Caps at max (100)
        
        // Verify hunger state after exercise depletes fullness
        pet.checkFullnessState();
        assertTrue(pet.isHungry());
    }
    
    @Test
    void testExerciseWhenDeadOrSleeping() {
        // Exercise when dead
        pet.setDead(true);
        pet.setHealth(80);
        pet.setSleep(100);
        pet.setFullness(100);
        pet.exercise();
        
        // Values should not change
        assertEquals(80, pet.getHealth());
        assertEquals(100, pet.getSleep());
        assertEquals(100, pet.getFullness());
        
        // Exercise when sleeping
        pet.setDead(false);
        pet.sleep();
        pet.exercise();
        
        // Values should not change
        assertEquals(80, pet.getHealth());
        assertEquals(100, pet.getSleep());
        assertEquals(100, pet.getFullness());
    }
    
    @Test
    void testTakeToVet() {
        // Normal vet visit
        pet.setHealth(60);
        pet.takeToVet();
        assertEquals(80, pet.getHealth()); // 60 + 20
        
        // Vet visit at high health
        pet.setHealth(90);
        pet.takeToVet();
        assertEquals(100, pet.getHealth()); // Capped at max (100)
    }
    
    @Test
    void testTakeToVetWhenDeadOrSleeping() {
        // Vet when dead
        pet.setDead(true);
        pet.setHealth(60);
        pet.takeToVet();
        assertEquals(60, pet.getHealth()); // Should not change
        
        // Vet when sleeping
        pet.setDead(false);
        pet.sleep();
        pet.takeToVet();
        assertEquals(60, pet.getHealth()); // Should not change
    }
    
    @Test
    void testGettersAndSetters() {
        // Test all getters and setters
        
        // Test health setter
        pet.setHealth(75);
        assertEquals(75, pet.getHealth());
        
        // Test sleep setter
        pet.setSleep(65);
        assertEquals(65, pet.getSleep());
        
        // Test fullness setter
        pet.setFullness(85);
        assertEquals(85, pet.getFullness());
        
        // Test happiness setter
        pet.setHappiness(45);
        assertEquals(45, pet.getHappiness());
        
        // Test dead setter
        pet.setDead(true);
        assertTrue(pet.isDead());
    }
    
    @Test
    void testRevive() {
        // Setup dead pet with low stats
        pet.setHealth(0);
        pet.setSleep(0);
        pet.setFullness(0);
        pet.setHappiness(0);
        pet.setDead(true);
        pet.sleep(); // Set sleeping
        
        // Revive pet
        pet.revive();
        
        // All stats should be restored to max
        assertEquals(100, pet.getHealth());
        assertEquals(100, pet.getSleep());
        assertEquals(100, pet.getFullness());
        assertEquals(100, pet.getHappiness());
        
        // States should be reset
        assertFalse(pet.isDead());
        assertFalse(pet.isSleeping());
        assertFalse(pet.isHungry());
        assertFalse(pet.isAngry());
    }
    
    @Test
    void testDifferentPetTypes() {
        // Test DEER pet type
        Pet deer = new Pet("Bambi", Pet.PetType.DEER);
        deer.updateStatistics();
        assertEquals(120 - 1, deer.getSleep()); // DEER sleep decline is 1
        assertEquals(90 - 4, deer.getFullness()); // DEER fullness decline is 4
        assertEquals(100 - 2, deer.getHappiness()); // DEER happiness decline is 2
        
        // Test FROG pet type
        Pet frog = new Pet("Kermit", Pet.PetType.FROG);
        frog.updateStatistics();
        assertEquals(90 - 2, frog.getSleep()); // FROG sleep decline is 2
        assertEquals(110 - 1, frog.getFullness()); // FROG fullness decline is 1
        assertEquals(100 - 3, frog.getHappiness()); // FROG happiness decline is 3
        
        // Test JELLYFISH pet type
        Pet jellyfish = new Pet("Squishy", Pet.PetType.JELLYFISH);
        jellyfish.updateStatistics();
        assertEquals(90 - 2, jellyfish.getSleep()); // JELLYFISH sleep decline is 2
        assertEquals(90 - 2, jellyfish.getFullness()); // JELLYFISH fullness decline is 2
        assertEquals(90 - 4, jellyfish.getHappiness()); // JELLYFISH happiness decline is 4
        
        // Test PENGUIN pet type
        Pet penguin = new Pet("Pingu", Pet.PetType.PENGUIN);
        penguin.updateStatistics();
        assertEquals(80 - 3, penguin.getSleep()); // PENGUIN sleep decline is 3
        assertEquals(110 - 2, penguin.getFullness()); // PENGUIN fullness decline is 2
        assertEquals(110 - 1, penguin.getHappiness()); // PENGUIN happiness decline is 1
    }
    
    @Test
    void testCascadingEffects() {
        // Test that critical fullness affects other stats
        pet.setFullness(1);
        pet.updateStatistics();
        
        // Pet should be hungry
        assertEquals(0, pet.getFullness());
        assertTrue(pet.isHungry());
        
        // Hunger should affect happiness with double decline rate
        assertEquals(100 - (2 * 2), pet.getHappiness()+2); // Standard decline is 2, doubled to 4
        
        // Hunger should affect health
        assertEquals(95, pet.getHealth()); // 100 - 5 health penalty
        
        // Another update should continue cascading effects
        pet.updateStatistics();
        
        // Happiness should continue to decline at accelerated rate
        assertEquals(100 - (2 * 2) - (2 * 2), pet.getHappiness()+4);
        
        // Health should continue to decline
        assertEquals(90, pet.getHealth()); // Another -5 health penalty
    }
    
    @Test
    void testForcedSleepWhenTired() {
        // Deplete sleep to force auto-sleep
        pet.setSleep(1);
        pet.updateStatistics();
        
        // Pet should be forced to sleep and get health penalty
        assertEquals(0, pet.getSleep());
        assertEquals(90, pet.getHealth()); // 100 - 10 health penalty
        assertTrue(pet.isSleeping());
        
        // Actions should have no effect while sleeping
        pet.feed(10);
        pet.giveGift(10);
        pet.exercise();
        
        // Values should not change
        assertEquals(0, pet.getSleep());
        assertEquals(90, pet.getHealth());
        assertEquals(100, pet.getFullness()+3);
        assertEquals(100, pet.getHappiness()+2);
        
        // Wake up should restore sleep
        pet.wakeUp();
        assertEquals(100, pet.getSleep());
        assertFalse(pet.isSleeping());
    }
    
    @Test
    void testDeathFromStarvation() {
        // Simulate death from starvation
        pet.setFullness(0);
        pet.setHealth(5); // Just enough health to die after one update
        
        // Update should trigger health penalty from hunger
        pet.updateStatistics();
        
        // Health should be depleted and pet should die
        assertEquals(0, pet.getHealth());
        assertTrue(pet.isDead());
        
        // Further updates should not change values
        int originalHappiness = pet.getHappiness();
        pet.updateStatistics();
        assertEquals(originalHappiness, pet.getHappiness());
    }
    
    @Test
    void testComplexScenario() {
        // Create pet with reasonable but not perfect stats
        pet.setHealth(80);
        pet.setSleep(70);
        pet.setFullness(60);
        pet.setHappiness(50);
        
        // Day 1 - Feed and play
        pet.feed(20);
        pet.giveGift(30);
        pet.updateStatistics(); // End of day
        
        assertEquals(80, pet.getHealth());
        assertEquals(68, pet.getSleep()); // 70 - 2
        assertEquals(77, pet.getFullness()); // 60 + 20 - 3
        assertEquals(78, pet.getHappiness()); // 50 + 30 - 2
        
        // Day 2 - Exercise and vet
        pet.exercise();
        pet.takeToVet();
        pet.updateStatistics(); // End of day
        
        assertEquals(100, pet.getHealth()); // (80 + 5 + 20), capped at 100
        assertEquals(56, pet.getSleep()); // 68 - 10 - 2
        assertEquals(69, pet.getFullness()); // 77 - 5 - 3
        assertEquals(76, pet.getHappiness()); // 78 - 2
        
        // Day 3 - Sleep all day
        pet.sleep();
        pet.updateStatistics(); // No effect while sleeping
        
        assertEquals(100, pet.getHealth());
        assertEquals(56, pet.getSleep());
        assertEquals(69, pet.getFullness());
        assertEquals(76, pet.getHappiness());
        
        // Day 4 - Wake up and continue
        pet.wakeUp();
        pet.feed(15);
        pet.updateStatistics(); // End of day
        
        assertEquals(100, pet.getHealth());
        assertEquals(98, pet.getSleep()); // Reset to 100 from wakeUp, then - 2
        assertEquals(81, pet.getFullness()); // 69 + 15 - 3
        assertEquals(74, pet.getHappiness()); // 76 - 2
    }
}