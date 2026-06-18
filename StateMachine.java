import java.util.ArrayList;
import java.util.List;

public class StateMachine {
  private SolverState currentState;
  private final List<String> stateHistory;

  public StateMachine() {
    this.currentState = SolverState.IDLE;
    this.stateHistory = new ArrayList<>();
    recordState();
  }

  public void transition(SolverState targetState) {
    if (!currentState.canTransitionTo(targetState)) {
      throw new IllegalStateException(
          "Cannot transition from " + currentState + " to " + targetState);
    }
    currentState = targetState;
    recordState();
  }

  private void recordState() {
    stateHistory.add(currentState + " - " + currentState.getDescription());
  }

  public SolverState getCurrentState() {
    return currentState;
  }

  public List<String> getHistory() {
    return new ArrayList<>(stateHistory);
  }

  public void reset() {
    currentState = SolverState.IDLE;
    stateHistory.clear();
    recordState();
  }

  public String generateStateReport() {
    StringBuilder sb = new StringBuilder();
    sb.append("=== State Machine Report ===\n");
    sb.append("Current State: ").append(currentState).append("\n\n");
    sb.append("History:\n");
    for (String state : stateHistory) {
      sb.append("  ").append(state).append("\n");
    }
    return sb.toString();
  }

  @Override
  public String toString() {
    return String.format("StateMachine{state=%s, history=%d}",
        currentState, stateHistory.size());
  }
}
