import java.util.List;

/**
 * Represents all aspects of the simulator that perform some functionality
 * with each step (organisms, water, weather, diseases)
 *
 * @version 2022.02.25
 */
interface Actor
{
    /**
     * Make this actor act - that is: make it do
     * whatever it wants/needs to do.
     * @param newActors A list to receive any actors relevant to action
     */
    abstract public void act(List<Actor> actorsList);
}