final class myQueue implements QueueView<Integer> {

    private final CircularQueue<Integer> delegate;

    public myQueue(int cap) {
        delegate = new CircularQueue<>(cap);
    }

    public void enqueue(int value) {
        enqueue(Integer.valueOf(value));
    }

    @Override
    public void enqueue(Integer value) {
        delegate.offer(value);
    }

    @Override
    public Integer dequeue() {
        Integer removed = delegate.poll();
        if (removed == null) {
            return -1;
        }
        return removed;
    }

    @Override
    public Integer peekFront() {
        return delegate.peekFront();
    }

    @Override
    public Integer peekRear() {
        return delegate.peekRear();
    }

    @Override
    public String snapshot() {
        return delegate.toString();
    }

    public int getFront() {
        Integer front = peekFront();
        return front == null ? -1 : front;
    }

    public int getRear() {
        Integer rear = peekRear();
        return rear == null ? -1 : rear;
    }

    public int size() {
        return delegate.size();
    }

    public boolean isEmpty() {
        return delegate.isEmpty();
    }

    public boolean isFull() {
        return delegate.isFull();
    }

    public int capacity() {
        return delegate.capacity();
    }

    public static void main(String[] args) {
        myQueue queue = new myQueue(5);
        QueueExamples.runBasicScenario(queue);
    }
}
