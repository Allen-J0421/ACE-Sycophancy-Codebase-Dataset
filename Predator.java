import java.util.List;
import java.util.ArrayList;

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
        ArrayList<Animal> neighboringAnimals = getNeighboringAnimalsList();
        checkForAttack(neighboringAnimals);

        if (isAlive()) {
            if (canReproduce(neighboringAnimals)){
                reproduce(newSpecies);
            }

            // Move towards a source of food if found.
            Location newLocation = null;
            if (isNotFull()) {
                newLocation = findFoodAndEat(neighboringAnimals);
            } 

            if(newLocation == null) {
                // No food found - try to move to a free location.
                newLocation = getField().freeAdjacentLocation(getLocation());
            }

            // See if it was possible to move.
            if(newLocation != null) {
                setLocation(newLocation);
            }
            else {
                // Overcrowding.
                setDead();
            }
        }
    }

    /**
     * Tries to find a prey in one of the neighboring cell. If a prey is found, it is eaten and its location is returned.
     *
     * @param  neighboringAnimals (ArrayList<Animal>) A list of neighboring animals.
     * @return (Location) the location of the eaten prey, null if no prey was found.
     */
    private Location findFoodAndEat(ArrayList<Animal> neighboringAnimals)
    {
        for (Animal animal : neighboringAnimals) {
            if(!(animal instanceof Predator)){
                animal.setDead();
                foodLevel += animal.getNutritionalValue();
                return animal.getLocation();
            }
        }
        // No food found
        return null;
    }

    /**
     * Check if predator if under attack from a horde of another species of predator. If a horde is attacking it and the horde's strength
     * is greater than the predator's one, the horde members eat it.
     *
     * @param  neighboringAnimals (ArrayList<Animal>) A list of neighboring animals.
     */
    private void checkForAttack(ArrayList<Animal> neighboringAnimals)
    {
        ArrayList<Predator> hordeMembers = new ArrayList<>();

        for (int i =0; i < neighboringAnimals.size(); i++)
        {
            if (neighboringAnimals.get(i) instanceof Predator) {

                Predator neighboringPredator = (Predator)neighboringAnimals.get(i);
                String nameOfInvestigatedHorde = neighboringPredator.getName();
                
                if(! this.getName().equals(nameOfInvestigatedHorde))
                {
                    int totalHordeStrength =  neighboringPredator.getStrength();

                    hordeMembers.add(neighboringPredator);
                    for (int j =0; j < neighboringAnimals.size(); j++) {
                        if (nameOfInvestigatedHorde.equals(neighboringAnimals.get(j).getName())) {
                            Predator predatorObject = (Predator)neighboringAnimals.get(i);
                            totalHordeStrength += predatorObject.getStrength();
                            hordeMembers.add(predatorObject);
                        }
                    }
                    if (totalHordeStrength > strength) {
                        attackedByHorde(hordeMembers);
                        break;
                    } else {
                        hordeMembers.clear();
                    }
                }
            }
        }
    }

    /**
     * Animal is under attack by a horde whose strength is greater than its own. It is eaten by the horde and dies. Its nutritional value
     * is therefore shared
     *
     * @param hordeMembers (ArrayList<Predator>) List of the predators constituting the horde.
     */
    private void attackedByHorde(ArrayList<Predator> hordeMembers)
    {
        // Sharing predator's nutritional value amongst the various horde members.
        int foodLevelAddedToEachHordeMember = this.getNutritionalValue() / hordeMembers.size();
        for (Predator predator : hordeMembers) {
            predator.incrementFoodLevel(foodLevelAddedToEachHordeMember);
        }
        this.setDead();
    }

    /**
     * Creates the appropriate number of predators of the same species due to reproduction. These new predators of course share the same
     * features as their "parent" except the sex which is randomized, their age and foodLevel are not randomized.
     *
     * @param newOfThisKind (List<Species>) The list of species to which newborns must be added.
     */
    protected void reproduce(List<Species> newOfThisKind)
    {
        Field field = getField();
        if (field != null)
        {
            List<Location> free = field.getFreeAdjacentLocations(getLocation());
            int births = numberOfBirths();
            for(int b = 0; b < births && free.size() > 0; b++) {
                Location loc = free.remove(0);
                Predator young = new Predator(strength, field, loc, getName(), getMaximumTemperature(), getMinimumTemperature(), getNutritionalValue(), getReproductionProbability(), getMaxAge(), getBreedingAge(), getMaxLitterSize(),false, getHibernates(), getIsNocturnal());
                newOfThisKind.add(young);
            }
        }
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