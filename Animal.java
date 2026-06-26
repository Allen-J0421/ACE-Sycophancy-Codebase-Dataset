import java.util.List;
/**
 * A class representing shared characteristics of animals.
 *
 * @version 25/02/2022
 */
public abstract class Animal extends LivingOrganism
{
    // Default disease probabilities shared by all species.
    private static final double DISEASE_PROBABILITY         = 0.00000015;
    private static final double DISEASE_SPREAD_PROBABILITY  = 0.80;
    private static final double INFECTION_DEATH_PROBABILITY = 0.13;
    private static final double IMMUNE_PROBABILITY          = 0.05;

    /**
     * Immutable value object holding the species-specific parameters that
     * every animal subclass must supply. Stored as a static constant per
     * species and applied via {@link #applyConfig(SpeciesConfig)}.
     */
    protected static class SpeciesConfig {
        final int breedingAge;
        final int maxAge;
        final double breedingProbability;
        final int maxLitterSize;
        final int maxFoodLevel;
        final int foodValue;

        SpeciesConfig(int breedingAge, int maxAge, double breedingProbability,
                      int maxLitterSize, int maxFoodLevel, int foodValue) {
            this.breedingAge = breedingAge;
            this.maxAge = maxAge;
            this.breedingProbability = breedingProbability;
            this.maxLitterSize = maxLitterSize;
            this.maxFoodLevel = maxFoodLevel;
            this.foodValue = foodValue;
        }
    }

    // Indicates whether an animal is a female or not;
    protected boolean isFemale;
    // The age at which an animal can start to breed.
    protected int breedingAge;
    // The age to which an animal can live.
    protected int maxAge;
    // The likelihood of an animal breeding.
    protected double breedingProbability;
    // Whether the animal is infected or not
    protected boolean infected;
    // The likelihood of an animal having a disease.
    protected double diseaseProbability;
    // The likelihood of an animal catching a disease from another animal
    protected double diseaseSpreadProbability;
    // The likelihood of an animal dying from catching the disease
    protected double deathFromInfectionProbability;
    // The likelihood of an animal becoming immune to the disease
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
     * @param infected Initial state if the animal is infected or not
     * @param immune   Initial state if the animal is immune or not
     */
    public Animal(Field field, Location location, boolean infected, boolean immune)
    {
        super(field, location);
        alive = true;
        this.infected = infected;
        this.immune = immune;
        
        diseaseProbability = DISEASE_PROBABILITY;
        diseaseSpreadProbability = DISEASE_SPREAD_PROBABILITY;
        deathFromInfectionProbability = INFECTION_DEATH_PROBABILITY;
        immuneProbability = IMMUNE_PROBABILITY;
        
        isFemale = rand.nextBoolean();
    }
    
    /**
     * Make this animal act - that is: make it do
     * whatever it wants/needs to do.
     *
     * @param newAnimals A list to receive newly born animals.
     */
    @Override
    public void act(List<LivingOrganism> newAnimals)
    {
        incrementAge();
        incrementHunger();
        updateDiseaseState();

        if(isAlive())
        {
            tryContractDisease();

            if(isFemale && canBreed() && rand.nextDouble() <= breedingProbability)
            {
                populate(newAnimals);
            }

            tryMove();
        }
    }

    /**
     * Progress the animal's disease/immunity status by one step:
     * an infected animal may die or recover; an immune animal may lose immunity.
     */
    private void updateDiseaseState()
    {
        if(!immune && infected)
        {
            if(rand.nextDouble() <= deathFromInfectionProbability)
            {
                setDead();
            }
            else if(rand.nextDouble() <= immuneProbability)
            {
                immune = true;
                infected = false;
            }
        }
        else if(rand.nextDouble() <= immuneProbability / 15)
        {
            // Non-infected animals slowly lose immunity over time
            immune = false;
        }
    }

    /**
     * Attempt to infect a susceptible (non-immune, non-infected) animal,
     * either via a neighbouring infected animal or spontaneously.
     */
    private void tryContractDisease()
    {
        if(immune || infected)
        {
            return;
        }
        if(surroundingsInfected() && rand.nextDouble() <= diseaseSpreadProbability)
        {
            infected = true;
        }
        else if(rand.nextDouble() <= diseaseProbability)
        {
            infected = true;
        }
    }

    /**
     * Attempt to move: first towards food, then to any free adjacent cell.
     * Dies from overcrowding if no adjacent cell is free.
     */
    private void tryMove()
    {
        Location newLocation = findFood();

        if(newLocation == null)
        {
            Location freeLocation = getField().freeAdjacentLocation(getLocation(), Animal.class);

            if(freeLocation == null && rand.nextDouble() < 0.3)
            {
                setDead();
            }

            if(rand.nextDouble() <= movementProbability)
            {
                newLocation = freeLocation;
            }
        }

        if(newLocation != null)
        {
            setLocation(newLocation);
        }
    }
    
    /**
     * Copy the species-specific parameters from a {@link SpeciesConfig} into
     * this animal's instance fields. Call this once in each concrete
     * subclass constructor after calling super().
     */
    protected void applyConfig(SpeciesConfig config) {
        breedingAge = config.breedingAge;
        maxAge = config.maxAge;
        breedingProbability = config.breedingProbability;
        maxLitterSize = config.maxLitterSize;
        maxFoodLevel = config.maxFoodLevel;
        foodValue = config.foodValue;
    }

    /**
     * Look for food adjacent to the current location.
     * Only the first live prey or plant is eaten.
     *
     * @return Where food was found, or null if it wasn't.
     */
    abstract protected Location findFood();

    /**
     * Instantiate a new offspring of this species at the given location.
     * Each concrete species class overrides this instead of requiring
     * {@link #createNewOffspring} to maintain a per-species if-chain.
     *
     * @param loc      The location for the new animal.
     * @param infected Whether the offspring starts infected.
     * @param immune   Whether the offspring starts immune.
     * @return A new animal of the same species as this one.
     */
    protected abstract Animal createOffspringAt(Location loc, boolean infected, boolean immune);

    /**
     * Indicate that the animal is no longer alive.
     * It is removed from the field.
     */
    @Override
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
     * When called, the animal is killed and returns
     * its food value.
     *
     * @return The food value of the animal eaten.
     */
    @Override
    protected int beEaten() 
    {
        setDead();
        
        return foodValue;
    }
    
    /**
     * Place the animal at the new location in the given field.
     *
     * @param newLocation The animal's new location.
     */
    @Override
    protected void setLocation(Location newLocation)
    {
        if(location != null)
        {
            field.clear(location, Animal.class);
        }
        location = newLocation;
        field.place(this, newLocation);
    }
    
    /**
     * @return True if the animal is female.
     */
    protected boolean isFemale()
    {
        return isFemale;
    }
    
    /**
     * Increase the age.
     * This could result in the Animal's death.
     */
    @Override
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
     * New births will be made into free adjacent locations.
     *
     * @param newAnimals A list to return newly born Animals.
     */
    @Override
    protected void populate(List<LivingOrganism> newAnimals)
    {
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation(), Animal.class);
        int births = breed();
        for(int b = 0; b < births && !free.isEmpty(); b++)
        {
            newAnimals.add(createNewOffspring(free, infected, immune));
        }
    }

    /**
     * Remove a free location from the list, apply disease-inheritance logic,
     * and delegate actual instantiation to {@link #createOffspringAt}.
     */
    protected Animal createNewOffspring(List<Location> free, boolean motherIsInfected, boolean motherIsImmune)
    {
        Location loc = free.remove(0);

        boolean offspringIsInfected = motherIsInfected;
        boolean offspringIsImmune = motherIsImmune;

        // Infected-but-not-immune mother: small chance offspring gains immunity instead
        if (!motherIsImmune && motherIsInfected && rand.nextDouble() < 0.15)
        {
            offspringIsImmune = true;
            offspringIsInfected = false;
        }
        // Immune mother: most offspring do not inherit immunity
        else if (motherIsImmune && rand.nextDouble() < 0.9)
        {
            offspringIsImmune = false;
        }

        return createOffspringAt(loc, offspringIsInfected, offspringIsImmune);
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
        Field field = getField();
        for(Location where : field.adjacentLocations(getLocation()))
        {
            Animal neighbor = (Animal) field.getObjectAt(where, Animal.class);
            if(neighbor != null && getClass().equals(neighbor.getClass()) && !neighbor.isFemale())
            {
                births = rand.nextInt(maxLitterSize) + 1;
                break;
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
     * @return True if the animal is currently infected.
     */
    public boolean isInfected()
    {
        return infected;
    }

    /**
     * @return True if the animal is currently immune.
     */
    public boolean isImmune()
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
        Field field = getField();
        for(Location where : field.adjacentLocations(getLocation()))
        {
            Animal neighbor = (Animal) field.getObjectAt(where, Animal.class);
            if(neighbor != null && neighbor.isInfected())
            {
                return true;
            }
        }
        return false;
    }
}