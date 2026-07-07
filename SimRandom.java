import java.util.Random;

/**
 * A utility wrapper around {@link java.util.Random} that exposes only the
 * methods the simulation uses. Consumers depend on this type rather than on
 * {@link java.util.Random} directly, so the underlying generator can be
 * changed without touching call sites.
 *
 * Instances are created and owned exclusively by {@link Randomizer}.
 */
public class SimRandom
{
    private final Random random;

    SimRandom(Random random)
    {
        this.random = random;
    }

    public boolean nextBoolean()       { return random.nextBoolean(); }
    public int     nextInt(int bound)  { return random.nextInt(bound); }
    public double  nextDouble()        { return random.nextDouble(); }

    /** Re-seed the underlying generator (used by {@link Randomizer#reset()}). */
    void setSeed(long seed) { random.setSeed(seed); }

    /**
     * Returns the underlying {@link Random} for APIs that require it
     * (e.g. {@link java.util.Collections#shuffle}).
     * Prefer the typed methods above for all other uses.
     */
    public Random asRandom() { return random; }
}
