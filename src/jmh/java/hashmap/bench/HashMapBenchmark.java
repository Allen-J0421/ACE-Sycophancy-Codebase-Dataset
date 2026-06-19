package hashmap.bench;

import hashmap.OpenAddressingHashMap;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

/**
 * JMH micro-benchmarks comparing {@link OpenAddressingHashMap} against
 * {@link java.util.HashMap} across the core operations.
 *
 * <p>The {@code impl} parameter selects the implementation under test and
 * {@code size} the number of entries, so every benchmark runs head-to-head against
 * the JDK baseline. Run it with the script {@code benchmarks/run.sh} or, with a
 * Maven build, {@code java -jar target/benchmarks.jar}. All JMH options
 * (iterations, forks, parameter filters) can be overridden on the command line —
 * e.g. {@code run.sh -p size=10000 -p impl=OpenAddressing -f 1 -wi 3 -i 5}.
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Thread)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 5, time = 1)
@Fork(2)
public class HashMapBenchmark {

    @Param({"10000", "1000000"})
    public int size;

    @Param({"OpenAddressing", "JdkHashMap"})
    public String impl;

    /** A map pre-populated with {@code size} entries, for read/iterate benchmarks. */
    private Map<Integer, Integer> populated;

    /** Keys that are present in {@code populated}, probed in round-robin order. */
    private Integer[] presentKeys;

    /** Keys that are guaranteed absent, for the lookup-miss benchmark. */
    private Integer[] absentKeys;

    private int cursor;

    @Setup
    public void setup() {
        Random random = new Random(42); // deterministic data set
        presentKeys = new Integer[size];
        absentKeys = new Integer[size];
        populated = newMap();
        for (int i = 0; i < size; i++) {
            int key = random.nextInt();
            presentKeys[i] = key;
            absentKeys[i] = ~key; // bitwise complement is never equal to key
            populated.put(key, i);
        }
    }

    private Map<Integer, Integer> newMap() {
        return "OpenAddressing".equals(impl)
            ? new OpenAddressingHashMap<>()
            : new HashMap<>();
    }

    private int nextIndex() {
        int i = cursor;
        cursor = (i + 1 == size) ? 0 : i + 1;
        return i;
    }

    /** Successful lookup of an existing key. */
    @Benchmark
    public Integer getHit() {
        return populated.get(presentKeys[nextIndex()]);
    }

    /** Unsuccessful lookup, which walks the probe chain to an empty slot. */
    @Benchmark
    public Integer getMiss() {
        return populated.get(absentKeys[nextIndex()]);
    }

    /** Build a fresh map by inserting every key (includes the resize cost). */
    @Benchmark
    public Map<Integer, Integer> populate() {
        Map<Integer, Integer> map = newMap();
        for (int i = 0; i < size; i++) {
            map.put(presentKeys[i], i);
        }
        return map;
    }

    /** Full iteration over the entry set (exercises the struct-of-arrays scan). */
    @Benchmark
    public long iterate() {
        long sum = 0;
        for (Map.Entry<Integer, Integer> e : populated.entrySet()) {
            sum += e.getValue();
        }
        return sum;
    }
}
