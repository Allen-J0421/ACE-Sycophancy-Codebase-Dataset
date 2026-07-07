import java.util.Random;

/**
 * Manages the single {@link SimRandom} instance used throughout the simulation.
 * All classes obtain their random generator via {@link #getRandom()} so that
 * the source of randomness is controlled from one place.
 */
public class Randomizer
{
    private static final long SEED = 1111;

    // One shared instance for the entire simulation.
    private static final SimRandom instance = new SimRandom(new Random());

    /**
     * Returns the shared random generator.
     *
     * @return The simulation-wide {@link SimRandom} instance.
     */
    public static SimRandom getRandom()
    {
        return instance;
    }

    /**
     * Re-seeds the shared generator to its default seed, producing a
     * reproducible sequence from this point forward.
     */
    public static void reset()
    {
        instance.setSeed(SEED);
    }
}
