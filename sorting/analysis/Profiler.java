package sorting.analysis;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class Profiler {
    private final Map<String, ProfilingData> data;
    private final String name;

    public Profiler(String name) {
        this.name = name;
        this.data = new ConcurrentHashMap<>();
    }

    public void start(String operation) {
        data.putIfAbsent(operation, new ProfilingData());
    }

    public void end(String operation) {
        ProfilingData profile = data.get(operation);
        if (profile != null) {
            profile.recordOperation();
        }
    }

    public void recordValue(String operation, long value) {
        ProfilingData profile = data.computeIfAbsent(operation, k -> new ProfilingData());
        profile.recordValue(value);
    }

    public ProfilingData getData(String operation) {
        return data.get(operation);
    }

    public String report() {
        StringBuilder sb = new StringBuilder("Profile Report: " + name + "\n");

        for (Map.Entry<String, ProfilingData> entry : data.entrySet()) {
            sb.append(String.format("  %s: calls=%d, total=%d ns, avg=%.2f ns\n",
                    entry.getKey(),
                    entry.getValue().callCount(),
                    entry.getValue().totalValue(),
                    entry.getValue().average()));
        }

        return sb.toString();
    }

    public static class ProfilingData {
        private final AtomicLong callCount = new AtomicLong(0);
        private final AtomicLong totalValue = new AtomicLong(0);

        public void recordOperation() {
            callCount.incrementAndGet();
        }

        public void recordValue(long value) {
            callCount.incrementAndGet();
            totalValue.addAndGet(value);
        }

        public long callCount() {
            return callCount.get();
        }

        public long totalValue() {
            return totalValue.get();
        }

        public double average() {
            long count = callCount.get();
            return count == 0 ? 0 : (double) totalValue.get() / count;
        }

        @Override
        public String toString() {
            return String.format("calls=%d, total=%d ns, avg=%.2f ns",
                    callCount(), totalValue(), average());
        }
    }
}
