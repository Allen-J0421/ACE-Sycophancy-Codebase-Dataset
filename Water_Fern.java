import java.util.Random;
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

    /**
     * Create Water fern. Water_Fern are created as age zero.
     * 
     * @param time The time in the simulation.
     * @param field The field currently occupied.
     * @param location The location within the field.
     * @param parentDiseases The diseases the parent of this Water fern had.
     */
    private Water_Fern(Time time, Field field, Location location, int age)
    {
        super(time, field, location);
        canGoLand = false;
        canGoWater = true;
        this.age = age;
    }

    public Water_Fern(Time time, Field field, Location location, Set<Disease> parentDiseases)
    {
        this(time, field, location, 0);
        inheritDiseases(parentDiseases);
    }

    public Water_Fern(Time time, Field field, Location location)
    {
        this(time, field, location, rand.nextInt(MAX_AGE));
        initStartingDiseases(Simulator.diseases);
    }

    protected int getBreedingAge() { return BREEDING_AGE; }
    protected double getBreedingProbability() { return BREEDING_PROBABILITY; }
    protected int getMaxLitterSize() { return MAX_LITTER_SIZE; }

    /**
     * Returns the max age that the Water fern can have before dying.
     * @return The max age that the Water fern can have before dying.
     */
    public int getMaxAge()
    {
        return MAX_AGE;
    }

    public Plant spawnOffspring(Location loc, Set<Disease> parentDiseases)
    {
        return new Water_Fern(getTime(), getField(), loc, parentDiseases);
    }

    public Plant spawnRandom(Location loc)
    {
        return new Water_Fern(getTime(), getField(), loc);
    }
}
