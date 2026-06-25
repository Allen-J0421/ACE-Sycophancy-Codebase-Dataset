public final class CircularQueueDemo {
    private CircularQueueDemo() {
    }

    public static void main(String[] args) {
        CircularQueue<Integer> queue = new CircularQueue<>(5);
        queue.enqueue(10);
        queue.enqueue(20);
        queue.enqueue(30);
        System.out.println(queue.peekFront() + " " + queue.peekRear());
        queue.dequeue();
        System.out.println(queue.peekFront() + " " + queue.peekRear());
        queue.enqueue(40);
        System.out.println(queue.peekFront() + " " + queue.peekRear());
        System.out.println(queue);
    }
}
