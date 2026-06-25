import java.util.List;
import java.util.Iterator;
import java.util.Random;
import java.util.ArrayList;

/**
 * A simple model of a Reddy
 * Reddies age, move, eat animals, and die.
 *
 * @version 2022.25.02
 */
public class Reddy extends Animal
{
    // Initialise random number generator
    Random random = new Random();
    
    // Characteristics shared by all Reddies (class variables).
    
    // The age at which a Reddy can start to breed.
    private static final int BREEDING_AGE = 20;
    // The age to which a Reddy can live.
    private static final int MAX_AGE = 450;
    // The likelihood of a Reddy breeding.
    private static final double BREEDING_PROBABILITY = 0.3;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 3;
    // The food value of a single animal. In effect, this is the
    // number of steps a Reddy can go before it has to eat again.
    private static final int REDDY_FOOD_VALUE = 12;
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
     * Constructor for objects of class Reddy
     */
    /**
     * Create a Reddy. A Reddy can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the Reddy will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Reddy(boolean randomAge, Field field, Location location)
    {
        super(field, location);
        if(randomAge) {
            age = rand.nextInt(MAX_AGE);
            foodLevel = rand.nextInt(REDDY_FOOD_VALUE);
        }
        else {
            age = 0;    
            foodLevel = REDDY_FOOD_VALUE;
        }
        
        isMale = genderAssign();
        
        
        // Store dummy prey animal objects for type comparison
        preyList.add(new Yellowy());
        preyList.add(new Purply());
        preyList.add(new Greeny());
        preyList.add(new Cyany());
        preyList.add(new Bluy());
        
        // Store dummy breeding animal objects for the type comparison
        breedingTypeList.add(new Reddy());
        breedingTypeList.add(new Greeny());
        breedingTypeList.add(new Bluy());
    }

    // Default constructor for dummy objects
    public Reddy()
    {
    }
    
    /**
     * This is what the Reddy does most of the time: it hunts for
     * all animals except itself. In the process, it might breed, die of hunger,
     * die of old age, or sleep at night.
     * @param newReddies A list to return newly born Reddies.
     * @param isThereFog Whether fog is currently present in the environment.
     */
    public void act(List<Animal> newReddies, boolean isThereFog)
    {
        incrementAge(MAX_AGE);
        if((Simulator.HOURS >= 5 && Simulator.HOURS < 16) || Simulator.HOURS > 20 || Simulator.HOURS < 1) {
            incrementHunger();
            if(isAlive()) {
                giveBirth(newReddies);
                Location newLocation;
                
                // Move towards a source of food if found.
                newLocation = findFood(preyList, REDDY_FOOD_VALUE);
                
                if(newLocation == null) { 
                    // No food found - try to move to a free location.
                    newLocation = getField().freeAdjacentLocation(getLocation());
                }
                
                if(isThereFog) {
                    newLocation = getField().randomAdjacentLocation(getLocation());
                    eatRandomPrey(newLocation, preyList, REDDY_FOOD_VALUE);
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
    }
    
    /**
     * Check whether or not this Reddy is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newReddies A list to return newly born Reddies.
     */
    private void giveBirth(List<Animal> newReddies)
    {
        Animal breedingAnimal = getBreedingAnimalAdjacent(breedingTypeList);
        
        if (breedingAnimal != null) {
            // New Reddies are born into adjacent locations.
            // Get a list of adjacent free locations.
            Field field = getField();
            List<Location> free = field.getFreeAdjacentLocations(getLocation());
            int births = getBreedNumber(age, BREEDING_AGE, BREEDING_PROBABILITY, MAX_LITTER_SIZE);
            
            if (breedingAnimal instanceof Reddy) {
                for(int b = 0; b < births && free.size() > 0; b++) {
                    Location loc = free.remove(0);
                    Reddy young = new Reddy(false, field, loc);
                    newReddies.add(young);
                }
            }
            else if (breedingAnimal instanceof Greeny) {
                for(int b = 0; b < births && free.size() > 0; b++) {
                    Location loc = free.remove(0);
                    Yellowy young = new Yellowy(false, field, loc);
                    newReddies.add(young);
                }
            }
            else if (breedingAnimal instanceof Bluy) {
                for(int b = 0; b < births && free.size() > 0; b++) {
                    Location loc = free.remove(0);
                    Purply young = new Purply(false, field, loc);
                    newReddies.add(young);
                }
            }
            
            // When an nfected aniaml breeds, it can spread the disease.
            if(getIsInfected()) {
                spreadInfection(BREED_SPREAD_PROBABILITY);
            }
        }
    }
}
