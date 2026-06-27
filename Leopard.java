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
    private static final AnimalAttributes ATTRIBUTES =
        ConfiguredAttributes.animal(Species.LEOPARD, Leopard::new,
                                    OrganismBehaviors.MATE_REQUIRED_BREEDING,
                                    OrganismBehaviors.FORAGE_OR_WANDER,
                                    OrganismBehaviors.INCREMENT_AGE,
                                    OrganismBehaviors.DECAY_HEALTH,
                                    OrganismBehaviors.GIVE_BIRTH,
                                    OrganismBehaviors.RELOCATE);

    /**
     * Create a bear. A bear can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the bear will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Leopard(boolean randomAge, Field field, Location location)
    {
        super(randomAge, field, location, ATTRIBUTES);
    }
}
