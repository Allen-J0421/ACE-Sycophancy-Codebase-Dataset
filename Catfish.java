import java.util.List;
import java.util.Random;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;

/**
 * A simple model of a catfish.
 * Catfishs age, move, eat their prey, and die.
 *
 * @version 27.02.22
 */
public class Catfish extends Animal
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
    private static final Map<Class<? extends Actor>, Integer> PREY_FOOD_VALUES = createPreyFoodValueMap();
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    // The max food value of the catfish. In effect, this is the
    // number of steps a catfish can go before it has to eat again.
    private static final int MAX_FOOD = 7;
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
        for(Disease parentDisease : parentDiseases){
            if (parentDisease.isSpreadByBirth()){
                setDiseases.add(parentDisease);
                //System.out.println("Spread by parent disease");
            }
        }
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
        for(Disease disease : Simulator.diseases){
            Double prob = disease.getStartingActorsMap().get(Catfish.class);
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
        mapTemp.put(Water_Fern.class, 3);
        return mapTemp;
    }

    /**
     * Generate a number representing the number of births,
     * if it can breed.
     * @return The number of births (may be zero).
     */
    public int breed()
    {
        int births = 0;
        if(canBreed() && rand.nextDouble() <= BREEDING_PROBABILITY) {
            births = rand.nextInt(MAX_LITTER_SIZE) + 1;
        }
        return births;
    }

    /**
     * A catfish can breed if it has reached the breeding age.
     * @return true if the catfish can breed, false otherwise.
     */
    private boolean canBreed()
    {
        return age >= BREEDING_AGE;
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
    public Map<Class<? extends Actor>, Integer> getPreyFoodValuesMap()
    {
        return PREY_FOOD_VALUES;
    }

    /**
     * Creates a new catfish 
     * If the catfish is created at the start of the simulation no parentDiseases Set is given as there is no parent.
     * @param location The new location of the child
     * @param Set<Disease> The diseases that the parent had is passed down
     * @return The new catfish created
     */
    public Animal birth(Location loc, Set<Disease>... parentDiseases)
    {
        if (parentDiseases.length > 0) {
            return new Catfish(getTime(), getField(), loc,parentDiseases[0]);
        }
        return new Catfish(getTime(), getField(), loc);
    }
}

