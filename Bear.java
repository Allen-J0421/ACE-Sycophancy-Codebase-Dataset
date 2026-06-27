/**
 * A model of a bear. Bears will eat monkeys,
 * but will kill monkeys and plants.
 * They will move and look for food and age at every step of the simulation,
 * and die when they reach their max age or have gone too long without food.
 * 
 * @version 2022.03.02
 */
public class Bear extends InfectiousAnimal
{
    private static final AnimalAttributes ATTRIBUTES =
        new AnimalAttributes(Species.BEAR, true, 15, 70, 0.125, 4, 40,
                             Bear::new,
                             AnimalAttributes.speciesSet(Species.MONKEY));

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
        super(randomAge, field, location, ATTRIBUTES);
    }
}
