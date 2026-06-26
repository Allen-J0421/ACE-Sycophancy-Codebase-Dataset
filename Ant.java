import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * A simple model of an ant.
 * Ants age, move, eat acacia and grass, breed, and die.
 *
 * @version 01.03.22
 */
public class Ant extends Animal
{
    private static final SpeciesTuning.AnimalTuning TUNING = SpeciesTuning.ant();
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();

    /**
     * Create an ant. An ant can be created as a newborn (age zero
     * and not hungry) or with a random age and food level.
     *
     * @param randomAge If true, the ant will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Ant(boolean randomAge, Field field, Location location) {
        super(field, location);
        this.setGender();
        if(randomAge) {
            setAge(rand.nextInt(TUNING.getMaxAge()));
            setFoodLevel(rand.nextInt(TUNING.getNewbornFoodLevel()));
        }
        else {
            setAge(0);
            setFoodLevel(TUNING.getNewbornFoodLevel());
        }
    }

    /**
     * This is what the ant does most of the time: it eats grass
     * and acacia. In the process, it might breed, die of hunger,
     * or die of old age.
     * @param newAnts A list to return newly born ants.
     * @param time the current time in the simulation
     */
    public void act(List<Animal> newAnts, int time) {
        incrementAge(TUNING.getMaxAge());
        incrementHunger();

        if(isAlive() && TUNING.isActive(time))
        {
            if (getDisease()) {
                spreadDisease();
            }
            if (giveBirth(TUNING.getBreedingAge())) {
                breedOffspring(newAnts, TUNING.getBreedingAge(),
                        TUNING.getBreedingProbability(), TUNING.getMaxLitterSize());
            }

            Location newLocation = findFood();
            if(newLocation == null) {
                newLocation = getField().freeAdjacentLocation(getLocation());
            }
            moveOrDie(newLocation);
        }
    }

    /**
     * Look for acacia and grass adjacent to the current location.
     * Only the first grass or acacia is eaten.
     * @return Where food was found, or null if it wasn't.
     */
    private Location findFood() {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object plant = field.getObjectAt(where);
            if(plant instanceof Acacia) {
                Acacia acacia = (Acacia) plant;
                if (acacia.isAlive()) {
                    acacia.setDead();
                    setFoodLevel(TUNING.foodValueFor(Acacia.class));
                    return where;
                }
            }
            else if (plant instanceof Grass) {
                Grass grass = (Grass) plant;
                if(grass.isAlive()) {
                    grass.setDead();
                    setFoodLevel(TUNING.foodValueFor(Grass.class));
                    return where;
                }
            }
        }
        return null;
    }

    /**
     * Create a new ant offspring.
     */
    protected Animal createOffspring(Field field, Location location) {
        return new Ant(false, field, location);
    }

}
