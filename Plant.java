/**
 * A class representing shared characteristics of plants.
 *
 * @version 2022.02.xx 
 */
public abstract class Plant extends Organism
{
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
     * Make this plant act - that is: make it do
     * whatever it wants/needs to do.
     * @param newPlants A list to receive newly born plants.
     * @param weather The current weather.
     */
    abstract public void act(int step, Weather weather);
}
