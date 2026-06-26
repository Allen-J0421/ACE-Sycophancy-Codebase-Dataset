package simulation;

/**
 * Event emitted when an entity moves.
 */
public final class MovementEvent extends SimulationEvent
{
    private final LivingEntity entity;
    private final Location from;
    private final Location to;

    public MovementEvent(Object source, LivingEntity entity, Location from, Location to) {
        super(SimulationEventType.MOVED, source);
        this.entity = entity;
        this.from = from;
        this.to = to;
    }

    public LivingEntity getEntity() {
        return entity;
    }

    public Location getFrom() {
        return from;
    }

    public Location getTo() {
        return to;
    }
}
