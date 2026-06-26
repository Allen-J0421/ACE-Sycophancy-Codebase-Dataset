import java.util.List;
/**
 * Interface Actor that gathers the similarities of plants and animals.
 *
 * @version 1.0
 */
public interface Actor
{
    /**
     * Describes the behaviour of an actor within a step.
     *
     * @param newActors A list to receive newly generated actors - that is animals or plants.
     * @param step The current simulation step context.
     */
    public void act(List<Actor> newActors, SimulationStep step);
    
    /**
     * Indicates whether an actor is alive or not.
     * @return Boolean value indicating whether an actor is alive or not.
     */
    public boolean isAlive();
}
