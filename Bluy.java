import java.util.List;
import java.util.Iterator;
import java.util.Random;
import java.util.ArrayList;

/**
 * A simple model of a Bluy.
 * Bluies age, move, breed, and die.
 *
 * @version 2022.25.02
 */
public class Bluy extends Animal
{
    // Initialise random number generator
    Random random = new Random();
    
    // Characteristics shared by all Bluies (class variables).
    private Nature nature;
    
    private NatureField natureField;

    // The age at which a Bluy can start to breed.
    private static final int BREEDING_AGE = 5;
    // The age to which a Bluy can live.
    private static final int MAX_AGE = 40;
    // The likelihood of a Bluy breeding.
    private static final double BREEDING_PROBABILITY = 0.65;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 4;
    // The food value of a single weed. In effect, this is the
    // number of steps a Bluy can go before it has to eat again.
    private static final int BLUY_FOOD_VALUE = 10;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    // The probability of spreading the disease to an adjacent animals
    private static final double ADJACENT_SPREAD_PROBABILITY = 0.0005;
    // The probability of spreading the disease to a breeding animal.
    private static final double BREED_SPREAD_PROBABILITY = 0.001;
    
  
    // List of species the animal can breed with (for class type comparison)
    private ArrayList<Animal> breedingTypeList = new ArrayList<>();

    /**
     * Create a new Bluy. A Bluy may be created with age
     * zero (a new born) or with a random age.
     * 
     * @param randomAge If true, the Bluy will have a random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Bluy(boolean randomAge, Field field, Location location)
    {
        super(field, location);
        if(randomAge) {
            age = rand.nextInt(MAX_AGE);
            foodLevel = rand.nextInt(BLUY_FOOD_VALUE);
        }
        else {
            age = 0;
            foodLevel = BLUY_FOOD_VALUE;
        }
        
        isMale = genderAssign();
    
        
        // Store dummy breeding animal objects for the type comparison
        breedingTypeList.add(new Cyany());
    }
    
    // Default constructor for dummy objects
    public Bluy()
    {
    }
    
    /**
     * This is what the Bluy does most of the time - it runs 
     * around. Sometimes it will breed or die of old age.
     * @param newBluies A list to return newly born Bluies.
     * @param isThereFog Whether fog is currently present in the environment.
     */
    public void act(List<Animal> newBluies, boolean isThereFog)
    {
        incrementAge(MAX_AGE);
        incrementHunger();
        if(isAlive()) {
            giveBirth(newBluies);            
            Location newLocation;
            
            // Move towards a source of food if found.
            newLocation = findFood();
            
            if(newLocation == null) { 
                // No food found - try to move to a free location.
                newLocation = getField().freeAdjacentLocation(getLocation());
            }
            
            if(getIsInfected()) {
                spreadInfection(0.9);
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
     * Look for Bluies adjacent to the current location.
     * Only the first live Bluy is eaten.
     * @return Where food was found, or null if it wasn't.
     */
    private Location findFood()
    {
        Field field = getField();
        List<Location> adjacent = natureField.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object nature = natureField.getObjectAt(where);
            Object animal = field.getObjectAt(where);
            if(nature instanceof Weed && animal == null) {
                Weed weed = (Weed) nature;
                if(weed.getIsAlive()) { 
                    weed.setDead();
                    foodLevel = BLUY_FOOD_VALUE;
                    return where;
                }
            }
        }
        return null;
    }
    
    /**
     * Eat a weed when the animal encountered a weed in a random location.
     */
    private void eatRandomWeed()
    {
        Field field = getField();
        Location where = field.randomAdjacentLocation(getLocation());
        Object nature = natureField.getObjectAt(where);
        Object animal = field.getObjectAt(where);
        if(nature instanceof Weed && animal == null) {
            Weed weed = (Weed) nature;
            if(weed.getIsAlive()) { 
                weed.setDead();
                foodLevel = BLUY_FOOD_VALUE;
            }
        }
    }
    
    /**
     * Check whether or not this Bluy is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newBluies A list to return newly born Bluies.
     */
    private void giveBirth(List<Animal> newBluies)
    {
        Animal breedingAnimal = getBreedingAnimalAdjacent(breedingTypeList);        
        
        if (oppositeGenderAdjacent()){
            // New Bluies are born into adjacent locations.
            // Get a list of adjacent free locations.
            Field field = getField();
            List<Location> free = field.getFreeAdjacentLocations(getLocation());
            int births = getBreedNumber(age, BREEDING_AGE, BREEDING_PROBABILITY, MAX_LITTER_SIZE);
            for(int b = 0; b < births && free.size() > 0; b++) {
                Location loc = free.remove(0);
                Bluy young = new Bluy(false, field, loc);
                young.setNatureField(natureField);
                newBluies.add(young);
            }
        }
        
        // When an nfected aniaml breeds, it can spread the disease.
        if(getIsInfected()) {
            spreadInfection(BREED_SPREAD_PROBABILITY);
        }
    }
    
    /**
     * Check whether or not there's an opposite gender Bluy in an adjacent location
     * 
     * @return Whether there's an opposite gender present
     */
    private boolean oppositeGenderAdjacent() 
    {
        boolean oppositeGender = false;
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object animal = field.getObjectAt(where);
            if(animal instanceof Bluy && !isMale){
                oppositeGender = true;
                Animal breedingAnimal = (Animal) animal;
                if(breedingAnimal.getIsInfected() && !(field.getObjectAt(getLocation()) instanceof Tree)) {
                    Animal currentAnimal = (Animal) field.getObjectAt(getLocation());
                    breedingAnimal.switchIsInfected();
                    if (currentAnimal != null) {
                        currentAnimal.switchIsInfected();
                    }
                }
            }
        }
        return oppositeGender;
    }
        
    /**
     * Sets the nature field to the current simulating nature field
     * @param natureField The current nature field occupied
     */
    public void setNatureField(NatureField natureField)
    {
        this.natureField = natureField;
    }
}
