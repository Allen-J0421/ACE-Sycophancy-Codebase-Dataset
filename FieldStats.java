import java.awt.Color;
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
     * Get details of what is in the field.
     * @param field The field to get details for.
     * @return A string describing what is in the field.
     */
    public String getPopulationDetails(Field field)
    {
        StringBuffer buffer = new StringBuffer();
        if(!countsValid) {
            generateCounts(field);
        }
        int totalCount = 0;
        for(Class key : counters.keySet()) {
            Counter info = counters.get(key);
            totalCount += info.getCount();
            buffer.append(info.getName());
            buffer.append(": ");
            buffer.append(info.getCount());
            buffer.append(' ');
        }
        buffer.append(" Total: " + totalCount);
        return buffer.toString();
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
     * Determine whether the simulation is still viable.
     * I.e., should it continue to run.
     * @field The field to generate the stats for.
     * @return true If there is more than one species alive.
     */
    public boolean isViable(Field field)
    {
        // How many counts are non-zero.
        int nonZero = 0;
        if(!countsValid) {
            generateCounts(field);
        }
        for(Class key : counters.keySet()) {
            Counter info = counters.get(key);
            if(info.getCount() > 0) {
                nonZero++;
            }
        }
        return nonZero > 1;
    }
    
    /**
     * Generate counts of every occupant currently in the field, keyed by
     * its class. This is the single, data-driven counting pass for the whole
     * simulation: it scans each cell once and counts whatever it finds there,
     * regardless of type (organism, water source, plant, ...).
     *
     * Counts are not kept up to date as objects are placed in the field, but
     * recomputed only when a request is made for the information.
     * @param field The field to generate the stats for.
     */
    private void generateCounts(Field field)
    {
        reset();
        for(int row = 0; row < field.getDepth(); row++) {
            for(int col = 0; col < field.getWidth(); col++) {
                Object occupant = field.getObjectAt(row, col);
                if(occupant != null) {
                    incrementCount(occupant.getClass());
                }
            }
        }
        countsValid = true;
    }

    /**
     * Increment the count for a single occupant's class, creating its
     * counter on first sighting.
     * @param objectClass The class of object to count.
     */
    private void incrementCount(Class objectClass)
    {
        Counter count = counters.get(objectClass);
        if(count == null) {
            count = new Counter(objectClass.getName());
            counters.put(objectClass, count);
        }
        count.increment();
    }
}
