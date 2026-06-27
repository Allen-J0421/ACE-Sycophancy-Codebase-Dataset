import java.util.List;

/**
 * Shared lifecycle state for one simulation tick.
 */
public final class SimulationContext
{
    private final List<Actor> actors;
    private final List<Actor> spawnedActors;
    private final boolean nightTime;
    private boolean generateWater;

    public SimulationContext(List<Actor> actors, List<Actor> spawnedActors, boolean nightTime)
    {
        this.actors = actors;
        this.spawnedActors = spawnedActors;
        this.nightTime = nightTime;
        this.generateWater = false;
    }

    public List<Actor> getActors()
    {
        return actors;
    }

    public List<Actor> getSpawnedActors()
    {
        return spawnedActors;
    }

    public boolean isNightTime()
    {
        return nightTime;
    }

    public void requestWaterGeneration()
    {
        generateWater = true;
    }

    public boolean shouldGenerateWater()
    {
        return generateWater;
    }
}
