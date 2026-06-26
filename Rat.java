import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * A simple model of a rat.
 * Rats age, move, eat ants, breed, and die.
 *
 * @version 01.03.22
 */
public class Rat extends Animal
{
    private static final SpeciesTuning.AnimalTuning TUNING = SpeciesTuning.rat();
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();

    /**
     * Create a rat. A rat can be created as a newborn (age zero
     * and not hungry) or with a random age and food level.
     *
     * @param randomAge If true, the rat will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Rat(boolean randomAge, Field field, Location location) {
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
     * This is what the rat does most of the time: it hunts for
     * ants. In the process, it might breed, die of hunger,
     * or die of old age.
     * @param newRats A list to return newly born rats.
     * @param time the current time in the simulation
     */
    public void act(List<Animal> newRats, int time) {
        incrementAge(TUNING.getMaxAge());
        incrementHunger();

        if(isAlive() && TUNING.isActive(time))
        {
            if (getDisease()){
                spreadDisease();
            }
            if (giveBirth(TUNING.getBreedingAge())) {
                breedOffspring(newRats, TUNING.getBreedingAge(),
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
     * Look for ants adjacent to the current location.
     * Only the first live ant is eaten.
     * if there is a plant adjacent, it can be 'trampled'
     * @return where food was found, or null if it wasn't.
     */
    private Location findFood() {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object animal = field.getObjectAt(where);
            if(animal instanceof Ant) {
                Ant ant = (Ant) animal;
                if(ant.isAlive()) {
                    ant.setDead();
                    setFoodLevel(TUNING.foodValueFor(Ant.class));
                    return where;
                }
            }
            else if (animal instanceof Plant) {
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
     * Create a new rat offspring.
     */
    protected Animal createOffspring(Field field, Location location) {
        return new Rat(false, field, location);
    }
}
