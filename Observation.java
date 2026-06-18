public class Observation {
  public enum Type { REQUEST, CACHE, ALGORITHM, ERROR }

  private final Type type;
  private final String description;
  private final long timestamp;
  private final java.util.Map<String, Object> data;

  public Observation(Type type, String description) {
    this.type = type;
    this.description = description;
    this.timestamp = System.currentTimeMillis();
    this.data = new java.util.HashMap<>();
  }

  public void addData(String key, Object value) {
    data.put(key, value);
  }

  public Type getType() {
    return type;
  }

  public String getDescription() {
    return description;
  }

  public long getTimestamp() {
    return timestamp;
  }

  public java.util.Map<String, Object> getData() {
    return new java.util.HashMap<>(data);
  }

  @Override
  public String toString() {
    return String.format("Observation{type=%s, desc=%s, timestamp=%d}",
        type, description, timestamp);
  }
}
