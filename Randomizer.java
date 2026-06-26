import java.util.Random;

/**
 * Provide control over the randomization of the simulation. By using the shared, fixed-seed 
 * randomizer, repeated runs will perform exactly the same (which helps with testing). Set 
 * 'useShared' to false to get different random behaviour every time.
 *
 * @version 18.02.22 (DD:MM:YY)
 */
public final class Randomizer
{
    // The default seed for control of randomization:
    private static final int SEED = 10001;
    // A shared Random object, if required:
    private static final Random rand = new Random(SEED);
    // Determine whether a shared random generator is to be provided:
    private static final boolean USE_SHARED = true;

    private Randomizer()
    {
    }

    /**
     * Provide a random generator.
     * 
     * @return A random object.
     */
    public static Random getRandom()
    {
        if (USE_SHARED) return rand;
        else           return new Random();
    }
    
    /**
     * Reset the randomization.
     * This will have no effect if randomization is not through
     * a shared Random generator.
     */
    public static void reset()
    {
        if (USE_SHARED) rand.setSeed(SEED);
    }

    public static void setSeed(int seed)
    {
        rand.setSeed(seed);
    }
}
