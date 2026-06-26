/**
 * Strategy for selecting a target location for an organism.
 */
public interface TargetAcquisitionPolicy
{
    Location acquireTarget(Organism forager, Environment environment);
}
