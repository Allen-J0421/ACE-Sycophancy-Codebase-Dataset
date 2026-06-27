import java.util.HashMap;

/**
 * This class collects and provides some statistical data on the state 
 * of a field. It is flexible: it will create and maintain a counter 
 * for any name of species that is found in the field.
 *
 * @version 2022.03.01
 */
public class FieldStats
{
    // Counters for each type of entity in the simulation.
    private HashMap<String, Counter> counters;
    // Whether the counters are currently up to date.
    private boolean countsValid;

    /**
     * Construct a FieldStats object.
     */
    public FieldStats()
    {
        // Set up a collection for counters for each type of specie that
        // we might find
        counters = new HashMap<>();
        countsValid = true;
    }

    /**
     * Check if species count is valid.
     *
     * @param field (Field) The simulation's field.
     */
    public void checkCountIsValid(Field field)
    {
        if(!countsValid) {
            generateCounts(field);
        }
    }
    /**
     * Get details of what is in the field.
     *
     * @return (String) A string describing what is in the field.
     */
    public int getCount(String speciesName)
    {
        Counter info = counters.get(speciesName);
        return info != null ? info.getCount() : 0;
    }
    
    /**
     * Invalidate the current set of statistics; reset all counts to zero.
     */
    public void reset()
    {
        countsValid = false;
        for(String key : counters.keySet()) {
            Counter count = counters.get(key);
            count.reset();
        }
    }

    /**
     * Increment the count for one specie.
     *
     * @param specieName (String) The name of specie to increment.
     */
    public void incrementCount(String specieName)
    {
        Counter count = counters.get(specieName);
        if(count == null) {
            // We do not have a counter for this specie yet.
            // Create one.
            count = new Counter();
            counters.put(specieName, count);
        }
        count.increment();
    }

    /**
     * Indicate that a specie count has been completed.
     */
    public void countFinished()
    {
        countsValid = true;
    }

    /**
     * Determine whether the simulation is still viable. I.e., should it continue to run.
     *
     * @return (boolean) true If there is more than one species alive.
     */
    public boolean isViable(Field field)
    {
        // How many counts are non-zero.
        int nonZero = 0;
        if(!countsValid) {
            generateCounts(field);
        }
        for(String key : counters.keySet()) {
            Counter info = counters.get(key);
            if(info.getCount() > 0) {
                nonZero++;
            }
        }
        return nonZero > 1;
    }
    
    /**
     * Generate counts of the number of species. These are not kept up to date as species are placed in the field, but only when a request
     * is made for the information.
     * @param field (Field) The field to generate the stats for.
     */
    private void generateCounts(Field field)
    {
        reset();
        for(int row = 0; row < field.getDepth(); row++) {
            for(int col = 0; col < field.getWidth(); col++) {
                Species specie = field.getObjectAt(row, col);
                if(specie != null) {
                    incrementCount(specie.getName());
                }
            }
        }
        countsValid = true;
    }
}
