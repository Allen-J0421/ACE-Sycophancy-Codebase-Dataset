/**
 * Provide a counter for a participant in the simulation.
 *
 * @version 2022.02.28
 */
public class Counter
{
    // How many of this type exist in the simulation.
    private int count;

    /**
     * Initialise the counter at zero.
     */
    public Counter()
    {
        count = 0;
    }

    /**
     * @return (int) The current count for this type.
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
