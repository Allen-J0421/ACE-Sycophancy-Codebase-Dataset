import java.util.*;

/**
 * A simple model of a wolf.
 * Wolves age, move, eat prey,contract diseases and die.
 *
 * @version 2022.03.02
 */
public class Wolf extends Animal
{
    // Characteristics shared by all wolves.
    private static final AnimalTraits TRAITS = new AnimalTraits(
            15,     // breeding age
            2,      // max litter size
            0.3,    // breeding probability
            150,    // max age
            15,     // max food level
            6,      // food value
            true,   // nocturnal
            new HashSet<>(Arrays.asList(Deer.class, Mouse.class, Coyote.class)));

    /**
     * Create a wolf. A wolf can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * @param randomAge If true, the wolf will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     * @param sex The gender of the wolf.
     */
    public Wolf(boolean randomAge, Field field, Location location, Gender sex)
    {
        super(TRAITS, field, location, randomAge, sex);
    }

    @Override
    protected Animal createOffspring(Field field, Location location)
    {
        return new Wolf(false, field, location, Randomizer.getRandomSex());
    }
}
