package group33.VirtualPet.src.main.game;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import group33.VirtualPet.src.main.model.Inventory;
import group33.VirtualPet.src.main.model.ParentalSettings;
import group33.VirtualPet.src.main.model.Pet;
import group33.VirtualPet.src.main.model.Player;

/**
 * Manages game saving and loading functionality for the Virtual Pet game.
 * This class provides static methods to:
 * <ul>
 *   <li>Save and load player game states (including pets and inventory) to/from CSV files</li>
 *   <li>Manage parental control settings (time restrictions, play sessions)</li>
 *   <li>List available save files</li>
 *   <li>Revive dead pets in save files</li>
 *   <li>Check if current time is allowed for play based on parental settings</li>
 * </ul>
 * <p>
 * The class automatically creates required directories (save_files and settings) duringinitialization.
 * Game saves are stored in CSV format in the group33/VirtualPet/src/main/save_files directory,
 * while parental settings are added to group33/VirtualPet/src/main/settings/parental_settings.csv
 * <p>
 * 
 * @author Team 33 (Dhir, Kostya, Fatima, Anna)
 * @since Winter 2025
 * 
 * 
 */
public class GameSaveManager {
    private static final String SAVE_DIRECTORY = "group33/VirtualPet/src/main/save_files";
    private static final String PARENTAL_SETTINGS_FILE = "group33/VirtualPet/src/main/settings/parental_settings.csv";
    private static final String SETTINGS_DIRECTORY = "group33/VirtualPet/src/main/settings";
    
    // Ensure save and settings directories exist
    static {
        try {
            Files.createDirectories(Paths.get(SAVE_DIRECTORY));
            Files.createDirectories(Paths.get(SETTINGS_DIRECTORY));
        } catch (IOException e) {
            System.err.println("Could not create directories: " + e.getMessage());
        }
    }
    
    /**
     * Save the current game state to a CSV file
     * @param player The player whose game state is to be saved
     * @param filename The name of the save file
     * @throws IOException If there's an error writing the file
     */
    public static void saveGame(Player player, String filename) throws IOException {
        // Ensure filename ends with .csv
        if (!filename.toLowerCase().endsWith(".csv")) {
            filename += ".csv";
        }
        
        File saveFile = new File(SAVE_DIRECTORY, filename);
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(saveFile))) {
            // Write player information
            writer.write("Player Name," + player.getName());
            writer.newLine();
            writer.write("Total Play Time," + player.getTotalPlayTime().toSeconds());
            writer.newLine();
            writer.write("Current Score," + player.getScore());
            writer.newLine();
            writer.write("last time play was used," + player.getLastP());
            writer.newLine();
            writer.write("last time player went to vet," + player.getLastV());
            writer.newLine();
            
            // Write pet information
            writer.write("Pets,Name,Type,Health,Happiness,Fullness,Sleep,Is Sleeping,Is Hungry,Is Angry,Is Dead");
            writer.newLine();
            
            // If there's a current pet
            Pet currentPet = player.getCurrentPet();
            if (currentPet != null) {
                writer.write("Current Pet," + 
                    currentPet.getName() + "," + 
                    currentPet.getType() + "," + 
                    currentPet.getHealth() + "," + 
                    currentPet.getHappiness() + "," + 
                    currentPet.getFullness() + "," + 
                    currentPet.getSleep() + "," + 
                    currentPet.isSleeping() + "," + 
                    currentPet.isHungry() + "," + 
                    currentPet.isAngry() + "," + 
                    currentPet.isDead());
                writer.newLine();
            }
            
            // Write inventory information
            Inventory inventory = player.getInventory();
            
            // Write food items
            writer.write("Food Items,Name,Fullness Value,Quantity");
            writer.newLine();
            inventory.getFoodItems().forEach((foodItem, quantity) -> {
                try {
                    writer.write("Food Item," + 
                        foodItem.getName() + "," + 
                        foodItem.getFullnessValue() + "," + 
                        quantity);
                    writer.newLine();
                } catch (IOException e) {
                    System.err.println("Error writing food item: " + e.getMessage());
                }
            });
            
            // Write gift items
            writer.write("Gift Items,Name,Happiness Value,Quantity");
            writer.newLine();
            inventory.getGiftItems().forEach((giftItem, quantity) -> {
                try {
                    writer.write("Gift Item," + 
                        giftItem.getName() + "," + 
                        giftItem.getHappinessValue() + "," + 
                        quantity);
                    writer.newLine();
                } catch (IOException e) {
                    System.err.println("Error writing gift item: " + e.getMessage());
                }
            });
        }
    }
    
    /**
     * Load a game state from a CSV file
     * @param filename The name of the save file to load
     * @return A new Player object with the loaded game state
     * @throws IOException If there's an error reading the file
     */
    public static Player loadGame(String filename) throws IOException {
        // Ensure filename ends with .csv
        if (!filename.toLowerCase().endsWith(".csv")) {
            filename += ".csv";
        }
        
        File saveFile = new File(SAVE_DIRECTORY, filename);
        Player player = null;
        
        try (BufferedReader reader = new BufferedReader(new FileReader(saveFile))) {
            String line;
            Pet loadedPet = null;
            Inventory inventory = new Inventory();
            
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                
                // Parse player information
                if (parts[0].equals("Player Name")) {
                    player = new Player(parts[1]);
                } else if (parts[0].equals("Total Play Time")) {
                    // Restore play time
                    if (player != null) {
                        player.setTotalPlayTime(Duration.ofSeconds(Long.parseLong(parts[1])));
                    }
                } else if (parts[0].equals("Current Score")) {
                    if (player != null) {
                        player.setScore(Integer.parseInt(parts[1]));
                    }
                }
                if (parts[0].equals("last time play was used"))
                {
                    player.setLastP(Long.parseLong(parts[1]));
                }

                if (parts[0].equals("last time player went to vet"))
                {
                    player.setLastV(Long.parseLong(parts[1]));
                }
                // Parse current pet information
                if (parts[0].equals("Current Pet")) {
                    String petName = parts[1];
                    Pet.PetType petType = Pet.PetType.valueOf(parts[2]);
                    loadedPet = new Pet(petName, petType);
                    
                    // Restore pet statistics
                    loadedPet.setHealth(Integer.parseInt(parts[3]));
                    loadedPet.setHappiness(Integer.parseInt(parts[4]));
                    loadedPet.setFullness(Integer.parseInt(parts[5]));
                    loadedPet.setSleep(Integer.parseInt(parts[6]));
                    
                    // Restore pet states
                    if (Boolean.parseBoolean(parts[7])) loadedPet.sleep();
                    // Other state flags are handled by the Pet class's internal checks

                    // Restore pet states
                    if (Boolean.parseBoolean(parts[7])) loadedPet.sleep();
                    // Add these lines to restore other states:
                    if (Boolean.parseBoolean(parts[10])) {
                        // Force the dead state to be set
                        loadedPet.setHealth(0);
                        loadedPet.checkHealthState(); // You'll need to add this method to Pet class
                    }
                }
                
                // Parse food items
                if (parts[0].equals("Food Item") && player != null) {
                    Inventory.FoodItem foodItem = new Inventory.FoodItem(parts[1], Integer.parseInt(parts[2]));
                    inventory.addFoodItem(foodItem, Integer.parseInt(parts[3]));
                }
                
                // Parse gift items
                if (parts[0].equals("Gift Item") && player != null) {
                    Inventory.GiftItem giftItem = new Inventory.GiftItem(parts[1], Integer.parseInt(parts[2]));
                    inventory.addGiftItem(giftItem, Integer.parseInt(parts[3]));
                }
            }
            
            // Set up the player
            if (player != null) {
                player.setInventory(inventory);
                if (loadedPet != null) {
                    player.adoptPet(loadedPet);
                }
            }
        }
        
        return player;
    }
    
    /**
     * List all available save files
     * @return List of save file names
     */
    public static List<String> listSaveFiles() {
        try {
            return Files.list(Paths.get(SAVE_DIRECTORY))
                .filter(path -> path.toString().toLowerCase().endsWith(".csv"))
                .map(path -> path.getFileName().toString())
                .collect(Collectors.toList());
        } catch (IOException e) {
            System.err.println("Error listing save files: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    /**
     * Save parental settings to a CSV file
     * @param settings The parental settings to save
     * @throws IOException If there's an error writing the file
     */
    public static void saveParentalSettings(ParentalSettings settings) throws IOException {
        File settingsFile = new File(PARENTAL_SETTINGS_FILE);
        // Create parent directories if they don't exist
        if (!settingsFile.getParentFile().exists()) {
            settingsFile.getParentFile().mkdirs();
        }
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(settingsFile))) {
            // Write CSV header
            writer.write("Setting,Value");
            writer.newLine();
            
            // Write time restriction settings
            writer.write("TimeRestrictionEnabled," + settings.isTimeRestrictionEnabled());
            writer.newLine();
            
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
            
            // Handle null start time
            LocalTime startTime = settings.getAllowedStartTime();
            if (startTime != null) {
                writer.write("AllowedStartTime," + startTime.format(timeFormatter));
            } else {
                writer.write("AllowedStartTime,");  // Empty value for null time
            }
            writer.newLine();
            
            // Handle null end time
            LocalTime endTime = settings.getAllowedEndTime();
            if (endTime != null) {
                writer.write("AllowedEndTime," + endTime.format(timeFormatter));
            } else {
                writer.write("AllowedEndTime,");  // Empty value for null time
            }
            writer.newLine();
            
            // Write statistics
            writer.write("TotalPlayTimeSeconds," + settings.getTotalPlayTime().getSeconds());
            writer.newLine();
            writer.write("SessionCount," + settings.getSessionCount());
            writer.newLine();
            
            // Write last session info if available
            if (settings.getLastSessionStart() != null) {
                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
                writer.write("LastSessionStart," + settings.getLastSessionStart().format(dateTimeFormatter));
                writer.newLine();
            }
            
            if (settings.getLastSessionEnd() != null) {
                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
                writer.write("LastSessionEnd," + settings.getLastSessionEnd().format(dateTimeFormatter));
                writer.newLine();
            }
        }
    }
    
    /**
     * Load parental settings from a CSV file
     * @return The loaded parental settings, or a new default settings object if the file doesn't exist
     * @throws IOException If there's an error reading the file
     */
    public static ParentalSettings loadParentalSettings() throws IOException {
        File settingsFile = new File(PARENTAL_SETTINGS_FILE);
        
        if (!settingsFile.exists()) {
            // Return default settings if file doesn't exist
            ParentalSettings defaultSettings = new ParentalSettings();
            // Don't automatically start a new session
            // defaultSettings.startSession();
            return defaultSettings;
        }

        ParentalSettings settings = new ParentalSettings();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(settingsFile))) {
            String line;
            
            // Skip header line
            reader.readLine();
            
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", 2);
                if (parts.length != 2) continue;
                
                String key = parts[0];
                String value = parts[1];
                
                switch (key) {
                    case "TimeRestrictionEnabled":
                        settings.setTimeRestrictionEnabled(Boolean.parseBoolean(value));
                        break;
                    case "AllowedStartTime":
                        settings.setAllowedStartTime(LocalTime.parse(value));
                        break;
                    case "AllowedEndTime":
                        settings.setAllowedEndTime(LocalTime.parse(value));
                        break;
                    case "TotalPlayTimeSeconds":
                        settings.setTotalPlayTime(Duration.ofSeconds(Long.parseLong(value)));
                        break;
                    case "SessionCount":
                        // We need to restore session count manually since we're not using addSessionTime
                        int sessionCount = Integer.parseInt(value);
                        for (int i = 0; i < sessionCount; i++) {
                            settings.addSessionTime(Duration.ZERO);  // Add zero duration to increment the counter
                        }
                        break;
                case "LastSessionStart":
                    // Parse the timestamp and set the lastSessionStart field using reflection
                    try {
                        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
                        LocalDateTime startTime = LocalDateTime.parse(value, dateTimeFormatter);
                        
                        // Use reflection to set the lastSessionStart field
                        java.lang.reflect.Field field = ParentalSettings.class.getDeclaredField("lastSessionStart");
                        field.setAccessible(true);
                        field.set(settings, startTime);
                    } catch (Exception e) {
                        System.err.println("Error setting lastSessionStart: " + e.getMessage());
                    }
                    break;
                case "LastSessionEnd":
                    // Parse the timestamp and set the lastSessionEnd field using reflection
                    try {
                        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
                        LocalDateTime endTime = LocalDateTime.parse(value, dateTimeFormatter);
                        
                        // Use reflection to set the lastSessionEnd field
                        java.lang.reflect.Field field = ParentalSettings.class.getDeclaredField("lastSessionEnd");
                        field.setAccessible(true);
                        field.set(settings, endTime);
                    } catch (Exception e) {
                        System.err.println("Error setting lastSessionEnd: " + e.getMessage());
                    }
                    break;
                }
            }
        } catch (Exception e) {
            System.err.println("Error parsing parental settings: " + e.getMessage());
            // Start a new session not used anymore
            // settings.startSession();
            return settings;
        }
        
        // Start a new session not used anymore
        // settings.startSession();
        return settings;
    }
    
    /**
     * Revive a pet in a save file
     * @param filename The name of the save file
     * @return true if a pet was successfully revived, false otherwise
     * @throws IOException If there's an error reading or writing the file
     */
    public static boolean revivePet(String filename) throws IOException {
        // Load the player and pet from the save file
        Player player = loadGame(filename);
        
        if (player == null || player.getCurrentPet() == null) {
            return false;
        }
        
        // Revive the pet
        Pet pet = player.getCurrentPet();
        pet.revive();
        
        // Save the updated game state
        saveGame(player, filename);
        
        return true;
    }
    
    /**
     * Check if the current time is allowed for play based on parental settings
     * @param screenType The type of screen being checked (e.g., "playing", "main_menu")
     * @return true if play is allowed, false otherwise
     */
    public static boolean isPlayTimeAllowed(String screenType) {
        try {
            ParentalSettings settings = loadParentalSettings();
            return settings.isTimeAllowed(screenType);
        } catch (IOException e) {
            System.err.println("Error checking play time restrictions: " + e.getMessage());
            // Default to allowing play if there's an error
            return true;
        }
    }
    
    /**
     * Backward compatibility method
     */
    public static boolean isPlayTimeAllowed() {
        return isPlayTimeAllowed("playing");
    }
    
    /**
     * Record a play session in the parental settings
     * @param sessionDuration The duration of the play session
     */
    public static void recordPlaySession(Duration sessionDuration) {
        try {
            ParentalSettings settings = loadParentalSettings();
            settings.addSessionTime(sessionDuration);
            // settings.endSession();
            saveParentalSettings(settings);
        } catch (IOException e) {
            System.err.println("Error recording play session: " + e.getMessage());
        }
    }
    
    /**
     * Start a new play session
     */
    public static void startPlaySession() {
        try {
            ParentalSettings settings = loadParentalSettings();
            settings.startSession();
            saveParentalSettings(settings);
        } catch (IOException e) {
            System.err.println("Error starting play session: " + e.getMessage());
        }
    }
    
    /**
     * End the current play session
     */
    public static void endPlaySession() {
        try {
            ParentalSettings settings = loadParentalSettings();
            settings.endSession();
            saveParentalSettings(settings);
        } catch (IOException e) {
            System.err.println("Error ending play session: " + e.getMessage());
        }
    }
}