class myQueue {

    private final CircularQueue<Integer> queue;

    public myQueue(int cap) {
        queue = new CircularQueue<>(cap);
    }

    public void enqueue(int value) {
        queue.offer(value);
    }

    public int dequeue() {
        Integer removed = queue.poll();
        if (removed == null) {
            return -1;
        }
        return removed;
    }

    public int getFront() {
        Integer front = queue.peekFront();
        return front == null ? -1 : front;
    }

    public int getRear() {
        Integer rear = queue.peekRear();
        return rear == null ? -1 : rear;
    }

    public int size() {
        return queue.size();
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }

    public boolean isFull() {
        return queue.isFull();
    }

    public int capacity() {
        return queue.capacity();
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
