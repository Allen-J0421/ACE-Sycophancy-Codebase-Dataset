import java.util.List;
import java.util.Iterator;
import java.util.Random;

/**
 * Bears are the apex predators, they eat Hedgehogs, Badgers and Frogs
 * Bears are not eaten by another species
 * Bears sleep during the night and hunt and move during day
 *
 * @version 2022.02.24 (2)
 */
public class Bear extends Animal
{
    // Characteristics shared by all bears (class variables).

    // The age at which a bear can start to breed.
    private static final int BREEDING_AGE = 10;
    // The age to which a bear can live.
    private static final int MAX_AGE = 1500;
    // The likelihood of a bear breeding.
    private static final double BREEDING_PROBABILITY = 0.99;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 7;
    // The food value of a single Hedgehog. In effect, this is the
    // number of steps a bear can go before it has to eat again.
    private static final int HEDGEHOG_FOOD_VALUE = 40;
    //The food value of a single Badger
    private static final int BADGER_FOOD_VALUE = 25;
    //The food value of a single Frog
    private static final int FROG_FOOD_VALUE = 40;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();

    // Individual characteristics (instance fields).
    
    // The bears's age.
    private int age;
    // The bears's food level, which is increased by eating hedgehogs.
    private int foodLevel;

    /**
     * Create a bear. A bear can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the bear will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Bear(boolean randomAge, Field field, Location location)
    {
        super(field, location);
        int hunger = HEDGEHOG_FOOD_VALUE + BADGER_FOOD_VALUE + FROG_FOOD_VALUE;
        if(randomAge) {
            age = rand.nextInt(MAX_AGE);
            foodLevel = rand.nextInt(hunger);
        }
        else {
            age = 0;
            foodLevel = hunger;
        }
    }

    /**
     * This is what the bear does most of the time: it hunts for
     * hedgehogs, badgers and frogs. In the process, it might breed, die of hunger,
     * or die of old age.
     * @param field The field currently occupied.
     * @param newBears A list to return newly born bears.
     */
    public void act(List<LivingBeing> newBears)
    {
        incrementAge();
        incrementHunger();
        if(!isNight()) {
            if(isAlive()) {
                giveBirth(newBears);            
                // Move towards a source of food if found.
                Location newLocation = findFood();
                if(newLocation == null) { 
                    // No food found - try to move to a free location.
                    //newLocation = getField().freeAdjacentLocation(getLocation());
                    Location l =  getLocation();
                    if(l != null) {
                        newLocation = getField().freeAdjacentLocation(l);
                    }
                }
                // See if it was possible to move.
                if(newLocation != null) {
                    setLocation(newLocation);
                }
                else {
                    // Overcrowding.
                }
            }
        }
        else {
            //Sleep at night
        }
    }

    /**
     * Increase the age. This could result in the bear's death.
     */
    private void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }

    /**
     * Make this bear more hungry. This could result in the bear's death.
     */
    private void incrementHunger()
    {
        foodLevel--;
        if(foodLevel <= 0) {
            setDead();
        }
    }

    /**
     * Look for food adjacent to the current location.
     * Only the first live hedgehog/badger/frog is eaten.
     * @return Where food was found, or null if it wasn't.
     */
    private Location findFood()
    {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        
        //goes through all adjacent locations checking for each type of animal to eat
        while(it.hasNext()) {
            Location where = it.next();
            Object animal = field.getObjectAt(where);
            if(animal instanceof Hedgehog) {
                Hedgehog hedgehog = (Hedgehog) animal;
                if(hedgehog.isAlive()) { 
                    hedgehog.setDead();
                    foodLevel += HEDGEHOG_FOOD_VALUE;
                    return where;
                }
            }

            else if(animal instanceof Badger) {
                Badger badger = (Badger) animal;
                if(badger.isAlive()) {
                    badger.setDead();
                    foodLevel += BADGER_FOOD_VALUE;
                    return where;
                }
            }

            else if(animal instanceof Frog) {
                Frog frog = (Frog) animal;
                if(frog.isAlive()) {
                    frog.setDead();
                    foodLevel += FROG_FOOD_VALUE;
                    return where;
                }
            }
        }
        return null;
    }

    /**
     * Check whether or not this bear is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newBears A list to return newly born bears.
     */
    private void giveBirth(List<LivingBeing> newBears)
    {
        // New bears are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        
        //get location of current animal and one surrounding it
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        
        //iterates through checking if one in the surrouding places are the opposite sex
        while(it.hasNext()) {
            Location nextPlace = it.next();
            Object animal = field.getObjectAt(nextPlace);
            Object currentAnimal = field.getObjectAt(getLocation());
            
            if(animal instanceof Bear && currentAnimal instanceof Bear) {
                Bear bear = (Bear) animal;
                Bear bear2 = (Bear) animal;
                
                //if they can breed populate the fields 
                if(getOppositeGender(bear,bear2)) {
                    List<Location> free = field.getFreeAdjacentLocations(getLocation());
                    int births = breed();
                    for(int b = 0; b < births && free.size() > 0; b++) {
                        Location loc = free.remove(0);
                        Bear young = new Bear(false, field, loc);
                        newBears.add(young);
                    }
                }
            }
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
     * A bear can breed if it has reached the breeding age.
     */
    private boolean canBreed()
    {
        return age >= BREEDING_AGE;
    }
}
