import java.util.*;

/**
 * A simple model of a deer.
 * Deers age, move, breed,contract diseases, eat plants and die.
 *
 * @version 2022.03.02
 */
public class Deer extends Animal
{
    // Characteristics shared by all deers.
    private static final AnimalTraits TRAITS = new AnimalTraits(
            5,      // breeding age
            2,      // max litter size
            0.5,    // breeding probability
            40,     // max age
            9,      // max food level
            4,      // food value
            false,  // nocturnal
            new HashSet<>(Arrays.asList(Grass.class)));

    /**
     * Create a new deer. A deer may be created with age
     * zero (a new born) or with a random age.
     * @param randomAge If true, the deer will have a random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     * @param sex The gender of the deer.
     */
    public Deer(boolean randomAge, Field field, Location location, Gender sex)
    {
        super(TRAITS, field, location, randomAge, sex);
    }

    /**
     * Makes the deer stay awake regardless of the time of the day.
     * Overrides the method in the Animal class that uses nocturnality to determine if it is awake.
     */
    @Override
    public boolean isAwake(Environment environment) {
        return true;
    }

    @Override
    protected Animal createOffspring(Field field, Location location)
    {
        return new Deer(false, field, location, Randomizer.getRandomSex());
    }
}
