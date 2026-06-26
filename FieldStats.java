import java.util.*;

/**
 * This class collects and provides some statistical data on the state 
 * of a field. It is flexible: it will create and maintain a counter 
 * for any class of object that is found within the field.
 *
 * @version 2022.03.3
 */
public class FieldStats
{
    // Counters for each type of entity (coyote, rabbit, etc.) in the simulation.
    private HashMap<Class, Counter> counters;
    private Counter diseaseCounter;
    // Whether the counters are currently up to date.
    private boolean countsValid;

    /**
     * Construct a FieldStats object.
     */
    public FieldStats()
    {
        // Set up a collection for counters for each type of animal that
        // we might find
        counters = new HashMap<>();
        diseaseCounter = new Counter("Diseased");
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
        for(Class key : counters.keySet()) {
            Counter info = counters.get(key);
            buffer.append(info.getName());
            buffer.append(": ");
            buffer.append(info.getCount());
            buffer.append(' ');
        }
        // displaying the number of diseased animals
        buffer.append(" | ");
        buffer.append(getDiseasedPopulation());
        return buffer.toString();
    }

    public int getCount(Class cls)
    {
        Counter count = counters.get(cls);
        if(count != null){
            return count.getCount();
        }
        return 0;
    }

    public String getCountDetails(Class cls)
    {
        return cls.getName() + ": " + getCount(cls);
    }
    
    /**
     * Invalidate the current set of statistics; reset all 
     * counts to zero.
     */
    public void reset()
    {
        countsValid = false;
        for(Class key : counters.keySet()) {
            Counter count = counters.get(key);
            count.reset();
        }
        diseaseCounter.reset();
    }

    /**
     * Increment the count for one class of animal.
     * @param animalClass The class of animal to increment.
     */
    public void incrementCount(Class animalClass)
    {
        Counter count = counters.get(animalClass);
        if(count == null) {
            // We do not have a counter for this species yet.
            // Create one.
            count = new Counter(animalClass.getName());
            counters.put(animalClass, count);
        }
        count.increment();
    }

    /**
     * Increment the count for number of infencted animals.
     */
    public void incrementDiseasedCount()
    {
        diseaseCounter.increment();
    }

    /**
     * Returns a string containing the current number of infected animals.
     * @return A string containing the current number of infected animals.
     */
    public String getDiseasedPopulation()
    {
        return  "Diseased: " + diseaseCounter.getCount();
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
        Set<Class> aliveSpecies = new HashSet<>();
        int nonZero = 0;
        if(!countsValid) {
            generateCounts(field);
        }
        for(Class key : counters.keySet()) {
            Counter info = counters.get(key);
            if(info.getCount() > 0) {
                nonZero++;
                aliveSpecies.add(key);
            }
        }

        // if viability is to be defined based on the species in the field as well, use the aliveSpecies Set
        // eg: return nonZero > 2 && aliveSpecies.contains(Deer.class)

        return nonZero > 2;
    }
    
    /**
     * Generate counts of the number of organisms.
     * These are not kept up to date as organisms.
     * are placed in the field, but only when a request
     * is made for the information.
     * @param field The field to generate the stats for.
     */
    private void generateCounts(Field field)
    {
        reset();
        for(int row = 0; row < field.getDepth(); row++) {
            for(int col = 0; col < field.getWidth(); col++) {
                Object actor = field.getObjectAt(row, col);
                if(actor != null) {
                    incrementCount(actor.getClass());
                    if(actor instanceof Organism){
                        if(((Organism) actor).isDiseased()){
                            incrementDiseasedCount();
                        }
                    }
                }
            }
        }
        countsValid = true;
    }
}
