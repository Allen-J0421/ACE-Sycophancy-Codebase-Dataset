/**
 * Immutable configuration snapshot for a Simulator instance.
 *
 * Constructed exclusively by Simulator.Builder and then passed to the
 * Simulator's private constructor.  Other classes that need runtime
 * configuration values (PopulationManager, Weather) obtain this object
 * via Simulator.getConfig().
 *
 * @version 2022.03.01
 */
public class SimulatorConfig
{
    // Grid
    private final int gridDepth;
    private final int gridWidth;

    // Simulation pace
    private final int stepDelayMs;

    // Initial population creation probabilities
    private final double hyenaCreationProbability;
    private final double lionCreationProbability;
    private final double gazelleCreationProbability;
    private final double mouseCreationProbability;
    private final double fennecFoxCreationProbability;
    private final double grassCreationProbability;
    private final double lakeCreationProbability;

    // Weather occurrence probabilities
    private final double rainProbability;
    private final double fogProbability;
    private final double heatwaveProbability;

    /** Package-private: only Simulator.Builder may construct this. */
    SimulatorConfig(Simulator.Builder b)
    {
        gridDepth                = b.gridDepth;
        gridWidth                = b.gridWidth;
        stepDelayMs              = b.stepDelayMs;
        hyenaCreationProbability     = b.hyenaCreationProbability;
        lionCreationProbability      = b.lionCreationProbability;
        gazelleCreationProbability   = b.gazelleCreationProbability;
        mouseCreationProbability     = b.mouseCreationProbability;
        fennecFoxCreationProbability = b.fennecFoxCreationProbability;
        grassCreationProbability     = b.grassCreationProbability;
        lakeCreationProbability      = b.lakeCreationProbability;
        rainProbability          = b.rainProbability;
        fogProbability           = b.fogProbability;
        heatwaveProbability      = b.heatwaveProbability;
    }

    public int    getGridDepth()                  { return gridDepth; }
    public int    getGridWidth()                  { return gridWidth; }
    public int    getStepDelayMs()                { return stepDelayMs; }
    public double getHyenaCreationProbability()     { return hyenaCreationProbability; }
    public double getLionCreationProbability()      { return lionCreationProbability; }
    public double getGazelleCreationProbability()   { return gazelleCreationProbability; }
    public double getMouseCreationProbability()     { return mouseCreationProbability; }
    public double getFennecFoxCreationProbability() { return fennecFoxCreationProbability; }
    public double getGrassCreationProbability()     { return grassCreationProbability; }
    public double getLakeCreationProbability()      { return lakeCreationProbability; }
    public double getRainProbability()     { return rainProbability; }
    public double getFogProbability()      { return fogProbability; }
    public double getHeatwaveProbability() { return heatwaveProbability; }
}
