import java.util.Random;
import java.util.List;
import java.util.Iterator;

/**
 * Wolves are the next level of predators; they eat Hedgehogs and Frogs.
 * Wolves hunt and move around at night and sleep during the day. 
 *
 * @version 2022.02.24
 */
public class Wolf extends Animal
{
    // Characteristics shared by all wolves (class variables).

    // The age at which a wolf can start to breed.
    private static final int BREEDING_AGE = 5;
    // The age to which a wolf can live.
    private static final int MAX_AGE = 500;
    // The likelihood of a wolf breeding.
    private static final double BREEDING_PROBABILITY = 0.99;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 8;
    // The food value of a single hedgehog. In effect, this is the
    // number of steps a wolf can go before it has to eat again.
    private static final int HEDGEHOG_FOOD_VALUE = 25;
    // number of steps a wolf can go before it has to eat again.
    private static final int FROG_FOOD_VALUE = 25;

    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();

    // Individual characteristics (instance fields).
    // The wolves' age.
    private int age;
    // The wolves' food level, which is increased by eating hedgehogs and frogs.
    private int foodLevel;

    /**
     * Create a wolf. A wolf can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the wolf will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Wolf(boolean randomAge, Field field, Location location)
    {
        super(field, location);
        int hunger = HEDGEHOG_FOOD_VALUE + FROG_FOOD_VALUE;
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
     * This is what the wolf does most of the time: it hunts for
     * hedgehogs. In the process, it might breed, die of hunger,
     * or die of old age.
     * @param newWolves A list to return newly born wolves.
     */
    public void act(List<LivingBeing> newWolves)
    {
        incrementAge();
        incrementHunger();
        if(isNight()) { //Move,hunt etc at night time
            if(isAlive()) {
                giveBirth(newWolves);            
                // Move towards a source of food if found.
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
     * Increase the age. This could result in the wolves' death.
     */
    private void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }

    /**
     * Make this wolf more hungry. This could result in the wolves' death.
     */
    private void incrementHunger()
    {
        foodLevel--;
        if(foodLevel <= 0) {
            setDead();
        }
    }

    /**
     * Look for hedgehogs and frogs adjacent to the current location.
     * Only the first live hedgehog and frogs is eaten.
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
     * Check whether or not this wolf is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newWolves A list to return newly born wolves.
     */
    private void giveBirth(List<LivingBeing> newWolves)
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
            Object animal2 = field.getObjectAt(getLocation());
            if(animal instanceof Wolf && animal2 instanceof Wolf) {
                Wolf wolf = (Wolf) animal;
                Wolf wolf2 = (Wolf) animal;
                
                //if they can breed populate the fields 
                if(getOppositeGender(wolf, wolf2)) {
                    List<Location> free = field.getFreeAdjacentLocations(getLocation());
                    int births = breed();
                    for(int b = 0; b < births && free.size() > 0; b++) {
                        Location loc = free.remove(0);
                        Wolf young = new Wolf(false, field, loc);
                        newWolves.add(young);
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
     * A wolf can breed if it has reached the breeding age.
     */
    private boolean canBreed()
    {
        return age >= BREEDING_AGE;
    }
}
