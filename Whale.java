/**
 * A simple model of a whale.
 *
 * Whales age, move, eat cod or salmon, consume oxygen, propogate,
 * and may get infected by disease and die of that or of weather.
 *
 * @version 2022/03/02
 */
public class Whale extends ForagingAnimal
{
    private static final ForagingBehavior BEHAVIOR = new ForagingBehavior(
        6, 150, 0.2, 8, 8, 1, Salmon.class, 2, false, Cod.class, Salmon.class);

    public Whale(boolean randomAge, Field field, Location location)
    {
        super(BEHAVIOR, randomAge, field, location);
    }

    @Override
    protected Creature createOffspring(Field field, Location location)
    {
        return new Whale(false, field, location);
    }
}
