import java.util.*;

/**
 * A simple model of a coyote.
 * Coyotes age, move, eat prey,contract diseases and die.
 * @version 2022.03.3
 */
public class Coyote extends Animal
{
    // Characteristics shared by all coyotes.
    private static final AnimalTraits TRAITS = new AnimalTraits(
            15,     // breeding age
            2,      // max litter size
            0.4,    // breeding probability
            150,    // max age
            15,     // max food level
            5,      // food value
            true,   // nocturnal
            new HashSet<>(Arrays.asList(Deer.class, Mouse.class)));

    /**
     * Create a coyote. A coyote can be created as a new-born (age zero
     * and not hungry) or with a random age and food level.
     * @param randomAge If true, the coyote will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     * @param sex The gender of the coyote.
     */
    public Coyote(boolean randomAge, Field field, Location location, Gender sex)
    {
        super(TRAITS, field, location, randomAge, sex);
    }

    @Override
    protected Animal createOffspring(Field field, Location location)
    {
        return new Coyote(false, field, location, Randomizer.getRandomSex());
    }
}
