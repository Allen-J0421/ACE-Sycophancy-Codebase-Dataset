import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.function.BiFunction;
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

    // Registry of all entity types: creation probability, display colour, and factory.
    // Entries are checked in order per cell; the first match wins.
    // To add a new species, add one line here — no other changes needed.
    private static final List<EntitySpec> ENTITY_SPECS = Arrays.asList(
        new EntitySpec(Lion.class,  0.01, Color.RED,    (f, l) -> new Lion(true, f, l)),
        new EntitySpec(Deer.class,  0.02, Color.BLUE,   (f, l) -> new Deer(true, f, l)),
        new EntitySpec(Owl.class,   0.05, Color.ORANGE, (f, l) -> new Owl(true, f, l)),
        new EntitySpec(Mouse.class, 0.04, Color.YELLOW, (f, l) -> new Mouse(true, f, l)),
        new EntitySpec(Cat.class,   0.05, Color.PINK,   (f, l) -> new Cat(true, f, l)),
        new EntitySpec(Grass.class, 0.40, Color.GREEN,  (f, l) -> new Grass(true, f, l))
    );

    // All living entities (animals and plants) in one unified list.
    private List<Entity> entities;
    // The current state of the field.
    private Field field;
    // The current step of the simulation.
    private int step;
    // The current weather of the simulation.
    private String weather;
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

        entities = new ArrayList<>();
        field    = new Field(depth, width);

        view = new SimulatorView(depth, width);
        for (EntitySpec spec : ENTITY_SPECS) {
            view.setColor(spec.type, spec.color);
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
        updateWeather();

        List<Entity> newEntities = new ArrayList<>();
        for (Iterator<Entity> it = entities.iterator(); it.hasNext(); ) {
            Entity entity = it.next();
            entity.act(newEntities, step, weather);
            if (!entity.isAlive()) {
                it.remove();
            }
        }
        // Add offspring (animals only; plants never populate newEntities).
        entities.addAll(newEntities);

        view.showStatus(step, field, weather);
    }

    /**
     * Reset the simulation to a starting position.
     */
    public void reset()
    {
        step = 0;
        entities.clear();
        populate();
        updateWeather();
        view.showStatus(step, field, weather);
    }

    /**
     * Randomly populate the field using the ENTITY_SPECS registry.
     * Each cell draws one random number per spec in order; the first spec
     * whose probability threshold is met claims the cell.
     */
    private void populate()
    {
        Random rand = Randomizer.getRandom();
        field.clear();
        for (int row = 0; row < field.getDepth(); row++) {
            for (int col = 0; col < field.getWidth(); col++) {
                Location location = new Location(row, col);
                for (EntitySpec spec : ENTITY_SPECS) {
                    if (rand.nextDouble() <= spec.probability) {
                        entities.add(spec.factory.apply(field, location));
                        break;
                    }
                }
            }
        }
    }

    /**
     * Randomly select the weather for this step.
     */
    private void updateWeather()
    {
        Random rand = Randomizer.getRandom();
        if (rand.nextDouble() <= 0.33) {
            weather = "Foggy";
        } else if (rand.nextDouble() <= 0.33) {
            weather = "Rainy";
        } else {
            weather = "Sunny";
        }
    }

    /**
     * @return the current weather.
     */
    private String getWeather()
    {
        return weather;
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

    // -------------------------------------------------------------------------

    /**
     * Describes one entity type for use in the species registry.
     * Bundles together the class (for colour registration), creation probability,
     * display colour, and a factory that constructs an instance.
     */
    private static class EntitySpec
    {
        final Class<?>                            type;
        final double                              probability;
        final Color                               color;
        final BiFunction<Field, Location, Entity> factory;

        EntitySpec(Class<?> type, double probability, Color color,
                   BiFunction<Field, Location, Entity> factory)
        {
            this.type        = type;
            this.probability = probability;
            this.color       = color;
            this.factory     = factory;
        }
    }
}
