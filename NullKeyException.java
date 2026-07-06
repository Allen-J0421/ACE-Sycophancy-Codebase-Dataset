public class NullKeyException extends CacheException {
    public NullKeyException() {
        super("Cache key must not be null");
    }
}
