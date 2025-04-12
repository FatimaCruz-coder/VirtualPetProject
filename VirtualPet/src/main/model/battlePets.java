package group33.VirtualPet.src.main.model;
/**
 * Represents a battle pet with attributes for math combat in the Virtual Pet game.
 * Each battle pet has a type, health points, and mathematical statistics used in battles.
 * 
 * @author Team 33 (Dhir, Kostya, Fatima, Anna)
 * @since Winter 2025
 * 
 */
public class battlePets {
    
    /** The type/name of the battle pet (i.e jellyfish, frog) */
    String type;
    /** The current health points of the battle pet (0-100) */
    int health;
    
    /** 
     * 2D array containing the pet's battle statistics for math problems.
     * Each sub-array represents a different math problem set.
     */
    int[][] stats;

    /**
     * Constructs a new battle pet with specified attributes.
     * 
     * @param type The classification/name of the pet (e.g., "jellyfish")
     * @param health The initial health points (typically 100)
     * @param stats The 2D array of math problem statistics
     */
    public battlePets(String type, int health, int[][] stats) {
        this.type = type;
        if(health<0)
        {
            this.health = 0;
        }else this.health = Math.min(health, 100);
        this.stats = stats;
    }

    /**
     * Sets the type/classification of the battle pet.
     * 
     * @param type The new type to assign to the pet
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Gets the current type/classification of the battle pet.
     * 
     * @return The pet's type as a String
     */
    public String getType() {
        return this.type;
    }

    /**
     * Sets the current health points of the battle pet.
     * 
     * @param health The new health value (should be between 0-100)
     */
    public void setHealth(int health) {
        if(health<0)
        {
            //if health is less than 0 set to 0
            this.health = 0;
        }
        else this.health = Math.min(health, 100);
    }

    /**
     * Gets the current health points of the battle pet.
     * 
     * @return The current health value
     */
    public int getHealth() {
        return this.health;
    }

    /**
     * Updates a specific value in the pet's stats array.
     * 
     * @param i The row index in the stats 2D array
     * @param j The column index in the stats 2D array
     * @param x The new value to set at the specified position
     */
    public void setStat(int i, int j, int x) {
        this.stats[i][j] = x;
    }

    /**
     * Gets the complete 2D array of battle statistics.
     * 
     * @return The current stats array containing math problems
     */
    public int[][] getStat() {
        return this.stats;
    }
}