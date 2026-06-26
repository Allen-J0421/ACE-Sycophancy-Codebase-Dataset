import java.util.Iterator;
import java.util.List;
/**
 * A class representing shared characteristics of animals.
 *
 * @version 25/02/2022
 */
public abstract class Animal extends LivingOrganism
    implements Ageable, HungerBased, DiseaseProne, Breedable, Movable
{
    private final BreedingState breedingState;
    private final AgeState ageState;
    private final HungerState hungerState;
    private final DiseaseState diseaseState;
    private final MovementState movementState;
    
    /**
     * Create a new animal at location in field.
     * 
     * @param field The field currently occupied.
     * @param location The location within the field.
     * @param infected Intial state if the animal is infected or not
     * @param immmune Intial state if the animal is immune or not
     */
    public Animal(Field field, Location location, boolean infected, boolean immune)
    {
        super(field, location);
        alive = true;
        breedingState = new BreedingState();
        ageState = new AgeState();
        hungerState = new HungerState();
        diseaseState = new DiseaseState();
        movementState = new MovementState();

        diseaseState.setInfected(infected);
        diseaseState.setImmune(immune);
        diseaseState.setDiseaseProbability(0.00000015);
        diseaseState.setDiseaseSpreadProbability(0.80);
        diseaseState.setDeathFromInfectionProbability(0.13);
        diseaseState.setImmuneProbability(0.05);

        breedingState.setFemale(rand.nextBoolean());
    }
    
    /**
     * @Override
     * 
     * Make this animal act - that is: make it do
     * whatever it wants/needs to do.
     * 
     * @param newAnimals A list to receive newly born animals.
     */
    public void act(List<LivingOrganism> newAnimals)
    {
        ageOneStep();
        digestOneStep();
        updateDiseaseState();
        
        if(isAlive()) 
        {
            exposeToDisease();
            
            if(this.getIsFemale()) 
            {
                if(canBreed() && rand.nextDouble() <= breedingState.getBreedingProbability())
                {
                    populate(newAnimals);
                }
            }

            moveOneStep();
        }
    }
    
    /**
     * Look for food adjacent to the current location.
     * Only the first live prey or plant is eaten.
     * 
     * @return Where food was found, or null if it wasn't.
     */
    abstract protected Location findFood();

    /**
     * Create a new instance of the current species.
     *
     * @param location Where the offspring should be placed.
     * @param isInfected Whether the offspring starts infected.
     * @param isImmune Whether the offspring starts immune.
     * @return A new offspring of the current species.
     */
    protected abstract Animal createOffspring(Location location, boolean isInfected, boolean isImmune);

    /**
     * @Override
     * 
     * Indicate that the animal is no longer alive.
     * It is removed from the field.
     */
    protected void setDead()
    {
        alive = false;
        
        if(location != null) 
        {
            field.clear(location, Animal.class);
            location = null;
        }
        
        if (this.getClass().equals(Lion.class) || this.getClass().equals(Cheetah.class)){
            location = null;
        }
    }
    
    /**
     * @Override
     * 
     * When called, the animal is killed and returns
     * its food value.
     * 
     * @return The food value of the animal eaten.
     */
    protected int beEaten() 
    {
        setDead();
        
        return foodValue;
    }
    
    /**
     * @Override
     * 
     * Place the animal at the new location in the given field.
     * 
     * @param newLocation The animal's new location.
     */
    protected void setLocation(Location newLocation)
    {
        if(location != null) 
        {
            if(field.getObjectAt(location, Animal.class) != null)
            {
                field.clear(location, Animal.class);
            }
        }
        
        location = newLocation;
        field.place(this, newLocation);
    }
    
    /**
     * Returns the gender of the animal.
     * 
     * @return Returns true if female.
     */
    protected boolean getIsFemale() 
    {
        return breedingState.isFemale();
    }
    
    /**
     * @Override
     * 
     * Increase the age.
     * This could result in the Animal's death.
     */
    protected void incrementAge()
    {
        ageOneStep();
    }
    
    /**
     * Make this animal's more hungry. This could result in the animal's death.
     */
    protected void incrementHunger()
    {
        digestOneStep();
    }
    
    /**
     * @Override
     * 
     * New births will be made into free adjacent locations.
     * @param newAnimals A list to return newly born Animals.
     */
    protected void populate(List<LivingOrganism> newAnimals)
    {
        populateOffspring(newAnimals);
    }
    
    /**
     * Generate a number representing the number of births,
     * if it can breed.
     * 
     * @return The number of births (may be zero).
     */
    protected int breed()
    {
        return calculateBirths();
    }

    /**
     * An Animal can breed if it has reached the breeding age.
     * 
     * @return true if the Animal can breed, false otherwise.
     */
    protected boolean canBreed()
    {
        return ageState.getAge() >= breedingState.getBreedingAge();
    }

    /**
     * Set the shared species-level characteristics for an animal.
     */
    protected final void configureAnimal(int breedingAge, int maxAge, double breedingProbability,
                                         int maxLitterSize, int maxFoodLevel, int foodValue)
    {
        breedingState.setBreedingAge(breedingAge);
        breedingState.setBreedingProbability(breedingProbability);
        breedingState.setMaxLitterSize(maxLitterSize);
        ageState.setMaxAge(maxAge);
        hungerState.setMaxFoodLevel(maxFoodLevel);
        this.foodValue = foodValue;
    }

    /**
     * Initialise age and hunger state for either a newborn or a random starting animal.
     */
    protected final void initializeAgeAndFood(boolean randomAge, int newbornFoodLevel)
    {
        if (randomAge) 
        {
            ageState.setAge(rand.nextInt(ageState.getMaxAge()));
            hungerState.setFoodLevel(rand.nextInt(hungerState.getMaxFoodLevel()));
        }
        else
        {
            ageState.setAge(0);
            hungerState.setFoodLevel(newbornFoodLevel);
        }
    }
    
    /**
     * @return Returns the status of the animal being infected or not
     */
    public boolean getIsInfected() 
    {
        return diseaseState.isInfected();
    }
    
    /**
     * @return Returns the status of the animal being immune or not
     */
    public boolean getIsImmune() 
    {
        return diseaseState.isImmune();
    }
    
    /**
     * Checks all the surroundings to see if there any animals which are infected.
     * 
     * @return Returns true if there is a surrounding animal which is infected.
     */
    public boolean surroundingsInfected()
    {
        return DiseaseProne.super.surroundingsInfected();
    }

    public BreedingState getBreedingState()
    {
        return breedingState;
    }

    public AgeState getAgeState()
    {
        return ageState;
    }

    public void applyAgeProgression()
    {
        ageState.setAge(ageState.getAge() + 1);
    }

    public boolean shouldDieFromAge()
    {
        return ageState.getAge() > ageState.getMaxAge();
    }

    public HungerState getHungerState()
    {
        return hungerState;
    }

    public DiseaseState getDiseaseState()
    {
        return diseaseState;
    }

    public MovementState getMovementState()
    {
        return movementState;
    }

    public Field currentField()
    {
        return getField();
    }

    public Location currentLocation()
    {
        return getLocation();
    }

    public boolean organismIsAlive()
    {
        return isAlive();
    }

    public void markDead()
    {
        setDead();
    }

    public Class<?> getBreedingSpaceType()
    {
        return Animal.class;
    }

    public int determineOffspringCount(int availableLocations)
    {
        return breed();
    }

    public LivingOrganism spawnOffspringAt(Location location)
    {
        boolean offspringIsInfected = diseaseState.isInfected();
        boolean offspringIsImmune = diseaseState.isImmune();

        if (!diseaseState.isImmune() && diseaseState.isInfected() && rand.nextDouble() < 0.15) 
        {
            offspringIsImmune = true;
            offspringIsInfected = false;
        }
        else if (diseaseState.isImmune() && rand.nextDouble() < 0.9)
        {
            offspringIsImmune = false;
        }

        return createOffspring(location, offspringIsInfected, offspringIsImmune);
    }

    public Location locateFoodSource()
    {
        return findFood();
    }

    public void relocate(Location newLocation)
    {
        setLocation(newLocation);
    }

    private int calculateBirths()
    {
        int births = 0;
        
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        
        while(it.hasNext()) 
        {
            Location where = it.next();
            Animal animal = field.getObjectAt(where, Animal.class);
            
            if (animal != null && this.getClass().equals(animal.getClass())) 
            {
                if(!animal.getBreedingState().isFemale()) 
                {
                    births = rand.nextInt(breedingState.getMaxLitterSize()) + 1;
                }
            }
        }
        
        return births;
    }
}
