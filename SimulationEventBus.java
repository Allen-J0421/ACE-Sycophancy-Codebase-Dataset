import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Synchronous in-process event bus for simulation lifecycle events.
 *
 * @version 2022.03.02
 */
public class SimulationEventBus
{
    private final Map<Class<? extends SimulationEvent>,
                     List<SimulationEventListener>> listeners;

    public SimulationEventBus()
    {
        listeners = new LinkedHashMap<>();
    }

    public void subscribe(Class<? extends SimulationEvent> eventType,
                          SimulationEventListener listener)
    {
        List<SimulationEventListener> subscribed = listeners.get(eventType);
        if(subscribed == null) {
            subscribed = new ArrayList<>();
            listeners.put(eventType, subscribed);
        }
        subscribed.add(listener);
    }

    public void publish(SimulationEvent event)
    {
        for(Map.Entry<Class<? extends SimulationEvent>,
                      List<SimulationEventListener>> entry : listeners.entrySet()) {
            if(entry.getKey().isInstance(event)) {
                notifyListeners(entry.getValue(), event);
            }
        }
    }

    private void notifyListeners(List<SimulationEventListener> subscribed,
                                 SimulationEvent event)
    {
        List<SimulationEventListener> listenersSnapshot =
            new ArrayList<>(subscribed);
        for(SimulationEventListener listener : listenersSnapshot) {
            listener.onEvent(event);
        }
    }
}
