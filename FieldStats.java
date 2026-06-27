import java.util.LinkedHashMap;
import java.util.Map;

/**
 * This class collects and provides statistical data on the state of a field.
 * It creates and maintains counts for every living-being class found.
 *
 * @version 2016.02.29
 */
public final class FieldStats
{
    // Counts for each type of entity in the simulation.
    private final Map<Class<?>, Integer> counts;
    // Whether the counters are currently up to date.
    private boolean countsCurrent;

    /**
     * Construct a FieldStats object.
     */
    public FieldStats()
    {
        // Set up a collection for each type of living being found.
        counts = new LinkedHashMap<>();
        countsCurrent = true;
    }

    /**
     * Get details of what is in the field.
     * @return A string describing what is in the field.
     */
    public String getPopulationDetails()
    {
        StringBuilder buffer = new StringBuilder();
        for(Map.Entry<Class<?>, Integer> entry : counts.entrySet()) {
            buffer.append(entry.getKey().getName());
            buffer.append(": ");
            buffer.append(entry.getValue());
            buffer.append(' ');
        }
        return buffer.toString();
    }
    
    /**
     * Clear the current counts and mark them as stale.
     */
    public void clear()
    {
        countsCurrent = false;
        counts.replaceAll((key, count) -> 0);
    }

    /**
     * Record one living being in the current count.
     * @param being The living being to record.
     */
    public void record(LivingBeing being)
    {
        counts.merge(being.getClass(), 1, Integer::sum);
    }

    /**
     * Indicate that counting has been completed.
     */
    public void markCurrent()
    {
        countsCurrent = true;
    }

    /**
     * Determine whether the simulation is still viable.
     * I.e., should it continue to run.
     * @return true If there is more than one species alive.
     */
    public boolean isViable(Field field)
    {
        ensureCurrent(field);

        int nonZero = 0;
        for(int count : counts.values()) {
            if(count > 0) {
                nonZero++;
            }
        }
        return nonZero > 1;
    }
    
    /**
     * Generate counts of the number of living beings.
     * These are not kept up to date as living beings
     * are placed in the field, but only when a request
     * is made for the information.
     * @param field The field to generate the stats for.
     */
    private void generateCounts(Field field)
    {
        clear();
        for(int row = 0; row < field.getDepth(); row++) {
            for(int col = 0; col < field.getWidth(); col++) {
                LivingBeing being = field.getLivingBeingAt(row, col);
                if(being != null) {
                    record(being);
                }
            }
        }
        countsCurrent = true;
    }

    private void ensureCurrent(Field field)
    {
        if(!countsCurrent) {
            generateCounts(field);
        }
    }
}
