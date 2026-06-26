import java.util.Random;

/**
 * Provide control over the randomization of the simulation. When {@code useShared} is
 * {@code true}, all callers share one fixed-seed generator and repeated runs are
 * deterministic (useful for testing). When {@code false} (the current default), each
 * call to {@link #getRandom()} returns a fresh unseeded generator, so runs differ.
 *
 * @version 2016.02.29
 */
public class Randomizer
{
    // The default seed for control of randomization.
    private static final int SEED = 1111;
    // A shared Random object, used when useShared is true.
    private static final Random rand = new Random(SEED);
    // Set to true to enable fixed-seed, reproducible runs; false for random behaviour every time.
    static boolean useShared = false;

    /**
     * Provide a random generator.
     * @return A random object.
     */
    public static Random getRandom()
    {
        if(useShared) {
            return rand;
        }
        else {
            return new Random();
        }
    }
    
    /**
     * Reset the randomization.
     * This will have no effect if randomization is not through
     * a shared Random generator.
     */
    public static void reset()
    {
        if(useShared) {
            rand.setSeed(SEED);
        }
    }
}
