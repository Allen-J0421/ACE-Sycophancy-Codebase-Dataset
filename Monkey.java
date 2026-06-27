/**
 * A model of a monkey. Monkeys will eat plants.
 * They will move and look for food and age at every step of the simulation,
 * and die when they reach their max age or have gone too long without food.
 * 
 * @version 2022.03.02
 */
public class Monkey extends Animal
{
    private static final AnimalAttributes ATTRIBUTES =
        new AnimalAttributes(Species.MONKEY, true, 4, 40, 0.17, 5, 10,
                             Monkey::new,
                             AnimalAttributes.speciesSet(Species.PLANT),
                             OrganismBehaviors.MATE_REQUIRED_BREEDING,
                             OrganismBehaviors.FORAGE_OR_WANDER,
                             OrganismBehaviors.INCREMENT_AGE,
                             OrganismBehaviors.DECAY_HEALTH,
                             OrganismBehaviors.GIVE_BIRTH,
                             OrganismBehaviors.RELOCATE);

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
        super(randomAge, field, location, ATTRIBUTES);
    }
}
