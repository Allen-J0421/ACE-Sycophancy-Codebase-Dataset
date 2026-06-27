import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Production implementation of {@link StepContext} used by {@link Simulator}.
 *
 * <p>Wraps the current {@link SimulatorState} to answer environmental queries,
 * and collects entities spawned during a step in an internal list.  Once all
 * actors have acted, {@link Simulator} calls {@link #getSpawned()} to merge
 * the new entities into the main population — no shared mutable list is
 * threaded through {@code act} calls.</p>
 */
class SimulationStepContext implements StepContext
{
    private final SimulatorState state;
    private final List<Simulatable> spawned = new ArrayList<>();

    SimulationStepContext(SimulatorState state)
    {
        this.state = state;
    }

    @Override
    public double getActivityReduction()
    {
        return state.getAggregatedProbabilityReduction();
    }

    @Override
    public Weather getCurrentWeather()
    {
        return state.getCurrentWeather();
    }

    /**
     * Returns the population counter for the given class.
     * If no count has been recorded yet (e.g. at startup), returns a
     * zero-count {@link Counter} so callers never need to null-check.
     */
    @Override
    public Counter getPopulationCount(Class entityClass)
    {
        Counter c = state.getCurrentStats(entityClass);
        return c != null ? c : new Counter(entityClass.getName(), 0);
    }

    @Override
    public void spawn(Simulatable newborn)
    {
        spawned.add(newborn);
    }

    /**
     * Returns all entities spawned during this step, in registration order.
     * Called once by {@link Simulator} after all actors have acted.
     */
    List<Simulatable> getSpawned()
    {
        return Collections.unmodifiableList(spawned);
    }
}
