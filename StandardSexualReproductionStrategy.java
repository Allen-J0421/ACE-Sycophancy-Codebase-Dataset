import java.util.List;

/**
 * Reproduction strategy that requires breeding age and an opposite-sex adjacent mate.
 */
public class StandardSexualReproductionStrategy extends AbstractReproductionStrategy
{
    @Override
    public void reproduce(Animal animal, List<Actor> newAnimals, Environment environment)
    {
        List<Organism> potentialMates = animal.getPotentialMates();
        if(!isBreedingAge(animal) || potentialMates.isEmpty()) {
            return;
        }

        int births = generateBirthCount(animal);
        if(births == 0) {
            return;
        }

        Animal mate = (Animal) potentialMates.get(rand.nextInt(potentialMates.size()));
        if(mate.isDiseased() && mate.getDisease().getDiseaseType() == DiseaseType.SEXUAL) {
            animal.setDisease(mate.getDisease());
        }
        addOffspring(animal, newAnimals, births);
    }
}
