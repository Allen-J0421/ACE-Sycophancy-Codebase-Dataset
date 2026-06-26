import java.util.List;
import java.util.Iterator;
import java.util.Random;

/**
 * A simple model of a dingo.
 * Dingoes age, move, eat snakes, and die.
 *
 * @version 01.03.22
 */
public class Dingo extends Animal
{
    private static final SpeciesTuning.AnimalTuning TUNING = SpeciesTuning.dingo();
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();

    /**
     * Create a dingo. A dingo can be created as a newborn (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the dingo will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Dingo(boolean randomAge, Field field, Location location) {
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
     * This is what the dingo does most of the time: it hunts for
     * snakes. In the process, it might breed, die of hunger,
     * or die of old age.
     * @param newDingoes A list to return newly born dingoes.
     * @param time the current time in the simulation
     */
    public void act(List<Animal> newDingoes, int time) {
        incrementAge(TUNING.getMaxAge());
        incrementHunger();
        if(isAlive() && TUNING.isActive(time))
        {
            if (getDisease()){
                spreadDisease();
            }
            if (giveBirth(TUNING.getBreedingAge())) {
                breedOffspring(newDingoes, TUNING.getBreedingAge(),
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
     * Look for snakes adjacent to the current location.
     * Only the first live snake is eaten.
     * If it is a plant, then it is 'trampled'
     * @return where food was found, or null if it wasn't.
     */
    private Location findFood() {
        if (getFog()){
            if (rand.nextInt(2) == 0) {
                Field field = getField();
                List<Location> adjacent = field.adjacentLocations(getLocation());
                Iterator<Location> it = adjacent.iterator();
                while (it.hasNext()) {
                    Location where = it.next();
                    Object animal = field.getObjectAt(where);
                    if (animal instanceof Snake) {
                        Snake snake = (Snake) animal;
                        if (snake.isAlive()) {
                            snake.setDead();
                            setFoodLevel(TUNING.foodValueFor(Snake.class));
                            return where;
                        }
                    } else if (animal instanceof Plant) {
                        Plant plant = (Plant) animal;
                        if (plant.isAlive()) {
                            plant.setDead();
                            return where;
                        }
                    }
                }
            }
            return null;
        }
        else {
            Field field = getField();
            List<Location> adjacent = field.adjacentLocations(getLocation());
            Iterator<Location> it = adjacent.iterator();
            while (it.hasNext()) {
                Location where = it.next();
                Object animal = field.getObjectAt(where);
                if (animal instanceof Snake) {
                    Snake snake = (Snake) animal;
                    if (snake.isAlive()) {
                        snake.setDead();
                        setFoodLevel(TUNING.foodValueFor(Snake.class));
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
    }

    /**
     * Create a new dingo offspring.
     */
    protected Animal createOffspring(Field field, Location location) {
        return new Dingo(false, field, location);
    }
}
