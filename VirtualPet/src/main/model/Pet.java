package group33.VirtualPet.src.main.model;

/**
 * Represents a virtual pet with customizable attributes and behaviors.
 * Each pet has statistics (health, sleep, fullness, happiness) that change over time
 * and can be affected by player actions. Pets can be of different types (DOG, DEER, etc.)
 * with unique characteristics.
 * 
 * @author Team 33 (Dhir, Kostya, Fatima, Anna)
 * @since Winter 2025 
 * 
 */

public class Pet {
    // Pet type-specific constants
    private String name;
    private PetType type;
    
    // Vital statistics
    private int health;
    private int sleep;
    private int fullness;
    private int happiness;
    
    // Maximum values for each statistic (can vary by pet type)
    private int maxHealth;
    private int maxSleep;
    private int maxFullness;
    private int maxHappiness;
    
    // Decline rates for statistics
    private int sleepDeclineRate;
    private int fullnessDeclineRate;
    private int happinessDeclineRate;
    
    // Pet state
    private boolean isDead;
    private boolean isSleeping;
    private boolean isHungry;
    private boolean isAngry;
    
    /**
     * Enum representing different types of pets with their base statistics.
     * Each type has unique maximum values and decline rates for its attributes.
     */
    public enum PetType {
        // Dog: Balanced stats with moderate decline rates
        DOG(100, 100, 100, 100, 2, 3, 2),
        
        // Deer: High energy (sleep) but gets hungry faster
        DEER(90, 120, 90, 100, 1, 4, 2),
        
        // Frog: Lower health but slow hunger rate
        FROG(80, 90, 110, 100, 2, 1, 3),
        
        // Jellyfish: High health but needs more attention (happiness declines faster)
        JELLYFISH(120, 90, 90, 90, 2, 2, 4),
        
        // Penguin: Gets tired quickly but stays happy longer
        PENGUIN(100, 80, 110, 110, 3, 2, 1);

        
        private final int health;
        private final int sleep;
        private final int fullness;
        private final int happiness;
        private final int sleepDecline;
        private final int fullnessDecline;
        private final int happinessDecline;
        
        /**
         * Constructor for PetType enum values.
         *
         * @param health The base health value
         * @param sleep The base sleep value
         * @param fullness The base fullness value
         * @param happiness The base happiness value
         * @param sleepDecline Rate at which sleep decreases
         * @param fullnessDecline Rate at which fullness decreases
         * @param happinessDecline Rate at which happiness decreases
         */
        PetType(int health, int sleep, int fullness, int happiness, 
        int sleepDecline, int fullnessDecline, int happinessDecline) {
            this.health = health;
            this.sleep = sleep;
            this.fullness = fullness;
            this.happiness = happiness;
            this.sleepDecline = sleepDecline;
            this.fullnessDecline = fullnessDecline;
            this.happinessDecline = happinessDecline;
        }
        
        /**
         * Converts the enum value to lowercase (currently unimplemented).
         * @return The lowercase string representation of the enum value
         * @throws UnsupportedOperationException Always thrown as this method is not implemented
         */
        public String toLowerCase() {
            throw new UnsupportedOperationException("Unimplemented method 'toLowerCase'");
        }
    }
    
    /**
     * Constructs a new Pet with the given name and type.
     * Initializes all statistics based on the pet type's base values.
     *
     * @param name The name of the pet
     * @param type The type of pet (from PetType enum)
     * 
     */
    public Pet(String name, PetType type) {
        this.name = name;
        this.type = type;
        
        // Initialize statistics based on pet type
        this.maxHealth = type.health;
        this.maxSleep = type.sleep;
        this.maxFullness = type.fullness;
        this.maxHappiness = type.happiness;
        
        this.health = maxHealth;
        this.sleep = maxSleep;
        this.fullness = maxFullness;
        this.happiness = maxHappiness;
        
        // Initialize decline rates
        this.sleepDeclineRate = type.sleepDecline;
        this.fullnessDeclineRate = type.fullnessDecline;
        this.happinessDeclineRate = type.happinessDecline;
        
        // Initial state
        this.isDead = false;
        this.isSleeping = false;
        this.isHungry = false;
        this.isAngry = false;
    }
    
    /**
     * Updates the pet's statistics based on time progression.
     * Decreases sleep, fullness, and happiness according to their decline rates.
     * Checks for critical states (sleep deprivation, hunger, etc.).
     * Does nothing if the pet is dead or sleeping.
     */
    public void updateStatistics() {
        if (isDead || isSleeping) return;
        
        // Decline statistics
        sleep = Math.max(0, sleep - sleepDeclineRate);
        fullness = Math.max(0, fullness - fullnessDeclineRate);
        happiness = Math.max(0, happiness - happinessDeclineRate);
        
        // Check for critical states
        checkSleepState();
        checkFullnessState();
        checkHappinessState();
        checkHealthState();
    }
    
    private void checkSleepState() {
        if (sleep <= 0) {
            // Apply health penalty and force sleep
            health = Math.max(0, health - 10);
            isSleeping = true;
        }
    }
    
    /**
     * Checks the pet's fullness state and updates hunger status.
     * Applies penalties if fullness is gone.
     */
    public void checkFullnessState() {
        if (fullness <= 0) {
            isHungry = true;
            // Faster happiness decline when hungry
            happiness = Math.max(0, happiness - (happinessDeclineRate * 2));
            // Decrease health while hungry
            health = Math.max(0, health - 5);
        } else {
            isHungry = false;
        }
    }
      
    /**
     * Checks the pet's happiness state and updates anger status.
     */
    public void checkHappinessState() {
        if (happiness <= 0) {
            isAngry = true;
        } else {
            isAngry = false;
        }
    }
    
    /**
     * Checks the pet's health state and updates death status.
     */
    public void checkHealthState() {
        if (health <= 0) {
            isDead = true;
        }
    }
    
     /**
     * Feeds the pet, increasing its fullness.
     * Has no effect if the pet is dead or sleeping.
     *
     * @param foodValue The amount to increase fullness by
     */
    public void feed(int foodValue) {
        if (isDead || isSleeping) return;
        
        fullness = Math.min(maxFullness, fullness + foodValue);
        isHungry = false;
    }
    
    
    /**
     * Gives a gift to the pet, increasing its happiness.
     * Has no effect if the pet is dead or sleeping.
     *
     * @param giftValue The amount to increase happiness by
     */
    public void giveGift(int giftValue) {
        if (isDead || isSleeping) return;
        
        happiness = Math.min(maxHappiness, happiness + giftValue);
        isAngry = false;
    }
    
    /**
     * Puts the pet to sleep.
     * Has no effect if the pet is dead.
     */
    public void sleep() {
        if (isDead) return;
        
        this.isSleeping = true;
    }
    
    /**
     * Wakes the pet up and restores its sleep to maximum.
     * Has no effect if the pet is dead.
     */
    public void wakeUp() {
        if (isDead) return;
        
        sleep = maxSleep;
        isSleeping = false;
    }

    /**
     * Exercises the pet, affecting multiple statistics.
     * Reduces sleep and fullness while increasing health.
     * Has no effect if the pet is dead or sleeping.
     */
    public void exercise() {
        if (isDead || isSleeping) return;
        
        // Reduce sleepiness and hunger, increase health
        sleep = Math.max(0, sleep - 10);
        fullness = Math.max(0, fullness - 5);
        health = Math.min(maxHealth, health + 5);
    }
    
    /**
     * Takes the pet to the vet, restoring some health.
     * Has no effect if the pet is dead or sleeping.
     */
    public void takeToVet() {
        if (isDead || isSleeping) return;
        
        health = Math.min(maxHealth, health + 20);
    }
    
    // Getters for game state and statistics
    /**
     * Checks if pet is dead
     * @return Whether the pet is dead
     */
    public boolean isDead() { return isDead; }
    
    /**
     * Checks if pet is sleeping
     * @return Whether the pet is sleeping
     */
    public boolean isSleeping() { return isSleeping; }
    
    /**
     * Checks if pet is hungry
     * @return Whether the pet is hungry
     */
    public boolean isHungry() { return isHungry; }
    
    /**
     * Checks if pet is angry
     * @return Whether the pet is angry
     */
    public boolean isAngry() { return isAngry; }
    
    /**
     * Gets the health level
     * @return The pet's current health
     */
    public int getHealth() { return health; }
    
    /**
     * Gets the sleep level
     * @return The pet's current sleep level
     */
    public int getSleep() { return sleep; }
    
    /**
     * Gets the fullness level
     * @return The pet's current fullness level
     */
    public int getFullness() { return fullness; }
    
    /**
     * Gets the happiness level
     * @return The pet's current happiness level
     */
    public int getHappiness() { return happiness; }
    
    /**
     * Gets the name
     * @return The pet's name
     */
    public String getName() { return name; }
    
    /**
     * Gets the pet type
     * @return The pet's type
     */
    public PetType getType() { return type; }

    /**
     * Sets the pet's health (clamped to 0-maxHealth)
     * @param health The new health value
     * 
     */
    public void setHealth(int health) {
        this.health = health;
    }

    /**
     * Sets the pet's dead flag
     * @param dead, true or false 
     * 
     */
    public void setDead(boolean dead) {
        this.isDead = dead;
    }

    /**
     * Sets the pet's happiness (clamped to 0-maxHappiness)
     * @param happiness The new happiness value
     * 
     */
    public void setHappiness(int happiness) {
        this.happiness = happiness;
    }

    /**
     * Sets the pet's fullness (clamped to 0-maxFullness)
     * @param fullness The new fullness value
     * 
     */
    public void setFullness(int fullness) {
        this.fullness = fullness;
    }

    /**
     * Sets the pet's sleep level (clamped to 0-maxSleepiness)
     * @param sleep The new sleep value
     * 
     */
    public void setSleep(int sleep) {
        this.sleep = sleep;
    }

    /**
     * Revives a dead pet and restores all statistics to maximum.
     * Resets all negative states (dead, sleeping, hungry, angry).
     */
    public void revive() {
        isDead = false;
        isSleeping = false;
        isHungry = false;
        isAngry = false;
        
        // Restore all statistics to maximum
        health = maxHealth;
        sleep = maxSleep;
        fullness = maxFullness;
        happiness = maxHappiness;
    }
}