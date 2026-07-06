class MinHeapTest {
    public static void main(String[] args) {
        MinHeap<Integer> h = new MinHeap<Integer>()
                .insert(3)
                .insert(2)
                .delete(1)
                .insert(15)
                .insert(5)
                .insert(4)
                .insert(45);

        System.out.print(h.extractMin() + " ");
        System.out.print(h.getMin() + " ");

        h.decrease(2, 1);
        System.out.print(h.getMin());
    }
}
