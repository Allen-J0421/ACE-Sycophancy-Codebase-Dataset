import java.util.HashMap;
import java.util.Map;

/**
 * This class collects and provides some statistical data on the state 
 * of a field. It is flexible: it will create and maintain a counter 
 * for any class of object that is found within the field.
 *
 * @version 26/02/2022
 */
public class FieldStats
{
    // Counters for each type of entity (lion, zebra, etc.) in the simulation.
    private Map<Class<?>, Counter> counters;
    // Whether the counters are currently up to date.
    private boolean countsValid;

    /**
     * Construct a FieldStats object.
     */
    public FieldStats()
    {
        // Set up a collection for counters for each type of animal that
        // we might find
        counters = new HashMap<>();
        countsValid = true;
    }

    /**
     * Get details of what is in the field.
     * 
     * @param field The field to get the population details of.
     * 
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
     * Increment the count for one object.
     * 
     * @param objectType The class of animal to increment.
     */
    public void incrementCount(Class<?> objectType)
    {
        Counter count = counters.get(objectType);
        if(count == null) {
            count = new Counter(objectType.getName());
            counters.put(objectType, count);
        }
        count.increment();
    }

    /**
     * Indicate that an animal count has been completed.
     */
    public void countFinished()
    {
        countsValid = true;
    }

    /**
     * Determine whether the simulation is still viable.
     * I.e., should it continue to run.
     * 
     * @return true If there is more than one species alive.
     */
    public boolean isViable(Field field)
    {
        if(!countsValid)
        {
            generateCounts(field);
        }

        int nonZero = 0;
        for(Counter info : counters.values())
        {
            if(info.getCount() > 0 && ++nonZero > 1)
            {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Generate counts of the number of each animal species present.
     * Plants are not counted here; their counts are maintained
     * incrementally by {@link SimulatorView#showStatus}.
     *
     * @param field The field to generate the stats for.
     */
    private void generateCounts(Field field)
    {
        reset();
        for(int row = 0; row < field.getDepth(); row++) 
        {
            for(int col = 0; col < field.getWidth(); col++) 
            {
                Object object = field.getObjectAt(row, col, Animal.class);
                
                if(object != null) 
                {
                    incrementCount(object.getClass());
                }
            }
        }
        
        countsValid = true;
    }
}
