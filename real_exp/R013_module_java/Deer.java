import java.util.List;
import java.util.Iterator;
import java.util.Random;

/**
 * A simple model of a Deer.
 * Deers age, move, eat grass, breed, and die.
 *
 * @version 2022.03.02 
 */
public class Deer extends Animal
{
    // Characteristics shared by all Deers (class variables).

    // The age at which a Deer can start to breed.
    private static final int BREEDING_AGE = 10;
    // The age to which a Deer can live.
    private static final int MAX_AGE = 175;
    // The likelihood of a Deer breeding.
    private static final double BREEDING_PROBABILITY = 0.10;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 5;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    // The food value of a single dear. In effect, this is the
    // number of steps it can go.
    private static final int FOOD_VALUE = 50;
    // The default food value of a single deer.
    private static final int DEFAULT_FOOD_LEVEL = 250;
    
    // Individual characteristics (instance fields).    
    // The Deer's age.
    private int age;
    // The Deer's food level, which is increased by eating grass.
    private int foodLevel;

    /**
     * Create a new Deer. A Deer may be created with age
     * zero (a new born) or with a random age.
     * 
     * @param randomAge If true, the Deer will have a random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Deer(boolean randomAge, Field field, Location location)
    {
        super(field, location);
        if(randomAge) {
            age = rand.nextInt(MAX_AGE);
            foodLevel = rand.nextInt(DEFAULT_FOOD_LEVEL);
        }
        else {
            age = 0;
            foodLevel = DEFAULT_FOOD_LEVEL;
        }
    }
    
    /**
     * This is what the Deer does most of the time - it runs 
     * around. Sometimes it will breed or die of old age.
     * @param newDeers A list to return newly born Deers.
     * @param step The current step
     * @param weather The current weather.
     */
    public void act(List<Animal> newDeers, int step, String weather)
    {
        incrementAge();
        incrementHunger();
        updateBurnStatus(weather);
        if(isAlive()) {
            giveBirth(newDeers);  
           
            // Move towards a source of food if found.
            Location newLocation;
            if (weather == "RAINY") {
                newLocation = null;
            }
            else {
                newLocation = findFood();
            }

            if(newLocation == null) { 
                // No food found - try to move to a free location.
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
     * This could result in the Deer's death.
     */
    private void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }
    
    /**
     * Check whether or not this Deer is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newDeers A list to return newly born Deers.
     */
    private void giveBirth(List<Animal> newDeers)
    {

        boolean breedingPair = false;
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation(), 2);
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object animal = field.getObjectAt(where);
            if(animal instanceof Deer) {
                Deer deer = (Deer) animal;
                if(deer.Gender() != Gender()) { 
                    breedingPair = true;
                    break;
                }
            }
        }

        // New Deers are born into adjacent locations.
        // Get a list of adjacent free locations.
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed();
        for(int b = 0; b < births && free.size() > 0 && breedingPair; b++) {
            Location loc = free.remove(0);
            Deer young = new Deer(false, field, loc);
            newDeers.add(young);
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
     * Make this deer more hungry. This could result in the deer's death.
     */
    private void incrementHunger()
    {
        foodLevel--;
        if(foodLevel <= 0) {
            setDead();
        }
    }
    
    /**
     * Look for grass adjacent to the current location.
     * Only the first live grass is eaten.
     * @return Where food was found, or null if it wasn't.
     */
    private Location findFood()
    {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation(), 1);
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object plant = field.getObjectAt(where);
            if(plant instanceof Grass) {
                Grass grass = (Grass) plant;
                if(grass.isAlive()) { 
                    if (grass.getSize()>12 && foodLevel<30){
                        grass.decrementSize();
                        foodLevel+=20;
                    }
                    return where;
                }
            }
        }
        return null;
    }
    
    /**
     * A Deer can breed if it has reached the breeding age.
     * @return true if the Deer can breed, false otherwise.
     */
    private boolean canBreed()
    {
        return age >= BREEDING_AGE;
    }


    /**
     * @return deer's food value.
     */
    public int foodValue()
    {
        return FOOD_VALUE;
    }
}
