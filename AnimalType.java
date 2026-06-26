/**
 * Supported animal species that can be created by animal factories.
 *
 * @version 1.0
 */
public enum AnimalType
{
    FOX,
    WOLVERINE,
    BEAR,
    SHEEP,
    REINDEER;

    /**
     * Convert external string input into a supported animal type.
     *
     * @param value The input value.
     * @return The matching animal type, or null when the input is invalid.
     */
    public static AnimalType fromString(String value)
    {
        if(value == null) {
            return null;
        }

        try {
            return AnimalType.valueOf(value.toUpperCase(java.util.Locale.ROOT));
        }
        catch(IllegalArgumentException exception) {
            return null;
        }
    }
}
