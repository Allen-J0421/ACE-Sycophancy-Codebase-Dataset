import java.util.*;

/**
 * A simple model of a eagle.
 * Eagles age, move,contract diseases, eat prey, and die.
 *
 * As an apex species an eagle does not die from overcrowding, and it cannot
 * forage while it is raining.
 *
 * @version 2022.03.02
 */
public class Eagle extends Animal
{
    // Characteristics shared by all eagles.
    private static final AnimalTraits TRAITS = new AnimalTraits(
            15,     // breeding age
            2,      // max litter size
            0.4,    // breeding probability
            150,    // max age
            14,     // max food level
            5,      // food value
            false,  // nocturnal
            new HashSet<>(Arrays.asList(Deer.class, Coyote.class, Mouse.class)));

    /**
     * Create a eagle. A eagle can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * @param randomAge If true, the eagle will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     * @param sex The gender of the eagle.
     */
    public Eagle(boolean randomAge, Field field, Location location, Gender sex)
    {
        super(TRAITS, field, location, randomAge, sex);
    }

    @Override
    protected Animal createOffspring(Field field, Location location)
    {
        return new Eagle(false, field, location, Randomizer.getRandomSex());
    }

    /**
     * Eagles sit above everyone else and so are not killed by overcrowding.
     */
    @Override
    protected boolean diesFromOvercrowding()
    {
        return false;
    }

    /**
     * Eagles cannot find food while it is raining.
     * @param environment The environment that the eagle resides in.
     * @return Where food was found, or null if it wasn't.
     */
    @Override
    protected Location findFood(Environment environment)
    {
        if (environment.getWeather().getCurrentWeather() != WeatherType.RAINING) {
            return super.findFood();
        }
        return null;
    }
}
