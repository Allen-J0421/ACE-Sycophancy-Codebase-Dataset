import java.util.List;
import java.util.Iterator;
import java.util.Random;

/**
 * A simple model of a otter.
 * Otters age, move, eat mice, breed, transmit disease, and die.
 *
 * @version 2022.03.02
 */
public class Otter extends Animal
{
    // Characteristics shared by all otters (class variables).
    
    // The age at which a otter can start to breed.
    private static final int BREEDING_AGE = 20;
    // The age to which a otter can live.
    private static final int MAX_AGE = 200;
    // The likelihood of a otter breeding.
    private static final double BREEDING_PROBABILITY = 0.4;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 5;
    
    // The food value of a single mouse. In effect, this is the
    // number of steps a otter can go before it has to eat again.
    private static final int MOUSE_FOOD_VALUE = 60;
    
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    
    
    /**
     * Create a otter. A otter can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the otter will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Otter(boolean randomAge, Field field, Location location)
    {
        super(field, location);
        if(randomAge) {
            age = rand.nextInt(MAX_AGE);
            //food level is initialised with a random number between 7 and the average food values of prey
            foodLevel = 7 + rand.nextInt(MOUSE_FOOD_VALUE);
        }
        else {
            age = 0;
            //food level is initialised with the average food values of prey
            foodLevel = MOUSE_FOOD_VALUE;
        }
    }
    
    /**
     * Create a otter. A otter can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the otter will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     * @param disease The disease the otter should spawn with.
     */
    public Otter(boolean randomAge, Field field, Location location, AnimalDisease disease)
    {
        super(field, location);
        if(randomAge) {
            age = rand.nextInt(MAX_AGE);
            //food level is initialised with a random number between 7 and the average food values of prey
            foodLevel = 7 + rand.nextInt(MOUSE_FOOD_VALUE);
        }
        else {
            age = 0;
            //food level is initialised with the average food values of prey
            foodLevel = MOUSE_FOOD_VALUE;
        }
        //initialise the otter with the disease
        contractDisease(disease);
    }
    
    /**
     * This is what the otter does during the day: it hunts for
     * mice.
     * In the process, it might die of hunger, disease or old age.
     * @param newOtters A list to return newly born otters.
     */
    protected void actDay(List<Animal> newOtters)
    {
        incrementAge();
        incrementHunger();
        //transmit disease and check if the otter dies from the disease
        transmit();
        checkDieFromInfection();
        if(isAlive()) {
            giveBirth(newOtters);            
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
     * This is what the otter does suring the night: it sleeps. 
     * In the process, it might die of hunger, disease or old age.
     * @param newOtters A list to return newly born otters.
     */
    protected void actNight(List<Animal> newOtters){
        incrementAge();
        incrementHunger();
        
        //transmit disease and check if the otter dies from the disease
        transmit();
        checkDieFromInfection();
    }
    
    /**
     * Increase the age. This could result in the otter's death.
     */
    private void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }
    
    
    /**
     * Look for mice adjacent to the current location.
     * Only the first live mouse is eaten.
     * @return Where food was found, or null if it wasn't.
     */
    private Location findFood()
    {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        // iterate through adjacent locations if the animal is hungry
        while(it.hasNext()&& foodLevel<MAX_AGE) {
            Location where = it.next();
            Object animal = field.getObjectAt(where);
            //Runs if the organism is a mouse
            if(animal instanceof Mouse) {
                Mouse mouse = (Mouse) animal;
                if(mouse.isAlive()) { 
                    mouse.setDead();
                    foodLevel += MOUSE_FOOD_VALUE;
                    return where;
                }
            }
        }
        return null;
    }
    
    /**
     * Check whether or not this otter is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newOtters A list to return newly born otters.
     */
    private void giveBirth(List<Animal> newOtters)
    {
        // New otters are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed();
        for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            Otter young = new Otter(false, field, loc);
            newOtters.add(young);
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
     * A otter can breed if it has reached the breeding age.
     */
    private boolean canBreed()
    {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        boolean flag = false;
        while(it.hasNext()){
            Location where = it.next();
            Object organism = field.getObjectAt(where);
            if (organism instanceof Animal){
                Animal animal = (Animal) organism;
                //runs if the adjacent animal is the same species(otter), their genders are opposite and both their current age's are higher than breeding age
                if (animal instanceof Otter && (animal.getGender()^ getGender()) && animal.getAge() >= BREEDING_AGE && age >= BREEDING_AGE ){
                    flag = true;
                }
            }
        }
        return flag;
    }
}