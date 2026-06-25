final class QueueExamples {

    private QueueExamples() {
    }

    static void runBasicScenario(QueueView<Integer> queue) {
        queue.enqueue(10);
        queue.enqueue(20);
        queue.enqueue(30);
        System.out.println(queue.peekFront() + " " + queue.peekRear());
        queue.dequeue();
        System.out.println(queue.peekFront() + " " + queue.peekRear());
        queue.enqueue(40);
        System.out.println(queue.peekFront() + " " + queue.peekRear());
        System.out.println(queue.snapshot());
    }
}

interface QueueView<T> {
    void enqueue(T value);

    T dequeue();

    T peekFront();

    T peekRear();

    String snapshot();
}
