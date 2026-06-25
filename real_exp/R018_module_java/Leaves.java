/**
 * A simple model of leaves.
 * Leaves age, breed and die.
 * In the process, they may adopt different behaviours.
 *
 * @version 15/03/2022
 */
public class Leaves extends Plant
{
    // The age at which leaves can start to breed.
    private static final int BREEDING_AGE = 2;
    // The age to which leaves can live.
    private static final int MAX_AGE = 12;
    // The likelihood of leaves breeding.
    private static double BREEDING_PROBABILITY = 0.8;
    // The maximum number of births leaves can have.
    private static final int MAX_LITTER_SIZE = 10;

    /**
     * Create new leaves. Leaves may be created with age
     * zero (a new born) or with a random age.
     * 
     * @param randomAge If true, the leaves will have a random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    protected Leaves(boolean randomAge, Field field, Location location)
    {
        super(randomAge, field, location);
    }
    
    /**
     * Returns the leaves' maximum age before it dies.
     * @return the leaves' maximum age before it dies.
     */
    protected int getMaxAge() {
        return Leaves.MAX_AGE;
    }

    /**
     * Returns the leaves' maximum possible litter size.
     * @return the leaves' maximum possible litter size.
     */   
    protected int getMaxLitterSize(){
        return Leaves.MAX_LITTER_SIZE;
    }
    
    /**
     * Returns the leaves' probability of breeding successfully.
     * @return the leaves' probability of breeding successfully.
     */
    protected double getBreedingProbability(){
        return Leaves.BREEDING_PROBABILITY;
    }
    
    /**
     * Gives the leaves a new breeding probability.
     * @param newBreedingProbability The leaves' probability of breeding.
     */
    protected void setBreedingProbability(double newBreedingProbability){
        BREEDING_PROBABILITY = newBreedingProbability;
    }
    
    /**
     * Returns the leaves' minimum breeding age.
     * @return the leaves' minimum breeding age.
     */  
    protected int getBreedingAge() {
        return Leaves.BREEDING_AGE;
    }
    
    /**
     * Returns a new Leaves instance.
     * @param randomAge If true, the leaves will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     * @return a new Leaves instance.
     */
    protected Organism getNewOrganism(boolean randomAge, Field field, Location loc){
        return new Leaves(randomAge, field, loc);
    }
}
