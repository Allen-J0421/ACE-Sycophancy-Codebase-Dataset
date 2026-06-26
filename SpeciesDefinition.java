import java.awt.Color;

/**
 * Immutable simulator-facing metadata for a creature species.
 */
public class SpeciesDefinition
{
    @FunctionalInterface
    public interface CreatureSpawner
    {
        Creature create(Field field, Location location);
    }

    private final Class<? extends Creature> creatureClass;
    private final double creationProbability;
    private final Color displayColor;
    private final CreatureSpawner spawner;

    public SpeciesDefinition(Class<? extends Creature> creatureClass, double creationProbability,
                             Color displayColor, CreatureSpawner spawner)
    {
        this.creatureClass = creatureClass;
        this.creationProbability = creationProbability;
        this.displayColor = displayColor;
        this.spawner = spawner;
    }

    public Class<? extends Creature> getCreatureClass()
    {
        return creatureClass;
    }

    public double getCreationProbability()
    {
        return creationProbability;
    }

    public Color getDisplayColor()
    {
        return displayColor;
    }

    public Creature create(Field field, Location location)
    {
        return spawner.create(field, location);
    }
}
