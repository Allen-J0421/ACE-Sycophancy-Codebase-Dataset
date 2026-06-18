public class APIGateway {
    private final RateLimiter rateLimiter;
    private final AuditLog auditLog;
    private final ServiceStatistics statistics;
    private final DistributedTracer tracer;

    public APIGateway() {
        this.rateLimiter = new RateLimiter(100, 60000); // 100 requests per minute
        this.auditLog = AuditLog.getInstance();
        this.statistics = ServiceStatistics.getInstance();
        this.tracer = DistributedTracer.getInstance();
    }

    public <T> GraphResponse<T> handleRequest(GraphRequest request, RequestHandler<T> handler) {
        String traceId = tracer.startTrace(request.getOperationType());
        long startTime = System.currentTimeMillis();

        try {
            // Rate limiting
            tracer.startSpan("rate-limit-check");
            if (!rateLimiter.allowRequest(request.getClientId())) {
                tracer.recordEvent("rate-limit-exceeded");
                tracer.endSpan();
                auditLog.logOperation(request.getClientId(), request.getOperationType(),
                    "gateway", "rate-limit-check", false);
                long processingTime = System.currentTimeMillis() - startTime;
                statistics.recordRequest(request.getOperationType(), processingTime, false);
                return GraphResponse.error(request.getRequestId(),
                    GraphResponse.SERVICE_UNAVAILABLE,
                    "Rate limit exceeded", processingTime, traceId);
            }
            tracer.endSpan();

            // Request validation
            tracer.startSpan("validation");
            String validationError = validateRequest(request);
            if (validationError != null) {
                tracer.recordEvent("validation-failed: " + validationError);
                tracer.endSpan();
                auditLog.logOperation(request.getClientId(), request.getOperationType(),
                    "gateway", "validation", false);
                long processingTime = System.currentTimeMillis() - startTime;
                statistics.recordRequest(request.getOperationType(), processingTime, false);
                return GraphResponse.error(request.getRequestId(),
                    GraphResponse.BAD_REQUEST,
                    "Invalid request: " + validationError, processingTime, traceId);
            }
            tracer.endSpan();

            // Execute handler
            tracer.startSpan("handler-execution");
            T result = handler.handle(request);
            tracer.endSpan();

            // Success
            long processingTime = System.currentTimeMillis() - startTime;
            auditLog.logOperation(request.getClientId(), request.getOperationType(),
                "gateway", "execute", true);
            statistics.recordRequest(request.getOperationType(), processingTime, true);
            tracer.recordEvent("success");

            return GraphResponse.success(request.getRequestId(), result, processingTime, traceId);

        } catch (Exception e) {
            tracer.recordEvent("exception: " + e.getMessage());
            long processingTime = System.currentTimeMillis() - startTime;
            auditLog.logOperation(request.getClientId(), request.getOperationType(),
                "gateway", "exception", false);
            statistics.recordRequest(request.getOperationType(), processingTime, false);
            Logger.error("Request failed", e);
            return GraphResponse.error(request.getRequestId(),
                GraphResponse.SERVER_ERROR,
                "Internal server error: " + e.getMessage(), processingTime, traceId);
        }
    }

    private String validateRequest(GraphRequest request) {
        if (request.getOperationType() == null || request.getOperationType().isEmpty()) {
            return "Missing operation type";
        }
        if (request.getClientId() == null || request.getClientId().isEmpty()) {
            return "Missing client ID";
        }
        return null;
    }

    public interface RequestHandler<T> {
        T handle(GraphRequest request) throws Exception;
    }

    public ServiceStatistics getStatistics() {
        return statistics;
    }

    public AuditLog getAuditLog() {
        return auditLog;
    }

    public DistributedTracer getTracer() {
        return tracer;
    }
}
