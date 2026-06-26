import java.util.List;
import java.util.*;

/**
 * A simple model of a eagle.
 * Eagles age, move,contract diseases, eat prey, and die.
 *
 * @version 2022.03.02
 */
public class Eagle extends Animal
{
     // Characteristics shared by all eagles (class variables).

    // The age at which a eagle can start to breed.
    private static final int BREEDING_AGE = 15;
    // The age to which a eagle can live.
    private static final int MAX_AGE = 150;
    // The likelihood of a eagle breeding.
    private static final double BREEDING_PROBABILITY = 0.4;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 2;
    // The eagles's food level which is increased by eating prey.
    private static final int MAX_FOOD_LEVEL = 14;
    // The food value of a single eagle.
    private static final int FOOD_VALUE = 5;
    // A set of organisms that a eagle consumes
    private static final Set<Class> DIET = new HashSet<>(Arrays.asList(Deer.class, Coyote.class, Mouse.class));

    // Implementing abstract methods to return fields to be used by the superclass
    protected double BREEDING_AGE() { return BREEDING_AGE; }
    protected int MAX_LITTER_SIZE() { return MAX_LITTER_SIZE; }
    protected double BREEDING_PROBABILITY() { return BREEDING_PROBABILITY; }
    protected int MAX_AGE() { return MAX_AGE; }
    protected int MAX_FOOD_LEVEL() { return MAX_FOOD_LEVEL; }
    protected int FOOD_VALUE() { return FOOD_VALUE; }
    protected Set<Class> DIET() { return DIET; }

    /**
     * Create a eagle. A eagle can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * @param randomAge If true, the eagle will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     * @param sex The gender of the eagle.
     */
    public Eagle(boolean randomAge, Field field, Location location, Gender sex)
    {
        super(field, location,randomAge,sex);
        this.isNocturnal = false;
    }

    
    @Override
    protected Animal createOffspring(Field field, Location location, Gender sex)
    {
        return new Eagle(false, field, location, sex);
    }

    /**
     * Make this animal act - that is: make it do
     * whatever it wants/needs to do.
     * @param newAnimals A list to receive newly born animals.
     * @param environment The environment that the eagle resides in. 
     */
    public void act(List<Actor> newAnimals, Environment environment)
    {
        randomlyContractDisease();
        incrementAge();
        incrementHunger();
        if(isAlive()) {
            giveBirth(newAnimals, environment);
            // Move towards a source of food if found.
            Location newLocation = findFood(environment);
            if(newLocation == null) {
                // No food found - try to move to a free location.
                newLocation = getField().freeAdjacentLocation(getLocation());
            }
            List<Location> adjacentGrassSpots = getField().adjacentLocationsWithSpecies(getLocation(), Grass.class);

            if(newLocation != null) {
                setLocation(newLocation);
            }
            else if (adjacentGrassSpots.size() > 0) {
                getField().clear(getLocation());
                setLocation(adjacentGrassSpots.get(rand.nextInt(adjacentGrassSpots.size())));
            }
            // removing overcrowding for eagles since they're above everyone

            if(isDiseased() && getDisease().getLethalityRate() <= rand.nextDouble()){
                // every step, check if the Animal is diseased
                // if it is Diseased, and a random double is less than the lethality rate, the Animal dies
                setDead();
            }

        }
    }


    /**
     * Additional functionality that doesn't allow eagles to find food while it's raining
     * @param environment The environment that the eagle resides in. 
     * @return Location Where food was found, or null if it wasn't.
     */
    protected Location findFood(Environment environment)
    {
        if (environment.getWeather().getCurrentWeather() != WeatherType.RAINING) {
            return super.findFood();
        }
        return null;
    }

}
