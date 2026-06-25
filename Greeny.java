import java.util.List;
import java.util.Iterator;
import java.util.Random;
import java.util.ArrayList;

/**
 * A simple model of a Greeny.
 * Greenies age, move, eat rabbits, and die.
 *
 * @version 2022.25.02
 */
public class Greeny extends Animal
{
    // Initialise random number generator
    Random random = new Random();
    
    // Characteristics shared by all Greenies (class variables).
    
    // The age at which a Greeny can start to breed.
    private static final int BREEDING_AGE = 15;
    // The age to which a Greeny can live.
    private static final int MAX_AGE = 425;
    // The likelihood of a Greeny breeding.
    private static final double BREEDING_PROBABILITY = 0.5;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 3;
    // The food value of a single animal. In effect, this is the
    // number of steps a Greeny can go before it has to eat again.
    private static final int GREENY_FOOD_VALUE = 16;
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
     * Create a Greeny. A Greeny can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the Greeny will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Greeny(boolean randomAge, Field field, Location location)
    {
        super(field, location);
        if(randomAge) {
            age = rand.nextInt(MAX_AGE);
            foodLevel = rand.nextInt(GREENY_FOOD_VALUE);
        }
        else {
            age = 0;
            foodLevel = GREENY_FOOD_VALUE;
        }
        
        isMale = genderAssign();
        
        // Store dummy prey objects for type comparison
        preyList.add(new Bluy());
        preyList.add(new Cyany());
        
        // Store dummy breeding animal objects for the type comparison
        breedingTypeList.add(new Greeny());
        breedingTypeList.add(new Bluy());
    }
    
    // Default constructor for dummy objects
    public Greeny()
    {
    }
    
    /**
     * This is what the Greeny does most of the time: it hunts for
     * prey. In the process, it might breed, die of hunger,
     * or die of old age.
     * @param newGreenies A list to return newly born Greenies.
     * @param isThereFog Whether fog is currently present in the environment.
     */
    public void act(List<Animal> newGreenies, boolean isThereFog)
    {
        incrementAge(MAX_AGE);
        incrementHunger();
        if(isAlive()) {
            giveBirth(newGreenies);            
            Location newLocation;
            
            // Move towards a source of food if found.
            newLocation = findFood(preyList, GREENY_FOOD_VALUE);
            
            if(newLocation == null) { 
                // No food found - try to move to a free location.
                newLocation = getField().freeAdjacentLocation(getLocation());
            }
            
            if (isThereFog) {
                newLocation = getField().randomAdjacentLocation(getLocation());
                eatRandomPrey(newLocation, preyList, GREENY_FOOD_VALUE);
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
     * Check whether or not this Greeny is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newGreenies A list to return newly born Greenies.
     */
    private void giveBirth(List<Animal> newAnimals)
    {
        Animal breedingAnimal = getBreedingAnimalAdjacent(breedingTypeList);        
        
        if (breedingAnimal != null) {
            // New Greenies are born into adjacent locations.
            // Get a list of adjacent free locations.
            Field field = getField();
            List<Location> free = field.getFreeAdjacentLocations(getLocation());
            int births = getBreedNumber(age, BREEDING_AGE, BREEDING_PROBABILITY, MAX_LITTER_SIZE);
            
            if (breedingAnimal instanceof Greeny) {
                for(int b = 0; b < births && free.size() > 0; b++) {
                    Location loc = free.remove(0);
                    Greeny young = new Greeny(false, field, loc);
                    newAnimals.add(young);
                }
            }
            else if (breedingAnimal instanceof Bluy) {
                for(int b = 0; b < births && free.size() > 0; b++) {
                    Location loc = free.remove(0);
                    Cyany young = new Cyany(false, field, loc);
                    newAnimals.add(young);
                }
            }
            
            // When an nfected aniaml breeds, it can spread the disease.
            if(getIsInfected()) {
                spreadInfection(BREED_SPREAD_PROBABILITY);
            }
        }
    }
}
