public class HealthStatus {
  public enum Status { HEALTHY, DEGRADED, UNHEALTHY }

  private final Status status;
  private final String message;
  private final long timestamp;
  private final java.util.Map<String, Object> details;

  public HealthStatus(Status status, String message) {
    this.status = status;
    this.message = message;
    this.timestamp = System.currentTimeMillis();
    this.details = new java.util.HashMap<>();
  }

  public Status getStatus() {
    return status;
  }

  public String getMessage() {
    return message;
  }

  public long getTimestamp() {
    return timestamp;
  }

  public void addDetail(String key, Object value) {
    details.put(key, value);
  }

  public java.util.Map<String, Object> getDetails() {
    return new java.util.HashMap<>(details);
  }

  public boolean isHealthy() {
    return status == Status.HEALTHY;
  }

  @Override
  public String toString() {
    return String.format("HealthStatus{status=%s, message=%s, details=%d}",
        status, message, details.size());
  }
}
