final class myQueue extends CircularQueue<Integer> {

    public myQueue(int cap) {
        super(cap);
    }

    public void enqueue(int value) {
        enqueue(Integer.valueOf(value));
    }

    @Override
    public void enqueue(Integer value) {
        offer(value);
    }

    @Override
    public Integer dequeue() {
        Integer removed = poll();
        return removed == null ? -1 : removed;
    }

    @Override
    public Integer peekFront() {
        Integer front = peek();
        return front == null ? -1 : front;
    }

    @Override
    public Integer peekRear() {
        if (isEmpty()) {
            return -1;
        }
        return super.peekRear();
    }

    public Integer getFront() {
        return peekFront();
    }

    public Integer getRear() {
        return peekRear();
    }

    public static void main(String[] args) {
        myQueue queue = new myQueue(5);
        QueueExamples.runBasicScenario(queue);
    }
}
