import java.util.Arrays;
import java.util.Comparator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import sorting.core.SortContext;
import sorting.core.SortObserver;

class Main {
    private static <T> void printArray(T[] arr) {
        for (T x : arr)
            System.out.print(x + " ");
        System.out.println();
    }

    public static void main(String[] args) throws InterruptedException {
        SortObserver logger = new SortObserver() {
            public void onSortStart() {
                System.out.printf("[thread %s] sort started%n",
                    Thread.currentThread().getName());
            }
            public void onSortComplete(long durationNs, long comparisons) {
                System.out.printf("[thread %s] done: %d ns, %d comparisons%n",
                    Thread.currentThread().getName(), durationNs, comparisons);
            }
        };

        // Build once; reuse safely across any number of sequential or concurrent sorts.
        SortContext<Integer> ctx = SortContext.<Integer>builder()
            .algorithm("merge")
            .comparator(Comparator.naturalOrder())
            .observer(logger)
            .build();

        System.out.println("--- sequential reuse ---");
        Integer[] a = {38, 27, 43, 10};
        Integer[] b = {5, 1, 9, 3, 7};
        ctx.sort(a);
        printArray(a);
        ctx.sort(b);
        printArray(b);

        System.out.println("--- concurrent reuse ---");
        ExecutorService pool = Executors.newFixedThreadPool(3);
        Integer[][] batches = {
            {64, 25, 12, 22, 11},
            {90, 45, 30, 60, 15},
            {8, 4, 2, 6, 1}
        };
        for (Integer[] batch : batches) {
            pool.submit(() -> {
                ctx.sort(batch);
                synchronized (Main.class) { printArray(batch); }
            });
        }
        pool.shutdown();
        pool.awaitTermination(5, TimeUnit.SECONDS);
    }
}
