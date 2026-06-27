/**
 * Published before each simulation step to determine whether to continue.
 * Starts viable; a subscriber calls veto() to signal that the simulation
 * should stop. The publisher reads isViable() after publish() returns.
 */
public class ViabilityCheckEvent {
    public final Field field;
    private boolean viable = true;

    public ViabilityCheckEvent(Field field) {
        this.field = field;
    }

    public boolean isViable() { return viable; }
    public void veto() { viable = false; }
}
