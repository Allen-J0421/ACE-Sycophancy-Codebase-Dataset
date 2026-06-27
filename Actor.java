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

    /**
     * Respond to rain. Physical entities gain water/volume; other actors
     * (weather, disease) ignore it.
     */
    default void onRain() { }

    /**
     * Respond to fog. Physical entities react in their own way; other
     * actors ignore it.
     */
    default void onFog() { }

    /**
     * Respond to a heatwave. Physical entities lose water/volume; other
     * actors ignore it.
     */
    default void onHeatwave() { }

    /**
     * Whether this actor is allowed to act at the current time of day.
     * Most actors always act; nocturnal animals are the exception.
     * @param isNight Whether it is currently night time.
     * @return true if the actor may act this step.
     */
    default boolean canActNow(boolean isNight) { return true; }

    /**
     * Whether this actor has finished participating in the simulation and
     * should be removed from the actor list (a dead organism, an emptied
     * water source). Most actors persist.
     * @return true if the actor should be removed.
     */
    default boolean isExpired() { return false; }
}