import java.util.HashMap;
import java.util.Map;

/**
 * Scans a {@link Field} to compute per-species population counts and
 * animal health totals (infected, immune). Results are retained between
 * calls so callers can read them without re-scanning the field.
 */
public class StatisticsTracker
{
    private final Map<Class, Counter> counters = new HashMap<>();
    private int infectedCount;
    private int immuneCount;

    /**
     * Scan the entire field and update all tracked statistics.
     * Must be called before reading any result method.
     *
     * @param field The field to analyse.
     */
    public void update(Field field)
    {
        for (Counter c : counters.values())
        {
            c.reset();
        }
        infectedCount = 0;
        immuneCount   = 0;

        for (int row = 0; row < field.getDepth(); row++)
        {
            for (int col = 0; col < field.getWidth(); col++)
            {
                Animal animal = (Animal) field.getObjectAt(row, col, Animal.class);
                Plant  plant  = (Plant)  field.getObjectAt(row, col, Plant.class);

                if (animal != null)
                {
                    increment(animal.getClass());
                    if (animal.getIsInfected()) infectedCount++;
                    if (animal.getIsImmune())   immuneCount++;
                }

                if (plant != null)
                {
                    increment(plant.getClass());
                }
            }
        }
    }

    /**
     * Determine whether the simulation is still viable (more than one
     * species alive). Uses the counts from the most recent {@link #update}.
     *
     * @return true if more than one species has a non-zero count.
     */
    public boolean isViable()
    {
        int nonZero = 0;
        for (Counter c : counters.values())
        {
            if (c.getCount() > 0) nonZero++;
        }
        return nonZero > 1;
    }

    /**
     * Build a human-readable population summary from the most recent
     * {@link #update}.
     *
     * @return A string listing each species and its count.
     */
    public String getPopulationDetails()
    {
        StringBuilder sb = new StringBuilder();
        for (Counter c : counters.values())
        {
            sb.append(c.getName()).append(": ").append(c.getCount()).append(' ');
        }
        return sb.toString();
    }

    /** @return Number of infected animals from the most recent {@link #update}. */
    public int getInfectedCount() { return infectedCount; }

    /** @return Number of immune animals from the most recent {@link #update}. */
    public int getImmuneCount()   { return immuneCount; }

    private void increment(Class type)
    {
        Counter c = counters.get(type);
        if (c == null)
        {
            c = new Counter(type.getName());
            counters.put(type, c);
        }
        c.increment();
    }
}
