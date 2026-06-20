import java.util.ArrayList;
import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Queue;

/**
 * Dependency-free test suite for {@link CircularQueue}.
 *
 * <p>The repository compiles each algorithm standalone with {@code javac} and
 * has no JUnit on the classpath, so this uses a tiny built-in harness instead
 * of a framework. Run with {@code java CircularQueueTest}; it prints a summary
 * and exits non-zero if any test fails.
 */
public final class CircularQueueTest {

    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        // Core ring-buffer behaviour
        constructorRejectsNonPositiveCapacity();
        newQueueIsEmpty();
        enqueueDequeuePreservesFifoOrder();
        wrapAroundKeepsOrder();
        clearResetsToEmptyKeepingCapacity();
        equalsIgnoresInternalAlignment();
        equalHashCodesForEqualQueues();
        toStringRendersFrontToRear();
        nullElementsAreRejected();

        // java.util.Queue contract
        usableAsQueueInterface();
        offerReturnsFalseWhenFull();
        addThrowsWhenFull();
        pollAndPeekReturnNullWhenEmpty();
        removeAndElementThrowWhenEmpty();
        inheritedCollectionOperations();

        // java.util.Deque contract
        usableAsDeque();
        bothEndsInsertAndRemove();
        offerFirstLastReturnFalseWhenFull();
        getFirstLastThrowWhenEmpty();
        usableAsStack();
        removeFirstOccurrenceRemovesFrontMost();
        removeLastOccurrenceRemovesRearMost();
        removeObjectDelegatesToFirstOccurrence();
        descendingIteratorGoesRearToFront();

        // Iteration
        iteratorYieldsFrontToRear();
        iteratorIsFailFast();
        iteratorRemoveDeletesCurrent();
        descendingIteratorRemoveDeletesCurrent();
        bulkRemovalViaIterator();

        System.out.printf("%n%d passed, %d failed%n", passed, failed);
        if (failed > 0) {
            System.exit(1);
        }
    }

    // --- core -------------------------------------------------------------

    private static void constructorRejectsNonPositiveCapacity() {
        String name = "constructorRejectsNonPositiveCapacity";
        expectThrows(name, IllegalArgumentException.class, () -> new CircularQueue<>(0));
        expectThrows(name, IllegalArgumentException.class, () -> new CircularQueue<>(-3));
    }

    private static void newQueueIsEmpty() {
        String name = "newQueueIsEmpty";
        CircularQueue<Integer> q = new CircularQueue<>(4);
        check(name, q.isEmpty(), "should be empty");
        check(name, !q.isFull(), "should not be full");
        checkEquals(name, 0, q.size());
        checkEquals(name, 4, q.capacity());
    }

    private static void enqueueDequeuePreservesFifoOrder() {
        String name = "enqueueDequeuePreservesFifoOrder";
        CircularQueue<Integer> q = new CircularQueue<>(3);
        q.enqueue(10);
        q.enqueue(20);
        q.enqueue(30);
        check(name, q.isFull(), "should be full at capacity");
        checkEquals(name, 10, q.dequeue());
        checkEquals(name, 20, q.dequeue());
        checkEquals(name, 30, q.dequeue());
        check(name, q.isEmpty(), "should be empty after draining");
    }

    private static void wrapAroundKeepsOrder() {
        String name = "wrapAroundKeepsOrder";
        // Fill, drain part, then refill so the rear wraps past index 0.
        CircularQueue<Integer> q = new CircularQueue<>(3);
        q.enqueue(1);
        q.enqueue(2);
        q.enqueue(3);
        checkEquals(name, 1, q.dequeue());
        checkEquals(name, 2, q.dequeue());
        q.enqueue(4); // wraps to index 0
        q.enqueue(5); // index 1
        checkEquals(name, 3, q.dequeue());
        checkEquals(name, 4, q.dequeue());
        checkEquals(name, 5, q.dequeue());
        check(name, q.isEmpty(), "should be empty after wrap-around drain");
    }

    private static void clearResetsToEmptyKeepingCapacity() {
        String name = "clearResetsToEmptyKeepingCapacity";
        CircularQueue<Integer> q = new CircularQueue<>(3);
        q.enqueue(1);
        q.enqueue(2);
        q.clear();
        check(name, q.isEmpty(), "should be empty after clear");
        checkEquals(name, 0, q.size());
        checkEquals(name, 3, q.capacity());
        q.enqueue(42); // still usable after clear
        checkEquals(name, 42, q.peekFirst());
    }

    private static void equalsIgnoresInternalAlignment() {
        String name = "equalsIgnoresInternalAlignment";
        // Same logical contents (2,3) but different internal front offsets.
        CircularQueue<Integer> aligned = new CircularQueue<>(3);
        aligned.enqueue(2);
        aligned.enqueue(3);

        CircularQueue<Integer> rotated = new CircularQueue<>(3);
        rotated.enqueue(9);
        rotated.dequeue();   // advance front
        rotated.enqueue(2);
        rotated.enqueue(3);

        check(name, aligned.equals(rotated), "equal contents should be equal");
        check(name, rotated.equals(aligned), "equality should be symmetric");

        rotated.enqueue(4);
        check(name, !aligned.equals(rotated), "different sizes should not be equal");
    }

    private static void equalHashCodesForEqualQueues() {
        String name = "equalHashCodesForEqualQueues";
        CircularQueue<Integer> a = new CircularQueue<>(4);
        a.enqueue(5);
        a.enqueue(6);
        CircularQueue<Integer> b = new CircularQueue<>(2);
        b.enqueue(5);
        b.enqueue(6);
        check(name, a.equals(b), "queues should be equal");
        checkEquals(name, a.hashCode(), b.hashCode());
    }

    private static void toStringRendersFrontToRear() {
        String name = "toStringRendersFrontToRear";
        CircularQueue<Integer> q = new CircularQueue<>(5);
        q.enqueue(10);
        q.enqueue(20);
        q.enqueue(30);
        checkEquals(name, "[10, 20, 30]", q.toString());
        checkEquals(name, "[]", new CircularQueue<Integer>(2).toString());
    }

    private static void nullElementsAreRejected() {
        String name = "nullElementsAreRejected";
        CircularQueue<String> q = new CircularQueue<>(2);
        expectThrows(name, NullPointerException.class, () -> q.offer(null));
        expectThrows(name, NullPointerException.class, () -> q.add(null));
        expectThrows(name, NullPointerException.class, () -> q.offerFirst(null));
        expectThrows(name, NullPointerException.class, () -> q.push(null));
    }

    // --- java.util.Queue contract -----------------------------------------

    private static void usableAsQueueInterface() {
        String name = "usableAsQueueInterface";
        Queue<Integer> q = new CircularQueue<>(3); // referenced through the interface
        check(name, q.offer(1), "offer should succeed with space");
        check(name, q.offer(2), "offer should succeed with space");
        checkEquals(name, 1, q.peek());
        checkEquals(name, 1, q.poll());
        checkEquals(name, 2, q.poll());
        checkEquals(name, null, q.poll());
    }

    private static void offerReturnsFalseWhenFull() {
        String name = "offerReturnsFalseWhenFull";
        CircularQueue<Integer> q = new CircularQueue<>(2);
        check(name, q.offer(1), "first offer should succeed");
        check(name, q.offer(2), "second offer should succeed");
        check(name, !q.offer(3), "offer should return false when full");
        checkEquals(name, 2, q.size());
    }

    private static void addThrowsWhenFull() {
        String name = "addThrowsWhenFull";
        CircularQueue<Integer> q = new CircularQueue<>(2);
        q.add(1);
        q.add(2);
        expectThrows(name, IllegalStateException.class, () -> q.add(3));
        expectThrows(name, IllegalStateException.class, () -> q.enqueue(3)); // alias
    }

    private static void pollAndPeekReturnNullWhenEmpty() {
        String name = "pollAndPeekReturnNullWhenEmpty";
        CircularQueue<Integer> q = new CircularQueue<>(2);
        checkEquals(name, null, q.poll());
        checkEquals(name, null, q.peek());
        checkEquals(name, null, q.peekFirst());
        checkEquals(name, null, q.peekLast());
    }

    private static void removeAndElementThrowWhenEmpty() {
        String name = "removeAndElementThrowWhenEmpty";
        CircularQueue<Integer> q = new CircularQueue<>(2);
        expectThrows(name, NoSuchElementException.class, q::remove);
        expectThrows(name, NoSuchElementException.class, q::element);
        expectThrows(name, NoSuchElementException.class, q::dequeue); // alias
    }

    private static void inheritedCollectionOperations() {
        String name = "inheritedCollectionOperations";
        CircularQueue<Integer> q = new CircularQueue<>(5);
        q.addAll(List.of(1, 2, 3)); // inherited from AbstractQueue
        check(name, q.contains(2), "contains should find an element");
        check(name, !q.contains(99), "contains should reject a missing element");
        checkEquals(name, 3, q.size());
        checkEquals(name, List.of(1, 2, 3), Arrays.asList(q.toArray())); // inherited toArray
    }

    // --- java.util.Deque contract -----------------------------------------

    private static void usableAsDeque() {
        String name = "usableAsDeque";
        Deque<Integer> d = new CircularQueue<>(4); // referenced through the interface
        d.addFirst(2);
        d.addFirst(1); // front -> [1, 2]
        d.addLast(3);
        d.addLast(4);  // -> [1, 2, 3, 4]
        checkEquals(name, 1, d.getFirst());
        checkEquals(name, 4, d.getLast());
        checkEquals(name, 1, d.pollFirst());
        checkEquals(name, 4, d.pollLast());
        checkEquals(name, List.of(2, 3), new ArrayList<>(d));
    }

    private static void bothEndsInsertAndRemove() {
        String name = "bothEndsInsertAndRemove";
        CircularQueue<Integer> d = new CircularQueue<>(3);
        d.offerFirst(2);
        d.offerLast(3);
        d.offerFirst(1); // -> [1, 2, 3]
        checkEquals(name, "[1, 2, 3]", d.toString());
        checkEquals(name, 1, d.peekFirst());
        checkEquals(name, 3, d.peekLast());
        checkEquals(name, 3, d.removeLast());
        checkEquals(name, 1, d.removeFirst());
        checkEquals(name, "[2]", d.toString());
    }

    private static void offerFirstLastReturnFalseWhenFull() {
        String name = "offerFirstLastReturnFalseWhenFull";
        CircularQueue<Integer> d = new CircularQueue<>(2);
        d.offerFirst(1);
        d.offerLast(2);
        check(name, !d.offerFirst(3), "offerFirst should return false when full");
        check(name, !d.offerLast(4), "offerLast should return false when full");
        expectThrows(name, IllegalStateException.class, () -> d.addFirst(5));
        expectThrows(name, IllegalStateException.class, () -> d.addLast(6));
    }

    private static void getFirstLastThrowWhenEmpty() {
        String name = "getFirstLastThrowWhenEmpty";
        CircularQueue<Integer> d = new CircularQueue<>(2);
        expectThrows(name, NoSuchElementException.class, d::getFirst);
        expectThrows(name, NoSuchElementException.class, d::getLast);
        expectThrows(name, NoSuchElementException.class, d::removeFirst);
        expectThrows(name, NoSuchElementException.class, d::removeLast);
        expectThrows(name, NoSuchElementException.class, d::pop);
    }

    private static void usableAsStack() {
        String name = "usableAsStack";
        Deque<Integer> stack = new CircularQueue<>(3);
        stack.push(1);
        stack.push(2);
        stack.push(3);
        checkEquals(name, 3, stack.peek()); // LIFO top is the front
        checkEquals(name, 3, stack.pop());
        checkEquals(name, 2, stack.pop());
        checkEquals(name, 1, stack.pop());
        check(name, stack.isEmpty(), "stack should be empty after popping all");
    }

    private static void removeFirstOccurrenceRemovesFrontMost() {
        String name = "removeFirstOccurrenceRemovesFrontMost";
        CircularQueue<Integer> d = new CircularQueue<>(6);
        d.addAll(List.of(1, 2, 3, 2, 1));
        check(name, d.removeFirstOccurrence(2), "should report removal");
        checkEquals(name, List.of(1, 3, 2, 1), new ArrayList<>(d));
        check(name, !d.removeFirstOccurrence(99), "missing element returns false");
    }

    private static void removeLastOccurrenceRemovesRearMost() {
        String name = "removeLastOccurrenceRemovesRearMost";
        CircularQueue<Integer> d = new CircularQueue<>(6);
        d.addAll(List.of(1, 2, 3, 2, 1));
        check(name, d.removeLastOccurrence(2), "should report removal");
        checkEquals(name, List.of(1, 2, 3, 1), new ArrayList<>(d));
    }

    private static void removeObjectDelegatesToFirstOccurrence() {
        String name = "removeObjectDelegatesToFirstOccurrence";
        CircularQueue<Integer> d = new CircularQueue<>(6);
        d.addAll(List.of(5, 6, 5));
        check(name, d.remove(Integer.valueOf(5)), "remove(Object) should remove front-most 5");
        checkEquals(name, List.of(6, 5), new ArrayList<>(d));
    }

    private static void descendingIteratorGoesRearToFront() {
        String name = "descendingIteratorGoesRearToFront";
        CircularQueue<Integer> d = new CircularQueue<>(4);
        d.addAll(List.of(1, 2, 3));
        List<Integer> seen = new ArrayList<>();
        Iterator<Integer> it = d.descendingIterator();
        while (it.hasNext()) {
            seen.add(it.next());
        }
        checkEquals(name, List.of(3, 2, 1), seen);
    }

    // --- iteration --------------------------------------------------------

    private static void iteratorYieldsFrontToRear() {
        String name = "iteratorYieldsFrontToRear";
        CircularQueue<Integer> q = new CircularQueue<>(3);
        q.enqueue(1);
        q.enqueue(2);
        q.dequeue();
        q.enqueue(3);
        q.enqueue(4); // contents front->rear: 2, 3, 4
        List<Integer> seen = new ArrayList<>();
        for (Integer v : q) {
            seen.add(v);
        }
        checkEquals(name, List.of(2, 3, 4), seen);
    }

    private static void iteratorIsFailFast() {
        String name = "iteratorIsFailFast";
        CircularQueue<Integer> q = new CircularQueue<>(3);
        q.enqueue(1);
        q.enqueue(2);
        Iterator<Integer> it = q.iterator();
        it.next();
        q.enqueue(3); // structural modification
        expectThrows(name, ConcurrentModificationException.class, it::next);
    }

    private static void iteratorRemoveDeletesCurrent() {
        String name = "iteratorRemoveDeletesCurrent";
        CircularQueue<Integer> q = new CircularQueue<>(6);
        q.addAll(List.of(1, 2, 3, 4, 5));
        Iterator<Integer> it = q.iterator();
        while (it.hasNext()) {
            if (it.next() % 2 == 0) {
                it.remove(); // drop evens
            }
        }
        checkEquals(name, List.of(1, 3, 5), new ArrayList<>(q));
        // remove() without a preceding next() is illegal
        expectThrows(name, IllegalStateException.class, () -> q.iterator().remove());
    }

    private static void descendingIteratorRemoveDeletesCurrent() {
        String name = "descendingIteratorRemoveDeletesCurrent";
        CircularQueue<Integer> q = new CircularQueue<>(6);
        q.addAll(List.of(1, 2, 3, 4, 5));
        Iterator<Integer> it = q.descendingIterator();
        while (it.hasNext()) {
            if (it.next() % 2 == 0) {
                it.remove(); // drop evens, iterating rear->front
            }
        }
        checkEquals(name, List.of(1, 3, 5), new ArrayList<>(q));
    }

    private static void bulkRemovalViaIterator() {
        String name = "bulkRemovalViaIterator";
        CircularQueue<Integer> q = new CircularQueue<>(8);
        q.addAll(List.of(1, 2, 3, 4, 5, 6));
        q.removeIf(v -> v > 3);          // inherited, uses iterator().remove()
        checkEquals(name, List.of(1, 2, 3), new ArrayList<>(q));
        q.retainAll(List.of(2));         // inherited
        checkEquals(name, List.of(2), new ArrayList<>(q));
    }

    // --- tiny assertion harness -------------------------------------------

    private interface Block {
        void run() throws Throwable;
    }

    private static void check(String test, boolean condition, String message) {
        if (condition) {
            pass();
        } else {
            fail(test, message);
        }
    }

    private static void checkEquals(String test, Object expected, Object actual) {
        if (java.util.Objects.equals(expected, actual)) {
            pass();
        } else {
            fail(test, "expected <" + expected + "> but was <" + actual + ">");
        }
    }

    private static void expectThrows(String test, Class<? extends Throwable> expected, Block block) {
        try {
            block.run();
            fail(test, "expected " + expected.getSimpleName() + " but nothing was thrown");
        } catch (Throwable thrown) {
            if (expected.isInstance(thrown)) {
                pass();
            } else {
                fail(test, "expected " + expected.getSimpleName() + " but got " + thrown);
            }
        }
    }

    private static void pass() {
        passed++;
    }

    private static void fail(String test, String message) {
        failed++;
        System.out.println("FAIL: " + test + " - " + message);
    }
}
