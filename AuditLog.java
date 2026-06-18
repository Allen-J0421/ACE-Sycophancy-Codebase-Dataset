import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AuditLog {
    private static final AuditLog instance = new AuditLog();
    private final List<AuditEntry> entries = Collections.synchronizedList(new ArrayList<>());
    private final int maxEntries;

    private AuditLog() {
        this.maxEntries = 10000;
    }

    public static AuditLog getInstance() {
        return instance;
    }

    public void logOperation(String clientId, String operationType, String resourceId, String action, boolean success) {
        AuditEntry entry = new AuditEntry(clientId, operationType, resourceId, action, success);
        entries.add(entry);

        if (entries.size() > maxEntries) {
            entries.remove(0);
        }

        Logger.info("AUDIT: " + entry);
    }

    public List<AuditEntry> getAuditTrail(String clientId) {
        List<AuditEntry> result = new ArrayList<>();
        for (AuditEntry entry : entries) {
            if (entry.clientId.equals(clientId)) {
                result.add(entry);
            }
        }
        return result;
    }

    public List<AuditEntry> getAllEntries() {
        return new ArrayList<>(entries);
    }

    public int getEntryCount() {
        return entries.size();
    }

    public void clear() {
        entries.clear();
        Logger.info("Audit log cleared");
    }

    public static class AuditEntry {
        public final String clientId;
        public final String operationType;
        public final String resourceId;
        public final String action;
        public final boolean success;
        public final long timestamp;

        public AuditEntry(String clientId, String operationType, String resourceId, String action, boolean success) {
            this.clientId = clientId;
            this.operationType = operationType;
            this.resourceId = resourceId;
            this.action = action;
            this.success = success;
            this.timestamp = System.currentTimeMillis();
        }

        @Override
        public String toString() {
            return "AuditEntry{" +
                    "client=" + clientId +
                    ", op=" + operationType +
                    ", resource=" + resourceId +
                    ", action=" + action +
                    ", success=" + success +
                    ", time=" + timestamp +
                    '}';
        }
    }
}
