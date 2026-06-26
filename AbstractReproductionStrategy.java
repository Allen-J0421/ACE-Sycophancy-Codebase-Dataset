import java.util.List;
import java.util.Random;

/**
 * Shared reproduction helpers for animal reproduction strategies.
 */
public abstract class AbstractReproductionStrategy implements ReproductionStrategy
{
    protected static final Random rand = Randomizer.getRandom();

    protected boolean isBreedingAge(Animal animal)
    {
        return animal.getAge() >= animal.getBreedingAge();
    }

    protected int generateBirthCount(Animal animal)
    {
        if(rand.nextDouble() <= animal.getBreedingProbability()) {
            return rand.nextInt(animal.getMaxLitterSize()) + 1;
        }
        return 0;
    }

    protected void addOffspring(Animal animal, List<Actor> newAnimals, int births)
    {
        Field field = animal.getField();
        List<Location> free = field.getFreeAdjacentLocations(animal.getLocation());
        for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            Animal.Gender sex = Randomizer.getRandomSex();
            Animal offspring = animal.createOffspring(field, loc, sex);
            offspring.setMovementStrategy(animal.getMovementStrategy());
            offspring.setReproductionStrategy(animal.getReproductionStrategy());
            newAnimals.add(offspring);
        }
    }
}
