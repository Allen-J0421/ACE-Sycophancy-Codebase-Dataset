import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * A class representing the characteristics of a predator.
 * They are different from other animals because they eat any other
 * animal or predator whose strength is weaker.
 *
 * A horde of weaker predators can kill and eat a stronger predator.
 *
 *
 * @version 2022.03.01
 */
public class Predator extends Animal
{
    // The predator's strength, if it is strong enough it can attack other predators
    private final int strength;

    /**
     * Create a new predator with given specifications.
     *
     * @param strength (int) the predator's strength
     * @param field (Field) the field where the simulation takes place
     * @param location (Location) the Location at which the predator should appear
     * @param name (String) the predator's name (its species' name)
     * @param maximumTemperature (int) the maximum temperature the predator can survive to
     * @param minimumTemperature (int) the minimum temperature an predator can survive to
     * @param nutritionalValue (int) the predator's nutritional value
     * @param reproductionProbability (double) the probability that the predator reproduces at each step after a given minimum breeding age
     * @param maxAge (int) the predator's life expectancy
     * @param breedingAge (int) the age at which predator can start to reproduce
     * @param maxLitterSize (int) the maximum number of children the predator can have in one reproduction
     * @param randomAge (boolean) whether or not predator should be created with a random age
     * @param hibernates (boolean) whether or not predator is able to hibernate
     * @param isNocturnal (boolean) whether or not predator is more active at night
     */
    public Predator (int strength, Field field, Location location, String name, int maximumTemperature, int minimumTemperature, int nutritionalValue, double reproductionProbability, int maxAge, int breedingAge, int maxLitterSize,  boolean randomAge, boolean hibernates, boolean isNocturnal)
    {
        // call to the constructor of the Animal class
        super(field, location, name, maximumTemperature, minimumTemperature, nutritionalValue, reproductionProbability, maxAge, breedingAge, maxLitterSize, randomAge, hibernates, isNocturnal);

        this.strength = strength;
    }

    /**
     * A predator's movement. It first checks if it attacked by a horde of another species of predators. If it is the case,
     * it dies and execution stops. If not, it first tries to reproduce, then to find a prey to eat in the neighboring cells,
     * and finally to move to either the cell the prey he ate was occupying or another free adjacent cell. If no adjacent cell
     * is available, it dies of overcrowding.
     *
     * @param newSpecies (List<Species>) A list to receive newly born animals.
     */
    protected void makeMove(List<Species> newSpecies)
    {
        List<Animal> neighboringAnimals = getNeighboringAnimalsList();
        checkForAttack(neighboringAnimals);

        if (!isAlive()) {
            return;
        }

        reproduceIfPossible(newSpecies, neighboringAnimals);
        moveToLocationOrDie(determineMoveLocation(neighboringAnimals));
    }

    /**
     * Tries to find a prey in one of the neighboring cell. If a prey is found, it is eaten and its location is returned.
     *
     * @param  neighboringAnimals (ArrayList<Animal>) A list of neighboring animals.
     * @return (Location) the location of the eaten prey, null if no prey was found.
     */
    private Location findFoodAndEat(List<Animal> neighboringAnimals)
    {
        for (Animal animal : neighboringAnimals) {
            if (!(animal instanceof Predator)) {
                Location preyLocation = animal.getLocation();
                animal.setDead();
                foodLevel += animal.getNutritionalValue();
                return preyLocation;
            }
        }
        // No food found
        return null;
    }

    /**
     * Determine where the predator should move after it has acted.
     *
     * @param neighboringAnimals the neighboring animals.
     * @return the location to move to, or null if no move is available.
     */
    private Location determineMoveLocation(List<Animal> neighboringAnimals)
    {
        if (isNotFull()) {
            Location preyLocation = findFoodAndEat(neighboringAnimals);
            if (preyLocation != null) {
                return preyLocation;
            }
        }
        return getFreeAdjacentLocation();
    }

    /**
     * Check if predator if under attack from a horde of another species of predator. If a horde is attacking it and the horde's strength
     * is greater than the predator's one, the horde members eat it.
     *
     * @param  neighboringAnimals (ArrayList<Animal>) A list of neighboring animals.
     */
    private void checkForAttack(List<Animal> neighboringAnimals)
    {
        HashMap<String, List<Predator>> hordesBySpecies = new HashMap<>();

        for (Animal animal : neighboringAnimals) {
            if (animal instanceof Predator) {
                Predator predator = (Predator) animal;
                if (!predator.getName().equals(getName())) {
                    List<Predator> hordeMembers = hordesBySpecies.get(predator.getName());
                    if (hordeMembers == null) {
                        hordeMembers = new ArrayList<>();
                        hordesBySpecies.put(predator.getName(), hordeMembers);
                    }
                    hordeMembers.add(predator);
                }
            }
        }

        for (List<Predator> hordeMembers : hordesBySpecies.values()) {
            if (calculateHordeStrength(hordeMembers) > strength) {
                attackedByHorde(hordeMembers);
                return;
            }
        }
    }

    /**
     * Calculate the combined strength of a horde.
     *
     * @param hordeMembers the predators in the horde.
     * @return the total strength.
     */
    private int calculateHordeStrength(List<Predator> hordeMembers)
    {
        int totalStrength = 0;
        for (Predator predator : hordeMembers) {
            totalStrength += predator.getStrength();
        }
        return totalStrength;
    }

    /**
     * Animal is under attack by a horde whose strength is greater than its own. It is eaten by the horde and dies. Its nutritional value
     * is therefore shared
     *
     * @param hordeMembers (ArrayList<Predator>) List of the predators constituting the horde.
     */
    private void attackedByHorde(List<Predator> hordeMembers)
    {
        // Sharing predator's nutritional value amongst the various horde members.
        int foodLevelAddedToEachHordeMember = this.getNutritionalValue() / hordeMembers.size();
        for (Predator predator : hordeMembers) {
            predator.incrementFoodLevel(foodLevelAddedToEachHordeMember);
        }
        this.setDead();
    }

    /**
     * Create a newborn predator of the current species.
     *
     * @param location the location at which the newborn should appear.
     * @return the newborn predator.
     */
    @Override
    protected Species createOffspring(Location location)
    {
        return new Predator(strength, getField(), location, getName(), getMaximumTemperature(), getMinimumTemperature(), getNutritionalValue(), getReproductionProbability(), getMaxAge(), getBreedingAge(), getMaxLitterSize(), false, getHibernates(), getIsNocturnal());
    }

    /**
     * Returns the predator's strength, public so that other predators can consult the strength of a pontential horde this predator could
     * be a part of.
     *
     * @return (int) the predator's strength.
     */
    public int getStrength() {
        return strength;
    }
}
