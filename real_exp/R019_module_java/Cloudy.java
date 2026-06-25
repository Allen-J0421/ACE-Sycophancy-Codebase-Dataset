
/**
 * Weather of type Cloudy, this weather impairs this plant's ability to reproduce.
 *
 * @version 2022.03.02
 */
public class Cloudy extends Weather
{
    public Cloudy()
    {
        breedingMultiplier = 0.7f;
    }
}
