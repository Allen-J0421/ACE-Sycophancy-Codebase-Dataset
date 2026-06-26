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
    // Shared Random object — all simulation classes draw from this to ensure determinism.
    private static final Random rand = new Random(SEED);

    /**
     * Provide the shared random generator.
     * @return The shared Random object.
     */
    public static Random getRandom()
    {
        return rand;
    }

    /**
     * Reset the shared randomizer to its initial seed.
     */
    public static void reset()
    {
        rand.setSeed(SEED);
    }
}
