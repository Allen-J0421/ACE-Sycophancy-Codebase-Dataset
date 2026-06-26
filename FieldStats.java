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
    // Counters for each type of entity (dingo, ant, etc.) in the simulation.
    private HashMap<Class<?>, Counter> counters;

    /**
     * Construct a FieldStats object.
     */
    public FieldStats() {
        // Set up a collection for counters for each type of animal that
        // we might find
        counters = new HashMap<>();
    }

    /**
     * Get details of what is in the field.
     * @return A string describing what is in the field.
     */
    public String getPopulationDetails() {
        StringBuffer buffer = new StringBuffer();
        for(Class<?> key : counters.keySet()) {
            Counter info = counters.get(key);
            buffer.append(info.getName());
            buffer.append(": ");
            buffer.append(info.getCount());
            buffer.append(' ');
        }
        return buffer.toString();
    }

    /**
     * Reset all counts to zero.
     */
    public void reset() {
        for(Class<?> key : counters.keySet()) {
            Counter count = counters.get(key);
            count.reset();
        }
    }

    /**
     * Increment the count for one class of occupant.
     * @param occupantClass The class of occupant to increment.
     */
    public void incrementCount(Class<?> occupantClass) {
        Counter count = counters.get(occupantClass);
        if(count == null) {
            // We do not have a counter for this species yet.
            // Create one.
            count = new Counter(occupantClass.getName());
            counters.put(occupantClass, count);
        }
        count.increment();
    }

    /**
     * Decrement the count for one class of occupant.
     * @param occupantClass The class of occupant to decrement.
     */
    public void decrementCount(Class<?> occupantClass) {
        Counter count = counters.get(occupantClass);
        if(count != null) {
            count.decrement();
        }
    }

    /**
     * Record that an occupant has been added to the field.
     */
    public void occupantAdded(FieldOccupant occupant) {
        if(occupant != null) {
            incrementCount(occupant.getClass());
        }
    }

    /**
     * Record that an occupant has been removed from the field.
     */
    public void occupantRemoved(FieldOccupant occupant) {
        if(occupant != null) {
            decrementCount(occupant.getClass());
        }
    }

    /**
     * Determine whether the simulation is still viable.
     * I.e., should it continue to run.
     * @return true If there is more than one species alive.
     */
    public boolean isViable() {
        // How many counts are non-zero.
        int nonZero = 0;
        for(Class<?> key : counters.keySet()) {
            Counter info = counters.get(key);
            if(info.getCount() > 0) {
                nonZero++;
            }
        }
        return nonZero > 1;
    }

}
