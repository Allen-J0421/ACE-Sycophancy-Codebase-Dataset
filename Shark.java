import java.util.List;
import java.util.Iterator;
import java.util.Random;

/**
 * A simple model of a shark.
 * Sharks age, move, eat dolphins and seabirds, 
 * and die.
 *
 * @version 2016.02.29 (2)
 */
public class Shark extends Animal
{
    // Characteristics shared by all sharks (class variables).
    
    // The age at which a shark can start to breed.
    private static final int BREEDING_AGE = 5;
    // The age to which a shark can live.
    private static final int MAX_AGE = 30;
    // The likelihood of a shark breeding.
    private static final double BREEDING_PROBABILITY = 0.35; 
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 1;
    // The food value of a single dolphin. In effect, this is the
    // number of steps a shark can go before it has to eat again.
    private static final int DOLPHIN_FOOD_VALUE = 9;
    // The food value of a single seabird. In effect, this is the
    // number of steps a shark can go before it has to eat again.
    private static final int SEABIRD_FOOD_VALUE = 9;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    // The maximum options to decide different genders. 
    private static final int whichGender = 2;
    
    // Individual characteristics (instance fields).
    
    // The shark's age.
    private int age;
    // The shark's food level, which is increased by eating dolphins.
    private int foodLevel;
    
    // The gender of the shark which is represented by numbers.
    // 0 is male, 1 is female.
    private int gender;
    

    /**
     * Create a shark. A shark with a random gender can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the shark will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Shark(boolean randomAge, Field field, Location location)
    {
        super(field, location);
        if(randomAge) {
            age = rand.nextInt(MAX_AGE);
            foodLevel = rand.nextInt(DOLPHIN_FOOD_VALUE);
            gender = rand.nextInt(whichGender);
        }
        else {
            age = 0;
            foodLevel = DOLPHIN_FOOD_VALUE;
            gender = rand.nextInt(whichGender);
        }
    }
    
    /**
     * This is what the shark does most of the time: it hunts for
     * dolphins. In the process, it might breed, 
     * or die of old age.
     * @param field The field currently occupied.
     * @param newSharks A list to return newly born sharks.
     */
    public void act(List<Animal> newSharks)
    {
        incrementAge();
        incrementHunger();
        if(isAlive()) {
            giveBirth(newSharks);    
            // Move towards a source of food if found.
            Location newLocation = findFood();
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
     * Increase the age. This could result in the shark's death.
     */
    private void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }
    
    /**
     * Make this shark more hungry.
     */
    private void incrementHunger()
    {
        foodLevel--;
        if(foodLevel <= 0) {
            //setDead();
        }
    }
    
    /**
     * Look for seabieds or dolphins adjacent to the current location.
     * Only the first live seabird or dolphin is eaten.
     * If there are both seabird and dolphin adjacent to the current location,
     * a shark will eat seabird first.
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
            if(animal instanceof Seabird) {
                Seabird seabird = (Seabird) animal;
                if(seabird.isAlive()) { 
                    seabird.setDead();
                    foodLevel = SEABIRD_FOOD_VALUE;
                    return where;
                }
            }
            else if(animal instanceof Dolphin) { 
                Dolphin dolphin = (Dolphin) animal;
                if(dolphin.isAlive()) {
                    dolphin.setDead();
                    foodLevel = DOLPHIN_FOOD_VALUE;
                    return where;
                }
                
            }
        }
        return null;
    }
    
    /**
     * Check whether or not this shark is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newSharks A list to return newly born sharks.
     */
    private void giveBirth(List<Animal> newSharks)
    {
        // New sharks are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed();
        for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            Shark young = new Shark(false, field, loc);
            newSharks.add(young);
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
     * A shark can breed if it has reached the breeding age 
     * and there is a shark with a different gender near it.
     */
    private boolean canBreed()
    {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        boolean ifCanBreed = false;
        while(it.hasNext()) {
            Location where = it.next();
            Object animal = field.getObjectAt(where);
            if(animal instanceof Shark) {
                Shark shark = (Shark) animal;
                if(gender != shark.getGender()) { 
                    ifCanBreed = !ifCanBreed;
                }
            }
        }
        return age >= BREEDING_AGE && ifCanBreed;
    }
    
    /**
     * @return The gender of the shark.
     */
    private int getGender()
    {
        return gender;
    }
}
