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

    private static final Gender[] REPRODUCTIVE_GENDERS = { MALE, FEMALE };
    private static final Random rand = Randomizer.getRandom();
    
    /**
     * @return Gender A random gender, excluding the NONE option.
     */
    public static Gender getRandom()
    {
        return REPRODUCTIVE_GENDERS[rand.nextInt(REPRODUCTIVE_GENDERS.length)];
    }
}
