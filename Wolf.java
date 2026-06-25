import java.util.List;
import java.util.Iterator;
import java.util.Random;

/**
 * A simple model of a wolf.
 * Wolves age, move, eat otters and possums, transmit disease, and die.
 *
 * @version 2022.03.02
 */
public class Wolf extends Animal
{
    // Characteristics shared by all wolves (class variables).
    
    // The age at which a wolf can start to breed.
    private static final int BREEDING_AGE = 45;
    // The age to which a wolf can live.
    private static final int MAX_AGE = 600;
    // The likelihood of a wolf breeding.
    private static final double BREEDING_PROBABILITY = 0.55;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 3;
    
    // The food values of wolf's preys. In effect, this is the
    // number of steps a wolf can go before it has to eat again.
    private static final int POSSUM_FOOD_VALUE = 35;
    private static final int OTTER_FOOD_VALUE = 50;
    
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    
    

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
        if(randomAge) {
            age = rand.nextInt(MAX_AGE);
            //food level is initialised with a random number between 7 and the average food values of prey            
            foodLevel = 7 + rand.nextInt((POSSUM_FOOD_VALUE + OTTER_FOOD_VALUE)/2);
        }
        else {
            age = 0;
            //food level is initialised with the average food values of prey
            foodLevel = (POSSUM_FOOD_VALUE+OTTER_FOOD_VALUE)/2;
        }
    }
    
    /**
     * Create a wolf. A wolf can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the wolf will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     * @param disease The disease the wolf should spawn with.
     */
    
    public Wolf(boolean randomAge, Field field, Location location, AnimalDisease disease)
    {
        super(field, location);
        if(randomAge) {
            age = rand.nextInt(MAX_AGE);
            //food level is initialised with a random number between 7 and the average food values of prey
            foodLevel = 7 + rand.nextInt((POSSUM_FOOD_VALUE + OTTER_FOOD_VALUE)/2);
        }
        else {
            age = 0;
            //food level is initialised with the average food values of prey
            foodLevel = (POSSUM_FOOD_VALUE+OTTER_FOOD_VALUE)/2;
        }
        //initialise the wolf with the disease
        contractDisease(disease);
    }
    
    /**
     * This is what the wolf does during the day: it sleeps. 
     * In the process, it might die of hunger, disease or old age.
     * @param newWolves A list to return newly born wolves.
     */
    protected void actDay(List<Animal> newWolves)
    {
        incrementAge();
        incrementHunger();
        
        //transmit disease and check if the wolf dies from the disease
        transmit();
        checkDieFromInfection();
    }
    
    /**
     * This is what the wolf does during the night: it hunts for
     * possums and otters. 
     * In the process, it might die of hunger, disease or old age.
     * @param newWolves A list to return newly born wolves.
     */
    protected void actNight(List<Animal> newWolves){
        incrementAge();
        incrementHunger();
        
        //transmit disease and check if the wolf dies from the disease
        transmit();
        checkDieFromInfection();
        if(isAlive()) {
            giveBirth(newWolves);            
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
     * Increase the age. This could result in the wolf's death.
     */
    private void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }
    
    
    /**
     * Look for possums adjacent to the current location.
     * Only the first live possum is eaten.
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
            Object animal = field.getObjectAt(where);
            //Runs if the organism is a possum
            if(animal instanceof Possum) {
                Possum possum = (Possum) animal;
                if(possum.isAlive()) { 
                    possum.setDead();
                    foodLevel += POSSUM_FOOD_VALUE;
                    return where;
                }
            }
            //Runs if the organism is a otter
            else if (animal instanceof Otter) {
                Otter otter = (Otter) animal;
                if(otter.isAlive()) { 
                    otter.setDead();
                    foodLevel += OTTER_FOOD_VALUE;
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
    private void giveBirth(List<Animal> newWolves)
    {
        // New wolves are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed();
        for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            Wolf young = new Wolf(false, field, loc);
            newWolves.add(young);
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
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        boolean flag = false;
        while(it.hasNext()){
            Location where = it.next();
            Object organism = field.getObjectAt(where);
            if (organism instanceof Animal){
                Animal animal = (Animal) organism;
                //runs if the adjacent animal is the same species(wolf), their genders are opposite and both their current age's are higher than breeding age
                if (animal instanceof Wolf && (animal.getGender()^ getGender()) && animal.getAge() >= BREEDING_AGE && age >= BREEDING_AGE ){
                    flag = true;
                }
            }
        }
        return flag;
    }
}
