import java.util.List;
import java.util.Random;

/**
 * A simple model of a grass plant
 * Grass ages, "breeds", transmits disease, and dies
 *
 * @version 2022.03.02
 */
public class Grass extends Plant
{
    
    // The likelihood of a possum breeding.
    private static final double BREEDING_PROBABILITY = 0.1;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 8;
    // The maximum age of the plant
    private static final int MAX_AGE = 100;
    
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();

    /**
     * Constructor for objects of class Grass
     * @Param randomAge Whether The plant is spawned with a random age
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Grass(boolean randomAge, Field field, Location location)
    {
        super(field, location);
        
        // Spawn in with a random age if randomAge is true
        if(randomAge) {
            age = rand.nextInt(MAX_AGE);
        }
        else {
            age = 0;
        }
    }
    
    /**
     * Constructor for if the plant spawns with a disease
     * @Param randomAge Whether The plant is spawned with a random age
     * @param field The field currently occupied.
     * @param location The location within the field.
     * @param disease The disease the plant contracts
     */
    public Grass (boolean randomAge, Field field, Location location, PlantDisease disease)
    {
        super(field, location);
        //Spawn in with a random age if randomAge is true
        if(randomAge) {
            age = rand.nextInt(MAX_AGE);
        }
        else {
            age = 0;
        }
        //set the current diease to the disease to be contracted
        contractDisease(disease);
    }
    
    /**
     * This is what the grass does most of the time during the day. Sometimes it will breed or die of old age, or it transmits disease.
     * 
     * @Param newGrass A list to return newly born grass objects
     * @Param currentWeatherMultiplier The multiplier on growth based on weather
     */
    protected void actDay(List<Plant> newGrass, float currentWeatherMultiplier)
    {
        //increment age and transmit disease to adjacent plants
        incrementAge();
        transmit();
        
        //If the plant has a disease then checks whether it dies from infection
        if (hasDisease()){
            checkDieFromInfection();
        }
        
        //give birth if alive
        if(isAlive() ) {
            giveBirth(newGrass,currentWeatherMultiplier);            
        }
    }
    
    /**
     * This is what the grass does most of the time during the night. Sometimes it will breed or die of old age, or it transmits disease.
     * 
     * @Param newGrass A list to return newly born grass objects
     * @Param currentWeatherMultiplier The multiplier on growth based on weather
     */
    protected void actNight(List<Plant> newGrass, float currentWeatherMultiplier){
        //increment age and transmit disease to adjacent plants
        incrementAge();
        transmit();
        
        //If the plant has a disease then checks whether it dies from infection
        if (hasDisease()){
            checkDieFromInfection();
        }
        
        // multiplier that reduces the probability of plants reproducing
        float nightMultiplier = 0.4f;
        if(isAlive() ) {
            giveBirth(newGrass,currentWeatherMultiplier*nightMultiplier);            
        }
    }
    
    /**
     * Check whether or not this grass is to "give birth" at this step (create a new grass object).
     * New births will be made into free adjacent locations.
     * @param newGrass A list to return newly born grass.
     */
    private void giveBirth(List<Plant> newGrass, float currentWeatherMultiplier)
    {
        // New grass is born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed(currentWeatherMultiplier);
        for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            Grass young = new Grass(false, field, loc);
            newGrass.add(young);
        }
    }
    
    /**
     * Generate a number representing the number of births,
     * if it can breed.
     * @return The number of births (may be zero).
     */
    private int breed(float currentWeatherMultiplier)
    {
        int births = 0;
        if(rand.nextDouble() <= BREEDING_PROBABILITY * currentWeatherMultiplier) {
            births = rand.nextInt(MAX_LITTER_SIZE) + 1;
        }
        return births;
    }
    
    /**
     * Increase the age.
     * This could result in the grass' death.
     */
    private void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }
}
