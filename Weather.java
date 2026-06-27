import java.util.Random;

/**
 * Weather states used by the simulation.
 */
public enum Weather
{
    FOGGY("Foggy"),
    RAINY("Rainy"),
    SUNNY("Sunny");

    private final String displayName;

    Weather(String displayName)
    {
        this.displayName = displayName;
    }

    /**
     * Pick a weather state for the next step.
     * The distribution matches the original simulator logic.
     */
    public static Weather random(Random rand)
    {
        double roll = rand.nextDouble();
        if(roll <= 0.33) {
            return FOGGY;
        }
        else if(roll <= 0.66) {
            return RAINY;
        }
        else {
            return SUNNY;
        }
    }

    @Override
    public String toString()
    {
        return displayName;
    }
}
