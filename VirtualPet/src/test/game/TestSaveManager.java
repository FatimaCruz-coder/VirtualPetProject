package group33.VirtualPet.src.test.game;

import group33.VirtualPet.src.main.game.GameSaveManager;
import group33.VirtualPet.src.main.model.Inventory;
import group33.VirtualPet.src.main.model.ParentalSettings;
import group33.VirtualPet.src.main.model.Pet;
import group33.VirtualPet.src.main.model.Player;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
/**
 * Test class for GameSaveManager functionality.
 * Tests various scenarios and methods in the save manager class to make sure we handle all edge cases and enforce the correct functionality.
 * 
 * @author Team 33
 */

class TestSaveManager {
    
    private Player testPlayer;
    private Pet testPet;
    private static final String TEST_SAVE_FILE = "test_save.csv";
    
    @BeforeEach
    void setUp() {
        // Create a test player with inventory and pet
        testPlayer = new Player("TestPlayer");
        testPlayer.setScore(100);
        testPlayer.setTotalPlayTime(Duration.ofMinutes(30));
        testPlayer.setLastP(System.currentTimeMillis() - 3600000); // 1 hour ago
        testPlayer.setLastV(System.currentTimeMillis() - 7200000); // 2 hours ago
        
        // Create and add a pet
        testPet = new Pet("TestPet", Pet.PetType.DOG);
        testPet.setHealth(80);
        testPet.setHappiness(70);
        testPet.setFullness(60);
        testPet.setSleep(50);
        testPlayer.adoptPet(testPet);
        
        // Add items to inventory
        Inventory inventory = testPlayer.getInventory();
        Inventory.FoodItem foodItem = new Inventory.FoodItem("TestFood", 10);
        Inventory.GiftItem giftItem = new Inventory.GiftItem("TestGift", 15);
        inventory.addFoodItem(foodItem, 5);
        inventory.addGiftItem(giftItem, 3);
    }
    
    @AfterEach
    void tearDown() {
        // Clean up test save file
        try {
            File saveDir = new File("group33/VirtualPet/src/main/save_files");
            if (saveDir.exists()) {
                File testFile = new File(saveDir, TEST_SAVE_FILE);
                if (testFile.exists()) {
                    testFile.delete();
                }
            }
        } catch (Exception e) {
            System.err.println("Error cleaning up test files: " + e.getMessage());
        }
    }
    
    @Test
    void testSaveAndLoadGame() throws IOException {
        // Save the game
        GameSaveManager.saveGame(testPlayer, TEST_SAVE_FILE);
        
        // Load the game
        Player loadedPlayer = GameSaveManager.loadGame(TEST_SAVE_FILE);
        
        // Verify player data
        assertNotNull(loadedPlayer, "Loaded player should not be null");
        assertEquals(testPlayer.getName(), loadedPlayer.getName(), "Player name should match");
        assertEquals(testPlayer.getScore(), loadedPlayer.getScore(), "Player score should match");
        assertEquals(testPlayer.getTotalPlayTime().toSeconds(), loadedPlayer.getTotalPlayTime().toSeconds(), "Play time should match");
        assertEquals(testPlayer.getLastP(), loadedPlayer.getLastP(), "Last play time should match");
        assertEquals(testPlayer.getLastV(), loadedPlayer.getLastV(), "Last vet time should match");
        
        // Verify pet data
        Pet loadedPet = loadedPlayer.getCurrentPet();
        assertNotNull(loadedPet, "Loaded pet should not be null");
        assertEquals(testPet.getName(), loadedPet.getName(), "Pet name should match");
        assertEquals(testPet.getType(), loadedPet.getType(), "Pet type should match");
        assertEquals(testPet.getHealth(), loadedPet.getHealth(), "Pet health should match");
        assertEquals(testPet.getHappiness(), loadedPet.getHappiness(), "Pet happiness should match");
        assertEquals(testPet.getFullness(), loadedPet.getFullness(), "Pet fullness should match");
        assertEquals(testPet.getSleep(), loadedPet.getSleep(), "Pet sleep should match");
        
        // Verify inventory data
        Inventory loadedInventory = loadedPlayer.getInventory();
        assertNotNull(loadedInventory, "Loaded inventory should not be null");
        
        // Check food items
        boolean foundFoodItem = false;
        for (Inventory.FoodItem foodItem : loadedInventory.getFoodItems().keySet()) {
            if (foodItem.getName().equals("TestFood") && foodItem.getFullnessValue() == 10) {
                assertEquals(5, loadedInventory.getFoodItems().get(foodItem), "Food item quantity should match");
                foundFoodItem = true;
                break;
            }
        }
        assertTrue(foundFoodItem, "Test food item should be in inventory");
        
        // Check gift items
        boolean foundGiftItem = false;
        for (Inventory.GiftItem giftItem : loadedInventory.getGiftItems().keySet()) {
            if (giftItem.getName().equals("TestGift") && giftItem.getHappinessValue() == 15) {
                assertEquals(3, loadedInventory.getGiftItems().get(giftItem), "Gift item quantity should match");
                foundGiftItem = true;
                break;
            }
        }
        assertTrue(foundGiftItem, "Test gift item should be in inventory");
    }
    
    @Test
    void testListSaveFiles() throws IOException {
        // Save a test game and get list of save files
        GameSaveManager.saveGame(testPlayer, TEST_SAVE_FILE);
        List<String> saveFiles = GameSaveManager.listSaveFiles();
        
        // Verify the test file is in the list
        assertTrue(saveFiles.contains(TEST_SAVE_FILE), "Save file list should contain the test file");
    }
    
    @Test
    void testRevivePet() throws IOException {
        // Kill the pet :( 
        testPet.setHealth(0);
        testPet.checkHealthState();
        assertTrue(testPet.isDead(), "Pet should be dead for this test");
        
        // Save the game with dead pet and then revive the pet
        GameSaveManager.saveGame(testPlayer, TEST_SAVE_FILE);
        
        boolean reviveResult = GameSaveManager.revivePet(TEST_SAVE_FILE);
        assertTrue(reviveResult, "Revive operation should succeed");
        
        // Load the game and check if pet is alive
        Player loadedPlayer = GameSaveManager.loadGame(TEST_SAVE_FILE);
        Pet loadedPet = loadedPlayer.getCurrentPet();
        
        assertFalse(loadedPet.isDead(), "Pet should be alive after reviving");
        assertTrue(loadedPet.getHealth() > 0, "Pet should have positive health after reviving");
    }
    
    @Test
    void testSaveAndLoadParentalSettings() throws IOException {
        // Create test parental settings
        ParentalSettings settings = new ParentalSettings();
        settings.setTimeRestrictionEnabled(true);
        settings.setAllowedStartTime(LocalTime.of(9, 0));
        settings.setAllowedEndTime(LocalTime.of(18, 0));
        settings.setTotalPlayTime(Duration.ofHours(2));
        settings.addSessionTime(Duration.ofMinutes(30));
        
        // Save and load settings
        GameSaveManager.saveParentalSettings(settings);
        ParentalSettings loadedSettings = GameSaveManager.loadParentalSettings();
        
        // Verify settings data
        assertNotNull(loadedSettings, "Loaded settings should not be null");
        assertEquals(settings.isTimeRestrictionEnabled(), loadedSettings.isTimeRestrictionEnabled(), "Time restriction setting should match");
        assertEquals(settings.getAllowedStartTime(), loadedSettings.getAllowedStartTime(), "Start time should match");
        assertEquals(settings.getAllowedEndTime(), loadedSettings.getAllowedEndTime(), "End time should match");
        assertEquals(settings.getTotalPlayTime().toSeconds(), loadedSettings.getTotalPlayTime().toSeconds(), "Total play time should match");
    }
    
    @Test
    void testPlayTimeAllowed() throws IOException {
        // Create settings with restrictions
        ParentalSettings settings = new ParentalSettings();
        settings.setTimeRestrictionEnabled(true);
        
        // Set allowed time to a window that includes now
        LocalTime now = LocalTime.now();
        LocalTime start = now.minusHours(1);
        LocalTime end = now.plusHours(1);
        
        settings.setAllowedStartTime(start);
        settings.setAllowedEndTime(end);
        
        // Save settings
        GameSaveManager.saveParentalSettings(settings);
        
        // Check if play is allowed
        boolean playAllowed = GameSaveManager.isPlayTimeAllowed("playing");
        assertTrue(playAllowed, "Play should be allowed during allowed hours");
        
        // Change settings to disallow play now
        settings.setAllowedStartTime(now.plusHours(2));
        settings.setAllowedEndTime(now.plusHours(3));
        GameSaveManager.saveParentalSettings(settings);
        
        // Check if play is allowed
        playAllowed = GameSaveManager.isPlayTimeAllowed("playing");
        assertFalse(playAllowed, "Play should not be allowed outside allowed hours");
    }
    
    @Test
    void testRecordPlaySession() throws IOException {
        // Load initial settings
        ParentalSettings initialSettings = GameSaveManager.loadParentalSettings();
        long initialSessionCount = initialSettings.getSessionCount();
        Duration initialPlayTime = initialSettings.getTotalPlayTime();
        
        // Record a play session
        Duration sessionDuration = Duration.ofMinutes(15);
        GameSaveManager.recordPlaySession(sessionDuration);
        
        // Load updated settings
        ParentalSettings updatedSettings = GameSaveManager.loadParentalSettings();
        
        // Verify session was recorded
        assertEquals(initialSessionCount + 1, updatedSettings.getSessionCount(), "Session count should be incremented");
        assertEquals(initialPlayTime.plus(sessionDuration).toSeconds(), 
                     updatedSettings.getTotalPlayTime().toSeconds(), 
                     "Total play time should be increased by session duration");
    }
    
    @Test
    void testStartAndEndPlaySession() throws IOException {
        // Start a play session
        GameSaveManager.startPlaySession();
        
        // Load settings to check if session was started
        ParentalSettings settings = GameSaveManager.loadParentalSettings();
        assertNotNull(settings.getLastSessionStart(), "Last session start should be set");
        
        // End the play session
        GameSaveManager.endPlaySession();
        
        // Load settings to check if session was ended
        settings = GameSaveManager.loadParentalSettings();
        assertNotNull(settings.getLastSessionEnd(), "Last session end should be set");
    }
    
    @Test
    void testFileAutoExtension() throws IOException {
        // Save game without .csv extension
        String filename = "test_save_no_extension";
        GameSaveManager.saveGame(testPlayer, filename);
        
        // Check if file exists with .csv extension
        File saveDir = new File("group33/VirtualPet/src/main/save_files");
        File savedFile = new File(saveDir, filename + ".csv");
        assertTrue(savedFile.exists(), "Save file should exist with added .csv extension");
        
        // Try to load the file without extension
        Player loadedPlayer = GameSaveManager.loadGame(filename);
        assertNotNull(loadedPlayer, "Should be able to load file without specifying extension");
        
        // Clean up
        savedFile.delete();
    }
    
    @Test
    void testSavePetState() throws IOException {
        // Set specific pet states (ex sleeping state)
        testPet.sleep();
        
        // Save adn load the game
        GameSaveManager.saveGame(testPlayer, TEST_SAVE_FILE);
        
        Player loadedPlayer = GameSaveManager.loadGame(TEST_SAVE_FILE);
        Pet loadedPet = loadedPlayer.getCurrentPet();
        
        // check if pet states were saved correctly
        assertTrue(loadedPet.isSleeping(), "Pet sleeping state should be preserved");
    }
    
    @Test
    void testDeadPetState() throws IOException {
        // Kill the pet :(
        testPet.setHealth(0);
        testPet.checkHealthState();
        assertTrue(testPet.isDead(), "Pet should be dead");
        
        // Save and load the game
        GameSaveManager.saveGame(testPlayer, TEST_SAVE_FILE);
        Player loadedPlayer = GameSaveManager.loadGame(TEST_SAVE_FILE);
        Pet loadedPet = loadedPlayer.getCurrentPet();
        
        // check if dead state was saved
        assertTrue(loadedPet.isDead(), "Pet dead state should be preserved");
    }
}