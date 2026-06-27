import java.util.List;

/**
 * Strategy interface for the behaviour an actor performs during a single phase
 * (time of day). Encapsulating a phase's behaviour behind this interface lets
 * the mapping of phase to behaviour be varied independently of Actor.act.
 *
 * @version (27/06/2026)
 */
@FunctionalInterface
public interface PhaseBehaviour
{
    /**
     * Perform this phase's behaviour.
     *
     * @param newActors A list to which newly born actors are added.
     */
    void act(List<Actor> newActors);
}
