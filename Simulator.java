import java.awt.Color;

/**
 * A simple predator-prey simulator, based on a rectangular field
 * containing Seaweed, Salmon, Cod, Shark, Whale, and also the element of disease and weather.
 * Coordinates the simulation engine and the graphical view.
 *
 * @version 2022/03/02
 */
public class Simulator
{
    private SimulationEngine engine;
    private SimulatorView view;

    /**
     * Construct a simulation field with default size.
     */
    public Simulator()
    {
        this(SimulationConfig.DEFAULT_DEPTH, SimulationConfig.DEFAULT_WIDTH);
    }

    /**
     * Create a simulation field with the given size.
     * @param depth Depth of the field. Must be greater than zero.
     * @param width Width of the field. Must be greater than zero.
     */
    public Simulator(int depth, int width)
    {
        if (width <= 0 || depth <= 0) {
            System.out.println("The dimensions must be greater than zero.");
            System.out.println("Using default values.");
            depth = SimulationConfig.DEFAULT_DEPTH;
            width = SimulationConfig.DEFAULT_WIDTH;
        }

        SimulationConfig config = SimulationConfig.withDimensions(depth, width);
        engine = new SimulationEngine(config);

        view = new SimulatorView(config.depth, config.width);
        view.setColor(Cod.class, Color.ORANGE);
        view.setColor(Salmon.class, Color.YELLOW);
        view.setColor(Seaweed.class, Color.RED);
        view.setColor(Shark.class, Color.BLACK);
        view.setColor(Whale.class, Color.PINK);
        view.setColor(Weather.class, Color.BLUE);

        view.showStatus(engine.getStep(), engine.getField(), engine.timeOfDay(), engine.getWeather(), engine.getOxygenLevel());
    }

    /**
     * Run the simulation from its current state for a reasonably long period,
     * (1000 steps).
     */
    public void runLongSimulation()
    {
        simulate(1000);
    }

    /**
     * Run the simulation from its current state for the given number of steps.
     * Stop before the given number of steps if it ceases to be viable.
     * @param numSteps The number of steps to run for.
     */
    public void simulate(int numSteps)
    {
        for (int step = 1; step <= numSteps && view.isViable(engine.getField()); step++) {
            simulateOneStep();
            delay(60);
        }
    }

    /**
     * Advance the simulation by one step and refresh the view.
     */
    public void simulateOneStep()
    {
        engine.stepOnce();
        view.showStatus(engine.getStep(), engine.getField(), engine.timeOfDay(), engine.getWeather(), engine.getOxygenLevel());
    }

    /**
     * Reset the simulation to a starting position.
     */
    public void reset()
    {
        engine.reset();
        view.showStatus(engine.getStep(), engine.getField(), engine.timeOfDay(), engine.getWeather(), engine.getOxygenLevel());
    }

    /**
     * 5 steps is considered as a day time followed by 5 steps considered as a night.
     * @return true if currently day time, false if night time.
     */
    public boolean timeOfDay()
    {
        return engine.timeOfDay();
    }

    /**
     * Pause for a given time.
     * @param millisec The time to pause for, in milliseconds.
     */
    private void delay(int millisec)
    {
        try {
            Thread.sleep(millisec);
        } catch (InterruptedException ie) {
            // wake up
        }
    }
}
