import java.util.Iterator;
import java.util.List;
import java.util.Set;
/**
 * An extension of the Animal Class that can only eat a specified type of animals
 *
 * @version 1.0
 */
public abstract class CarnivoreAnimal extends Animal
{
    
    /*///////////////////////////////////////////////////////////////
                                CONSTRUCTOR
    //////////////////////////////////////////////////////////////*/
    
    /**
     * Creates a Carnivore Animal.
     * 
     * @param randomAge Boolean flag dictating whether or not the attributes will be assigned a random value
     * @param field The environment the animal can move in
     * @param location Location of the animal in the field/grid
     * @param gender Gender of the animal
     * @param baseLevel the base hunger index associated with the animal
     * @param maxAge the maximum age an animal can take before he dies.
     */
    
    public CarnivoreAnimal(boolean randomAge,
                            Field field,
                            Location location,
                            Gender gender,
                            int baseLevel,
                            int maxLevel
                            )
    {
        super(randomAge,field, location, gender, baseLevel, maxLevel);
    }

    protected abstract int getMaxAge();

    protected abstract int getBreedingAge();

    protected abstract double getBreedingProbability(Weather weather);

    protected abstract int getMaxLitterSize();

    protected abstract Set<AnimalSpecies> getPreyDiet();

    protected boolean canAct(Weather weather, DayState dayState)
    {
        return true;
    }

    protected void onFoodFound(Location location)
    {
    }

    @Override
    public final void act(List<Actor> newAnimals, Weather weather, DayState dayState)
    {
        if(!canAct(weather, dayState)) {
            return;
        }
        incrementAge(getMaxAge());
        incrementHunger();
        if(!isAlive()) {
            return;
        }
        meet(newAnimals, getMaxLitterSize(), getBreedingProbability(weather), getBreedingAge());
        Location newLocation = findFood(getPreyDiet());
        if(newLocation != null) {
            onFoodFound(newLocation);
        }
        if(newLocation == null) {
            newLocation = getField().freeAdjacentLocation(getLocation());
        }
        if(newLocation != null) {
            setLocation(newLocation);
        }
        else {
            setDead();
        }
    }
    
    /**
     * Seek for the food in adjacent locations that are of type of plant.
     * 
     * @param preys The preys of the animal.
     * @return the location traveled to in order to access the food.
     */
    public Location findFood(Set<AnimalSpecies> preys)
    {   
        Field field = getField();
        Iterator<Location> it = field.adjacentLocations(getLocation()).iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Animal animal = field.getAnimalAt(where);
            if(animal != null && preys.contains(animal.getSpecies())) {
                if(animal.isAlive()) {
                    animal.setDead();
                    
                    this.foodLevel = animal.getFeedingValue();
                    return where;
                }
            }
        }
        return null; 
    }
}
