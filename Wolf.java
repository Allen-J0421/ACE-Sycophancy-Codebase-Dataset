import java.util.*;

/**
 * A simple model of a wolf.
 * Wolves age, move, eat prey, contract diseases and die.
 *
 * @version 2022.03.02
 */
public class Wolf extends Animal
{
    private static final AnimalStats STATS = new AnimalStats(
        15,   // breedingAge
        2,    // maxLitterSize
        0.3,  // breedingProbability
        150,  // maxAge
        15,   // maxFoodLevel
        6,    // foodValue
        Set.of(Deer.class, Mouse.class, Coyote.class)
    );

    @Override
    protected AnimalStats getStats() { return STATS; }

    /**
     * Create a wolf. A wolf can be created as a new born (age zero and not hungry)
     * or with a random age and food level.
     * @param randomAge If true, the wolf will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     * @param sex The gender of the wolf.
     */
    public Wolf(boolean randomAge, Field field, Location location, Gender sex)
    {
        super(field, location, randomAge, sex);
        this.isNocturnal = true;
    }

    @Override
    protected Animal createOffspring(Field field, Location location, Gender sex)
    {
        return new Wolf(false, field, location, sex);
    }
}
