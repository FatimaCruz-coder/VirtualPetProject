package group33.VirtualPet.src.main.game;

import group33.VirtualPet.src.main.model.ParentalSettings;

import group33.VirtualPet.src.main.ui.MainMenuScreen;

import javax.swing.*;
import java.io.IOException;
import java.time.LocalTime;

/**
 * Manager class for handling parental time restrictions across the application.
 * This class checks if the current time is within allowed play hours and provides
 * methods to enforce time restrictions in gameplay screens while allowing access
 * to non-gameplay screens like MainMenu, Tutorial, and Parental Controls.
 *
 * @author Team 33 (Dhir, Kostya, Fatima, Anna)
 * @since Winter 2025
 */
public class TimeRestrictionManager {
    // Screen types that should be exempt from time restrictions
    public enum ScreenType {
        GAMEPLAY_SCREEN,  // Screens that should enforce time restrictions
        UNRESTRICTED_SCREEN  // Screens that should be accessible at all times
    }
    
    /**
     * Checks if the current time is within the allowed play hours.
     * 
     * @return true if time restrictions are satisfied (player can play), false otherwise
     */
    public static boolean isWithinAllowedTime() {
        try {
            ParentalSettings settings = GameSaveManager.loadParentalSettings();
            
            if (settings == null || !settings.isTimeRestrictionEnabled()) {
                // No time restrictions if settings don't exist or restrictions are disabled
                return true;
            }
            
            LocalTime currentTime = LocalTime.now();
            LocalTime startTime = settings.getAllowedStartTime();
            LocalTime endTime = settings.getAllowedEndTime();
            
            if (startTime == null || endTime == null) {
                // No time restrictions if times are not set
                return true;
            }
            
            // Check if current time is within allowed range
            if (startTime.isBefore(endTime)) {
                // Simple case: start time is before end time (same day)
                return !currentTime.isBefore(startTime) && !currentTime.isAfter(endTime);
            } else {
                // Complex case: start time is after end time (overnight)
                return !currentTime.isBefore(startTime) || !currentTime.isAfter(endTime);
            }
            
        } catch (IOException e) {
            System.err.println("Error checking time restrictions: " + e.getMessage());
            // Default to allowing play if there's an error
            return true;
        }
    }
    
    /**
     * Enforces time restrictions for the given screen type.
     * Shows an alert dialog and returns false if restrictions are enabled and time is outside allowed hours.
     * 
     * @param screenType The type of screen attempting to be accessed
     * @param parent The parent component for dialog display
     * @return true if the screen can be accessed, false otherwise
     */
    public static boolean enforceTimeRestrictions(ScreenType screenType, JFrame parent) {
        // Unrestricted screens are always accessible
        if (screenType == ScreenType.UNRESTRICTED_SCREEN) {
            return true;
        }
        
        // For gameplay screens, check if within allowed time
        if (!isWithinAllowedTime() && screenType == ScreenType.GAMEPLAY_SCREEN) {
            JOptionPane.showMessageDialog(parent,
                "You are not allowed to play at this time.\n" +
                "Please try again during your allowed play hours.",
                "Parental Controls",
                JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }
    
    /**
     * Updates screen according to time restriction result. If restrictions prevent access,
     * redirects to main menu. Otherwise does nothing, allowing screen to continue loading.
     * 
     * @param screenType Type of screen being loaded
     * @param currentScreen The current JFrame that's attempting to load
     * @return true if screen can proceed, false if redirected to main menu
     */
    public static boolean checkAndRedirect(ScreenType screenType, JFrame currentScreen) {
        if (!enforceTimeRestrictions(screenType, currentScreen)) {
            // Time restriction failed, redirect to main menu
            currentScreen.setVisible(false);
            MainMenuScreen mainMenu = new MainMenuScreen();
            mainMenu.setVisible(true);
            currentScreen.dispose();
            return false;
        }
        return true;
    }
}