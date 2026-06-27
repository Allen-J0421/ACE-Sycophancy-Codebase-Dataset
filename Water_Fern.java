import java.util.Random;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;

/**
 * A simple model of Water fern.
 * Water_Fern age, take in water and sunlight, and die.
 *
 * @version 27.02.22
 */
public class Water_Fern extends Plant
{
    // The age at which Water fern can start to breed.
    private static final int BREEDING_AGE = 5;
    // The age to which Water fern can live.
    private static final int MAX_AGE = 200;
    // The likelihood of Water fern breeding.
    private static final double BREEDING_PROBABILITY = 0.6;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 7;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    // String name for the Water fern
    public static final String name = "Water_Fern";

    /**
     * Create Water fern. Water_Fern are created as age zero.
     * 
     * @param time The time in the simulation.
     * @param field The field currently occupied.
     * @param location The location within the field.
     * @param parentDiseases The diseases the parent of this Water fern had.
     */
    public Water_Fern(Time time, Field field, Location location, Set<Disease> parentDiseases)
    {
        super(time, field, location);
        initializeNewbornState(false, true, parentDiseases);
    }

    public Water_Fern(Time time, Field field, Location location)
    {
        super(time, field, location);
        initializeRandomStartState(false, true, MAX_AGE, name);
    }

    /**
     * Generate a number representing the number of births,
     * if it can breed.
     * @return The number of births (may be zero).
     */
    public int breed()
    {
        return breedOffspringCount(BREEDING_AGE, BREEDING_PROBABILITY, MAX_LITTER_SIZE, rand);
    }

    /**
     * Returns the String name.
     * @return The String name.
     */
    public String getActorName (){
        return name;
    }

    /**
     * Returns the max age that the Water fern can have before dying.
     * @return The max age that the Water fern can have before dying.
     */
    public int getMaxAge()
    {
        return MAX_AGE;
    }

    /**
     * Creates new Water fern 
     * If the Water fern are created at the start of the simulation no parentDiseases Set is given as there is no parent.
     * @param location The new location of the child
     * @param Set<Disease> The diseases that the parent had is passed down
     * @return The new Water fern created
     */
    public Plant birth(Location loc, Set<Disease> parentDiseases)
    {
        return new Water_Fern(getTime(), getField(), loc, parentDiseases);
    }
}
