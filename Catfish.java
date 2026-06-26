import java.util.Random;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;

/**
 * A simple model of a catfish.
 * Catfishs age, move, eat their prey, and die.
 *
 * @version 27.02.22
 */
public final class Catfish extends Animal
{
    // Characteristics shared by all catfishs (class variables).

    // The age at which a catfish can start to breed.
    private static final int BREEDING_AGE = 5;
    // The age to which a catfish can live.
    private static final int MAX_AGE = 20;
    // The likelihood of a catfish breeding.
    private static final double BREEDING_PROBABILITY = 0.8;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 6;
    // The food value of all prey.
    private static final Map<String, Integer> PREY_FOOD_VALUES = createPreyFoodValueMap();
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    // The max food value of the catfish. In effect, this is the
    // number of steps a catfish can go before it has to eat again.
    private static final int MAX_FOOD = 7;
    // String name for the catfish
    public static final String name = "Catfish";
    
    /**
     * Create a catfish. The catfish is created as a new born (age zero
     * and not hungry).
     * 
     * @param time The time in the simulation.
     * @param field The field currently occupied.
     * @param location The location within the field.
     * @param parentDiseases The diseases the parent of this catfish had.
     */
    public Catfish(Time time, Field field, Location location, Set<Disease> parentDiseases){
        super(time, field, location);
        female = rand.nextBoolean();
        nocturnal = true;
        
        canGoLand = false;
        canGoWater = true;
        
        age = 0;
        foodLevel = MAX_FOOD;
        inheritDiseases(setDiseases, parentDiseases);
        setLocation(location);
    }
    
    /**
     * Create a catfish. The catfish is created with a random age and food level.
     * 
     * @param time The time in the simulation.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Catfish(Time time, Field field, Location location){
        super(time, field, location);
        female = rand.nextBoolean();
        nocturnal = true;
        canGoLand = false;
        canGoWater = true;
        
        age = rand.nextInt(MAX_AGE);
        foodLevel = rand.nextInt(MAX_FOOD)+1;
        addStartingDiseases(name, setDiseases, Simulator.diseases, rand);
        setLocation(location);
    }

    /**
     * Create a Map with a key of the prey String name
     * and a value of the food level is given when eaten.
     * @return The prey food value Map.
     */
    private static Map<String, Integer> createPreyFoodValueMap()
    {
        Map<String,Integer> mapTemp = new HashMap<>();
        mapTemp.put("Water_Fern", 3);
        return mapTemp;
    }

    /**
     * Generate a number representing the number of births,
     * if it can breed.
     * @return The number of births (may be zero).
     */
    public int breed()
    {
        return breedIfAble(age, BREEDING_AGE, BREEDING_PROBABILITY, MAX_LITTER_SIZE, rand);
    }

    /**
     * Returns the String name.
     * @return The String name.
     */
    public String getActorName (){
        return name;
    }

    /**
     * Returns the max food level that the catfish can have.
     * @return The max food level that the catfish can have.
     */
    public int getMaxFood()
    {
        return MAX_FOOD;
    }

    /**
     * Returns the max age that the catfish can have before dying.
     * @return The max age that the catfish can have before dying.
     */
    public int getMaxAge()
    {
        return MAX_AGE;
    }

    /**
     * Returns the prey food values Map.
     * @return The prey food values Map.
     */
    public Map<String, Integer> getPreyFoodValuesMap()
    {
        return PREY_FOOD_VALUES;
    }

    /**
     * Creates a new catfish 
     * If the catfish is created at the start of the simulation no parentDiseases Set is given as there is no parent.
     * @param loc The new location of the child
     * @param parentDiseases The diseases that the parent had is passed down
     * @return The new catfish created
     */
    public Animal birth(Location loc, Set<Disease> parentDiseases)
    {
        return new Catfish(getTime(), getField(), loc, parentDiseases);
    }

    /**
     * Creates a new catfish without parent diseases.
     * @param loc The new location of the catfish
     * @return The new catfish created
     */
    public Animal birth(Location loc)
    {
        return new Catfish(getTime(), getField(), loc);
    }
}
