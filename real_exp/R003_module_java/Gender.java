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
    
    /**
     * @return Gender A random gender, excluding the NONE option.
     */
    public static Gender getRandom()
    {
        Gender randomGender;
        
        do
        {
            final int randomIndex = (int) (Math.random() * values().length);
            
            randomGender = values()[randomIndex];
        }
        while (randomGender == NONE);
        
        return randomGender;
    }
}
