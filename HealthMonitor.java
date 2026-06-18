import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HealthMonitor {
  private final Map<String, HealthCheck> checks;
  private final long checkIntervalMillis;

  public HealthMonitor(long checkIntervalMillis) {
    this.checks = new HashMap<>();
    this.checkIntervalMillis = checkIntervalMillis;
  }

  public void register(String name, HealthCheck check) {
    checks.put(name, check);
  }

  public HealthStatus checkHealth(String name) {
    HealthCheck check = checks.get(name);
    if (check == null) {
      return new HealthStatus(HealthStatus.Status.UNHEALTHY,
          "Check not found: " + name);
    }
    return check.check();
  }

  public Map<String, HealthStatus> checkAllHealth() {
    Map<String, HealthStatus> results = new HashMap<>();
    for (Map.Entry<String, HealthCheck> entry : checks.entrySet()) {
      results.put(entry.getKey(), entry.getValue().check());
    }
    return results;
  }

  public HealthStatus.Status getOverallStatus() {
    Map<String, HealthStatus> allChecks = checkAllHealth();
    for (HealthStatus status : allChecks.values()) {
      if (status.getStatus() == HealthStatus.Status.UNHEALTHY) {
        return HealthStatus.Status.UNHEALTHY;
      }
    }
    for (HealthStatus status : allChecks.values()) {
      if (status.getStatus() == HealthStatus.Status.DEGRADED) {
        return HealthStatus.Status.DEGRADED;
      }
    }
    return HealthStatus.Status.HEALTHY;
  }

  public String generateHealthReport() {
    StringBuilder sb = new StringBuilder();
    sb.append("=== System Health Report ===\n");

    Map<String, HealthStatus> allChecks = checkAllHealth();
    for (Map.Entry<String, HealthStatus> entry : allChecks.entrySet()) {
      HealthStatus status = entry.getValue();
      sb.append("  ").append(entry.getKey()).append(": ")
          .append(status.getStatus()).append(" - ")
          .append(status.getMessage()).append("\n");
    }

    sb.append("Overall: ").append(getOverallStatus());
    return sb.toString();
  }
}
