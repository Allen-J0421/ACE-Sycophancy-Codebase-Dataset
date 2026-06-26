package simulation;

import java.util.ArrayList;
import java.util.List;

/**
 * Simple synchronous event bus for the simulation.
 */
public final class SimulationEventBus
{
    private final List<SimulationEventListener> listeners = new ArrayList<>();

    public void addListener(SimulationEventListener listener) {
        listeners.add(listener);
    }

    public void removeListener(SimulationEventListener listener) {
        listeners.remove(listener);
    }

    public void publish(SimulationEvent event) {
        List<SimulationEventListener> snapshot = new ArrayList<>(listeners);
        for (SimulationEventListener listener : snapshot) {
            listener.onSimulationEvent(event);
        }
    }
}
