/**
 * Immutable configuration data for a Simulator instance.
 * Holds grid dimensions and per-species creation probabilities.
 */
public class SimulationConfig {
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
