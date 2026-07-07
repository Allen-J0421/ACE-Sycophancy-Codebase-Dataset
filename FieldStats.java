import java.util.HashMap;

/**
 * This class collects and provides some statistical data on the state
 * of a field. It is flexible: it will create and maintain a counter
 * for any class of object that is found within the field.
 *
 * @version 2016.02.29
 */
public class FieldStats
{
    private final PopulationTracker tracker;

    /**
     * Construct a FieldStats object.
     */
    public FieldStats()
    {
        tracker = new PopulationTracker();
    }

    /**
     * Get details of what is in the field.
     * @return A string describing what is in the field.
     */
    public String getPopulationDetails(Field field)
    {
        return tracker.getPopulationDetails(field);
    }

    /**
     * Invalidate the current set of statistics; reset all
     * counts to zero.
     */
    public void reset()
    {
        tracker.reset();
    }

    /**
     * Increment the count for one class of animal.
     * @param animalClass The class of animal to increment.
     */
    public void incrementCount(Class animalClass)
    {
        tracker.incrementCount(animalClass);
    }

    /**
     * Indicate that an animal count has been completed.
     */
    public void countFinished()
    {
        tracker.countFinished();
    }

    /**
     * Determine whether the simulation is still viable.
     * I.e., should it continue to run.
     * @return true If there is more than one species alive.
     */
    public boolean isViable(Field field)
    {
        return tracker.isViable(field);
    }

    /**
     * Returns the raw counter map.
     * @return the map of class to Counter.
     */
    public HashMap<Class, Counter> getCounters()
    {
        return tracker.getCounters();
    }
}
