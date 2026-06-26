import java.util.List;
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

    /**
     * Create a new animal at location in field.
     *
     * @param field The field currently occupied.
     * @param location The location within the field.
     * @param infected Intial state if the animal is infected or not
     * @param immmune Intial state if the animal is immune or not
     */
    private Animal(Field field, Location location, boolean infected, boolean immune)
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
     * Create an animal with species-specific behavior values.
     */
    @SuppressWarnings("this-escape")
    protected Animal(Field field, Location location, boolean infected, boolean immune,
                     int breedingAge, int maxAge, double breedingProbability, int maxLitterSize,
                     int maxFoodLevel, int foodValue, boolean randomAge, double newbornFoodPercentage,
                     boolean randomNewbornFoodPercentage)
    {
        this(field, location, infected, immune);

        configureSpecies(breedingAge, maxAge, breedingProbability, maxLitterSize, maxFoodLevel, foodValue);
        initialiseAgeAndFoodLevel(randomAge, newbornFoodPercentage, randomNewbornFoodPercentage);
        setLocation(location);
    }

    /**
     * Set the species-specific behavior values shared by all animals.
     */
    private void configureSpecies(int breedingAge, int maxAge, double breedingProbability,
                                  int maxLitterSize, int maxFoodLevel, int foodValue)
    {
        this.breedingAge = breedingAge;
        this.maxAge = maxAge;
        this.breedingProbability = breedingProbability;
        this.maxLitterSize = maxLitterSize;
        this.maxFoodLevel = maxFoodLevel;
        this.foodValue = foodValue;
    }

    /**
     * Set starting age and food level for either an existing animal or a newborn.
     */
    private void initialiseAgeAndFoodLevel(boolean randomAge, double newbornFoodPercentage,
                                           boolean randomNewbornFoodPercentage)
    {
        if (randomAge)
        {
            age = rand.nextInt(maxAge);
            foodLevel = rand.nextInt(maxFoodLevel);
        }
        else
        {
            age = 0;

            double percentageOfMaxFoodLevel = newbornFoodPercentage;
            if (randomNewbornFoodPercentage)
            {
                percentageOfMaxFoodLevel = rand.nextDouble() / 5.5;
            }

            foodLevel = (int) (percentageOfMaxFoodLevel * maxFoodLevel);
        }
    }

    /**
     * @Override
     *
     * Make this animal act - that is: make it do
     * whatever it wants/needs to do.
     *
     * @param newAnimals A list to receive newly born animals.
     */
    public void act(List<Animal> newAnimals)
    {
        incrementAge();
        incrementHunger();
        updateDiseaseOutcome();

        if(isAlive())
        {
            maybeCatchDisease();
            maybeBreed(newAnimals);
            move();
        }
    }

    /**
     * Update disease effects that can kill, grant immunity, or remove immunity.
     */
    private void updateDiseaseOutcome()
    {
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
        else
        {
            if(rand.nextDouble() <= (immuneProbability / 15)) {
                immune = false;
            }
        }
    }

    /**
     * Try to catch a disease from nearby animals or random infection.
     */
    private void maybeCatchDisease()
    {
        if(!getIsImmune() && !getIsInfected())
        {
            if (surroundingsInfected() && rand.nextDouble() <= diseaseSpreadProbability)
            {
                infected = true;
            }
            else if (rand.nextDouble() <= diseaseProbability)
            {
                infected = true;
            }
        }
    }

    /**
     * Breed when this animal is eligible and probability allows it.
     */
    private void maybeBreed(List<Animal> newAnimals)
    {
        if(this.getIsFemale())
        {
            if(canBreed() && rand.nextDouble() <= breedingProbability)
            {
                populate(newAnimals);
            }
        }
    }

    /**
     * Move towards food or an available nearby location.
     */
    private void move()
    {
        Location newLocation = findFood();

        if(newLocation == null)
        {
            newLocation = findMovementLocation();
        }

        if(newLocation != null)
        {
            setLocation(newLocation);
        }
    }

    /**
     * Pick a non-food movement location, with overcrowding risk when boxed in.
     */
    private Location findMovementLocation()
    {
        Location possibleNewLocation = getField().freeAdjacentLocation(getLocation(), Animal.class);

        if (possibleNewLocation == null)
        {
            if (rand.nextDouble() < 0.3)
            {
                setDead();
            }
        }

        if (rand.nextDouble() <= movementProbability)
        {
            return possibleNewLocation;
        }

        return null;
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
    @SuppressWarnings("this-escape")
    protected final void setLocation(Location newLocation)
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
    protected void populate(List<Animal> newAnimals)
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
     * Creates a new offspring and places it into the field at the first free location
     *
     * @param free A list of the free adjacent location
     * @param motherIsInfected If the mother has currently the disease, so will the child
     *
     * @return Returns the new animal
     */
    protected Animal createNewOffspring(List<Location> free, boolean motherIsInfected, boolean motherIsImmune)
    {
        Location loc = free.remove(0);

        boolean offspringIsInfected = motherIsInfected;
        boolean offspringIsImmune = motherIsImmune;

        // If the mother is immune and the mother is infected, then there is
        // a small of the child getting immunity
        if (!motherIsImmune && motherIsInfected && rand.nextDouble() < 0.15)
        {
            offspringIsImmune = true;
            offspringIsInfected = false;
        }
        // If the mother is immune then there is a chance child isn't immune
        else if (motherIsImmune && rand.nextDouble() < 0.9)
        {
            offspringIsImmune = false;
        }

        return createOffspring(loc, offspringIsInfected, offspringIsImmune);
    }

    /**
     * Create a new animal of this animal's concrete species.
     */
    protected abstract Animal createOffspring(Location loc, boolean isInfected, boolean isImmune);

    /**
     * Generate a number representing the number of births,
     * if it can breed.
     *
     * @return The number of births (may be zero).
     */
    protected int breed()
    {
        int births = 0;

        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());

        for(Location where : adjacent)
        {
            Animal animal = field.getObjectAt(where, Animal.class);

            if(animal != null && this.getClass().equals(animal.getClass()))
            {
                //checks to make the other animal is also a male
                if(! animal.getIsFemale())
                {
                    births = rand.nextInt(maxLitterSize) + 1;
                }
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
        boolean surroundingsAreInfected = false;

        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());

        for(Location where : adjacent)
        {
            Animal animal = field.getObjectAt(where, Animal.class);

            if (animal != null && animal.getIsInfected())
            {
                surroundingsAreInfected = true;
                break;
            }
        }

        return surroundingsAreInfected;
    }
}
