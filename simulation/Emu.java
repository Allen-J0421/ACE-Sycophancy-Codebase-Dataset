package simulation;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

import configuration.Configuration;

/**
 * A simple model of an emu.
 * Emu age, move, eat grass, and die.
 *
 * @version 01.03.22
 */
public class Emu extends Animal
{
    private static final Configuration.AnimalTuning TUNING = Configuration.defaults().species().emu();
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();

    /**
     * Create an emu. An emu can be created as a newborn (age zero
     * and not hungry) or with a random age and food level.
     *
     * @param randomAge If true, the emu will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Emu(boolean randomAge, SimulationContext context, Location location) {
        super(context, location);
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
     * This is what the emu does most of the time: it eats grass.
     * In the process, it might breed, die of hunger,
     * or die of old age.
     * @param newEmus A list to return newly born emus.
     * @param time the current time in the simulation
     */
    public void act(int time) {
        incrementAge(TUNING.getMaxAge());
        incrementHunger();

        if(isAlive() && TUNING.isActive(time)) {
            if (getDisease()){
                spreadDisease();
            }
            if (giveBirth(TUNING.getBreedingAge())) {
                breedOffspring(TUNING.getBreedingAge(),
                        TUNING.getBreedingProbability(), TUNING.getMaxLitterSize());
            }
            Location newLocation = findFood();
            if(newLocation == null) {
                newLocation = getContext().freeAdjacentLocation(getLocation());
            }
            moveOrDie(newLocation);
        }
    }


    /**
     * Look for grass adjacent to the current location.
     * Only the first grass is eaten.
     * If acacia is adjacent, it is 'trampled'
     * @return Where food was found, or null if it wasn't.
     */
    private Location findFood() {
        SimulationContext context = getContext();
        List<Location> adjacent = context.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object searchPlant = context.getObjectAt(where);
            if(searchPlant instanceof Grass) {
                Grass grass = (Grass) searchPlant;
                if (grass.isAlive()) {
                    grass.setDead();
                    context.emit(new FoodConsumptionEvent(this, this, grass,
                            TUNING.foodValueFor(Configuration.SpeciesId.GRASS)));
                    setFoodLevel(TUNING.foodValueFor(Configuration.SpeciesId.GRASS));
                    return where;
                }
            }
            else if (searchPlant instanceof Plant) {
                Plant plant = (Plant) searchPlant;
                if(plant.isAlive()) {
                    plant.setDead();
                    context.emit(new FoodConsumptionEvent(this, this, plant, 0));
                    return where;
                }
            }
        }
        return null;
    }

    /**
     * Create a new emu offspring.
     */
    protected Animal createOffspring(SimulationContext context, Location location) {
        return new Emu(false, context, location);
    }
}
