import java.util.HashMap;
import java.util.Map;

/**
 * This class collects and provides some statistical data on the state 
 * of a field. It is flexible: it will create and maintain a counter 
 * for any class of object that is found within the field.
 * 
 * @version 2022.03.02
 */
public class FieldStats
{
    // A shared weather object between the organisms and the simulator
    public static final Weather weather = Weather.getWeather();
    // Counters for each type of entity in the simulation.
    private final Map<Class<?>, Counter> counters;
    // Whether the counters are currently up to date.
    private boolean countsValid;
    // The step of the simulation
    private int step;

    /**
     * Construct a FieldStats object.
     */
    public FieldStats(int steps)
    {
        // Set up a collection for counters for each type of animal that
        // we might find
        step = steps;
        counters = new HashMap<>();
        countsValid = true;
    }

    /**
     * Get details of what is in the field.
     * @return A string describing what is in the field.
     */
    public String getPopulationDetails(Field field)
    {
        ++step;
        StringBuilder buffer = new StringBuilder();
        if(!countsValid) {
            generateCounts(field);
        }
        for(Counter info : counters.values()) {
            buffer.append(info.getName());
            buffer.append(": ");
            buffer.append(info.getCount());
            buffer.append(' ');
        }
        buffer.append("Weather: ");
        buffer.append(weather.getCurrentWeather());
        buffer.append(" Time: ");
        buffer.append(getTimeOfDay());
        return buffer.toString();
    }
    
    private String getTimeOfDay()
    {
        return DayCycle.getTimeOfDay(step);
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
        Counter count = counters.computeIfAbsent(animalClass, key -> new Counter(key.getName()));
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
     * I.e., should it continue to run.
     * @return true If there is more than one species alive.
     */
    public boolean isViable(Field field)
    {
        if(!countsValid) {
            generateCounts(field);
        }
        return countNonZeroCounters() > 1;
    }

    /**
     * Count how many organism types are currently present.
     */
    private int countNonZeroCounters()
    {
        int nonZero = 0;
        for(Counter info : counters.values()) {
            if(info.getCount() > 0) {
                nonZero++;
            }
        }
        return nonZero;
    }
    
    /**
     * Generate counts of the organisms in the field.
     * These are counted on demand rather than updated as organisms move.
     * @param field The field to generate the stats for.
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
