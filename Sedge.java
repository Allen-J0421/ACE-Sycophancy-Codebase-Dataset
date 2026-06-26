import java.util.List;
/**
 * Implementation of a sedge plant, sedge's lifecycle consists of spreading, growing and getting eaten
 *
 * @version 1.0
 */
public class Sedge extends Plant
{
    
    /*///////////////////////////////////////////////////////////////
                                 CONSTANTS
    //////////////////////////////////////////////////////////////*/
    
    public static final int MAX_AGE = 20;
    public static final double MULTIPLY_PROBABILITY = 0.15;
    private static final double OPTIMAL_BREEDING_FACTOR = 4.0;
    
    /*///////////////////////////////////////////////////////////////
                                CONSTRUCTOR
    //////////////////////////////////////////////////////////////*/
    
    /**
     * Creates sedge.
     * 
     * @param randomAge Boolean flag to denote whether to assign a random age or not
     * @param field field where the plant is emplaced
     * @param location Location of the plant within the terrain
     */
    public Sedge(boolean randomAge, Field field, Location location)
    {
        super(randomAge, field, location,MAX_AGE);
    }
    
    /*///////////////////////////////////////////////////////////////
                            PLANT BEHAVIOUR LOGIC
    //////////////////////////////////////////////////////////////*/
    
    /**
     *  Imitates the actions sedge takes, sedge grows and multiplies if the weather and daylight conditions are optimal
     * @param weather Handler to get current weather
     * @param clock Time handler to get the current day state
     * @param newPlants the new plants to be generated
     */
    @Override
    public void act(List<Actor> newPlants, Weather weather, DayState dayState) {
        growAndMultiply(newPlants, weather, dayState, MAX_AGE, MULTIPLY_PROBABILITY, OPTIMAL_BREEDING_FACTOR);
    }
}
