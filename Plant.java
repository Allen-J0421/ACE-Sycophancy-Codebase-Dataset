import java.util.List;

/**
 * A plant in the simulation. Plants age and spread to adjacent free locations.
 *
 * @version 2022.03.02
 */
public class Plant extends Organism
{
    // The age at which a plant can start to breed.
    private static final int BREEDING_AGE = 5;
    // The age to which a plant can live.
    private static final int MAX_AGE = 15;
    // The likelihood of a plant breeding.
    private static final double BREEDING_PROBABILITY = 0.09;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 6;
    // Whether the plant is diurnal or nocturnal
    private static final boolean IS_DIURNAL = true;

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
    @Override
    protected boolean getIsDiurnal() {
        return IS_DIURNAL;
    }
    
    @Override
    public void act(List<Organism> newOrganisms) {
        incrementAge();
        if(isAlive()) {
            giveBirth(newOrganisms);
        }
    }
    
    /**
     * Returns breeding age of a plant
     * 
     * @return int of the plant's breeding age.
     */
    @Override
    protected int getBreedingAge() {
        return BREEDING_AGE;
    }
    
    /**
     * Returns max age of a plant
     * 
     * @return int of the plant's max age.
     */
    @Override
    protected int getMaxAge() {
        return MAX_AGE;
    }
    
    /**
     * Returns whether the plant can breed, if it is above the breeding age
     * 
     * @return boolean True if the plant can breed
     */
    @Override
    protected boolean canBreed() {
        return getAge() >= BREEDING_AGE;
    }
    
    /**
     * Returns max litter size of a plant
     * 
     * @return int of the plant's max litter size
     */
    @Override
    protected int getMaxLitterSize() {
        return MAX_LITTER_SIZE;
    }
    
    /**
     * Returns breeding probability of a plant
     * 
     * @return double of the plant's breeding probability
     */
    @Override
    protected double getBreedingProbability() {
        return BREEDING_PROBABILITY;
    }
    
    // Functional methods
    
    /**
     * Creates and returns a new plant object
     * 
     * @return Organism object of subclass plant
     */
    @Override
    protected Organism createNewOrganism(boolean randomAge, Field field, Location location) {
        return new Plant(randomAge, field, location);
    }
}
