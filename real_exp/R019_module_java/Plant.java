import java.util.List;
/**
 * A class representing shared characteristics of plants
 * @version 2022.03.02
 */
public abstract class Plant extends Organism
{
    //The current weather
    protected Weather weather;
    
    /**
     * Create a new plant at location in field.
     * 
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Plant(Field field, Location location)
    {
        super(field, location);
    }
    
    /**
     * Make this plant act during the day - that is: make it do
     * whatever it wants/needs to do.
     * @param newPlants A list to receive newly born plants.
     */
    abstract protected void actDay(List<Plant> newPlants,float currentWeatherMultiplier);   
    
    /**
     * Make this plant act during during night - that is: make it do
     * whatever it wants/needs to do.
     * @param newPlants A list to receive newly born plants.
     */
    abstract protected void actNight(List<Plant> newPlants, float currentWeatherMultiplier);
}


