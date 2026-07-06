/**
 * Centralized lifecycle manager for simulation actors.
 * Applies age and hunger ticks before each act() call so that Actor subclasses
 * can focus purely on behavioural logic.
 *
 * Carcasses are exempt: they have no aging or hunger in the simulation model.
 */
public class ActorLifecycle
{
    /**
     * Apply one lifecycle tick to the given actor.
     * Increments age for Consumers and Producers (killing if the maximum is
     * exceeded), then decrements sustenance for Consumers (killing if depleted).
     *
     * @param actor The actor to tick.
     */
    public void tick(Actor actor)
    {
        if (actor instanceof Consumer)
        {
            tickAge(actor);
            if (actor.getIsAlive()) tickHunger((Consumer) actor);
        }
        else if (actor instanceof Producer)
        {
            tickAge(actor);
        }
        // Carcass: no age or hunger lifecycle
    }

    private void tickAge(Actor actor)
    {
        actor.currentAge++;
        if (actor.currentAge > actor.maxAge)
        {
            actor.setDead();
        }
    }

    private void tickHunger(Consumer consumer)
    {
        consumer.incrementHunger();
    }
}
