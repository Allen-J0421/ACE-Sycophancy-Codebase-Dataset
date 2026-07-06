public class NullValueException extends CacheException {
    public NullValueException() {
        super("Cache value must not be null");
    }
}
