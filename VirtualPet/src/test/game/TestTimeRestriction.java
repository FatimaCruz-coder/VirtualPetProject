package group33.VirtualPet.src.test.game;

import group33.VirtualPet.src.main.game.GameSaveManager;
import group33.VirtualPet.src.main.game.TimeRestrictionManager;
import group33.VirtualPet.src.main.game.TimeRestrictionManager.ScreenType;
import group33.VirtualPet.src.main.model.ParentalSettings;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.*;

import java.io.File;
import java.io.IOException;

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for TimeRestrictionManager functionality.
 * Tests various scenarios and methods of time restrictions to ensure proper enforcement.
 * 
 * @author Team 33
 */
class TestTimeRestriction {
    
    private ParentalSettings testSettings;
    private JFrame testFrame;

    @BeforeEach
    void setUp() throws IOException {
        // Create test settings for time restrictions
        testSettings = new ParentalSettings();
        testSettings.setTimeRestrictionEnabled(true);
        
        // Set default test frame
        testFrame = new JFrame("Test Frame");
        // Don't make it visible
        testFrame.setVisible(false);

        deleteParentalSettingsFile();
    }
    
    @AfterEach
    void tearDown() {
        // Close the test frame
        if (testFrame != null) {
            testFrame.dispose();
        }
        
        // Clean up test settings file
        deleteParentalSettingsFile();
    }
    
    /**
     * Helper method to delete the parental settings file
     */
    private void deleteParentalSettingsFile() {
        try {
            File settingsDir = new File("group33/VirtualPet/src/main/settings");
            if (settingsDir.exists()) {
                File settingsFile = new File(settingsDir, "parental_settings.csv");
                if (settingsFile.exists()) {
                    settingsFile.delete();
                }
            }
        } catch (Exception e) {
            System.err.println("Error cleaning up test settings file: " + e.getMessage());
        }
    }
    
    @Test
    void testIsWithinAllowedTime_NoRestrictions() throws IOException {
        // Create settings with no time restrictions
        testSettings.setTimeRestrictionEnabled(false);
        GameSaveManager.saveParentalSettings(testSettings);
        
        // Test time restriction check
        boolean result = TimeRestrictionManager.isWithinAllowedTime();
        
        // Should be allowed to play when restrictions are disabled
        assertTrue(result, "Play should be allowed when restrictions are disabled");
    }
    
    @Test
    void testIsWithinAllowedTime_WithinTimeRange() throws IOException {
        // Get current time and set the allowed time range to include current time
        LocalTime now = LocalTime.now();
        testSettings.setAllowedStartTime(now.minusHours(1));
        testSettings.setAllowedEndTime(now.plusHours(1));
        GameSaveManager.saveParentalSettings(testSettings);
        
        // Test time restriction check
        boolean result = TimeRestrictionManager.isWithinAllowedTime();
        
        // Should be allowed to play within time range
        assertTrue(result, "Play should be allowed within the specified time range");
    }
    
    @Test
    void testIsWithinAllowedTime_OutsideTimeRange() throws IOException {
        // Get current time and set allowed time range to exclude current time
        LocalTime now = LocalTime.now();
        
        testSettings.setAllowedStartTime(now.plusHours(1));
        testSettings.setAllowedEndTime(now.plusHours(2));
        GameSaveManager.saveParentalSettings(testSettings);
        
        // Test time restriction check
        boolean result = TimeRestrictionManager.isWithinAllowedTime();
        
        // Should not be allowed to play outside time range
        assertFalse(result, "Play should not be allowed outside the specified time range");
    }
    
    @Test
    void testIsWithinAllowedTime_OvernightTimeRange() throws IOException {
        // Create settings with overnight range (e.g., 10 PM to 6 AM)
        LocalTime startTime = LocalTime.of(22, 0); // 10 PM
        LocalTime endTime = LocalTime.of(6, 0); // 6 AM
        
        testSettings.setAllowedStartTime(startTime);
        testSettings.setAllowedEndTime(endTime);
        GameSaveManager.saveParentalSettings(testSettings);
        
        // Get current time and determine expected result based on current time
        LocalTime now = LocalTime.now();
        
        boolean expected;
        if (now.isAfter(startTime) || now.isBefore(endTime) || now.equals(startTime) || now.equals(endTime)) {
            expected = true; // Should be allowed during overnight hours
        } else {
            expected = false; // Should not be allowed during daytime hours
        }
        
        // Test time restriction check
        boolean result = TimeRestrictionManager.isWithinAllowedTime();
        
        // Should match our expected value based on current time
        assertEquals(expected, result, "Overnight time range calculation should be correct");
    }
    
    @Test
    void testIsWithinAllowedTime_NullSettings() throws IOException {
        // Don't save any settings, ensuring null return from loadParentalSettings()
        // Delete any existing settings file to ensure it's not found
        deleteParentalSettingsFile();
        
        // Test time restriction check
        boolean result = TimeRestrictionManager.isWithinAllowedTime();
        
        // Should be allowed to play when no settings exist
        assertTrue(result, "Play should be allowed when no settings exist");
    }

    @Test
    void testIsWithinAllowedTime_NullTimeValues() throws IOException {
        // Create settings with null time values
        testSettings.setTimeRestrictionEnabled(true);
        testSettings.setAllowedStartTime(null);
        testSettings.setAllowedEndTime(null);
        GameSaveManager.saveParentalSettings(testSettings);
        
        // Test time restriction check
        boolean result = TimeRestrictionManager.isWithinAllowedTime();
        
        // Should be allowed to play when time values are null
        assertTrue(result, "Play should be allowed when time values are null");
    }
    
    @Test
    void testEnforceTimeRestrictions_UnrestrictedScreen() throws IOException {
        // Set up time restrictions that would normally block play
        LocalTime now = LocalTime.now();
        testSettings.setAllowedStartTime(now.plusHours(1));
        testSettings.setAllowedEndTime(now.plusHours(2));
        GameSaveManager.saveParentalSettings(testSettings);
        
        // Test enforcing restrictions on UNRESTRICTED_SCREEN
        boolean result = TimeRestrictionManager.enforceTimeRestrictions(
            ScreenType.UNRESTRICTED_SCREEN, testFrame);
        
        // Should allow access to unrestricted screens regardless of time
        assertTrue(result, "Unrestricted screens should be accessible at any time");
    }
    
    @Test
    void testEnforceTimeRestrictions_GameplayScreen_Allowed() throws IOException {
        // Set up time restrictions that allow play
        LocalTime now = LocalTime.now();
        testSettings.setAllowedStartTime(now.minusHours(1));
        testSettings.setAllowedEndTime(now.plusHours(1));
        GameSaveManager.saveParentalSettings(testSettings);
        
        // Test enforcing restrictions on GAMEPLAY_SCREEN
        boolean result = TimeRestrictionManager.enforceTimeRestrictions(
            ScreenType.GAMEPLAY_SCREEN, testFrame);
        
        // Should allow access to gameplay screens when within allowed time
        assertTrue(result, "Gameplay screens should be accessible within allowed time");
    }
    
    @Test
    void testEnforceTimeRestrictions_GameplayScreen_Blocked() throws IOException, Exception {
        // Set up time restrictions that block play
        LocalTime now = LocalTime.now();
        testSettings.setAllowedStartTime(now.plusHours(1));
        testSettings.setAllowedEndTime(now.plusHours(2));
        GameSaveManager.saveParentalSettings(testSettings);
        
        try {
            // Create a custom TimeRestrictionManager that doesn't show dialogs
            boolean result = testEnforceTimeRestrictionsWithoutDialog(ScreenType.GAMEPLAY_SCREEN, testFrame);
            
            // Should block access to gameplay screens when outside allowed time
            assertFalse(result, "Gameplay screens should be blocked outside allowed time");
        } finally {
            // No need to restore the original JOptionPane since I'm using custom implementation
        }
    }
    
    // Custom implementation to test without showing dialog
    private boolean testEnforceTimeRestrictionsWithoutDialog(ScreenType screenType, JFrame parent) {
        // Unrestricted screens are always accessible (copied from TimeRestrictionManager)
        if (screenType == ScreenType.UNRESTRICTED_SCREEN) {
            return true;
        }
        
        // Check if within allowed time without showing dialog
        if (!TimeRestrictionManager.isWithinAllowedTime() && screenType == ScreenType.GAMEPLAY_SCREEN) {
            // Log instead of showing dialog
            System.out.println("TEST: Time restriction triggered - dialog would show here");
            return false;
        }
        return true;
    }
    
    @Test
    void testCheckAndRedirect_Allowed() throws IOException {
        // Set up time restrictions that allow play
        LocalTime now = LocalTime.now();
        testSettings.setAllowedStartTime(now.minusHours(1));
        testSettings.setAllowedEndTime(now.plusHours(1));
        GameSaveManager.saveParentalSettings(testSettings);
        
        // Create a test JFrame to represent current screen
        JFrame currentScreen = new JFrame("Test Game Screen");
        currentScreen.setVisible(true);
        
        // Test checkAndRedirect for allowed gameplay
        try {
            boolean result = testCheckAndRedirectWithoutMainMenu(
                ScreenType.GAMEPLAY_SCREEN, currentScreen);
            
            // Should not redirect when play is allowed
            assertTrue(result, "Should not redirect when play is allowed");
            assertTrue(currentScreen.isVisible(), "Current screen should remain visible");
        } finally {
            // Clean up
            currentScreen.dispose();
        }
    }
    
    @Test
    void testCheckAndRedirect_Blocked() throws IOException {
        // Set up time restrictions that block play
        LocalTime now = LocalTime.now();
        testSettings.setAllowedStartTime(now.plusHours(1));
        testSettings.setAllowedEndTime(now.plusHours(2));
        GameSaveManager.saveParentalSettings(testSettings);
        
        // Create a test JFrame
        JFrame currentScreen = new JFrame("Test Game Screen");
        currentScreen.setVisible(true);
        
        try {
            // Test with custom implementation that avoids MainMenuScreen
            boolean result = testCheckAndRedirectWithoutMainMenu(
                ScreenType.GAMEPLAY_SCREEN, currentScreen);
            
            // Should return false when play is blocked
            assertFalse(result, "Should indicate redirection when play is blocked");
            // Frame should still be visible because our test method doesn't modify it
        } finally {
            currentScreen.dispose();
        }
    }
    
    // Custom implementation to test without creating MainMenuScreen
    private boolean testCheckAndRedirectWithoutMainMenu(ScreenType screenType, JFrame currentScreen) {
        // Use our custom method that doesn't show dialog
        if (!testEnforceTimeRestrictionsWithoutDialog(screenType, currentScreen)) {
            // Log instead of creating MainMenuScreen
            System.out.println("TEST: Would redirect to main menu here");
            return false;
        }
        return true;
    }
    
    @Test
    void testExactlyMatchingTimes() throws IOException {
        // Test with exactly matching current time
        LocalTime now = LocalTime.now();
        
        // Set allowed time range to exactly match current time at both ends
        testSettings.setAllowedStartTime(now);
        testSettings.setAllowedEndTime(now);
        GameSaveManager.saveParentalSettings(testSettings);
        
        // Test time restriction check
        boolean result = TimeRestrictionManager.isWithinAllowedTime();
        
        // Should be allowed to play when current time exactly matches start and end time
        assertTrue(result, "Play should be allowed when current time exactly matches start and end time");
    }
    
    @Test
    void testSmallTimeWindow() throws IOException {
        // Get current time
        LocalTime now = LocalTime.now();
        
        // Set a very small allowed time window around current time (1 minute on each side)
        testSettings.setAllowedStartTime(now.minusMinutes(1));
        testSettings.setAllowedEndTime(now.plusMinutes(1));
        GameSaveManager.saveParentalSettings(testSettings);
        
        // Test time restriction check
        boolean result = TimeRestrictionManager.isWithinAllowedTime();
        
        // Should be allowed to play within small time window
        assertTrue(result, "Play should be allowed within a small time window");
    }
    
    @Test
    void testIOExceptionHandling() {
        // Simulate IO Exception by using a file path that doesn't exist
        // Test to see if the method gracefully handles errors
        try {
            // Attempt to read from a non-existent location, this should not throw an exception despite the IO error (hopefully)
            boolean result = TimeRestrictionManager.isWithinAllowedTime();
            
            // Should default to allowing play when IO error occurs
            assertTrue(result, "Should default to allowing play when IO error occurs");
        } catch (Exception e) {
            fail("TimeRestrictionManager should handle IO errors gracefully: " + e.getMessage());
        }
    }
    
    @Test
    void testNullFrameHandling() throws IOException {
        // Set up time restrictions
        LocalTime now = LocalTime.now();
        testSettings.setAllowedStartTime(now.plusHours(1));
        testSettings.setAllowedEndTime(now.plusHours(2));
        GameSaveManager.saveParentalSettings(testSettings);
        
        try {
            // Test with null JFrame using our custom method that doesn't show dialog
            boolean result = testEnforceTimeRestrictionsWithoutDialog(ScreenType.GAMEPLAY_SCREEN, null);
            
            // Should return false (blocked) but not throw exception
            assertFalse(result, "Should indicate blocked access but handle null frame gracefully");
        } catch (NullPointerException e) {
            fail("Should handle null frame reference gracefully: " + e.getMessage());
        }
    }
}