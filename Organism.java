import java.util.Random;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

/**
 * A class to represent all living creatures in the simulation
 * Shared characteristics between organisms include whether they are alive,
 * their location on the field and their age
 *
 * @version 2022.02.27
 */
public abstract class Organism extends SimulationEntity
{
    // Whether the organism is alive or not
    private boolean alive;
    // The organism's position in the field
    private Location location;
    // The water level of the organism
    private int waterLevel;
    // Whether the organism is infected with a disease or not.
    private boolean infected;
    // The organism's age
    private int age;
    
    /** 
     * Constructor for organisms - places new organism in location
     * 
     * @param randomAge If true, the organism will have random age
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Organism(boolean randomAge, Field field, Location location)
    {
        super(field);
        alive = true;
        setLocation(location);
        age = 0;
    }
    
     /**
     * Make this organism act - that is: make it do
     * whatever it wants/needs to do.
     * All organisms will age and lose water with each step that they act
     *
     * @param newOrganisms A list to receive newly born organisms
     */
    public void act(List<Actor> newOrganisms)
    {
        incrementAge();
        decreaseWaterLevel();
    }
    
    /**
     * Return the organism's location.
     * @return The organism's location.
     */
    protected Location getLocation()
    {
        return location;
    }
    
     /**
     * Place the organism at the new location in the given field.
     * @param newLocation The organism's new location.
     */
    protected void setLocation(Location newLocation)
    {
        if(location != null) {
            getField().clear(location);
        }
        location = newLocation;
        getField().place(this, newLocation);
    }

    /**
     * Check whether the organism is alive or not.
     * @return true if the organism is still alive.
     */
    protected boolean isAlive()
    {
        return alive;
    }

    /**
     * Indicate that the organism is no longer alive.
     * It is removed from the field.
     */
    public void setDead()
    {
        alive = false;
        if(location != null) {
            getField().clear(location);
            location = null;
            setField(null);
        }
    }
    
    /**
     * Increase the age.
     */
    public void incrementAge()
    {
        age = age + 1;
    }
    
    /**
     * @return The value that eating this organism gives predator
     */
    abstract public int getFoodValue();
    
    /**
     * Look for water adjacent to the organism's current location
     * @return Where water was found, or null if it wasn't 
     */
    protected Location findWater() {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()){
            Location where = it.next();
            Object waterSource = field.getObjectAt(where);
            if(waterSource instanceof Lake) {
                Lake lake = (Lake) waterSource;
                // if there is enough water left to drink
                if(lake.getVolume() >= lake.getWaterValue()){
                    // increase drinking organism's water level and reduces lake's water level
                    int newWaterLevel = getWaterLevel() + lake.getWaterValue();
                    this.setWaterLevel(newWaterLevel);
                    lake.reduceVolume(lake.getWaterValue());
                    return where;
                }
                // if only a little bit of water left to drink, drinks remnants 
                // then empties lake
                else if (lake.getVolume() >= 0) {
                    int newWaterLevel = getWaterLevel() + lake.getVolume();
                    this.setWaterLevel(newWaterLevel);
                    lake.setVolume(0);
                    lake.setDead();
                    return where;
                }
                else if(lake.getVolume() <=0){
                    lake.setDead();
                    return null;
                }
            }
        }
        return null;
    }
    
    /** 
     * Lower's the organism's water level - may result in its death
     */
    public void decreaseWaterLevel(){
        int newWaterLevel = getWaterLevel() - 1;
        setWaterLevel(newWaterLevel);
        if(getWaterLevel() <=0) {
            setDead();
        }
    }
    
    /**
     * @param waterValue The level of water an organism has 
     */
    protected void setWaterLevel(int newWaterLevel)
    {
        waterLevel = newWaterLevel;
    }
    
    /**
     * @return The current level of water an organism has
     */
    protected int getWaterLevel()
    {
        return waterLevel;
    }
    
    /**
     * @param age The current age of the organism
     */
    protected void setAge(int age)
    {
        this.age = age;
    }
    
    /**
     * @return Current age of the organism
     */
    protected double getAge()
    { 
        return age;
    }
    
    /**
     * Infects the organism with a disease
     */
    protected void setInfected() {
        infected = true;
    }
    
    /** 
     * Infection does not last forever in water, removes disease
     */
    protected void notInfected() {
        infected = false;
    }
    
    /**
     * @return Whether the organism is infected with a disease
     */
    protected boolean isInfected(){
        return infected;
    }
}
