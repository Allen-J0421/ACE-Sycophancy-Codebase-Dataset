import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Correctness driver for R007_module_java (aquatic predator-prey simulation:
 * Creature -> Animal (Shark/Whale/Cod/Salmon) + Seaweed, plus Disease/Weather).
 *
 * The simulation is intrinsically NON-deterministic in the baseline: Animal's
 * constructor draws its sex from Math.random(), which gates how many draws
 * each creature subsequently takes from the shared seeded Randomizer.  The
 * driver seeds Math.random()'s generator (via --add-opens, see unit.conf) and
 * calls Randomizer.reset() when available, purely so that a given subject
 * reproduces; the oracle itself never relies on an exact trace (refactors may
 * legitimately reorder RNG draws), it checks properties that hold on every run
 * of the baseline:
 *
 *   1. Simulator(depth, width) constructs and populates without exception
 *      (the GUI view is replaced by a headless stub via stubs/); every species
 *      (Salmon, Cod, Seaweed, Shark, Whale) is present initially.
 *   2. simulateOneStep() runs STEPS times per rep with no uncaught exception;
 *      the step counter (field "step", when present) equals STEPS.
 *   3. GRID CONSISTENCY after construction and after every step:
 *        - every live creature in the simulator's lists has a location inside
 *          the field and the field holds exactly that creature there;
 *        - every object on the field is a live creature from the lists (no
 *          ghosts left behind by moves or deaths).
 *   4. LIFE-CYCLE DYNAMICS, aggregated over all reps (tracked by object
 *      identity): every species has births (new instances appear) and deaths
 *      (instances die or vanish); the population vector keeps changing.
 *   5. Populations stay within [0, capacity].
 *
 * Checks 3-4 were added in the T10 audit: the earlier smoke oracle passed
 * baselines with breeding disabled for a species or with moves that leave
 * ghost occupants on the grid.
 */
public class Driver {
    static final int DEPTH = 40, WIDTH = 60;      // 2400 cells; all species populate reliably
    static final int STEPS = 100;
    static final int REPS = Integer.getInteger("reps", 4);
    static final int MIN_DISTINCT = 3;
    static final String[] SPECIES = {"Salmon", "Cod", "Seaweed", "Shark", "Whale"};

    static Method isAliveM, getLocM, rowM, colM, objAtM;

    public static void main(String[] args) {
        try {
            run(args);
        } catch (Throwable t) {
            t.printStackTrace(System.out);
            System.out.println("RESULT FAIL driver-level exception: " + t);
        }
    }

    static void run(String[] args) throws Exception {
        Path classes = Paths.get(args[0]).resolve("_classes");
        URLClassLoader cl = new URLClassLoader(new URL[]{classes.toUri().toURL()});

        Class<?> simC = findSimulator(classes, cl);
        if (simC == null) {
            System.out.println("RESULT FAIL no Simulator-like class (int,int ctor + no-arg step method) found");
            return;
        }
        Constructor<?> ctor = simC.getDeclaredConstructor(int.class, int.class);
        ctor.setAccessible(true);
        Method stepM = findStepMethod(simC);
        if (stepM == null) {
            System.out.println("RESULT FAIL no no-arg simulateOneStep-like method on " + simC.getName());
            return;
        }
        stepM.setAccessible(true);
        System.out.println("simulator: " + simC.getName() + ", step method: " + stepM.getName());

        Method randReset = null;
        try {
            Class<?> rz = Class.forName("Randomizer", true, cl);
            randReset = rz.getDeclaredMethod("reset");
            randReset.setAccessible(true);
        } catch (Throwable ignored) { }

        Stats stats = new Stats();
        List<String> problems = new ArrayList<>();
        for (int rep = 0; rep < REPS && problems.isEmpty(); rep++) {
            seedMathRandom(1000L + rep);
            if (randReset != null) {
                try { randReset.invoke(null); } catch (Throwable ignored) { }
            }
            runOnce(rep, ctor, stepM, problems, stats);
        }
        if (problems.isEmpty()) {
            System.out.println("births " + stats.births + ", deaths " + stats.deaths);
            for (String sp : SPECIES) {
                if (stats.births.getOrDefault(sp, 0) == 0)
                    problems.add("no " + sp + " was ever born in " + REPS + " reps x " + STEPS + " steps (births " + stats.births + ")");
                else if (stats.deaths.getOrDefault(sp, 0) == 0)
                    problems.add("no " + sp + " ever died in " + REPS + " reps x " + STEPS + " steps (deaths " + stats.deaths + ")");
            }
        }
        if (problems.isEmpty()) {
            System.out.println("RESULT PASS " + REPS + " reps x " + STEPS
                    + " steps: grid consistent, all species born and died, sim progresses");
        } else {
            System.out.println("RESULT FAIL " + problems.get(0));
        }
    }

    static final class Stats {
        Map<String, Integer> births = new TreeMap<>();
        Map<String, Integer> deaths = new TreeMap<>();
    }

    static void seedMathRandom(long seed) {
        try {
            Class<?> h = Class.forName("java.lang.Math$RandomNumberGeneratorHolder");
            java.lang.reflect.Field f = h.getDeclaredField("randomNumberGenerator");
            f.setAccessible(true);
            ((Random) f.get(null)).setSeed(seed);
        } catch (Throwable t) {
            // best effort only (reproducibility aid); the oracle does not depend on it
        }
    }

    static void runOnce(int rep, Constructor<?> ctor, Method stepM, List<String> problems, Stats stats) {
        Object sim;
        try {
            sim = ctor.newInstance(DEPTH, WIDTH);
        } catch (Throwable t) {
            cause(t).printStackTrace(System.out);
            problems.add("rep " + rep + ": exception constructing Simulator: " + cause(t));
            return;
        }
        int capacity = DEPTH * WIDTH;
        Object field = findField(sim);
        if (field == null) {
            problems.add("rep " + rep + ": no Field (getObjectAt(int,int)) reachable from the simulator");
            return;
        }

        Map<String, Integer> initial = census(sim);
        System.out.println("rep " + rep + " initial: " + initial);
        for (String sp : SPECIES) {
            if (initial.getOrDefault(sp, 0) <= 0) {
                problems.add("rep " + rep + ": species " + sp + " absent after populate(): " + initial);
                return;
            }
        }
        if (total(initial) > capacity) {
            problems.add("rep " + rep + ": initial population " + total(initial) + " exceeds capacity " + capacity);
            return;
        }
        IdentityHashMap<Object, Boolean> prevAlive = new IdentityHashMap<>();
        String err = checkGrid(sim, field, prevAlive);
        if (err != null) { problems.add("rep " + rep + " after populate: " + err); return; }

        Set<Map<String, Integer>> distinct = new HashSet<>();
        distinct.add(initial);
        Map<String, Integer> last = initial;
        for (int s = 1; s <= STEPS; s++) {
            try {
                stepM.invoke(sim);
            } catch (Throwable t) {
                cause(t).printStackTrace(System.out);
                problems.add("rep " + rep + ": exception at step " + s + ": " + cause(t));
                return;
            }
            last = census(sim);
            if (total(last) > capacity) {
                problems.add("rep " + rep + ": population " + total(last) + " exceeds capacity " + capacity + " at step " + s);
                return;
            }
            IdentityHashMap<Object, Boolean> alive = new IdentityHashMap<>();
            err = checkGrid(sim, field, alive);
            if (err != null) { problems.add("rep " + rep + " step " + s + ": " + err); return; }
            for (Object o : alive.keySet())
                if (!prevAlive.containsKey(o)) stats.births.merge(o.getClass().getSimpleName(), 1, Integer::sum);
            for (Object o : prevAlive.keySet())
                if (!alive.containsKey(o)) stats.deaths.merge(o.getClass().getSimpleName(), 1, Integer::sum);
            prevAlive = alive;
            distinct.add(last);
        }
        System.out.println("rep " + rep + " final:   " + last + " (" + distinct.size() + " distinct population vectors)");

        if (distinct.size() < MIN_DISTINCT) {
            problems.add("rep " + rep + ": simulation frozen - only " + distinct.size()
                    + " distinct population vectors over " + STEPS + " steps");
            return;
        }
        Integer stepCounter = readIntField(sim, "step");
        if (stepCounter != null && stepCounter != STEPS) {
            problems.add("rep " + rep + ": step counter is " + stepCounter + " after " + STEPS + " simulateOneStep() calls");
        }
    }

    /**
     * Grid consistency: each live listed creature sits in-bounds on its own cell,
     * and every occupied cell holds a live listed creature. Fills {@code alive}.
     */
    static String checkGrid(Object sim, Object field, IdentityHashMap<Object, Boolean> alive) {
        try {
            for (Object o : listed(sim)) {
                Method am = isAliveM != null && isAliveM.getDeclaringClass().isInstance(o) ? isAliveM
                        : (isAliveM = findNoArg(o.getClass(), "isalive", boolean.class));
                if (am == null) return "no isAlive() on " + o.getClass().getName();
                if (!(Boolean) am.invoke(o)) continue;
                alive.put(o, Boolean.TRUE);
                Method lm = getLocM != null && getLocM.getDeclaringClass().isInstance(o) ? getLocM
                        : (getLocM = findNoArg(o.getClass(), "getlocation", null));
                if (lm == null) return "no getLocation() on " + o.getClass().getName();
                Object loc = lm.invoke(o);
                if (loc == null) return "live " + o.getClass().getSimpleName() + " has a null location";
                if (rowM == null) {
                    rowM = findNoArg(loc.getClass(), "getrow", int.class);
                    colM = findNoArg(loc.getClass(), "getcol", int.class);
                    if (rowM == null || colM == null) return "cannot read row/col of " + loc.getClass().getName();
                }
                int r = (Integer) rowM.invoke(loc), c = (Integer) colM.invoke(loc);
                if (r < 0 || r >= DEPTH || c < 0 || c >= WIDTH)
                    return "live " + o.getClass().getSimpleName() + " at out-of-field location " + loc;
                Object at = objAtM.invoke(field, r, c);
                if (at != o)
                    return "live " + o.getClass().getSimpleName() + " claims " + loc + " but the field holds "
                            + (at == null ? "nothing" : at.getClass().getSimpleName()) + " there";
            }
            for (int r = 0; r < DEPTH; r++)
                for (int c = 0; c < WIDTH; c++) {
                    Object at = objAtM.invoke(field, r, c);
                    if (at != null && !alive.containsKey(at))
                        return "ghost " + at.getClass().getSimpleName() + " on the field at (" + r + "," + c
                                + ") is not a live creature of the simulation";
                }
            return null;
        } catch (Throwable t) {
            return "grid inspection threw " + cause(t);
        }
    }

    static Method findNoArg(Class<?> c, String lname, Class<?> ret) {
        for (Class<?> k = c; k != null && k != Object.class; k = k.getSuperclass()) {
            for (Method m : k.getDeclaredMethods()) {
                if (m.getParameterCount() == 0 && !m.isSynthetic() && m.getName().toLowerCase().equals(lname)
                        && (ret == null || m.getReturnType() == ret)) {
                    m.setAccessible(true);
                    return m;
                }
            }
        }
        return null;
    }

    static Object findField(Object sim) {
        for (java.lang.reflect.Field f : sim.getClass().getDeclaredFields()) {
            if (Modifier.isStatic(f.getModifiers())) continue;
            try {
                Method m = f.getType().getDeclaredMethod("getObjectAt", int.class, int.class);
                m.setAccessible(true);
                f.setAccessible(true);
                Object v = f.get(sim);
                if (v == null) continue;
                objAtM = m;
                return v;
            } catch (NoSuchMethodException | IllegalAccessException ignored) { }
        }
        return null;
    }

    static List<Object> listed(Object sim) throws IllegalAccessException {
        List<Object> out = new ArrayList<>();
        for (java.lang.reflect.Field f : sim.getClass().getDeclaredFields()) {
            if (Modifier.isStatic(f.getModifiers())) continue;
            f.setAccessible(true);
            if (f.get(sim) instanceof List<?> list)
                for (Object o : list) if (o != null) out.add(o);
        }
        return out;
    }

    /** Count occupants of every java.util.List field of the simulator, by simple class name. */
    static Map<String, Integer> census(Object sim) {
        Map<String, Integer> counts = new TreeMap<>();
        for (java.lang.reflect.Field f : sim.getClass().getDeclaredFields()) {
            f.setAccessible(true);
            Object v;
            try { v = f.get(sim); } catch (IllegalAccessException e) { continue; }
            if (v instanceof List<?> list) {
                for (Object o : list) {
                    if (o != null) counts.merge(o.getClass().getSimpleName(), 1, Integer::sum);
                }
            }
        }
        return counts;
    }

    static int total(Map<String, Integer> counts) {
        return counts.values().stream().mapToInt(Integer::intValue).sum();
    }

    static Integer readIntField(Object sim, String name) {
        try {
            java.lang.reflect.Field f = sim.getClass().getDeclaredField(name);
            if (f.getType() != int.class) return null;
            f.setAccessible(true);
            return f.getInt(sim);
        } catch (Exception e) {
            return null;
        }
    }

    static Throwable cause(Throwable t) {
        return t instanceof InvocationTargetException && t.getCause() != null ? t.getCause() : t;
    }

    static Class<?> findSimulator(Path classes, ClassLoader cl) throws Exception {
        List<String> names;
        try (Stream<Path> s = Files.walk(classes)) {
            names = s.filter(p -> p.toString().endsWith(".class"))
                    .map(p -> classes.relativize(p).toString()
                            .replace(".class", "").replace(java.io.File.separatorChar, '.'))
                    .filter(n -> !n.contains("$"))
                    .collect(Collectors.toList());
        }
        // Prefer the conventional name, fall back to a structural scan.
        Class<?> best = null;
        for (String n : names) {
            Class<?> c;
            try { c = Class.forName(n, false, cl); } catch (Throwable t) { continue; }
            try { c.getDeclaredConstructor(int.class, int.class); } catch (NoSuchMethodException e) { continue; }
            if (findStepMethod(c) == null) continue;
            if (c.getSimpleName().equals("Simulator")) return c;
            if (best == null) best = c;
        }
        return best;
    }

    static Method findStepMethod(Class<?> c) {
        Method fallback = null;
        for (Method m : c.getDeclaredMethods()) {
            if (m.getParameterCount() != 0) continue;
            String n = m.getName().toLowerCase();
            if (n.equals("simulateonestep")) return m;
            if (n.contains("onestep") || n.equals("step")) fallback = m;
        }
        return fallback;
    }
}
