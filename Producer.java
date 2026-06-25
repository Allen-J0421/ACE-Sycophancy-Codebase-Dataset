import java.util.List;

/**
 * A class representing shared characteristics of producers.
 *
 * @version 18.02.22 (DD:MM:YY)
 */
public abstract class Producer extends Actor
{
    // The % of the normal breeding probability when there is no rain:
    private double noRainBreedingProbabilityPercentage = 0.2;
    /**
     * Create a new producer at a location in the field.
     * 
     * @param field            The field currently occupied.
     * @param location         The location within the field.
     * @param consumptionWorth The worth of the producer if consumed.
     */
    public Producer(Field field, Location location, int consumptionWorth,
                    double breedingProbability, int maxBirthsAtOnce,int maxAge)
    {
        super(field, location, consumptionWorth, breedingProbability,
              maxBirthsAtOnce,0,maxAge);
        currentAge = 0;
    }
    
    /**
     * Make this producer act - that is: make it do
     * whatever it wants/needs to do.
     * 
     * @param newProducers A list to receive newly born producers.
     */
    public void act(List<Actor> newProducers)
    {
        incrementAge();
        if (getIsAlive())
        {
            giveBirth(newProducers);
        }
    }
    
    /**
     * Check whether or not this producer is to give birth at this step.
     * New births will be made into free adjacent locations.
     * 
     * @param newProducers A list to return newly born producers.
     */
    protected void giveBirth(List<Actor> newProducers)
    {
        // Get a list of free adjacent locations:
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        
        // Work out the number of births this producer will have this step:
        int births = breed();
        
        // Add each birth into an adjacent location:
        for (int b = 0; b < births && free.size() > 0; b++)
        {
            Location location = free.remove(0);
            
            try
            {
                Actor child = this.getClass()
                              .getDeclaredConstructor(Field.class,
                                                      Location.class)
                              .newInstance(field, location);
                
                newProducers.add(child);
            }
            catch (java.lang.Exception e)
            {
                continue;
            }
        }
    }
        
    /**
     * Generate a number representing the number of births,
     * if it can breed.
     * 
     * @return The number of births (may be zero).
     */
    private int breed()
    {
        int births = 0;
        
        // Decrease breeding probability if there is no rain:
        double actualBreedingProbability = getBreedingProbability();
        
        if (!WeatherSystem.getIsRaining()) actualBreedingProbability *= noRainBreedingProbabilityPercentage;
        
        // Calculate number of births:
        if (rand.nextDouble() <= actualBreedingProbability)
            births = rand.nextInt(getMaxBirthsAtOnce()) + 1;
        
        return births;
    }
    protected boolean becomeCarcass()
    {
        return false;
    }
}
