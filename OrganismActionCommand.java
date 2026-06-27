import java.util.List;

/**
 * Command executed as part of an organism's act cycle.
 *
 * @version 2022.03.02
 */
public interface OrganismActionCommand
{
    void execute(Organism organism, List<Organism> newOrganisms);
}
