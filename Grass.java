import java.util.List;

/**
 * A simple model of a Grass plant.
 * Grass age, breed and die.
 *
 * @version 2022.03.01
 */
public class Grass extends Plants
{
    // The age at which a grass plant can start to grow.
    private static final int BREEDING_AGE = 1;
    // The age to which a grass plant can live.
    private static final int MAX_AGE = 200;
    // The maximum number of sqaures new grass can grow into.
    private static final int MAX_LITTER_SIZE = 500;

    /**
     * Create a new grass plant at location in field.
     *
     * @param randomAge True if a random age should be generated for the grass
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Grass(boolean randomAge, Field field, Location location)
    {
        super(field, location);
        setAge(0);
        if(randomAge) {
            setAge(getRandom().nextInt(MAX_AGE));
        } 
        setGrowthLevel(getAge()/50.0);
    }

    /**
     * Make this plant act - that is: make it do
     * whatever it wants/needs to do.
     * 
     * @param newGrass A list to receive newly born grass.
     * @param simulator The simulator.
     */
    public void act(List<Actor> newGrass,Simulator simulator){
        incrementAge(simulator.getSteps());
        setGrowthLevel(0.05);
        if(simulator.isDay() && isActive()) {
            Field field = getField();
            restoreVisibleGrass(field);
            applyWeather(simulator.getWeather());
            spread(newGrass, field, simulator.getSteps());
        }
    }

    /**
     * Restore this grass in the field after an actor has moved away.
     * @param field The field currently occupied.
     */
    private void restoreVisibleGrass(Field field)
    {
        Object actorAtLocation = field.getObjectAt(getLocation());
        if(actorAtLocation == null) {
            setLocation(getLocation());
        } else if(actorAtLocation != this && actorAtLocation instanceof Plants) {
            Plants grassPlant = (Plants) actorAtLocation;
            grassPlant.setDead();
            setLocation(getLocation());
        }
    }

    /**
     * Update water and sunlight levels for the current weather.
     * @param weather The current weather.
     */
    private void applyWeather(Weather weather)
    {
        switch(weather){
            case SUNNY:
                increaseSunLevel();
                decreaseWaterLevel();
                break;
            case RAINY:
                increaseWaterLevel();
                decreaseSunLevel();
                break;
            case FOGGY:
                increaseWaterLevel();
                break;
            default:
                break;
        }
    }

    /**
     * Spread new grass into free adjacent locations.
     * @param newGrass A list to receive newly born grass.
     * @param field The field currently occupied.
     * @param step The current simulation step.
     */
    private void spread(List<Actor> newGrass, Field field, int step)
    {
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = growth(step);
        for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            Grass young = new Grass(false, field, loc);
            newGrass.add(young);
        }
    }

    /**
     * Get breeding age of a grass.
     * @return int. Breeding age of the grass.
     */
    protected int getBreedingAge(){
        return BREEDING_AGE;
    }

    /**
     * Get max age of a grass.
     * @return int. Maximum age of the grass.
     */
    protected int getMaxAge(){
        return MAX_AGE;
    }

    /**
     * Get max litter size of a grass.
     * @return int. Maximum litter size of the grass;
     */
    protected int getMaxLitter(){
        return MAX_LITTER_SIZE;
    }
}
