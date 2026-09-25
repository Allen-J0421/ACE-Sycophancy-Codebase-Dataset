import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

/**
 * Correctness driver for R006_module_java (tundra predator-prey simulation:
 * Bear/CarnivoreFox/Wolverine + Reindeer/Sheep on an animal layer,
 * Grass/Sage/Sedge on a separate plant "terrain" layer, plus weather,
 * day/night clock and a block-based disease model).
 *
 * The subject is DETERMINISTIC: every source of randomness (Field shuffles,
 * PopulationGenerator, Animal/Plant ages, breeding, genders via Utils, weather,
 * disease) flows through the shared Randomizer (fixed seed 1111,
 * useShared=true). Oracle = golden per-step trace measured at iteration 0.
 *
 * Strategy:
 *  - Load subject classes with a FRESH URLClassLoader whose parent is the
 *    platform loader (fresh static state per run: seeded RNG, the static
 *    DiseaseHandler.count map).
 *  - Find the Simulator class by reflection (declares no-arg simulateOneStep()
 *    and an (int,int) constructor); construct the baseline default 80x120 field
 *    (the constructor populates it via reset()).
 *  - After construction and after each of STEPS steps record one trace line:
 *      animal-layer counts per class, plant-layer counts per class, number of
 *      infected animals (non-null getInfectionTimestamp()), and a 64-bit hash of
 *      the full two-layer layout (row, col, class of every occupant).
 *  - Run TWICE per subject: traces must be identical (determinism regression
 *    check), then must match the embedded golden trace.
 *
 * NOTE: the JavaFX Dashboard is replaced by a no-op stub (stubs/, STUB_OVERRIDE)
 * because javafx is not installed; the Dashboard is only built by a GUI button.
 * The Simulator is inseparable from its Swing view, so this unit runs
 * NON-headless; windows are disposed after each run and the driver exits via
 * System.exit.
 *
 * Set env GOLDEN_DUMP=1 to print the measured trace (used once at iteration 0
 * to record the golden trace).
 */
public class Driver {
    static final int DEPTH = 80, WIDTH = 120, STEPS = 100;

    public static void main(String[] args) {
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
                    System.out.println("RESULT PASS deterministic " + STEPS
                            + "-step two-layer population/infection/layout trace matches golden");
                }
            }
        } catch (Throwable t) {
            Throwable root = t;
            while (root instanceof InvocationTargetException && root.getCause() != null) root = root.getCause();
            root.printStackTrace(System.out);
            System.out.println("RESULT FAIL uncaught exception: " + root);
        }
        disposeWindows();
        System.exit(0); // kill Swing EDT
    }

    /** One full simulation run under a fresh classloader; returns trace[0..STEPS]. */
    static String[] runTrace(Path classes) throws Exception {
        try (URLClassLoader cl = new URLClassLoader(
                new URL[]{classes.toUri().toURL()}, ClassLoader.getPlatformClassLoader())) {
            Class<?> simClass = findSimulatorClass(classes, cl);
            System.out.println("simulator class: " + simClass.getName());
            Constructor<?> ctor = simClass.getDeclaredConstructor(int.class, int.class);
            ctor.setAccessible(true);
            String[] trace = new String[STEPS + 1];
            try {
                Object sim = ctor.newInstance(DEPTH, WIDTH);
                Method stepM = findStepMethod(simClass);
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
                if (m.isSynthetic() || m.isBridge()) continue;
                if (m.getParameterCount() != 0 || m.getReturnType() != void.class) continue;
                String mn = m.getName().toLowerCase(Locale.ROOT);
                if (hasCtor && mn.equals("simulateonestep")) return c;
                if (hasCtor && mn.contains("step") && !mn.startsWith("get") && fallback == null) fallback = c;
            }
        }
        if (fallback != null) return fallback;
        throw new IllegalStateException("no Simulator class (simulateOneStep) found among " + names);
    }

    static Method findStepMethod(Class<?> c) throws Exception {
        for (Method m : c.getDeclaredMethods())
            if (!m.isSynthetic() && m.getParameterCount() == 0 && m.getName().equalsIgnoreCase("simulateOneStep")) return m;
        for (Method m : c.getDeclaredMethods()) {
            String mn = m.getName().toLowerCase(Locale.ROOT);
            if (!m.isSynthetic() && m.getParameterCount() == 0 && m.getReturnType() == void.class
                    && mn.contains("step") && !mn.startsWith("get")) return m;
        }
        throw new NoSuchMethodException("simulateOneStep on " + c);
    }

    /** Locate the grid object (getDepth/getWidth/getObjectAt(int,int)/getPlantAt(int,int)) inside the simulator. */
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
        throw new IllegalStateException("no two-layer Field-like object found in " + sim.getClass());
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
            c.getMethod("getPlantAt", int.class, int.class);
            return true;
        } catch (NoSuchMethodException e) { return false; }
    }

    /**
     * Canonical per-step state: animal-layer and plant-layer class counts,
     * infected-animal count, and an FNV-1a hash of the full two-layer layout.
     */
    static String snapshot(Object field) throws Exception {
        Method gd = field.getClass().getMethod("getDepth");
        Method gw = field.getClass().getMethod("getWidth");
        Method go = field.getClass().getMethod("getObjectAt", int.class, int.class);
        Method gp = field.getClass().getMethod("getPlantAt", int.class, int.class);
        int depth = (Integer) gd.invoke(field), width = (Integer) gw.invoke(field);
        TreeMap<String, Integer> animals = new TreeMap<>(), plants = new TreeMap<>();
        int infected = 0;
        long h = 0xcbf29ce484222325L;
        for (int r = 0; r < depth; r++)
            for (int c = 0; c < width; c++) {
                Object a = go.invoke(field, r, c);
                Object p = gp.invoke(field, r, c);
                String an = a == null ? "-" : a.getClass().getSimpleName();
                String pn = p == null ? "-" : p.getClass().getSimpleName();
                if (a != null) {
                    animals.merge(an, 1, Integer::sum);
                    if (infectionOf(a) != null) infected++;
                }
                if (p != null) plants.merge(pn, 1, Integer::sum);
                if (a != null || p != null) {
                    String cell = r + "," + c + ":" + an + "/" + pn + ";";
                    for (int i = 0; i < cell.length(); i++) {
                        h ^= cell.charAt(i);
                        h *= 0x100000001b3L;
                    }
                }
            }
        return "A{" + join(animals) + "} P{" + join(plants) + "} inf=" + infected
                + " h=" + Long.toHexString(h);
    }

    static final Map<Class<?>, Method> INF = new HashMap<>();

    static Object infectionOf(Object animal) throws Exception {
        Method m = INF.computeIfAbsent(animal.getClass(), k -> {
            for (Class<?> c = k; c != null; c = c.getSuperclass())
                for (Method mm : c.getDeclaredMethods())
                    if (!mm.isSynthetic() && mm.getParameterCount() == 0
                            && mm.getName().equals("getInfectionTimestamp")) {
                        mm.setAccessible(true);
                        return mm;
                    }
            throw new IllegalStateException("no getInfectionTimestamp() on " + k);
        });
        return m.invoke(animal);
    }

    static String join(Map<String, Integer> m) {
        return m.entrySet().stream().map(e -> e.getKey() + "=" + e.getValue())
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
        "A{Bear=261,CarnivoreFox=853,Reindeer=841,Sheep=969,Wolverine=584} P{Grass=922,Sage=641,Sedge=553} inf=11 h=331f7e3a1605bc72",
        "A{Bear=224,CarnivoreFox=706,Reindeer=473,Sheep=407,Wolverine=487} P{Grass=3244,Sage=1845,Sedge=1575} inf=13 h=c85ca0f8567a2033",
        "A{Bear=218,CarnivoreFox=635,Reindeer=388,Sheep=290,Wolverine=432} P{Grass=4244,Sage=2367,Sedge=2074} inf=12 h=39665f0cf48591f3",
        "A{Bear=214,CarnivoreFox=587,Reindeer=296,Sheep=230,Wolverine=420} P{Grass=4397,Sage=2451,Sedge=2138} inf=17 h=695734adf736e86c",
        "A{Bear=201,CarnivoreFox=541,Reindeer=355,Sheep=307,Wolverine=446} P{Grass=4078,Sage=2309,Sedge=2003} inf=28 h=604bca021622c9a6",
        "A{Bear=188,CarnivoreFox=518,Reindeer=484,Sheep=372,Wolverine=443} P{Grass=3818,Sage=2168,Sedge=1865} inf=39 h=b9a78d035c67c17e",
        "A{Bear=205,CarnivoreFox=555,Reindeer=463,Sheep=376,Wolverine=396} P{Grass=3874,Sage=2150,Sedge=1856} inf=45 h=31878977200f377d",
        "A{Bear=213,CarnivoreFox=537,Reindeer=467,Sheep=418,Wolverine=370} P{Grass=3914,Sage=2100,Sedge=1860} inf=63 h=c1164eba10d9a976",
        "A{Bear=215,CarnivoreFox=510,Reindeer=481,Sheep=430,Wolverine=346} P{Grass=3895,Sage=2047,Sedge=1823} inf=73 h=e62a89c353b29ff3",
        "A{Bear=209,CarnivoreFox=491,Reindeer=535,Sheep=452,Wolverine=338} P{Grass=3862,Sage=2005,Sedge=1799} inf=77 h=bd77797a42e0865d",
        "A{Bear=205,CarnivoreFox=478,Reindeer=598,Sheep=529,Wolverine=311} P{Grass=3820,Sage=1961,Sedge=1770} inf=89 h=8a54b59de43c8b3e",
        "A{Bear=195,CarnivoreFox=449,Reindeer=673,Sheep=586,Wolverine=278} P{Grass=3745,Sage=1900,Sedge=1727} inf=104 h=752fc6efc4f37366",
        "A{Bear=184,CarnivoreFox=434,Reindeer=875,Sheep=784,Wolverine=291} P{Grass=3467,Sage=1775,Sedge=1606} inf=120 h=303cba2d2c0b1de8",
        "A{Bear=175,CarnivoreFox=416,Reindeer=1078,Sheep=994,Wolverine=295} P{Grass=3169,Sage=1626,Sedge=1479} inf=134 h=318543fbce0260ce",
        "A{Bear=186,CarnivoreFox=418,Reindeer=1109,Sheep=1053,Wolverine=261} P{Grass=3165,Sage=1624,Sedge=1492} inf=132 h=7ac8b39449cebb79",
        "A{Bear=178,CarnivoreFox=386,Reindeer=1208,Sheep=1177,Wolverine=253} P{Grass=3033,Sage=1560,Sedge=1432} inf=150 h=a472f8d207496324",
        "A{Bear=177,CarnivoreFox=361,Reindeer=1329,Sheep=1344,Wolverine=241} P{Grass=2896,Sage=1463,Sedge=1362} inf=148 h=507f27bcbf5771ba",
        "A{Bear=172,CarnivoreFox=349,Reindeer=1482,Sheep=1529,Wolverine=225} P{Grass=2740,Sage=1329,Sedge=1257} inf=159 h=50fa101ff5a74ef4",
        "A{Bear=162,CarnivoreFox=320,Reindeer=1659,Sheep=1687,Wolverine=195} P{Grass=2530,Sage=1222,Sedge=1114} inf=165 h=312ce8f0dbac1212",
        "A{Bear=169,CarnivoreFox=299,Reindeer=1753,Sheep=1873,Wolverine=166} P{Grass=2330,Sage=1122,Sedge=999} inf=162 h=b47a742904e42b5f",
        "A{Bear=159,CarnivoreFox=266,Reindeer=2026,Sheep=2249,Wolverine=187} P{Grass=1925,Sage=966,Sedge=825} inf=190 h=d34b96b816ee9df4",
        "A{Bear=148,CarnivoreFox=244,Reindeer=2210,Sheep=2553,Wolverine=185} P{Grass=1617,Sage=834,Sedge=676} inf=202 h=b8151141d84586a2",
        "A{Bear=159,CarnivoreFox=282,Reindeer=2225,Sheep=2598,Wolverine=165} P{Grass=1485,Sage=748,Sedge=596} inf=215 h=d7b8af8d5c04794c",
        "A{Bear=160,CarnivoreFox=287,Reindeer=2291,Sheep=2741,Wolverine=157} P{Grass=1323,Sage=647,Sedge=515} inf=215 h=8e3ec3b7fa5e64ae",
        "A{Bear=160,CarnivoreFox=311,Reindeer=2359,Sheep=2908,Wolverine=150} P{Grass=1159,Sage=563,Sedge=443} inf=213 h=4b3bff07961a6abd",
        "A{Bear=163,CarnivoreFox=296,Reindeer=2411,Sheep=3035,Wolverine=152} P{Grass=1009,Sage=485,Sedge=387} inf=230 h=9d3876e98f015989",
        "A{Bear=162,CarnivoreFox=306,Reindeer=2483,Sheep=3182,Wolverine=145} P{Grass=883,Sage=395,Sedge=307} inf=243 h=c10f0d8ca51e2cba",
        "A{Bear=172,CarnivoreFox=287,Reindeer=2483,Sheep=3283,Wolverine=166} P{Grass=768,Sage=317,Sedge=245} inf=261 h=dda469801e23cb3b",
        "A{Bear=167,CarnivoreFox=282,Reindeer=2651,Sheep=3460,Wolverine=171} P{Grass=634,Sage=243,Sedge=199} inf=325 h=1d2ba81c03d67231",
        "A{Bear=162,CarnivoreFox=272,Reindeer=2727,Sheep=3612,Wolverine=179} P{Grass=501,Sage=196,Sedge=164} inf=357 h=42a18b59f74647ab",
        "A{Bear=173,CarnivoreFox=303,Reindeer=2669,Sheep=3595,Wolverine=182} P{Grass=442,Sage=120,Sedge=101} inf=432 h=57828503c33805a0",
        "A{Bear=169,CarnivoreFox=305,Reindeer=2666,Sheep=3664,Wolverine=182} P{Grass=386,Sage=73,Sedge=69} inf=478 h=3cc2f5c0562eb24c",
        "A{Bear=175,CarnivoreFox=322,Reindeer=2655,Sheep=3674,Wolverine=180} P{Grass=331,Sage=63,Sedge=62} inf=506 h=f5397a6e207c4624",
        "A{Bear=186,CarnivoreFox=328,Reindeer=2643,Sheep=3629,Wolverine=179} P{Grass=272,Sage=57,Sedge=56} inf=546 h=a4e2f9f837449856",
        "A{Bear=183,CarnivoreFox=327,Reindeer=2648,Sheep=3664,Wolverine=184} P{Grass=230,Sage=47,Sedge=42} inf=572 h=c5adb31433108094",
        "A{Bear=184,CarnivoreFox=349,Reindeer=2633,Sheep=3722,Wolverine=177} P{Grass=182,Sage=40,Sedge=32} inf=603 h=3d829cf26f7d5b00",
        "A{Bear=183,CarnivoreFox=348,Reindeer=2696,Sheep=3854,Wolverine=185} P{Grass=151,Sage=25,Sedge=15} inf=683 h=9ce861902e92e3f3",
        "A{Bear=183,CarnivoreFox=347,Reindeer=2750,Sheep=3862,Wolverine=191} P{Grass=121,Sage=11,Sedge=7} inf=767 h=3cf1eb31e9143cce",
        "A{Bear=197,CarnivoreFox=353,Reindeer=2715,Sheep=3862,Wolverine=183} P{Grass=121,Sage=9,Sedge=5} inf=776 h=dd5ddd0b7eca9f41",
        "A{Bear=195,CarnivoreFox=361,Reindeer=2688,Sheep=3795,Wolverine=184} P{Grass=130,Sage=9,Sedge=5} inf=801 h=3a9ee910ffb7e7a7",
        "A{Bear=203,CarnivoreFox=368,Reindeer=2624,Sheep=3689,Wolverine=187} P{Grass=137,Sage=8,Sedge=4} inf=844 h=dd03ca4f77709b36",
        "A{Bear=195,CarnivoreFox=371,Reindeer=2567,Sheep=3757,Wolverine=185} P{Grass=151,Sage=8,Sedge=3} inf=884 h=506265b9b8b9e8f",
        "A{Bear=196,CarnivoreFox=384,Reindeer=2621,Sheep=3699,Wolverine=183} P{Grass=166,Sage=8,Sedge=1} inf=879 h=727697950274f761",
        "A{Bear=198,CarnivoreFox=388,Reindeer=2550,Sheep=3602,Wolverine=182} P{Grass=179,Sage=7} inf=948 h=5ecb1d508917f9b3",
        "A{Bear=198,CarnivoreFox=385,Reindeer=2688,Sheep=3688,Wolverine=180} P{Grass=162,Sage=6} inf=1057 h=a05c1d3b896d697f",
        "A{Bear=198,CarnivoreFox=383,Reindeer=2787,Sheep=3727,Wolverine=183} P{Grass=150,Sage=5} inf=1113 h=8efcfaef7f0976fe",
        "A{Bear=199,CarnivoreFox=396,Reindeer=2752,Sheep=3641,Wolverine=176} P{Grass=178,Sage=5} inf=1140 h=8c1affb58f4b4c1",
        "A{Bear=200,CarnivoreFox=413,Reindeer=2729,Sheep=3576,Wolverine=173} P{Grass=202,Sage=5} inf=1152 h=d6f54adf5cd612cd",
        "A{Bear=201,CarnivoreFox=418,Reindeer=2657,Sheep=3541,Wolverine=173} P{Grass=230,Sage=5} inf=1171 h=3ef0ccd2e9f52352",
        "A{Bear=204,CarnivoreFox=421,Reindeer=2632,Sheep=3477,Wolverine=171} P{Grass=258,Sage=5} inf=1227 h=1cf74849bdb0370",
        "A{Bear=206,CarnivoreFox=426,Reindeer=2650,Sheep=3401,Wolverine=165} P{Grass=285,Sage=4} inf=1216 h=d7e9033153336de9",
        "A{Bear=201,CarnivoreFox=420,Reindeer=2672,Sheep=3389,Wolverine=160} P{Grass=301,Sage=4} inf=1211 h=6626d49e6044bf50",
        "A{Bear=201,CarnivoreFox=418,Reindeer=2809,Sheep=3516,Wolverine=159} P{Grass=254,Sage=4} inf=1309 h=e1322eebe2a45a8f",
        "A{Bear=201,CarnivoreFox=417,Reindeer=2869,Sheep=3548,Wolverine=156} P{Grass=216,Sage=4} inf=1390 h=807241d681694608",
        "A{Bear=202,CarnivoreFox=423,Reindeer=2839,Sheep=3459,Wolverine=154} P{Grass=200,Sage=4} inf=1356 h=5837d5321295b86e",
        "A{Bear=204,CarnivoreFox=435,Reindeer=2837,Sheep=3396,Wolverine=151} P{Grass=184,Sage=4} inf=1384 h=a977d249f71edb5e",
        "A{Bear=206,CarnivoreFox=437,Reindeer=2853,Sheep=3326,Wolverine=148} P{Grass=169,Sage=4} inf=1412 h=ede2313c0fb1f5ce",
        "A{Bear=207,CarnivoreFox=440,Reindeer=2844,Sheep=3283,Wolverine=148} P{Grass=157,Sage=4} inf=1421 h=d67e8e1a315908c4",
        "A{Bear=206,CarnivoreFox=449,Reindeer=2813,Sheep=3228,Wolverine=148} P{Grass=145,Sage=3} inf=1430 h=60f391204f35a27c",
        "A{Bear=201,CarnivoreFox=450,Reindeer=2829,Sheep=3173,Wolverine=143} P{Grass=136,Sage=3} inf=1482 h=5618dafbe2f5abd4",
        "A{Bear=201,CarnivoreFox=447,Reindeer=2915,Sheep=3347,Wolverine=142} P{Grass=114,Sage=3} inf=1597 h=4b9ea7f69b57aa90",
        "A{Bear=201,CarnivoreFox=439,Reindeer=2998,Sheep=3388,Wolverine=143} P{Grass=91,Sage=3} inf=1681 h=bdb45f96c0b5feb1",
        "A{Bear=202,CarnivoreFox=452,Reindeer=2925,Sheep=3270,Wolverine=145} P{Grass=97,Sage=3} inf=1740 h=3aba1d89fcc30fcf",
        "A{Bear=202,CarnivoreFox=453,Reindeer=2941,Sheep=3214,Wolverine=145} P{Grass=106,Sage=3} inf=1694 h=a969fb5da47ea778",
        "A{Bear=205,CarnivoreFox=455,Reindeer=2870,Sheep=3152,Wolverine=137} P{Grass=110,Sage=3} inf=1743 h=164537d980d5f839",
        "A{Bear=209,CarnivoreFox=457,Reindeer=2808,Sheep=3094,Wolverine=134} P{Grass=115,Sage=2} inf=1799 h=f4ae0af0546cb771",
        "A{Bear=208,CarnivoreFox=464,Reindeer=2762,Sheep=3050,Wolverine=133} P{Grass=123,Sage=2} inf=1846 h=c44c425d3b0099f8",
        "A{Bear=211,CarnivoreFox=468,Reindeer=2796,Sheep=3008,Wolverine=131} P{Grass=138,Sage=2} inf=1794 h=f724a8e6f23b09cf",
        "A{Bear=210,CarnivoreFox=464,Reindeer=2920,Sheep=3101,Wolverine=136} P{Grass=117,Sage=2} inf=1877 h=c030972c80b01ac3",
        "A{Bear=209,CarnivoreFox=454,Reindeer=2991,Sheep=3154,Wolverine=142} P{Grass=94,Sage=2} inf=2001 h=76dd60ba804eb697",
        "A{Bear=203,CarnivoreFox=487,Reindeer=2925,Sheep=3102,Wolverine=144} P{Grass=98,Sage=2} inf=2059 h=2c4998d99bf06ee9",
        "A{Bear=197,CarnivoreFox=504,Reindeer=2960,Sheep=3020,Wolverine=140} P{Grass=107,Sage=2} inf=1989 h=2442f58994976da1",
        "A{Bear=193,CarnivoreFox=510,Reindeer=2903,Sheep=2901,Wolverine=140} P{Grass=115,Sage=2} inf=1993 h=ab27962df1eb3497",
        "A{Bear=200,CarnivoreFox=534,Reindeer=2826,Sheep=2828,Wolverine=141} P{Grass=126,Sage=2} inf=2049 h=fdb742101fcd5f6b",
        "A{Bear=200,CarnivoreFox=551,Reindeer=2791,Sheep=2778,Wolverine=139} P{Grass=141,Sage=2} inf=2104 h=4f3c2899e3f99ac0",
        "A{Bear=197,CarnivoreFox=571,Reindeer=2787,Sheep=2635,Wolverine=136} P{Grass=159,Sage=2} inf=2091 h=5478388c012fc966",
        "A{Bear=197,CarnivoreFox=571,Reindeer=2863,Sheep=2748,Wolverine=140} P{Grass=158,Sage=2} inf=2207 h=de10688100816e0",
        "A{Bear=197,CarnivoreFox=570,Reindeer=2946,Sheep=2790,Wolverine=142} P{Grass=155,Sage=2} inf=2245 h=56245a1fca5f8422",
        "A{Bear=197,CarnivoreFox=589,Reindeer=2930,Sheep=2623,Wolverine=140} P{Grass=193,Sage=2} inf=2230 h=194b830e9443e26c",
        "A{Bear=204,CarnivoreFox=613,Reindeer=2895,Sheep=2544,Wolverine=137} P{Grass=218,Sage=2} inf=2128 h=1e5f25492d0f9e8a",
        "A{Bear=201,CarnivoreFox=623,Reindeer=2853,Sheep=2466,Wolverine=133} P{Grass=245,Sage=2} inf=2133 h=45da3d4ddb0c91d3",
        "A{Bear=207,CarnivoreFox=636,Reindeer=2791,Sheep=2341,Wolverine=130} P{Grass=268,Sage=2} inf=2167 h=db2ac6cdeca3cecf",
        "A{Bear=210,CarnivoreFox=680,Reindeer=2766,Sheep=2275,Wolverine=125} P{Grass=296,Sage=2} inf=2178 h=c50f96f3d526663a",
        "A{Bear=215,CarnivoreFox=705,Reindeer=2730,Sheep=2233,Wolverine=124} P{Grass=330,Sage=2} inf=2094 h=82263d1f83b6e46",
        "A{Bear=215,CarnivoreFox=702,Reindeer=2807,Sheep=2298,Wolverine=126} P{Grass=310,Sage=2} inf=2231 h=d67f718a316be47e",
        "A{Bear=213,CarnivoreFox=699,Reindeer=2851,Sheep=2398,Wolverine=130} P{Grass=283,Sage=2} inf=2304 h=cb85c3c486908813",
        "A{Bear=226,CarnivoreFox=712,Reindeer=2827,Sheep=2284,Wolverine=127} P{Grass=284,Sage=2} inf=2317 h=3a1f76a4050b15bc",
        "A{Bear=237,CarnivoreFox=712,Reindeer=2780,Sheep=2184,Wolverine=124} P{Grass=291,Sage=2} inf=2251 h=7f141076c57e27f1",
        "A{Bear=250,CarnivoreFox=721,Reindeer=2727,Sheep=2083,Wolverine=121} P{Grass=285,Sage=2} inf=2243 h=d0524256d7632246",
        "A{Bear=260,CarnivoreFox=740,Reindeer=2638,Sheep=2023,Wolverine=117} P{Grass=290,Sage=1} inf=2240 h=38ad75f10ebc4039",
        "A{Bear=262,CarnivoreFox=771,Reindeer=2585,Sheep=1952,Wolverine=114} P{Grass=296} inf=2242 h=a2281802458f7633",
        "A{Bear=281,CarnivoreFox=809,Reindeer=2568,Sheep=1943,Wolverine=109} P{Grass=296} inf=2164 h=5592e3937f43eabc",
        "A{Bear=281,CarnivoreFox=807,Reindeer=2639,Sheep=2041,Wolverine=111} P{Grass=286} inf=2252 h=6e9d2f8f4182d7c2",
        "A{Bear=281,CarnivoreFox=802,Reindeer=2739,Sheep=2119,Wolverine=112} P{Grass=267} inf=2351 h=453b69ec35c2de1d",
        "A{Bear=287,CarnivoreFox=831,Reindeer=2620,Sheep=2024,Wolverine=115} P{Grass=280} inf=2321 h=e4b99bde40080e32",
        "A{Bear=296,CarnivoreFox=848,Reindeer=2565,Sheep=1952,Wolverine=120} P{Grass=300} inf=2206 h=bb6313492b0a661f",
        "A{Bear=302,CarnivoreFox=892,Reindeer=2498,Sheep=1835,Wolverine=125} P{Grass=329} inf=2192 h=181a60e1de3f7f0c",
        "A{Bear=314,CarnivoreFox=922,Reindeer=2400,Sheep=1765,Wolverine=128} P{Grass=351} inf=2182 h=31303ebf3a0d98a",
        "A{Bear=319,CarnivoreFox=944,Reindeer=2352,Sheep=1685,Wolverine=128} P{Grass=375} inf=2210 h=ffb3f48e9dc6714f",
        "A{Bear=334,CarnivoreFox=973,Reindeer=2341,Sheep=1617,Wolverine=128} P{Grass=393} inf=2168 h=d9abeaa3617e299f",
        "A{Bear=333,CarnivoreFox=966,Reindeer=2459,Sheep=1676,Wolverine=128} P{Grass=355} inf=2289 h=28caa79e1d6c7929"
    };
}
