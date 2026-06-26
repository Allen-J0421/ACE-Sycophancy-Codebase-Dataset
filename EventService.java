import java.util.ArrayList;
import java.util.List;

/**
 * Publishes simulation domain events to interested subscribers.
 */
public class EventService
{
    private final List<SimulationEventListener> listeners;
    private SimulationEvent lastEvent;

    public EventService()
    {
        this.listeners = new ArrayList<>();
    }

    public void subscribe(SimulationEventListener listener)
    {
        listeners.add(listener);
        if(lastEvent != null) {
            listener.onEvent(lastEvent);
        }
    }

    public void unsubscribe(SimulationEventListener listener)
    {
        listeners.remove(listener);
    }

    public void publish(SimulationEvent event)
    {
        lastEvent = event;
        List<SimulationEventListener> snapshot = new ArrayList<>(listeners);
        for(SimulationEventListener listener : snapshot) {
            listener.onEvent(event);
        }
    }
}
