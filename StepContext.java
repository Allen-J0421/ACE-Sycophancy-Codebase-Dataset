/**
 * Provides an actor with everything it needs during a single simulation step,
 * without exposing the full {@link SimulatorState}.
 *
 * <p>Implementors of {@link Simulatable#act(StepContext)} use this interface
 * to query environmental conditions, look up population statistics, and
 * register newly spawned entities.  Because the interface is narrow and
 * self-contained, actors can be tested in isolation by supplying a simple
 * stub implementation rather than a fully wired {@link SimulatorState}.</p>
 *
 * <p>The production implementation is {@link SimulationStepContext}.</p>
 */
interface StepContext
{
    /**
     * Returns the combined environmental activity multiplier for this step.
     * A value of 1.0 means full activity; lower values reduce the probability
     * that an actor takes any action.
     */
    double getActivityReduction();

    /**
     * Returns the current weather state, allowing actors to respond to
     * precipitation or other weather-dependent conditions.
     */
    Weather getCurrentWeather();

    /**
     * Returns the population counter for the given entity class at the
     * start of this step.  Never returns {@code null}; if no count has
     * been recorded yet a zero-count {@link Counter} is returned.
     * @param entityClass the class whose count is requested
     */
    Counter getPopulationCount(Class entityClass);

    /**
     * Registers a newly created entity to be added to the simulation after
     * the current step completes.  Replaces the old out-parameter pattern
     * where actors appended directly to a shared mutable list.
     * @param newborn the entity to add
     */
    void spawn(Simulatable newborn);
}
