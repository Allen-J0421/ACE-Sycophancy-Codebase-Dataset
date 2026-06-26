/**
 * Hunter-specific target acquisition logic.
 */
public final class HunterTargetAcquisitionPolicy implements TargetAcquisitionPolicy
{
    private static final java.util.Set<Class<?>> DIET = java.util.Set.of(
            Deer.class, Mouse.class, Wolf.class, Coyote.class, Eagle.class
    );

    @Override
    public Location acquireTarget(MobileForager forager)
    {
        Hunter hunter = (Hunter) forager;
        Location preyLocation = AdjacentTargetSearch.findMatchingLocation(
                hunter.getField(),
                hunter.getLocation(),
                occupant -> occupant != null && DIET.contains(occupant.getClass())
        );
        if(preyLocation == null) {
            return null;
        }

        Animal food = (Animal) hunter.getField().getObjectAt(preyLocation);
        if(food.isAlive()) {
            food.setDead();
        }
        return preyLocation;
    }
}
