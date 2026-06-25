import java.util.List;

/**
 * A class representing shared characteristics of plants.
 *
 * @version 15/03/2022
 */
public abstract class Plant extends Organism
{
    /**
     * Create a new plant at location in field.
     * 
     * @param randomAge If true, the plant will have a random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    protected Plant(boolean randomAge, Field field, Location location)
    {
        super(field, location);
        
        age = 0;
        if(randomAge) {
            age = rand.nextInt(getMaxAge());
        }
    }
    
    /**
     * This is what the plant does most of the time: it breeds, dies
     * of old age or might behave differently.
     * @param newOrganism A list to return newly born plants.
     */
    public void act(List<Organism> newOrganism)
    {
        incrementAge();
        if(isAlive()) {
            if (getWeather() == "Snowing" || getWeather() == "Stormy") {
                // Plants have a 0.15% chance of dying in snow or stormy weather.
                if (Math.random() < 0.0015) {
                    setDead();
                    return;
                }
            }
            
            if (getWeather() == "Windy") {
                // Plants have a 10% chance of dying in windy weather.
                if (Math.random() < 0.1) {
                    setDead();
                    return;
                }
            }
            
            giveBirth(newOrganism);            
            // Try to move into a free location.
            Location newLocation = getField().freeAdjacentLocation(getLocation());
            if(newLocation != null) {
                setLocation(newLocation);
            }
            else {
                // Overcrowding.
                setDead();
            }
        }
    }
    
    /**
     * Returns the maximum possible age of the plant.
     */
    abstract protected int getMaxAge();
}
