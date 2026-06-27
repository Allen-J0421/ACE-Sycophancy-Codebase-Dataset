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
    public Whale(boolean randomAge, Field field, Location location)
    {
        super(randomAge, field, location);
    }
}
