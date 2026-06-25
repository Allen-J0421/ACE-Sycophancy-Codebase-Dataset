import java.util.List;

/**
 * A class representing shared characteristics of carpetweeds.
 *
 * 
 * @version 2022.02.28
 */
public class Carpetweed extends Plant{

    // The age at which a species can start to breed.
    private static final int BREEDING_AGE = 2;
    // The age to which a species can live.
    private static final int MAX_AGE = 8;
    // The likelihood of a species breeding.
    private static final double BREEDING_PROBABILITY = 0.5; 
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 5;
    // The likelihood of dead at fog weather.
    private static final double DEATH_PROBABILITY = 0.1; 

    /**
     * Create a new carpetweed at location in field.
     * 
     * @param randomAge If true, the plant will have random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Carpetweed(boolean randomAge, Field field, Location location)
    {
        super(randomAge, field, location, MAX_AGE);
        maxAge = MAX_AGE;
    }

    /**
     * Create a new carpetweed at location in field.
     * 
     * @param randomAge If true, the plant will have random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Species spawn(boolean randomAge, Field field, Location location)
    {
        return new Carpetweed(randomAge, field, location);
    }
    
    /**
     * Make this species act - that is: make it do
     * whatever it wants/needs to do.
     * @param newSpecies A list to receive newly born species.
     */
    public void act(List<Species> newSpecies, boolean isDay){
        incrementAge();
        if(isAlive()) {
            Weather weather = getField().getWeather();
            if(weather == Weather.FOG && rand.nextDouble() < DEATH_PROBABILITY){
                setDead();
            }
            giveBirth(newSpecies, BREEDING_AGE, BREEDING_PROBABILITY, MAX_LITTER_SIZE);            
        }
    }
}
