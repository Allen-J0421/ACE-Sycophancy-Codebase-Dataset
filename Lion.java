/**
 * A simple model of a lion.
 * Lions age, move, find food (gazelles), find water, find mates, breed and die.
 *
 * @version 2022.02.27
 */
public class Lion extends Animal
{
    /**
     * Create a lion. A lion can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     *
     * @param randomAge If true, the lion will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Lion(boolean randomAge, Field field, Location location)
    {
        super(SpeciesRegistry.get("Lion"), randomAge, field, location);
    }

    /**
     * Create a new-born lion.
     */
    protected Animal createOffspring(Field field, Location location)
    {
        return new Lion(false, field, location);
    }
}
