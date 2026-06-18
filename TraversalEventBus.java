import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

public class TraversalEventBus {
    private final List<TraversalEventListener> listeners = new ArrayList<>();

    public void subscribe(TraversalEventListener listener) {
        if (listener != null) {
            listeners.add(listener);
        }
    }

    public void unsubscribe(TraversalEventListener listener) {
        listeners.remove(listener);
    }

    public void publish(TraversalEvent event) {
        for (TraversalEventListener listener : new ArrayList<>(listeners)) {
            listener.onEvent(event);
        }
    }

    public void clear() {
        listeners.clear();
    }

    public int getListenerCount() {
        return listeners.size();
    }
}
