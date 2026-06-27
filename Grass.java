import java.util.Random;
import java.util.Set;

/**
 * A simple model of grass.
 * Grass age, take in water and sunlight, and die.
 *
 * @version 27.02.22
 */
public class Grass extends Plant
{
    // The age at which grass can start to breed.
    private static final int BREEDING_AGE = 5;
    // The age to which grass can live.
    private static final int MAX_AGE = 250;
    // The likelihood of grass breeding.
    private static final double BREEDING_PROBABILITY = 0.8;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 12;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    // String name for the grass
    public static final String name = "Grass";

    /**
     * Create grass. Grass are created as age zero.
     * 
     * @param time The time in the simulation.
     * @param field The field currently occupied.
     * @param location The location within the field.
     * @param parentDiseases The diseases the parent of this grass had.
     */
    public Grass(Time time, Field field, Location location, Set<Disease> parentDiseases)
    {
        super(time, field, location, name, MAX_AGE);
        initializeNewbornState(true, false, parentDiseases);
    }

    public Grass(Time time, Field field, Location location)
    {
        super(time, field, location, name, MAX_AGE);
        initializeRandomStartState(true, false);
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
     * Creates new grass 
     * If the grass are created at the start of the simulation no parentDiseases Set is given as there is no parent.
     * @param location The new location of the child
     * @param Set<Disease> The diseases that the parent had is passed down
     * @return The new grass created
     */
    public Plant birth(Location loc, Set<Disease> parentDiseases)
    {
        return new Grass(getTime(), getField(), loc, parentDiseases);
    }
}
