import java.util.List;
import java.util.Random;
import java.util.Iterator;

/**
 * Write a description of class Plant here.
 *
 * @version (a version number or a date)
 */

public class Plant extends Organism
{
    // The age at which a monkey can start to breed.
    private static final int BREEDING_AGE = 5;
    // The age to which a monkey can live.
    private static final int MAX_AGE = 15;
    // The likelihood of a monkey breeding.
    private static final double BREEDING_PROBABILITY = 0.09;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 6;
    // Whether the animal is diurnal or nocturnal
    private static final boolean IS_DIURNAL = true;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    
    public Plant(boolean randomAge, Field field, Location location)
    {
        super(randomAge, field, location);
    }
    
    // Accessor and mutator methods
    
    /**
     * Return whether the plant is diurnal
     * 
     * @return boolean True if the plant is diurnal, false if nocturnal
     */
    protected boolean getIsDiurnal() {
        return IS_DIURNAL;
    }
    
    /**
     * Returns breeding age of a plant
     * 
     * @return int of the plant's breeding age.
     */
    protected int getBreedingAge() {
        return BREEDING_AGE;
    }
    
    /**
     * Returns max age of a plant
     * 
     * @return int of the plant's max age.
     */
    protected int getMaxAge() {
        return MAX_AGE;
    }
    
    /**
     * Returns whether the plant can breed, if it is above the breeding age
     * 
     * @return boolean True if the plant can breed
     */
    protected boolean canBreed() {
        return getAge() >= BREEDING_AGE;
    }
    
    /**
     * Returns max litter size of a plant
     * 
     * @return int of the plant's max litter size
     */
    protected int getMaxLitterSize() {
        return MAX_LITTER_SIZE;
    }
    
    /**
     * Returns breeding probability of a plant
     * 
     * @return double of the plant's breeding probability
     */
    protected double getBreedingProbability() {
        return BREEDING_PROBABILITY;
    }
    
    // Functional methods
    
    /**
     * Creates and returns a new plant object
     * 
     * @return Organism object of subclass plant
     */
    protected Organism createNewOrganism(boolean randomAge, Field field, Location location) {
        return new Plant(randomAge, field, location);
    }
}
