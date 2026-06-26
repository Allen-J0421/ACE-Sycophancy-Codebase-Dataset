import java.util.Map;

/**
 * Thin entry-point that wires a {@link SimulationEngine} to a {@link SimulatorView}.
 * All simulation logic lives in SimulationEngine; this class is responsible only
 * for constructing the two collaborators and connecting them.
 *
 * @version 2022.03.02
 */
public class Simulator implements SimulationControls
{
    private final SimulationEngine engine;

    /** Construct a simulation with default field size and default creation probabilities. */
    public Simulator()
    {
        this(SimulationEngine.DEFAULT_DEPTH, SimulationEngine.DEFAULT_WIDTH);
    }

    /**
     * Construct a simulation with a custom field size and default creation probabilities.
     * @param depth Depth of the field. Must be greater than zero.
     * @param width Width of the field. Must be greater than zero.
     */
    public Simulator(int depth, int width)
    {
        if (width <= 0 || depth <= 0) {
            System.out.println("The dimensions must be greater than zero.");
            System.out.println("Using default values.");
            depth = SimulationEngine.DEFAULT_DEPTH;
            width = SimulationEngine.DEFAULT_WIDTH;
        }
        SimulatorView view = buildView(depth, width);
        engine = new SimulationEngine(depth, width, view);
        view.wireButtons(this);
        engine.reset();
    }

    /**
     * Construct a simulation with custom creation probabilities.
     * Useful for testing different probability combinations to find an optimal configuration.
     */
    public Simulator(double GrassProbability, double DeerProbability, double CoyoteProbability,
                     double WolfProbability, double EagleProbability, double HunterProbability,
                     double MouseProbability)
    {
        SimulatorView view = buildView(SimulationEngine.DEFAULT_DEPTH, SimulationEngine.DEFAULT_WIDTH);
        Map<Class<?>, Double> probs = Map.ofEntries(
                Map.entry(Coyote.class, CoyoteProbability),
                Map.entry(Deer.class,   DeerProbability),
                Map.entry(Wolf.class,   WolfProbability),
                Map.entry(Eagle.class,  EagleProbability),
                Map.entry(Mouse.class,  MouseProbability),
                Map.entry(Grass.class,  GrassProbability),
                Map.entry(Hunter.class, HunterProbability)
        );
        engine = new SimulationEngine(SimulationEngine.DEFAULT_DEPTH, SimulationEngine.DEFAULT_WIDTH, probs, view);
        view.wireButtons(this);
        engine.reset();
    }

    // --- SimulationControls delegation ---

    @Override public void stepOnce()          { engine.stepOnce(); }
    @Override public void runLongSimulation() { engine.runLongSimulation(); }
    @Override public void stop()              { engine.stop(); }
    @Override public void reset()             { engine.reset(); }
    @Override public boolean isPlaying()      { return engine.isPlaying(); }

    /** Delegates to the engine for callers that need direct step control (e.g. TestingMain). */
    public void simulate(int numSteps)        { engine.simulate(numSteps); }
    public void simulateOneStep()             { engine.simulateOneStep(); }

    // --- View construction ---

    private static SimulatorView buildView(int depth, int width)
    {
        SimulatorView view = new SimulatorView(depth, width);
        for (Class cls : SimulationInfo.DEFAULT_COLOR_MAP.keySet()) {
            view.setColor(cls, SimulationInfo.DEFAULT_COLOR_MAP.get(cls));
        }
        return view;
    }
}
