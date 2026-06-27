/**
 * A model of a hippopotamus. Hippopotamus will eat monkeys and plants,
 * but will kill monkeys, plants and bears.
 * They will move and look for food and age at every step of the simulation,
 * and die when they reach their max age or have gone too long without food.
 *
 * @version 2022.03.02
 */
public class Hippopotamus extends Animal
{
    // Characteristics shared by all hippopotamuses.
    private static final AnimalTraits TRAITS = new AnimalTraits.Builder()
            .breedingAge(25)
            .maxAge(120)
            .breedingProbability(0.12)
            .maxLitterSize(3)
            .maxHealth(100)
            .diurnal(true)
            .foodSources(Monkey.class, Plant.class)
            .killable(Monkey.class, Plant.class, Bear.class)
            .build();

    /**
     * Create a hippopotamus. A hippopotamus can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     *
     * @param randomAge If true, the hippopotamus will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Hippopotamus(boolean randomAge, Field field, Location location)
    {
        super(randomAge, field, location);
    }

    /**
     * @return the fixed species characteristics shared by all hippopotamuses.
     */
    protected AnimalTraits getTraits()
    {
        return TRAITS;
    }

    /**
     * Creates and returns a new hippopotamus object
     *
     * @return Organism object of subclass hippopotamus
     */
    protected Organism createNewOrganism(boolean randomAge, Field field, Location location)
    {
        return new Hippopotamus(randomAge, field, location);
    }
}
