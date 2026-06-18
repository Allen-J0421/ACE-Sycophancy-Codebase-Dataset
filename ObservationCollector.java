import java.util.ArrayList;
import java.util.List;

public class ObservationCollector {
  private final List<Observation> observations;
  private final int maxObservations;

  public ObservationCollector(int maxObservations) {
    this.observations = new ArrayList<>();
    this.maxObservations = maxObservations;
  }

  public synchronized void collect(Observation observation) {
    if (observations.size() >= maxObservations) {
      observations.remove(0);
    }
    observations.add(observation);
  }

  public synchronized List<Observation> getObservations() {
    return new ArrayList<>(observations);
  }

  public synchronized List<Observation> getObservationsByType(Observation.Type type) {
    List<Observation> result = new ArrayList<>();
    for (Observation obs : observations) {
      if (obs.getType() == type) {
        result.add(obs);
      }
    }
    return result;
  }

  public synchronized int getTotalObservations() {
    return observations.size();
  }

  public synchronized String generateReport() {
    StringBuilder sb = new StringBuilder();
    sb.append("=== Observations Report ===\n");
    sb.append("Total: ").append(getTotalObservations()).append("\n\n");

    for (Observation.Type type : Observation.Type.values()) {
      List<Observation> typeObs = getObservationsByType(type);
      sb.append(type).append(": ").append(typeObs.size()).append("\n");
    }

    return sb.toString();
  }

  public synchronized void clear() {
    observations.clear();
  }
}
