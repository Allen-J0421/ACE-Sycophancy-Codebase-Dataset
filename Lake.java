import java.util.Random;

/**
 * A simple model of a lake - a type of water source.
 *
 * @version 2022.02.28
 */
public class Lake extends WaterSources
{
    private static final Random rand = Randomizer.getRandom();

    /**
     * Constructor for objects of class Lake
     *
     * @param randomVolume If true, the lake will have a random volume
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Lake(boolean randomVolume, Field field, Location location)
    {
        super(randomVolume, field, location);
        if(randomVolume) {
            this.setVolume(rand.nextInt(1000));
        }
    }

    public int getWaterValue() {
        return SimulationConfiguration.LAKE_WATER_VALUE;
    }
}
