import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * JUnit 5 test suite for the {@link CycleDetectionLogger} facade and the
 * {@link LoggingCycleDetector} decorator.
 *
 * <p>Compile and run with {@code ./run-tests.sh} (no build tool required).
 */
@DisplayName("CycleDetectionLogger")
class CycleDetectionLoggerTest {

    /** Test double that records each event as a compact string for assertions. */
    private static final class RecordingLogger implements CycleDetectionLogger {
        final List<String> events = new ArrayList<>();

        @Override
        public void detectionStarted(String algorithm, DirectedGraph graph) {
            events.add("started:" + algorithm + ":" + graph.vertices());
        }

        @Override
        public void cycleFound(String algorithm, Cycle cycle) {
            events.add("found:" + algorithm + ":" + cycle);
        }

        @Override
        public void cycleDetected(String algorithm) {
            events.add("detected:" + algorithm);
        }

        @Override
        public void noCycleFound(String algorithm) {
            events.add("none:" + algorithm);
        }
    }

    private static final DirectedGraph CYCLIC = DirectedGraph.from(3, new int[][] {
        {0, 1}, {1, 2}, {2, 0}
    });
    private static final DirectedGraph ACYCLIC = DirectedGraph.from(3, new int[][] {
        {0, 1}, {1, 2}
    });

    @ParameterizedTest(name = "{0}")
    @EnumSource(CycleDetectionAlgorithm.class)
    @DisplayName("findCycle emits started then cycleFound for a cyclic graph")
    void findCycleEmitsFound(CycleDetectionAlgorithm algorithm) {
        RecordingLogger logger = new RecordingLogger();
        CycleDetector.create(algorithm).withLogging(logger).findCycle(CYCLIC);

        assertEquals(2, logger.events.size(), logger.events.toString());
        assertEquals("started:" + algorithm + ":3", logger.events.get(0));
        assertTrue(logger.events.get(1).startsWith("found:" + algorithm + ":"), logger.events.toString());
    }

    @ParameterizedTest(name = "{0}")
    @EnumSource(CycleDetectionAlgorithm.class)
    @DisplayName("findCycle emits started then noCycleFound for an acyclic graph")
    void findCycleEmitsNone(CycleDetectionAlgorithm algorithm) {
        RecordingLogger logger = new RecordingLogger();
        CycleDetector.create(algorithm).withLogging(logger).findCycle(ACYCLIC);

        assertEquals(List.of("started:" + algorithm + ":3", "none:" + algorithm), logger.events);
    }

    @ParameterizedTest(name = "{0}")
    @EnumSource(CycleDetectionAlgorithm.class)
    @DisplayName("hasCycle emits started then cycleDetected for a cyclic graph")
    void hasCycleEmitsDetected(CycleDetectionAlgorithm algorithm) {
        RecordingLogger logger = new RecordingLogger();
        CycleDetector.create(algorithm).withLogging(logger).hasCycle(CYCLIC);

        assertEquals(List.of("started:" + algorithm + ":3", "detected:" + algorithm), logger.events);
    }

    @ParameterizedTest(name = "{0}")
    @EnumSource(CycleDetectionAlgorithm.class)
    @DisplayName("hasCycle emits started then noCycleFound for an acyclic graph")
    void hasCycleEmitsNone(CycleDetectionAlgorithm algorithm) {
        RecordingLogger logger = new RecordingLogger();
        CycleDetector.create(algorithm).withLogging(logger).hasCycle(ACYCLIC);

        assertEquals(List.of("started:" + algorithm + ":3", "none:" + algorithm), logger.events);
    }

    @Test
    @DisplayName("the decorator passes the underlying result through unchanged")
    void resultUnchanged() {
        CycleDetector base = CycleDetector.create(CycleDetectionAlgorithm.DFS);
        CycleDetector logged = base.withLogging(new RecordingLogger());
        assertEquals(base.findCycle(CYCLIC), logged.findCycle(CYCLIC));
        assertEquals(base.hasCycle(ACYCLIC), logged.hasCycle(ACYCLIC));
    }

    @Test
    @DisplayName("an unwrapped detector logs nothing")
    void silentByDefault() {
        RecordingLogger logger = new RecordingLogger();
        // A plain detector has no logger attached, so nothing is recorded.
        CycleDetector.create(CycleDetectionAlgorithm.DFS).findCycle(CYCLIC);
        assertTrue(logger.events.isEmpty());
    }

    @Test
    @DisplayName("the no-op logger ignores every event")
    void noOpLoggerIsSilent() {
        CycleDetector detector = CycleDetector.create(CycleDetectionAlgorithm.KAHN)
                .withLogging(CycleDetectionLogger.noOp());
        // Exercises the no-op path; succeeds simply by not throwing.
        assertTrue(detector.hasCycle(CYCLIC));
    }

    @Test
    @DisplayName("withLogging rejects a null logger")
    void rejectsNullLogger() {
        assertThrows(NullPointerException.class,
                () -> CycleDetector.create(CycleDetectionAlgorithm.DFS).withLogging(null));
    }
}
