import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.time.Instant;

// Event sourcing infrastructure
abstract class TrieEvent {
    private final String eventId;
    private final Instant timestamp;
    private final String word;

    TrieEvent(String word) {
        this.eventId = UUID.randomUUID().toString();
        this.timestamp = Instant.now();
        this.word = word;
    }

    String getEventId() {
        return eventId;
    }

    Instant getTimestamp() {
        return timestamp;
    }

    String getWord() {
        return word;
    }

    abstract String getEventType();

    @Override
    public String toString() {
        return String.format("%s{id=%s, timestamp=%s, word=%s}",
            getEventType(), eventId, timestamp, word);
    }
}

class WordInsertedEvent extends TrieEvent {
    WordInsertedEvent(String word) {
        super(word);
    }

    @Override
    String getEventType() {
        return "WordInserted";
    }
}

class WordDeletedEvent extends TrieEvent {
    WordDeletedEvent(String word) {
        super(word);
    }

    @Override
    String getEventType() {
        return "WordDeleted";
    }
}

class WordSearchedEvent extends TrieEvent {
    private final boolean found;

    WordSearchedEvent(String word, boolean found) {
        super(word);
        this.found = found;
    }

    boolean wasFound() {
        return found;
    }

    @Override
    String getEventType() {
        return "WordSearched";
    }

    @Override
    public String toString() {
        return String.format("%s{id=%s, timestamp=%s, word=%s, found=%s}",
            getEventType(), getEventId(), getTimestamp(), getWord(), found);
    }
}

class TrieStateChangedEvent extends TrieEvent {
    private final TrieState previousState;
    private final TrieState newState;

    TrieStateChangedEvent(TrieState previousState, TrieState newState) {
        super("*");
        this.previousState = previousState;
        this.newState = newState;
    }

    TrieState getPreviousState() {
        return previousState;
    }

    TrieState getNewState() {
        return newState;
    }

    @Override
    String getEventType() {
        return "TrieStateChanged";
    }

    @Override
    public String toString() {
        return String.format("%s{id=%s, timestamp=%s, %s -> %s}",
            getEventType(), getEventId(), getTimestamp(), previousState, newState);
    }
}

// Event bus for reactive event handling
interface EventHandler {
    void handle(TrieEvent event);
}

class EventBus {
    private final List<EventHandler> handlers = new CopyOnWriteArrayList<>();
    private final Queue<TrieEvent> eventLog = new LinkedList<>();
    private final int maxLogSize;

    EventBus() {
        this(10000);
    }

    EventBus(int maxLogSize) {
        this.maxLogSize = maxLogSize;
    }

    void subscribe(EventHandler handler) {
        if (handler != null) {
            handlers.add(handler);
        }
    }

    void unsubscribe(EventHandler handler) {
        handlers.remove(handler);
    }

    void publish(TrieEvent event) {
        eventLog.offer(event);
        if (eventLog.size() > maxLogSize) {
            eventLog.poll();
        }
        handlers.forEach(h -> h.handle(event));
    }

    List<TrieEvent> getEventLog() {
        return new ArrayList<>(eventLog);
    }

    int getEventLogSize() {
        return eventLog.size();
    }

    void clearLog() {
        eventLog.clear();
    }
}

// Logging event handler
class LoggingEventHandler implements EventHandler {
    private final String name;

    LoggingEventHandler(String name) {
        this.name = name;
    }

    @Override
    public void handle(TrieEvent event) {
        System.out.println("[" + name + "] " + event);
    }
}

// Metrics aggregation event handler
class MetricsEventHandler implements EventHandler {
    private long totalInserts;
    private long totalDeletes;
    private long totalSearches;
    private long totalSearchHits;

    @Override
    public void handle(TrieEvent event) {
        if (event instanceof WordInsertedEvent) {
            totalInserts++;
        } else if (event instanceof WordDeletedEvent) {
            totalDeletes++;
        } else if (event instanceof WordSearchedEvent) {
            WordSearchedEvent searchEvent = (WordSearchedEvent) event;
            totalSearches++;
            if (searchEvent.wasFound()) {
                totalSearchHits++;
            }
        }
    }

    long getTotalInserts() {
        return totalInserts;
    }

    long getTotalDeletes() {
        return totalDeletes;
    }

    long getTotalSearches() {
        return totalSearches;
    }

    long getTotalSearchHits() {
        return totalSearchHits;
    }

    double getHitRate() {
        return totalSearches == 0 ? 0.0 : (double) totalSearchHits / totalSearches;
    }

    @Override
    public String toString() {
        return String.format("EventMetrics{inserts=%d, deletes=%d, searches=%d, hits=%d, hitRate=%.2f%%}",
            totalInserts, totalDeletes, totalSearches, totalSearchHits, getHitRate() * 100);
    }
}
