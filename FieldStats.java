import java.util.HashMap;
import java.util.Map;

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
    // Responsible for traversing the field and tallying species counts.
    private final FieldAnalyzer analyzer;

    /**
     * Construct a FieldStats object.
     */
    public FieldStats()
    {
        counters = new HashMap<>();
        countsValid = true;
        analyzer = new FieldAnalyzer();
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
    public int getCount (String speciesName)
    {
        Counter info = counters.get(speciesName);
        return info.getCount();
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
            count = new Counter(specieName);
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
     * Generate counts of the number of species by delegating field traversal
     * to the FieldAnalyzer, then loading the results into the counters map.
     *
     * @param field (Field) The field to generate the stats for.
     */
    private void generateCounts(Field field)
    {
        reset();
        HashMap<String, Integer> totals = analyzer.countSpecies(field);
        for (Map.Entry<String, Integer> entry : totals.entrySet()) {
            for (int i = 0; i < entry.getValue(); i++) {
                incrementCount(entry.getKey());
            }
        }
        countsValid = true;
    }
}
