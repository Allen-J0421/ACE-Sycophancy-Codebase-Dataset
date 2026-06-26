package simulation;

/**
 * Event emitted when an entity dies.
 */
public final class DeathEvent extends SimulationEvent
{
    private final LivingEntity entity;
    private final Location location;

    public DeathEvent(Object source, LivingEntity entity, Location location) {
        super(SimulationEventType.DEATH, source);
        this.entity = entity;
        this.location = location;
    }

    public LivingEntity getEntity() {
        return entity;
    }

    public Location getLocation() {
        return location;
    }
}
