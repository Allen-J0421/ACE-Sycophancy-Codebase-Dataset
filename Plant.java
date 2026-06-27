import java.util.List;

/**
 * A class representing shared characteristics of plants.
 * All lifecycle state (alive, field, location) lives in Entity.
 *
 * @version 2022.02.xx
 */
public abstract class Plant extends Entity
{
    /**
     * Create a new plant at location in field.
     *
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Plant(Field field, Location location)
    {
        super(field, location);
    }

    /**
     * Make this plant act.
     * Plants do not spawn new entities, so newEntities is unused.
     *
     * @param newEntities Unused; present to satisfy the Entity contract.
     * @param step The current simulation step number.
     * @param weather The current weather.
     */
    @Override
    public abstract void act(List<Entity> newEntities, int step, String weather);
}
