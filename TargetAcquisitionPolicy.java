/**
 * Strategy for selecting a target location for a mobile forager.
 */
public interface TargetAcquisitionPolicy
{
    Location acquireTarget(MobileForager forager);
}
