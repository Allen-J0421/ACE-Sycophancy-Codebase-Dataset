/**
 * Receives newly created actors during a simulation step.
 */
public interface ActorSink
{
    void add(Actor actor);
}
