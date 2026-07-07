/**
 * Immutable value object holding all tunable parameters for a simulation run:
 * grid dimensions and the per-species initial creation probabilities and storm
 * probability. Decouples parameter storage from engine logic so setups can be
 * varied without touching SimulationEngine.
 *
 * @version 2022/03/02
 */
public class SimulationConfig
{
    public static final int DEFAULT_DEPTH = 80;
    public static final int DEFAULT_WIDTH = 120;

    public final int depth;
    public final int width;
    public final double salmonCreationProbability;
    public final double codCreationProbability;
    public final double seaweedCreationProbability;
    public final double sharkCreationProbability;
    public final double whaleCreationProbability;
    public final double stormHappenProbability;

    public SimulationConfig(int depth, int width,
                            double salmonCreationProbability,
                            double codCreationProbability,
                            double seaweedCreationProbability,
                            double sharkCreationProbability,
                            double whaleCreationProbability,
                            double stormHappenProbability)
    {
        this.depth = depth;
        this.width = width;
        this.salmonCreationProbability = salmonCreationProbability;
        this.codCreationProbability = codCreationProbability;
        this.seaweedCreationProbability = seaweedCreationProbability;
        this.sharkCreationProbability = sharkCreationProbability;
        this.whaleCreationProbability = whaleCreationProbability;
        this.stormHappenProbability = stormHappenProbability;
    }

    /** Config with default grid size and default creation probabilities. */
    public static SimulationConfig defaults()
    {
        return withDimensions(DEFAULT_DEPTH, DEFAULT_WIDTH);
    }

    /** Config with custom grid size and default creation probabilities. */
    public static SimulationConfig withDimensions(int depth, int width)
    {
        return new SimulationConfig(depth, width, 0.08, 0.08, 0.03, 0.04, 0.03, 0.14);
    }
}
