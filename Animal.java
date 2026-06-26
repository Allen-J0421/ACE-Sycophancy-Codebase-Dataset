import java.util.List;
import java.util.ArrayList;
/**
 * A class representing shared characteristics of animals.
 *
 * @version 25/02/2022
 */
public abstract class Animal extends LivingOrganism
{
    // Indicates whether an animal is a female or not;
    protected boolean isFemale;    
    // The age at which an animal can start to breed.
    protected int breedingAge;
    // The age to which an animal can live.
    protected int maxAge;
    // The likelihood of an animal breeding.
    protected double breedingProbability;
    // whther the animal is infected or not
    protected boolean infected;
    // The likelihood of an animal having a disease.
    protected double diseaseProbability;
    // The likelihood of an animal catching a disease from another animal
    protected double diseaseSpreadProbability;
    // The likelihood of an animal dying from catching the disease
    protected double deathFromInfectionProbability;
    // The likelihood of an animal becomming immune to the disease
    protected double immuneProbability;
    // whether the animal is immune or not
    protected boolean immune;
    // The maximum number of births.
    protected int maxLitterSize;
    // The animals's age.
    protected int age;
    // The animals's food level, which is increased by eating prey.
    protected int foodLevel;
    // The maximum food level that an animal will eat at. The food level they will stop
    // being hungry at
    protected int maxFoodLevel;
    // Probability that an animal will move at a given step.
    protected double movementProbability;

    // Probability an animal dies when boxed in with no free adjacent cell.
    private static final double OVERCROWDING_DEATH_PROBABILITY = 0.3;
    // An immune animal loses its immunity each step with probability
    // immuneProbability scaled down by this divisor.
    private static final double IMMUNITY_LOSS_DIVISOR = 15;
    // Probability the offspring of an infected, non-immune mother is born immune.
    private static final double OFFSPRING_IMMUNITY_INHERITANCE_PROBABILITY = 0.15;
    // Probability the offspring of an immune mother is NOT born immune.
    private static final double OFFSPRING_IMMUNITY_LOSS_PROBABILITY = 0.9;

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
        this.infected = infected;
        this.immune = immune;
        
        diseaseProbability = 0.00000015;
        diseaseSpreadProbability = 0.80;
        deathFromInfectionProbability = 0.13;
        immuneProbability = 0.05;
        
        isFemale = rand.nextBoolean();
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
        incrementAge();
        incrementHunger();

        updateInfectionState();

        if(isAlive())
        {
            tryToCatchDisease();
            tryToBreed(newAnimals);
            move();
        }
    }

    /**
     * Resolve the animal's current infection: an infected animal may die of the
     * disease or develop immunity to it; an animal that is already immune may
     * lose that immunity over time.
     */
    private void updateInfectionState()
    {
        // checks to see if the animal is going to die from it infection
        // or become immune
        // if the animal becomes immune then it no longer is infected
        if (!getIsImmune() && getIsInfected())
        {
            if(rand.nextDouble() <= deathFromInfectionProbability)
            {
                setDead();
            }
            else if(rand.nextDouble() <= immuneProbability) {
                immune = true;
                infected = false;
            }
        }
        // If the animal is immune and not infected, there is a chance
        // of losing immunity.
        else
        {
            if(rand.nextDouble() <= (immuneProbability / IMMUNITY_LOSS_DIVISOR)) {
                immune = false;
            }
        }
    }

    /**
     * A healthy (non-immune, non-infected) animal may catch a disease from an
     * infected neighbour, or spontaneously develop one.
     */
    private void tryToCatchDisease()
    {
        if(!getIsImmune() && !getIsInfected())
        {
            // checks to see if the animal is going to catch a disease from
            // its surroundings.

            if (surroundingsInfected() && rand.nextDouble() <= diseaseSpreadProbability)
            {
                infected = true;
            }

            // checks to see if the animal is going to get a disease out
            // of nowhere.
            else if (rand.nextDouble() <= diseaseProbability)
            {
                infected = true;
            }
        }
    }

    /**
     * A female animal that has reached breeding age may give birth this step.
     *
     * @param newAnimals A list to receive newly born animals.
     */
    private void tryToBreed(List<LivingOrganism> newAnimals)
    {
        // checks to see if the animal is able to give birth
        if(this.getIsFemale())
        {
            if(canBreed() && rand.nextDouble() <= breedingProbability)
            {
                populate(newAnimals);
            }
        }
    }

    /**
     * Move towards adjacent food if any is found; otherwise possibly move to a
     * free neighbouring cell, or die from overcrowding when boxed in.
     */
    private void move()
    {
        // Move towards a source of food if found.
        Location newLocation = findFood();

        if(newLocation == null)
        {
            Location possibleNewLocation = getField().freeAdjacentLocation(getLocation(), Animal.class);

            if (possibleNewLocation == null)
            {
                // no free adjacent locations therefore it is
                // overcrowded
                if (rand.nextDouble() < OVERCROWDING_DEATH_PROBABILITY)
                {
                    setDead();
                }
            }

            // No food found and there is a free location - move there.
            if (rand.nextDouble() <= movementProbability)
            {
                newLocation = possibleNewLocation;
            }
        }

        // Move to new location
        if(newLocation != null)
        {
            setLocation(newLocation);
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
        return isFemale;
    }
    
    /**
     * @Override
     * 
     * Increase the age.
     * This could result in the Animal's death.
     */
    protected void incrementAge()
    {
        age++;
        if(age > maxAge) {
            setDead();
        }
    }
    
    /**
     * Make this animal's more hungry. This could result in the animal's death.
     */
    protected void incrementHunger()
    {
        foodLevel--;
        if(foodLevel <= 0) {
            setDead();
        }
    }
    
    /**
     * @Override
     * 
     * New births will be made into free adjacent locations.
     * @param newAnimals A list to return newly born Animals.
     */
    protected void populate(List<LivingOrganism> newAnimals)
    {
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation(), Animal.class);
        //determines the number of offspring the animal will produce
        
        int births = breed();
        // New animals are born into adjacent locations.

        for(int b = 0; b < births && free.size() > 0; b++)
        {
            Animal newAnimal = createNewOffspring(free, infected, immune);
            newAnimals.add(newAnimal);
        }
    }

    /**
     * Creates a new offspring and places it into the field at the first free location.
     * The concrete species of the offspring is determined polymorphically by
     * {@link #createOffspring}, so this method needs no knowledge of subtypes.
     *
     * @param free A list of the free adjacent locations.
     * @param motherIsInfected If the mother currently has the disease, so will the child.
     * @param motherIsImmune If the mother is currently immune.
     *
     * @return Returns the new animal.
     */
    protected Animal createNewOffspring(List<Location> free, boolean motherIsInfected, boolean motherIsImmune)
    {
        Location loc = free.remove(0);

        boolean offspringIsInfected = motherIsInfected;
        boolean offspringIsImmune = motherIsImmune;

        // If the mother is immune and the mother is infected, then there is
        // a small of the child getting immunity
        if (!motherIsImmune && motherIsInfected && rand.nextDouble() < OFFSPRING_IMMUNITY_INHERITANCE_PROBABILITY)
        {
            offspringIsImmune = true;
            offspringIsInfected = false;
        }
        // If the mother is immune then there is a chance child isn't immune
        else if (motherIsImmune && rand.nextDouble() < OFFSPRING_IMMUNITY_LOSS_PROBABILITY)
        {
            offspringIsImmune = false;
        }

        return createOffspring(field, loc, offspringIsInfected, offspringIsImmune);
    }

    /**
     * Create a new-born offspring of this animal's own species. Each concrete
     * species supplies its own constructor call, replacing the former
     * class-based if/else dispatch.
     *
     * @param field The field the offspring is born into.
     * @param location The free location the offspring is placed at.
     * @param isInfected Whether the offspring starts infected.
     * @param isImmune Whether the offspring starts immune.
     *
     * @return The new offspring.
     */
    protected abstract Animal createOffspring(Field field, Location location, boolean isInfected, boolean isImmune);
    
    /**
     * Collect the animals occupying the locations adjacent to this one, in the
     * (shuffled) order the field returns them.
     *
     * @return The neighbouring animals; empty if there are none.
     */
    private List<Animal> getAdjacentAnimals()
    {
        List<Animal> neighbours = new ArrayList<>();
        Field field = getField();

        for(Location where : field.adjacentLocations(getLocation()))
        {
            Animal animal = (Animal) field.getObjectAt(where, Animal.class);
            if(animal != null)
            {
                neighbours.add(animal);
            }
        }

        return neighbours;
    }

    /**
     * Generate a number representing the number of births,
     * if it can breed.
     *
     * @return The number of births (may be zero).
     */
    protected int breed()
    {
        int births = 0;

        for(Animal neighbour : getAdjacentAnimals())
        {
            // breed only with an adjacent male of the same species
            if(this.getClass().equals(neighbour.getClass()) && !neighbour.getIsFemale())
            {
                births = rand.nextInt(maxLitterSize) + 1;
            }
        }

        return births;
    }

    /**
     * An Animal can breed if it has reached the breeding age.
     * 
     * @return true if the Animal can breed, false otherwise.
     */
    protected boolean canBreed()
    {
        return age >= breedingAge;
    }
    
    /**
     * @return Returns the status of the animal being infected or not
     */
    public boolean getIsInfected() 
    {
        return infected;
    }
    
    /**
     * @return Returns the status of the animal being immune or not
     */
    public boolean getIsImmune() 
    {
        return immune;
    }
    
    /**
     * Checks all the surroundings to see if there any animals which are infected.
     * 
     * @return Returns true if there is a surrounding animal which is infected.
     */
    protected boolean surroundingsInfected()
    {
        for(Animal neighbour : getAdjacentAnimals())
        {
            if(neighbour.getIsInfected())
            {
                return true;
            }
        }

        return false;
    }
}