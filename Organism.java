import java.util.List;
import java.util.Random;

/**
 * A class to represent all living creatures in the simulation
 * Shared characteristics between organisms include whether they are alive,
 * their location on the field and their age
 *
 * @version 2022.02.27
 */
public abstract class Organism extends FieldOccupant implements Actor
{
    // Whether the organism is alive or not
    private boolean alive;
    // The water level of the organism
    private int waterLevel;
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
        super(field, location);
        alive = true;
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
     * Organisms can randomly contract any disease whose species list includes
     * their own: each disease is rolled against its occurrence probability.
     * @param diseases The diseases currently present in the simulation.
     * @param rand The shared random source.
     */
    public void exposeToDiseases(List<Disease> diseases, Random rand)
    {
        for (Disease disease : diseases) {
            // random low chance of developing each disease in turn
            if (rand.nextDouble() <= disease.getProbability()) {
                // only contract a disease that affects this species
                if (disease.getSpecies().contains(getClass().getName())) {
                    disease.addIndividual(this);
                    setInfected();
                }
            }
        }
    }

    /**
     * Organisms breed into the separate nursery so their newborns are not
     * visited again during the same step.
     */
    public List<Actor> stepTarget(List<Actor> population, List<Actor> nursery)
    {
        return nursery;
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
        clearFromField();
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
        // Find the first adjacent water source (a lake) and drink from it.
        Location where = field.firstAdjacentLocationMatching(getLocation(),
            occupant -> occupant instanceof Lake);
        if(where != null) {
            Lake lake = (Lake) field.getObjectAt(where);
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
     * Rain raises every organism's water level.
     */
    public void onRain() {
        setWaterLevel(getWaterLevel() + 5);
    }

    /**
     * A heatwave halves every organism's water level.
     */
    public void onHeatwave() {
        setWaterLevel(getWaterLevel() / 2);
    }

    /**
     * An organism leaves the simulation once it is no longer alive.
     * @return true if this organism is dead.
     */
    public boolean isExpired() {
        return !isAlive();
    }

    /**
     * When an organism's infection runs its course, the organism dies.
     */
    protected void expireInfection() {
        setDead();
    }

    /**
     * @return Whether this organism is still alive, and so can carry and
     *         spread a disease.
     */
    protected boolean isActive() {
        return isAlive();
    }
}
