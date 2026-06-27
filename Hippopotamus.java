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
    private static final AnimalAttributes ATTRIBUTES =
        new AnimalAttributes(Species.HIPPOPOTAMUS, true, 25, 120, 0.12, 3, 100,
                             Hippopotamus::new,
                             AnimalAttributes.speciesSet(Species.MONKEY, Species.PLANT),
                             OrganismBehaviors.MATE_REQUIRED_BREEDING,
                             OrganismBehaviors.FORAGE_OR_WANDER,
                             OrganismBehaviors.INCREMENT_AGE,
                             OrganismBehaviors.DECAY_HEALTH,
                             OrganismBehaviors.GIVE_BIRTH,
                             OrganismBehaviors.RELOCATE);

    /**
     * Create a fox. A fox can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the fox will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Hippopotamus(boolean randomAge, Field field, Location location)
    {
        super(randomAge, field, location, ATTRIBUTES);
    }
}
