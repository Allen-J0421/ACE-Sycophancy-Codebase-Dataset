/**
 * Small runnable demonstration of {@link CircularDeque}, kept separate so the
 * data structure itself stays free of executable/demo code.
 */
public final class CircularDequeDemo {

    private CircularDequeDemo() {
    }

    public static void main(String[] args) {
        CircularDeque<Integer> queue = new CircularDeque<>(5);
        queue.enqueue(10);
        queue.enqueue(20);
        queue.enqueue(30);
        print(queue);

        queue.dequeue();
        print(queue);

        queue.enqueue(40);
        print(queue);
    }

    private static void print(CircularDeque<Integer> queue) {
        System.out.println(queue + " front=" + queue.peekFirst() + " rear=" + queue.peekLast());
    }
}
