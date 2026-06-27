import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.awt.Color;

/**
 * A simple predator-prey simulator, based on a rectangular field
 * containing rabbits and foxes.
 *
 * @version 2022.02.xx
 */
public class Simulator
{
    // The default width for the grid.
    private static final int DEFAULT_WIDTH = 120;
    // The default depth of the grid.
    private static final int DEFAULT_DEPTH = 80;

    // All living entities (animals and plants) in one unified list.
    private List<Entity> entities;
    // The current state of the field.
    private Field<Entity> field;
    // The current step of the simulation.
    private int step;
    // Manages weather state and step-to-step transitions.
    private WeatherManager weatherManager;
    // A graphical view of the simulation.
    private SimulatorView view;

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
        if (width <= 0 || depth <= 0) {
            System.out.println("The dimensions must be greater than zero.");
            System.out.println("Using default values.");
            depth = DEFAULT_DEPTH;
            width = DEFAULT_WIDTH;
        }

        entities       = new ArrayList<>();
        field          = new Field<>(depth, width);
        weatherManager = new WeatherManager();

        view = new SimulatorView(depth, width);
        for (EntityRegistry.Registration reg : EntityRegistry.getAll()) {
            view.setColor(reg.type, reg.color);
        }

        // Setup a valid starting point.
        reset();
    }

    /**
     * Run the simulation from its current state for a reasonably long period
     * (4000 steps).
     */
    public void runLongSimulation()
    {
        simulate(4000);
    }

    /**
     * Run the simulation from its current state for the given number of steps.
     * Stop before the given number of steps if it ceases to be viable.
     * @param numSteps The number of steps to run for.
     */
    public void simulate(int numSteps)
    {
        for (int step = 1; step <= numSteps && view.isViable(field); step++) {
            simulateOneStep();
            delay(60);
        }
    }

    /**
     * Run the simulation from its current state for a single step.
     * All entities — animals and plants alike — are driven through the same loop.
     */
    public void simulateOneStep()
    {
        step++;
        weatherManager.update();

        List<Entity> newEntities = new ArrayList<>();
        for (Iterator<Entity> it = entities.iterator(); it.hasNext(); ) {
            Entity entity = it.next();
            entity.act(newEntities, step, weatherManager.getCurrent());
            if (!entity.isAlive()) {
                it.remove();
            }
        }
        // Add offspring (animals only; plants never populate newEntities).
        entities.addAll(newEntities);

        view.showStatus(step, field, weatherManager.getCurrent());
    }

    /**
     * Reset the simulation to a starting position.
     */
    public void reset()
    {
        step = 0;
        entities.clear();
        populate();
        weatherManager.update();
        view.showStatus(step, field, weatherManager.getCurrent());
    }

    /**
     * Randomly populate the field from the entity registry.
     * Each cell draws one random number per registration in order;
     * the first match claims the cell.
     */
    private void populate()
    {
        Random rand = Randomizer.getRandom();
        field.clear();
        for (int row = 0; row < field.getDepth(); row++) {
            for (int col = 0; col < field.getWidth(); col++) {
                Location location = new Location(row, col);
                for (EntityRegistry.Registration reg : EntityRegistry.getAll()) {
                    if (rand.nextDouble() <= reg.probability) {
                        entities.add(reg.factory.create(field, location));
                        break;
                    }
                }
            }
        }
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
