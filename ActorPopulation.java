import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Tracks a homogeneous population of actors and applies per-step updates.
 */
public class ActorPopulation<T extends Actor>
{
    private final Class<T> actorType;
    private final List<T> actors;

    public ActorPopulation(Class<T> actorType)
    {
        this.actorType = actorType;
        this.actors = new ArrayList<>();
    }

    public void add(T actor)
    {
        actors.add(actor);
    }

    public void clear()
    {
        actors.clear();
    }

    public int size()
    {
        return actors.size();
    }

    public T get(int index)
    {
        return actors.get(index);
    }

    public void simulateStep(Weather weather, DayState dayState)
    {
        List<Actor> newborns = new ArrayList<>();
        for(Iterator<T> it = actors.iterator(); it.hasNext(); ) {
            T actor = it.next();
            actor.act(newborns, weather, dayState);
            if(!actor.isAlive()) {
                it.remove();
            }
        }
        appendNewActors(newborns);
    }

    private void appendNewActors(List<Actor> newborns)
    {
        for (Actor actor : newborns) {
            if(!actorType.isInstance(actor)) {
                throw new IllegalStateException(
                    "Expected newborn actor of type " + actorType.getSimpleName()
                        + " but got " + actor.getClass().getSimpleName()
                );
            }
            actors.add(actorType.cast(actor));
        }
    }
}
