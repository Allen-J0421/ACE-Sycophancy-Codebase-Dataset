package safari;

import java.util.HashMap;

/**
 * This class collects and provides some statistical data on the state 
 * of a field. It is flexible: it will create and maintain a counter 
 * for any class of object that is found within the field.
 *
 * @version 2022.03.01
 */
public class FieldStats
{
    // Counters for each type of entity (jaguar, gazelle, etc.) in the simulation.
    private HashMap<ActorKind, Counter> counters;
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
        countsValid = true;
    }

    /**
     * Get details of what is in the field.
     * @param field The field currently occupied.
     * @param simulator. The current simulator the simulation is being run on.
     * @return A string listing what is in the field.
     */
    public String getPopulationDetails(Field field)
    {
        StringBuffer buffer = new StringBuffer();
        if(!countsValid) {
            generateCounts(field);
        }
        for(ActorKind key : counters.keySet()) {
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
        for(ActorKind key : counters.keySet()) {
            Counter count = counters.get(key);
            count.reset();
        }
    }

    /**
     * Increment the count for one class of actor.
     * @param actor The object of animal to increment.
     * @param simulator. The current simulator the simulation is being run on.
     */
    public void incrementCount(Actor actor)
    {
        if(actor == null) {
            return;
        }
        ActorKind actorKind = actor.getKind();
        Counter count = counters.get(actorKind);
        if(count == null) {
            // We do not have a counter for this species yet.
            // Create one.
            count = new Counter(actorKind.displayName());
            counters.put(actorKind, count);
        }
        count.increment();
    }

    /**
     * Indicate that an actor count has been completed.
     */
    public void countFinished()
    {
        countsValid = true;
    }

    /**
     * Determine whether the simulation is still viable.
     * I.e., should it continue to run.
     * @param field The field currently occupied.
     * @param simulator The current simulator the simulation is being run on.
     * @return true If there is more than one species alive.
     */
    public boolean isViable(Field field)
    {
        // How many counts are non-zero.
        int nonZero = 0;
        if(!countsValid) {
            generateCounts(field);
        }
        for(ActorKind key : counters.keySet()) {
            Counter info = counters.get(key);
            if(info.getCount() > 0) {
                nonZero++;
            }
        }
        return nonZero > 1;
    }

    /**
     * Generate counts of the number of all animals.
     * These are not kept up to date as all animals
     * are placed in the field, but only when a request
     * is made for the information.
     * @param field The field to generate the stats for.
     * @param simulator The current simulator the simulation is being run on.
     */
    private void generateCounts(Field field)
    {
        reset();
        for(int row = 0; row < field.getDepth(); row++) {
            for(int col = 0; col < field.getWidth(); col++) {
                Location location = new Location(row, col);
                incrementCount(field.getTraversableActorAt(location));
                incrementCount(field.getOccupantAt(location));
            }
        }
        countsValid = true;
    }
}
