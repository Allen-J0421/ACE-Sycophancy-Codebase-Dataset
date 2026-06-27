import java.util.List;
import java.util.Iterator;
import java.util.Random;

/**
 * A simple model of a Lion.
 * Lions age, move, eat deers/cats/owls/mouse, and die.
 *
 * @version 2022.03.02 
 */
public class Lion extends Animal
{
    // Characteristics shared by all Liones (class variables).
    
    // The age at which a Lion can start to breed.
    private static final int BREEDING_AGE = 10;
    // The age to which a Lion can live.
    private static final int MAX_AGE = 225;
    // The likelihood of a Lion breeding. 
    private static final double BREEDING_PROBABILITY = 0.10;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 5;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    // The food value of a single lion.
    private static final int FOOD_VALUE = 20;
    // number of steps a lion can go before it has to eat again.
    private static final int DEFAULT_FOOD_LEVEL = 275;
    
    // Individual characteristics (instance fields).
    
    // The Lion's age.
    private int age;
    // The Lion's food level, which is increased by eating other animals.
    private int foodLevel;

    /**
     * Create a Lion. A Lion can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the Lion will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Lion(boolean randomAge, Field field, Location location)
    {
        super(field, location);
        if(randomAge) {
            age = rand.nextInt(MAX_AGE);
            foodLevel = rand.nextInt(DEFAULT_FOOD_LEVEL);
        }
        else {
            age = 0;
            foodLevel = 40;
        }
    }
    
    /**
     * This is what the Lion does most of the time: it hunts for
     * rabbits. In the process, it might breed, die of hunger,
     * or die of old age.
     * If the weather is foggy, it stops moving.
     * @param newLiones A list to return newly born Liones.
     * @param step The current step.
     * @param weather The current weather.
     */
    public void act(List<Animal> newLions, int step, Weather weather)
    {
        incrementAge();
        incrementHunger();
        updateBurnStatus(weather);

        if(isAlive()) {
            giveBirth(newLions);            
            // Move towards a source of food if found.
            Location newLocation;
            if (weather == Weather.FOGGY) {
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
            if (step % 4 != 0) {
                if(newLocation != null) {
                    setLocation(newLocation);
                }
                else {
                    // Overcrowding.
                    setDead(); 
                }
            } 
        }
    }

    /**
     * Increase the age. This could result in the Lion's death.
     */
    private void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }
    
    /**
     * Make this Lion more hungry. This could result in the Lion's death.
     */
    private void incrementHunger()
    {
        foodLevel--;
        if(foodLevel <= 0) {
            setDead();
        }
    }
    
    /**
     * Look for deer, cat, owl ,mouse adjacent to the current location.
     * Only the first live deer/cat/owl/mouse is eaten.
     * @return Where food was found, or null if it wasn't.
     */
    private Location findFood()
    {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation(), 1);
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object animal = field.getObjectAt(where);
            if(animal instanceof Deer) {
                Deer deer = (Deer) animal;
                if(deer.isAlive()) { 
                    deer.setDead();
                    foodLevel = foodLevel + deer.foodValue();
                    return where;
                }
            }
            else if(animal instanceof Cat) {
                Cat cat = (Cat) animal;
                if(cat.isAlive()) { 
                    cat.setDead();
                    foodLevel = foodLevel + cat.foodValue();
                    return where;
                }
            }
            else if(animal instanceof Owl) {
                Owl owl = (Owl) animal;
                if(owl.isAlive()) { 
                    owl.setDead();
                    foodLevel = foodLevel + owl.foodValue();
                    return where;
                }
            }
            else if(animal instanceof Mouse) {
                Mouse mouse = (Mouse) animal;
                if(mouse.isAlive()) { 
                        mouse.setDead();
                    foodLevel = foodLevel + mouse.foodValue();
                    return where;
                }
            }             
        }
        return null;
    }
    
    /**
     * Check whether or not this Lion is to give birth at this step.
     * New births will be made into free adjacent locations of the 24 nearest locations
     * surrounding the Lion.
     * @param newLions A list to return newly born Lions.
     */
    private void giveBirth(List<Animal> newLions)
    {        
        boolean breedingPair = false;
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation(), 2);
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object animal = field.getObjectAt(where);
            if(animal instanceof Lion) {
                Lion lion = (Lion) animal;
                if(lion.getGender() != getGender()) {
                    breedingPair = true;
                    break;
                }
            }
        }
        
        // New Lions are born into adjacent locations.
        // Get a list of adjacent free locations.
        //Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed();
        for(int b = 0; b < births && free.size() > 0 && breedingPair; b++) {
            Location loc = free.remove(0);
            Lion young = new Lion(false, field, loc);
            newLions.add(young);
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
     * A Lion can breed if it has reached the breeding age.
     * @return true if the Lion can breed.
     */
    private boolean canBreed()
    {
        return age >= BREEDING_AGE;
    }
}
