import java.util.List;

/**
 * A simulation participant that can act and be removed from the simulation.
 */
public interface Entity {

    /**
     * Method called for this entity at every step in the simulation.
     *
     * @param newEntities Newborn entities at this step.
     * @param weather The current state of weather in the simulation.
     * @param time The current state of time in the simulation.
     */
    void act(List<Entity> newEntities, Weather weather, TimeOfDay time);

    /**
     * Return whether this entity should be removed from the simulation list.
     *
     * @return true if this entity has been removed.
     */
    boolean isRemoved();
}
