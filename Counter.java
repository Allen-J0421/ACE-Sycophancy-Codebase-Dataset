/**
 * Provide a counter for a participant in the simulation.
 * This includes an identifying string, a count of how many participants of
 * this type currently exist, and a flag indicating whether this type counts
 * toward the simulation's viability check.
 *
 * @version 2016.02.29
 */
public class Counter
{
    // A name for this type of simulation participant.
    private String name;
    // How many of this type exist in the simulation.
    private int count;
    // Whether this species contributes to the viability count.
    private boolean countsTowardViability;

    /**
     * Provide a name for one of the simulation types.
     * @param name  A name, e.g. "Fox".
     * @param countsTowardViability Whether this type counts toward viability.
     */
    public Counter(String name, boolean countsTowardViability)
    {
        this.name = name;
        this.countsTowardViability = countsTowardViability;
        count = 0;
    }

    /**
     * @return The short description of this type.
     */
    public String getName()
    {
        return name;
    }

    /**
     * @return The current count for this type.
     */
    public int getCount()
    {
        return count;
    }

    /**
     * @return true if this species counts toward the simulation viability check.
     */
    public boolean countsTowardViability()
    {
        return countsTowardViability;
    }

    /**
     * Increment the current count by one.
     */
    public void increment()
    {
        count++;
    }

    /**
     * Reset the current count to zero.
     */
    public void reset()
    {
        count = 0;
    }
}
