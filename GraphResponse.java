public class GraphResponse<T> {
    private final String requestId;
    private final int statusCode;
    private final String message;
    private final T data;
    private final long processingTimeMs;
    private final String traceId;

    public static final int SUCCESS = 200;
    public static final int CREATED = 201;
    public static final int BAD_REQUEST = 400;
    public static final int NOT_FOUND = 404;
    public static final int SERVER_ERROR = 500;
    public static final int SERVICE_UNAVAILABLE = 503;

    public GraphResponse(String requestId, int statusCode, String message, T data, long processingTimeMs, String traceId) {
        this.requestId = requestId;
        this.statusCode = statusCode;
        this.message = message;
        this.data = data;
        this.processingTimeMs = processingTimeMs;
        this.traceId = traceId;
    }

    public static <T> GraphResponse<T> success(String requestId, T data, long processingTimeMs, String traceId) {
        return new GraphResponse<>(requestId, SUCCESS, "Success", data, processingTimeMs, traceId);
    }

    public static <T> GraphResponse<T> error(String requestId, int statusCode, String message, long processingTimeMs, String traceId) {
        return new GraphResponse<>(requestId, statusCode, message, null, processingTimeMs, traceId);
    }

    public String getRequestId() {
        return requestId;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }

    public long getProcessingTimeMs() {
        return processingTimeMs;
    }

    public String getTraceId() {
        return traceId;
    }

    public boolean isSuccess() {
        return statusCode == SUCCESS || statusCode == CREATED;
    }

    @Override
    public String toString() {
        return "GraphResponse{" +
                "id=" + requestId +
                ", status=" + statusCode +
                ", message=" + message +
                ", time=" + processingTimeMs + "ms" +
                ", trace=" + traceId +
                '}';
    }
}
