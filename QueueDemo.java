final class QueueDemo {

    private QueueDemo() {
    }

    static void run() {
        CircularQueue queue = new CircularQueue(5);
        queue.enqueue(10);
        queue.enqueue(20);
        queue.enqueue(30);
        printEnds(queue);
        queue.dequeue();
        printEnds(queue);
        queue.enqueue(40);
        printEnds(queue);
    }

    private static void printEnds(CircularQueue queue) {
        System.out.println(queue.getFront() + " " + queue.getRear());
    }
}
