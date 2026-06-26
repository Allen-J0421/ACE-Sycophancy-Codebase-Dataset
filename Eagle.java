import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * A simple model of an eagle.
 * Eagles age, move, eat snakes and rats, and die.
 *
 * @version 01.03.22
 */

public class Eagle extends Animal
{
    private static final SpeciesTuning.AnimalTuning TUNING = SpeciesTuning.eagle();
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();

    /**
     * Create an eagle. An eagle can be created as a newborn (age zero
     * and not hungry) or with a random age and food level.
     *
     * @param randomAge If true, the eagle will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Eagle(boolean randomAge, Field field, Location location) {
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
     * This is what the eagle does most of the time: it hunts for
     * rats and snakes. In the process, it might breed, die of hunger,
     * or die of old age.
     * @param newEagles A list to return newly born eagles.
     * @param time the current time in the simulation
     */
    public void act(List<Animal> newEagles, int time) {
        incrementAge(TUNING.getMaxAge());
        incrementHunger();
        if(isAlive() && TUNING.isActive(time)) {
            if (getDisease()){
                spreadDisease();
            }
            if (giveBirth(TUNING.getBreedingAge())) {
                breedOffspring(newEagles, TUNING.getBreedingAge(),
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
     * Look for rats and snakes adjacent to the current location.
     * Only the first live rat or snake is eaten.
     * If it is a plant, then it is 'trampled'
     * @return Where food was found, or null if it wasn't.
     */
    private Location findFood() {
        if (getFog() && (rand.nextInt(2) == 0)) {
            Field field = getField();
            List<Location> adjacent = field.adjacentLocations(getLocation());
            Iterator<Location> it = adjacent.iterator();
            while(it.hasNext()) {
                Location where = it.next();
                Object animal = field.getObjectAt(where);
                if(animal instanceof Snake) {
                    Snake snake = (Snake) animal;
                    if(snake.isAlive()) {
                        snake.setDead();
                        setFoodLevel(TUNING.foodValueFor(Snake.class));
                        return where;
                    }
                }
                else if(animal instanceof Rat) {
                    Rat rat = (Rat) animal;
                    if(rat.isAlive()) {
                        rat.setDead();
                        setFoodLevel(TUNING.foodValueFor(Rat.class));
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
                else if(animal instanceof Rat) {
                    Rat rat = (Rat) animal;
                    if(rat.isAlive()) {
                        rat.setDead();
                        setFoodLevel(TUNING.foodValueFor(Rat.class));
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
     * Create a new eagle offspring.
     */
    protected Animal createOffspring(Field field, Location location) {
        return new Eagle(false, field, location);
    }
}
