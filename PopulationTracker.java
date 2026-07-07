import java.util.HashMap;
/**
 * Collects and validates population counts for all species present in the field.
 * Tracks per-species counters, lazily regenerates them from the field when stale,
 * and determines whether the simulation remains viable.
 *
 * @version 1.0
 */
public class PopulationTracker
{

    /*///////////////////////////////////////////////////////////////
                                 STATE
    //////////////////////////////////////////////////////////////*/

    // Counters for each type of entity (fox, rabbit, etc.) in the simulation.
    private HashMap<Class, Counter> counters;
    // Whether the counters are currently up to date.
    private boolean countsValid;

    /*///////////////////////////////////////////////////////////////
                              CONSTRUCTOR
    //////////////////////////////////////////////////////////////*/

    /**
     * Constructs a PopulationTracker with an empty, valid counter set.
     */
    public PopulationTracker()
    {
        counters = new HashMap<>();
        countsValid = true;
    }

    /*///////////////////////////////////////////////////////////////
                         DATA COLLECTION
    //////////////////////////////////////////////////////////////*/

    /**
     * Invalidate the current set of statistics; reset all counts to zero.
     */
    public void reset()
    {
        countsValid = false;
        for(Class key : counters.keySet()) {
            Counter count = counters.get(key);
            count.reset();
        }
    }

    /**
     * Increment the count for one class of entity.
     *
     * @param animalClass The class of entity to increment.
     */
    public void incrementCount(Class animalClass)
    {
        Counter count = counters.get(animalClass);
        if(count == null) {
            count = new Counter(animalClass.getName());
            counters.put(animalClass, count);
        }
        count.increment();
    }

    /**
     * Indicate that an entity count has been completed.
     */
    public void countFinished()
    {
        countsValid = true;
    }

    /*///////////////////////////////////////////////////////////////
                         MONITORING / VALIDATION
    //////////////////////////////////////////////////////////////*/

    /**
     * Get details of what is in the field.
     *
     * @param field The field to describe.
     * @return A string describing what is in the field.
     */
    public String getPopulationDetails(Field field)
    {
        StringBuffer buffer = new StringBuffer();
        if(!countsValid) {
            generateCounts(field);
        }
        for(Class key : counters.keySet()) {
            Counter info = counters.get(key);
            buffer.append(info.getName());
            buffer.append(": ");
            buffer.append(info.getCount());
            buffer.append(" ");
        }
        return buffer.toString();
    }

    /**
     * Determine whether the simulation is still viable (more than one species alive).
     *
     * @param field The field to evaluate.
     * @return true if there is more than one species alive.
     */
    public boolean isViable(Field field)
    {
        int nonZero = 0;
        if(!countsValid) {
            generateCounts(field);
        }
        for(Class key : counters.keySet()) {
            Counter info = counters.get(key);
            if(info.getCount() > 0) {
                nonZero++;
            }
        }
        return nonZero > 1;
    }

    /**
     * Returns the raw counter map.
     *
     * @return the map of class to Counter.
     */
    public HashMap<Class, Counter> getCounters()
    {
        return counters;
    }

    /*///////////////////////////////////////////////////////////////
                           PRIVATE HELPERS
    //////////////////////////////////////////////////////////////*/

    /**
     * Scan the field and generate fresh counts for every entity present.
     *
     * @param field The field to scan.
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
}
