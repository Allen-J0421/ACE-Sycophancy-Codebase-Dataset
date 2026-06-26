import java.util.*;

/**
 * A simple model of a coyote.
 * Coyotes age, move, eat prey, contract diseases and die.
 *
 * @version 2022.03.3
 */
public class Coyote extends Animal
{
    private static final AnimalStats STATS = new AnimalStats(
        15,   // breedingAge
        2,    // maxLitterSize
        0.4,  // breedingProbability
        150,  // maxAge
        15,   // maxFoodLevel
        5,    // foodValue
        Set.of(Deer.class, Mouse.class)
    );

    @Override
    protected AnimalStats getStats() { return STATS; }

    /**
     * Create a coyote. A coyote can be created as a new-born (age zero and not hungry)
     * or with a random age and food level.
     * @param randomAge If true, the coyote will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     * @param sex The gender of the coyote.
     */
    public Coyote(boolean randomAge, Field field, Location location, Gender sex)
    {
        super(field, location, randomAge, sex);
        this.isNocturnal = true;
    }

    @Override
    protected Animal createOffspring(Field field, Location location, Gender sex)
    {
        return new Coyote(false, field, location, sex);
    }
}
