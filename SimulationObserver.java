/**
 * Observer for published simulator state changes.
 */
public interface SimulationObserver
{
    void onStateChanged(SimulationState state);
}
