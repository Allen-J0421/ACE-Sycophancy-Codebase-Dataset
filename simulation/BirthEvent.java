package simulation;

/**
 * Event emitted when a new entity is born.
 */
public final class BirthEvent extends SimulationEvent
{
    private final LivingEntity offspring;
    private final Location location;

    public BirthEvent(Object source, LivingEntity offspring, Location location) {
        super(SimulationEventType.BIRTH, source);
        this.offspring = offspring;
        this.location = location;
    }

    public LivingEntity getOffspring() {
        return offspring;
    }

    public Location getLocation() {
        return location;
    }
}
