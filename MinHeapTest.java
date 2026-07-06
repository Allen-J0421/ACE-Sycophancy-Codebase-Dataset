import java.util.Comparator;

class MinHeapTest {
    public static void main(String[] args) {
        // Declared as Heap<T> — implementation is swappable
        Heap<Integer> minHeap = new MinHeap<Integer>()
                .insert(3)
                .insert(2)
                .delete(1)
                .insert(15)
                .insert(5)
                .insert(4)
                .insert(45);

        System.out.print(minHeap.poll() + " ");
        System.out.print(minHeap.peek() + " ");

        minHeap.decrease(2, 1);
        System.out.print(minHeap.peek());

        System.out.println();

        // Max-heap: same interface, different strategy
        Heap<Integer> maxHeap = new MinHeap<>(new BinaryHeapStrategy<Integer>(Comparator.reverseOrder()));
        maxHeap.insert(3).insert(2).insert(15).insert(5).insert(4).insert(45);
        System.out.print(maxHeap.poll() + " "); // largest first: 45
        System.out.print(maxHeap.poll() + " "); // 15
        System.out.print(maxHeap.peek());        // 5 (next maximum)
    }
}
