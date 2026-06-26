import java.util.Iterator;
import java.util.List;
import java.util.Set;
/**
 * An extension of the Animal Class that can only eat a specified type of plants
 *
 * @version 1.0
 */
public abstract class HerbivoreAnimal extends Animal
{
    
    /*///////////////////////////////////////////////////////////////
                                CONSTRUCTOR
    //////////////////////////////////////////////////////////////*/

    /**
     * Creates a Herbivore Animal.
     * 
     * @param randomAge Boolean flag dictating whether or not the attributes will be assigned a random value
     * @param field The environment the animal can move in
     * @param location Location of the animal in the field/grid
     * @param gender Gender of the animal
     * @param baseLevel the base hunger index associated with the animal
     * @param maxAge the maximum age an animal can take before he dies.
     */
    
    public HerbivoreAnimal(boolean randomAge,Field field, Location location, Gender gender, int baseLevel, int maxAge)
    {
        super(randomAge, field, location, gender, baseLevel, maxAge);
    }

    protected abstract int getMaxAge();

    protected abstract int getBreedingAge();

    protected abstract double getBreedingProbability(Weather weather);

    protected abstract int getMaxLitterSize();

    protected abstract Set<PlantSpecies> getTargetPlants();

    @Override
    public final void act(List<Actor> newAnimals, Weather weather, DayState dayState)
    {
        incrementAge(getMaxAge());
        incrementHunger();
        if(!isAlive()) {
            return;
        }
        meet(newAnimals, getMaxLitterSize(), getBreedingProbability(weather), getBreedingAge());
        Location newLocation = findFood(getTargetPlants());
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
     * Seek for the food in adjacent locations that are of type of plant
     * @param targetPlants the preferred diet of the animal (e.g eats only grass)
     * @return the location traveled to in order to access the food
     */
    public Location findFood(Set<PlantSpecies> targetPlants) 
    {
        Field field = getField();
        Iterator<Location> it = field.adjacentLocations(getLocation()).iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Plant plant = field.getPlantAt(where);
            if(plant != null && targetPlants.contains(plant.getSpecies())) {
                if(plant.isAlive()) {
                    this.foodLevel += plant.getFeedingValue();
                    plant.setDead();
                    return where;
                }
            }
        }
        return null;
    }
}
