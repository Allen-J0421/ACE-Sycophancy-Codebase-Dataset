/**
 * An immutable snapshot of the population count for one class of simulation
 * participant.
 *
 * Instances are created exclusively by {@link CounterRegistry#snapshot()} and
 * carry a fixed count value.  All counting operations (increment, reset) are
 * the responsibility of {@link CounterRegistry}; this class is a read-only
 * data container.
 *
 * @version 2016.02.29
 */
public class Counter
{
    private final String name;
    private final int    count;

    /**
     * @param name  display label for this class of participant (e.g. "Rat")
     * @param count the population count at the moment of snapshot
     */
    public Counter(String name, int count)
    {
        this.name  = name;
        this.count = count;
    }

    /** @return the display label for this participant class */
    public String getName()
    {
        return name;
    }

    /** @return the population count recorded at snapshot time */
    public int getCount()
    {
        return count;
    }
}
