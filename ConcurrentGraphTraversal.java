import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ConcurrentGraphTraversal implements GraphTraversal {
    private final int threadCount;

    public ConcurrentGraphTraversal(int threadCount) {
        if (threadCount <= 0) {
            throw new IllegalArgumentException("Thread count must be positive");
        }
        this.threadCount = threadCount;
    }

    @Override
    public TraversalResult traverse(Graph graph) {
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        ConcurrentHashMap<Integer, Boolean> visited = new ConcurrentHashMap<>();
        ConcurrentLinkedQueue<Integer> resultQueue = new ConcurrentLinkedQueue<>();
        int componentCount = 0;

        try {
            for (int i = 0; i < graph.getVertexCount(); i++) {
                if (!visited.containsKey(i)) {
                    bfsAsync(graph, i, visited, resultQueue, executor);
                    componentCount++;
                }
            }

            executor.shutdown();
            executor.awaitTermination(Long.MAX_VALUE, java.util.concurrent.TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            Logger.error("Concurrent traversal interrupted", e);
        }

        List<Integer> result = new ArrayList<>(resultQueue);
        return new TraversalResult(result, componentCount);
    }

    private void bfsAsync(Graph graph, int start, ConcurrentHashMap<Integer, Boolean> visited,
                         ConcurrentLinkedQueue<Integer> result, ExecutorService executor) {
        visited.putIfAbsent(start, true);
        executor.submit(() -> {
            ConcurrentLinkedQueue<Integer> queue = new ConcurrentLinkedQueue<>();
            queue.add(start);

            while (!queue.isEmpty()) {
                Integer current = queue.poll();
                if (current == null) continue;

                result.add(current);

                for (int neighbor : graph.getAdjacent(current)) {
                    if (!visited.containsKey(neighbor)) {
                        visited.putIfAbsent(neighbor, true);
                        queue.add(neighbor);
                    }
                }
            }
        });
    }
}
