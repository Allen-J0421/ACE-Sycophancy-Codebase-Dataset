import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Tracks current populations by concrete organism type.
 *
 * @version 26/02/2022
 */
public class PopulationManager
{
    private final Map<Class<?>, Integer> populationCounts;

    /**
     * Create a new population manager with no tracked organisms.
     */
    public PopulationManager()
    {
        populationCounts = new LinkedHashMap<>();
    }

    /**
     * Clear all tracked populations.
     */
    public void clear()
    {
        populationCounts.clear();
    }

    /**
     * Record that an organism of the given type has been added.
     */
    public void recordAddition(Class<?> objectType)
    {
        Integer count = populationCounts.get(objectType);
        if (count == null) {
            populationCounts.put(objectType, 1);
        }
        else {
            populationCounts.put(objectType, count + 1);
        }
    }

    /**
     * Record that an organism of the given type has been removed.
     */
    public void recordRemoval(Class<?> objectType)
    {
        Integer count = populationCounts.get(objectType);
        if (count == null) {
            return;
        }

        if (count <= 1) {
            populationCounts.remove(objectType);
        }
        else {
            populationCounts.put(objectType, count - 1);
        }
    }

    /**
     * Return a snapshot of the tracked populations by concrete type.
     */
    public Map<Class<?>, Integer> getPopulationCounts()
    {
        return new LinkedHashMap<>(populationCounts);
    }

    /**
     * Return how many concrete species currently have a non-zero population.
     */
    public int getActivePopulationTypeCount()
    {
        return populationCounts.size();
    }
}
