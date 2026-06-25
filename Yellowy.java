import java.util.List;
import java.util.Iterator;
import java.util.Random;
import java.util.ArrayList;

/**
 * A simple model of a Yellowy
 * Yellowies age, move, eat rabbits, and die.
 *
 * @version 2022.25.02
 */
public class Yellowy extends Animal
{
    // Initialise random number generator
    Random random = new Random();
    
    // Characteristics shared by all Yellowies (class variables).
    
    // The age at which a Yellowy can start to breed.
    private static final int BREEDING_AGE = 20;
    // The age to which a Yellowy can live.
    private static final int MAX_AGE = 175;
    // The likelihood of a Yellowy breeding.
    private static final double BREEDING_PROBABILITY = 0.6;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 2;
    // The food value of a single animal. In effect, this is the
    // number of steps a Yellowy can go before it has to eat again.
    private static final int YELLOWY_FOOD_VALUE = 12;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    // The probability of spreading the disease to an adjacent animals
    private static final double ADJACENT_SPREAD_PROBABILITY = 0.0005;
    // The probability of spreading the disease to a breeding animal.
    private static final double BREED_SPREAD_PROBABILITY = 0.001;
    
    // List of preys (for class type comparison)
    private ArrayList<Animal> preyList = new ArrayList<>();
    
    // List of species the animal can breed with (for class type comparison)
    private ArrayList<Animal> breedingTypeList = new ArrayList<>();

    /**
     * Constructor for objects of class Yellowy
     */
    /**
     * Create a Yellowy. A Yellowy can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the Yellowy will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Yellowy(boolean randomAge, Field field, Location location)
    {
        super(field, location);
        if(randomAge) {
            age = rand.nextInt(MAX_AGE);
            foodLevel = rand.nextInt(YELLOWY_FOOD_VALUE);
        }
        else {
            age = 0;
            foodLevel = YELLOWY_FOOD_VALUE;
        }
        
        isMale = genderAssign();
        
        // Store dummy prey objects for type comparison
        preyList.add(new Greeny());
        preyList.add(new Bluy());
        preyList.add(new Cyany());
        
        // Store dummy breeding animal objects for the type comparison
        breedingTypeList.add(new Yellowy());
    }

    // Default constructor for dummy objects
    public Yellowy()
    {
    }
    
    /**
     * This is what the Yellowy does most of the time: it hunts for
     * rabbits. In the process, it might breed, die of hunger,
     * or die of old age.
     * @param newYellowies A list to return newly born Yellowies.
     * @param isThereFog Whether fog is currently present in the environment.
     */
    public void act(List<Animal> newYellowies, boolean isThereFog)
    {
        incrementAge(MAX_AGE);
        incrementHunger();
        if(isAlive()) {
            giveBirth(newYellowies);
            Location newLocation;
            
            // Move towards a source of food if found.
            newLocation = findFood(preyList, YELLOWY_FOOD_VALUE);
            
            if(newLocation == null) { 
                // No food found - try to move to a free location.
                newLocation = getField().freeAdjacentLocation(getLocation());
            }
            
            if (isThereFog) {
                newLocation = getField().randomAdjacentLocation(getLocation());
                eatRandomPrey(newLocation, preyList, YELLOWY_FOOD_VALUE);
            }
            
            if(getIsInfected()) {
                spreadInfection(ADJACENT_SPREAD_PROBABILITY);
            }
                
            // See if it was possible to move.
            if(newLocation != null) {
                setFieldLocation(newLocation);
            }
            else {
                // Overcrowding.
                setDead();
            }
        }
    }
    
    /**
     * Check whether or not this Yellowy is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newYellowies A list to return newly born Yellowies.
     */
    private void giveBirth(List<Animal> newYellowies)
    {
        if (getBreedingAnimalAdjacent(breedingTypeList) != null) {
            // New Yellowies are born into adjacent locations.
            // Get a list of adjacent free locations.
            Field field = getField();
            List<Location> free = field.getFreeAdjacentLocations(getLocation());
            int births = getBreedNumber(age, MAX_AGE, BREEDING_PROBABILITY, MAX_LITTER_SIZE);
            for(int b = 0; b < births && free.size() > 0; b++) {
                Location loc = free.remove(0);
                Yellowy young = new Yellowy(false, field, loc);
                newYellowies.add(young);
            }
        }
        
        // When an nfected aniaml breeds, it can spread the disease.
        if(getIsInfected()) {
            spreadInfection(BREED_SPREAD_PROBABILITY);
        }
    }
}
