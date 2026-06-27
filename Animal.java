import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import java.util.Iterator;



/**
    * Interface method required for lambda expression which initalises an animal of any type 
*/
interface CreateAnimal {

    /**
    * Function required for initalisating any animal
    * @param loc required location for initalisation of animal
    * @return animal, the initalised animal 
    */
    Animal initialise(Location loc);
}

/**
 * A class representing shared characteristics of animals.
 *
 * @version 2s
 */
public abstract class Animal extends Locatable implements Simulatable, Breedable, Displayable, Infectable
{
    // Whether the animal is alive or not.
    private boolean alive;

    //The gender of the animal
    protected Gender gender;


    //Storage for a disease of the animal. Is set to null as animals are not (in this sim) born with diseases
    protected Disease currentDisease = null; 


    //Random for the calculation of probabilities. 
    protected static final Random rand = Randomizer.getRandom();

    // The animals's age.
    protected int age;
    // The animals's food level, which is increased by eating rabbits.
    protected int foodLevel;
    
    /**
     * Create a new animal at location in field.
     * 
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Animal(Field field, Location location)
    {
        //Initalise the location 
        super(field, location);
        alive = true;
        gender = rand.nextInt(2) == 0 ? Gender.MALE : Gender.FEMALE; //Create a gender, with a 50% chance of either. 
    }
    

    /**
     * Check whether the animal is alive or not.
     * @return true if the animal is still alive.
     */
    public boolean isAlive()
    {
        return alive;
    }

    /**
     * Get the gender of the animal 
     * @return gender 
     */
    public Gender getGender () {
        return gender;
    }


    /**
     * This amounts to an exposurure of a particular disease. If the animal meets the required probability, it will give the animal the disease
     * @param disease the disease that the animal has been exposed
     */
    public void giveDisease(Disease disease) {
        //Check if the disease exists, if the probability is high enough and the animal is not already diseased
        if (disease != null && rand.nextDouble () <= disease.getR0() && currentDisease == null){
            currentDisease = disease;
        }
    }

    /**
     * Force a disease on an animal without probability requirements. 
     * @param disease the disease to give the animal
     */
    public void forceDisease(Disease disease) {
        currentDisease = disease;
    }


    /**
     * Get the disease of the animal 
     * @return a disease if the animal has one, if not just null. 
     */
    public Disease getDisease() {
        return currentDisease;
    }



    /**
     * Indicate that the animal is no longer alive.
     * It is removed from the field.
     */
    public void setDead()
    {
        alive = false;
        removeFromField();//Call the super (Locatable) removeFromField which removes the animal from the field 
    }


    /**
     * fetch all the potential mates (of the same animal type) for an animal. 
     * @param type the type of animal to get the mates for 
     * @return a list of all potential mates 
     */
    protected List<Animal> getPotentialMates(Class type) {
        Field field = getField();
        ArrayList<Animal> potentialMates = new ArrayList<Animal>();
        List<Object> nearAnimals = field.getItemOfTypeNear(type, getLocation());
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        for(Object i : nearAnimals) {
            var potentialMate = (Animal)type.cast(i);

            if (potentialMate.canBreed(getGender().invert()) && canBreed(getGender())) {
                potentialMates.add(potentialMate);

            }
        }
        return potentialMates;
    }

    
    /**
     * Generate a number representing the number of births,
     * if it can breed.
     * @return The number of births (may be zero).
     */
    protected int breed(int currentNumberOfAnimal)
    {
        int births = 0;

        // double counterCyclical = -(1 / (1 + Math.pow(Math.E, -((currentNumberOfAnimal / 20 - 40))))) + getBreedingProbability();//See report for formula

        double counterCyclical = (2 * getBreedingProbability()) / (1 + Math.pow(Math.E, (currentNumberOfAnimal / 50) - 300));

        // double breedingProbability = (getBreedingProbability() + counteCyclical) / 2; //Counter cyclical calculations

        if(rand.nextDouble() <= counterCyclical) {
            births = rand.nextInt(getMaxLitterSize()) + 1;
        }
        return births;
    }

    /**
     * Make this an animal more hungry. This could result in the animal's death.
     */
    protected void incrementHunger()
    {
        foodLevel--;
        if(foodLevel <= 0) {
            setDead();
        }
    }


    /**
     * Make this animal older. This could result in the animal's death
     * @param maxAge the age at which the animal should die.
     */
    protected void incrementAge(int maxAge)
    {
        age++;
        if(age > maxAge) {
            setDead();

        }
    }


    /**
     * Spawn offspring if conditions are met: a compatible mate is nearby and
     * the breeding probability check passes.  New entities are registered via
     * {@link StepContext#spawn(Simulatable)} rather than appended to a list.
     * @param numberOfAnimals population counter used by the breeding formula
     * @param animalType      the species class to search for mates
     * @param context         the current step context (receives spawned young)
     * @param animalCreator   factory for creating one offspring at a location
     */
    protected void giveBirth(Counter numberOfAnimals, Class animalType,
                             StepContext context, CreateAnimal animalCreator)
    {
        Field field = getField();
        List<Animal> potentialMates = getPotentialMates(animalType);
        List<Location> free = new ArrayList<>(field.getFreeAdjacentLocations(getLocation()));
        for (Animal i : potentialMates) {
            int births = breed(numberOfAnimals.getCount());
            for (int b = 0; b < births && free.size() > 0; b++) {
                Location loc = free.remove(0);
                context.spawn(animalCreator.initialise(loc));
            }
        }
    }


    /**
     * Look for food sources adjacent to the current location.
     * Only the first food source is eaten.
     * @return Where food was found, or null if it wasn't.
     */
    protected Location findFood(List<Class> eats)
    {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object animal = field.getObjectAt(where);
            if(animal != null && eats.contains(animal.getClass())) {
                var prey = (Simulatable & Eatable) animal;
                if(prey.isAlive()) { 
                    prey.setDead();
                    foodLevel = prey.getFoodValue();
                    return where;
                }
            }
        }
        return null;
    }


    /**
     * Move to a new location. If there is a food source nearby, move to 
     * that location, if not, move to a random location. 
     * @param eats a list of what this animal eats
     * @param maxFoodHunger an int value of the max amount an animal can eat
     */
    protected void moveToNewLocation(List<Class> eats, int maxFoodHunger) {
        // Move towards a source of food if found and needed.
        Location newLocation = foodLevel < maxFoodHunger ? findFood(eats) : null;
       
        
        if(newLocation == null ) { 
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


    /**
     * Expose all nearby animals to the disease this current animal has.
    */
    protected void giveNearbyAnimalsDisease(){
        List<Object> nearAnimals = getField().getItemOfTypeNear(Animal.class, getLocation());
        for(Object i : nearAnimals) {
            if (i instanceof Animal) {
                Animal otherAnimal = (Animal)i;
                otherAnimal.giveDisease(getDisease());
            }
        }
    }

    // --- Template-method hooks ---

    /** @return the maximum age for this species */
    protected abstract int getMaxAge();

    /** @return the food level at which this animal will seek food */
    protected abstract int getMaxHunger();

    /** @return the list of prey/food classes this animal eats */
    protected abstract List<Class> getEats();

    /**
     * Create a new (non-random-age) offspring of this species at the given location.
     * @param loc the location for the newborn
     */
    protected abstract Simulatable createOffspring(Location loc);

    /**
     * Shared act logic for all animals: age, hunger, disease, breeding, and movement.
     * Subclasses supply species-specific behaviour via the abstract hooks above.
     */
    public void act(StepContext context) {
        incrementAge(getMaxAge());
        if (rand.nextDouble() <= context.getActivityReduction()) {
            incrementHunger();
            if (isAlive()) {
                giveNearbyAnimalsDisease();
                Counter count = context.getPopulationCount(getClass());
                giveBirth(count, getClass(), context, loc -> createOffspring(loc));
                applyDiseaseEffects();
                moveToNewLocation(getEats(), getMaxHunger());
            }
        }
    }

    private void applyDiseaseEffects() {
        Disease disease = getDisease();
        if (disease != null) {
            foodLevel -= disease.getHungerEffect();
            age -= disease.getAgeEffect();
        }
    }
}








