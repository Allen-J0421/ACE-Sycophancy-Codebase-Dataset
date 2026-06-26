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
    protected PlantSpecies getSpecies()
    {
        return PlantSpecies.SEDGE;
    }

    @Override
    protected int getMaxAge()
    {
        return MAX_AGE;
    }

    @Override
    protected double getSpreadProbability(Weather weather)
    {
        if (weather == Weather.RAIN || weather == Weather.SUNNY) {
            return OPTIMAL_BREEDING_FACTOR * MULTIPLY_PROBABILITY;
        }
        return MULTIPLY_PROBABILITY;
    }
}
