import java.util.List;

/**
 * Reproduction strategy that does not require a mate.
 */
public class AsexualReproductionStrategy extends AbstractReproductionStrategy
{
    @Override
    public void reproduce(Animal animal, List<Actor> newAnimals, Environment environment)
    {
        if(!isBreedingAge(animal)) {
            return;
        }
        addOffspring(animal, newAnimals, generateBirthCount(animal));
    }
}
