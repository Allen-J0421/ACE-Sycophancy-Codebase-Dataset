
/**
 * Weather of type sunny, A plant thrives in this weather type.
 *
 * @version 2022.03.02
 */
public class Sunny extends Weather
{
    /**
     * Creates a Sunny object, it contains a multiplier on plant's growth
     */
    public Sunny()
    {
        breedingMultiplier = 1.25f;
    }
}
