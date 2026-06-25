import java.util.List;

/**
 * A class representing shared characteristics of Ryegrass.
 *
 * 
 * @version 2022.02.28
 */
public class Ryegrass extends Plant{
    
    // The age at which a species can start to breed.
    private static final int BREEDING_AGE = 4;
    // The age to which a species can live.
    private static final int MAX_AGE = 8;
    // The likelihood of a species breeding.
    private static final double BREEDING_PROBABILITY = 0.6; 
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 3;

    /**
     * Create a new ryegrass at location in field.
     * 
     * @param randomAge If true, the plant will have random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Ryegrass(boolean randomAge, Field field, Location location)
    {
        super(randomAge, field, location, MAX_AGE);
        maxAge = MAX_AGE;
    }

    /**
     * Create a new ryegrass at location in field.
     * 
     * @param randomAge If true, the plant will have random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Species spawn(boolean randomAge, Field field, Location location)
    {
        return new Ryegrass(randomAge, field, location);
    }
    
    /**
     * Make this species act - that is: make it do
     * whatever it wants/needs to do.
     * @param newSpecies A list to receive newly born species.
     */
    public void act(List<Species> newSpecies, boolean isDay){
        incrementAge();
        if(isAlive()) {
            giveBirth(newSpecies, BREEDING_AGE, BREEDING_PROBABILITY, MAX_LITTER_SIZE);            
        }
    }
}
