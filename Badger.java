import java.util.Random;
import java.util.List;
import java.util.Iterator;

/**
 * Badgers are predators. 
 * Badgers are omnivores as they eat hedgehogs, frogs and plants
 * Badgers are eaten by bears
 * Badgers sleep at night time and move around and hunt during the day.
 *
 * @version 2022.02.24
 */
public class Badger extends Animal
{
    // Characteristics shared by all badgers (class variables).

    // The age at which a badger can start to breed.
    private static final int BREEDING_AGE = 2;
    // The age to which a badger can live.
    private static final int MAX_AGE = 1000;
    // The likelihood of a badger breeding.
    private static final double BREEDING_PROBABILITY = 0.75;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 10;
    // The food value of a single Hedgehog. In effect, this is the
    // number of steps a badger can go before it has to eat again.
    private static final int HEDGEHOG_FOOD_VALUE = 23;
    //The food value of a single Frog
    private static final int FROG_FOOD_VALUE = 23;
    //The food value of a single Plant
    private static final int PLANT_FOOD_VALUE = 12;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();

    // Individual characteristics (instance fields).
    // The badger's age.
    private int age;
    // The badger's food level, which is increased by eating hedgehogs.
    private int foodLevel;

    /**
     * Create a badger. A badger can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the badger will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Badger(boolean randomAge, Field field, Location location)
    {
        super(field, location);
        int hunger = HEDGEHOG_FOOD_VALUE + FROG_FOOD_VALUE + PLANT_FOOD_VALUE;
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
     * This is what the badger does most of the time: it hunts for
     * hedgehogs, badgers and plants. In the process, it might breed, die of hunger,
     * or die of old age.
     * @param field The field currently occupied.
     * @param newBears A list to return newly born badgers.
     */
    public void act(List<LivingBeing> newBadgers)
    {
        incrementAge();
        incrementHunger();
        if(!isNight()) {
            if(isAlive()) {
                giveBirth(newBadgers);            
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
            //Sleep at night
        }
    }

    /**
     * Increase the age. This could result in the badger's death.
     */
    private void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }

    /**
     * Make this badger more hungry. This could result in the badger's death.
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
     * Only the first live food is eaten.
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
            else if(animal instanceof Plant) {
                Plant plant = (Plant) animal;
                if(plant.isAlive()) {
                    plant.setDead();
                    foodLevel += PLANT_FOOD_VALUE;
                    return where;
                }
            }
        }
        return null;
    }

    /**
     * Check whether or not this badger is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newBadgers A list to return newly born badgers.
     */
    private void giveBirth(List<LivingBeing> newBadgers)
    {
        // New foxes are born into adjacent locations.
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
            
            if(animal instanceof Badger && animal2 instanceof Badger) {
                Badger badge = (Badger) animal;
                Badger badge2 = (Badger) animal;
                
                //if they can breed populate the fields 
                if(getOppositeGender(badge,badge2)) {
                    List<Location> free = field.getFreeAdjacentLocations(getLocation());
                    int births = breed();
                    for(int b = 0; b < births && free.size() > 0; b++) {
                        Location loc = free.remove(0);
                        Badger young = new Badger(false, field, loc);
                        newBadgers.add(young);
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
     * A badger can breed if it has reached the breeding age.
     */
    private boolean canBreed()
    {
        return age >= BREEDING_AGE;
    }

}
