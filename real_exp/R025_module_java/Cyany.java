import java.util.List;
import java.util.Iterator;
import java.util.Random;
import java.util.ArrayList;

/**
 * A simple model of a Cyany.
 * Cyanies age, move, breed, and die.
 *
 * @version 2022.25.02
 */
public class Cyany extends Animal
{
    // Initialise random number generator
    Random random = new Random();
    
    // Characteristics shared by all Cyanies (class variables).

    // The age at which a Cyany can start to breed.
    private static final int BREEDING_AGE = 5;
    // The age to which a Cyany can live.
    private static final int MAX_AGE = 300;
    // The likelihood of a Cyany breeding.
    private static final double BREEDING_PROBABILITY = 0.3;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 4;
    // The food value of a single animal. In effect, this is the
    // number of steps a Cyany can go before it has to eat again.
    private static final int CYANY_FOOD_VALUE = 17;
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
     * Create a new Cyany. A rabbit may be created with age
     * zero (a new born) or with a random age.
     * 
     * @param randomAge If true, the Cyany will have a random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Cyany(boolean randomAge, Field field, Location location)
    {
        super(field, location);
        if(randomAge) {
            age = rand.nextInt(MAX_AGE);
            foodLevel = rand.nextInt(CYANY_FOOD_VALUE);
        }
        else {
            age = 0;
            foodLevel = CYANY_FOOD_VALUE;
        }
        
        isMale = genderAssign();
        
        // Store dummy prey objects for type comparison
        preyList.add(new Bluy());
        
        // Store dummy breeding animal objects for the type comparison
        breedingTypeList.add(new Cyany());
    }
    
    // Default constructor for dummy objects
    public Cyany()
    {
    }
    
    /**
     * This is what the Cyany does most of the time - it runs 
     * around. Sometimes it will breed or die of old age.
     * @param newCyanies A list to return newly born Cyanies.
     * @param isThereFog Whether fog is currently present in the environment.
     */
    public void act(List<Animal> newCyanies, boolean isThereFog)
    {
        incrementAge(MAX_AGE);
        if(Simulator.HOURS >= 5 || Simulator.HOURS < 1) {
            incrementHunger();
            if(isAlive()) {
                giveBirth(newCyanies);            
                Location newLocation;
            
                // Move towards a source of food if found.
                newLocation = findFood(preyList, CYANY_FOOD_VALUE);
                
                if(newLocation == null) { 
                    // No food found - try to move to a free location.
                    newLocation = getField().freeAdjacentLocation(getLocation());
                }
                
                if (isThereFog) {
                    newLocation = getField().randomAdjacentLocation(getLocation());
                    eatRandomPrey(newLocation, preyList, CYANY_FOOD_VALUE);
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
     * Check whether or not this Cyany is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newCyanies A list to return newly born Cyanies.
     */
    private void giveBirth(List<Animal> newCyanies)
    {
        Animal breedingAnimal = getBreedingAnimalAdjacent(breedingTypeList);        

        if (breedingAnimal != null) {
            // New Cyanies are born into adjacent locations.
            // Get a list of adjacent free locations.
            Field field = getField();
            List<Location> free = field.getFreeAdjacentLocations(getLocation());
            int births = getBreedNumber(age, BREEDING_AGE, BREEDING_PROBABILITY, MAX_LITTER_SIZE);
            
            for(int b = 0; b < births && free.size() > 0; b++) {
                Location loc = free.remove(0);
                Cyany young = new Cyany(false, field, loc);
                newCyanies.add(young);
            }
        }
        
        // When an nfected aniaml breeds, it can spread the disease.
        if(getIsInfected()) {
            spreadInfection(BREED_SPREAD_PROBABILITY);
        }
    }
}
