/**
 * Supported plant species that can be created by plant factories.
 *
 * @version 1.0
 */
public enum PlantType
{
    GRASS,
    SAGE,
    SEDGE;

    /**
     * Convert external string input into a supported plant type.
     *
     * @param value The input value.
     * @return The matching plant type, or null when the input is invalid.
     */
    public static PlantType fromString(String value)
    {
        if(value == null) {
            return null;
        }

        try {
            return PlantType.valueOf(value.toUpperCase(java.util.Locale.ROOT));
        }
        catch(IllegalArgumentException exception) {
            return null;
        }
    }
}
