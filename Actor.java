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
     * @param weather The current weather.
     * @param dayState The different state of the day.
     */
    void act(List<Actor> newActors, Weather weather, DayState dayState);
    
    /**
     * Indicates whether an actor is alive or not.
     * @return Boolean value indicating whether an actor is alive or not.
     */
    boolean isAlive();
}
