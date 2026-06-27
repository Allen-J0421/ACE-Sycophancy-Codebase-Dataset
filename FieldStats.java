import java.util.HashMap;

/**
 * Collects and provides statistical data on the state of a field.
 *
 * Call {@link #update(Field)} to refresh all counts from the current field
 * state. All other methods return values from the most recent update and
 * require no Field reference.
 *
 * @version 2016.02.29
 */
public class FieldStats
{
    private HashMap<Class, Counter> counters = new HashMap<>();

    /**
     * Scan the field and refresh all counts.
     * Also counts diseases carried by {@link Infectable} occupants.
     * @param field the field to scan
     */
    public void update(Field field)
    {
        for (Counter c : counters.values()) {
            c.reset();
        }
        for (Object item : field.getOccupants()) {
            incrementCount(item.getClass());
            if (item instanceof Infectable) {
                Disease disease = ((Infectable) item).getDisease();
                if (disease != null) {
                    incrementCount(disease.getClass());
                }
            }
        }
    }

    /**
     * Return the raw counters keyed by class, reflecting the most recent
     * {@link #update(Field)} call.
     * @return the population counters
     */
    public HashMap<Class, Counter> getCounters()
    {
        return counters;
    }

    /**
     * Return a human-readable summary of all population counts.
     * @return formatted population string
     */
    public String getSummary()
    {
        StringBuilder sb = new StringBuilder();
        for (Counter info : counters.values()) {
            sb.append(info.getName()).append(": ").append(info.getCount()).append(' ');
        }
        return sb.toString();
    }

    /**
     * Determine whether the simulation is still viable.
     * @return true if more than one species has a non-zero count
     */
    public boolean isViable()
    {
        int nonZero = 0;
        for (Counter info : counters.values()) {
            if (info.getCount() > 0) {
                nonZero++;
            }
        }
        return nonZero > 1;
    }

    private void incrementCount(Class clazz)
    {
        Counter c = counters.get(clazz);
        if (c == null) {
            c = new Counter(clazz.getName());
            counters.put(clazz, c);
        }
        c.increment();
    }
}
