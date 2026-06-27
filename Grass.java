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

    /**
     * Create grass. Grass are created as age zero.
     * 
     * @param time The time in the simulation.
     * @param field The field currently occupied.
     * @param location The location within the field.
     * @param parentDiseases The diseases the parent of this grass had.
     */
    private Grass(Time time, Field field, Location location, int age)
    {
        super(time, field, location);
        canGoLand = true;
        canGoWater = false;
        this.age = age;
    }

    public Grass(Time time, Field field, Location location, Set<Disease> parentDiseases)
    {
        this(time, field, location, 0);
        inheritDiseases(parentDiseases);
    }

    public Grass(Time time, Field field, Location location)
    {
        this(time, field, location, rand.nextInt(MAX_AGE));
        initStartingDiseases(Simulator.diseases);
    }

    protected int getBreedingAge() { return BREEDING_AGE; }
    protected double getBreedingProbability() { return BREEDING_PROBABILITY; }
    protected int getMaxLitterSize() { return MAX_LITTER_SIZE; }

    /**
     * Returns the max age that the grass can have before dying.
     * @return The max age that the grass can have before dying.
     */
    public int getMaxAge()
    {
        return MAX_AGE;
    }

    public Plant spawnOffspring(Location loc, Set<Disease> parentDiseases)
    {
        return new Grass(getTime(), getField(), loc, parentDiseases);
    }

    public Plant spawnRandom(Location loc)
    {
        return new Grass(getTime(), getField(), loc);
    }
}
