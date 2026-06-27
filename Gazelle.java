/**
 * A simple model of a gazelle
 * Gazelles age, move, find water, find food(grass), find mate, breed, and die.
 *
 * @version 2022.02.27
 */
public class Gazelle extends Animal
{
    /**
     * Create a new gazelle. A gazelle may be created with age
     * zero (a new born) or with a random age.
     *
     * @param randomAge If true, the gazelle will have a random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Gazelle(boolean randomAge, Field field, Location location)
    {
        super(AnimalConfig.builder()
                  .breedingAge(10).maxAge(45).breedingProbability(0.5)
                  .maxLitterSize(4).foodValue(20).startingFoodLevel(20)
                  .nocturnal(false)
                  .prey("Grass").build(),
              randomAge, field, location);
    }

    /**
     * Create a new-born gazelle.
     */
    protected Animal createOffspring(Field field, Location location)
    {
        return new Gazelle(false, field, location);
    }
}
