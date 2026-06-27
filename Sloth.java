/**
 * A model of a Sloth. Sloths will eat plants.
 * They will move and look for food and age at every step of the simulation,
 * and die when they reach their max age or have gone too long without food.
 *
 * @version 2022.03.02
 */
public class Sloth extends InfectableAnimal
{
    // Characteristics shared by all sloths.
    private static final AnimalTraits TRAITS = new AnimalTraits.Builder()
            .breedingAge(5)
            .maxAge(30)
            .breedingProbability(0.17)
            .maxLitterSize(4)
            .maxHealth(8)
            .diurnal(true)
            .foodSources(Plant.class)
            .killable(Plant.class)
            .build();

    /**
     * Create a new sloth. A sloth may be created with age
     * zero (a new born) or with a random age.
     *
     * @param randomAge If true, the sloth will have a random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Sloth(boolean randomAge, Field field, Location location)
    {
        super(randomAge, field, location);
    }

    /**
     * @return the fixed species characteristics shared by all sloths.
     */
    protected AnimalTraits getTraits()
    {
        return TRAITS;
    }

    /**
     * Creates a new Animal object for a newborn sloth
     *
     * @return an Animal object for a newborn sloth
     */
    protected Animal createNewOrganism(boolean randomAge, Field field, Location location)
    {
        return new Sloth(randomAge, field, location);
    }
}
