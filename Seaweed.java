/**
 * A simple model of seaweed.
 *
 * Seaweed ages, consumes oxygen during the night and produces oxygen during the daytime,
 * propagates, and may die because of environment conditions or weather.
 *
 * @version 2022/03/02
 */
public class Seaweed extends Plant
{
    private static final PlantProfile PROFILE =
        new PlantProfile(20, 40, 1, 0.00000002, 0.0000005,
            (field, location) -> new Seaweed(false, field, location));

    public Seaweed(boolean randomAge, Field field, Location location)
    {
        super(randomAge, field, location, PROFILE);
    }
}
