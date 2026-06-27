/**
 * A model of a leopard. Leopards will eat sloths,
 * but will kill sloths and plants.
 * They will move and look for food, age at every step of the simulation,
 * and die when they reach their max age or have gone too long without food.
 *
 * @version 2022.03.02
 */
public class Leopard extends Animal
{
    // Characteristics shared by all leopards.
    private static final AnimalTraits TRAITS = new AnimalTraits.Builder()
            .breedingAge(15)
            .maxAge(55)
            .breedingProbability(0.145)
            .maxLitterSize(4)
            .maxHealth(40)
            .diurnal(false)
            .foodSources(Sloth.class)
            .killable(Sloth.class, Plant.class)
            .build();

    /**
     * Create a leopard. A leopard can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     *
     * @param randomAge If true, the leopard will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Leopard(boolean randomAge, Field field, Location location)
    {
        super(randomAge, field, location);
    }

    /**
     * @return the fixed species characteristics shared by all leopards.
     */
    protected AnimalTraits getTraits()
    {
        return TRAITS;
    }
}
