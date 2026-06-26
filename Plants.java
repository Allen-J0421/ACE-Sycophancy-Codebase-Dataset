/**
 * A class representing shared characteristics of plants.
 *
 * @version 2022.03.01
 */
public abstract class Plants extends Actor
{
    private static final int LATE_GROWTH_STEP_THRESHOLD = 1600;
    private static final int LATE_GROWTH_BONUS = 20;

    private int sunLevel; 
    private int waterLevel;
    /**
     * Create a new plant at location in field.
     *
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Plants(Field field, Location location)
    {
        super(field,location);
        sunLevel = getRandom().nextInt(10) + 1;
        waterLevel = getRandom().nextInt(10) + 1;
    }


    /**
     * Generate a number representing how much a plant grows,if it can grow.
     * @return The number of squares it grows into(may be zero).
     */
    protected int growth(int step)
    {
        int births = 0;
        if(getAge() >= getBreedingAge() && getAge() % 2 == 0 ) {
            births = getRandom().nextInt(getMaxLitter()) + 1;
        }
        
        if(step > LATE_GROWTH_STEP_THRESHOLD){
            births = births + LATE_GROWTH_BONUS;
        }
        return births;
    }

    /**
     * Get growing age of a plant.
     * @return Growing age of the plant.
     */
    abstract protected int getBreedingAge();

    /**
     * Get the maximum number of squares the plant can grow into.
     * @return  The number of squares the plant can grow into.
     */
    abstract protected int getMaxLitter();  

    /**
     * Increase the current water level.
     */
    protected void increaseWaterLevel(){
        waterLevel++;
    }

    /**
     * Decrease the current water level.
     */
    protected void decreaseWaterLevel(){
        waterLevel--;
    }

    /**
     * Increase the current sun level.
     */
    protected void increaseSunLevel(){
        sunLevel++;
    }

    /**
     * Decrease the current sun level.
     */
    protected void decreaseSunLevel(){
        sunLevel--;
    }

    /**
     * Get the current water level.
     * @return The plants current water level.
     */
    protected int getWaterLevel(){
        return waterLevel;
    }

    /**
     * Get the current sun level.
     * @return The plants current sun level.
     */
    protected int getSunLevel(){
        return sunLevel;
    }

    /**
     * If either the water level or the sun level is 0 the plant dies.
     * @param step. The number of steps in the simulation.
     */
    protected void incrementAge(int step){
        super.incrementAge(step);
        if(waterLevel == 0 || sunLevel == 0){
            setDead();
        }
    }
}
