package group33.VirtualPet.src.main.model;

/**
 * Represents a math battle system in the Virtual Pet game.
 * Manages player and opponent health, and contains math problems
 * for different difficulty levels (addition, subtraction, multiplication, division).
 */
public class mathBattle {

    /** The player's current health points */
    public int pHealth;
    
    /** The opponent pet in the battle */
    public battlePets opponent;

    /**
     * 2D array of addition problems in format {a, b, c} representing a + b = c
     */
    public int[][] addition = {
            {1,1,2},
            {2,3,5},
            {2,4,6},
            {3,6,9},
            {3,7,10},
            {3,8,11},
            {4,5,9},
            {4,6,10},
            {4,7,11},
            {6,8,14}
    };

    /**
     * 2D array of subtraction problems in format {a, b, c} representing a - b = c
     */
    public int[][] subtraction = {
            {1,1,0},
            {3,2,1},
            {4,2,2},
            {6,3,3},
            {7,3,4},
            {8,3,5},
            {5,4,1},
            {6,4,2},
            {7,4,3},
            {8,6,2}
    };

    /**
     * 2D array of multiplication problems in format {a, b, c} representing a * b = c
     */
    public int[][] multiplication = {
            {1,1,1},
            {2,3,6},
            {2,4,8},
            {3,6,18},
            {3,7,21},
            {3,8,24},
            {4,5,20},
            {4,6,24},
            {4,7,28},
            {6,8,48}
    };

    /**
     * 2D array of division problems in format {a, b, c} representing a / b = c
     */
    public int[][] division = {
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

    /**
     * Constructs a new math battle with specified difficulty level.
     * Initializes player health to 100 and creates appropriate opponent.
     * @param level The difficulty level ("jellyfish", "frog", "deer", or "penguin")
     */
    public mathBattle(String level) {
        this.pHealth = 100;

        if(level.equals("jellyfish")) {
            this.opponent = new battlePets(level, 100, addition);
        }
        if(level.equals("frog")) {
            this.opponent = new battlePets(level, 100, subtraction);
        }
        if(level.equals("deer")) {
            this.opponent = new battlePets(level, 100, multiplication);
        }
        if(level.equals("penguin")) {
            this.opponent = new battlePets(level, 100, division);
        }
    }

    /**
     * Gets the player's current health.
     * @return Current player health points
     */
    public int getpHealth() {
        return pHealth;
    }

    /**
     * Sets the player's health to specified value.
     * @param pHealth The new health value to set
     */
    public void setpHealth(int pHealth) {
        this.pHealth = pHealth;
    }

    /**
     * Gets the current opponent battle pet.
     * @return The opponent battlePets object
     */
    public battlePets getOpponent() {
        return opponent;
    }

    /**
     * Reduces player's health by 25 points when they lose a turn.
     */
    public void pLose() {
        this.pHealth -= 25;
    }

    /**
     * Reduces opponent's health by 25 points when they lose a turn.
     */
    public void oLose() {
        this.opponent.setHealth(this.opponent.getHealth() - 25);
    }
}