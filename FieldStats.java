import java.util.Comparator;
import java.util.LinkedHashMap;
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

    /**
     * Construct a FieldStats object.
     */
    public FieldStats()
    {
        // Set up a collection for counters for each type of animal that
        // we might find
        counters = new LinkedHashMap<>();
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

        syncCounts(field);
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
    private void reset()
    {
        for(Counter count : counters.values()) {
            count.reset();
        }
    }

    /**
     * Determine whether the simulation is still viable.
     * I.e., should it continue to run.
     * 
     * @return true If there is more than one species alive.
     */
    public boolean isViable(Field field)
    {
        return field.getActivePopulationTypeCount() > 1;
    }

    /**
     * Synchronise display counters with the populations tracked by Field.
     */
    private void syncCounts(Field field)
    {
        reset();

        Map<Class<?>, Integer> populations = field.getPopulationCounts();
        populations.entrySet().stream()
            .sorted(Comparator.comparing(entry -> entry.getKey().getName()))
            .forEach(entry -> {
                Counter counter = counters.get(entry.getKey());
                if(counter == null) {
                    counter = new Counter(entry.getKey().getName());
                    counters.put(entry.getKey(), counter);
                }

                counter.setCount(entry.getValue());
            });
    }
}
