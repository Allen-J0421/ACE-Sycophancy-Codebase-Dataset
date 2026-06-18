import java.util.ArrayList;
import java.util.List;

public class EventStore {
  public static class Event {
    private final String eventType;
    private final long timestamp;
    private final java.util.Map<String, Object> data;

    public Event(String eventType, java.util.Map<String, Object> data) {
      this.eventType = eventType;
      this.timestamp = System.currentTimeMillis();
      this.data = new java.util.HashMap<>(data);
    }

    public String getEventType() { return eventType; }
    public long getTimestamp() { return timestamp; }
    public java.util.Map<String, Object> getData() {
      return new java.util.HashMap<>(data);
    }

    @Override
    public String toString() {
      return String.format("Event{type=%s, timestamp=%d}", eventType, timestamp);
    }
  }

  private final List<Event> events;

  public EventStore() {
    this.events = new ArrayList<>();
  }

  public void append(String eventType, java.util.Map<String, Object> data) {
    events.add(new Event(eventType, data));
  }

  public List<Event> getEvents() {
    return new ArrayList<>(events);
  }

  public List<Event> getEventsByType(String type) {
    List<Event> filtered = new ArrayList<>();
    for (Event event : events) {
      if (event.getEventType().equals(type)) {
        filtered.add(event);
      }
    }
    return filtered;
  }

  public int getEventCount() {
    return events.size();
  }

  public String generateEventLog() {
    StringBuilder sb = new StringBuilder();
    sb.append("=== Event Log ===\n");
    sb.append("Total Events: ").append(events.size()).append("\n\n");

    for (Event event : events) {
      sb.append(event.toString()).append("\n");
      sb.append("  Data: ").append(event.getData()).append("\n");
    }

    return sb.toString();
  }

  @Override
  public String toString() {
    return String.format("EventStore{events=%d}", events.size());
  }
}
