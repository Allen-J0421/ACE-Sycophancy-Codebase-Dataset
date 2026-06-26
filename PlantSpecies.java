import java.awt.Color;

/**
 * Central definition of the supported plant species and their creation metadata.
 */
public enum PlantSpecies
{
    GRASS(0.09, Grass.class, new Color(50, 184, 121), Grass::new),
    SAGE(0.075, Sage.class, new Color(27, 117, 19), Sage::new),
    SEDGE(0.07, Sedge.class, new Color(78, 117, 19), Sedge::new);

    private final double spawnProbability;
    private final Class<? extends Plant> actorClass;
    private final Color color;
    private final PlantCreator creator;

    PlantSpecies(
        double spawnProbability,
        Class<? extends Plant> actorClass,
        Color color,
        PlantCreator creator
    ) {
        this.spawnProbability = spawnProbability;
        this.actorClass = actorClass;
        this.color = color;
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
