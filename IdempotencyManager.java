import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class IdempotencyManager {
    private static final IdempotencyManager instance = new IdempotencyManager();
    private final Map<String, IdempotencyEntry> entries = new ConcurrentHashMap<>();
    private final long ttlMillis;

    private IdempotencyManager() {
        this.ttlMillis = 3600000; // 1 hour
    }

    public static IdempotencyManager getInstance() {
        return instance;
    }

    public boolean isIdempotencyKeyProcessed(String idempotencyKey) {
        IdempotencyEntry entry = entries.get(idempotencyKey);
        if (entry == null) {
            return false;
        }
        if (entry.isExpired()) {
            entries.remove(idempotencyKey);
            return false;
        }
        return true;
    }

    public void recordIdempotencyKey(String idempotencyKey, Object result) {
        entries.put(idempotencyKey, new IdempotencyEntry(result));
        Logger.debug("Idempotency key recorded: " + idempotencyKey);
    }

    public Object getResult(String idempotencyKey) {
        IdempotencyEntry entry = entries.get(idempotencyKey);
        if (entry != null && !entry.isExpired()) {
            return entry.result;
        }
        return null;
    }

    public void clear() {
        entries.clear();
        Logger.info("Idempotency entries cleared");
    }

    private class IdempotencyEntry {
        final Object result;
        final long expirationTime;

        IdempotencyEntry(Object result) {
            this.result = result;
            this.expirationTime = System.currentTimeMillis() + ttlMillis;
        }

        boolean isExpired() {
            return System.currentTimeMillis() > expirationTime;
        }
    }
}
