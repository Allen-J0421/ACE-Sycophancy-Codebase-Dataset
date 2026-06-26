import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Configuration data used to set up a simulation.
 */
public final class SimulationConfig
{
    private static final int DEFAULT_WIDTH = 240;
    private static final int DEFAULT_DEPTH = 160;
    private static final int DEFAULT_HUNTER_LIMIT = 5;

    private final int depth;
    private final int width;
    private final int hunterLimit;
    private final Map<Class<?>, Double> populationRates;

    private SimulationConfig(int depth, int width, int hunterLimit, Map<Class<?>, Double> populationRates)
    {
        if(depth <= 0 || width <= 0) {
            throw new IllegalArgumentException("Simulation dimensions must be greater than zero.");
        }
        if(hunterLimit < 0) {
            throw new IllegalArgumentException("Hunter limit cannot be negative.");
        }

        this.depth = depth;
        this.width = width;
        this.hunterLimit = hunterLimit;
        this.populationRates = Collections.unmodifiableMap(new LinkedHashMap<>(Objects.requireNonNull(populationRates, "populationRates")));
    }

    public static SimulationConfig defaultConfig()
    {
        return new SimulationConfig(DEFAULT_DEPTH, DEFAULT_WIDTH, DEFAULT_HUNTER_LIMIT, defaultPopulationRates());
    }

    public static SimulationConfig withDimensions(int depth, int width)
    {
        return new SimulationConfig(depth, width, DEFAULT_HUNTER_LIMIT, defaultPopulationRates());
    }

    public static SimulationConfig withPopulationRates(double grassRate,
                                                       double deerRate,
                                                       double coyoteRate,
                                                       double wolfRate,
                                                       double eagleRate,
                                                       double hunterRate,
                                                       double mouseRate)
    {
        return new SimulationConfig(
                DEFAULT_DEPTH,
                DEFAULT_WIDTH,
                DEFAULT_HUNTER_LIMIT,
                populationRates(grassRate, deerRate, coyoteRate, wolfRate, eagleRate, hunterRate, mouseRate));
    }

    public static SimulationConfig of(int depth, int width, Map<Class<?>, Double> populationRates)
    {
        return new SimulationConfig(depth, width, DEFAULT_HUNTER_LIMIT, populationRates);
    }

    public static SimulationConfig of(int depth, int width, int hunterLimit, Map<Class<?>, Double> populationRates)
    {
        return new SimulationConfig(depth, width, hunterLimit, populationRates);
    }

    public int getDepth()
    {
        return depth;
    }

    public int getWidth()
    {
        return width;
    }

    public int getHunterLimit()
    {
        return hunterLimit;
    }

    public double getPopulationRate(Class<?> actorClass)
    {
        return populationRates.getOrDefault(actorClass, 0.0);
    }

    public Map<Class<?>, Double> getPopulationRates()
    {
        return populationRates;
    }

    private static Map<Class<?>, Double> defaultPopulationRates()
    {
        return populationRates(
                0.030,
                0.080,
                0.010,
                0.010,
                0.010,
                0.030,
                0.080);
    }

    private static Map<Class<?>, Double> populationRates(double grassRate,
                                                         double deerRate,
                                                         double coyoteRate,
                                                         double wolfRate,
                                                         double eagleRate,
                                                         double hunterRate,
                                                         double mouseRate)
    {
        Map<Class<?>, Double> rates = new LinkedHashMap<>();
        rates.put(Grass.class, grassRate);
        rates.put(Deer.class, deerRate);
        rates.put(Coyote.class, coyoteRate);
        rates.put(Wolf.class, wolfRate);
        rates.put(Eagle.class, eagleRate);
        rates.put(Mouse.class, mouseRate);
        rates.put(Hunter.class, hunterRate);
        return rates;
    }
}
