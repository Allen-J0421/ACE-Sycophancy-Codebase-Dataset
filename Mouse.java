import java.util.*;

/**
 * A simple model of a mouse.
 * Mice can age, move, contract diseases, eat plants and die.
 *
 * @version 2022.03.02
 */
public class Mouse extends Animal
{
    private static final AnimalStats STATS = new AnimalStats(
        5,    // breedingAge
        4,    // maxLitterSize
        0.5,  // breedingProbability
        40,   // maxAge
        9,    // maxFoodLevel
        2,    // foodValue
        Set.of(Grass.class),
        false // diurnal
    );

    @Override
    protected AnimalStats getStats() { return STATS; }

    /**
     * Create a new mouse. A mouse may be created with age zero (a new born) or with a random age.
     * @param randomAge If true, the mouse will have a random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     * @param sex The gender of the mouse.
     */
    public Mouse(boolean randomAge, Field field, Location location, Gender sex)
    {
        super(field, location, randomAge, sex);
    }

    @Override
    protected Animal createOffspring(Field field, Location location, Gender sex)
    {
        return new Mouse(false, field, location, sex);
    }
}
