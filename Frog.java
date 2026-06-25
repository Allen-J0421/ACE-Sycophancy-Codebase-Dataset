import java.util.Random;
import java.util.List;
import java.util.Iterator;

/**
 * Frogs are not predators as they eat Plants.  
 * Frogs are simulated to be able to move and eat plants during night time and sleep in day time.
 * Frogs are eaten by Bears, Wolves and Badgers
 *
 * @version 2022.02.24
 */
public class Frog extends Animal
{
    // Characteristics shared by all frogs (class variables).

    // The age at which a frog can start to breed.
    private static final int BREEDING_AGE = 3;
    // The age to which a frog can live.
    private static final int MAX_AGE = 27;
    // The likelihood of a frog breeding.
    private static final double BREEDING_PROBABILITY = 0.75;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 4;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();

    // Individual characteristics (instance fields)
    private static final int PLANT_FOOD_VALUE = 12;
    // The frog's age.
    private int age;
    // The frog's food level
    private int foodLevel;
    /**
     * Create a new frog. A frog may be created with age
     * zero (a new born) or with a random age.
     * 
     * @param randomAge If true, the frog will have a random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Frog(boolean randomAge, Field field, Location location)
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
            foodLevel = hunger/2;
        }
    }

    /**
     * This is what the frog does most of the time - it runs 
     * around. Sometimes it will breed or die of old age.
     * @param newFrogs A list to return newly born frogs.
     */
    public void act(List<LivingBeing> newFrogs)
    {
        incrementAge();
        incrementHunger();
        if(isNight()) {
            if(isAlive()) {
                giveBirth(newFrogs);            
                // Try to move into a free location.
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
                    //setDead();
                }
            }
        } else {
            //sleep during daytime
        }
    }

    /**
     * Look for plants adjacent to the current location.
     * Only the first live plant is eaten.
     * @return Where food was found, or null if it wasn't.
     */
    private Location findFood() {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        
        //goes through all adjacent locations checking for plants to eat
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
        return null;
    }

    /**
     * Make this frog more hungry. This could result in the frog's death.
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
     * This could result in the frog's death.
     */
    private void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }

    /**
     * Check whether or not this frog is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newFrogs A list to return newly born rabbits.
     */
    private void giveBirth(List<LivingBeing> newFrogs)
    {
        //New frogs are born
        Field field = getField();
        
        //get location of current animal and one surrounding it
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        
        //iterates through checking if one in the surrouding places are the opposite sex
        while(it.hasNext()) {
            Location nextPlace = it.next();
            Object animal = field.getObjectAt(nextPlace);
            Object animal2 = field.getObjectAt(getLocation());
            
            if(animal instanceof Frog && animal2 instanceof Frog) {
                Frog frog = (Frog) animal;
                Frog frog2 = (Frog) animal2;
                
                //if they can breed populate the fields 
                if(getOppositeGender(frog,frog2)) {
                    List<Location> free = field.getFreeAdjacentLocations(getLocation());
                    int births = breed();
                    for(int b = 0; b < births && free.size() > 0; b++) {
                        Location loc = free.remove(0);
                        Frog young = new Frog(false, field, loc);
                        newFrogs.add(young);
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
     * A frog can breed if it has reached the breeding age.
     * @return true if the rabbit can breed, false otherwise.
     */
    private boolean canBreed()
    {
        return age >= BREEDING_AGE;
    }
}
