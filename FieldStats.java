import java.util.HashMap;

/**
 * This class collects and provides statistical data on the state of a field.
 * It maintains a counter for each class of object found within the field.
 *
 * @version 2022/03/02
 */
public class FieldStats
{
    // One counter per species class encountered in the simulation.
    private HashMap<Class<?>, Counter> counters;
    // Whether the counters reflect the current field state.
    private boolean countsValid;

    public FieldStats()
    {
        counters = new HashMap<>();
        countsValid = true;
    }

    /**
     * Return a string describing the current population of each species.
     */
    public String getPopulationDetails(Field field)
    {
        StringBuilder buffer = new StringBuilder();
        if(!countsValid) {
            generateCounts(field);
        }
        for(Class<?> key : counters.keySet()) {
            Counter info = counters.get(key);
            buffer.append(info.getName());
            buffer.append(": ");
            buffer.append(info.getCount());
            buffer.append(' ');
        }
        return buffer.toString();
    }

    /**
     * Invalidate the current counts; reset all counters to zero.
     */
    public void reset()
    {
        countsValid = false;
        for(Class<?> key : counters.keySet()) {
            counters.get(key).reset();
        }
    }

    /**
     * Increment the count for one species class.
     */
    public void incrementCount(Class<?> animalClass)
    {
        Counter count = counters.get(animalClass);
        if(count == null) {
            count = new Counter(animalClass.getName());
            counters.put(animalClass, count);
        }
        count.increment();
    }

    /**
     * Signal that a counting pass over the field has just completed.
     */
    public void countFinished()
    {
        countsValid = true;
    }

    /**
     * Return true if more than one species has a non-zero population.
     */
    public boolean isViable(Field field)
    {
        int nonZero = 0;
        if(!countsValid) {
            generateCounts(field);
        }
        for(Class<?> key : counters.keySet()) {
            if(counters.get(key).getCount() > 0) {
                nonZero++;
            }
        }
        return nonZero > 1;
    }

    /**
     * Scan the field and rebuild all species counts from scratch.
     */
    private void generateCounts(Field field)
    {
        reset();
        for(int row = 0; row < field.getDepth(); row++) {
            for(int col = 0; col < field.getWidth(); col++) {
                Object animal = field.getObjectAt(row, col);
                if(animal != null) {
                    incrementCount(animal.getClass());
                }
            }
        }
        countsValid = true;
    }
}
