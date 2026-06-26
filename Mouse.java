import java.util.*;

/**
 * A simple model of a mouse.
 * Mice can age, move, contract diseases, eat plants and die.
 *
 * @version 2022.03.02
 */
public class Mouse extends Animal
{
    // Characteristics shared by all mice.
    private static final AnimalTraits TRAITS = new AnimalTraits(
            5,      // breeding age
            4,      // max litter size
            0.5,    // breeding probability
            40,     // max age
            9,      // max food level
            2,      // food value
            false,  // nocturnal
            new HashSet<>(Arrays.asList(Grass.class)));

    /**
     * Create a new Mouse. A Mouse may be created with age
     * zero (a new born) or with a random age.
     * @param randomAge If true, the Mouse will have a random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     * @param sex The gender of the mouse.
     */
    public Mouse(boolean randomAge, Field field, Location location, Gender sex)
    {
        super(TRAITS, field, location, randomAge, sex);
    }

    /**
     * Newborn mice inherit the gender of their parent (unlike most species,
     * whose offspring gender is drawn at random).
     */
    @Override
    protected Animal createOffspring(Field field, Location location)
    {
        return new Mouse(false, field, location, sex);
    }
}
