import java.util.Random;
import java.util.List;

/**
 * A simple model of grass - a type of plant.
 *
 * @version 2022.02.28
 */
public class Grass extends Plant
{
    // Characteristics shared by all grass (class variables).

    // The age to which a grass can live.
    private static final int MAX_AGE = 20;
    // The likelihood of a grass reproducing.
    private static final double REPRODUCTION_PROBABILITY = 0.44;
    // The maximum number of times it can reproduce.
    private static final int MAX_LITTER_SIZE = 6;
    // The food value of a single grass, which the predator gets when they eat grass. 
    //In effect, this is the number of steps a predator of grass can go before it has to eat again.
    private static final int GRASS_FOOD_VALUE = 10;
    // A shared random number generator to control reproduction of grass.
    private static final Random rand = Randomizer.getRandom();
    
    /**
     * Create a piece of gras. Grass can be created as a new born (age zero) or with a random age and food level.
     * 
     * @param randomAge If true, the age will have random age and water level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Grass(boolean randomAge, Field field, Location location)
    {
        super(randomAge, field, location);
        this.setAge(0);
        if(randomAge) {
            //random age & water level assigned to plant
            this.setAge(rand.nextInt(MAX_AGE));
            setWaterLevel(rand.nextInt(15));
        }
    }
    
    /**
     * This is what the grass does each step of the simulation 
     * - it grows and may reproduce.
     * @param newGrass A list to return newly grown grass
     */
    public void act(List<Actor> newGrass)
    {
        super.act(newGrass);
        decreaseWaterLevel();
        if(isAlive()) {
            giveBirth(newGrass); 
            findWater();
        }
    }

    /**
     * Increase the age.
     * This could result in the plant's death if they have reached their
     * maximum age.
     */
    public void incrementAge()
    {
        super.incrementAge();
        if(this.getAge() > MAX_AGE) {
            setDead();
        }
    }
    
    /**
     * Check whether or not this plant is to reproduce at this step.
     * New growths will be made into free adjacent locations.
     * @param newGrass A list to return newly grown grass
     */
    private void giveBirth(List<Actor> newGrass)
    {
        // New mice are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = reproduce();
        for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            Grass young = new Grass(false, field, loc);
            newGrass.add(young);
        }
    }
        
    /**
     * Generate a number representing the number of births,
     * if it can reproduce.
     * @return The number of births (may be zero).
     */
    private int reproduce()
    {
        int births = 0;
        if(canReproduce() && rand.nextDouble() <= REPRODUCTION_PROBABILITY) {
            births = rand.nextInt(MAX_LITTER_SIZE) + 1;
        }
        return births;
    }

    /**
     * A grass can reproduce if it's water level is greater than 9.
     * @return true if the plant can reproduce, false otherwise.
     */
    private boolean canReproduce()
    {
        return (this.getWaterLevel() >= 9);
    }
    
    /**
     * @return GRASS_FOOD_VALUE The food value of grass.
     */
    public int getFoodValue()
    { 
        return GRASS_FOOD_VALUE;
    }
}

