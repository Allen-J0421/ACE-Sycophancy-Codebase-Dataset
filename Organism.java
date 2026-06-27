import java.util.Random;
import java.util.List;
import java.util.Iterator;

/**
 * A class to represent all living creatures in the simulation
 * Shared characteristics between organisms include whether they are alive,
 * their location on the field and their age
 *
 * @version 2022.02.27
 */
public abstract class Organism implements Actor
{
    // Shared random source for organism breeding decisions.
    private static final Random rand = Randomizer.getRandom();
    // Whether the organism is alive or not
    private boolean alive;
    // The organism's field
    private Field field;
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
        alive = true;
        this.field = field;
        setLocation(location);
        age = 0;
    }
    
    /**
     * Advance the organism by one lifecycle tick.
     * All organisms age and lose water at each tick before specialized
     * lifecycle behavior is executed.
     *
     * @param context Shared lifecycle state for the current step.
     */
    @Override
    public final void tick(SimulationContext context)
    {
        if (!isAlive()) {
            return;
        }
        if (!shouldTick(context)) {
            return;
        }
        incrementAge();
        decreaseWaterLevel();
        if (isAlive()) {
            performLifecycle(context);
        }
    }

    /**
     * Decide whether this organism should advance during the current tick.
     * Subclasses can use this to gate lifecycle advancement on context.
     *
     * @param context Shared lifecycle state for the current step.
     * @return true if the organism should run its lifecycle this tick.
     */
    protected boolean shouldTick(SimulationContext context)
    {
        return true;
    }

    /**
     * Execute the organism-specific lifecycle behaviour after shared upkeep.
     *
     * @param context Shared lifecycle state for the current step.
     */
    protected abstract void performLifecycle(SimulationContext context);
    
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
            field.clear(location);
        }
        location = newLocation;
        field.place(this, newLocation);
    }
    
    /**
     * Return the organism's field.
     * @return The organism's field.
     */
    protected Field getField()
    {
        return field;
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
            field.clear(location);
            location = null;
            field = null;
        }
    }
    
    /**
     * Increase the age.
     */
    public final void incrementAge()
    {
        age = age + 1;
        if (age > getMaxAge()) {
            setDead();
        }
    }

    /**
     * Apply shared parent state to a newly created offspring.
     * Subclasses can override to copy domain-specific state.
     * @param offspring The newly created offspring.
     */
    protected void initializeOffspring(Organism offspring)
    {
        // Default is intentionally empty.
    }

    /**
     * Determine whether this organism can breed during the current step.
     * Subclasses define their own breeding condition.
     *
     * @return true if the organism can breed.
     */
    protected final boolean canBreed()
    {
        return meetsBreedingCondition();
    }

    /**
     * Check the species-specific breeding condition.
     *
     * @return true if the organism satisfies its breeding condition.
     */
    protected abstract boolean meetsBreedingCondition();

    /**
     * Shared breeding helper used by subclasses.
     *
     * @param breedingProbability The chance of successful breeding when allowed.
     * @param maxLitterSize The maximum number of offspring produced at once.
     * @return The number of offspring to create.
     */
    protected final int breed(double breedingProbability, int maxLitterSize)
    {
        if (canBreed() && rand.nextDouble() <= breedingProbability) {
            return rand.nextInt(maxLitterSize) + 1;
        }
        return 0;
    }

    /**
     * Create offspring for a breeding event using the subclass-specific hooks.
     * Subclasses decide how many offspring to create and where they can be placed.
     *
     * @param newOffspring A list that receives the newly created offspring.
     */
    protected final void giveBirth(List<Actor> newOffspring)
    {
        int births = breed();
        if (births <= 0) {
            return;
        }

        List<Location> free = getBirthLocations();
        if (free == null) {
            return;
        }

        Field field = getField();
        for (int b = 0; b < births && free.size() > 0; b++) {
            Location location = free.remove(0);
            Organism offspring = createOffspring(field, location);
            initializeOffspring(offspring);
            newOffspring.add(offspring);
        }
    }
    
    /**
     * @return The value that eating this organism gives predator
     */
    abstract public int getFoodValue();

    /**
     * @return The maximum age this organism can reach.
     */
    protected abstract int getMaxAge();

    /**
     * Determine how many offspring this organism should create.
     * @return The number of offspring to create.
     */
    protected abstract int breed();

    /**
     * Find the free locations available for offspring.
     * @return The free locations available for offspring, or null if breeding is not possible.
     */
    protected abstract List<Location> getBirthLocations();

    /**
     * Create a new offspring of the current species.
     * @param field The field the offspring should occupy.
     * @param location The offspring's location.
     * @return A new instance of the same species.
     */
    protected abstract Organism createOffspring(Field field, Location location);
    
    /**
     * Look for water adjacent to the organism's current location
     * @return Where water was found, or null if it wasn't 
     */
    protected Location findWater() {
        if (getWaterLevel() > getWaterSearchThreshold()) {
            return null;
        }
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()){
            Location where = it.next();
            Object waterSource = field.getObjectAt(where);
            if(waterSource instanceof Lake) {
                Lake lake = (Lake) waterSource;
                if(lake.getVolume() <= 0){
                    lake.setDead();
                    return null;
                }
                int waterDrawn = Math.min(lake.getWaterValue(), lake.getVolume());
                changeWaterLevel(waterDrawn);
                lake.reduceVolume(waterDrawn);
                if (lake.getVolume() <= 0) {
                    lake.setDead();
                }
                return where;
            }
        }
        return null;
    }
    
    /** 
     * Lower's the organism's water level - may result in its death
     */
    public void decreaseWaterLevel(){
        changeWaterLevel(-1);
        if(getWaterLevel() <=0) {
            setDead();
        }
    }

    /**
     * Adjust the organism's water level by the given amount.
     * @param delta The amount to add to the current water level.
     */
    protected void changeWaterLevel(int delta)
    {
        waterLevel = waterLevel + delta;
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
     * @return The minimum water level below which the organism will seek water.
     */
    protected abstract int getWaterSearchThreshold();
    
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
    protected int getAge()
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

    @Override
    public boolean isExpired()
    {
        return !alive;
    }
}
