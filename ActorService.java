import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Centralizes actor lifecycle management and per-step update sequencing.
 */
public class ActorService
{
    private final SimulationContext context;
    private final List<Actor> actors;
    private final Field field;

    public ActorService(SimulationContext context, Field field)
    {
        this.context = context;
        this.actors = new ArrayList<>();
        this.field = field;
    }

    public void reset()
    {
        actors.clear();
        actors.addAll(context.getOrganismFactory().populate(field));
    }

    public void updateActors(Environment environment, int step)
    {
        List<Actor> newActors = new ArrayList<>();
        for(Iterator<Actor> iterator = actors.iterator(); iterator.hasNext(); ) {
            Actor actor = iterator.next();
            updateActor(actor, newActors, environment);
            maybeGrowPlant(actor, step);
            removeIfDead(actor, iterator);
        }

        actors.addAll(newActors);
        actors.addAll(context.getOrganismFactory().createGrassPatches(field, environment));
    }

    private void updateActor(Actor actor, List<Actor> newActors, Environment environment)
    {
        if(actor instanceof Animal animal) {
            if(!animal.isAwake(environment)) {
                return;
            }
            if(!context.getDiseaseService().processPreAct(animal)) {
                return;
            }
        }

        if(actor.isAlive()) {
            actor.act(newActors, environment);
            if(actor instanceof Animal animal) {
                context.getDiseaseService().processPostAct(animal);
            }
        }
    }

    private void maybeGrowPlant(Actor actor, int step)
    {
        if(actor instanceof Plant plant && step % plant.getStepsPerStage() == 0) {
            plant.incrementGrowth();
        }
    }

    private void removeIfDead(Actor actor, Iterator<Actor> iterator)
    {
        if(!actor.isAlive()) {
            if(actor instanceof Organism organism) {
                context.getDiseaseService().unregister(organism);
            }
            iterator.remove();
        }
    }
}
