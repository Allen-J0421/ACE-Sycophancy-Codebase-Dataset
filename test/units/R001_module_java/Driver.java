import java.lang.reflect.*;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

/**
 * Correctness driver for R001_module_java (predator-prey sim with climate and
 * disease: Bear/Wolf predators, Bird/Duck/Mouse prey, Grass/Flower plants).
 *
 * The subject is DETERMINISTIC: every random draw flows through the shared
 * Randomizer (fixed seed 1111, useShared=true) and the views consume no
 * randomness. Checked at the baseline by running it twice in-process under
 * fresh classloaders. Oracle = golden per-step state trace recorded at
 * iteration 0.
 *
 * Headless: the runner keeps -Djava.awt.headless=true. At iterations 0-6 the
 * Simulator constructor itself instantiates the Swing JFrame views
 * (SimulatorView/GraphView), which throw HeadlessException. The driver
 * therefore generates, at runtime, no-op replacements for every top-level
 * subject class that extends java.awt.Window (same name, package, public
 * constructors/methods and non-JDK interfaces, default-valued bodies), compiles
 * them with javax.tools, and puts them ahead of _classes in the subject
 * classloader. Only rendering is removed. The real model layer (Field,
 * animals, plants, climate, disease) and the real Simulator orchestration
 * (populate + simulateOneStep) run unchanged. From iteration 7 on the
 * Simulator talks to an EventPublisher, which gets a no-op proxy.
 *
 * Construction: the Simulator constructor with the most parameters ending in
 * (int depth, int width) is used. Other parameters are resolved by discovery:
 * interfaces get no-op proxies, other types come from a static factory in the
 * composition root (Main.standardSpecies(), Main.buildRegistry(
 * Main.loadConfig(logger), new SpeciesCatalog())) or a no-arg constructor.
 *
 * Trace line per step (after populate, then after each of STEPS steps):
 * per-species animal counts on the grid, sick animals, plant counts, sum of
 * plant growth stages, weather, season, humidity, day/night cycle and the size
 * of the simulator's live-animal list. Two runs must be identical
 * (determinism), then must equal GOLDEN. GOLDEN_DUMP=1 prints the trace.
 */
public class Driver {
    static final int DEPTH = 80, WIDTH = 120, STEPS = 100;

    public static void main(String[] args) {
        try {
            Path work = Paths.get(args[0]);
            Path classes = work.resolve("_classes");
            boolean dump = System.getenv("GOLDEN_DUMP") != null;

            Path stubs = buildViewStubs(work, classes);

            String[] t1 = runTrace(classes, stubs);
            String[] t2 = runTrace(classes, stubs);
            if (dump) for (String s : t1) System.out.println("GOLDEN\t" + s);

            int nd = firstDiff(t1, t2);
            if (nd >= 0) {
                System.out.println("run1[" + nd + "]=" + t1[nd]);
                System.out.println("run2[" + nd + "]=" + t2[nd]);
                System.out.println("RESULT FAIL nondeterministic: two identical seeded runs diverge at step " + nd);
            } else if (dump) {
                System.out.println("RESULT PASS (golden dump mode; deterministic over " + STEPS + " steps)");
            } else {
                int d = firstDiff(GOLDEN, t1);
                if (d >= 0) {
                    System.out.println("expected[" + d + "]=" + GOLDEN[d]);
                    System.out.println("actual  [" + d + "]=" + t1[d]);
                    System.out.println("RESULT FAIL state trace diverges from golden at step " + d);
                } else {
                    System.out.println("RESULT PASS deterministic " + STEPS
                            + "-step state trace (species/sick/plants/climate) matches golden");
                }
            }
        } catch (Throwable t) {
            Throwable root = unwrap(t);
            root.printStackTrace(System.out);
            System.out.println("RESULT FAIL uncaught exception: " + root);
        }
        System.exit(0);
    }

    // ------------------------------------------------------------------ run

    static String[] runTrace(Path classes, Path stubs) throws Exception {
        List<URL> urls = new ArrayList<>();
        if (stubs != null) urls.add(stubs.toUri().toURL());
        urls.add(classes.toUri().toURL());
        try (URLClassLoader cl = new URLClassLoader(urls.toArray(new URL[0]),
                ClassLoader.getPlatformClassLoader())) {
            List<Class<?>> all = loadAll(classes, cl);
            Class<?> simClass = findSimulatorClass(all);
            System.out.println("simulator class: " + simClass.getName());
            Object sim = construct(simClass, all);
            Method step = findStepMethod(simClass);
            step.setAccessible(true);
            Probe probe = new Probe(sim);
            String[] trace = new String[STEPS + 1];
            trace[0] = probe.snapshot();
            for (int i = 1; i <= STEPS; i++) {
                step.invoke(sim);
                trace[i] = probe.snapshot();
            }
            return trace;
        }
    }

    static List<Class<?>> loadAll(Path classes, ClassLoader cl) throws Exception {
        List<String> names;
        try (Stream<Path> s = Files.walk(classes)) {
            names = s.filter(p -> p.toString().endsWith(".class"))
                    .map(p -> classes.relativize(p).toString()
                            .replace(".class", "").replace(java.io.File.separatorChar, '.'))
                    .filter(n -> !n.contains("$"))
                    .sorted().collect(Collectors.toList());
        }
        List<Class<?>> out = new ArrayList<>();
        for (String n : names) {
            try { out.add(Class.forName(n, false, cl)); } catch (Throwable t) { /* skip */ }
        }
        return out;
    }

    static Class<?> findSimulatorClass(List<Class<?>> all) {
        for (Class<?> c : all) {
            if (java.awt.Component.class.isAssignableFrom(c) || c.isInterface()) continue;
            for (Method m : c.getDeclaredMethods())
                if (!m.isSynthetic() && m.getParameterCount() == 0
                        && m.getName().equalsIgnoreCase("simulateOneStep")) return c;
        }
        throw new IllegalStateException("no Simulator class (simulateOneStep) found");
    }

    static Method findStepMethod(Class<?> c) throws Exception {
        for (Method m : c.getDeclaredMethods())
            if (!m.isSynthetic() && m.getParameterCount() == 0
                    && m.getName().equalsIgnoreCase("simulateOneStep")) return m;
        throw new NoSuchMethodException("simulateOneStep on " + c);
    }

    /** Pick the richest ctor ending in (int,int) and resolve the other arguments. */
    static Object construct(Class<?> simClass, List<Class<?>> all) throws Exception {
        Constructor<?> best = null;
        for (Constructor<?> k : simClass.getDeclaredConstructors()) {
            if (k.isSynthetic()) continue;
            Class<?>[] p = k.getParameterTypes();
            int n = p.length;
            if (n >= 2 && p[n - 2] == int.class && p[n - 1] == int.class
                    && (best == null || n > best.getParameterCount())) best = k;
        }
        if (best == null) throw new IllegalStateException("no (…, int depth, int width) ctor on " + simClass);
        best.setAccessible(true);
        Class<?>[] p = best.getParameterTypes();
        Object[] a = new Object[p.length];
        for (int i = 0; i < p.length - 2; i++) a[i] = obtain(p[i], all, 0);
        a[p.length - 2] = DEPTH;
        a[p.length - 1] = WIDTH;
        System.out.println("ctor: " + best);
        return best.newInstance(a);
    }

    /** Resolve an instance of {@code type}: proxy for interfaces, else static factory, else no-arg ctor. */
    static Object obtain(Class<?> type, List<Class<?>> all, int depth) throws Exception {
        if (depth > 4) throw new IllegalStateException("cannot resolve " + type);
        if (type.isInterface() && !type.isSealed()) return noopProxy(type);
        // static factories returning exactly this type; composition root (Main) first, fewest params
        List<Method> cands = new ArrayList<>();
        for (Class<?> c : all)
            for (Method m : c.getDeclaredMethods())
                if (Modifier.isStatic(m.getModifiers()) && !m.isSynthetic() && !m.isBridge()
                        && m.getReturnType() == type && !m.getName().startsWith("lambda$"))
                    cands.add(m);
        cands.sort(Comparator.comparingInt((Method m) -> m.getDeclaringClass().getSimpleName().equals("Main") ? 0 : 1)
                .thenComparingInt(Method::getParameterCount));
        for (Method m : cands) {
            try {
                Class<?>[] p = m.getParameterTypes();
                Object[] a = new Object[p.length];
                for (int i = 0; i < p.length; i++) a[i] = obtain(p[i], all, depth + 1);
                m.setAccessible(true);
                Object r = m.invoke(null, a);
                if (r != null) {
                    System.out.println("resolved " + type.getSimpleName() + " via " + m);
                    return r;
                }
            } catch (IllegalStateException e) { /* try next */ }
        }
        try {
            Constructor<?> k = type.getDeclaredConstructor();
            k.setAccessible(true);
            return k.newInstance();
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException("cannot resolve " + type);
        }
    }

    static Object noopProxy(Class<?> iface) {
        return Proxy.newProxyInstance(iface.getClassLoader(), new Class<?>[]{iface}, (proxy, m, args) -> {
            switch (m.getName()) {
                case "hashCode": return System.identityHashCode(proxy);
                case "equals": return proxy == args[0];
                case "toString": return "noop-" + iface.getSimpleName();
            }
            return defaultValue(m.getReturnType());
        });
    }

    static Object defaultValue(Class<?> t) {
        if (!t.isPrimitive() || t == void.class) return null;
        if (t == boolean.class) return Boolean.TRUE;
        if (t == char.class) return (char) 0;
        if (t == long.class) return 0L;
        if (t == double.class) return 0.0;
        if (t == float.class) return 0f;
        if (t == byte.class) return (byte) 0;
        if (t == short.class) return (short) 0;
        return 0;
    }

    // --------------------------------------------------------------- probe

    /** Reads the simulator's state through the model objects it owns. */
    static class Probe {
        final Object sim, field, climate;
        final java.lang.reflect.Field cycleF, animalsF;
        final Method depthM, widthM, animalAt, plantAt;
        final Map<Class<?>, Method> sickM = new HashMap<>(), stageM = new HashMap<>();

        Probe(Object sim) throws Exception {
            this.sim = sim;
            Object fld = null, clim = null;
            java.lang.reflect.Field cyc = null, anim = null;
            for (java.lang.reflect.Field f : sim.getClass().getDeclaredFields()) {
                if (Modifier.isStatic(f.getModifiers())) continue;
                f.setAccessible(true);
                Object v = f.get(sim);
                if (v == null) continue;
                Class<?> t = v.getClass();
                if (fld == null && hasMethod(t, "getDepth") && hasMethod(t, "getAnimalAt", int.class, int.class)) fld = v;
                else if (clim == null && hasMethod(t, "getCurrentWeather") && hasMethod(t, "getHumidity")) clim = v;
                else if (cyc == null && v instanceof Enum && ((Enum<?>) v).getDeclaringClass().getSimpleName()
                        .toLowerCase(Locale.ROOT).contains("cycle")) cyc = f;
                else if (anim == null && v instanceof List && f.getName().toLowerCase(Locale.ROOT).contains("animal")) anim = f;
            }
            if (fld == null) throw new IllegalStateException("no Field object in simulator");
            if (clim == null) throw new IllegalStateException("no Climate object in simulator");
            field = fld; climate = clim; cycleF = cyc; animalsF = anim;
            depthM = field.getClass().getMethod("getDepth");
            widthM = field.getClass().getMethod("getWidth");
            animalAt = field.getClass().getMethod("getAnimalAt", int.class, int.class);
            plantAt = field.getClass().getMethod("getPlantAt", int.class, int.class);
        }

        String snapshot() throws Exception {
            int depth = (Integer) depthM.invoke(field), width = (Integer) widthM.invoke(field);
            TreeMap<String, Integer> animals = new TreeMap<>(), plants = new TreeMap<>();
            int sick = 0; long stages = 0;
            for (int r = 0; r < depth; r++)
                for (int c = 0; c < width; c++) {
                    Object a = animalAt.invoke(field, r, c);
                    if (a != null) {
                        animals.merge(a.getClass().getSimpleName(), 1, Integer::sum);
                        if ((Boolean) find(sickM, a.getClass(), "isSick").invoke(a)) sick++;
                    }
                    Object p = plantAt.invoke(field, r, c);
                    if (p != null) {
                        plants.merge(p.getClass().getSimpleName(), 1, Integer::sum);
                        stages += (Integer) find(stageM, p.getClass(), "getStage").invoke(p);
                    }
                }
            Object w = call(climate, "getCurrentWeather"), s = call(climate, "getCurrentSeason"),
                    h = call(climate, "getHumidity");
            String cyc = cycleF == null ? "?" : String.valueOf(cycleF.get(sim));
            String live = animalsF == null ? "?" : String.valueOf(((List<?>) animalsF.get(sim)).size());
            return join(animals) + "|sick=" + sick + "|" + join(plants) + "|stage=" + stages
                    + "|" + w + "," + s + ",h=" + h + "," + cyc + "|live=" + live;
        }
    }

    static String join(Map<String, Integer> m) {
        return m.entrySet().stream().map(e -> e.getKey() + "=" + e.getValue()).collect(Collectors.joining(","));
    }

    static Method find(Map<Class<?>, Method> cache, Class<?> c, String name) throws Exception {
        Method m = cache.get(c);
        if (m != null) return m;
        for (Class<?> k = c; k != null; k = k.getSuperclass()) {
            try { m = k.getDeclaredMethod(name); break; } catch (NoSuchMethodException e) { /* up */ }
        }
        if (m == null) throw new NoSuchMethodException(name + " on " + c);
        m.setAccessible(true);
        cache.put(c, m);
        return m;
    }

    static Object call(Object o, String name) throws Exception {
        return find(new HashMap<>(), o.getClass(), name).invoke(o);
    }

    static boolean hasMethod(Class<?> c, String n, Class<?>... p) {
        try { c.getMethod(n, p); return true; } catch (NoSuchMethodException e) { return false; }
    }

    // ---------------------------------------------------------- view stubs

    /**
     * Generate and compile a no-op stand-in for every top-level subject class
     * extending java.awt.Window. Returns the output dir, or null if none.
     */
    static Path buildViewStubs(Path work, Path classes) throws Exception {
        Path srcDir = work.resolve("_viewstub_src"), outDir = work.resolve("_viewstub_classes");
        List<Path> sources = new ArrayList<>();
        try (URLClassLoader cl = new URLClassLoader(new URL[]{classes.toUri().toURL()},
                ClassLoader.getPlatformClassLoader())) {
            for (Class<?> c : loadAll(classes, cl)) {
                if (!java.awt.Window.class.isAssignableFrom(c)) continue;
                String pkg = c.getPackageName();
                StringBuilder sb = new StringBuilder();
                if (!pkg.isEmpty()) sb.append("package ").append(pkg).append(";\n");
                List<String> ifs = new ArrayList<>();
                for (Class<?> i : c.getInterfaces()) if (!isJdk(i)) ifs.add(i.getCanonicalName());
                sb.append("public class ").append(c.getSimpleName());
                if (!ifs.isEmpty()) sb.append(" implements ").append(String.join(", ", ifs));
                sb.append(" {\n");
                for (Constructor<?> k : c.getDeclaredConstructors()) {
                    if (k.isSynthetic() || Modifier.isPrivate(k.getModifiers())) continue;
                    sb.append("  public ").append(c.getSimpleName()).append("(")
                            .append(params(k.getParameterTypes())).append(") {}\n");
                }
                for (Method m : c.getDeclaredMethods()) {
                    if (m.isSynthetic() || m.isBridge() || Modifier.isPrivate(m.getModifiers())) continue;
                    Class<?> rt = m.getReturnType();
                    if (rt.getCanonicalName() == null) continue;
                    sb.append("  public ").append(Modifier.isStatic(m.getModifiers()) ? "static " : "")
                            .append(rt.getCanonicalName()).append(' ').append(m.getName()).append('(')
                            .append(params(m.getParameterTypes())).append(") {");
                    if (rt != void.class) sb.append(" return ").append(literal(rt)).append(';');
                    sb.append(" }\n");
                }
                sb.append("}\n");
                Path f = srcDir.resolve(pkg.replace('.', '/')).resolve(c.getSimpleName() + ".java");
                Files.createDirectories(f.getParent());
                Files.write(f, sb.toString().getBytes(StandardCharsets.UTF_8));
                sources.add(f);
                System.out.println("headless view stub: " + c.getName());
            }
        }
        if (sources.isEmpty()) return null;
        Files.createDirectories(outDir);
        javax.tools.JavaCompiler jc = javax.tools.ToolProvider.getSystemJavaCompiler();
        if (jc == null) throw new IllegalStateException("no system Java compiler for view stubs");
        List<String> a = new ArrayList<>(List.of("-nowarn", "-d", outDir.toString(), "-cp", classes.toString()));
        for (Path s : sources) a.add(s.toString());
        java.io.ByteArrayOutputStream err = new java.io.ByteArrayOutputStream();
        int rc = jc.run(null, null, err, a.toArray(new String[0]));
        if (rc != 0) throw new IllegalStateException("view stub compile failed: " + err);
        return outDir;
    }

    static String params(Class<?>[] ps) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < ps.length; i++) {
            if (i > 0) sb.append(", ");
            sb.append(ps[i].getCanonicalName()).append(" a").append(i);
        }
        return sb.toString();
    }

    static String literal(Class<?> t) {
        if (!t.isPrimitive()) return "null";
        if (t == boolean.class) return "true";
        if (t == char.class) return "(char) 0";
        return "(" + t.getName() + ") 0";
    }

    static boolean isJdk(Class<?> c) {
        String n = c.getName();
        return n.startsWith("java.") || n.startsWith("javax.") || n.startsWith("jdk.")
                || n.startsWith("sun.") || n.startsWith("com.sun.");
    }

    static int firstDiff(String[] a, String[] b) {
        for (int i = 0; i < Math.min(a.length, b.length); i++)
            if (!Objects.equals(a[i], b[i])) return i;
        return a.length == b.length ? -1 : Math.min(a.length, b.length);
    }

    static Throwable unwrap(Throwable t) {
        while (t instanceof InvocationTargetException && t.getCause() != null) t = t.getCause();
        return t;
    }

    // Golden per-step trace measured at iteration 0 (see GOLDEN_DUMP).
    static final String[] GOLDEN = {
        "Bear=226,Bird=654,Duck=553,Mouse=626,Wolf=246|sick=0|Flower=697,Grass=8903|stage=19701|SUN,SPRING,h=0,DAY|live=2305",
        "Bear=138,Bird=769,Duck=635,Mouse=729,Wolf=176|sick=151|Flower=697,Grass=8903|stage=13590|SUN,SPRING,h=19,DAY|live=2553",
        "Bear=156,Bird=810,Duck=698,Mouse=811,Wolf=177|sick=241|Flower=697,Grass=8903|stage=11685|SUN,SPRING,h=30,DAY|live=2748",
        "Bear=163,Bird=868,Duck=722,Mouse=854,Wolf=198|sick=310|Flower=697,Grass=8903|stage=9933|SUN,SPRING,h=45,DAY|live=2902",
        "Bear=183,Bird=874,Duck=733,Mouse=908,Wolf=221|sick=383|Flower=697,Grass=8903|stage=8389|SUN,SPRING,h=62,NIGHT|live=3026",
        "Bear=182,Bird=818,Duck=680,Mouse=855,Wolf=245|sick=471|Flower=697,Grass=8903|stage=8389|SUN,SPRING,h=74,NIGHT|live=2830",
        "Bear=182,Bird=768,Duck=628,Mouse=795,Wolf=270|sick=503|Flower=697,Grass=8903|stage=8389|SUN,SPRING,h=86,NIGHT|live=2701",
        "Bear=178,Bird=722,Duck=582,Mouse=738,Wolf=281|sick=522|Flower=697,Grass=8903|stage=8389|CLOUD,SPRING,h=98,NIGHT|live=2560",
        "Bear=174,Bird=667,Duck=536,Mouse=673,Wolf=316|sick=526|Flower=697,Grass=8903|stage=8389|CLOUD,SPRING,h=115,DAY|live=2428",
        "Bear=189,Bird=709,Duck=588,Mouse=729,Wolf=306|sick=473|Flower=697,Grass=8903|stage=9089|RAIN,SPRING,h=115,DAY|live=2626",
        "Bear=215,Bird=722,Duck=595,Mouse=716,Wolf=323|sick=440|Flower=697,Grass=8903|stage=9435|RAIN,SPRING,h=103,DAY|live=2657",
        "Bear=241,Bird=720,Duck=592,Mouse=686,Wolf=307|sick=450|Flower=697,Grass=8903|stage=9726|RAIN,SPRING,h=92,DAY|live=2653",
        "Bear=263,Bird=749,Duck=594,Mouse=671,Wolf=315|sick=483|Flower=697,Grass=8903|stage=9829|RAIN,SPRING,h=82,NIGHT|live=2699",
        "Bear=262,Bird=712,Duck=557,Mouse=634,Wolf=331|sick=509|Flower=697,Grass=8903|stage=11319|RAIN,SPRING,h=67,NIGHT|live=2529",
        "Bear=262,Bird=677,Duck=525,Mouse=605,Wolf=358|sick=524|Flower=697,Grass=8903|stage=11319|SUN,SPRING,h=0,NIGHT|live=2453",
        "Bear=259,Bird=647,Duck=489,Mouse=572,Wolf=362|sick=540|Flower=697,Grass=8903|stage=11319|SUN,SPRING,h=15,NIGHT|live=2365",
        "Bear=246,Bird=604,Duck=450,Mouse=534,Wolf=364|sick=541|Flower=697,Grass=8903|stage=11319|SUN,SUMMER,h=34,DAY|live=2235",
        "Bear=243,Bird=608,Duck=497,Mouse=537,Wolf=351|sick=483|Flower=697,Grass=8903|stage=10308|SUN,SUMMER,h=46,DAY|live=2314",
        "Bear=246,Bird=615,Duck=517,Mouse=514,Wolf=347|sick=450|Flower=697,Grass=8903|stage=9474|SUN,SUMMER,h=61,DAY|live=2301",
        "Bear=255,Bird=615,Duck=504,Mouse=501,Wolf=332|sick=447|Flower=697,Grass=8903|stage=8846|SUN,SUMMER,h=78,DAY|live=2275",
        "Bear=268,Bird=590,Duck=475,Mouse=470,Wolf=331|sick=430|Flower=697,Grass=8903|stage=8342|SUN,SUMMER,h=91,NIGHT|live=2190",
        "Bear=268,Bird=550,Duck=443,Mouse=435,Wolf=339|sick=480|Flower=697,Grass=8903|stage=8342|CLOUD,SUMMER,h=107,NIGHT|live=2072",
        "Bear=268,Bird=519,Duck=412,Mouse=409,Wolf=337|sick=515|Flower=697,Grass=8903|stage=10682|RAIN,SUMMER,h=107,NIGHT|live=1974",
        "Bear=258,Bird=468,Duck=358,Mouse=367,Wolf=342|sick=472|Flower=697,Grass=8903|stage=12552|RAIN,SUMMER,h=93,NIGHT|live=1821",
        "Bear=251,Bird=395,Duck=305,Mouse=335,Wolf=347|sick=437|Flower=697,Grass=8903|stage=14076|RAIN,SUMMER,h=78,DAY|live=1652",
        "Bear=269,Bird=371,Duck=272,Mouse=296,Wolf=324|sick=331|Flower=697,Grass=8903|stage=13459|SUN,SUMMER,h=0,DAY|live=1574",
        "Bear=296,Bird=390,Duck=261,Mouse=291,Wolf=289|sick=312|Flower=697,Grass=8903|stage=12888|SUN,SUMMER,h=16,DAY|live=1569",
        "Bear=331,Bird=363,Duck=255,Mouse=282,Wolf=285|sick=313|Flower=697,Grass=8903|stage=12403|SUN,SUMMER,h=30,DAY|live=1548",
        "Bear=348,Bird=365,Duck=241,Mouse=273,Wolf=277|sick=315|Flower=697,Grass=8903|stage=12022|SUN,SUMMER,h=42,NIGHT|live=1534",
        "Bear=348,Bird=350,Duck=230,Mouse=252,Wolf=282|sick=318|Flower=697,Grass=8903|stage=12022|SUN,SUMMER,h=54,NIGHT|live=1478",
        "Bear=348,Bird=331,Duck=219,Mouse=233,Wolf=291|sick=333|Flower=697,Grass=8903|stage=12022|SUN,SUMMER,h=73,NIGHT|live=1442",
        "Bear=330,Bird=309,Duck=197,Mouse=210,Wolf=336|sick=340|Flower=697,Grass=8903|stage=12022|SUN,SUMMER,h=91,NIGHT|live=1400",
        "Bear=297,Bird=272,Duck=183,Mouse=189,Wolf=347|sick=332|Flower=697,Grass=8903|stage=10615|CLOUD,AUTUMN,h=102,DAY|live=1309",
        "Bear=295,Bird=250,Duck=163,Mouse=199,Wolf=318|sick=284|Flower=697,Grass=8903|stage=11507|RAIN,AUTUMN,h=102,DAY|live=1261",
        "Bear=296,Bird=220,Duck=136,Mouse=205,Wolf=319|sick=259|Flower=697,Grass=8903|stage=12181|RAIN,AUTUMN,h=90,DAY|live=1206",
        "Bear=308,Bird=213,Duck=114,Mouse=210,Wolf=290|sick=243|Flower=697,Grass=8903|stage=12636|RAIN,AUTUMN,h=74,DAY|live=1159",
        "Bear=329,Bird=206,Duck=108,Mouse=207,Wolf=270|sick=228|Flower=697,Grass=8903|stage=12313|SUN,AUTUMN,h=0,NIGHT|live=1148",
        "Bear=328,Bird=197,Duck=101,Mouse=193,Wolf=257|sick=227|Flower=697,Grass=8903|stage=12313|SUN,AUTUMN,h=17,NIGHT|live=1088",
        "Bear=328,Bird=187,Duck=97,Mouse=184,Wolf=263|sick=261|Flower=697,Grass=8903|stage=12313|SUN,AUTUMN,h=29,NIGHT|live=1068",
        "Bear=311,Bird=177,Duck=91,Mouse=171,Wolf=272|sick=246|Flower=697,Grass=8903|stage=12313|SUN,AUTUMN,h=48,NIGHT|live=1033",
        "Bear=296,Bird=158,Duck=85,Mouse=163,Wolf=293|sick=236|Flower=697,Grass=8903|stage=12313|SUN,AUTUMN,h=60,DAY|live=1010",
        "Bear=309,Bird=157,Duck=83,Mouse=167,Wolf=276|sick=210|Flower=697,Grass=8903|stage=12107|SUN,AUTUMN,h=71,DAY|live=1021",
        "Bear=322,Bird=149,Duck=79,Mouse=182,Wolf=270|sick=195|Flower=697,Grass=8903|stage=11929|SUN,AUTUMN,h=83,DAY|live=1016",
        "Bear=367,Bird=147,Duck=67,Mouse=185,Wolf=256|sick=187|Flower=697,Grass=8903|stage=11757|CLOUD,AUTUMN,h=101,DAY|live=1034",
        "Bear=408,Bird=135,Duck=64,Mouse=174,Wolf=230|sick=204|Flower=697,Grass=8903|stage=12503|RAIN,AUTUMN,h=101,NIGHT|live=1025",
        "Bear=408,Bird=129,Duck=61,Mouse=163,Wolf=210|sick=214|Flower=697,Grass=8903|stage=13261|RAIN,AUTUMN,h=90,NIGHT|live=982",
        "Bear=408,Bird=123,Duck=58,Mouse=153,Wolf=205|sick=222|Flower=697,Grass=8903|stage=13788|RAIN,AUTUMN,h=79,NIGHT|live=960",
        "Bear=388,Bird=115,Duck=56,Mouse=141,Wolf=205|sick=205|Flower=697,Grass=8903|stage=13788|SUN,AUTUMN,h=0,NIGHT|live=916",
        "Bear=359,Bird=102,Duck=51,Mouse=122,Wolf=200|sick=195|Flower=697,Grass=8903|stage=9020|SUN,WINTER,h=11,DAY|live=843",
        "Bear=343,Bird=112,Duck=51,Mouse=121,Wolf=208|sick=179|Flower=697,Grass=8903|stage=8838|SUN,WINTER,h=30,DAY|live=847",
        "Bear=339,Bird=122,Duck=49,Mouse=112,Wolf=190|sick=177|Flower=697,Grass=8903|stage=8664|SUN,WINTER,h=40,DAY|live=823",
        "Bear=383,Bird=131,Duck=48,Mouse=125,Wolf=198|sick=182|Flower=697,Grass=8903|stage=8511|SUN,WINTER,h=50,DAY|live=896",
        "Bear=431,Bird=125,Duck=45,Mouse=118,Wolf=187|sick=189|Flower=697,Grass=8903|stage=8393|SUN,WINTER,h=67,NIGHT|live=923",
        "Bear=431,Bird=120,Duck=44,Mouse=113,Wolf=182|sick=200|Flower=697,Grass=8903|stage=8393|SUN,WINTER,h=77,NIGHT|live=896",
        "Bear=428,Bird=114,Duck=43,Mouse=109,Wolf=167|sick=216|Flower=697,Grass=8903|stage=8393|SUN,WINTER,h=97,NIGHT|live=867",
        "Bear=399,Bird=105,Duck=42,Mouse=105,Wolf=159|sick=200|Flower=697,Grass=8903|stage=8393|CLOUD,WINTER,h=117,NIGHT|live=814",
        "Bear=362,Bird=97,Duck=41,Mouse=94,Wolf=173|sick=195|Flower=697,Grass=8903|stage=8393|SUN,WINTER,h=0,DAY|live=782",
        "Bear=379,Bird=98,Duck=45,Mouse=91,Wolf=141|sick=153|Flower=697,Grass=8903|stage=8306|SUN,WINTER,h=17,DAY|live=766",
        "Bear=386,Bird=89,Duck=44,Mouse=81,Wolf=128|sick=137|Flower=697,Grass=8903|stage=8232|SUN,WINTER,h=29,DAY|live=733",
        "Bear=422,Bird=77,Duck=56,Mouse=67,Wolf=124|sick=140|Flower=697,Grass=8903|stage=8169|SUN,WINTER,h=42,DAY|live=750",
        "Bear=492,Bird=58,Duck=50,Mouse=58,Wolf=121|sick=150|Flower=697,Grass=8903|stage=8115|SUN,WINTER,h=59,NIGHT|live=788",
        "Bear=491,Bird=55,Duck=48,Mouse=55,Wolf=126|sick=167|Flower=697,Grass=8903|stage=8115|SUN,WINTER,h=74,NIGHT|live=779",
        "Bear=491,Bird=55,Duck=47,Mouse=54,Wolf=129|sick=176|Flower=697,Grass=8903|stage=8115|SUN,WINTER,h=88,NIGHT|live=777",
        "Bear=428,Bird=49,Duck=43,Mouse=44,Wolf=144|sick=167|Flower=697,Grass=8903|stage=8115|CLOUD,WINTER,h=100,NIGHT|live=709",
        "Bear=374,Bird=38,Duck=37,Mouse=41,Wolf=151|sick=176|Flower=697,Grass=8903|stage=8115|SUN,SPRING,h=0,DAY|live=642",
        "Bear=397,Bird=36,Duck=23,Mouse=32,Wolf=144|sick=153|Flower=697,Grass=8903|stage=8080|SUN,SPRING,h=18,DAY|live=636",
        "Bear=406,Bird=29,Duck=24,Mouse=21,Wolf=122|sick=131|Flower=697,Grass=8903|stage=8052|SUN,SPRING,h=30,DAY|live=604",
        "Bear=462,Bird=27,Duck=21,Mouse=27,Wolf=128|sick=140|Flower=697,Grass=8903|stage=8025|SUN,SPRING,h=44,DAY|live=667",
        "Bear=489,Bird=27,Duck=16,Mouse=29,Wolf=120|sick=127|Flower=697,Grass=8903|stage=7995|SUN,SPRING,h=59,NIGHT|live=687",
        "Bear=488,Bird=27,Duck=16,Mouse=29,Wolf=115|sick=152|Flower=697,Grass=8903|stage=7995|SUN,SPRING,h=70,NIGHT|live=677",
        "Bear=481,Bird=26,Duck=15,Mouse=29,Wolf=120|sick=170|Flower=697,Grass=8903|stage=7995|SUN,SPRING,h=85,NIGHT|live=676",
        "Bear=420,Bird=25,Duck=13,Mouse=29,Wolf=116|sick=154|Flower=697,Grass=8903|stage=7995|CLOUD,SPRING,h=99,NIGHT|live=603",
        "Bear=329,Bird=23,Duck=10,Mouse=29,Wolf=120|sick=128|Flower=697,Grass=8903|stage=7995|CLOUD,SPRING,h=113,DAY|live=516",
        "Bear=305,Bird=22,Duck=10,Mouse=19,Wolf=101|sick=111|Flower=697,Grass=8903|stage=10147|RAIN,SPRING,h=113,DAY|live=461",
        "Bear=322,Bird=12,Duck=7,Mouse=28,Wolf=88|sick=103|Flower=697,Grass=8903|stage=11613|RAIN,SPRING,h=99,DAY|live=460",
        "Bear=362,Bird=8,Duck=11,Mouse=27,Wolf=79|sick=113|Flower=697,Grass=8903|stage=12684|RAIN,SPRING,h=87,DAY|live=490",
        "Bear=393,Bird=7,Duck=12,Mouse=23,Wolf=71|sick=108|Flower=697,Grass=8903|stage=13453|RAIN,SPRING,h=72,NIGHT|live=507",
        "Bear=388,Bird=6,Duck=12,Mouse=23,Wolf=70|sick=121|Flower=697,Grass=8903|stage=13453|SUN,SPRING,h=0,NIGHT|live=503",
        "Bear=384,Bird=6,Duck=12,Mouse=23,Wolf=64|sick=124|Flower=697,Grass=8903|stage=13453|SUN,SPRING,h=15,NIGHT|live=490",
        "Bear=331,Bird=4,Duck=12,Mouse=23,Wolf=66|sick=114|Flower=697,Grass=8903|stage=13453|SUN,SPRING,h=33,NIGHT|live=439",
        "Bear=274,Bird=4,Duck=12,Mouse=23,Wolf=63|sick=98|Flower=697,Grass=8903|stage=13453|SUN,SUMMER,h=49,DAY|live=377",
        "Bear=244,Bird=9,Duck=14,Mouse=23,Wolf=60|sick=85|Flower=697,Grass=8903|stage=13427|SUN,SUMMER,h=60,DAY|live=353",
        "Bear=256,Bird=12,Duck=16,Mouse=22,Wolf=54|sick=68|Flower=697,Grass=8903|stage=13394|SUN,SUMMER,h=77,DAY|live=360",
        "Bear=293,Bird=11,Duck=18,Mouse=19,Wolf=47|sick=80|Flower=697,Grass=8903|stage=13364|SUN,SUMMER,h=89,DAY|live=389",
        "Bear=322,Bird=20,Duck=17,Mouse=21,Wolf=46|sick=89|Flower=697,Grass=8903|stage=13333|CLOUD,SUMMER,h=102,NIGHT|live=428",
        "Bear=314,Bird=20,Duck=17,Mouse=21,Wolf=50|sick=98|Flower=697,Grass=8903|stage=15013|RAIN,SUMMER,h=102,NIGHT|live=422",
        "Bear=298,Bird=20,Duck=17,Mouse=21,Wolf=53|sick=98|Flower=697,Grass=8903|stage=16235|RAIN,SUMMER,h=89,NIGHT|live=410",
        "Bear=274,Bird=20,Duck=17,Mouse=21,Wolf=57|sick=93|Flower=697,Grass=8903|stage=17169|RAIN,SUMMER,h=70,NIGHT|live=389",
        "Bear=220,Bird=20,Duck=17,Mouse=21,Wolf=63|sick=90|Flower=697,Grass=8903|stage=17169|SUN,SUMMER,h=0,DAY|live=341",
        "Bear=204,Bird=26,Duck=15,Mouse=20,Wolf=62|sick=78|Flower=697,Grass=8903|stage=17124|SUN,SUMMER,h=13,DAY|live=329",
        "Bear=212,Bird=33,Duck=18,Mouse=15,Wolf=62|sick=64|Flower=697,Grass=8903|stage=17081|SUN,SUMMER,h=30,DAY|live=342",
        "Bear=230,Bird=25,Duck=21,Mouse=19,Wolf=55|sick=71|Flower=697,Grass=8903|stage=17039|SUN,SUMMER,h=45,DAY|live=354",
        "Bear=272,Bird=17,Duck=21,Mouse=19,Wolf=54|sick=77|Flower=697,Grass=8903|stage=17006|SUN,SUMMER,h=57,NIGHT|live=387",
        "Bear=271,Bird=17,Duck=21,Mouse=19,Wolf=52|sick=79|Flower=697,Grass=8903|stage=17006|SUN,SUMMER,h=77,NIGHT|live=380",
        "Bear=260,Bird=17,Duck=21,Mouse=19,Wolf=43|sick=80|Flower=697,Grass=8903|stage=17006|SUN,SUMMER,h=93,NIGHT|live=361",
        "Bear=227,Bird=17,Duck=21,Mouse=19,Wolf=51|sick=74|Flower=697,Grass=8903|stage=17006|CLOUD,SUMMER,h=112,NIGHT|live=336",
        "Bear=186,Bird=15,Duck=20,Mouse=19,Wolf=49|sick=78|Flower=697,Grass=8903|stage=14775|RAIN,AUTUMN,h=112,DAY|live=290",
        "Bear=183,Bird=12,Duck=28,Mouse=13,Wolf=49|sick=68|Flower=697,Grass=8903|stage=14897|RAIN,AUTUMN,h=101,DAY|live=288",
        "Bear=152,Bird=14,Duck=32,Mouse=11,Wolf=41|sick=45|Flower=697,Grass=8903|stage=14989|RAIN,AUTUMN,h=91,DAY|live=253",
        "Bear=171,Bird=11,Duck=36,Mouse=10,Wolf=55|sick=54|Flower=697,Grass=8903|stage=15050|RAIN,AUTUMN,h=71,DAY|live=284",
        "Bear=190,Bird=8,Duck=35,Mouse=16,Wolf=52|sick=61|Flower=697,Grass=8903|stage=15010|SUN,AUTUMN,h=0,NIGHT|live=304"
    };
}
