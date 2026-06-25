/**
 * A simple model of an algae.
 * Algae age, breed and die.
 * In the process, they may adopt different behaviours.
 *
 * @version 15/03/2022
 */
public class Algae extends Plant
{
    // The age at which an algae can start to breed.
    private static final int BREEDING_AGE = 4;
    // The age to which an algae can live.
    private static final int MAX_AGE = 6;
    // The likelihood of an algae breeding.
    private static double BREEDING_PROBABILITY = 0.4;
    // The maximum number of births an algae can have.
    private static final int MAX_LITTER_SIZE = 10;
    
    /**
     * Create a new algae. An algae may be created with age
     * zero (a new born) or with a random age.
     * 
     * @param randomAge If true, the algae will have a random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    protected Algae(boolean randomAge, Field field, Location location)
    {
        super(randomAge, field, location);
    }
    
    /**
     * Returns the algae's maximum age before it dies.
     * @return the algae's maximum age before it dies.
     */
    protected int getMaxAge() {
        return Algae.MAX_AGE;
    }
    
    /**
     * Returns the algae's maximum possible litter size.
     * @return the algae's maximum possible litter size.
     */    
    protected int getMaxLitterSize(){
        return Algae.MAX_LITTER_SIZE;
    }
    
    /**
     * Returns the algae's probability of breeding successfully.
     * @return the algae's probability of breeding successfully.
     */
    protected double getBreedingProbability(){
        return Algae.BREEDING_PROBABILITY;
    }
    
    /**
     * Gives the algae a new breeding probability.
     * @param newBreedingProbability The algae's probability of breeding.
     */
    protected void setBreedingProbability(double newBreedingProbability){
        BREEDING_PROBABILITY = newBreedingProbability;
    }
    
    /**
     * Returns the algae's minimum breeding age.
     * @return the algae's minimum breeding age.
     */  
    protected int getBreedingAge() {
        return Algae.BREEDING_AGE;
    }
    
    /**
     * Returns a new Algae instance.
     * @param randomAge If true, the algae will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     * @return a new Algae instance.
     */
    protected Organism getNewOrganism(boolean randomAge, Field field, Location loc){
        return new Algae(randomAge, field, loc);
    }
}
