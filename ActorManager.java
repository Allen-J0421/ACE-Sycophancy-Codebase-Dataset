import java.util.*;

/**
 * Owns the live actor collection and drives one simulation tick.
 * Each tick: every actor acts (potentially spawning offspring into a staging list),
 * dead actors are removed, then offspring are merged into the live list.
 * Centralises actor lifecycle so the simulation engine loop stays policy-free.
 *
 * @version 2022.03.02
 */
public class ActorManager
{
    private final List<Actor> actors = new ArrayList<>();

    /**
     * Add a single actor to the live collection.
     * Used by FieldPopulator during initial population and rain-driven grass growth.
     * @param actor The actor to add.
     */
    public void add(Actor actor)
    {
        actors.add(actor);
    }

    /** Remove all actors; called during simulation reset before re-population. */
    public void clear()
    {
        actors.clear();
    }

    /**
     * Run one lifecycle tick over every live actor.
     * Each actor's {@code act()} method is called; actors that die during the tick
     * are removed immediately, and all newly spawned actors are appended at the end.
     * @param environment The current simulation environment.
     */
    public void tick(Environment environment)
    {
        List<Actor> offspring = new ArrayList<>();
        for (Iterator<Actor> it = actors.iterator(); it.hasNext(); ) {
            Actor actor = it.next();
            actor.act(offspring, environment);
            if (!actor.isAlive()) {
                it.remove();
            }
        }
        actors.addAll(offspring);
    }
}
