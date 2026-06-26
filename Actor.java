/**
 * Interface Actor that gathers the similarities of plants and animals.
 *
 * @version 1.0
 */
public interface Actor
{
    ActorType getActorType();

    /**
     * Describes the behaviour of an actor within a step.
     *
     * @param newActors Receives newly generated actors - that is animals or plants.
     * @param weather The current weather.
     * @param dayState The different state of the day.
     */
    public void act(ActorSink newActors, Weather weather, DayState dayState);
    
    /**
     * Indicates whether an actor is alive or not.
     * @return Boolean value indicating whether an actor is alive or not.
     */
    public boolean isAlive();
}
