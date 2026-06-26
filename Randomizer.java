
import java.util.Random;

/**
 * Provide control over the randomization of the simulation. By using the shared, fixed-seed
 * randomizer, repeated runs will perform exactly the same (which helps with testing).
 *
 * @version 2016.02.29
 */
public class Randomizer
{
    // The default seed for control of randomization.
    private static final int SEED = 1111;
    // A shared Random object used by all actors.
    private static final Random rand = new Random(SEED);

    private Randomizer() {}

    /**
     * Provide a random generator.
     * @return A random object.
     */
    public static Random getRandom()
    {
        return rand;
    }

    /**
     * Reset the randomization to the initial seed.
     */
    public static void reset()
    {
        rand.setSeed(SEED);
    }
}
