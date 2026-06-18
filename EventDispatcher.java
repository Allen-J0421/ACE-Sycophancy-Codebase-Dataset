import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Central event dispatcher for managing observers and publishing events.
 * Thread-safe implementation using CopyOnWriteArrayList for concurrent access.
 */
class EventDispatcher {
    private final List<EventObserver> observers = new CopyOnWriteArrayList<>();
    private boolean enabled = true;

    /**
     * Registers an observer.
     */
    public void subscribe(EventObserver observer) {
        if (observer != null && !observers.contains(observer)) {
            observers.add(observer);
        }
    }

    /**
     * Unregisters an observer.
     */
    public void unsubscribe(EventObserver observer) {
        observers.remove(observer);
    }

    /**
     * Publishes an event to all interested observers.
     */
    public void publish(SortingEvent event) {
        if (!enabled || event == null) {
            return;
        }

        for (EventObserver observer : observers) {
            if (observer.isInterestedIn(event.getEventType())) {
                try {
                    observer.onEvent(event);
                } catch (Exception e) {
                    System.err.println("Observer error: " + observer.getName() + " - " + e.getMessage());
                }
            }
        }
    }

    /**
     * Enables event publishing.
     */
    public void enable() {
        enabled = true;
    }

    /**
     * Disables event publishing.
     */
    public void disable() {
        enabled = false;
    }

    /**
     * Checks if event publishing is enabled.
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Clears all observers.
     */
    public void clearObservers() {
        observers.clear();
    }

    /**
     * Gets observer count.
     */
    public int getObserverCount() {
        return observers.size();
    }

    /**
     * Gets list of observer names.
     */
    public List<String> getObserverNames() {
        List<String> names = new ArrayList<>();
        for (EventObserver observer : observers) {
            names.add(observer.getName());
        }
        return names;
    }
}
