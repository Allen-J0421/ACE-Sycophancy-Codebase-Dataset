/**
 * A simple model of a Salmon.
 *
 * Salmons age, move, eat seaweed, consume oxygen, propogate,
 * and may get infected by disease and die of that or of weather.
 *
 * @version 2022/03/02
 */
public class Salmon extends ForagingAnimal
{
    public Salmon(boolean randomAge, Field field, Location location)
    {
        super(randomAge, field, location);
    }
}
