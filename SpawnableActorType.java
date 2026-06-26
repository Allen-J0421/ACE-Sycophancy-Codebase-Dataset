import java.awt.Color;

/**
 * Shared metadata and creation behavior for actor species that can be spawned into the field.
 */
public interface SpawnableActorType<T extends Actor> extends ActorType
{
    double getSpawnProbability();

    Color getColor();

    T createRandom(Field field, Location location);
}
