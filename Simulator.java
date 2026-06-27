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
    // Entries are checked in order per cell; the first match wins (preserving the
    // original else-if semantics). To add a new species, add one line here.
    private static final List<EntitySpec> ENTITY_SPECS = Arrays.asList(
        new EntitySpec(Lion.class,  0.01, Color.RED,    (f, l) -> new Lion(true, f, l),  true),
        new EntitySpec(Deer.class,  0.02, Color.BLUE,   (f, l) -> new Deer(true, f, l),  true),
        new EntitySpec(Owl.class,   0.05, Color.ORANGE, (f, l) -> new Owl(true, f, l),   true),
        new EntitySpec(Mouse.class, 0.04, Color.YELLOW, (f, l) -> new Mouse(true, f, l), true),
        new EntitySpec(Cat.class,   0.05, Color.PINK,   (f, l) -> new Cat(true, f, l),   true),
        new EntitySpec(Grass.class, 0.40, Color.GREEN,  (f, l) -> new Grass(true, f, l), false)
    );

    // List of animals in the field.
    private List<Animal> animals;
    // List of plants in the field.
    private List<Plant> plants;
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

        animals = new ArrayList<>();
        plants  = new ArrayList<>();
        field   = new Field(depth, width);

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
     */
    public void simulateOneStep()
    {
        step++;
        updateWeather();

        // Provide space for newborn animals.
        List<Animal> newAnimals = new ArrayList<>();
        for (Iterator<Animal> it = animals.iterator(); it.hasNext(); ) {
            Animal animal = it.next();
            animal.act(newAnimals, step, weather);
            if (!animal.isAlive()) {
                it.remove();
            }
        }
        animals.addAll(newAnimals);

        // Let all plants act.
        for (Iterator<Plant> it = plants.iterator(); it.hasNext(); ) {
            Plant plant = it.next();
            plant.act(step, weather);
            if (!plant.isAlive()) {
                it.remove();
            }
        }

        view.showStatus(step, field, weather);
    }

    /**
     * Reset the simulation to a starting position.
     */
    public void reset()
    {
        step = 0;
        animals.clear();
        plants.clear();
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
                        Object entity = spec.factory.apply(field, location);
                        if (spec.isAnimal) {
                            animals.add((Animal) entity);
                        } else {
                            plants.add((Plant) entity);
                        }
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
     * display colour, a factory that constructs an instance, and a flag that
     * distinguishes animals (which produce offspring) from plants (which don't).
     */
    private static class EntitySpec
    {
        final Class<?>                           type;
        final double                             probability;
        final Color                              color;
        final BiFunction<Field, Location, Object> factory;
        final boolean                            isAnimal;

        EntitySpec(Class<?> type, double probability, Color color,
                   BiFunction<Field, Location, Object> factory, boolean isAnimal)
        {
            this.type        = type;
            this.probability = probability;
            this.color       = color;
            this.factory     = factory;
            this.isAnimal    = isAnimal;
        }
    }
}
