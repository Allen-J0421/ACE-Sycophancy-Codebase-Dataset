import java.util.List;
import java.util.Iterator;
import java.util.Random;
import java.util.ArrayList;

/**
 * A class representing shared characteristics of animals.
 *
 * @version 2022.25.02
 */
public abstract class Animal
{
    // Whether the animal is alive or not.
    private boolean isAlive;
    
    // Whether the animal is infected or not
    private boolean isInfected;
    
    // The animal's field.
    private Field field;
    // The nature field
    private NatureField natureField;
    // The animal's position in the field.
    private Location location;
    
    Random rand = new Random();
    // A random number either 0 or 1 to determine the Reddy's gender
    private int randomProb = rand.nextInt(1);
    
    //The animal's gender
    protected boolean isMale;
    // The animal's age.
    protected int age;
    // The animal's food level, which is increased by eating preys.
    protected int foodLevel;
    
    /**
     * Create a new animal at location in field.
     * 
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Animal(Field field, Location location)
    {
        isAlive = true;
        this.field = field;
        setFieldLocation(location);
        isInfected = false;
        genderAssign();
    }
    
    // Default constructor for dummy objects
    public Animal()
    {
    }
    
    /**
     * Make this animal act - that is: make it do
     * whatever it wants/needs to do.
     * @param newAnimals A list to receive newly born animals.
     */
    abstract public void act(List<Animal> newAnimals, boolean isThereFog);

    /**
     * Increase the age.
     * This could result in the the animal's death.
     * 
     * @parm maxAge The animal's max age.
     */
    protected void incrementAge(int maxAge)
    {
        age++;
        if(age > maxAge) {
            setDead();
        }
    }
    
    /**
     * Make this animal more hungry. This could result in the animal's death.
     */
    protected void incrementHunger()
    {
        foodLevel--;
        if(isInfected && rand.nextDouble() <= 0.5) {
            foodLevel--;
        }
        if(foodLevel <= 0) {
            setDead();
        }
    }
    
    /**
     * Check whether the animal is alive or not.
     * @return true if the animal is still alive.
     */
    protected boolean isAlive()
    {
        return isAlive;
    }
    
    /**
     * Return whether the animal is infected or not.
     * @return true if the animal is infected
     */
    protected boolean getIsInfected()
    {
        return isInfected;
    }

    /**
     * Switch the infected status of the animal.
     * If the animal was infected, it becomes not infected.
     * If the animal was not infected, it becomes infected.
     */
    protected void switchIsInfected()
    {
        isInfected = !isInfected;
    }
    
    /**
     * Chance to spread infection to adjacent animals
     * @parm probability The probability of getting infected.
     */
    protected void spreadInfection(double probability)
    {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object animal = field.getObjectAt(where);
            
            if(animal != null && !(animal instanceof Tree)){
                Animal currentAnimal = (Animal) animal;
                if(!(currentAnimal.getIsInfected()) && rand.nextDouble() <= probability) {
                    currentAnimal.switchIsInfected();
                }
            }
        }
    }
    
    /**
     * Indicate that the animal is no longer alive.
     * It is removed from the field.
     */
    protected void setDead()
    {
        isAlive = false;
        if(location != null) {
            field.clear(location);
            location = null;
            field = null;
        }
    }

    /**
     * Return the animal's location.
     * @return The animal's location.
     */
    protected Location getLocation()
    {
        return location;
    }
    
    /**
     * Place the animal at the new location in the given field.
     * @param newLocation The animal's new location.
     */
    protected void setFieldLocation(Location newLocation)
    {
        if(location != null) {
            field.clear(location);
        }
        location = newLocation;
        field.place(this, newLocation);
    }
    
    /**
     * Place the animal at the new location in the given field.
     * @param newLocation The animal's new location.
     */
    protected void setNatureLocation(Location newLocation)
    {
        if(location != null) {
            natureField.clear(location);
        }
        location = newLocation;
        natureField.place(this, newLocation);
    }
    
    /**
     * Return the animal's field.
     * @return The animal's field.
     */
    protected Field getField()
    {
        return field;
    }
    
    /**
     * Get the breeding animal at the adjacent location 
     * 
     * @return The breeding animal
     */
    protected Animal getBreedingAnimalAdjacent(ArrayList<Animal> breedingTypeList)
    {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object currentObject = field.getObjectAt(where);
            if (currentObject != null && !(currentObject instanceof Tree) && !isMale) {
                for(int i=0; i<breedingTypeList.size(); i++) {
                    Animal animal = (Animal) currentObject;
                    if(animal.getClass() == breedingTypeList.get(i).getClass()) {
                        return animal;
                    }
                }
            }
        }
        return null;
    }
    
    /**
     * Assigns the gender of the Purply
     * 
     * @return 
    */
    
    protected boolean genderAssign() 
    {
        if (rand.nextInt(1) == 1) {
            return true;
        }
        else {
            return false;
        }  
    }
    
    /**
     * Look for prey adjacent to the current location.
     * Only the first live prey is eaten.
     * @return Where food was found, or null if it wasn't.
     */
    protected Location findFood(ArrayList<Animal> preyList, int animalFoodValue)
    {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        Location returnLocation = null;
        while(it.hasNext()) {
            Location where = it.next();
            Object currentObject = field.getObjectAt(where);
            returnLocation = getKillAnimalLocation(where, currentObject, preyList, animalFoodValue);
            if(returnLocation != null) {
                return returnLocation;
            }

         }
        return null;
    }
    
    /**
     * Eat a prey when the animal encountered a prey in a random location.
     * 
     * @parm where The prey's location, preyList The list of prey species, animalFoodValue The animal's food value
     */
    protected void eatRandomPrey(Location where, ArrayList<Animal> preyList, int animalFoodValue)
    {
        Field field = getField();
        Object currentObject = field.getObjectAt(where);
        getKillAnimalLocation(where, currentObject, preyList, animalFoodValue);
    }
    
    /**
     * Return the killed animal's location.
     * 
     * @return The killed animal's location.
     */
    protected Location getKillAnimalLocation(Location currentWhere, Object currentThisObject, ArrayList<Animal> currentPreyList, int currentAnimalFoodValue)
    {
        if (currentThisObject != null && !(currentThisObject instanceof Tree)) {
            Animal animal = (Animal) currentThisObject;
            for(int i=0; i<currentPreyList.size(); i++)
            {
                if(animal.getClass() == currentPreyList.get(i).getClass())
                {
                    if(animal.isAlive()) {
                        animal.setDead();
                        foodLevel = currentAnimalFoodValue;
                        return currentWhere;
                    }
                }
            }
        }
        return null;
    }
    
    /**
     * Generate a number representing the number of births,
     * if it can breed.
     * @parm age Age breedingAge, Age to breed,breedingProbability Probability of breeding, maxLitterSize Maximum litter size.
     * @return The number of births (may be zero).
     */
    protected int getBreedNumber(int age, double breedingAge, double breedingProbability, int maxLitterSize)
    {
        int births = 0;
        if(age>=breedingAge && rand.nextDouble() <= breedingProbability) {
            births = rand.nextInt(maxLitterSize) + 1;
        }
        return births;
    }
}
