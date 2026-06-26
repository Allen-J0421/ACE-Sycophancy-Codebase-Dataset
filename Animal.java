import java.util.List;
import java.util.Iterator;
/**
 * A class representing shared characteristics of animals.
 *
 * @version 25/02/2022
 */
public abstract class Animal extends LivingOrganism
{
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
            if(rand.nextDouble() <= (immuneProbability / 15)) {
                immune = false;
            }
        }
        
        if(isAlive()) 
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
            
            // checks to see if the animal is able to give birth
            if(this.getIsFemale()) 
            {
                if(canBreed() && rand.nextDouble() <= breedingProbability)
                {
                    populate(newAnimals);
                }
            }

            // Move towards a source of food if found.
            Location newLocation = findFood();
            
            if(newLocation == null) 
            { 
                Location possibleNewLocation = getField().freeAdjacentLocation(getLocation(), Animal.class);
                
                if (possibleNewLocation == null) 
                {
                    // no free adjacent locations therefore it is 
                    // overcrowded
                    if (rand.nextDouble() < 0.3) 
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
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation(), Animal.class);
        int births = breed();
        for(int b = 0; b < births && free.size() > 0; b++)
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
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        
        while(it.hasNext()) 
        {
            Location where = it.next();
            Animal animal = (Animal) field.getObjectAt(where, Animal.class);
            Class typeOfOtherAnimal = null;
            
            if (field.getObjectAt(where, Animal.class) != null) 
            {
                typeOfOtherAnimal  = field.getObjectAt(where, Animal.class).getClass();
            }
            
            //checks to make sure they are of the same species
            if(this.getClass().equals(typeOfOtherAnimal)) 
            {
                //checks to make the other animal is also a male
                if(animal.getIsFemale() == false) 
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
        Iterator<Location> it = adjacent.iterator();
        
        while(it.hasNext() && ! surroundingsAreInfected) 
        {
            Location where = it.next();
            Animal animal = (Animal) field.getObjectAt(where, Animal.class);
            
            if (field.getObjectAt(where, Animal.class) != null) 
            {
                if (animal.getIsInfected())
                {
                    surroundingsAreInfected = true;
                }
            }
        }
        
        return surroundingsAreInfected;
    }
}