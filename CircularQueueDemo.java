public final class CircularQueueDemo {
    private CircularQueueDemo() {
    }

    public static void main(String[] args) {
        CircularQueue<Integer> queue = new CircularQueue<>(5);
        QueueExamples.runBasicScenario(queue);
    }
}
