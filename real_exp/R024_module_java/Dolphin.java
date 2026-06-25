import java.util.List;
import java.util.Iterator;
import java.util.Random;
 
/**
 * A simple model of a dolphin.
 * Dolphins age, move, breed, eat shrimps and die.
 *
 * @version 2016.02.29 (2)
 */
public class Dolphin extends Animal
{
    // Characteristics shared by all dolphins (class variables).

    // The age at which a dolphin can start to breed.
    private static final int BREEDING_AGE = 1;
    // The age to which a dolphin can live.
    private static final int MAX_AGE = 50;
    // The likelihood of a dolphin breeding.
    private static final double BREEDING_PROBABILITY = 0.40; 
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 4; 
    // The food value of a single shrimp. In effect, this is the
    // number of steps a dolphin can go before it has to eat again.
    private static final int SHRIMP_FOOD_VALUE = 9;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    
    // Individual characteristics (instance fields).
    
    // The dolphin's age.
    private int age;
    // The dolphin's food level, which is increased by eating shrimps.
    private int foodLevel;

    /**
     * Create a new dolphin. A dolphin may be created with age
     * zero (a new born) or with a random age and food level.
     * 
     * @param randomAge If true, the dolphin will have a random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Dolphin(boolean randomAge, Field field, Location location)
    {
        super(field, location);
        if(randomAge) {
            age = rand.nextInt(MAX_AGE);
            foodLevel = rand.nextInt(SHRIMP_FOOD_VALUE);
        }
        else {
            age = 0;
            foodLevel = SHRIMP_FOOD_VALUE;
        }
    }
    
    /**
     * This is what the dolphin does most of the time - it hunts 
     * for shrimps. Sometimes it will breed,  
     * die of being eaten or die of old age.
     * @param newDolphins A list to return newly born dolphins.
     */
    public void act(List<Animal> newDolphins)
    {
        incrementAge();
        incrementHunger();
        if(isAlive()) {
            giveBirth(newDolphins);
            // See if food found.
            Location newLocation = findFood();
            if(newLocation != null) {
                // Food found, try to move to next location.
                newLocation = getField().freeAdjacentLocation(getLocation());
            }
            // See if it was possible to move.
            if(newLocation != null) {
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
     * This could result in the dolphin's death.
     */
    private void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }
    
    /**
     * Make this dolphin more hungry. 
     */
    private void incrementHunger()
    {
        foodLevel--;
        if(foodLevel <= 0) {
            //setDead();
        }
    }
    
    /**
     * Look for shrimps adjacent to the current location.
     * Only the first live shrimp is eaten.
     * @return Where food was found, or null if it wasn't.
     */
    private Location findFood()
    {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object animal = field.getObjectAt(where);
            if(animal instanceof Shrimp) {
                Shrimp shrimp = (Shrimp) animal;
                if(shrimp.isAlive()) { 
                    shrimp.setDead();
                    foodLevel = SHRIMP_FOOD_VALUE;
                    return where;
                }
            }
        }
        return null;
    }
    
    /**
     * Check whether or not this dolphin is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newDolphins A list to return newly born dolphins.
     */
    private void giveBirth(List<Animal> newDolphins)
    {
        // New dolphins are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed();
        for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            Dolphin young = new Dolphin(false, field, loc);
            newDolphins.add(young);
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
            births = rand.nextInt(MAX_LITTER_SIZE) + 1;
        }
        return births;
    }

    /**
     * A dolphin can breed if it has reached the breeding age.
     * @return true if the rabbit can breed, false otherwise.
     */
    private boolean canBreed()
    {
        return age >= BREEDING_AGE;
    }
}