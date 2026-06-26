import java.util.List;
/**
 * Implementation of a grass plant, the lifecycle of grass consists of spreading, growing and getting eaten
 *
 * @version 1.0
 */
public class Grass extends Plant
{
    
    /*///////////////////////////////////////////////////////////////
                                 CONSTANTS
    //////////////////////////////////////////////////////////////*/
    
    public static final int MAX_AGE = 25;
    public static final double MULTIPLY_PROBABILITY = 0.2;
    private static final double OPTIMAL_BREEDING_FACTOR = 4.0;
    
    /*///////////////////////////////////////////////////////////////
                                CONSTRUCTOR
    //////////////////////////////////////////////////////////////*/
    
    /**
     * Creates grass.
     * 
     * @param randomAge Boolean flag to denote whether to assign a random age or not
     * @param field field where the plant is emplaced
     * @param location Location of the plant within the terrain
     */
    public Grass(boolean randomAge, Field field, Location location)
    {
        super(randomAge, field, location,MAX_AGE);
    }
    
    /*///////////////////////////////////////////////////////////////
                            PLANT BEHAVIOUR LOGIC
    //////////////////////////////////////////////////////////////*/
    
    /**
     * Imitates the actions grass takes, grass grows and multiplies if the weather and daylight conditions are optimal.
     * 
     * @param weather Handler to get current weather
     * @param clock Time handler to get the current day state
     * @param newPlants the new plants to be generated
     */
    @Override
    public void act(List<Actor> newPlants, Weather weather, DayState dayState) {
        if(dayState == DayState.NIGHT) {
            return;
        }
        grow(MAX_AGE);
        if(!isAlive()) {
            return;
        }
        // Change behaviour of grass depending on the weather.
        if (weather == Weather.RAIN || weather == Weather.SUNNY) {
            multiply((double)OPTIMAL_BREEDING_FACTOR * MULTIPLY_PROBABILITY, newPlants);
            return;
        }
        multiply(MULTIPLY_PROBABILITY, newPlants);
    }

    @Override
    protected PlantSpecies getSpecies()
    {
        return PlantSpecies.GRASS;
    }
}
