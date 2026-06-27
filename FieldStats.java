import java.util.EnumMap;

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
    // Counters for each type of entity (fox, rabbit, etc.) in the simulation.
    private EnumMap<Species, Counter> counters;
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
        counters = new EnumMap<>(Species.class);
        countsValid = true;
    }

    /**
     * Get details of what is in the field.
     * @return A string describing what is in the field.
     */
    public String getPopulationDetails(Field field)
    {
        ++step;
        StringBuffer buffer = new StringBuffer();
        if(!countsValid) {
            generateCounts(field);
        }
        for(Species species : counters.keySet()) {
            Counter info = counters.get(species);
            buffer.append(info.getName());
            buffer.append(": ");
            buffer.append(info.getCount());
            buffer.append(' ');
        }
        buffer.append("Weather: " + weather.getCurrentWeather());
        buffer.append(" Time: " + getTimeOfDay());
        return buffer.toString();
    }
    
    private String getTimeOfDay()
    {
        if(step % 80<=55)
        {
            return "Day";
        }
        return "Night";
    }
    
    /**
     * Invalidate the current set of statistics; reset all 
     * counts to zero.
     */
    public void reset()
    {
        countsValid = false;
        for(Species species : counters.keySet()) {
            Counter count = counters.get(species);
            count.reset();
        }
    }

    /**
     * Increment the count for one class of animal.
     * @param animalClass The class of animal to increment.
     */
    public void incrementCount(Species species)
    {
        Counter count = counters.get(species);
        if(count == null) {
            count = new Counter(species.getDisplayName());
            counters.put(species, count);
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
     * I.e., should it continue to run.
     * @return true If there is more than one species alive.
     */
    public boolean isViable(Field field)
    {
        // How many counts are non-zero.
        int nonZero = 0;
        if(!countsValid) {
            generateCounts(field);
        }
        for(Species species : counters.keySet()) {
            Counter info = counters.get(species);
            if(info.getCount() > 0) {
                nonZero++;
            }
        }
        return nonZero > 1;
    }
    
    /**
     * Generate counts of the number of foxes and rabbits.
     * These are not kept up to date as foxes and rabbits
     * are placed in the field, but only when a request
     * is made for the information.
     * @param field The field to generate the stats for.
     */
    private void generateCounts(Field field)
    {
        reset();
        for(int row = 0; row < field.getDepth(); row++) {
            for(int col = 0; col < field.getWidth(); col++) {
                Organism organism = field.getObjectAt(row, col);
                if(organism != null) {
                    incrementCount(organism.getSpecies());
                }
            }
        }
        countsValid = true;
    }
}
