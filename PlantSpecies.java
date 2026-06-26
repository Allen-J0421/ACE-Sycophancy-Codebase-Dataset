import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
import java.awt.Color;

/**
 * Central definition of the supported plant species and their creation metadata.
 */
public enum PlantSpecies implements SpawnableActorType<Plant>
{
    GRASS("Grass", 0.09, new Color(50, 184, 121), 25, 0.2, 4.0, EnumSet.of(Weather.RAIN, Weather.SUNNY), Grass::new),
    SAGE("Sage", 0.075, new Color(27, 117, 19), 20, 0.15, 4.0, EnumSet.of(Weather.RAIN, Weather.SUNNY), Sage::new),
    SEDGE("Sedge", 0.07, new Color(78, 117, 19), 20, 0.15, 4.0, EnumSet.of(Weather.RAIN, Weather.SUNNY), Sedge::new);

    private final String displayName;
    private final double spawnProbability;
    private final Color color;
    private final int maxAge;
    private final double spreadProbability;
    private final double optimalSpreadFactor;
    private final Set<Weather> boostedWeather;
    private final PlantCreator creator;

    PlantSpecies(
        String displayName,
        double spawnProbability,
        Color color,
        int maxAge,
        double spreadProbability,
        double optimalSpreadFactor,
        Set<Weather> boostedWeather,
        PlantCreator creator
    ) {
        this.displayName = displayName;
        this.spawnProbability = spawnProbability;
        this.color = color;
        this.maxAge = maxAge;
        this.spreadProbability = spreadProbability;
        this.optimalSpreadFactor = optimalSpreadFactor;
        this.boostedWeather = Collections.unmodifiableSet(EnumSet.copyOf(boostedWeather));
        this.creator = creator;
    }

    @Override
    public double getSpawnProbability()
    {
        return spawnProbability;
    }

    @Override
    public String getDisplayName()
    {
        return displayName;
    }

    @Override
    public ActorCategory getCategory()
    {
        return ActorCategory.PLANT;
    }

    @Override
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

    @Override
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
