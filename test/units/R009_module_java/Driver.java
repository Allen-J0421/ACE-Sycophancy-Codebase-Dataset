import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

/**
 * Correctness driver for R009_module_java (safari predator-prey simulation:
 * Lion/Cheetah/Jaguar/Gazelle/Zebra/Hunter/Grass).
 *
 * The subject is DETERMINISTIC: all randomness flows through the shared
 * Randomizer (fixed seed 1111, useShared=true). Oracle = golden per-step
 * population trace measured at iteration 0.
 *
 * Strategy:
 *  - Load subject classes with a FRESH URLClassLoader whose parent is the
 *    platform loader (subject classes are also on the system classpath; a
 *    platform parent guarantees fresh static state, i.e. a fresh seeded RNG,
 *    per run).
 *  - Find the Simulator class by reflection: declares a no-arg
 *    simulateOneStep() and an (int,int) constructor. Construct a 60x70 field.
 *  - After construction (reset+populate) and after each of 60 steps, count
 *    grid occupants per class simple name -> one trace line per step.
 *  - Run TWICE per subject: traces must be identical (determinism regression
 *    check), then must match the embedded golden trace.
 *
 * NOTE: the Simulator is inseparable from its Swing view at the baseline
 * (view created in the constructor, used inside simulateOneStep), so this
 * unit runs NON-headless (unit.conf JAVA_FLAGS); windows are disposed after
 * each run and the driver exits via System.exit.
 *
 * Set env GOLDEN_DUMP=1 to print the measured trace (used once at iteration 0
 * to record the golden trace).
 */
public class Driver {
    static final int DEPTH = 60, WIDTH = 70, STEPS = 60;

    public static void main(String[] args) {
        int code = 0;
        try {
            Path classes = Paths.get(args[0]).resolve("_classes");
            boolean dump = System.getenv("GOLDEN_DUMP") != null;

            String[] t1 = runTrace(classes);
            String[] t2 = runTrace(classes);

            if (dump) {
                for (String s : t1) System.out.println("GOLDEN\t" + s);
            }

            int nd = firstDiff(t1, t2);
            if (nd >= 0) {
                System.out.println("run1[" + nd + "]=" + t1[nd]);
                System.out.println("run2[" + nd + "]=" + t2[nd]);
                System.out.println("RESULT FAIL nondeterministic: two identical runs diverge at step " + nd);
            } else if (dump) {
                System.out.println("RESULT PASS (golden dump mode; deterministic over " + STEPS + " steps)");
            } else {
                int d = firstDiff(GOLDEN, t1);
                if (d >= 0) {
                    System.out.println("expected[" + d + "]=" + GOLDEN[d]);
                    System.out.println("actual  [" + d + "]=" + t1[d]);
                    System.out.println("RESULT FAIL trace diverges from golden at step " + d);
                } else {
                    System.out.println("RESULT PASS deterministic " + STEPS + "-step population trace matches golden");
                }
            }
        } catch (Throwable t) {
            Throwable root = t;
            while (root instanceof InvocationTargetException && root.getCause() != null) root = root.getCause();
            root.printStackTrace(System.out);
            System.out.println("RESULT FAIL uncaught exception: " + root);
            code = 0; // RESULT line already printed
        }
        disposeWindows();
        System.exit(code); // kill Swing EDT
    }

    /** One full simulation run under a fresh classloader; returns trace[0..STEPS]. */
    static String[] runTrace(Path classes) throws Exception {
        try (URLClassLoader cl = new URLClassLoader(
                new URL[]{classes.toUri().toURL()}, ClassLoader.getPlatformClassLoader())) {
            Class<?> simClass = findSimulatorClass(classes, cl);
            System.out.println("simulator class: " + simClass.getName());
            Constructor<?> ctor = simClass.getDeclaredConstructor(int.class, int.class);
            ctor.setAccessible(true);
            Object sim;
            Method stepM;
            String[] trace = new String[STEPS + 1];
            try {
                sim = ctor.newInstance(DEPTH, WIDTH);
                stepM = findStepMethod(simClass);
                stepM.setAccessible(true);
                Object field = findFieldObject(sim);
                trace[0] = snapshot(field);
                for (int i = 1; i <= STEPS; i++) {
                    stepM.invoke(sim);
                    trace[i] = snapshot(field);
                }
            } finally {
                disposeWindows();
            }
            return trace;
        }
    }

    /** Class declaring a no-arg simulateOneStep-like method + (int,int) ctor, not a Window. */
    static Class<?> findSimulatorClass(Path classes, ClassLoader cl) throws Exception {
        List<String> names;
        try (Stream<Path> s = Files.walk(classes)) {
            names = s.filter(p -> p.toString().endsWith(".class"))
                    .map(p -> classes.relativize(p).toString()
                            .replace(".class", "").replace(java.io.File.separatorChar, '.'))
                    .filter(n -> !n.contains("$"))
                    .sorted().collect(Collectors.toList());
        }
        Class<?> fallback = null;
        for (String n : names) {
            Class<?> c;
            try { c = Class.forName(n, false, cl); } catch (Throwable t) { continue; }
            if (java.awt.Window.class.isAssignableFrom(c)) continue;
            boolean hasCtor = false;
            try { c.getDeclaredConstructor(int.class, int.class); hasCtor = true; } catch (NoSuchMethodException e) {}
            for (Method m : c.getDeclaredMethods()) {
                if (m.getParameterCount() != 0 || m.getReturnType() != void.class) continue;
                String mn = m.getName().toLowerCase(Locale.ROOT);
                if (mn.equals("simulateonestep")) return c;
                if (hasCtor && mn.contains("step") && !mn.startsWith("get") && fallback == null) fallback = c;
            }
        }
        if (fallback != null) return fallback;
        throw new IllegalStateException("no Simulator class (simulateOneStep) found among " + names);
    }

    static Method findStepMethod(Class<?> c) throws Exception {
        for (Method m : c.getDeclaredMethods())
            if (m.getParameterCount() == 0 && m.getName().equalsIgnoreCase("simulateOneStep")) return m;
        for (Method m : c.getDeclaredMethods()) {
            String mn = m.getName().toLowerCase(Locale.ROOT);
            if (m.getParameterCount() == 0 && m.getReturnType() == void.class
                    && mn.contains("step") && !mn.startsWith("get")) return m;
        }
        throw new NoSuchMethodException("simulateOneStep on " + c);
    }

    /** Locate the grid object (has getDepth/getWidth/getObjectAt(int,int)) inside the simulator. */
    static Object findFieldObject(Object sim) throws Exception {
        for (Class<?> k = sim.getClass(); k != null; k = k.getSuperclass()) {
            for (java.lang.reflect.Field f : k.getDeclaredFields()) {
                if (f.getType().isPrimitive()) continue;
                Object v = get(f, sim);
                if (v == null || isJdk(v.getClass())) continue;
                if (isGrid(v.getClass())) return v;
            }
        }
        // one level deep (in case the grid got wrapped in a component)
        for (Class<?> k = sim.getClass(); k != null; k = k.getSuperclass()) {
            for (java.lang.reflect.Field f : k.getDeclaredFields()) {
                if (f.getType().isPrimitive()) continue;
                Object v = get(f, sim);
                if (v == null || isJdk(v.getClass()) || java.awt.Component.class.isInstance(v)) continue;
                for (Class<?> k2 = v.getClass(); k2 != null && !isJdk(k2); k2 = k2.getSuperclass()) {
                    for (java.lang.reflect.Field f2 : k2.getDeclaredFields()) {
                        if (f2.getType().isPrimitive()) continue;
                        Object v2 = get(f2, v);
                        if (v2 != null && !isJdk(v2.getClass()) && isGrid(v2.getClass())) return v2;
                    }
                }
            }
        }
        throw new IllegalStateException("no Field-like object found in " + sim.getClass());
    }

    static Object get(java.lang.reflect.Field f, Object o) {
        try { f.setAccessible(true); return f.get(o); }
        catch (Throwable t) { return null; }
    }

    static boolean isJdk(Class<?> c) {
        String n = c.getName();
        return n.startsWith("java.") || n.startsWith("javax.") || n.startsWith("jdk.")
                || n.startsWith("sun.") || n.startsWith("com.sun.");
    }

    static boolean isGrid(Class<?> c) {
        try {
            c.getMethod("getDepth"); c.getMethod("getWidth");
            c.getMethod("getObjectAt", int.class, int.class);
            return true;
        } catch (NoSuchMethodException e) { return false; }
    }

    /** Per-class occupant counts over the whole grid, canonical string. */
    static String snapshot(Object field) throws Exception {
        Method gd = field.getClass().getMethod("getDepth");
        Method gw = field.getClass().getMethod("getWidth");
        Method go = field.getClass().getMethod("getObjectAt", int.class, int.class);
        int depth = (Integer) gd.invoke(field), width = (Integer) gw.invoke(field);
        TreeMap<String, Integer> counts = new TreeMap<>();
        for (int r = 0; r < depth; r++)
            for (int c = 0; c < width; c++) {
                Object o = go.invoke(field, r, c);
                if (o != null) counts.merge(o.getClass().getSimpleName(), 1, Integer::sum);
            }
        return counts.entrySet().stream().map(e -> e.getKey() + "=" + e.getValue())
                .collect(Collectors.joining(","));
    }

    static int firstDiff(String[] a, String[] b) {
        for (int i = 0; i < Math.min(a.length, b.length); i++)
            if (!Objects.equals(a[i], b[i])) return i;
        return a.length == b.length ? -1 : Math.min(a.length, b.length);
    }

    static void disposeWindows() {
        try {
            for (java.awt.Window w : java.awt.Window.getWindows()) w.dispose();
        } catch (Throwable t) { /* ignore */ }
    }

    // Golden per-step trace measured at iteration 0 (see GOLDEN_DUMP).
    static final String[] GOLDEN = {
        "Cheetah=115,Gazelle=795,Grass=1774,Hunter=4,Jaguar=40,Lion=64,Zebra=697",
        "Cheetah=104,Gazelle=1001,Grass=1497,Hunter=4,Jaguar=38,Lion=59,Zebra=653",
        "Cheetah=102,Gazelle=1001,Grass=1424,Hunter=4,Jaguar=36,Lion=59,Zebra=653",
        "Cheetah=101,Gazelle=1001,Grass=1424,Hunter=4,Jaguar=36,Lion=59,Zebra=653",
        "Cheetah=99,Gazelle=978,Grass=907,Hunter=4,Jaguar=37,Lion=58,Zebra=619",
        "Cheetah=93,Gazelle=1049,Grass=479,Hunter=4,Jaguar=38,Lion=57,Zebra=607",
        "Cheetah=93,Gazelle=1048,Grass=443,Hunter=4,Jaguar=38,Lion=55,Zebra=606",
        "Cheetah=93,Gazelle=1048,Grass=443,Hunter=4,Jaguar=38,Lion=55,Zebra=606",
        "Cheetah=91,Gazelle=1139,Grass=398,Hunter=4,Jaguar=39,Lion=58,Zebra=583",
        "Cheetah=89,Gazelle=1212,Grass=216,Hunter=4,Jaguar=37,Lion=58,Zebra=570",
        "Cheetah=89,Gazelle=1212,Grass=216,Hunter=4,Jaguar=37,Lion=58,Zebra=570",
        "Cheetah=89,Gazelle=1212,Grass=216,Hunter=4,Jaguar=37,Lion=58,Zebra=570",
        "Cheetah=89,Gazelle=1292,Grass=190,Hunter=4,Jaguar=42,Lion=58,Zebra=561",
        "Cheetah=89,Gazelle=1329,Grass=123,Hunter=4,Jaguar=42,Lion=59,Zebra=549",
        "Cheetah=89,Gazelle=1329,Grass=123,Hunter=4,Jaguar=42,Lion=59,Zebra=549",
        "Cheetah=89,Gazelle=1329,Grass=123,Hunter=4,Jaguar=42,Lion=59,Zebra=549",
        "Cheetah=84,Gazelle=1458,Grass=182,Hunter=4,Jaguar=44,Lion=59,Zebra=563",
        "Cheetah=85,Gazelle=1494,Grass=132,Hunter=4,Jaguar=44,Lion=59,Zebra=549",
        "Cheetah=85,Gazelle=1494,Grass=132,Hunter=4,Jaguar=44,Lion=59,Zebra=549",
        "Cheetah=85,Gazelle=1494,Grass=132,Hunter=4,Jaguar=44,Lion=59,Zebra=549",
        "Cheetah=86,Gazelle=1671,Grass=119,Hunter=4,Jaguar=43,Lion=59,Zebra=556",
        "Cheetah=84,Gazelle=1637,Grass=85,Hunter=4,Jaguar=43,Lion=59,Zebra=538",
        "Cheetah=84,Gazelle=1637,Grass=80,Hunter=4,Jaguar=43,Lion=59,Zebra=538",
        "Cheetah=84,Gazelle=1637,Grass=80,Hunter=4,Jaguar=43,Lion=59,Zebra=538",
        "Cheetah=82,Gazelle=1826,Grass=115,Hunter=4,Jaguar=43,Lion=59,Zebra=550",
        "Cheetah=81,Gazelle=1789,Grass=94,Hunter=4,Jaguar=44,Lion=59,Zebra=539",
        "Cheetah=81,Gazelle=1789,Grass=87,Hunter=4,Jaguar=44,Lion=58,Zebra=539",
        "Cheetah=81,Gazelle=1789,Grass=87,Hunter=4,Jaguar=44,Lion=58,Zebra=539",
        "Cheetah=84,Gazelle=1935,Grass=76,Hunter=4,Jaguar=43,Lion=59,Zebra=542",
        "Cheetah=82,Gazelle=1823,Grass=58,Hunter=4,Jaguar=43,Lion=59,Zebra=534",
        "Cheetah=81,Gazelle=1823,Grass=58,Hunter=4,Jaguar=43,Lion=58,Zebra=534",
        "Cheetah=81,Gazelle=1823,Grass=58,Hunter=4,Jaguar=43,Lion=58,Zebra=534",
        "Cheetah=80,Gazelle=2013,Grass=88,Hunter=4,Jaguar=43,Lion=58,Zebra=529",
        "Cheetah=84,Gazelle=1901,Grass=68,Hunter=4,Jaguar=43,Lion=58,Zebra=516",
        "Cheetah=84,Gazelle=1901,Grass=66,Hunter=4,Jaguar=43,Lion=57,Zebra=516",
        "Cheetah=84,Gazelle=1901,Grass=66,Hunter=4,Jaguar=43,Lion=57,Zebra=516",
        "Cheetah=83,Gazelle=2013,Grass=54,Hunter=4,Jaguar=42,Lion=60,Zebra=513",
        "Cheetah=83,Gazelle=1941,Grass=46,Hunter=4,Jaguar=42,Lion=61,Zebra=503",
        "Cheetah=83,Gazelle=1941,Grass=45,Hunter=4,Jaguar=42,Lion=61,Zebra=503",
        "Cheetah=83,Gazelle=1941,Grass=45,Hunter=4,Jaguar=42,Lion=60,Zebra=503",
        "Cheetah=86,Gazelle=2065,Grass=70,Hunter=4,Jaguar=44,Lion=60,Zebra=507",
        "Cheetah=86,Gazelle=1938,Grass=55,Hunter=4,Jaguar=44,Lion=60,Zebra=497",
        "Cheetah=86,Gazelle=1938,Grass=53,Hunter=4,Jaguar=44,Lion=60,Zebra=497",
        "Cheetah=86,Gazelle=1938,Grass=53,Hunter=4,Jaguar=44,Lion=60,Zebra=497",
        "Cheetah=83,Gazelle=2078,Grass=50,Hunter=4,Jaguar=44,Lion=60,Zebra=485",
        "Cheetah=80,Gazelle=2012,Grass=39,Hunter=4,Jaguar=43,Lion=62,Zebra=470",
        "Cheetah=80,Gazelle=2012,Grass=39,Hunter=4,Jaguar=43,Lion=62,Zebra=470",
        "Cheetah=80,Gazelle=2012,Grass=39,Hunter=4,Jaguar=43,Lion=62,Zebra=470",
        "Cheetah=81,Gazelle=2148,Grass=57,Hunter=4,Jaguar=44,Lion=62,Zebra=460",
        "Cheetah=79,Gazelle=2024,Grass=50,Hunter=4,Jaguar=44,Lion=62,Zebra=447",
        "Cheetah=79,Gazelle=2024,Grass=49,Hunter=4,Jaguar=43,Lion=61,Zebra=447",
        "Cheetah=79,Gazelle=2024,Grass=49,Hunter=4,Jaguar=43,Lion=60,Zebra=447",
        "Cheetah=78,Gazelle=2130,Grass=46,Hunter=4,Jaguar=44,Lion=59,Zebra=430",
        "Cheetah=83,Gazelle=2052,Grass=38,Hunter=4,Jaguar=44,Lion=60,Zebra=408",
        "Cheetah=83,Gazelle=2052,Grass=38,Hunter=4,Jaguar=44,Lion=60,Zebra=408",
        "Cheetah=83,Gazelle=2052,Grass=38,Hunter=4,Jaguar=44,Lion=60,Zebra=408",
        "Cheetah=89,Gazelle=2217,Grass=51,Hunter=4,Jaguar=44,Lion=59,Zebra=405",
        "Cheetah=87,Gazelle=2073,Grass=43,Hunter=4,Jaguar=44,Lion=60,Zebra=390",
        "Cheetah=87,Gazelle=2073,Grass=42,Hunter=4,Jaguar=44,Lion=60,Zebra=390",
        "Cheetah=87,Gazelle=2073,Grass=42,Hunter=4,Jaguar=44,Lion=60,Zebra=390",
        "Cheetah=86,Gazelle=2159,Grass=48,Hunter=4,Jaguar=44,Lion=60,Zebra=360"
    };
}
