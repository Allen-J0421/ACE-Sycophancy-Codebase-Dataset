/**
 * Immutable configuration data for a Simulator instance.
 * Holds grid dimensions, per-species creation probabilities, and animal lifecycle parameters.
 */
public class SimulationConfig {
    // Per-species animal lifecycle configuration.
    public static final AnimalConfig DINGO_CONFIG = new AnimalConfig(50, 700, 0.04,  3, 100);
    public static final AnimalConfig ANT_CONFIG   = new AnimalConfig(20, 400, 0.32, 14,  60);
    public static final AnimalConfig RAT_CONFIG   = new AnimalConfig(25, 600, 0.31, 15, 100);
    public static final AnimalConfig EAGLE_CONFIG = new AnimalConfig(50, 700, 0.10, 11,  60);
    public static final AnimalConfig SNAKE_CONFIG = new AnimalConfig(30, 700, 0.33, 11, 100);
    public static final AnimalConfig EMU_CONFIG   = new AnimalConfig(30, 600, 0.17,  7,  60);

    public static final SimulationConfig DEFAULT = new SimulationConfig(
        80, 120,
        0.09, 0.13, 0.11, 0.12, 0.12, 0.08, 0.34, 0.36
    );

    public final int depth;
    public final int width;
    public final double dingoProbability;
    public final double antProbability;
    public final double ratProbability;
    public final double eagleProbability;
    public final double snakeProbability;
    public final double emuProbability;
    public final double acaciaProbability;
    public final double grassProbability;

    public SimulationConfig(int depth, int width,
                            double dingoProbability, double antProbability,
                            double ratProbability, double eagleProbability,
                            double snakeProbability, double emuProbability,
                            double acaciaProbability, double grassProbability) {
        this.depth = depth;
        this.width = width;
        this.dingoProbability = dingoProbability;
        this.antProbability = antProbability;
        this.ratProbability = ratProbability;
        this.eagleProbability = eagleProbability;
        this.snakeProbability = snakeProbability;
        this.emuProbability = emuProbability;
        this.acaciaProbability = acaciaProbability;
        this.grassProbability = grassProbability;
    }

    public SimulationConfig withDimensions(int depth, int width) {
        return new SimulationConfig(depth, width,
            dingoProbability, antProbability, ratProbability,
            eagleProbability, snakeProbability, emuProbability,
            acaciaProbability, grassProbability);
    }
}
