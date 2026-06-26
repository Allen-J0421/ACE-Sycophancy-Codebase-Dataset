import java.util.LinkedHashMap;
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
    private final Map<Class<? extends Actor>, Counter> counters;
    // Whether the counters are currently up to date:
    private boolean areCountsValid;

    /**
     * Construct a FieldStats object.
     */
    public FieldStats()
    {
        // Set up a collection for counters for each type of actor that
        // we might find:
        counters = new LinkedHashMap<>();
        areCountsValid = true;
    }

    /**
     * @return A string describing what is in the field.
     */
    public String getPopulationDetails(Field field)
    {
        StringBuffer buffer = new StringBuffer();
        
        if (!areCountsValid) generateCounts(field);
        
        String prefix = "";
        
        for (Map.Entry<Class<? extends Actor>, Counter> entry : counters.entrySet())
        {
            Counter info = entry.getValue();
            
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
    public Map<Class<? extends Actor>, Counter> getCounters(Field field)
    {
        if (!areCountsValid) generateCounts(field);
        return counters;
    }
    
    /**
     * Invalidate the current set of statistics; reset all 
     * counts to zero.
     */
    public void reset()
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
    public void incrementCount(Class<? extends Actor> actorClass)
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
    private void generateCounts(Field field)
    {
        reset();

        field.forEachLocation((location, actor) ->
        {
            if (actor != null)
            {
                incrementCount(actor.getClass());
            }
        });
        
        areCountsValid = true;
    }
}
