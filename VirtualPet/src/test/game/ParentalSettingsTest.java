package group33.VirtualPet.src.test.game;

import group33.VirtualPet.src.main.model.ParentalSettings;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;
/**
 * Test class for ParentalSettings functionality.
 * Tests various scenarios and methods in the ParentalSettings class to ensure proper enforcement.
 *
 * @author Team 33
 */

class ParentalSettingsTest {

    @Test
    void isTimeRestrictionEnabled() {
        //test if isTimeRestrictionEnabled method works correctly
        ParentalSettings settings = new ParentalSettings();
        settings.setTimeRestrictionEnabled(true);
        assertTrue(settings.isTimeRestrictionEnabled());
    }

    @Test
    void isTimeRestrictionNotEnabled() {
        //test isTimeRestrictionEnabled method edge case where it is not enabled
        ParentalSettings settings = new ParentalSettings();
        boolean result = false;
        settings.setTimeRestrictionEnabled(false);
        assertEquals(result, settings.isTimeRestrictionEnabled());
    }

    @Test
    void startSession() {
        //test if start session method is functioning
        ParentalSettings settings = new ParentalSettings();

        settings.startSession();

        //get current time
        LocalDateTime now = LocalDateTime.now();

        assertEquals(now,settings.getLastSessionStart());
    }

    @Test
    void addSessionTime() {
        //check if add session logic functions correctly
        ParentalSettings settings = new ParentalSettings();

        //create a duration
        Duration duration = Duration.between(LocalDateTime.now(), LocalDateTime.now());

        //add it to session
        settings.addSessionTime(duration);

        //check if it correctly added
        assertEquals(duration,settings.getTotalPlayTime());
        assertEquals(1,settings.getSessionCount());
    }



    @Test
    void resetPlayTimeStatistics() {
        //check if reset play statistics method functions correctly
        ParentalSettings settings = new ParentalSettings();

        //create a duration and add it to session time
        Duration duration = Duration.between(LocalDateTime.now(), LocalDateTime.now());
        settings.addSessionTime(duration);

        //reset the session time
        settings.resetPlayTimeStatistics();

        //check for correct results
        assertEquals(Duration.ZERO,settings.getTotalPlayTime());
        assertEquals(0,settings.getSessionCount());
    }

    @Test
    void isTimeAllowed() {
        ParentalSettings settings = new ParentalSettings();

        //isTimeAllowed should always be true if time restriction is not enabled
        settings.setTimeRestrictionEnabled(false);

        assertTrue(settings.isTimeAllowed("play"));
    }

    @Test
    void isTimeAllowedSameTime() {
        ParentalSettings settings = new ParentalSettings();

        LocalTime now = LocalTime.now();

        //edge case if allowed start and end time are the same
        settings.setAllowedStartTime(now);
        settings.setAllowedEndTime(now);

        assertTrue(settings.isTimeAllowed("play"));

    }

}