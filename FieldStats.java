import java.util.HashMap;
import java.util.Map;

/**
 * This class collects and provides some statistical data on the state 
 * of a field. It is flexible: it will create and maintain a counter 
 * for any class of object that is found within the field.
 *
 * @version 18.02.22 (DD:MM:YY)
 */
public class FieldStats
{
    // Counters for each type of actor in the simulation:
    private static final Map<Class<?>, Counter> counters = new HashMap<>();
    // Whether the counters are currently up to date:
    private static boolean areCountsValid = true;

    /**
     * Construct a FieldStats object.
     */
    public FieldStats()
    {
        // Set up a collection for counters for each type of actor that
        // we might find:
        counters.clear();
        areCountsValid = true;
    }

    /**
     * @return A string describing what is in the field.
     */
    public String getPopulationDetails(Field field)
    {
        StringBuilder buffer = new StringBuilder();
        
        if (!areCountsValid) generateCounts(field);
        
        String prefix = "";
        
        for (Counter info : counters.values())
        {
            buffer.append(prefix);
            buffer.append(info.getName());
            buffer.append(": ");
            buffer.append(info.getCount());
            
            prefix = ", ";
        }
        return buffer.toString();
    }
    
    /**
     * @return The counters representing population numbers.
     */
    public static Map<Class<?>, Counter> getCounters(Field field)
    {
        if (!areCountsValid) generateCounts(field);
        return counters;
    }
    
    /**
     * Invalidate the current set of statistics; reset all 
     * counts to zero.
     */
    public static void reset()
    {
        areCountsValid = false;
        
        for (Counter count : counters.values())
        {
            count.reset();
        }
    }

    /**
     * Increment the count for one class of actor.
     * 
     * @param actorClass The class of actor to increment.
     */
    public static void incrementCount(Class<?> actorClass)
    {
        Counter count = counters.get(actorClass);
        
        if (count == null)
        {
            // We do not have a counter for this species yet.
            // Create one:
            count = new Counter(actorClass.getName());
            
            counters.put(actorClass, count);
        }
        
        count.increment();
    }

    /**
     * Indicate that an actor count has been completed.
     */
    public void countFinished() { areCountsValid = true; }

    /**
     * Determine whether the simulation is still viable.
     * I.e., should it continue to run.
     * 
     * @return True if there is more than one species alive.
     */
    public boolean isViable(Field field)
    {
        // How many counts are non-zero:
        int nonZero = 0;
        
        if (!areCountsValid) generateCounts(field);
        
        for (Counter info : counters.values())
        {
            if (info.getCount() > 0) nonZero++;
        }
        
        return nonZero > 1;
    }
    
    /**
     * Generate counts of the number of actors.
     * These are not kept up to date as actors are placed in the
     * field, but only when a request is made for the information.
     * 
     * @param field The field to generate the stats for.
     */
    private static void generateCounts(Field field)
    {
        reset();
        
        for (int row = 0; row < field.getDepth(); row++)
        {
            for (int col = 0; col < field.getWidth(); col++)
            {
                Object actor = field.getObjectAt(row, col);
                
                if (actor != null) incrementCount(actor.getClass());
            }
        }
        
        areCountsValid = true;
    }
}
