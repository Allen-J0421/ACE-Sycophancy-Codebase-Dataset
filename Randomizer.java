import java.util.Random;

/**
 * Provide control over the randomization of the simulation.
 * Using a fixed seed means repeated runs produce identical results, which helps with testing.
 *
 * @version 2016.02.29
 */
public class Randomizer
{
    private static final int SEED = 1111;
    private static final Random rand = new Random(SEED);

    private Randomizer() {}

    /**
     * Return the shared, seeded random generator.
     */
    public static Random getRandom()
    {
        return rand;
    }

    /**
     * Reset the shared generator to its initial seed, making the next run reproducible.
     */
    public static void reset()
    {
        rand.setSeed(SEED);
    }
}
