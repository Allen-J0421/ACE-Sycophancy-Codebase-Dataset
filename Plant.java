import java.util.List;

/**
 * Write a description of class Plant here.
 *
 * @version (a version number or a date)
 */
public class Plant extends Organism
{
    private static final int BREEDING_AGE = 5;
    private static final int MAX_AGE = 15;
    private static final double BREEDING_PROBABILITY = 0.09;
    private static final int MAX_LITTER_SIZE = 6;
    private static final boolean IS_DIURNAL = true;
    
    public Plant(boolean randomAge, Field field, Location location)
    {
        super(randomAge, field, location, BREEDING_AGE, MAX_AGE,
                BREEDING_PROBABILITY, MAX_LITTER_SIZE, IS_DIURNAL);
    }
    
    public void act(List<Organism> newOrganisms) {
        incrementAge();
        if(isAlive()) {
            giveBirth(newOrganisms);
        }
    }
    
    /**
     * Returns whether the plant can breed, if it is above the breeding age
     * 
     * @return boolean True if the plant can breed
     */
    protected boolean canBreed() {
        return getAge() >= getBreedingAge();
    }
    
    /**
     * Creates and returns a new plant object
     * 
     * @return Organism object of subclass plant
     */
    protected Organism createNewOrganism(boolean randomAge, Field field, Location location) {
        return new Plant(randomAge, field, location);
    }
}
