import java.util.*;

/**
 * Headless regression harness. Drives the REAL SimulationEngine (no copied
 * loop) and FNV-hashes per-step class counts. Deterministic via the fixed-seed
 * Randomizer. Expected fingerprint after the engine extraction = the same value
 * the copied-loop harness produced for the default path: -2010090214429012298.
 *
 * NOT part of the shipped simulation - a verification artifact only.
 */
public class RegressionHarness
{
    private static final int DEPTH = 160;
    private static final int WIDTH = 240;
    private static final int STEPS = 150;

    public static void main(String[] args)
    {
        Randomizer.reset();

        SimulationEngine engine = new SimulationEngine(DEPTH, WIDTH);
        engine.reset();

        long fingerprint = 1469598103934665603L; // FNV-1a offset basis
        for (int step = 1; step <= STEPS; step++) {
            engine.step();
            fingerprint = mix(fingerprint, signature(engine.getField(), step));
        }
        System.out.println("FINGERPRINT=" + fingerprint);
    }

    private static long signature(Field field, int step)
    {
        Map<String, Integer> counts = new TreeMap<>();
        int diseased = 0;
        for (int row = 0; row < field.getDepth(); row++) {
            for (int col = 0; col < field.getWidth(); col++) {
                Object o = field.getObjectAt(row, col);
                if (o != null) {
                    counts.merge(o.getClass().getName(), 1, Integer::sum);
                    if (o instanceof Organism && ((Organism) o).isDiseased()) {
                        diseased++;
                    }
                }
            }
        }
        long s = step * 1000003L + diseased * 31L;
        for (Map.Entry<String, Integer> e : counts.entrySet()) {
            s = s * 1000003L + e.getKey().hashCode() * 17L + e.getValue();
        }
        return s;
    }

    private static long mix(long h, long v)
    {
        h ^= v;
        h *= 1099511628211L; // FNV prime
        return h;
    }
}
