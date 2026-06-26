import java.util.*;

/**
 * A simple model of an eagle.
 * Eagles age, move, contract diseases, eat prey, and die.
 * Eagles do not hunt in rainy weather and are not killed by overcrowding.
 *
 * @version 2022.03.02
 */
public class Eagle extends Animal
{
    private static final AnimalStats STATS = new AnimalStats(
        15,   // breedingAge
        2,    // maxLitterSize
        0.4,  // breedingProbability
        150,  // maxAge
        14,   // maxFoodLevel
        5,    // foodValue
        Set.of(Deer.class, Coyote.class, Mouse.class),
        false // diurnal
    );

    @Override
    protected AnimalStats getStats() { return STATS; }

    /**
     * Create an eagle. An eagle can be created as a new born (age zero and not hungry)
     * or with a random age and food level.
     * @param randomAge If true, the eagle will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     * @param sex The gender of the eagle.
     */
    public Eagle(boolean randomAge, Field field, Location location, Gender sex)
    {
        super(field, location, randomAge, sex);
    }

    @Override
    protected Animal createOffspring(Field field, Location location, Gender sex)
    {
        return new Eagle(false, field, location, sex);
    }

    /**
     * Eagles act like other animals but do not die from overcrowding (they fly above).
     */
    @Override
    public void act(List<Actor> newAnimals, Environment environment)
    {
        if (!isAwake(environment)) {
            return;
        }
        if (isDiseased() && getDisease().getPropagationRate() <= rand.nextDouble()) {
            setDead();
            return;
        }
        randomlyContractDisease();
        incrementAge();
        incrementHunger();
        if (isAlive()) {
            giveBirth(newAnimals, environment);
            Location newLocation = findFood(environment);
            if (newLocation == null) {
                newLocation = getField().freeAdjacentLocation(getLocation());
            }
            List<Location> adjacentGrassSpots = getField().adjacentLocationsWithSpecies(getLocation(), Grass.class);

            if (newLocation != null) {
                setLocation(newLocation);
            } else if (adjacentGrassSpots.size() > 0) {
                getField().clear(getLocation());
                setLocation(adjacentGrassSpots.get(rand.nextInt(adjacentGrassSpots.size())));
            }
            // Eagles are not killed by overcrowding — they fly above the ground

            if (isDiseased() && getDisease().getLethalityRate() <= rand.nextDouble()) {
                setDead();
            }
        }
    }

    /**
     * Eagles do not hunt in rainy weather.
     * @return Where food was found, or null if it wasn't (or if raining).
     */
    protected Location findFood(Environment environment)
    {
        if (environment.getWeather().getCurrentWeather() != WeatherType.RAINING) {
            return super.findFood();
        }
        return null;
    }
}
