import java.util.Collection;
import java.util.HashMap;

/**
 * This class collects and provides some statistical data on the state 
 * of a field. It is flexible: it will create and maintain a counter 
 * for any class of object that is found within the field.
 *
 * @version 2022.02.28
 */
public class FieldStats
{
    // Counters for each type of entity in the simulation.
    private HashMap<Class, Counter> counters;
    // Whether the counters are currently up to date.
    private boolean countsValid;

    /**
     * Construct a FieldStats object.
     */
    public FieldStats()
    {
        counters = new HashMap<>();
        countsValid = true;
    }

    /**
     * Invalidate the current set of statistics; reset all 
     * counts to zero.
     */
    public void reset()
    {
        countsValid = false;
        for(Class key : counters.keySet()) {
            Counter count = counters.get(key);
            count.reset();
        }
    }

    /**
     * Increment the count for one class of each object(animal, 
     * water sources, plants).
     * @param objectClass The class of object to increment.
     */
    public void incrementCount(Class objectClass)
    {
        Counter count = counters.get(objectClass);
        if(count == null) {
            count = new Counter(objectClass.getName());
            counters.put(objectClass, count);
        }
        count.increment();
    }

    /**
     * Indicate that an object count has been completed.
     */
    public void countFinished()
    {
        countsValid = true;
    }

    /**
     * @return Whether the current counts are in sync with the field.
     */
    public boolean isValid()
    {
        return countsValid;
    }

    /**
     * @return All currently known counters.
     */
    public Collection<Counter> getCounters()
    {
        return counters.values();
    }
}
