package group33.VirtualPet.src.main.model;

import java.io.Serializable;
import java.time.Duration;
import java.time.LocalTime;
import java.time.LocalDateTime;

/**
 * Manages parental control settings and play time tracking for the Virtual Pet game.
 * Implements Serializable to allow saving and loading of settings.
 * 
 * @author Team 33 (Dhir, Kostya, Fatima, Anna)
 * @since Winter 2025
 * 
 */
public class ParentalSettings implements Serializable {
    private static final long serialVersionUID = 1L;
    
    /** Flag indicating whether time restrictions are enabled */
    private boolean timeRestrictionEnabled;
    
    /** The earliest allowed play time (inclusive) */
    private LocalTime allowedStartTime;
    
    /** The latest allowed play time (inclusive) */
    private LocalTime allowedEndTime;
    
    /** Total accumulated play time across all sessions */
    private Duration totalPlayTime;
    
    /** Total time used for calculating average session duration */
    private Duration totalSessionTime;
    
    /** Count of completed play sessions */
    private int sessionCount;
    
    /** Timestamp when the current session started */
    private LocalDateTime lastSessionStart;
    
    /** Timestamp when the last session ended */
    private LocalDateTime lastSessionEnd;
    
    /**
     * Constructs default parental settings with:
     * - Time restrictions disabled
     * - Default play window: current time to +2 hours
     * - All time counters zeroed
     */
    public ParentalSettings() {
        LocalTime currentTime = LocalTime.now();
        timeRestrictionEnabled = false;
        allowedStartTime = currentTime;
        allowedEndTime = currentTime.plusHours(2);
        if (allowedEndTime.isBefore(currentTime)) {
            allowedEndTime = LocalTime.of(23, 59);
        }
        totalPlayTime = Duration.ZERO;
        totalSessionTime = Duration.ZERO;
        sessionCount = 0;
        lastSessionStart = null;
        lastSessionEnd = null;
    }
    
    /**
     * Checks if time restrictions are enabled.
     * @return true if time restrictions are active
     */
    public boolean isTimeRestrictionEnabled() {
        return timeRestrictionEnabled;
    }
    
    /**
     * Enables or disables time restrictions.
     * @param timeRestrictionEnabled true to enable restrictions
     */
    public void setTimeRestrictionEnabled(boolean timeRestrictionEnabled) {
        this.timeRestrictionEnabled = timeRestrictionEnabled;
    }
    
    /**
     * Gets the allowed start time for play sessions.
     * @return The earliest allowed play time
     */
    public LocalTime getAllowedStartTime() {
        return allowedStartTime;
    }
    
    /**
     * Sets the allowed start time for play sessions.
     * @param allowedStartTime The new start time
     */
    public void setAllowedStartTime(LocalTime allowedStartTime) {
        this.allowedStartTime = allowedStartTime;
    }
    
    /**
     * Gets the allowed end time for play sessions.
     * @return The latest allowed play time
     */
    public LocalTime getAllowedEndTime() {
        return allowedEndTime;
    }
    
    /**
     * Sets the allowed end time for play sessions.
     * @param allowedEndTime The new end time
     */
    public void setAllowedEndTime(LocalTime allowedEndTime) {
        this.allowedEndTime = allowedEndTime;
    }
    
    /**
     * Gets the total accumulated play time.
     * @return The sum of all play sessions
     */
    public Duration getTotalPlayTime() {
        return totalPlayTime;
    }
    
    /**
     * Sets the total play time (used when loading saved settings).
     * @param totalPlayTime The new total play time
     */
    public void setTotalPlayTime(Duration totalPlayTime) {
        this.totalPlayTime = totalPlayTime;
    }
    
    /**
     * Starts a new play session by recording the current time.
     */
    public void startSession() {
        lastSessionStart = LocalDateTime.now();
    }
    
    /**
     * Ends the current play session and updates statistics.
     * Calculates session duration and adds it to totals.
     */
    public void endSession() {
        if (lastSessionStart != null) {
            lastSessionEnd = LocalDateTime.now();
            Duration sessionDuration = Duration.between(lastSessionStart, lastSessionEnd);
            addSessionTime(sessionDuration);
            lastSessionStart = null;
        }
    }
    
    /**
     * Adds a session duration to the accumulated totals.
     * Filters out sessions longer than 24 hours as invalid.
     * @param sessionTime The duration to add
     */
    public void addSessionTime(Duration sessionTime) {
        if (totalPlayTime == null) totalPlayTime = Duration.ZERO;
        if (totalSessionTime == null) totalSessionTime = Duration.ZERO;
        
        if (sessionTime.toHours() <= 24) {
            totalPlayTime = totalPlayTime.plus(sessionTime);
            totalSessionTime = totalSessionTime.plus(sessionTime);
            sessionCount++;
        }
    }
    
    /**
     * Calculates the average session duration.
     * @return Average duration or ZERO if no sessions recorded
     */
    public Duration getAverageSessionTime() {
        return sessionCount == 0 ? Duration.ZERO : totalSessionTime.dividedBy(sessionCount);
    }
    
    /**
     * Gets the total number of play sessions.
     * @return Count of completed sessions
     */
    public int getSessionCount() {
        return sessionCount;
    }
    
    /**
     * Resets all play time statistics to zero.
     */
    public void resetPlayTimeStatistics() {
        totalPlayTime = Duration.ZERO;
        totalSessionTime = Duration.ZERO;
        sessionCount = 0;
    }
    
    /**
     * Checks if the current time is allowed for play based on settings.
     * @param screenType The context ("playing" or other screens)
     * @return true if playing is allowed, false if restricted
     */
    public boolean isTimeAllowed(String screenType) {
        if (!timeRestrictionEnabled || !"playing".equals(screenType)) {
            return true;
        }
        
        LocalTime currentTime = LocalTime.now();
        if (allowedEndTime.isBefore(allowedStartTime)) {
            return !currentTime.isBefore(allowedStartTime) || !currentTime.isAfter(allowedEndTime);
        } else {
            return !currentTime.isBefore(allowedStartTime) && !currentTime.isAfter(allowedEndTime);
        }
    }
    
    /**
     * Gets the start time of the last session.
     * @return Session start timestamp or null if no active session
     */
    public LocalDateTime getLastSessionStart() {
        return lastSessionStart;
    }
    
    /**
     * Gets the end time of the last completed session.
     * @return Session end timestamp or null if no completed sessions
     */
    public LocalDateTime getLastSessionEnd() {
        return lastSessionEnd;
    }
    
    /**
     * Calculates the duration of the current active session.
     * @return Current session duration or ZERO if no active session
     */
    public Duration getCurrentSessionDuration() {
        return lastSessionStart == null ? Duration.ZERO : Duration.between(lastSessionStart, LocalDateTime.now());
    }
}