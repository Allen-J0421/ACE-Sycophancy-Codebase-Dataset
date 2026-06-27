/**
 * A simple model of a shark.
 *
 * Sharks age, move, eat cod or salmon, consume oxygen, propogate,
 * and may get infected by disease and die of that or of weather.
 *
 * @version 2022/03/02
 */
public class Shark extends ForagingAnimal
{
    public Shark(boolean randomAge, Field field, Location location)
    {
        super(randomAge, field, location);
    }
}
