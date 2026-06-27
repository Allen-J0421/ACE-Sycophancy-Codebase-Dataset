/**
 * A model of a bear. Bears will eat monkeys,
 * but will kill monkeys and plants.
 * They will move and look for food and age at every step of the simulation,
 * and die when they reach their max age or have gone too long without food.
 *
 * @version 2022.03.02
 */
public class Bear extends InfectableAnimal
{
    // Characteristics shared by all bears.
    private static final AnimalTraits TRAITS = new AnimalTraits.Builder()
            .breedingAge(15)
            .maxAge(70)
            .breedingProbability(0.125)
            .maxLitterSize(4)
            .maxHealth(40)
            .diurnal(true)
            .foodSources(Monkey.class)
            .killable(Monkey.class, Plant.class)
            .build();

    /**
     * Create a bear. A bear can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     *
     * @param randomAge If true, the bear will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Bear(boolean randomAge, Field field, Location location)
    {
        super(randomAge, field, location);
    }

    /**
     * @return the fixed species characteristics shared by all bears.
     */
    protected AnimalTraits getTraits()
    {
        return TRAITS;
    }

    /**
     * Creates and returns a new bear object
     *
     * @return Organism object of subclass bear
     */
    protected Organism createNewOrganism(boolean randomAge, Field field, Location location)
    {
        return new Bear(randomAge, field, location);
    }
}
