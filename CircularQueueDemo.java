/**
 * Small runnable demonstration of {@link CircularQueue}, kept separate so the
 * data structure itself stays free of executable/demo code.
 */
public final class CircularQueueDemo {

    private CircularQueueDemo() {
    }

    public static void main(String[] args) {
        CircularQueue<Integer> queue = new CircularQueue<>(5);
        queue.enqueue(10);
        queue.enqueue(20);
        queue.enqueue(30);
        print(queue);

        queue.dequeue();
        print(queue);

        queue.enqueue(40);
        print(queue);
    }

    private static void print(CircularQueue<Integer> queue) {
        System.out.println(queue + " front=" + queue.peek() + " rear=" + queue.peekRear());
    }
}
