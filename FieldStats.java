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
    // Counts per species class; populated on demand.
    private Map<Class<?>, Integer> counts;
    // Whether the counts are currently up to date.
    private boolean countsValid;

    /**
     * Construct a FieldStats object.
     */
    public FieldStats()
    {
        counts      = new HashMap<>();
        countsValid = true;
    }

    /**
     * Get details of what is in the field.
     * @return A string describing what is in the field.
     */
    public String getPopulationDetails(Field field)
    {
        StringBuilder buffer = new StringBuilder();
        if (!countsValid) {
            generateCounts(field);
        }
        for (Map.Entry<Class<?>, Integer> entry : counts.entrySet()) {
            buffer.append(entry.getKey().getSimpleName());
            buffer.append(": ");
            buffer.append(entry.getValue());
            buffer.append(' ');
        }
        return buffer.toString();
    }

    /**
     * Invalidate the current set of statistics; reset all counts to zero.
     */
    public void reset()
    {
        countsValid = false;
        counts.replaceAll((k, v) -> 0);
    }

    /**
     * Increment the count for one class of organism.
     * @param speciesClass The class of organism to increment.
     */
    public void incrementCount(Class<?> speciesClass)
    {
        counts.merge(speciesClass, 1, Integer::sum);
    }

    /**
     * Indicate that a count has been completed.
     */
    public void countFinished()
    {
        countsValid = true;
    }

    /**
     * Determine whether the simulation is still viable (more than one species alive).
     * @return true if there is more than one species alive.
     */
    public boolean isViable(Field field)
    {
        if (!countsValid) {
            generateCounts(field);
        }
        int nonZero = 0;
        for (int count : counts.values()) {
            if (count > 0) nonZero++;
        }
        return nonZero > 1;
    }

    /**
     * Generate counts of all species currently in the field.
     * @param field The field to generate the stats for.
     */
    private void generateCounts(Field field)
    {
        reset();
        for (int row = 0; row < field.getDepth(); row++) {
            for (int col = 0; col < field.getWidth(); col++) {
                Organism organism = field.getObjectAt(row, col);
                if (organism != null) {
                    incrementCount(organism.getClass());
                }
            }
        }
        countsValid = true;
    }
}
