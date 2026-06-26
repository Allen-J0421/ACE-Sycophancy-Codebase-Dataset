import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Centralizes actor lifecycle management and per-step update sequencing.
 */
public class ActorService
{
    private final List<Actor> actors;
    private final Field field;
    private final OrganismFactory organismFactory;
    private final DiseaseService diseaseService;

    public ActorService(Field field, OrganismFactory organismFactory, DiseaseService diseaseService)
    {
        this.actors = new ArrayList<>();
        this.field = field;
        this.organismFactory = organismFactory;
        this.diseaseService = diseaseService;
    }

    public void reset()
    {
        actors.clear();
        actors.addAll(organismFactory.populate(field));
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
        actors.addAll(organismFactory.createGrassPatches(field, environment));
    }

    private void updateActor(Actor actor, List<Actor> newActors, Environment environment)
    {
        if(actor instanceof Animal animal) {
            if(!animal.isAwake(environment)) {
                return;
            }
            if(!diseaseService.processPreAct(animal)) {
                return;
            }
        }

        if(actor.isAlive()) {
            actor.act(newActors, environment);
            if(actor instanceof Animal animal) {
                diseaseService.processPostAct(animal);
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
                diseaseService.unregister(organism);
            }
            iterator.remove();
        }
    }
}
