public class LoggingSolveListener implements SolveEventListener {
  private final boolean verbose;

  public LoggingSolveListener() {
    this(false);
  }

  public LoggingSolveListener(boolean verbose) {
    this.verbose = verbose;
  }

  @Override
  public void onEvent(SolveEvent event) {
    switch (event.getType()) {
      case BEFORE_SOLVE:
        if (verbose) {
          System.out.println("  [BEFORE] Solving: sum=" + event.getTargetSum() +
              ", coins=" + event.getCoins().length);
        }
        break;
      case AFTER_SOLVE:
        CoinChangeResult result = event.getResult();
        System.out.println("  [AFTER]  Result: " + result.getWays() + " ways");
        break;
      case ERROR:
        System.err.println("  [ERROR]  " + event.getError().getMessage());
        break;
    }
  }
}
