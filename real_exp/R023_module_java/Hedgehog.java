import java.util.List;
import java.util.Random;
import java.util.Iterator;

/**
 * Hedgehogs are animals that are not predetors in this simulation.
 * They only eat plants and no animals.
 * Hedgehogs get eaten by wolves, bears and badgers
 *
 * @version 2022.02.24
 */
public class Hedgehog extends Animal
{
    // Characteristics shared by all hedgehog (class variables).

    // The age at which a hedgehog can start to breed.
    private static final int BREEDING_AGE = 3;
    // The age to which a hedgehog can live.
    private static final int MAX_AGE = 35;
    // The likelihood of a hedgehog breeding.
    private static final double BREEDING_PROBABILITY = 0.75;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 5;
    //The food value of a single Plant
    private static final int PLANT_FOOD_VALUE = 13;

    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();

    // Individual characteristics (instance fields).

    // The hedgehog's age.
    private int age;
    //The hedgehog's food level
    private int foodLevel;

    /**
     * Create a new hedgehog. A hedgehog may be created with age
     * zero (a new born) or with a random age.
     * 
     * @param randomAge If true, the hedgehog will have a random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Hedgehog(boolean randomAge, Field field, Location location)
    {
        super(field, location);
        age = 0;
        int hunger = PLANT_FOOD_VALUE;
        if(randomAge) {
            age = rand.nextInt(MAX_AGE);
            foodLevel = rand.nextInt(hunger);
        }
        else {
            age = 0;
            foodLevel = hunger/2; //Newly born hedgehogs start with half the food level
        }
    }

    /**
     * This is what the hedgehog does most of the time - it runs 
     * around. Sometimes it will breed or die of old age.
     * @param newHedgehogs A list to return newly born hedgehog.
     */
    public void act(List<LivingBeing> newHedgehogs)
    {
        incrementAge();
        incrementHunger();

        if(isNight()) { //if it is night time, then move around
            if(isAlive()) {
                incrementHunger();
                giveBirth(newHedgehogs);            

                Location newLocation = findFood();

                if(newLocation == null) { 
                    // No food found - try to move to a free location.
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
        } else {
            //Sleep in day time

        }
    }

    /**
     * Look for plants adjacent to the current location
     * Only the first plant is eaten
     * @return Where food was found or null if food was not found
     */
    private Location findFood() {
        Field field = getField();
        if(field != null) {
            List<Location> adjacent = field.adjacentLocations(getLocation());
            Iterator<Location> it = adjacent.iterator();
            while(it.hasNext()) {
                Location where = it.next();
                Object plant = field.getObjectAt(where);
                if(plant instanceof Plant) {
                    Plant pl = (Plant) plant;
                    if(pl.isAlive()) {
                        pl.setDead();
                        foodLevel += PLANT_FOOD_VALUE;
                        return where;
                    }
                }
            }
        } 
        return null;
    }

    /**
     * Make this hedgehog more hungry. This could result in the hedgehog's death.
     */
    private void incrementHunger()
    {
        foodLevel--;
        if(foodLevel <= 0) {
            setDead();
        }
    }

    /**
     * Increase the age.
     * This could result in the hedgehog's death.
     */
    private void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }

    /**
     * Check whether or not this hedgehog is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newHedgehogs A list to return newly born hedgehogs.
     */
    private void giveBirth(List<LivingBeing> newHedgehogs)
    {
        // New hedgehogs are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        if(field == null) {
            return;
        }
        
        //get location of current animal and one surrounding it
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        
        //iterates through checking if one in the surrouding places are the opposite sex
        while(it.hasNext()) {
            Location nextPlace = it.next();
            Object animal = field.getObjectAt(nextPlace);
            Object currAnimal = field.getObjectAt(getLocation());
            
            if(animal instanceof Hedgehog && currAnimal instanceof Hedgehog) {
                Hedgehog hedge = (Hedgehog) animal;
                Hedgehog hedge2 = (Hedgehog) currAnimal;
                
                //if they can breed populate the fields 
                if(getOppositeGender(hedge,hedge2)) {
                    List<Location> free = field.getFreeAdjacentLocations(getLocation());
                    int births = breed();
                
                    for(int b = 0; b < births && free.size() > 0; b++) {
                        Location loc = free.remove(0);
                        Hedgehog young = new Hedgehog(false, field, loc);
                        newHedgehogs.add(young);
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
     * A hedgehog can breed if it has reached the breeding age.
     * @return true if the rabbit can breed, false otherwise.
     */
    private boolean canBreed()
    {
        return age >= BREEDING_AGE;
    }
}
