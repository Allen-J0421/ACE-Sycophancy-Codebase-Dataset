import java.util.Random;

/**
 * A simple model of a lake - a type of water source.
 *
 * @version 2022.02.28
 */
public class Lake extends WaterSources
{
    // Characteristics shared by all Lake (class variables).
    
    // A shared random number generator to generate a random volume of the
    // water source when created.
    private static final Random rand = Randomizer.getRandom();
    // The food value of drinking a drop of water.
    private static final int LAKE_WATER_VALUE = 15;

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

    /**
     * @return The water value of the lake.
     */
    public int getWaterValue() {
        return LAKE_WATER_VALUE;
    }
}
