import java.util.Random;

/**
 * Provides three options for gender (male, female and none) along with the ability
 * to retrieve a random gender (excluding the none option).
 *
 * @version 18.02.22 (DD:MM:YY)
 */
public enum Gender
{
    // The possible genders:
    MALE, FEMALE, NONE;

    private static final Gender[] RANDOM_GENDERS = { MALE, FEMALE };
    private static final Random rand = Randomizer.getRandom();
    
    /**
     * @return Gender A random gender, excluding the NONE option.
     */
    public static Gender getRandom()
    {
        return RANDOM_GENDERS[rand.nextInt(RANDOM_GENDERS.length)];
    }
}
