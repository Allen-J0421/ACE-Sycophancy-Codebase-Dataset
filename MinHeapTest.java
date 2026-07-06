import java.util.Comparator;

class MinHeapTest {
    public static void main(String[] args) {
        // Default min-heap via BinaryHeapStrategy(naturalOrder)
        MinHeap<Integer> minHeap = new MinHeap<Integer>()
                .insert(3)
                .insert(2)
                .delete(1)
                .insert(15)
                .insert(5)
                .insert(4)
                .insert(45);

        System.out.print(minHeap.extractMin() + " ");
        System.out.print(minHeap.getMin() + " ");

        minHeap.decrease(2, 1);
        System.out.print(minHeap.getMin());

        System.out.println();

        // Max-heap by supplying a reversed BinaryHeapStrategy
        MinHeap<Integer> maxHeap = new MinHeap<>(new BinaryHeapStrategy<Integer>(Comparator.reverseOrder()));
        maxHeap.insert(3).insert(2).insert(15).insert(5).insert(4).insert(45);
        System.out.print(maxHeap.extractMin() + " "); // largest first: 45
        System.out.print(maxHeap.extractMin() + " "); // 15
        System.out.print(maxHeap.getMin());            // 5 (next maximum)
    }
}
