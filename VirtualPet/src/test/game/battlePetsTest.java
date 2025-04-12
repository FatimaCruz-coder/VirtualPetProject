package group33.VirtualPet.src.test.game;

import group33.VirtualPet.src.main.model.battlePets;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;

import static org.junit.jupiter.api.Assertions.*;
/**
 * Test class for battlePets functionality.
 * Tests various scenarios and methods in the battlePets class to ensure proper enforcement.
 *
 * @author Team 33
 */
class battlePetsTest {


    @Test
    void testType() {
        int[][] stat = new int[10][3];
        battlePets pet = new battlePets("Frog",100,stat);

        //assert battlePet was properly created
        assertEquals("Frog", pet.getType());
    }



    @Test
    void testHealth() {
        int[][] stat = new int[10][3];
        battlePets pet = new battlePets("Frog",100,stat);

        //assert health was properly inserted
        assertEquals(100, pet.getHealth());
    }

    @Test
    void testHealthNegative() {
        int[][] stat = new int[10][3];
        battlePets pet = new battlePets("Frog",-10,stat);

        //assert health cannot go below zero
        //if health <0, health = 0
        assertEquals(0, pet.getHealth());
    }

    @Test
    void testHealthOver100() {
        int[][] stat = new int[10][3];
        battlePets pet = new battlePets("Frog",150,stat);

        //assert health cannot go above 100
        //if health >100, health = 100
        assertEquals(100, pet.getHealth());
    }

    @Test
    void testStat() {
        int[][] stat = {
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

        battlePets pet = new battlePets("Frog",100,stat);

        //assert stat is inserted correctly
        assertEquals(stat, pet.getStat());
    }

}