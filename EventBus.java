import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Generic publish/subscribe event bus. Subscribers register a handler
 * for a specific event class; publishers post an event instance and all
 * matching handlers are invoked synchronously in subscription order.
 */
public class EventBus {
    private final Map<Class<?>, List<Consumer<Object>>> subscribers = new HashMap<>();

    /**
     * Subscribe to events of exactly the given type.
     * The handler is called synchronously inside publish().
     */
    @SuppressWarnings("unchecked")
    public <E> void subscribe(Class<E> eventType, Consumer<E> handler) {
        subscribers.computeIfAbsent(eventType, k -> new ArrayList<>())
                   .add((Consumer<Object>) handler);
    }

    /**
     * Deliver the event to all handlers registered for its exact runtime type.
     */
    public void publish(Object event) {
        List<Consumer<Object>> handlers = subscribers.get(event.getClass());
        if (handlers != null) {
            for (Consumer<Object> handler : handlers) {
                handler.accept(event);
            }
        }
    }
}
