import java.util.List;
import java.util.ArrayList;

/**
 * Collects the organisms born during a single simulation step.
 *
 * Acting organisms register their offspring here as they breed, and the simulator
 * drains the collected newborns into the field's population once every organism
 * has acted. This gives both the {@link Simulator} and the {@link Organism}
 * hierarchy a single, shared mechanism for handling newly born organisms, in
 * place of the bare {@code List<Organism>} that was previously threaded through
 * every {@code act}/{@code giveBirth} call.
 *
 * @version 2022.03.02
 */
public class Nursery
{
    // The organisms born during the current simulation step.
    private final List<Organism> newborns = new ArrayList<>();

    /**
     * Record a newly born organism produced during this step.
     * @param organism The newborn organism to collect.
     */
    public void register(Organism organism)
    {
        newborns.add(organism);
    }

    /**
     * Return the organisms collected during this step.
     * @return The list of newborn organisms.
     */
    public List<Organism> getNewborns()
    {
        return newborns;
    }
}
