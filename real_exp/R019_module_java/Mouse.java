import java.util.List;
import java.util.Iterator;
import java.util.Random;

/**
 * A simple model of a mouse.
 * Mice age, move, eat grass, breed, transmit disease and die.
 *
 * @version 2022.03.02
 */
public class Mouse extends Animal
{
    // Characteristics shared by all Mice (class variables).
    
    // The age at which a mouse can start to breed.
    private static final int BREEDING_AGE = 2;
    // The age to which a mouse can live.
    private static final int MAX_AGE = 100;
    // The likelihood of a mouse breeding.
    private static final double BREEDING_PROBABILITY = 0.65;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 8;
    
    // The food value of a single grass object. In effect, this is the
    // number of steps a mouse can go before it has to eat again.
    private static final int GRASS_FOOD_VALUE = 25;
    
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    
    
    

    /**
     * Create a mouse. A mouse can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the mouse will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Mouse(boolean randomAge, Field field, Location location)
    {
        super(field, location);
        if(randomAge) {
            age = rand.nextInt(MAX_AGE);
            //food level is initialised with a random number between 7 and the average food values of prey
            foodLevel = 7 + rand.nextInt(GRASS_FOOD_VALUE);
        }
        else {
            age = 0;
            //food level is initialised with the average food values of prey
            foodLevel = GRASS_FOOD_VALUE;
        }
    }
    
    /**
     * Create a mouse. A mouse can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the mouse will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     * @param disease The disease the mouse should spawn with.
     */
    public Mouse(boolean randomAge, Field field, Location location, AnimalDisease disease)
    {
        super(field, location);
        if(randomAge) {
            age = rand.nextInt(MAX_AGE);
            //food level is initialised with a random number between 7 and the average food values of prey
            foodLevel = 7 + rand.nextInt(GRASS_FOOD_VALUE);
        }
        else {
            age = 0;
            //food level is initialised with the average food values of prey
            foodLevel = GRASS_FOOD_VALUE;
        }
        //The mouse is initialised with the disease
        contractDisease(disease);
    }
    
    /**
     * This is what the mouse does during the day. It sleeps.
     * In the process, it might die of hunger, disease or old age.
     * @param newMouse A list to return newly born Mice.
     */
    protected void actDay(List<Animal> newMouse){
        incrementAge();
        incrementHunger();
        
        //transmit disease and check if the mouse dies from the disease
        transmit();
        checkDieFromInfection();
    }
    
    /**
     * This is what the mouse does during the night: it looks for
     * grass to eat.
     * In the process, it might die of hunger, disease or old age.
     * @param newMouse A list to return newly born Mice.
     */
    protected void actNight(List<Animal> newMouse)
    {
        incrementAge();
        incrementHunger();
        
        //transmit disease and check if the mouse dies from the disease
        transmit();
        checkDieFromInfection();
        
        if(isAlive()) {
            giveBirth(newMouse);            
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
     * Increase the age. This could result in the mouse's death.
     */
    private void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
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
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        // iterate through adjacent locations if the animal is hungry
        while(it.hasNext() && foodLevel<MAX_AGE) {
            Location where = it.next();
            Object plant = field.getObjectAt(where);
            //Runs if the organism is grass
            if(plant instanceof Grass) {
                Grass grass = (Grass) plant;
                if(grass.isAlive()) { 
                    grass.setDead();
                    foodLevel += GRASS_FOOD_VALUE;
                    return where;
                }
            }
        }
        return null;
    }
    
    /**
     * Check whether or not this mouse is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newMouse A list to return newly born Mice.
     */
    private void giveBirth(List<Animal> newMouse)
    {
        // New Mice are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed();
        for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            Mouse young = new Mouse(false, field, loc);
            newMouse.add(young);
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
     * A mouse can breed if it has reached the breeding age.
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
                //runs if the adjacent animal is the same species(mouse), their genders are opposite and both their current age's are higher than breeding age
                if (animal instanceof Mouse && (animal.getGender()^ getGender()) && animal.getAge() >= BREEDING_AGE && age >= BREEDING_AGE ){
                    flag = true;
                }
            }
        }
        return flag;
    }
}