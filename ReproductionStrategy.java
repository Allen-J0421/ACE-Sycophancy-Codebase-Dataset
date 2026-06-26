import java.util.List;

/**
 * Strategy for producing offspring during an animal's action step.
 */
public interface ReproductionStrategy
{
    void reproduce(Animal animal, List<Actor> newAnimals, Environment environment);
}
