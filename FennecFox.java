/**
 * A simple model of a fennec fox.
 * Fennec foxes age, move, eat mice, and die.
 *
 * @version 2016.02.29 (2)
 */
public class FennecFox extends Animal
{
    /**
     * Create a fennec fox. A fennec fox can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     *
     * @param randomAge If true, the fennec fox will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public FennecFox(boolean randomAge, Field field, Location location)
    {
        super(AnimalConfig.builder()
                  .breedingAge(12).maxAge(100).breedingProbability(0.5)
                  .maxLitterSize(2).foodValue(12).startingFoodLevel(12)
                  .huntProbability(0.6).nocturnal(true)
                  .prey("Grass", "Mouse").build(),
              randomAge, field, location);
    }

    /**
     * Create a new-born fennec fox.
     */
    protected Animal createOffspring(Field field, Location location)
    {
        return new FennecFox(false, field, location);
    }
}
