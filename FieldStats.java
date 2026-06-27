import java.util.Collections;
import java.util.Map;

/**
 * Collects and provides statistical data on the state of a field.
 *
 * Call {@link #update(Field)} to refresh all counts from the current field
 * state. All other methods return values from the most recent update and
 * require no Field reference.
 *
 * Mutation (increment / reset) is fully delegated to the internal
 * {@link CounterRegistry}; this class only reads from the immutable snapshot
 * produced after each scan.
 *
 * @version 2016.02.29
 */
public class FieldStats
{
    private final CounterRegistry registry = new CounterRegistry();
    private Map<Class, Counter> snapshot   = Collections.emptyMap();

    /**
     * Scan the field and refresh all counts.
     * Also counts diseases carried by {@link Infectable} occupants.
     * @param field the field to scan
     */
    public void update(Field field)
    {
        registry.clear();
        for (Object item : field.getOccupants()) {
            registry.increment(item.getClass());
            if (item instanceof Infectable) {
                Disease disease = ((Infectable) item).getDisease();
                if (disease != null) {
                    registry.increment(disease.getClass());
                }
            }
        }
        snapshot = registry.snapshot();
    }

    /**
     * Return an immutable snapshot of population counts from the most recent
     * {@link #update(Field)} call.
     * @return map from class to its {@link Counter}
     */
    public Map<Class, Counter> getCounters()
    {
        return snapshot;
    }

    /**
     * Return a human-readable summary of all population counts.
     * @return formatted population string
     */
    public String getSummary()
    {
        StringBuilder sb = new StringBuilder();
        for (Counter info : snapshot.values()) {
            sb.append(info.getName()).append(": ").append(info.getCount()).append(' ');
        }
        return sb.toString();
    }

    /**
     * Determine whether the simulation is still viable.
     * @return true if more than one class has a non-zero count
     */
    public boolean isViable()
    {
        int nonZero = 0;
        for (Counter info : snapshot.values()) {
            if (info.getCount() > 0) {
                nonZero++;
            }
        }
        return nonZero > 1;
    }
}
