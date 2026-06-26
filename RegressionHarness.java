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

        boolean verbose = args.length > 0 && args[0].equals("verbose");
        long fingerprint = 1469598103934665603L; // FNV-1a offset basis
        for (int step = 1; step <= STEPS; step++) {
            engine.step();
            long sig = signature(engine.getField(), step);
            if (verbose) System.out.println(step + "," + sig);
            fingerprint = mix(fingerprint, sig);
        }
        System.out.println("FINGERPRINT=" + fingerprint);
    }

    private static long signature(Field field, int step)
    {
        // Position-sensitive: hash the class (and diseased flag) at every cell.
        long s = 1469598103934665603L ^ step;
        for (int row = 0; row < field.getDepth(); row++) {
            for (int col = 0; col < field.getWidth(); col++) {
                Object o = field.getObjectAt(row, col);
                long cell = 0;
                if (o != null) {
                    cell = o.getClass().getName().hashCode();
                    if (o instanceof Organism && ((Organism) o).isDiseased()) cell = cell * 7 + 3;
                }
                s = (s ^ cell) * 1099511628211L;
            }
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
