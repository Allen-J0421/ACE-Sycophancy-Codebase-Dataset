import java.util.HashMap;

/**
 * Maintains per-species population counts for a simulation step.
 * Counts are built incrementally: call reset() before scanning, incrementCount()
 * for each occupied cell, then countFinished() to mark them valid.
 */
public class PopulationStats
{
    private final HashMap<Class, Counter> counters;
    private boolean areCountsValid;

    public PopulationStats()
    {
        counters = new HashMap<>();
        areCountsValid = true;
    }

    /**
     * Invalidate all counts and reset each counter to zero.
     * Call this before starting a new scan pass.
     */
    public void reset()
    {
        areCountsValid = false;
        for (Counter count : counters.values()) count.reset();
    }

    /**
     * Increment the count for the given actor class, creating a counter on
     * first encounter.
     *
     * @param actorClass The runtime class of the actor occupying a cell.
     */
    public void incrementCount(Class actorClass)
    {
        Counter count = counters.get(actorClass);
        if (count == null)
        {
            count = new Counter(actorClass.getName());
            counters.put(actorClass, count);
        }
        count.increment();
    }

    /**
     * Mark the current counts as complete and valid.
     * Call this after the scan pass is finished.
     */
    public void countFinished() { areCountsValid = true; }

    /**
     * @return A comma-separated summary of each species and its count.
     */
    public String getPopulationDetails()
    {
        StringBuilder buffer = new StringBuilder();
        String prefix = "";
        for (Class key : counters.keySet())
        {
            Counter info = counters.get(key);
            buffer.append(prefix);
            buffer.append(info.getName());
            buffer.append(": ");
            buffer.append(info.getCount());
            prefix = ", ";
        }
        return buffer.toString();
    }

    /**
     * @return True if more than one species has a non-zero count.
     */
    public boolean isViable()
    {
        int nonZero = 0;
        for (Counter info : counters.values())
        {
            if (info.getCount() > 0) nonZero++;
        }
        return nonZero > 1;
    }

    /**
     * @return The raw counter map, keyed by actor class.
     */
    public HashMap<Class, Counter> getCounters() { return counters; }
}
