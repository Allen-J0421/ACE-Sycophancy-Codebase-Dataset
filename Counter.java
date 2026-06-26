/**
 * Provide a counter for a participant in the simulation.
 * This includes an identifying string and a count of how
 * many participants of this type currently exist within 
 * the simulation.
 *
 * @version 2016.02.29
 */
public class Counter
{
    private static final int MINIMUM_COUNT = 0;

    // A name for this type of simulation participant
    private final String name;
    // How many of this type exist in the simulation.
    private int count;

    /**
     * Provide a name for one of the simulation types.
     * @param name  A name, e.g. "Dingo".
     */
    public Counter(String name)
    {
        this.name = name;
        setCount(MINIMUM_COUNT);
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
     * Increment the current count by one.
     */
    public void increment()
    {
        setCount(count + 1);
    }

    /**
     * Decrement the current count by one.
     */
    public void decrement()
    {
        if(count == MINIMUM_COUNT) {
            return;
        }
        setCount(count - 1);
    }
    
    /**
     * Reset the current count to zero.
     */
    public void reset()
    {
        setCount(MINIMUM_COUNT);
    }

    /**
     * Set the count after validating it.
     */
    private void setCount(int newCount) {
        if(newCount < MINIMUM_COUNT) {
            throw new IllegalArgumentException("Counter cannot be negative.");
        }
        count = newCount;
    }
}
