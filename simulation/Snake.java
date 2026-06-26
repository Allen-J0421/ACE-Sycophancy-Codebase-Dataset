package simulation;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

import configuration.Configuration;

/**
 * A simple model of a snake.
 * Snakes age, move, eat rats, breed, and die.
 *
 * @version 01.03.22
 */
public class Snake extends Animal
{
    private static final Configuration.AnimalTuning TUNING = Configuration.defaults().species().snake();
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();

    /**
     * Create a snake. A snake can be created as a newborn (age zero
     * and not hungry) or with a random age and food level.
     *
     * @param randomAge If true, the snake will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Snake(boolean randomAge, Field field, Location location) {
        super(field, location);
        this.setGender();
        if(randomAge) {
            setAge( rand.nextInt(TUNING.getMaxAge()));
            setFoodLevel(rand.nextInt(TUNING.getNewbornFoodLevel()));
        }
        else {
            setAge(0);
            setFoodLevel(TUNING.getNewbornFoodLevel());
        }
    }

    /**
     * This is what the snake does most of the time: it hunts for
     * rats. In the process, it might breed, die of hunger,
     * or die of old age.
     * @param time the current time in the simulation
     * @param newSnakes A list to return newly born snakes.
     */
    public void act(List<Animal> newSnakes, int time) {
        incrementAge(TUNING.getMaxAge());
        incrementHunger();
        if(isAlive() && TUNING.isActive(time))
        {
            if (getDisease()){
                spreadDisease();
            }
            if (giveBirth(TUNING.getBreedingAge())) {
                breedOffspring(newSnakes, TUNING.getBreedingAge(),
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
     * Look for rats adjacent to the current location.
     * Only the first live ant is eaten.
     * If it is a plant, it can be 'trampled'
     * @return Where food was found, or null if it wasn't.
     */
    private Location findFood()
    {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object animal = field.getObjectAt(where);
            if(animal instanceof Rat) {
                Rat rat = (Rat) animal;
                if(rat.isAlive()) {
                    rat.setDead();
                    setFoodLevel(TUNING.foodValueFor(Configuration.SpeciesId.RAT));
                    return where;
                }
            }
            else if (animal instanceof Plant){
                Plant plant = (Plant) animal;
                if(plant.isAlive()) {
                    plant.setDead();
                    return where;
                }
            }
        }
        return null;
    }

    /**
     * Create a new snake offspring.
     */
    protected Animal createOffspring(Field field, Location location) {
        return new Snake(false, field, location);
    }

}
