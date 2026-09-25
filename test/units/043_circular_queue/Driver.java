import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URLClassLoader;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

/**
 * Correctness driver for 043_circular_queue.
 *
 * Baseline (iteration 0): class myQueue(int cap), int ring buffer:
 *   void enqueue(int x)  - on FULL: prints "Queue is full!" and silently drops x
 *                          (no exception, queue unchanged)
 *   int dequeue()        - on EMPTY: prints "Queue is empty!" and returns -1
 *   int getFront()       - on EMPTY: returns -1
 *   int getRear()        - on EMPTY: returns -1
 * No size() accessor. Iterations 1+ become a generic CircularQueue<E>(int)
 * (later extends AbstractQueue, iteration 8+ implements Deque, 9+ renamed
 * CircularDeque); enqueue/dequeue are kept as aliases, front/rear become
 * peek/peekRear or peekFirst/peekLast.
 *
 * Phase A (core FIFO): fixed-seed random enqueue/dequeue/front/rear streams
 *   that never cross a boundary, over capacities 1..13 with heavy wraparound,
 *   checked against a java.util.ArrayDeque model bounded to the capacity
 *   (plus size() whenever the subject exposes it). Includes the baseline demo.
 * Phase B (full/empty contract, must match the baseline EXACTLY):
 *   enqueue on full must be silent (no throw, state unchanged); dequeue on
 *   empty must return the sentinel (-1; a null sentinel is also accepted for a
 *   generic API) without throwing; front/rear on empty likewise. Exercised at
 *   many wrap offsets, with the model re-verified afterwards.
 * Phase C (extra, only for subjects implementing java.util.Deque / Queue):
 *   the JDK-interface methods (offer/poll/peek variants, occurrence removal,
 *   iterator/descendingIterator incl. Iterator.remove) are model-tested
 *   against a capacity-bounded ArrayDeque. Failures here are genuine bugs in
 *   the added API.
 */
public class Driver {
    static int checks = 0, failed = 0;
    static String firstFail = null, firstCoreFail = null;
    static boolean inViolation = false;
    static final LinkedHashSet<String> contractViolations = new LinkedHashSet<>();

    static void fail(String msg) {
        failed++;
        if (firstFail == null) firstFail = msg;
        if (!inViolation && firstCoreFail == null) firstCoreFail = msg;
        if (failed <= 12) System.out.println("CHECK FAIL " + msg);
    }

    static Constructor<?> ctor;
    static Method enq, deq, front, rear, size;
    static boolean primitive;

    public static void main(String[] args) throws Exception {
        Path classes = Paths.get(args[0]).resolve("_classes");
        List<Class<?>> all = scan(classes);
        Class<?> qc = null;
        for (Class<?> c : all) {
            if (c.isInterface() || Modifier.isAbstract(c.getModifiers())) continue;
            String sn = c.getSimpleName().toLowerCase();
            if (sn.contains("test") || sn.contains("demo")) continue;
            try { c.getDeclaredConstructor(int.class); } catch (NoSuchMethodException e) { continue; }
            if (find(c, 1, "enqueue") != null && find(c, 0, "dequeue") != null) { qc = c; break; }
        }
        if (qc == null) {
            System.out.println("RESULT FAIL no queue class with (int) ctor + enqueue/dequeue found");
            return;
        }
        ctor = qc.getDeclaredConstructor(int.class); ctor.setAccessible(true);
        enq = find(qc, 1, "enqueue");
        deq = find(qc, 0, "dequeue");
        front = find(qc, 0, "getFront", "front", "peekFront", "peekFirst", "peek");
        rear = find(qc, 0, "getRear", "rear", "peekRear", "peekLast");
        size = find(qc, 0, "size");
        primitive = enq.getParameterTypes()[0] == int.class;
        System.out.println("subject " + qc.getName() + ": enqueue=" + enq.getName() + " dequeue=" + deq.getName()
                + " front=" + (front == null ? "-" : front.getName()) + " rear=" + (rear == null ? "-" : rear.getName())
                + " size=" + (size == null ? "-" : "yes") + (primitive ? " (int API)" : " (generic API)"));
        if (front == null || rear == null) fail("front/rear accessor missing");

        demo();
        phaseA();
        int beforeB = failed;
        phaseB();
        int bFails = failed - beforeB;
        int beforeC = failed;
        Object probe = ctor.newInstance(4);
        if (probe instanceof Deque) phaseCDeque();
        else if (probe instanceof Queue) phaseCQueue();
        int cFails = failed - beforeC;

        System.out.println(checks + " checks, " + failed + " failed (phase B contract failures: " + bFails
                + ", phase C interface failures: " + cFails + ")");
        if (failed == 0) {
            System.out.println("RESULT PASS " + checks + " checks");
        } else if (bFails == failed && !contractViolations.isEmpty()) {
            System.out.println("RESULT FAIL full/empty contract changed vs baseline (core FIFO/wraparound correct): "
                    + String.join("; ", contractViolations));
        } else {
            System.out.println("RESULT FAIL " + failed + "/" + checks + " checks: "
                    + (firstCoreFail != null ? firstCoreFail : firstFail)
                    + (contractViolations.isEmpty() ? "" : " (plus full/empty contract changes: " + String.join("; ", contractViolations) + ")"));
        }
    }

    // ------------------------------------------------------------------ adapter
    static Object box(int v) { return v; }

    static void doEnq(Object q, int v) throws Exception { enq.invoke(q, primitive ? (Object) v : (Object) Integer.valueOf(v)); }

    static Integer val(Object r) { return r == null ? null : ((Number) r).intValue(); }

    static String ex(Throwable t) {
        Throwable c = t;
        while (c instanceof java.lang.reflect.InvocationTargetException && c.getCause() != null) c = c.getCause();
        return c.getClass().getSimpleName();
    }

    /** Compares observable state (front, rear, size) with the model. */
    static void verifyState(Object q, ArrayDeque<Integer> model, String where) throws Exception {
        checks++;
        if (size != null) {
            int s = ((Number) size.invoke(q)).intValue();
            if (s != model.size()) { fail(where + ": size()=" + s + ", expected " + model.size()); return; }
        }
        if (!model.isEmpty()) {
            Integer f = val(front.invoke(q)), r = val(rear.invoke(q));
            if (!Objects.equals(f, model.peekFirst()) || !Objects.equals(r, model.peekLast()))
                fail(where + ": front/rear=" + f + "/" + r + ", expected " + model.peekFirst() + "/" + model.peekLast()
                        + " model=" + model);
        }
    }

    static void demo() throws Exception {
        Object q = ctor.newInstance(5);
        checks++;
        try {
            doEnq(q, 10); doEnq(q, 20); doEnq(q, 30);
            String s1 = val(front.invoke(q)) + " " + val(rear.invoke(q));
            deq.invoke(q);
            String s2 = val(front.invoke(q)) + " " + val(rear.invoke(q));
            doEnq(q, 40);
            String s3 = val(front.invoke(q)) + " " + val(rear.invoke(q));
            String got = s1 + "|" + s2 + "|" + s3;
            if (!got.equals("10 30|20 30|20 40")) fail("baseline demo printed " + got + ", expected 10 30|20 30|20 40");
        } catch (Throwable t) { fail("baseline demo threw " + ex(t)); }
    }

    // ------------------------------------------------------------------ phase A
    static void phaseA() throws Exception {
        Random rnd = new Random(4343);
        int[] caps = {1, 2, 3, 4, 5, 7, 8, 13};
        for (int cap : caps) {
            Object q = ctor.newInstance(cap);
            ArrayDeque<Integer> model = new ArrayDeque<>();
            int next = 0;
            for (int op = 0; op < 1500; op++) {
                // bias: fill phases and drain phases so front walks around the ring many times
                boolean fillPhase = (op / (cap * 2 + 1)) % 2 == 0;
                int r = rnd.nextInt(100);
                boolean doEnqueue = fillPhase ? r < 70 : r < 30;
                try {
                    if (doEnqueue && model.size() < cap) {
                        int v = next++ % 1000;
                        doEnq(q, v); model.addLast(v);
                    } else if (!model.isEmpty()) {
                        checks++;
                        Integer got = val(deq.invoke(q));
                        Integer exp = model.pollFirst();
                        if (!Objects.equals(got, exp)) fail("A cap=" + cap + " op#" + op + " dequeue()=" + got + ", expected " + exp);
                    }
                    verifyState(q, model, "A cap=" + cap + " op#" + op);
                } catch (Throwable t) {
                    fail("A cap=" + cap + " op#" + op + " threw " + ex(t) + " with model=" + model);
                    break;
                }
                if (failed >= 12) return;
            }
            // drain completely, checking order
            while (!model.isEmpty()) {
                checks++;
                try {
                    Integer got = val(deq.invoke(q)), exp = model.pollFirst();
                    if (!Objects.equals(got, exp)) { fail("A cap=" + cap + " drain dequeue()=" + got + ", expected " + exp); break; }
                } catch (Throwable t) { fail("A cap=" + cap + " drain threw " + ex(t)); break; }
            }
        }
        System.out.println("phase A done, failed so far " + failed);
    }

    // ------------------------------------------------------------------ phase B
    static void phaseB() throws Exception {
        int start = failed;
        int[] caps = {1, 2, 3, 5, 8};
        for (int cap : caps) {
            for (int offset = 0; offset < cap + 2; offset++) {
                Object q = ctor.newInstance(cap);
                ArrayDeque<Integer> model = new ArrayDeque<>();
                int next = 100;
                try {
                    // rotate the ring by `offset` so boundaries happen at different physical slots
                    for (int i = 0; i < offset; i++) { doEnq(q, next); deq.invoke(q); next++; }
                    // empty: front / rear / dequeue
                    emptyChecks(q, model, "cap=" + cap + " off=" + offset + " initially-empty");
                    // fill to capacity then overfill
                    for (int i = 0; i < cap; i++) { doEnq(q, next); model.addLast(next); next++; }
                    verifyState(q, model, "B cap=" + cap + " off=" + offset + " full");
                    for (int extra = 0; extra < 3; extra++) {
                        checks++;
                        String where = "B cap=" + cap + " off=" + offset + " enqueue-on-full#" + extra;
                        try {
                            doEnq(q, 900 + extra);
                        } catch (Throwable t) {
                            violation("enqueue on full throws " + ex(t) + " (baseline: silently ignored)", where);
                        }
                        verifyState(q, model, where + " (state must be unchanged)");
                    }
                    // half drain, refill across the wrap point, overfill again
                    for (int i = 0; i < (cap + 1) / 2; i++) {
                        checks++;
                        Integer got = val(deq.invoke(q)), exp = model.pollFirst();
                        if (!Objects.equals(got, exp)) fail("B cap=" + cap + " off=" + offset + " dequeue()=" + got + ", expected " + exp);
                    }
                    while (model.size() < cap) { doEnq(q, next); model.addLast(next); next++; }
                    checks++;
                    try { doEnq(q, 999); } catch (Throwable t) {
                        violation("enqueue on full throws " + ex(t) + " (baseline: silently ignored)", "B cap=" + cap + " off=" + offset + " refull");
                    }
                    verifyState(q, model, "B cap=" + cap + " off=" + offset + " after wrapped overfill");
                    // drain fully in order
                    while (!model.isEmpty()) {
                        checks++;
                        Integer got = val(deq.invoke(q)), exp = model.pollFirst();
                        if (!Objects.equals(got, exp)) { fail("B cap=" + cap + " off=" + offset + " drain dequeue()=" + got + ", expected " + exp); break; }
                    }
                    emptyChecks(q, model, "cap=" + cap + " off=" + offset + " drained");
                    // still usable afterwards
                    doEnq(q, 4242); model.addLast(4242);
                    verifyState(q, model, "B cap=" + cap + " off=" + offset + " reuse");
                } catch (Throwable t) {
                    fail("B cap=" + cap + " off=" + offset + " unexpected " + ex(t) + " model=" + model);
                }
                if (failed - start >= 60) return;
            }
        }
        System.out.println("phase B done, contract violations: " + contractViolations);
    }

    static void emptyChecks(Object q, ArrayDeque<Integer> model, String where) throws Exception {
        for (int rep = 0; rep < 2; rep++) {
            checks++;
            try {
                Object r = deq.invoke(q);
                if (!isSentinel(r)) fail("B " + where + " dequeue on empty returned " + r + ", expected sentinel -1");
            } catch (Throwable t) {
                violation("dequeue on empty throws " + ex(t) + " (baseline: returns -1)", where);
            }
            checks++;
            try {
                Object r = front.invoke(q);
                if (!isSentinel(r)) fail("B " + where + " front on empty returned " + r + ", expected sentinel -1");
            } catch (Throwable t) {
                violation(front.getName() + " on empty throws " + ex(t) + " (baseline getFront: returns -1)", where);
            }
            checks++;
            try {
                Object r = rear.invoke(q);
                if (!isSentinel(r)) fail("B " + where + " rear on empty returned " + r + ", expected sentinel -1");
            } catch (Throwable t) {
                violation(rear.getName() + " on empty throws " + ex(t) + " (baseline getRear: returns -1)", where);
            }
            verifyState(q, model, "B " + where + " (state must be unchanged)");
        }
    }

    static boolean isSentinel(Object r) {
        return r == null || (r instanceof Number && ((Number) r).intValue() == -1);
    }

    static void violation(String what, String where) {
        if (contractViolations.add(what)) System.out.println("CONTRACT " + what + " [first at " + where + "]");
        inViolation = true;
        fail("B " + where + ": " + what);
        inViolation = false;
    }

    // ------------------------------------------------------------------ phase C
    @SuppressWarnings("unchecked")
    static void phaseCDeque() throws Exception {
        int start = failed;
        Random rnd = new Random(43043);
        for (int cap : new int[]{1, 2, 3, 5, 8, 13}) {
            Deque<Integer> d = (Deque<Integer>) ctor.newInstance(cap);
            ArrayDeque<Integer> m = new ArrayDeque<>();
            int next = 0;
            for (int op = 0; op < 1500; op++) {
                int kind = rnd.nextInt(12);
                String w = "C cap=" + cap + " op#" + op + " ";
                checks++;
                try {
                    switch (kind) {
                        case 0: case 1: { int v = next++ % 50; boolean exp = m.size() < cap; if (exp) m.offerFirst(v);
                            boolean got = d.offerFirst(v); if (got != exp) fail(w + "offerFirst=" + got + " expected " + exp); break; }
                        case 2: case 3: { int v = next++ % 50; boolean exp = m.size() < cap; if (exp) m.offerLast(v);
                            boolean got = d.offerLast(v); if (got != exp) fail(w + "offerLast=" + got + " expected " + exp); break; }
                        case 4: { Integer got = d.pollFirst(), exp = m.pollFirst(); if (!Objects.equals(got, exp)) fail(w + "pollFirst=" + got + " expected " + exp); break; }
                        case 5: { Integer got = d.pollLast(), exp = m.pollLast(); if (!Objects.equals(got, exp)) fail(w + "pollLast=" + got + " expected " + exp); break; }
                        case 6: { Integer got = d.peekFirst(), exp = m.peekFirst(); if (!Objects.equals(got, exp)) fail(w + "peekFirst=" + got + " expected " + exp); break; }
                        case 7: { Integer got = d.peekLast(), exp = m.peekLast(); if (!Objects.equals(got, exp)) fail(w + "peekLast=" + got + " expected " + exp); break; }
                        case 8: { Integer o = rnd.nextInt(50); boolean got = d.removeFirstOccurrence(o), exp = m.removeFirstOccurrence(o);
                            if (got != exp) fail(w + "removeFirstOccurrence(" + o + ")=" + got + " expected " + exp); break; }
                        case 9: { Integer o = rnd.nextInt(50); boolean got = d.removeLastOccurrence(o), exp = m.removeLastOccurrence(o);
                            if (got != exp) fail(w + "removeLastOccurrence(" + o + ")=" + got + " expected " + exp); break; }
                        case 10: { int mod = 2 + rnd.nextInt(3); boolean desc = rnd.nextBoolean();
                            Iterator<Integer> it = desc ? d.descendingIterator() : d.iterator();
                            Iterator<Integer> mi = desc ? m.descendingIterator() : m.iterator();
                            while (it.hasNext()) { Integer x = it.next(); Integer y = mi.next(); if (!x.equals(y)) { fail(w + "iterator mismatch"); break; }
                                if (x % mod == 0) { it.remove(); mi.remove(); } }
                            break; }
                        default: {
                            if (rnd.nextBoolean()) { Integer v = d.poll(), e = m.poll(); if (!Objects.equals(v, e)) fail(w + "poll=" + v + " expected " + e); }
                            else { Integer v = d.peek(), e = m.peek(); if (!Objects.equals(v, e)) fail(w + "peek=" + v + " expected " + e); }
                            break; }
                    }
                    List<Integer> got = new ArrayList<>(d), exp = new ArrayList<>(m);
                    if (!got.equals(exp) || d.size() != m.size()) fail(w + "contents " + got + " expected " + exp);
                    List<Integer> gd = new ArrayList<>(); d.descendingIterator().forEachRemaining(gd::add);
                    List<Integer> ed = new ArrayList<>(); m.descendingIterator().forEachRemaining(ed::add);
                    if (!gd.equals(ed)) fail(w + "descending " + gd + " expected " + ed);
                } catch (Throwable t) {
                    fail(w + "threw " + t);
                }
                if (failed - start >= 12) return;
            }
        }
        System.out.println("phase C (Deque interface) done");
    }

    @SuppressWarnings("unchecked")
    static void phaseCQueue() throws Exception {
        int start = failed;
        Random rnd = new Random(43044);
        for (int cap : new int[]{1, 2, 3, 5, 8, 13}) {
            Queue<Integer> d = (Queue<Integer>) ctor.newInstance(cap);
            ArrayDeque<Integer> m = new ArrayDeque<>();
            int next = 0;
            for (int op = 0; op < 1500; op++) {
                int kind = rnd.nextInt(3);
                String w = "C cap=" + cap + " op#" + op + " ";
                checks++;
                try {
                    if (kind == 0) { int v = next++; boolean exp = m.size() < cap; if (exp) m.offer(v);
                        boolean got = d.offer(v); if (got != exp) fail(w + "offer=" + got + " expected " + exp); }
                    else if (kind == 1) { Integer got = d.poll(), exp = m.poll(); if (!Objects.equals(got, exp)) fail(w + "poll=" + got + " expected " + exp); }
                    else { Integer got = d.peek(), exp = m.peek(); if (!Objects.equals(got, exp)) fail(w + "peek=" + got + " expected " + exp); }
                    List<Integer> got = new ArrayList<>(d), exp = new ArrayList<>(m);
                    if (!got.equals(exp) || d.size() != m.size()) fail(w + "contents " + got + " expected " + exp);
                } catch (Throwable t) {
                    fail(w + "threw " + t);
                }
                if (failed - start >= 12) return;
            }
        }
        System.out.println("phase C (Queue interface) done");
    }

    // ------------------------------------------------------------------ reflection
    static Method find(Class<?> c, int arity, String... names) {
        for (String n : names) {
            for (Class<?> k = c; k != null && k != Object.class; k = k.getSuperclass()) {
                for (Method m : k.getDeclaredMethods()) {
                    if (m.isSynthetic() || m.isBridge() || Modifier.isStatic(m.getModifiers())) continue;
                    if (m.getName().equals(n) && m.getParameterCount() == arity) { m.setAccessible(true); return m; }
                }
            }
        }
        return null;
    }

    static List<Class<?>> scan(Path classes) throws Exception {
        List<String> names;
        try (Stream<Path> s = Files.walk(classes)) {
            names = s.filter(p -> p.toString().endsWith(".class"))
                    .map(p -> classes.relativize(p).toString()
                            .replace(".class", "").replace(File.separatorChar, '.'))
                    .filter(n -> !n.contains("$"))
                    .sorted()
                    .collect(Collectors.toList());
        }
        URLClassLoader cl = new URLClassLoader(new java.net.URL[]{classes.toUri().toURL()});
        List<Class<?>> out = new ArrayList<>();
        for (String n : names) {
            try { out.add(Class.forName(n, false, cl)); } catch (Throwable ignored) {}
        }
        return out;
    }
}
