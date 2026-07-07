import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
/**
 * Manages the lifecycle of all actors (animals and plants) in the simulation.
 * Responsible for iterating actor updates, removing the dead, and integrating newborns.
 *
 * @version 1.0
 */
public class ActorManager
{

    /*///////////////////////////////////////////////////////////////
                                 STATE
    //////////////////////////////////////////////////////////////*/

    private final List<Actor> animals;
    private final List<Actor> plants;

    /*///////////////////////////////////////////////////////////////
                              CONSTRUCTOR
    //////////////////////////////////////////////////////////////*/

    /**
     * Creates a new ActorManager with empty actor lists.
     */
    public ActorManager()
    {
        animals = new ArrayList<>();
        plants = new ArrayList<>();
    }

    /*///////////////////////////////////////////////////////////////
                          ACTOR LIFECYCLE LOGIC
    //////////////////////////////////////////////////////////////*/

    /**
     * Advances all actors by one step. Plants act first, then animals.
     * Newly born actors are appended to their respective lists after each group acts.
     *
     * @param weather  The current weather for this step.
     * @param dayState The current day/night state for this step.
     */
    public void stepAll(Weather weather, DayState dayState)
    {
        List<Actor> newPlants = new ArrayList<>();
        List<Actor> newAnimals = new ArrayList<>();
        actorsAct(plants, newPlants, weather, dayState);
        actorsAct(animals, newAnimals, weather, dayState);
    }

    /**
     * Clears all actor lists, preparing for a fresh population.
     */
    public void clear()
    {
        animals.clear();
        plants.clear();
    }

    /*///////////////////////////////////////////////////////////////
                              ACCESSORS
    //////////////////////////////////////////////////////////////*/

    /**
     * Returns the list of animals currently in the simulation.
     *
     * @return the animal list.
     */
    public List<Actor> getAnimals()
    {
        return animals;
    }

    /**
     * Returns the list of plants currently in the simulation.
     *
     * @return the plant list.
     */
    public List<Actor> getPlants()
    {
        return plants;
    }

    /*///////////////////////////////////////////////////////////////
                              PRIVATE HELPERS
    //////////////////////////////////////////////////////////////*/

    /**
     * Makes each actor in the list act, removes those that die, then appends newborns.
     *
     * @param actors    The list of actors to update.
     * @param newActors An empty list to receive newly born actors.
     * @param weather   The current weather.
     * @param dayState  The current day/night state.
     */
    private void actorsAct(List<Actor> actors, List<Actor> newActors, Weather weather, DayState dayState)
    {
        for(Iterator<Actor> it = actors.iterator(); it.hasNext(); ) {
            Actor actor = it.next();
            actor.act(newActors, weather, dayState);
            if(!actor.isAlive()) {
                it.remove();
            }
        }
        actors.addAll(newActors);
    }
}
