import java.util.*;

// Performance benchmarking framework

class BenchmarkResult {
    private final String testName;
    private final int iterations;
    private final long totalTimeMs;
    private final long minTimeMs;
    private final long maxTimeMs;
    private final double avgTimeMs;
    private final double opsPerSecond;

    BenchmarkResult(String testName, int iterations, long totalTimeMs, long minTimeMs, long maxTimeMs) {
        this.testName = testName;
        this.iterations = iterations;
        this.totalTimeMs = totalTimeMs;
        this.minTimeMs = minTimeMs;
        this.maxTimeMs = maxTimeMs;
        this.avgTimeMs = (double) totalTimeMs / iterations;
        this.opsPerSecond = (iterations * 1000.0) / totalTimeMs;
    }

    @Override
    public String toString() {
        return String.format(
            "%s: %d iterations in %dms (avg: %.2fms, min: %dms, max: %dms, %.0f ops/sec)",
            testName, iterations, totalTimeMs, avgTimeMs, minTimeMs, maxTimeMs, opsPerSecond);
    }

    String getReport() {
        return String.format(
            "Test: %s\n" +
            "  Iterations: %d\n" +
            "  Total Time: %dms\n" +
            "  Average: %.3fms\n" +
            "  Min: %dms\n" +
            "  Max: %dms\n" +
            "  Throughput: %.0f ops/sec",
            testName, iterations, totalTimeMs, avgTimeMs, minTimeMs, maxTimeMs, opsPerSecond);
    }

    double getAvgTimeMs() {
        return avgTimeMs;
    }

    double getOpsPerSecond() {
        return opsPerSecond;
    }
}

interface Benchmark {
    void setup();
    void execute();
    void teardown();
}

class BenchmarkRunner {
    private final List<BenchmarkResult> results = new ArrayList<>();
    private final int warmupIterations;
    private final int benchmarkIterations;

    BenchmarkRunner(int warmupIterations, int benchmarkIterations) {
        this.warmupIterations = warmupIterations;
        this.benchmarkIterations = benchmarkIterations;
    }

    BenchmarkResult run(String testName, Benchmark benchmark) {
        try {
            // Warmup
            benchmark.setup();
            for (int i = 0; i < warmupIterations; i++) {
                benchmark.execute();
            }
            benchmark.teardown();

            // Actual benchmark
            benchmark.setup();
            long[] times = new long[benchmarkIterations];
            long startTotal = System.currentTimeMillis();

            for (int i = 0; i < benchmarkIterations; i++) {
                long start = System.nanoTime();
                benchmark.execute();
                times[i] = System.nanoTime() - start;
            }

            long totalTimeMs = System.currentTimeMillis() - startTotal;
            benchmark.teardown();

            long minTimeMs = Long.MAX_VALUE;
            long maxTimeMs = Long.MIN_VALUE;
            for (long time : times) {
                minTimeMs = Math.min(minTimeMs, time / 1_000_000);
                maxTimeMs = Math.max(maxTimeMs, time / 1_000_000);
            }

            BenchmarkResult result = new BenchmarkResult(testName, benchmarkIterations, totalTimeMs, minTimeMs, maxTimeMs);
            results.add(result);
            return result;
        } catch (Exception e) {
            System.err.println("Benchmark failed: " + e.getMessage());
            return null;
        }
    }

    void printResults() {
        System.out.println("\n=== Benchmark Results ===\n");
        for (BenchmarkResult result : results) {
            System.out.println(result);
        }
        System.out.println("\n=== Summary ===");
        System.out.println("Total tests: " + results.size());
        double avgThroughput = results.stream()
            .mapToDouble(BenchmarkResult::getOpsPerSecond)
            .average()
            .orElse(0);
        System.out.println("Average throughput: " + String.format("%.0f ops/sec", avgThroughput));
    }

    List<BenchmarkResult> getResults() {
        return new ArrayList<>(results);
    }
}

// Specific benchmarks for Trie operations

class TrieInsertBenchmark implements Benchmark {
    private Trie trie;
    private String[] words;
    private int wordIndex;

    TrieInsertBenchmark(String[] words) {
        this.words = words;
    }

    @Override
    public void setup() {
        trie = new Trie();
        wordIndex = 0;
    }

    @Override
    public void execute() {
        trie.insert(words[wordIndex % words.length]);
        wordIndex++;
    }

    @Override
    public void teardown() {
        trie.clear();
    }
}

class TrieSearchBenchmark implements Benchmark {
    private Trie trie;
    private String[] words;
    private int wordIndex;

    TrieSearchBenchmark(String[] words) {
        this.words = words;
    }

    @Override
    public void setup() {
        trie = new Trie();
        for (String word : words) {
            trie.insert(word);
        }
        wordIndex = 0;
    }

    @Override
    public void execute() {
        trie.search(words[wordIndex % words.length]);
        wordIndex++;
    }

    @Override
    public void teardown() {
        trie.clear();
    }
}

class TriePrefixSearchBenchmark implements Benchmark {
    private Trie trie;
    private String[] prefixes;
    private int prefixIndex;

    TriePrefixSearchBenchmark(String[] prefixes) {
        this.prefixes = prefixes;
    }

    @Override
    public void setup() {
        trie = new Trie();
        for (String prefix : prefixes) {
            trie.insert(prefix + "test");
            trie.insert(prefix + "testing");
            trie.insert(prefix + "tester");
        }
        prefixIndex = 0;
    }

    @Override
    public void execute() {
        trie.findWordsWithPrefix(prefixes[prefixIndex % prefixes.length]);
        prefixIndex++;
    }

    @Override
    public void teardown() {
        trie.clear();
    }
}

class MemoryBenchmark implements Benchmark {
    private Trie trie;
    private int wordCount;

    MemoryBenchmark(int wordCount) {
        this.wordCount = wordCount;
    }

    @Override
    public void setup() {
        trie = new Trie();
    }

    @Override
    public void execute() {
        for (int i = 0; i < wordCount; i++) {
            trie.insert("word" + i);
        }
    }

    @Override
    public void teardown() {
        Runtime runtime = Runtime.getRuntime();
        long usedMemory = runtime.totalMemory() - runtime.freeMemory();
        System.out.println("  Memory used: " + (usedMemory / 1024 / 1024) + " MB for " + wordCount + " words");
    }
}

class CacheEffectivenessBenchmark implements Benchmark {
    private Trie trie;
    private String[] words;
    private int searchIndex;

    CacheEffectivenessBenchmark(String[] words) {
        this.words = words;
    }

    @Override
    public void setup() {
        trie = new Trie();
        for (String word : words) {
            trie.insert(word);
        }
        searchIndex = 0;
    }

    @Override
    public void execute() {
        // Repeated searches should benefit from cache
        trie.search(words[0]);
        trie.search(words[0]);
        trie.search(words[1]);
        searchIndex++;
    }

    @Override
    public void teardown() {
        trie.clear();
    }
}
