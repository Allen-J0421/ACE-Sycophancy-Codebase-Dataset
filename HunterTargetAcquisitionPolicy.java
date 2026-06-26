/**
 * Hunter-specific target acquisition logic.
 */
public final class HunterTargetAcquisitionPolicy extends AbstractTargetAcquisitionPolicy
{
    private static final java.util.Set<Class<?>> DIET = java.util.Set.of(
            Deer.class, Mouse.class, Wolf.class, Coyote.class, Eagle.class
    );
    private static final RemovalPolicy REMOVAL_POLICY = new StandardRemovalPolicy();

    @Override
    public Location acquireTarget(Organism forager, Environment environment)
    {
        Hunter hunter = (Hunter) forager;
        Location preyLocation = findAdjacentTarget(
                hunter,
                occupant -> occupant != null && DIET.contains(occupant.getClass())
        );
        if(preyLocation == null) {
            return null;
        }

        Animal food = (Animal) hunter.getField().getObjectAt(preyLocation);
        REMOVAL_POLICY.remove(food);
        return preyLocation;
    }
}
