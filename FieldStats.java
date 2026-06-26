import java.util.HashMap;
import java.util.Map;

/**
 * This class collects and provides some statistical data on the state 
 * of a field. It is flexible: it will create and maintain a counter 
 * for any class of object that is found within the field.
 *
 * @version 2016.02.29
 */
public class FieldStats
{
    // Counters for each type of entity in the simulation.
    private final Map<Class<?>, Counter> counters;
    // Whether the counters are currently up to date.
    private boolean countsValid;

    /**
     * Construct a FieldStats object.
     */
    public FieldStats()
    {
        // Set up a collection for counters for each type of entity that
        // we might find.
        counters = new HashMap<>();
        countsValid = true;
    }

    /**
     * Get details of what is in the field.
     * @return A string describing what is in the field.
     */
    public String getPopulationDetails(Field field)
    {
        StringBuilder buffer = new StringBuilder();
        if(!countsValid) {
            generateCounts(field);
        }
        for(Counter info : counters.values()) {
            buffer.append(info.getName());
            buffer.append(": ");
            buffer.append(info.getCount());
            buffer.append(' ');
        }
        return buffer.toString();
    }
    
    /**
     * Invalidate the current set of statistics; reset all 
     * counts to zero.
     */
    public void reset()
    {
        countsValid = false;
        for(Counter count : counters.values()) {
            count.reset();
        }
    }

    /**
     * Increment the count for one class of entity.
     * @param entityClass The class of entity to increment.
     */
    public void incrementCount(Class<?> entityClass)
    {
        Counter count = counters.get(entityClass);
        if(count == null) {
            // We do not have a counter for this species yet.
            // Create one.
            count = new Counter(entityClass.getName());
            counters.put(entityClass, count);
        }
        count.increment();
    }

    /**
     * Indicate that an entity count has been completed.
     */
    public void countFinished()
    {
        countsValid = true;
    }

    /**
     * Determine whether the simulation is still viable.
     * I.e., should it continue to run.
     * @return true If there is more than one species alive.
     */
    public boolean isViable(Field field)
    {
        // How many counts are non-zero.
        int nonZero = 0;
        if(!countsValid) {
            generateCounts(field);
        }
        for(Counter info : counters.values()) {
            if(info.getCount() > 0) {
                nonZero++;
            }
        }
        return nonZero > 1;
    }
    
    /**
     * Generate counts of the number of entities in the field.
     * These are calculated only when a request is made for the information.
     * @param field The field to generate the stats for.
     */
    private void generateCounts(Field field)
    {
        reset();
        for(int row = 0; row < field.getDepth(); row++) {
            for(int col = 0; col < field.getWidth(); col++) {
                Organism organism = Placement.getOccupant(field, row, col);
                if(organism != null) {
                    incrementCount(organism.getClass());
                }
            }
        }
        countsValid = true;
    }
}
