import java.util.Random;

/**
 * Provides the shared random generator used by a simulation run.
 */
public class RandomProvider
{
    private static final long DEFAULT_SEED = 1L;

    private final Random random;
    private final Long initialSeed;

    public RandomProvider()
    {
        this(DEFAULT_SEED);
    }

    public RandomProvider(long seed)
    {
        this.random = new Random(seed);
        this.initialSeed = seed;
    }

    public RandomProvider(Random random)
    {
        this.random = random;
        this.initialSeed = null;
    }

    public Random getRandom()
    {
        return random;
    }

    public void reset()
    {
        if(initialSeed != null) {
            random.setSeed(initialSeed);
        }
    }
}
