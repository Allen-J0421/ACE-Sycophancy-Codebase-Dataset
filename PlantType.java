import java.awt.Color;
import java.util.Locale;

/**
 * Enumerates the plant species available in the simulation.
 */
public enum PlantType
{
    GRASS(0.09, "Grass", new Color(50, 184, 121), Grass.class)
    {
        @Override
        Plant create(boolean randomAge, Field field, Location location)
        {
            return new Grass(randomAge, field, location);
        }
    },
    SAGE(0.075, "Sage", new Color(27, 117, 19), Sage.class)
    {
        @Override
        Plant create(boolean randomAge, Field field, Location location)
        {
            return new Sage(randomAge, field, location);
        }
    },
    SEDGE(0.07, "Sedge", new Color(78, 117, 19), Sedge.class)
    {
        @Override
        Plant create(boolean randomAge, Field field, Location location)
        {
            return new Sedge(randomAge, field, location);
        }
    };

    private final double spawnProbability;
    private final String displayName;
    private final Color color;
    private final Class<? extends Plant> actorClass;

    PlantType(double spawnProbability, String displayName, Color color, Class<? extends Plant> actorClass)
    {
        this.spawnProbability = spawnProbability;
        this.displayName = displayName;
        this.color = color;
        this.actorClass = actorClass;
    }

    public double getSpawnProbability()
    {
        return spawnProbability;
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public Color getColor()
    {
        return color;
    }

    public Class<? extends Plant> getActorClass()
    {
        return actorClass;
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
        try {
            return valueOf(plantType.trim().toUpperCase(Locale.ROOT));
        }
        catch (IllegalArgumentException ex) {
            return null;
        }
    }

    public static PlantType fromActorClass(Class<?> actorClass)
    {
        if(actorClass == null) {
            return null;
        }
        for(PlantType type : values()) {
            if(type.getActorClass().equals(actorClass)) {
                return type;
            }
        }
        return null;
    }
}
