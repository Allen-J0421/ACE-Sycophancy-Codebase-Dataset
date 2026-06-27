import java.util.HashMap;

/**
 * This class collects and provides some statistical data on the state
 * of a field. It is flexible: it will create and maintain a counter
 * for any entity type found within the field.
 *
 * <p>Entity types are identified by their {@link Viewable#getDisplayName()}
 * string rather than their runtime {@code Class}, so no class-specific
 * knowledge is needed here. Whether a type counts toward the viability
 * check is determined by {@link Viewable#countsTowardViability()}.
 *
 * @version 2016.02.29
 */
public class FieldStats
{
    // Counters for each entity type (keyed by display name).
    private HashMap<String, Counter> counters;
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
     * @return A string describing what is in the field.
     */
    public String getPopulationDetails(Field<Entity> field)
    {
        StringBuffer buffer = new StringBuffer();
        if (!countsValid) {
            generateCounts(field);
        }
        for (Counter info : counters.values()) {
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
        for (Counter count : counters.values()) {
            count.reset();
        }
    }

    /**
     * Increment the count for the given entity's type.
     * @param entity The entity whose type count should be incremented.
     */
    public void incrementCount(Entity entity)
    {
        String name = entity.getDisplayName();
        Counter count = counters.get(name);
        if (count == null) {
            count = new Counter(name, entity.countsTowardViability());
            counters.put(name, count);
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
     * @return true if there is more than one animal species alive.
     */
    public boolean isViable(Field<Entity> field)
    {
        int nonZero = 0;
        if (!countsValid) {
            generateCounts(field);
        }
        for (Counter info : counters.values()) {
            if (info.getCount() > 0 && info.countsTowardViability()) {
                nonZero++;
            }
        }
        return nonZero > 1;
    }

    /**
     * Generate counts of the number of entities.
     * These are not kept up to date as entities are placed in the field,
     * but only when a request is made for the information.
     * @param field The field to generate the stats for.
     */
    private void generateCounts(Field<Entity> field)
    {
        reset();
        for (int row = 0; row < field.getDepth(); row++) {
            for (int col = 0; col < field.getWidth(); col++) {
                Entity entity = field.getObjectAt(row, col);
                if (entity != null) {
                    incrementCount(entity);
                }
            }
        }
        countsValid = true;
    }
}
