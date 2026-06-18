public enum SolverState {
  IDLE("Waiting for input"),
  VALIDATING("Validating input"),
  TRANSFORMING("Transforming context"),
  SOLVING("Executing algorithm"),
  CACHING("Storing result"),
  COMPLETE("Finished"),
  ERROR("Failed");

  private final String description;

  SolverState(String description) {
    this.description = description;
  }

  public String getDescription() {
    return description;
  }

  public SolverState nextState() {
    switch (this) {
      case IDLE: return VALIDATING;
      case VALIDATING: return TRANSFORMING;
      case TRANSFORMING: return SOLVING;
      case SOLVING: return CACHING;
      case CACHING: return COMPLETE;
      case COMPLETE: return IDLE;
      case ERROR: return IDLE;
      default: return IDLE;
    }
  }

  public boolean canTransitionTo(SolverState targetState) {
    if (this == ERROR) return targetState == IDLE;
    if (this == COMPLETE) return targetState == IDLE;
    return nextState() == targetState;
  }
}
