import java.util.List;
/**
 * Implementation of a sage plant, sage's lifecycle consists of spreading, growing and getting eaten
 *
 * @version 1.0
 */
public class Sage extends Plant
{
    
    /*///////////////////////////////////////////////////////////////
                                 CONSTANTS
    //////////////////////////////////////////////////////////////*/
    
    public static final int MAX_AGE = 20;
    public static final double MULTIPLY_PROBABILITY = 0.15;

    /*///////////////////////////////////////////////////////////////
                                CONSTRUCTOR
    //////////////////////////////////////////////////////////////*/
    
    /**
     * Creates sage.
     * 
     * @param randomAge Boolean flag to denote whether to assign a random age or not
     * @param field field where the plant is emplaced
     * @param location Location of the plant within the terrain
     */
    public Sage(boolean randomAge, Field field, Location location)
    {
        super(randomAge, field, location,MAX_AGE);
    }

    /*///////////////////////////////////////////////////////////////
                            PLANT BEHAVIOUR LOGIC
    //////////////////////////////////////////////////////////////*/
    @Override protected int getMaxAge() { return MAX_AGE; }

    @Override protected double getMultiplyProbability() { return MULTIPLY_PROBABILITY; }
}
