import java.util.Arrays;

class SortTestCase {

    private final String label;
    private final Runnable task;

    private SortTestCase(String label, Runnable task) {
        this.label = label;
        this.task = task;
    }

    static <T extends Comparable<T>> SortTestCase of(String label, T[] arr, Sorter<T> sorter) {
        return new SortTestCase(label, () -> {
            try {
                sorter.sort(arr);
                System.out.println(label + ": " + Arrays.toString(arr));
            } catch (SortException e) {
                System.out.println(label + " [error]: " + e.getMessage());
            }
        });
    }

    void run() {
        task.run();
    }
}
