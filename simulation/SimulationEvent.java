package simulation;

/**
 * Base type for simulation events.
 */
public abstract class SimulationEvent
{
    private final SimulationEventType type;
    private final Object source;

    protected SimulationEvent(SimulationEventType type, Object source) {
        this.type = type;
        this.source = source;
    }

    public SimulationEventType getType() {
        return type;
    }

    public Object getSource() {
        return source;
    }
}
