import java.util.List;
import java.util.Random;
import java.util.Iterator;

/**
 * A class representing shared characteristics of plants
 *
 * @version 2022.02.28
 */
public abstract class Plant extends Organism
{
    /**
     * Create a new plant at location in field.
     * @param randomAge If true, the plant will have a random age assigned to it.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Plant(boolean randomAge, Field field, Location location)
    {
        super(randomAge, field, location);
        Random rand = new Random();
        if(!randomAge) {
            setAge(0);
            //sets water level to a random value when first created
            setWaterLevel(rand.nextInt(15));
        }
    }
    
    /**
     * Make this plant act - that is: make it do
     * whatever it needs to do.
     * @param newPlants A list to receive newly grown plants
     */
    public void act(List<Actor> newPlants)
    {
        super.act(newPlants);
    }
    
    /**
     * Reduces the current water level of the plant by 1 and updates it.
     * Checks if water level is 0. If it does, the plant dies.
     */
    public void decreaseWaterLevel(){
        //newWaterLevel - passes updated value to set method in Organism class
        int newWaterLevel = getWaterLevel() - 1;
        setWaterLevel(newWaterLevel);
        if(getWaterLevel() <=0) {
            setDead();
        }
    }
    
    /**
     * Look for water adjacent to the plant's current location
     * @return Where water was found, or null if it wasn't
     */
    protected Location findWater() {
        if (getWaterLevel() <= 5) {
            super.findWater();
        }
        return null;
    }
}
