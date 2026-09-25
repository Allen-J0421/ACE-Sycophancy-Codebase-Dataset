import java.io.File;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URLClassLoader;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

/**
 * Correctness driver for 018_coin_change.
 *
 * Baseline semantics (iteration 0): counts the number of distinct UNORDERED
 * combinations of coin denominations (unlimited supply of each) summing to a
 * target:  static int count(int[] coins, int sum).  Later iterations rename to
 * countCombinations and widen the return type to long.
 *
 * Oracle: independent combination-count DP (long) over the baseline demo,
 * edge cases, and 40 random instances with a fixed seed.
 */
public class Driver {
    public static void main(String[] args) throws Exception {
        Path work = Paths.get(args[0]);
        Path classes = work.resolve("_classes");
        if (!Stage.compileSubjectSources(work, classes)) return;
        List<Class<?>> all = scan(classes);

        Method entry = null;
        Object receiver = null;
        for (Class<?> c : all) {
            for (Method m : c.getDeclaredMethods()) {
                Class<?>[] p = m.getParameterTypes();
                boolean shape = p.length == 2
                        && ((p[0] == int[].class && p[1] == int.class)
                            || (p[0] == int.class && p[1] == int[].class))
                        && isNumeric(m.getReturnType());
                if (!shape) continue;
                String n = m.getName().toLowerCase();
                if (!(n.contains("count") || n.contains("ways") || n.contains("combin")
                        || n.contains("change"))) continue;
                m.setAccessible(true);
                if (!Modifier.isStatic(m.getModifiers())) {
                    try {
                        var ct = c.getDeclaredConstructor();
                        ct.setAccessible(true);
                        receiver = ct.newInstance();
                    } catch (Throwable t) { continue; }
                }
                entry = m;
                break;
            }
            if (entry != null) break;
        }
        if (entry == null) {
            System.out.println("RESULT FAIL no coin-change counting entry point found");
            return;
        }
        boolean coinsFirst = entry.getParameterTypes()[0] == int[].class;
        System.out.println("entry: " + entry);

        List<Object[]> instances = new ArrayList<>();   // {coins, sum, note}
        instances.add(new Object[]{new int[]{1, 2, 3}, 5, "baseline demo -> 5"});
        instances.add(new Object[]{new int[]{}, 0, "no coins, sum 0 -> 1"});
        instances.add(new Object[]{new int[]{}, 7, "no coins, sum 7 -> 0"});
        instances.add(new Object[]{new int[]{2, 4}, 7, "unreachable odd sum -> 0"});
        instances.add(new Object[]{new int[]{5}, 5, "single coin exact -> 1"});
        instances.add(new Object[]{new int[]{1, 2, 5}, 0, "sum 0 -> 1"});
        instances.add(new Object[]{new int[]{1, 2, 5, 10, 20, 50}, 60, "currency set"});
        Random rnd = new Random(1818);
        for (int t = 0; t < 40; t++) {
            int k = 1 + rnd.nextInt(5);
            Set<Integer> denoms = new LinkedHashSet<>();
            while (denoms.size() < k) denoms.add(1 + rnd.nextInt(9));
            int[] coins = denoms.stream().mapToInt(Integer::intValue).toArray();
            instances.add(new Object[]{coins, rnd.nextInt(61), "random"});
        }

        int checks = 0, failed = 0;
        for (Object[] inst : instances) {
            int[] coins = (int[]) inst[0];
            int sum = (Integer) inst[1];
            long expected = refCount(coins, sum);
            checks++;
            Object got;
            try {
                got = coinsFirst ? entry.invoke(receiver, coins, sum)
                                 : entry.invoke(receiver, sum, coins);
            } catch (Throwable t) {
                failed++;
                System.out.println("CHECK FAIL coins=" + Arrays.toString(coins) + " sum=" + sum
                        + " threw " + deep(t));
                continue;
            }
            long g = ((Number) got).longValue();
            if (g != expected) {
                failed++;
                System.out.println("CHECK FAIL coins=" + Arrays.toString(coins) + " sum=" + sum
                        + " -> " + g + ", expected " + expected + " (" + inst[2] + ")");
            }
        }
        System.out.println(checks + " checks, " + failed + " failed");
        System.out.println(failed == 0 ? "RESULT PASS " + checks + " instances"
                : "RESULT FAIL " + failed + "/" + checks + " combination counts wrong");
    }

    /** Independent reference: unordered combination count, coins in outer loop. */
    static long refCount(int[] coins, int sum) {
        long[] ways = new long[sum + 1];
        ways[0] = 1;
        for (int coin : coins) {
            for (int amount = coin; amount <= sum; amount++) {
                ways[amount] += ways[amount - coin];
            }
        }
        return ways[sum];
    }

    static boolean isNumeric(Class<?> t) {
        return t == int.class || t == long.class || t == Integer.class || t == Long.class
                || t == java.math.BigInteger.class;
    }

    static String deep(Throwable t) {
        Throwable c = t.getCause() != null ? t.getCause() : t;
        return c.toString();
    }

    static List<Class<?>> scan(Path classes) throws Exception {
        List<String> names;
        try (Stream<Path> s = Files.walk(classes)) {
            names = s.filter(p -> p.toString().endsWith(".class"))
                    .map(p -> classes.relativize(p).toString()
                            .replace(".class", "").replace(File.separatorChar, '.'))
                    .filter(n -> !n.contains("$"))
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
