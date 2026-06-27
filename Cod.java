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
    public Cod(boolean randomAge, Field field, Location location)
    {
        super(randomAge, field, location);
    }
}
