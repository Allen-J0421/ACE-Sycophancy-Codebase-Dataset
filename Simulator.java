import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.awt.Color;
import javafx.application.Application;

/**
 * A simple predator-prey simulator, based on a rectangular field.
 *
 * @version 18.02.22 (DD:MM:YY)
 */
public class Simulator
{
    // Constants representing configuration information for the simulation:
    //   The default width for the grid:
    private static final int DEFAULT_WIDTH = 120;
    //   The default height for the grid:
    private static final int DEFAULT_DEPTH = 160;

    // The number of steps in a day:
    public static final int NUMBER_OF_STEPS_PER_DAY = 25;

    // Single source of truth for per-species metadata (class, colour, creation probability, factory).
    // Entries are in population-priority order (highest-probability first), matching the original
    // if/else-if cascade so that the same nextDouble draw sequence is preserved per cell.
    private static final List<SpeciesDescriptor> SPECIES = List.of(
        new SpeciesDescriptor(Grasshopper.class,  new Color(188, 248, 236), 0.15,  (f, l) -> new Grasshopper(true, f, l)),
        new SpeciesDescriptor(HarvesterAnt.class, new Color(4, 139, 168),   0.25,  (f, l) -> new HarvesterAnt(true, f, l)),
        new SpeciesDescriptor(Termite.class,      new Color(22, 219, 147),  0.21,  (f, l) -> new Termite(true, f, l)),
        new SpeciesDescriptor(Impala.class,       new Color(239, 234, 90),  0.15,  (f, l) -> new Impala(true, f, l)),
        new SpeciesDescriptor(Pangolin.class,     new Color(242, 158, 76),  0.125, (f, l) -> new Pangolin(true, f, l)),
        new SpeciesDescriptor(Aardvark.class,     new Color(204, 183, 174), 0.12,  (f, l) -> new Aardvark(true, f, l)),
        new SpeciesDescriptor(Mongoose.class,     new Color(65, 69, 53),    0.12,  (f, l) -> new Mongoose(true, f, l)),
        new SpeciesDescriptor(StarGrass.class,    new Color(125, 97, 103),  0.05,  (f, l) -> new StarGrass(f, l)),
        new SpeciesDescriptor(RedOatGrass.class,  new Color(164, 3, 111),   0.04,  (f, l) -> new RedOatGrass(f, l)),
        new SpeciesDescriptor(Acacia.class,       new Color(187, 214, 134), 0.04,  (f, l) -> new Acacia(f, l))
    );
    // Carcasses are produced dynamically (not seeded), so kept separate from SPECIES:
    private static final Color CARCASS_COLOR = new Color(202, 0, 0);

    // List of actors in the field:
    private List<Actor> actors;
    // The current state of the field:
    private static Field field;
    // The current step of the simulation.
    private static int step = 0;
    // A graphical view of the simulation:
    private static SimulatorView view;
    // A Boolean indicating whether or not to reset the StatisticsView:
    static boolean resetStatisticsView = false;

    /**
     * Construct a simulation field with default size.
     */
    public Simulator()
    {
        this(DEFAULT_DEPTH, DEFAULT_WIDTH);
    }

    /**
     * Create a simulation field with the given size.
     *
     * @param depth Depth of the field. Must be greater than zero.
     * @param width Width of the field. Must be greater than zero.
     */
    public Simulator(int depth, int width)
    {
        boolean isSizeImpossible = depth <= 0 || depth <= 0;

        if (isSizeImpossible)
        {
            System.out.println("The dimensions must be greater than zero.");
            System.out.println("Using default values.");

            depth = DEFAULT_DEPTH;
            width = DEFAULT_WIDTH;
        }

        actors = new ArrayList<>();
        field = new Field(depth, width);

        // Set the initial weather:
        WeatherSystem.changeToNextDay();

        // Create a view of the state of each location in the field:
        view = new SimulatorView(this, depth, width);

        for (SpeciesDescriptor d : SPECIES)
            view.setColor(d.getSpeciesClass(), d.getColor());
        view.setColor(Carcass.class, CARCASS_COLOR);

        // Show the statistics window:
        new Thread(() -> {
            Application.launch(StatisticsView.class);
        }).start();

        // Setup a valid starting point:
        reset();
    }

    /**
     * @return The current step in the simulation.
     */
    public static int getCurrentStep() { return step; }

    /**
     * @return The current field in the simulation.
     */
    public static Field getCurrentField() { return field; }

    /**
     * Run the simulation from its current state for a reasonably long period,
     * (4000 steps).
     */
    public void runLongSimulation()
    {
        simulate(4000);
    }

    /**
     * Run the simulation from its current state for the given number of steps.
     * Stop before the given number of steps if it ceases to be viable.
     *
     * @param numSteps The number of steps to run for.
     */
    public void simulate(int numSteps)
    {
        for (int i = 0; i < numSteps && view.isViable(field); i++)
        {
            simulateOneStep();
        }
    }

    /**
     * Run the simulation from its current state for a single step.
     * Iterate over the whole field updating the state of each actor.
     */
    public void simulateOneStep()
    {
        step++;

        // Update the weather:
        if (TimeSystem.hasDayChanged())
        {
            WeatherSystem.changeToNextDay();
        }

        // Provide space for newborn actors:
        List<Actor> newActors = new ArrayList<>();

        // Let all actors act:
        for (Actor actor : actors) actor.act(newActors);
        actors.removeIf(a -> !a.getIsAlive());

        // Add the newly born actors to the main list:
        actors.addAll(newActors);

        view.showStatus(step, field);
    }

    /**
     * Reset the simulation to a starting position.
     */
    public void reset()
    {
        step = 0;
        actors.clear();
        populate();

        // Show the starting state in the view:
        view.showStatus(step, field);

        // Toggle boolean to show StatisticsView should be reset:
        resetStatisticsView = true;
    }

    /**
     * Randomly populate the field with actors.
     */
    private void populate()
    {
        Random rand = Randomizer.getRandom();
        field.clear();

        for (int row = 0; row < field.getDepth(); row++)
        {
            for (int col = 0; col < field.getWidth(); col++)
            {
                Location location = new Location(row, col);
                // The for-loop-with-break is the exact desugaring of the original
                // if/else-if cascade, preserving the same nextDouble draw sequence.
                for (SpeciesDescriptor d : SPECIES)
                {
                    if (rand.nextDouble() <= d.getCreationProbability())
                    {
                        actors.add(d.create(field, location));
                        break;
                    }
                }
                // Else leave the location empty.
            }
        }
    }

}
