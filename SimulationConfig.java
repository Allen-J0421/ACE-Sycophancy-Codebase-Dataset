import java.util.Map;

/**
 * Immutable configuration for a simulator run.
 */
public class SimulationConfig
{
    public static final int DEFAULT_WIDTH = 240;
    public static final int DEFAULT_DEPTH = 160;
    public static final int LONG_SIMULATION_STEPS = 4000;
    private static final int DEFAULT_HUNTER_LIMIT = 5;

    private static final Map<Class<?>, Double> DEFAULT_CREATION_PROBABILITIES = Map.ofEntries(
            Map.entry(Coyote.class, 0.010),
            Map.entry(Deer.class, 0.080),
            Map.entry(Wolf.class, 0.010),
            Map.entry(Eagle.class, 0.010),
            Map.entry(Mouse.class, 0.080),
            Map.entry(Grass.class, 0.030),
            Map.entry(Hunter.class, 0.030)
    );

    private final Map<Class<?>, Double> creationProbabilities;
    private final int hunterLimit;

    private SimulationConfig(Map<Class<?>, Double> creationProbabilities, int hunterLimit)
    {
        this.creationProbabilities = Map.copyOf(creationProbabilities);
        this.hunterLimit = hunterLimit;
    }

    public static SimulationConfig defaultConfig()
    {
        return new SimulationConfig(DEFAULT_CREATION_PROBABILITIES, DEFAULT_HUNTER_LIMIT);
    }

    public static SimulationConfig withCreationProbabilities(double grassProbability,
            double deerProbability,
            double coyoteProbability,
            double wolfProbability,
            double eagleProbability,
            double hunterProbability,
            double mouseProbability)
    {
        return new SimulationConfig(
                Map.ofEntries(
                        Map.entry(Coyote.class, coyoteProbability),
                        Map.entry(Deer.class, deerProbability),
                        Map.entry(Wolf.class, wolfProbability),
                        Map.entry(Eagle.class, eagleProbability),
                        Map.entry(Mouse.class, mouseProbability),
                        Map.entry(Grass.class, grassProbability),
                        Map.entry(Hunter.class, hunterProbability)
                ),
                DEFAULT_HUNTER_LIMIT
        );
    }

    public double getCreationProbability(Class<?> actorClass)
    {
        return creationProbabilities.getOrDefault(actorClass, 0.0);
    }

    public Map<Class<?>, Double> getCreationProbabilities()
    {
        return creationProbabilities;
    }

    public int getHunterLimit()
    {
        return hunterLimit;
    }
}
