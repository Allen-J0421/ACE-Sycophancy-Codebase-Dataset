import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

// Middleware pattern for request/response processing
interface TrieMiddleware {
    OperationResult<?> process(TrieRequest request, TrieChain chain);
}

// Request abstraction for middleware
class TrieRequest {
    private final String operation;
    private final String parameter;
    private final Map<String, Object> metadata;
    private final long timestamp;

    TrieRequest(String operation, String parameter) {
        this.operation = operation;
        this.parameter = parameter;
        this.metadata = new LinkedHashMap<>();
        this.timestamp = System.currentTimeMillis();
    }

    String getOperation() {
        return operation;
    }

    String getParameter() {
        return parameter;
    }

    Map<String, Object> getMetadata() {
        return metadata;
    }

    void setMetadata(String key, Object value) {
        metadata.put(key, value);
    }

    Object getMetadata(String key) {
        return metadata.get(key);
    }

    long getTimestamp() {
        return timestamp;
    }
}

// Chain of responsibility for middleware
interface TrieChain {
    OperationResult<?> proceed(TrieRequest request);
}

class TrieMiddlewareChain implements TrieChain {
    private final List<TrieMiddleware> middlewares;
    private final Trie trie;
    private int currentIndex = 0;

    TrieMiddlewareChain(List<TrieMiddleware> middlewares, Trie trie) {
        this.middlewares = new ArrayList<>(middlewares);
        this.trie = trie;
    }

    @Override
    public OperationResult<?> proceed(TrieRequest request) {
        if (currentIndex >= middlewares.size()) {
            return executeOperation(request);
        }

        TrieMiddleware middleware = middlewares.get(currentIndex++);
        return middleware.process(request, this);
    }

    private OperationResult<?> executeOperation(TrieRequest request) {
        switch (request.getOperation()) {
            case "insert":
                return trie.insert(request.getParameter());
            case "delete":
                return trie.delete(request.getParameter());
            case "search":
                boolean found = trie.search(request.getParameter());
                return OperationResult.success(found);
            default:
                return OperationResult.failure("Unknown operation: " + request.getOperation());
        }
    }
}

// Middleware implementations

class LoggingMiddleware implements TrieMiddleware {
    private final String name;

    LoggingMiddleware(String name) {
        this.name = name;
    }

    @Override
    public OperationResult<?> process(TrieRequest request, TrieChain chain) {
        System.out.println("[" + name + "] Before: " + request.getOperation() + "(" + request.getParameter() + ")");
        OperationResult<?> result = chain.proceed(request);
        System.out.println("[" + name + "] After: " + result.getMessage() + " (time: " + result.getExecutionTimeMs() + "ms)");
        return result;
    }
}

class TimingMiddleware implements TrieMiddleware {
    private final Map<String, Long> timings = new LinkedHashMap<>();

    @Override
    public OperationResult<?> process(TrieRequest request, TrieChain chain) {
        long start = System.nanoTime();
        OperationResult<?> result = chain.proceed(request);
        long duration = System.nanoTime() - start;
        timings.put(request.getOperation() + ":" + request.getParameter(), duration / 1_000_000);
        return result;
    }

    Map<String, Long> getTimings() {
        return new LinkedHashMap<>(timings);
    }
}

class ValidationMiddleware implements TrieMiddleware {
    @Override
    public OperationResult<?> process(TrieRequest request, TrieChain chain) {
        if (request.getParameter() == null || request.getParameter().isEmpty()) {
            return OperationResult.failure("Invalid parameter: cannot be null or empty");
        }
        return chain.proceed(request);
    }
}

class CachingMiddleware implements TrieMiddleware {
    private final Map<String, OperationResult<?>> cache = new LinkedHashMap<>();

    @Override
    public OperationResult<?> process(TrieRequest request, TrieChain chain) {
        String cacheKey = request.getOperation() + ":" + request.getParameter();

        if ("search".equals(request.getOperation()) && cache.containsKey(cacheKey)) {
            request.setMetadata("cached", true);
            return cache.get(cacheKey);
        }

        OperationResult<?> result = chain.proceed(request);

        if ("search".equals(request.getOperation())) {
            cache.put(cacheKey, result);
        }

        return result;
    }

    Map<String, OperationResult<?>> getCache() {
        return new LinkedHashMap<>(cache);
    }

    void clearCache() {
        cache.clear();
    }
}

class MetricsMiddleware implements TrieMiddleware {
    private final Map<String, Integer> operationCounts = new LinkedHashMap<>();
    private final Map<String, Long> totalExecutionTimes = new LinkedHashMap<>();

    @Override
    public OperationResult<?> process(TrieRequest request, TrieChain chain) {
        String operation = request.getOperation();
        operationCounts.put(operation, operationCounts.getOrDefault(operation, 0) + 1);

        OperationResult<?> result = chain.proceed(request);

        long time = result.getExecutionTimeMs();
        totalExecutionTimes.put(operation, totalExecutionTimes.getOrDefault(operation, 0L) + time);

        return result;
    }

    Map<String, Integer> getOperationCounts() {
        return new LinkedHashMap<>(operationCounts);
    }

    Map<String, Long> getTotalExecutionTimes() {
        return new LinkedHashMap<>(totalExecutionTimes);
    }

    @Override
    public String toString() {
        return String.format("Metrics{counts=%s, times=%s}",
            operationCounts, totalExecutionTimes);
    }
}

// Middleware pipeline builder
class MiddlewarePipeline {
    private final List<TrieMiddleware> middlewares = new CopyOnWriteArrayList<>();

    MiddlewarePipeline addMiddleware(TrieMiddleware middleware) {
        middlewares.add(middleware);
        return this;
    }

    OperationResult<?> execute(TrieRequest request, Trie trie) {
        TrieChain chain = new TrieMiddlewareChain(middlewares, trie);
        return chain.proceed(request);
    }

    List<TrieMiddleware> getMiddlewares() {
        return new ArrayList<>(middlewares);
    }
}
