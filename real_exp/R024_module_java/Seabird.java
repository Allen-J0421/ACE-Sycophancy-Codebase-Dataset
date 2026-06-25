import java.util.List;
import java.util.Random;
import java.util.Iterator;

/**
 * A simple model of a seabird.
 * Seabirds age, move, breed, eat seaweeds, sleep and die.
 *
 * @version 2016.02.29 (2)
 */
public class Seabird extends Animal
{
    // Characteristics shared by all seabirds (class variables).

    // The age at which a seabird can start to breed.
    private static final int BREEDING_AGE = 2;
    // The age to which a seabird can live.
    private static final int MAX_AGE = 40;
    // The likelihood of a seabird breeding.
    private static final double BREEDING_PROBABILITY = 0.45; //0.12
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 3;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    // The food value of a single plant (seaweed). In effect, this is the
    // number of steps a seabird can go before it has to eat again.
    private static final int SEAWEED_FOOD_VALUE = 9;
    
    // The current hour of the time.
    private Time time;
    
    // Individual characteristics (instance fields).
    
    // The seabird's age.
    private int age;
    
    // The seabird's food level.
    private int foodLevel;
    /**
     * Create a new seabird. A seabird may be created with age
     * zero (a new born) or with a random age.
     * 
     * @param randomAge If true, the seabird will have a random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     * @param time The current time.
     */
    public Seabird(boolean randomAge, Field field, Location location, Time time)
    {
        super(field, location);
        this.time = time;
        age = 0;
        if(randomAge) {
            age = rand.nextInt(MAX_AGE);
            foodLevel = rand.nextInt(SEAWEED_FOOD_VALUE);
        }
    }
    
    /**
     * This is what the seabird does most of the time - it hunts for
     * seaweeds. Sometimes it will breed,  
     * die of being eaten or die of old age.
     * The seabird only does the aboved things in the day, it sleeps at night.
     * @param newSeabirds A list to return newly born seabirds.
     */
    public void act(List<Animal> newSeabirds)
    {
        incrementAge();
        incrementHunger();
        if(isAlive() && isAwake()) {
            giveBirth(newSeabirds);            
            // See if food is found.
            findFood();
            // Whether there is food or not, move to another location.
            Location newLocation = getField().freeAdjacentLocation(getLocation());
            // See if it was possible to move.
            if(newLocation !=  null) {
                setLocation(newLocation);            
            }
            else {
                // Overcrowding.
                setDead();
            }
        }
    }

    /**
     * Increase the age.
     * This could result in the seabird's death.
     */
    private void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }
    
    /**
     * Make this seabird more hungry. 
     */
    private void incrementHunger()
    {
        foodLevel--;
        if(foodLevel <= 0) {
            //setDead();
        }
    }
    
    /**
     * Look for seaweeds adjacent to the current location.
     * Only the first live seaweed is eaten.
     * The seaweed will die if the amount of the seaweed is zero.
     * @return Where food was found, or null if it wasn't.
     */
    private Location findFood()
    {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object plant = field.getObjectAt(where);
            if(plant instanceof Seaweed) {
                Seaweed seaweed = (Seaweed) plant;
                if(seaweed.isAlive()) { 
                    seaweed.reduceAmount();
                    foodLevel = SEAWEED_FOOD_VALUE;
                    return where;
                }
            } 
        }
        return null;
    }
    
    /**
     * Check whether or not this seabird is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newSeabirds A list to return newly born seabirds.
     */
    private void giveBirth(List<Animal> newSeabirds)
    {
        // New seabirds are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed();
        for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            Seabird young = new Seabird(false, field, loc, time);
            newSeabirds.add(young);
        }
    }
        
    /**
     * Generate a number representing the number of births,
     * if it can breed.
     * @return The number of births (may be zero).
     */
    private int breed()
    {
        int births = 0;
        if(canBreed() && rand.nextDouble() <= BREEDING_PROBABILITY) {
            births = rand.nextInt(MAX_LITTER_SIZE) + 1 ;
        }
        return births;
    }

    /**
     * A seabird can breed if it has reached the breeding age.
     * @return true if the rabbit can breed, false otherwise.
     */
    private boolean canBreed()
    {
        return age >= BREEDING_AGE;
    }
    
    /**
     * Check whether the seabird is awake or not.
     * If it is awake, then it can move and do other things it can do.
     */
    private boolean isAwake()
    {
        return time.isDay();
    }
}