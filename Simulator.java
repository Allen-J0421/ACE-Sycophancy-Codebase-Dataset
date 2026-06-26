/**
 * A simple predator-prey simulator, based on a rectangular field of organisms.
 *
 * This class is a thin entry point: it wires the Swing-based {@link
 * SimulatorView} to a {@link SimulationEngine} (which holds all the simulation
 * logic) and delegates to it. The view observes the engine as a {@link
 * SimulationListener}, so the engine drives the GUI through events rather than
 * depending on it directly.
 *
 * @version 2022.03.02
 */
public class Simulator
{
    // Constants representing configuration information for the simulation.
    // The default width for the grid.
    private static final int DEFAULT_WIDTH = 200;
    // The default depth of the grid.
    private static final int DEFAULT_DEPTH = 200;

    // The engine that runs the simulation.
    private final SimulationEngine engine;

    /**
     * Construct a simulation field with default size.
     */
    public Simulator()
    {
        this(DEFAULT_DEPTH, DEFAULT_WIDTH);
    }

    /**
     * Create a simulation field with the given size.
     * @param depth Depth of the field. Must be greater than zero.
     * @param width Width of the field. Must be greater than zero.
     */
    public Simulator(int depth, int width)
    {
        if(width <= 0 || depth <= 0) {
            System.out.println("The dimensions must be greater than zero.");
            System.out.println("Using default values.");
            depth = DEFAULT_DEPTH;
            width = DEFAULT_WIDTH;
        }

        // Create the GUI view, build a populator that draws species onto it,
        // and wire the view to the engine as an observer. The engine never
        // references the view directly - it only emits events.
        SimulatorView view = new SimulatorView(depth, width);
        Populator populator = new Populator(view);
        engine = new SimulationEngine(depth, width, populator);
        engine.addListener(view);

        // Populate the field and show the initial state (fires a reset event).
        engine.reset();
    }

    /**
     * Run the simulation from its current state for a reasonably long period,
     * (4000 steps).
     */
    public void runLongSimulation()
    {
        engine.runLongSimulation();
    }

    /**
     * Run the simulation from its current state for the given number of steps.
     * @param numSteps The number of steps to run for.
     */
    public void simulate(int numSteps)
    {
        engine.simulate(numSteps);
    }

    /**
     * Run the simulation from its current state for a single step.
     */
    public void simulateOneStep()
    {
        engine.simulateOneStep();
    }

    public int getHour() {
        return engine.getHour();
    }

    /**
     * Reset the simulation to a starting position.
     */
    public void reset()
    {
        engine.reset();
    }
}
