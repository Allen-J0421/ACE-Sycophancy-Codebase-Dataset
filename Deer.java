import java.util.*;

/**
 * A simple model of a deer.
 * Deers age, move, breed, contract diseases, eat plants and die.
 *
 * @version 2022.03.02
 */
public class Deer extends Animal
{
    private static final AnimalStats STATS = new AnimalStats(
        5,    // breedingAge
        2,    // maxLitterSize
        0.5,  // breedingProbability
        40,   // maxAge
        9,    // maxFoodLevel
        4,    // foodValue
        Set.of(Grass.class)
    );

    @Override
    protected AnimalStats getStats() { return STATS; }

    /**
     * Create a new deer. A deer may be created with age zero (a new born) or with a random age.
     * @param randomAge If true, the deer will have a random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     * @param sex The gender of the deer.
     */
    public Deer(boolean randomAge, Field field, Location location, Gender sex)
    {
        super(field, location, randomAge, sex);
        this.isNocturnal = false;
    }

    /** Deer are always awake regardless of time of day. */
    @Override
    public boolean isAwake(Environment environment)
    {
        return true;
    }

    @Override
    protected Animal createOffspring(Field field, Location location, Gender sex)
    {
        return new Deer(false, field, location, sex);
    }
}
