import java.util.Locale;

/**
 * Enumerates the plant species available in the simulation.
 */
public enum PlantType
{
    GRASS(0.09)
    {
        @Override
        Plant create(boolean randomAge, Field field, Location location)
        {
            return new Grass(randomAge, field, location);
        }
    },
    SAGE(0.075)
    {
        @Override
        Plant create(boolean randomAge, Field field, Location location)
        {
            return new Sage(randomAge, field, location);
        }
    },
    SEDGE(0.07)
    {
        @Override
        Plant create(boolean randomAge, Field field, Location location)
        {
            return new Sedge(randomAge, field, location);
        }
    };

    private final double spawnProbability;

    PlantType(double spawnProbability)
    {
        this.spawnProbability = spawnProbability;
    }

    public double getSpawnProbability()
    {
        return spawnProbability;
    }

    abstract Plant create(boolean randomAge, Field field, Location location);

    public Plant createForPopulation(Field field, Location location)
    {
        return create(true, field, location);
    }

    public static PlantType fromString(String plantType)
    {
        if(plantType == null) {
            return null;
        }
        return valueOf(plantType.trim().toUpperCase(Locale.ROOT));
    }
}
