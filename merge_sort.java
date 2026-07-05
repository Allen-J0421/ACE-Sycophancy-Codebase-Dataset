import java.util.Comparator;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import sorting.core.SortContext;
import sorting.core.SortObserver;
import sorting.core.SortTask;

class Main {
    private static <T> void printArray(T[] arr) {
        for (T x : arr)
            System.out.print(x + " ");
        System.out.println();
    }

    public static void main(String[] args) throws Exception {
        SortObserver logger = new SortObserver() {
            public void onSortStart() {
                System.out.printf("[%s] sort started%n",
                    Thread.currentThread().getName());
            }
            public void onSortComplete(long durationNs, long comparisons) {
                System.out.printf("[%s] done: %d ns, %d comparisons%n",
                    Thread.currentThread().getName(), durationNs, comparisons);
            }
        };

        SortContext<Integer> ctx = SortContext.<Integer>builder()
            .algorithm("merge")
            .comparator(Comparator.naturalOrder())
            .observer(logger)
            .build();

        System.out.println("--- sync reuse ---");
        SortTask<Integer> reusableTask = new SortTask<>(new Integer[]{38, 27, 43, 10}, ctx);
        printArray(reusableTask.sort()); // original preserved; safe to re-sort
        printArray(reusableTask.sort());

        System.out.println("--- async ---");
        ExecutorService pool = Executors.newFixedThreadPool(3);

        CompletableFuture<Void> all = CompletableFuture.allOf(
            new SortTask<>(new Integer[]{64, 25, 12, 22, 11}, ctx)
                .sortAsync(pool)
                .thenAccept(sorted -> printArray(sorted)),
            new SortTask<>(new Integer[]{90, 45, 30, 60, 15}, ctx)
                .sortAsync(pool)
                .thenAccept(sorted -> printArray(sorted)),
            new SortTask<>(new Integer[]{8, 4, 2, 6, 1}, ctx)
                .sortAsync(pool)
                .thenAccept(sorted -> printArray(sorted))
        );

        all.join();
        pool.shutdown();
    }
}
