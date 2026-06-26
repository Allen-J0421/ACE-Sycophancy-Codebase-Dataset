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
        super(randomAge, field, location, MAX_AGE);
    }

    /*///////////////////////////////////////////////////////////////
                            BEHAVIOUR HOOKS
    //////////////////////////////////////////////////////////////*/

    @Override protected int getMaxAge() { return MAX_AGE; }
    @Override protected double getMultiplyProbability() { return MULTIPLY_PROBABILITY; }

    @Override
    protected Plant createOffspring(Field field, Location location)
    {
        return new Grass(false, field, location);
    }
}
