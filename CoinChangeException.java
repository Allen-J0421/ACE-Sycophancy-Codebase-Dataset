public abstract class CoinChangeException extends RuntimeException {
  public CoinChangeException(String message) {
    super(message);
  }

  public CoinChangeException(String message, Throwable cause) {
    super(message, cause);
  }
}
