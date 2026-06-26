import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
import java.awt.Color;

/**
 * Central definition of the supported plant species and their creation metadata.
 */
public enum PlantSpecies
{
    GRASS(0.09, Grass.class, new Color(50, 184, 121), 25, 0.2, 4.0, EnumSet.of(Weather.RAIN, Weather.SUNNY), Grass::new),
    SAGE(0.075, Sage.class, new Color(27, 117, 19), 20, 0.15, 4.0, EnumSet.of(Weather.RAIN, Weather.SUNNY), Sage::new),
    SEDGE(0.07, Sedge.class, new Color(78, 117, 19), 20, 0.15, 4.0, EnumSet.of(Weather.RAIN, Weather.SUNNY), Sedge::new);

    private final double spawnProbability;
    private final Class<? extends Plant> actorClass;
    private final Color color;
    private final int maxAge;
    private final double spreadProbability;
    private final double optimalSpreadFactor;
    private final Set<Weather> boostedWeather;
    private final PlantCreator creator;

    PlantSpecies(
        double spawnProbability,
        Class<? extends Plant> actorClass,
        Color color,
        int maxAge,
        double spreadProbability,
        double optimalSpreadFactor,
        Set<Weather> boostedWeather,
        PlantCreator creator
    ) {
        this.spawnProbability = spawnProbability;
        this.actorClass = actorClass;
        this.color = color;
        this.maxAge = maxAge;
        this.spreadProbability = spreadProbability;
        this.optimalSpreadFactor = optimalSpreadFactor;
        this.boostedWeather = Collections.unmodifiableSet(EnumSet.copyOf(boostedWeather));
        this.creator = creator;
    }

    public double getSpawnProbability()
    {
        return spawnProbability;
    }

    public Class<? extends Plant> getActorClass()
    {
        return actorClass;
    }

    public Color getColor()
    {
        return color;
    }

    public int getMaxAge()
    {
        return maxAge;
    }

    public double getSpreadProbability(Weather weather)
    {
        if(boostedWeather.contains(weather)) {
            return optimalSpreadFactor * spreadProbability;
        }
        return spreadProbability;
    }

    public Plant createRandom(Field field, Location location)
    {
        return creator.create(true, field, location);
    }

    public Plant createYoung(Field field, Location location)
    {
        return creator.create(false, field, location);
    }

    @FunctionalInterface
    private interface PlantCreator
    {
        Plant create(boolean randomAge, Field field, Location location);
    }
}
