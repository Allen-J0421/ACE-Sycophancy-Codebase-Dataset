class myQueue {

    private final CircularQueue<Integer> delegate;

    public myQueue(int cap) {
        delegate = new CircularQueue<>(cap);
    }

    public void enqueue(int value) {
        delegate.offer(value);
    }

    public int dequeue() {
        Integer removed = delegate.poll();
        if (removed == null) {
            return -1;
        }
        return removed;
    }

    public int getFront() {
        Integer front = delegate.peekFront();
        return front == null ? -1 : front;
    }

    public int getRear() {
        Integer rear = delegate.peekRear();
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
        queue.enqueue(10);
        queue.enqueue(20);
        queue.enqueue(30);
        System.out.println(queue.getFront() + " " + queue.getRear());
        queue.dequeue();
        System.out.println(queue.getFront() + " " + queue.getRear());
        queue.enqueue(40);
        System.out.println(queue.getFront() + " " + queue.getRear());
    }
}
