import java.util.EnumMap;
import java.util.List;

/**
 * The Strategy context for an actor's phase behaviour. It holds the behaviour
 * to run for each Phase, so the simulation can swap day/night schedules - or
 * plug in entirely new ones - without changing the Actor.act control flow.
 *
 * @version (27/06/2026)
 */
public class Schedule
{
    // The behaviour to run for each phase.
    private final EnumMap<Phase, PhaseBehaviour> behaviours = new EnumMap<>(Phase.class);

    /**
     * Register the behaviour to run during a given phase.
     *
     * @param phase     The phase the behaviour applies to.
     * @param behaviour The behaviour to run during that phase.
     * @return This schedule, so calls can be chained.
     */
    public Schedule on(Phase phase, PhaseBehaviour behaviour)
    {
        behaviours.put(phase, behaviour);
        return this;
    }

    /**
     * Run the behaviour registered for the given phase. Does nothing if no
     * behaviour has been registered for that phase.
     *
     * @param phase     The current phase.
     * @param newActors A list to which newly born actors are added.
     */
    public void run(Phase phase, List<Actor> newActors)
    {
        PhaseBehaviour behaviour = behaviours.get(phase);
        if (behaviour != null) {
            behaviour.act(newActors);
        }
    }

    /**
     * Create a copy of this schedule with the behaviours of two phases
     * exchanged. This lets callers swap, for example, the day and night
     * routines without touching the actors that run them.
     *
     * @param first  The first phase.
     * @param second The second phase.
     * @return A new schedule with the two phases' behaviours swapped.
     */
    public Schedule swapped(Phase first, Phase second)
    {
        Schedule copy = new Schedule();
        copy.behaviours.putAll(behaviours);
        copy.behaviours.put(first, behaviours.get(second));
        copy.behaviours.put(second, behaviours.get(first));
        return copy;
    }
}
