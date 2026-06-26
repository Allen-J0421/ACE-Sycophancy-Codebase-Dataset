package savannah.model;

import java.util.EnumMap;
import java.util.Map;

/**
 * This class collects and provides some statistical data on the state
 * of a field. It maintains counters for each species found within the field.
 *
 * @version 26/02/2022
 */
public class FieldStats
{
    // Counters for each type of entity (lion, zebra, etc.) in the simulation.
    private Map<SpeciesType, Counter> counters;
    // Whether the counters are currently up to date.
    private boolean countsValid;

    /**
     * Construct a FieldStats object.
     */
    public FieldStats()
    {
        // Set up a collection for counters for each type of animal that
        // we might find
        counters = new EnumMap<>(SpeciesType.class);
        countsValid = true;
    }

    /**
     * Get details of what is in the field.
     * 
     * @param field The field to get the population details of.
     * 
     * @return A string describing what is in the field.
     */
    public String getPopulationDetails(Field field)
    {
        StringBuilder buffer = new StringBuilder();
        if(!countsValid) {
            generateCounts(field);
        }
        for(SpeciesType key : counters.keySet()) {
            Counter info = counters.get(key);
            buffer.append(info.getName());
            buffer.append(": ");
            buffer.append(info.getCount());
            buffer.append(' ');
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
        for(SpeciesType key : counters.keySet()) {
            Counter count = counters.get(key);
            count.reset();
        }
    }

    /**
     * Increment the count for one species.
     *
     * @param speciesType The species to increment.
     */
    public void incrementCount(SpeciesType speciesType)
    {
        Counter count = counters.get(speciesType);
        if(count == null) {
            // We do not have a counter for this species yet.
            // Create one.
            count = new Counter(SpeciesRegistry.INSTANCE.getDisplayName(speciesType));
            counters.put(speciesType, count);
        }
        count.increment();
    }

    /**
     * Indicate that a species count has been completed.
     */
    public void countFinished()
    {
        countsValid = true;
    }

    /**
     * Determine whether the simulation is still viable.
     * I.e., should it continue to run.
     * 
     * @return true If there is more than one species alive.
     */
    public boolean isViable(Field field)
    {
        // How many counts are non-zero.
        int nonZero = 0;
        if(!countsValid) 
        {
            generateCounts(field);
        }
        
        for(SpeciesType key : counters.keySet()) 
        {
            Counter info = counters.get(key);
            if(info.getCount() > 0) 
            {
                nonZero++;
            }
        }
        return nonZero > 1;
    }
    
    /**
     * Generate counts of the number of all species of 
     * animal and plant.
     * These are not kept up to date as animals and plants
     * are placed in the field, but only when a request
     * is made for the information.
     * 
     * @param field The field to generate the stats for.
     */
    private void generateCounts(Field field)
    {
        reset();
        for(int row = 0; row < field.getDepth(); row++) 
        {
            for(int col = 0; col < field.getWidth(); col++) 
            {
                Animal animal = (Animal) field.getObjectAt(row, col, Animal.class);
                Plant plant = (Plant) field.getObjectAt(row, col, Plant.class);

                if(animal != null) 
                {
                    incrementCount(animal.getSpeciesType());
                }
                if (plant != null)
                {
                    incrementCount(plant.getSpeciesType());
                }
            }
        }
        
        countsValid = true;
    }
}
