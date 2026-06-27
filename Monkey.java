/**
 * A model of a monkey. Monkeys will eat plants.
 * They will move and look for food and age at every step of the simulation,
 * and die when they reach their max age or have gone too long without food.
 *
 * @version 2022.03.02
 */
public class Monkey extends Animal
{
    // Characteristics shared by all monkeys.
    private static final AnimalTraits TRAITS = new AnimalTraits.Builder()
            .breedingAge(4)
            .maxAge(40)
            .breedingProbability(0.17)
            .maxLitterSize(5)
            .maxHealth(10)
            .diurnal(true)
            .foodSources(Plant.class)
            .killable(Plant.class)
            .build();

    /**
     * Create a new monkey. A monkey may be created with age
     * zero (a new born) or with a random age.
     *
     * @param randomAge If true, the monkey will have a random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Monkey(boolean randomAge, Field field, Location location)
    {
        super(randomAge, field, location);
    }

    /**
     * @return the fixed species characteristics shared by all monkeys.
     */
    protected AnimalTraits getTraits()
    {
        return TRAITS;
    }

    /**
     * Creates a new Animal object for a newborn monkey
     *
     * @return an Animal object for a newborn monkey
     */
    protected Animal createNewOrganism(boolean randomAge, Field field, Location location)
    {
        return new Monkey(randomAge, field, location);
    }
}
