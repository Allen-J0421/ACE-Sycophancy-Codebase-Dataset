package safari;

import java.util.Random;
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
        if(simulator.isDay()){
            if(isActive()) {
                 Field field = getField();
                //sets the location to show the grass once the actor on top of it has moved.
                if (field.getObjectAt(getLocation()) == null ){
                    setLocation(getLocation());
                }else if (field.getObjectAt(getLocation()) != this && (field.getObjectAt(getLocation()) instanceof Plants)){
                    Object grassObject = field.getObjectAt(getLocation());
                    Plants grassPlant = (Plants)grassObject;
                    grassPlant.setDead();
                    setLocation(getLocation());

                }
                
                switch(simulator.getWeather()){
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
                List<Location> free = field.getFreeAdjacentLocations(getLocation());
                int births = growth(simulator.getSteps());
                for(int b = 0; b < births && free.size() > 0; b++) {
                    Location loc = free.remove(0);
                    Grass young = new Grass(false, field, loc);
                    newGrass.add(young);
                }
            }  
        }else{
            //space for potential night activities
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
