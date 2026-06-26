/**
 * Eagle-specific target acquisition logic.
 */
public final class EagleTargetAcquisitionPolicy implements TargetAcquisitionPolicy
{
    private static final java.util.Set<Class<?>> DIET = java.util.Set.of(Deer.class, Coyote.class, Mouse.class);

    @Override
    public Location acquireTarget(MobileForager forager, Environment environment)
    {
        Eagle eagle = (Eagle) forager;
        if(environment.getWeather().getCurrentWeather() == WeatherType.RAINING) {
            return null;
        }

        Location preyLocation = AdjacentTargetSearch.findMatchingLocation(
                eagle.getField(),
                eagle.getLocation(),
                occupant -> occupant != null && DIET.contains(occupant.getClass())
        );
        if(preyLocation == null) {
            return null;
        }

        Organism food = (Organism) eagle.getField().getObjectAt(preyLocation);
        if (food.isDiseased()
                && food.getDisease().getDiseaseType() == DiseaseType.FOODBORNE
                && food.getDisease().getPropagationRate() <= Randomizer.getRandom().nextDouble())
        {
            eagle.setDisease(food.getDisease());
        }
        if(food.isAlive())
        {
            food.setDead();
            int newFoodLevel = eagle.foodLevel + ((Edible) food).getFoodValue();
            eagle.foodLevel = Math.min(newFoodLevel, eagle.MAX_FOOD_LEVEL());
        }
        return preyLocation;
    }
}
