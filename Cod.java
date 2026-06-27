/**
 * A simple model of a cod.
 *
 * Cods age, move, eat seaweed, consume oxygen, propogate,
 * and may get infected by disease and die of that or of weather.
 *
 * @version 2022/03/02
 */
public class Cod extends ForagingAnimal
{
    private static final ForagingBehavior BEHAVIOR = new ForagingBehavior(
        6, 50, 0.3, 10, 13, 1, Cod.class, 1, true, Seaweed.class);

    public Cod(boolean randomAge, Field field, Location location)
    {
        super(BEHAVIOR, randomAge, field, location);
    }

    @Override
    protected Creature createOffspring(Field field, Location location)
    {
        return new Cod(false, field, location);
    }
}
