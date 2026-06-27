/**
 * A model of a Sloth. Sloths will eat plants.
 * They will move and look for food and age at every step of the simulation,
 * and die when they reach their max age or have gone too long without food.
 * 
 * @version 2022.03.02
 */
public class Sloth extends InfectiousAnimal
{
    private static final AnimalAttributes ATTRIBUTES =
        new AnimalAttributes(Species.SLOTH, true, 5, 30, 0.17, 4, 8,
                             Sloth::new,
                             AnimalAttributes.speciesSet(Species.PLANT),
                             OrganismBehaviors.MATE_REQUIRED_BREEDING,
                             OrganismBehaviors.FORAGE_OR_WANDER,
                             OrganismBehaviors.INCREMENT_AGE,
                             OrganismBehaviors.DECAY_HEALTH,
                             OrganismBehaviors.APPLY_ILLNESS,
                             OrganismBehaviors.SPREAD_DISEASE,
                             OrganismBehaviors.GIVE_BIRTH,
                             OrganismBehaviors.RELOCATE);

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
        super(randomAge, field, location, ATTRIBUTES);
    }
}
