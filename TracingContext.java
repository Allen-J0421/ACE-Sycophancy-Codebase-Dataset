public class TracingContext {
  private final String traceId;
  private final String spanId;
  private final long startTime;
  private long endTime;
  private java.util.Map<String, String> tags;
  private java.util.List<String> logs;

  public TracingContext(String traceId, String spanId) {
    this.traceId = traceId;
    this.spanId = spanId;
    this.startTime = System.nanoTime();
    this.tags = new java.util.HashMap<>();
    this.logs = new java.util.ArrayList<>();
  }

  public String getTraceId() {
    return traceId;
  }

  public String getSpanId() {
    return spanId;
  }

  public long getStartTime() {
    return startTime;
  }

  public void end() {
    this.endTime = System.nanoTime();
  }

  public long getDurationNanos() {
    return endTime > 0 ? endTime - startTime : System.nanoTime() - startTime;
  }

  public void addTag(String key, String value) {
    tags.put(key, value);
  }

  public String getTag(String key) {
    return tags.get(key);
  }

  public void addLog(String message) {
    logs.add(String.format("[%d] %s", System.currentTimeMillis(), message));
  }

  public java.util.List<String> getLogs() {
    return new java.util.ArrayList<>(logs);
  }

  @Override
  public String toString() {
    return String.format("TracingContext{traceId=%s, spanId=%s, duration=%.2fms}",
        traceId, spanId, getDurationNanos() / 1_000_000.0);
  }
}
