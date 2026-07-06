import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * The simulation model: owns the field, the actor list, and the weather,
 * and advances them one step at a time. All GUI and lifecycle concerns live
 * in SimulationOrchestrator.
 */
public class Simulator
{
    private static final int DEFAULT_WIDTH = 120;
    private static final int DEFAULT_DEPTH = 160;

    // The number of steps in a day:
    public static final int NUMBER_OF_STEPS_PER_DAY = 25;

    // Manages weather state and daily transitions:
    private static WeatherSystem weather;
    // The current state of the field:
    private static Field field;
    // The current step of the simulation:
    private static int step = 0;
    // Per-species population counts, updated each display refresh:
    private static PopulationStats populationStats;
    // A Boolean indicating whether or not to reset the StatisticsView:
    public static boolean resetStatisticsView = false;

    // Manages per-step age and hunger updates for all actors:
    private final ActorLifecycle lifecycle;
    // Populates the field with actors at simulation start/reset:
    private final FieldPopulator populator;
    // List of actors in the field:
    private List<Actor> actors;

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

        weather         = new WeatherSystem();
        populationStats = new PopulationStats();
        lifecycle       = new ActorLifecycle();
        populator       = new FieldPopulator();
        actors        = new ArrayList<>();
        field         = new Field(depth, width);

        reset();
    }

    // -------------------------------------------------------------------------
    // Static accessors (used by actors, views, and StatisticsView)
    // -------------------------------------------------------------------------

    /** @return The current step in the simulation. */
    public static int getCurrentStep() { return step; }

    /** @return The current field in the simulation. */
    public static Field getCurrentField() { return field; }

    /** @return The population stats for the current simulation step. */
    public static PopulationStats getPopulationStats() { return populationStats; }

    /** @return The weather system managing the current weather state. */
    public static WeatherSystem getWeather() { return weather; }

    // -------------------------------------------------------------------------
    // Simulation lifecycle (called by SimulationOrchestrator)
    // -------------------------------------------------------------------------

    /**
     * Advance the simulation by one step: update weather if the day has
     * changed, then let every actor act.
     */
    public void simulateOneStep()
    {
        step++;

        if (TimeSystem.hasDayChanged())
        {
            weather.advance();
        }

        updateActors();
    }

    /**
     * Reset the simulation to a fresh starting position.
     */
    public void reset()
    {
        step = 0;
        actors.clear();
        populate();
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    /**
     * Advance every actor by one step: let each act, collect offspring,
     * remove the dead, then merge offspring into the main list.
     */
    private void updateActors()
    {
        List<Actor> newActors = new ArrayList<>();
        for (Iterator<Actor> it = actors.iterator(); it.hasNext(); )
        {
            Actor actor = it.next();
            lifecycle.tick(actor);
            if (actor.getIsAlive())
            {
                actor.act(newActors);
            }
            if (!actor.getIsAlive()) it.remove();
        }
        actors.addAll(newActors);
    }

    /**
     * Populate the field with all actor species.
     */
    private void populate()
    {
        populator.populate(field, actors);
    }
}
