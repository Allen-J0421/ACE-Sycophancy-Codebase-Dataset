import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Iterator;
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
    
    /**
     * @return the set of plant types this herbivore grazes on.
     */
    protected abstract List<Class<? extends Plant>> getTargetPlants();

    /**
     * Herbivores feed by grazing on a live plant of their target diet at the
     * given location.
     *
     * @param where the adjacent location to inspect.
     * @return true if a plant was found and grazed.
     */
    @Override
    protected boolean eatAt(Location where)
    {
        Plant plant = getField().getPlantAt(where);
        if(plant != null && getTargetPlants().contains(plant.getClass())) {
            if(plant.isAlive()) {
                this.foodLevel += plant.getFeedingValue();
                plant.setDead();
                return true;
            }
        }
        return false;
    }
}
