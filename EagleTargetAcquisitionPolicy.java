/**
 * Eagle-specific target acquisition logic.
 */
public final class EagleTargetAcquisitionPolicy extends AbstractTargetAcquisitionPolicy
{
    private static final java.util.Set<Class<?>> DIET = java.util.Set.of(Deer.class, Coyote.class, Mouse.class);

    @Override
    public Location acquireTarget(MobileForager forager, Environment environment)
    {
        Eagle eagle = (Eagle) forager;
        if(environment.getWeather().getCurrentWeather() == WeatherType.RAINING) {
            return null;
        }

        contractDiseaseFromAdjacentOrganisms(eagle, occupant ->
                occupant != null && !(occupant instanceof Hunter));

        Location preyLocation = findAdjacentTarget(
                eagle,
                occupant -> occupant != null && DIET.contains(occupant.getClass())
        );
        if(preyLocation == null) {
            return null;
        }

        consumePrey(eagle, (Organism) eagle.getField().getObjectAt(preyLocation));
        return preyLocation;
    }
}
