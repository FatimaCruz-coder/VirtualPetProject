package group33.VirtualPet.src.test.game;

import group33.VirtualPet.src.main.model.battlePets;
import group33.VirtualPet.src.main.model.mathBattle;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
/**
 * Test class for MathBattle functionality.
 * Tests various scenarios and methods in the MathBattle class to ensure proper enforcement.
 *
 * @author Team 33
 */


class mathBattleTest {


    @Test
    void getOpponent() {

        //stats for division
        int[][] division = {
                {1,1,1},
                {8,2,4},
                {10,5,2},
                {30,3,10},
                {9,3,3},
                {50,25,2},
                {4,4,1},
                {12,2,6},
                {20,4,5},
                {15,5,3}
        };

        //create a mathBattle of penguin level
        mathBattle battle = new mathBattle("penguin");

        //get the stats from opponent
        int[][] stat = battle.getOpponent().getStat();

        //assert opponent stats were saved correctly
        assertEquals("penguin", battle.getOpponent().getType());
        assertEquals(100, battle.getOpponent().getHealth());
        assertEquals(division[2][2], stat[2][2]);
    }

    @Test
    void pLose() {
        mathBattle battle = new mathBattle("penguin");

        //health of player after pLose()
        int health = battle.getpHealth() - 25;

        battle.pLose();

        assertEquals(health, battle.getpHealth());
    }

    @Test
    void oLose() {
        mathBattle battle = new mathBattle("penguin");

        //health of opponent after oLose()
        int health = battle.getOpponent().getHealth() - 25;

        battle.oLose();

        assertEquals(health, battle.getOpponent().getHealth());
    }
}