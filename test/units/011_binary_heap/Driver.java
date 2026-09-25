import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URLClassLoader;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

/**
 * Correctness driver for 011_binary_heap.
 *
 * Baseline: class MinHeap(int capacity) with insertKey(int)->boolean,
 * getMin()->int, extractMin()->int (Integer.MAX_VALUE when empty).
 * Iterations 5+ generify to MinHeap<T> (no-arg ctor, insertKey(T),
 * getMin()/extractMin() throwing on empty); iteration 8+ has insertKey
 * returning a Handle. The oracle models the heap with
 * java.util.PriorityQueue<Integer> over scripted op sequences (fixed seed):
 * every getMin/extractMin must return the model's minimum. On an empty heap,
 * extractMin may signal emptiness by exception, null, or the baseline's
 * Integer.MAX_VALUE sentinel — but never a live element value.
 *
 * Values inserted are confined to [0, 1_000_000) so the MAX_VALUE sentinel
 * is unambiguous.
 */
public class Driver {
    public static void main(String[] args) throws Exception {
        Path classes = Paths.get(args[0]).resolve("_classes");
        List<String> classNames;
        try (Stream<Path> s = Files.walk(classes)) {
            classNames = s.filter(p -> p.toString().endsWith(".class"))
                    .map(p -> classes.relativize(p).toString()
                            .replace(".class", "").replace(File.separatorChar, '.'))
                    .filter(n -> !n.contains("$"))
                    .collect(Collectors.toList());
        }
        URLClassLoader cl = new URLClassLoader(new java.net.URL[]{classes.toUri().toURL()});

        Class<?> heapClass = null;
        Method insert = null, extract = null, peek = null;
        for (String cn : classNames) {
            Class<?> c;
            try { c = Class.forName(cn, false, cl); } catch (Throwable t) { continue; }
            if (c.getSimpleName().toLowerCase().contains("test")) continue;
            Method ins = null, ext = null, pk = null;
            for (Method m : c.getDeclaredMethods()) {
                String n = m.getName().toLowerCase();
                Class<?>[] p = m.getParameterTypes();
                if (p.length == 1 && (p[0] == int.class || p[0] == Integer.class
                        || p[0] == Object.class || Comparable.class.isAssignableFrom(p[0]))
                        && (n.equals("insertkey") || n.equals("insert") || n.equals("offer"))) {
                    if (ins == null || n.equals("insertkey")) ins = m;
                }
                if (p.length == 0 && (n.equals("extractmin") || n.equals("poll")
                        || n.equals("removemin") || n.equals("deletemin"))) {
                    if (ext == null || n.equals("extractmin")) ext = m;
                }
                if (p.length == 0 && (n.equals("getmin") || n.equals("peek")
                        || n.equals("findmin") || n.equals("min"))) {
                    if (pk == null || n.equals("getmin")) pk = m;
                }
            }
            if (ins != null && ext != null) {
                heapClass = c; insert = ins; extract = ext; peek = pk;
                break;
            }
        }
        if (heapClass == null) {
            System.out.println("RESULT FAIL no min-heap entry point (insertKey + extractMin) among " + classNames);
            return;
        }
        boolean primitive = insert.getParameterTypes()[0] == int.class;
        insert.setAccessible(true); extract.setAccessible(true);
        if (peek != null) peek.setAccessible(true);
        System.out.println("class: " + heapClass.getName() + " primitive=" + primitive
                + " insert=" + insert.getName() + " extract=" + extract.getName()
                + " peek=" + (peek == null ? "none" : peek.getName()));

        Random rnd = new Random(90210L);
        int checks = 0, failed = 0;

        for (int trial = 0; trial < 6 && failed == 0; trial++) {
            // Build a deterministic op script: 0=insert, 1=extract, 2=peek.
            int opsCount = trial == 0 ? 12 : 400;
            int[] ops = new int[opsCount];
            int[] vals = new int[opsCount];
            int inserts = 0;
            for (int i = 0; i < opsCount; i++) {
                int r = rnd.nextInt(10);
                ops[i] = r < 6 ? 0 : (r < 9 ? 1 : 2);
                if (ops[i] == 0) { vals[i] = rnd.nextInt(1_000_000); inserts++; }
            }
            Object heap = construct(heapClass, Math.max(1, inserts));
            if (heap == null) {
                System.out.println("RESULT FAIL cannot construct " + heapClass.getName());
                return;
            }
            PriorityQueue<Integer> model = new PriorityQueue<>();

            // Empty-heap extract must not produce a live value.
            checks++;
            Object emptyR = tryInvoke(extract, heap);
            if (emptyR instanceof ExInfo || emptyR == null
                    || (emptyR instanceof Number num && num.longValue() == Integer.MAX_VALUE)) {
                // acceptable empty signal
            } else {
                failed++;
                System.out.println("CHECK FAIL extract on empty heap returned live value " + emptyR);
            }

            for (int i = 0; i < opsCount && failed == 0; i++) {
                if (ops[i] == 0) {
                    checks++;
                    Object r = primitive ? tryInvoke(insert, heap, vals[i])
                                         : tryInvoke(insert, heap, Integer.valueOf(vals[i]));
                    if (r instanceof ExInfo ex) {
                        failed++;
                        System.out.println("CHECK FAIL insert(" + vals[i] + ") threw " + ex.t);
                        break;
                    }
                    if (r instanceof Boolean b && !b) {
                        failed++;
                        System.out.println("CHECK FAIL insert(" + vals[i] + ") returned false (capacity refused)");
                        break;
                    }
                    model.add(vals[i]);
                } else if (ops[i] == 1) {
                    checks++;
                    Object r = tryInvoke(extract, heap);
                    if (model.isEmpty()) {
                        if (r instanceof ExInfo || r == null
                                || (r instanceof Number num && num.longValue() == Integer.MAX_VALUE)) {
                            // ok: empty signal
                        } else {
                            failed++;
                            System.out.println("CHECK FAIL extract on empty returned " + r);
                        }
                    } else {
                        Integer want = model.poll();
                        if (!(r instanceof Number num) || num.longValue() != want) {
                            failed++;
                            System.out.println("CHECK FAIL extractMin returned " + describe(r) + " expected " + want);
                        }
                    }
                } else if (peek != null && !model.isEmpty()) {
                    checks++;
                    Object r = tryInvoke(peek, heap);
                    Integer want = model.peek();
                    if (!(r instanceof Number num) || num.longValue() != want) {
                        failed++;
                        System.out.println("CHECK FAIL getMin returned " + describe(r) + " expected " + want);
                    }
                }
            }
            // Drain fully; extraction order must be exactly ascending model order.
            while (!model.isEmpty() && failed == 0) {
                checks++;
                Object r = tryInvoke(extract, heap);
                Integer want = model.poll();
                if (!(r instanceof Number num) || num.longValue() != want) {
                    failed++;
                    System.out.println("CHECK FAIL drain extractMin returned " + describe(r) + " expected " + want);
                }
            }
            System.out.println("trial " + trial + " done, checks so far " + checks);
        }

        System.out.println(checks + " checks, " + failed + " failed");
        System.out.println(failed == 0 ? "RESULT PASS " + checks + " heap ops verified"
                                       : "RESULT FAIL " + failed + " heap op(s) diverged from PriorityQueue model");
    }

    /** Try no-arg ctor first (generic versions), then (int capacity). */
    static Object construct(Class<?> c, int capacity) {
        try {
            Constructor<?> k = c.getDeclaredConstructor();
            k.setAccessible(true);
            return k.newInstance();
        } catch (Throwable ignored) { }
        try {
            Constructor<?> k = c.getDeclaredConstructor(int.class);
            k.setAccessible(true);
            return k.newInstance(capacity);
        } catch (Throwable ignored) { }
        return null;
    }

    record ExInfo(Throwable t) { }

    static Object tryInvoke(Method m, Object recv, Object... a) {
        try {
            return m.invoke(recv, a);
        } catch (Throwable e) {
            return new ExInfo(e.getCause() != null ? e.getCause() : e);
        }
    }

    static String describe(Object r) {
        if (r instanceof ExInfo ex) return "exception " + ex.t;
        return String.valueOf(r);
    }
}
