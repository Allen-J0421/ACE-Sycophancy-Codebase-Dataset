import java.util.List;
import java.util.ArrayList;

/**
 * A class representing shared characteristics of plants.
 *
 * 
 * @version 2022.02.28
 */
public abstract class Plant extends Species{
    /**
     * Create a new plant at location in field.
     * 
     * @param randomAge If true, the plant will have random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     * @param maxAge The age to which an species can live.
     */
    public Plant(boolean randomAge, Field field, Location location, int maxAge)
    {
        super(randomAge, field, location, maxAge);
    }
    
    /**
     * Generate a number representing the number of births,
     * if it can breed. 
     * Plant can make more births when the weather is rain.
     * @param breadingAge The age at which an species can start to breed.
     * @param probability The likelihood of an species breeding.
     * @param maxLitterSize The maximum number of births.
     * @return The number of births (may be zero).
     */
    protected int breed(int breadingAge, double probability, int maxLitterSize)
    {
        if(getField() == null){
            return 0;
        }
        int births = super.breed(breadingAge, probability, maxLitterSize);
        if(getField().getWeather() == Weather.RAIN) {
            births += births;
        }
        return births;
    }

    /**
     * Get locations where can birth
     */
    protected List<Location> getBirthLocations(Location location){
        Field field = getField();
        if(field == null){
            return new ArrayList<Location>();
        }
        List<Location> free = field.getFreeAdjacentLocations(getLocation(), 1);
        return free;
    }
    
}
