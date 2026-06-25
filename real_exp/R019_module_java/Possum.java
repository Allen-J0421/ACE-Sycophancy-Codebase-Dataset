import java.util.List;
import java.util.Random;
import java.util.Iterator;

/**
 * A simple model of a possum.
 * Possums age, move, eat mice and muskrats, breed,transmit disease, and die.
 *
 * @version 2022.03.02
 */
public class Possum extends Animal
{
    // Characteristics shared by all possums (class variables).

    // The age at which a possum can start to breed.
    private static final int BREEDING_AGE = 15;
    // The age to which a possum can live.
    private static final int MAX_AGE = 150;
    // The likelihood of a possum breeding.
    private static final double BREEDING_PROBABILITY = 0.45;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 3;
    
    //Food values for the preys of possums
    private static final int MOUSE_FOOD_VALUE = 75;
    private static final int MUSKRAT_FOOD_VALUE = 80;
    
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();

    /**
     * Create a new possum. A possum may be created with age
     * zero (a new born) or with a random age.
     * 
     * @param randomAge If true, the possum will have a random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Possum(boolean randomAge, Field field, Location location)
    {
        super(field, location);
        age = 0;
        if(randomAge) {
            age = rand.nextInt(MAX_AGE);
            //food level is initialised with a random number between 7 and the average food values of prey
            foodLevel = 7 + rand.nextInt((MUSKRAT_FOOD_VALUE + MOUSE_FOOD_VALUE)/2);
        }
        else {
            age = 0;
            //food level is initialised with the average food values of prey
            foodLevel = (MOUSE_FOOD_VALUE+MUSKRAT_FOOD_VALUE)/2;
        }
    }
    
    /**
     * Create a new possum. A possum may be created with age
     * zero (a new born) or with a random age.
     * 
     * @param randomAge If true, the possum will have a random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     * @param disease The disease the possum should spawn with.
     */
    public Possum(boolean randomAge, Field field, Location location, AnimalDisease disease)
    {
        super(field, location);
        age = 0;
        if(randomAge) {
            age = rand.nextInt(MAX_AGE);
            //food level is initialised with a random number between 7 and the average food values of prey
            foodLevel = 1 + rand.nextInt((MUSKRAT_FOOD_VALUE + MOUSE_FOOD_VALUE)/2);
        }
        else {
            age = 0;
            //food level is initialised with the average food values of prey
            foodLevel = (MOUSE_FOOD_VALUE+MUSKRAT_FOOD_VALUE)/2;
        }
        // initialise the possum with the disease
        contractDisease(disease);
    }
    
    /**
     * This is what the possum does during the night - it hunts for mice and muskrats.
     * In the process, it might die of hunger, disease or old age.
     * @param newPossums A list to return newly born possums.
     */
    protected void actNight(List<Animal> newPossums)
    {
        incrementAge();
        incrementHunger();
        
        //transmit disease and check if the possum dies from the disease
        transmit();
        checkDieFromInfection();
        
        if(isAlive() ) {
            giveBirth(newPossums);   
            // Move towards a source of food if found.
            Location newLocation = findFood();
            if (newLocation == null){
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
     * This is what the possum does during the day: it sleeps. 
     * In the process, it might die of hunger, disease or old age.
     * @param newPossums A list to return newly born possums.
     */
    protected void actDay(List<Animal> newPossum){
        incrementAge();
        incrementHunger();
        
        //transmit and check if the possum dies from it's disease (if it has one)
        transmit();
        checkDieFromInfection();
    }
    
    /**
     * Increase the age.
     * This could result in the possum's death.
     */
    private void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }
    
    /**
     * Check whether or not this possum is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newPossums A list to return newly born possums.
     */
    private void giveBirth(List<Animal> newPossums)
    {
        // New possums are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed();
        for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            
            Possum young = new Possum(false, field, loc);
            newPossums.add(young);
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
            Object organism = field.getObjectAt(where);
            //Runs if the organism is a mouse
            if(organism instanceof Mouse) {
                Mouse mouse = (Mouse) organism;
                if(mouse.isAlive()) { 
                    mouse.setDead();
                    foodLevel += MOUSE_FOOD_VALUE;
                    return where;
                }
            }
            //runs if the organism is a muskrat
            else if (organism instanceof Muskrat) {
                Muskrat muskrat = (Muskrat) organism;
                if(muskrat.isAlive()) { 
                    muskrat.setDead();
                    foodLevel += MUSKRAT_FOOD_VALUE;
                    return where;
                }
            }
        }
        return null;
    }

    /**
     * A possum can breed if it has reached the breeding age.
     * @return true if the possum can breed, false otherwise.
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
                //runs if the adjacent animal is the same species(possum), their genders are opposite and both their current age's are higher than breeding age
                if (animal instanceof Possum && (animal.getGender()^ getGender()) && animal.getAge() >= BREEDING_AGE && age >= BREEDING_AGE ){
                    flag = true;
                }
            }
        }
        return flag;
    }
}
