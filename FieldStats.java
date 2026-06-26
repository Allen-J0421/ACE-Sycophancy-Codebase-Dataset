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
    private final Map<Class<?>, Counter> counters;
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
    public String getPopulationDetails(Field field)
    {
        StringBuilder buffer = new StringBuilder();
        if(!countsValid) {
            generateCounts(field);
        }
        for(Map.Entry<Class<?>, Counter> entry : counters.entrySet()) {
            Counter info = entry.getValue();
            buffer.append(info.getName());
            buffer.append(": ");
            buffer.append(info.getCount());
            buffer.append(" ");
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
        for(Counter count : counters.values()) {
            count.reset();
        }
    }

    /**
     * Increment the count for one class of animal.
     * @param animalClass The class of animal to increment.
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
     * Indicate that an animal count has been completed.
     */
    public void countFinished()
    {
        countsValid = true;
    }

    /**
     * Determine whether the simulation is still viable.
     * @return true If there is more than one species alive.
     */
    public boolean isViable(Field field)
    {
        int nonZero = 0;
        if(!countsValid) {
            generateCounts(field);
        }
        for(Counter info : counters.values()) {
            if(info.getCount() > 0) {
                nonZero++;
            }
        }
        return nonZero > 1;
    }

    /**
     * Generate counts of the number of foxes and rabbits.
     * @param field The field to generate the stats for.
     */
    private void generateCounts(Field field)
    {
        reset();
        for(int row = 0; row < field.getDepth(); row++) {
            for(int col = 0; col < field.getWidth(); col++) {
                Object animal = field.getObjectAt(row, col);
                Plant plant = field.getPlantAt(row, col);
                if(animal != null) {
                    incrementCount(animal.getClass());
                }
                if(plant != null) {
                    incrementCount(plant.getClass());
                }
            }
        }
        countsValid = true;
    }

    public Map<Class<?>, Counter> getCounters()
    {
        return counters;
    }
}
