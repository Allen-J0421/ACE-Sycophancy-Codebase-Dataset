import java.util.List;

/**
 * An entity that participates in the simulation's per-step lifecycle.
 * SimulationEngine calls tick() on every actor each step; each implementing
 * class encapsulates its own lifecycle rules (sleep/wake, growth, etc.)
 * so the engine requires no knowledge of specific actor types.
 *
 * @version 2022.03.02
 */
public interface Tickable
{
    /**
     * Execute one lifecycle step for this entity.
     *
     * @param step       The current global simulation step number.
     * @param newActors  List to receive any new actors spawned this step.
     * @param environment The current environment (time, weather).
     */
    void tick(int step, List<Actor> newActors, Environment environment);
}
