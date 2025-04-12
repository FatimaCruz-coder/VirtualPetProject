package group33.VirtualPet.src.main.model;

import java.time.LocalDateTime;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a player in the Virtual Pet game, tracking their progress, pets, and game statistics.
 * <p>
 * The Player class manages:
 * <ul>
 *   <li>Player identity (name)</li>
 *   <li>Game score and points system</li>
 *   <li>Current pet ownership</li>
 *   <li>Inventory of food and gift items</li>
 *   <li>Play time tracking and session management</li>
 *   <li>Timestamps for last interactions (playing with pet, vet visits)</li>
 * </ul>
 * 
 * @author Team 33 (Dhir, Kostya, Fatima, Anna)
 * @since Winter 2025
 */
public class Player {
    private String name;                // The player's name
    private int score;                  // Current game score
    private Pet currentPet;             // Currently owned pet (null if none)
    private Inventory inventory;        // Collection of food and gift items
    
    // Play time tracking
    private LocalDateTime sessionStartTime;  // When current session started
    private Duration totalPlayTime;          // Cumulative play time across all sessions
    private List<Duration> sessionDurations; // History of all session durations

    private long lastP;  // Timestamp of last play interaction with pet (in milliseconds)
    private long lastV;  // Timestamp of last vet visit (in milliseconds)
    private int cool = 300000;

    /**
     * Creates a new Player with default initial state.
     * @param name The player's name (cannot be null or empty)
     */
    public Player(String name) {
        this.name = name;
        this.score = 0;
        this.inventory = new Inventory();
        this.totalPlayTime = Duration.ZERO;
        this.sessionDurations = new ArrayList<>();
        // this.lastP = 0;
        // this.lastV = 0;
        this.lastV = System.currentTimeMillis() - cool;
        this.lastP = System.currentTimeMillis() - cool;
    }
    
    /**
     * Adopts a pet, making it the player's current companion.
     * @param pet The pet to adopt (null to abandon current pet)
     */
    public void adoptPet(Pet pet) {
        this.currentPet = pet;
    }
    
    /**
     * Increases the player's score by specified points.
     * @param points The positive number of points to add
     */
    public void incrementScore(int points) {
        if (points < 0) return; // ignore negative input
        if (score > Integer.MAX_VALUE - points) {
            score = Integer.MAX_VALUE; // cap at max value
        } else {
            score += points;
        }
    }    
    /**
     * Decreases the player's score by specified points (never goes below 0).
     * @param points The positive number of points to subtract
     */
    public void decrementScore(int points) {
        score = Math.max(0, score - points);
    }
    
    /**
     * Starts a new play session by recording the current time.
     * Must be paired with endSession() to track duration.
     */
    public void startSession() {
        sessionStartTime = LocalDateTime.now();
    }
    
    /**
     * Ends the current play session and updates total play time.
     * Calculates duration since startSession() was called.
     */
    public void endSession() {
        if (sessionStartTime != null) {
            Duration sessionDuration = Duration.between(sessionStartTime, LocalDateTime.now());
            totalPlayTime = totalPlayTime.plus(sessionDuration);
            sessionDurations.add(sessionDuration);
            sessionStartTime = null;
        }
    }

    /**
     * Sets the total accumulated play time.
     * @param duration The new total play time (cannot be null)
     */
    public void setTotalPlayTime(Duration duration) {
        this.totalPlayTime = duration;
    }

    /**
     * Sets the player's score directly.
     * @param score The new score value (will not be lower than 0 and is (clamped between 0 and Integer.MAX_VALUE)
     */
    public void setScore(int score) {
        if (score < 0) {
            this.score = 0;
        } else {
            this.score = Math.min(score, Integer.MAX_VALUE);
        }
    }
    /**
     * Replaces the player's inventory with a new one.
     * @param inventory The new inventory (cannot be null)
     */
    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    /**
     * @return The total accumulated play time across all sessions
     */
    public Duration getTotalPlayTime() {
        return totalPlayTime;
    }
    
    /**
     * Calculates average duration of all play sessions.
     * @return Average session length in seconds, or 0 if no sessions recorded
     */
    public double getAveragePlayTime() {
        if (sessionDurations.isEmpty()) return 0;
        
        return sessionDurations.stream()
            .mapToLong(Duration::toSeconds)
            .average()
            .orElse(0);
    }
    
    /**
     * Resets all play time statistics (total and session history).
     */
    public void resetPlayTimeStatistics() {
        totalPlayTime = Duration.ZERO;
        sessionDurations.clear();
    }
    
    /**
     * @return The player's inventory of items
     */
    public Inventory getInventory() {
        return inventory;
    }
    
    // Basic getters and setters
    
    /**
     * @return The player's name
     */
    public String getName() { return name; }
    
    /**
     * @return The current game score (clamped to Integer.MAX_VALUE)
     */
    public int getScore() { return Math.min(score, Integer.MAX_VALUE);
    }
    
    /**
     * @return The currently owned pet, or null if none
     */
    public Pet getCurrentPet() { return currentPet; }

    /**
     * Sets timestamp of last play interaction with pet.
     * @param last Timestamp in milliseconds since epoch
     */
    public void setLastP(long last) {
        this.lastP = last;
    }

    /**
     * @return Timestamp of last play interaction with pet in milliseconds since epoch
     */
    public long getLastP() {
        return lastP;
    }

    /**
     * @return Timestamp of last vet visit in milliseconds since epoch
     */
    public long getLastV() {
        return lastV;
    }

    /**
     * Sets timestamp of last vet visit.
     * @param last Timestamp in milliseconds since epoch
     */
    public void setLastV(long last) {
        this.lastV = last;
    }
}