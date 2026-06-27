import java.util.Random;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;

/**
 * A simple model of a salamander.
 * Salamanders age, move, eat their prey, and die.
 *
 * @version 27.02.22
 */
public class Salamander extends Animal
{
    // Characteristics shared by all salamanders (class variables).

    // The age at which a salamander can start to breed.
    private static final int BREEDING_AGE = 5;
    // The age to which a salamander can live.
    private static final int MAX_AGE = 20;
    // The likelihood of a salamander breeding.
    private static final double BREEDING_PROBABILITY = 0.4;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 4;
    // The food value of all prey.
    private static final Map<Class<? extends Actor>, Integer> PREY_FOOD_VALUES = createPreyFoodValueMap();
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    // The max food value of the salamander. In effect, this is the
    // number of steps a salamander can go before it has to eat again.
    private static final int MAX_FOOD = 7;

    /**
     * Create a salamander. The salamander is created as a new born (age zero
     * and not hungry).
     * 
     * @param time The time in the simulation.
     * @param field The field currently occupied.
     * @param location The location within the field.
     * @param parentDiseases The diseases the parent of this salamander had.
     */
    public Salamander(Time time, Field field, Location location, Set<Disease> parentDiseases)
    {
        super(time, field, location);
        female = rand.nextBoolean();
        nocturnal = false;
        
        canGoLand = true;
        canGoWater = true;
        
        age = 0;
        foodLevel = MAX_FOOD;
        for(Disease parentDisease : parentDiseases){
            if (parentDisease.isSpreadByBirth()){
                setDiseases.add(parentDisease);
            }
        }
    }

    /**
     * Create a salamander. The salamander is created with a random age and food level.
     * 
     * @param time The time in the simulation.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Salamander(Time time, Field field, Location location)
    {
        super(time, field, location);
        female = rand.nextBoolean();
        nocturnal = false;
        canGoLand = true;
        canGoWater = true;
        age = rand.nextInt(MAX_AGE);
        foodLevel = rand.nextInt(MAX_FOOD)+1;
        for(Disease disease : Simulator.diseases){
            Double prob = disease.getStartingActorsMap().get(Salamander.class);
            if(prob != null && rand.nextDouble() <= prob){
                setDiseases.add(disease);
            }
        }
    }

    /**
     * Create a Map with a key of the prey class
     * and a value of the food level is given when eaten.
     * @return The prey food value Map.
     */
    private static Map<Class<? extends Actor>, Integer> createPreyFoodValueMap()
    {
        Map<Class<? extends Actor>, Integer> mapTemp = new HashMap<>();
        mapTemp.put(Grass.class, 4);
        mapTemp.put(Water_Fern.class, 5);
        return mapTemp;
    }

    protected int getBreedingAge() { return BREEDING_AGE; }
    protected double getBreedingProbability() { return BREEDING_PROBABILITY; }
    protected int getMaxLitterSize() { return MAX_LITTER_SIZE; }

    /**
     * Returns the max food level that the salamander can have.
     * @return The max food level that the salamander can have.
     */
    public int getMaxFood()
    {
        return MAX_FOOD;
    }

    /**
     * Returns the max age that the salamander can have before dying.
     * @return The max age that the salamander can have before dying.
     */
    public int getMaxAge()
    {
        return MAX_AGE;
    }

    /**
     * Returns the prey food values Map.
     * @return The prey food values Map.
     */
    public Map<Class<? extends Actor>, Integer> getPreyFoodValuesMap()
    {
        return PREY_FOOD_VALUES;
    }

    /**
     * Creates a new salamander 
     * If the salamander is created at the start of the simulation no parentDiseases Set is given as there is no parent.
     * @param location The new location of the child
     * @param Set<Disease> The diseases that the parent had is passed down
     * @return The new salamander created
     */
    public Animal birth(Location loc, Set<Disease>... parentDiseases)
    {
        if (parentDiseases.length > 0) {
            return new Salamander(getTime(), getField(), loc,parentDiseases[0]);
        }
        return new Salamander(getTime(), getField(), loc);
    }
}

