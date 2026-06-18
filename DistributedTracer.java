import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class DistributedTracer {
    private static final DistributedTracer instance = new DistributedTracer();
    private final ThreadLocal<Stack<TraceSpan>> spans = ThreadLocal.withInitial(Stack::new);
    private final Map<String, java.util.List<TraceSpan>> traces = new java.util.HashMap<>();

    private DistributedTracer() {}

    public static DistributedTracer getInstance() {
        return instance;
    }

    public String startTrace(String operationName) {
        String traceId = java.util.UUID.randomUUID().toString();
        TraceSpan span = new TraceSpan(traceId, traceId, operationName, System.nanoTime());
        spans.get().push(span);
        Logger.debug("Trace started: " + traceId + " for " + operationName);
        return traceId;
    }

    public void startSpan(String spanName) {
        Stack<TraceSpan> stack = spans.get();
        if (stack.isEmpty()) {
            startTrace(spanName);
            return;
        }

        TraceSpan parent = stack.peek();
        TraceSpan span = new TraceSpan(parent.traceId, java.util.UUID.randomUUID().toString(), spanName, System.nanoTime());
        stack.push(span);
        Logger.debug("Span started: " + spanName + " under trace " + parent.traceId);
    }

    public void endSpan() {
        Stack<TraceSpan> stack = spans.get();
        if (!stack.isEmpty()) {
            TraceSpan span = stack.pop();
            span.endTime = System.nanoTime();
            Logger.debug("Span ended: " + span.spanName + " duration: " + span.getDurationMs() + "ms");
        }
    }

    public String getCurrentTraceId() {
        Stack<TraceSpan> stack = spans.get();
        return stack.isEmpty() ? null : stack.peek().traceId;
    }

    public void recordEvent(String eventName) {
        Stack<TraceSpan> stack = spans.get();
        if (!stack.isEmpty()) {
            TraceSpan span = stack.peek();
            span.addEvent(eventName);
            Logger.debug("Event recorded: " + eventName);
        }
    }

    public static class TraceSpan {
        public final String traceId;
        public final String spanId;
        public final String spanName;
        public final long startTime;
        public long endTime;
        private final java.util.List<String> events = new java.util.ArrayList<>();

        public TraceSpan(String traceId, String spanId, String spanName, long startTime) {
            this.traceId = traceId;
            this.spanId = spanId;
            this.spanName = spanName;
            this.startTime = startTime;
        }

        public void addEvent(String event) {
            events.add(event);
        }

        public long getDurationMs() {
            return (endTime - startTime) / 1_000_000;
        }

        @Override
        public String toString() {
            return "TraceSpan{" +
                    "trace=" + traceId +
                    ", span=" + spanId +
                    ", name=" + spanName +
                    ", duration=" + getDurationMs() + "ms" +
                    '}';
        }
    }
}
