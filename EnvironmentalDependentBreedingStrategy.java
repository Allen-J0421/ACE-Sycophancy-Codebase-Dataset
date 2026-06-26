import java.util.List;
import java.util.Objects;

/**
 * Reproduction strategy that delegates only when configured environment conditions are met.
 */
public class EnvironmentalDependentBreedingStrategy implements ReproductionStrategy
{
    private final ReproductionStrategy delegate;
    private final WeatherType requiredWeather;
    private final Boolean requiresDay;

    public EnvironmentalDependentBreedingStrategy()
    {
        this(new StandardSexualReproductionStrategy(), null, Boolean.TRUE);
    }

    public EnvironmentalDependentBreedingStrategy(ReproductionStrategy delegate, WeatherType requiredWeather, Boolean requiresDay)
    {
        this.delegate = Objects.requireNonNull(delegate, "delegate");
        this.requiredWeather = requiredWeather;
        this.requiresDay = requiresDay;
    }

    @Override
    public void reproduce(Animal animal, List<Actor> newAnimals, Environment environment)
    {
        if(environmentAllowsBreeding(environment)) {
            delegate.reproduce(animal, newAnimals, environment);
        }
    }

    private boolean environmentAllowsBreeding(Environment environment)
    {
        if(requiredWeather != null && environment.getWeather().getCurrentWeather() != requiredWeather) {
            return false;
        }
        return requiresDay == null || environment.getTime().isDay() == requiresDay.booleanValue();
    }
}
