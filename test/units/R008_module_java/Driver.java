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
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Correctness driver for R008_module_java (savannah predator-prey simulation:
 * LivingOrganism -> Animal (Lion/Cheetah/Zebra/Giraffe/Lemur) + Plant, with
 * static Time and Weather).
 *
 * The simulation is intrinsically NON-deterministic: Randomizer has
 * useShared=false, so every Randomizer.getRandom() call returns a brand-new
 * unseeded java.util.Random.  A golden-trace oracle is therefore impossible;
 * this driver checks properties that hold on every run of the baseline
 * (validated over many baseline runs), identically for every iteration:
 *
 *   1. Simulator(depth, width) constructs and populates without exception
 *      (the GUI view is replaced by a headless stub via stubs/); the plant
 *      layer is completely full (populate() puts a Plant in every cell) and
 *      every animal species is present.
 *   2. simulateOneStep() runs STEPS times per rep with no uncaught exception;
 *      the static Time.getStep() counter (when present) equals STEPS.
 *   3. GRID CONSISTENCY after construction and after every step, per layer
 *      (the Field keeps one Animal and one Plant per cell):
 *        - every live organism in the simulator's lists has an in-bounds
 *          location and the field's layer for its kind holds exactly it there;
 *        - every object on either layer is a live listed organism (no ghosts).
 *   4. LIFE-CYCLE DYNAMICS, aggregated over all reps (tracked by object
 *      identity): every species has births and deaths; the population vector
 *      keeps changing.
 *   5. Populations stay within [0, capacity] per layer.
 *
 * Checks 3-4 were added in the T10 audit: the earlier smoke oracle passed
 * baselines with breeding disabled for a species or with moves that leave
 * ghost occupants on the grid.
 */
public class Driver {
    static final int DEPTH = 50, WIDTH = 75;      // 3750 cells; all species populate reliably
    static final int STEPS = 80;
    static final int REPS = Integer.getInteger("reps", 4);
    static final int MIN_DISTINCT = 3;
    static final String[] ANIMALS = {"Lion", "Cheetah", "Zebra", "Giraffe", "Lemur"};
    static final String PLANT = "Plant";
    static final String[] SPECIES = {"Lion", "Cheetah", "Zebra", "Giraffe", "Lemur", "Plant"};

    static Method isAliveM, getLocM, rowM, colM, objAtM, timeGetStep;
    static Class<?> animalLayer, plantLayer;

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

        timeGetStep = findTimeGetStep(cl);
        try {
            animalLayer = Class.forName("Animal", false, cl);
            plantLayer = Class.forName("Plant", false, cl);
        } catch (Throwable t) {
            System.out.println("RESULT FAIL Animal/Plant layer classes not found: " + t);
            return;
        }

        Stats stats = new Stats();
        List<String> problems = new ArrayList<>();
        for (int rep = 0; rep < REPS && problems.isEmpty(); rep++) {
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
                    + " steps: grid layers consistent, all species born and died, sim progresses");
        } else {
            System.out.println("RESULT FAIL " + problems.get(0));
        }
    }

    static final class Stats {
        Map<String, Integer> births = new TreeMap<>();
        Map<String, Integer> deaths = new TreeMap<>();
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
            problems.add("rep " + rep + ": no Field (getObjectAt(int,int,Class)) reachable from the simulator");
            return;
        }

        Map<String, Integer> initial = census(sim);
        System.out.println("rep " + rep + " initial: " + initial);
        for (String sp : ANIMALS) {
            if (initial.getOrDefault(sp, 0) <= 0) {
                problems.add("rep " + rep + ": species " + sp + " absent after populate(): " + initial);
                return;
            }
        }
        if (initial.getOrDefault(PLANT, 0) != capacity) {
            problems.add("rep " + rep + ": expected a full plant layer of " + capacity
                    + " after populate(), got " + initial.getOrDefault(PLANT, 0));
            return;
        }
        if (animalTotal(initial) > capacity) {
            problems.add("rep " + rep + ": initial animal population " + animalTotal(initial)
                    + " exceeds capacity " + capacity);
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
            if (last.getOrDefault(PLANT, 0) > capacity) {
                problems.add("rep " + rep + ": plant population " + last.get(PLANT)
                        + " exceeds capacity " + capacity + " at step " + s);
                return;
            }
            if (animalTotal(last) > capacity) {
                problems.add("rep " + rep + ": animal population " + animalTotal(last)
                        + " exceeds capacity " + capacity + " at step " + s);
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
        if (timeGetStep != null) {
            try {
                int step = ((Number) timeGetStep.invoke(null)).intValue();
                if (step != STEPS) {
                    problems.add("rep " + rep + ": Time.getStep() is " + step + " after "
                            + STEPS + " simulateOneStep() calls");
                }
            } catch (Throwable t) {
                problems.add("rep " + rep + ": Time.getStep() threw " + cause(t));
            }
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
                Class<?> layer = animalLayer.isInstance(o) ? animalLayer : plantLayer;
                Object at = objAtM.invoke(field, r, c, layer);
                if (at != o)
                    return "live " + o.getClass().getSimpleName() + " claims " + loc + " but the field's "
                            + layer.getSimpleName() + " layer holds "
                            + (at == null ? "nothing" : at.getClass().getSimpleName()) + " there";
            }
            for (int r = 0; r < DEPTH; r++)
                for (int c = 0; c < WIDTH; c++)
                    for (Class<?> layer : new Class<?>[]{animalLayer, plantLayer}) {
                        Object at = objAtM.invoke(field, r, c, layer);
                        if (at != null && !alive.containsKey(at))
                            return "ghost " + at.getClass().getSimpleName() + " on the " + layer.getSimpleName()
                                    + " layer at (" + r + "," + c + ") is not a live organism of the simulation";
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
                Method m = f.getType().getDeclaredMethod("getObjectAt", int.class, int.class, Class.class);
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
            if (Modifier.isStatic(f.getModifiers())) continue;
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

    static int animalTotal(Map<String, Integer> counts) {
        return counts.entrySet().stream()
                .filter(e -> !e.getKey().equals(PLANT))
                .mapToInt(Map.Entry::getValue).sum();
    }

    static Method findTimeGetStep(ClassLoader cl) {
        try {
            Class<?> timeC = Class.forName("Time", false, cl);
            Method m = timeC.getDeclaredMethod("getStep");
            if (Modifier.isStatic(m.getModifiers())) {
                m.setAccessible(true);
                return m;
            }
        } catch (Throwable t) {
            // optional check - absent if the class/method was renamed
        }
        return null;
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
            if (c.getSimpleName().equals("SimulatorView")) continue;
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
            if (n.contains("onestep")) fallback = m;
        }
        return fallback;
    }
}
