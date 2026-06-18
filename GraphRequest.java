import java.util.HashMap;
import java.util.Map;

public class GraphRequest {
    private final String requestId;
    private final String operationType;
    private final Map<String, Object> parameters;
    private final long timestamp;
    private final String clientId;

    public GraphRequest(String operationType, Map<String, Object> parameters, String clientId) {
        this.requestId = java.util.UUID.randomUUID().toString();
        this.operationType = operationType;
        this.parameters = new HashMap<>(parameters);
        this.timestamp = System.currentTimeMillis();
        this.clientId = clientId;
    }

    public String getRequestId() {
        return requestId;
    }

    public String getOperationType() {
        return operationType;
    }

    public Map<String, Object> getParameters() {
        return new HashMap<>(parameters);
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getClientId() {
        return clientId;
    }

    @Override
    public String toString() {
        return "GraphRequest{" +
                "id=" + requestId +
                ", op=" + operationType +
                ", client=" + clientId +
                ", time=" + timestamp +
                '}';
    }
}
