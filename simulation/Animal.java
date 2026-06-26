package simulation;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * A class representing shared characteristics of animals.
 *
 * @version 01.03.22
 */
public abstract class Animal extends LivingEntity
{
    // Distinguishes between male and female to determine breeding
    private boolean isMale;
    // whether or not the weather is fog
    private boolean fog;
    // The animal's food level
    private int foodLevel;
    //whether the animal has a disease or not
    private boolean disease;
    // The animal's age.
    private int age;

    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    
    /**
     * Create a new animal at location in field.
     * 
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Animal(SimulationContext context, Location location) {
        super(context, location);
        fog = false;
        disease = false;
    }
    
    /**
     * Make this animal act - that is: make it do
     * whatever it wants/needs to do.
     * @param time The current time in the simulation.
     */
    abstract public void act(int time);

    /**
     * Return the animal's gender.
     * @return True if the animal is male, false if it is female.
     */
    protected boolean getGender(){return isMale;}

    /**
     * Randomly assigns an animal a gender
     */
    protected void setGender() {
        Random rand = Randomizer.getRandom();
        if (rand.nextInt(2) == 1) {
            isMale = true;
        }
        else {
            isMale = false;
        }
    }

    /**
     * assigns true to fog field
     */
    protected void setFog() { fog = true; }

    /**
     * assigns false to fog field
     */
    protected void resetFog(){ fog = false; }

    /**
     * returns the fog field
     * @return true if the weather is fog, false if it is not
     */
    protected boolean getFog() {return fog;}

    /**
     * takes a food level parameter and assigns it to the foodLevel field
     * @param foodLevel the value to be assigned to foodLevel
     */
    protected void setFoodLevel(int foodLevel){ this.foodLevel = foodLevel; }

    /**
     * decrements foodLevel by 1
     */
    protected void decrementHealth(){ foodLevel--; }

    /**
     * gives an animal a disease
     * 1 in 100 chance of assigning true to disease
     */
    protected void giveDisease() {
        Random rand = Randomizer.getRandom();
        if (rand.nextInt(101) == 1) {
            disease = true;
        }
    }

    /**
     * returns whether the animal has a disease or not
     * @return true if the animal has a disease, false if they don't
     */
    protected boolean getDisease() { return disease; }

    /**
     * assigns false to disease
     */
    protected void resetDisease() { disease = false; }

    /**
     * if an animal has a disease, this method spreads it to animals
     * in adjacent locations
     */
    protected void spreadDisease() {
        SimulationContext context = getContext();
        List<Location> adjacent = context.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object animal = context.getObjectAt(where);
            if(animal instanceof Animal) {
                Animal diseaseAnimal = (Animal) animal;
                diseaseAnimal.giveDisease();
                diseaseAnimal.decrementHealth();
            }
        }
    }

    /**
     * Breed new offspring into free adjacent locations.
     * @param BREEDING_AGE The minimum age required to breed.
     * @param BREEDING_PROBABILITY The likelihood of breeding.
     * @param MAX_LITTER_SIZE The maximum number of births.
     */
    protected void breedOffspring(int BREEDING_AGE,
            double BREEDING_PROBABILITY, int MAX_LITTER_SIZE) {
        if(giveBirth(BREEDING_AGE)) {
            SimulationContext context = getContext();
            List<Location> free = context.getFreeAdjacentLocations(getLocation());
            int births = breed(BREEDING_AGE, BREEDING_PROBABILITY, MAX_LITTER_SIZE);
            for (int b = 0; b < births && free.size() > 0; b++) {
                Location loc = free.remove(0);
                Animal offspring = createOffspring(context, loc);
                context.emit(new BirthEvent(this, offspring, loc));
            }
        }
    }

    /**
     * Create a new offspring of this animal's species.
     * @param context The simulation context for the offspring.
     * @param location The offspring's location.
     * @return A new animal instance.
     */
    protected abstract Animal createOffspring(SimulationContext context, Location location);

    /**
     * Move to a new location if one exists, otherwise die.
     * @param newLocation The destination location, or null.
     */
    protected void moveOrDie(Location newLocation) {
        if(newLocation != null) {
            setLocation(newLocation);
        }
        else {
            setDead();
        }
    }

    /**
     * takes an age parameter and assigns it to the age field
     * @param age the value to be assigned to the age field
     */
    protected void setAge(int age) { this.age = age; }

    /**
     * Generate a number representing the number of births,
     * if it can breed.
     * @return The number of births (may be zero).
     */
    protected int breed(int BREEDING_AGE, double BREEDING_PROBABILITY, int MAX_LITTER_SIZE) {
        int births = 0;
        if(canBreed(BREEDING_AGE) && rand.nextDouble() <= BREEDING_PROBABILITY) {
            births = rand.nextInt(MAX_LITTER_SIZE) + 1;
        }
        return births;
    }

    /**
     * Check whether or not this animal is to give birth at this step.
     * animals are checked to be: of the same species(class), different genders and
     *  of breeding age
     * @param BREEDING_AGE the minimum age this animal must be to breed
     * @retyrn true if the animal can breed, false if it can not
     */
    protected boolean giveBirth(int BREEDING_AGE) {
        SimulationContext context = getContext();
        List<Location> adjacent = context.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object animal = context.getObjectAt(where);
            if (animal != null) {
                if (animal.getClass() == this.getClass()) {
                    Animal adjAnimal = (Animal) animal;
                    if (this.getGender() != adjAnimal.getGender()) {
                        if (age >= BREEDING_AGE) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }


    /**
     * checks to see if the animal has reached breeding age
     * @param BREEDING_AGE the minimum an animal must be in order to breed
     * @return true if the animal is of breeding age
     */
    protected boolean canBreed(int BREEDING_AGE)
    {
        return age >= BREEDING_AGE;
    }

    /**
     * make the animal more hungry by decreasing its health
     * if the foodLevel is less than or equal to 0, set the animal to dead
     */
    protected void incrementHunger() {
        decrementHealth();
        if(foodLevel <= 0) {
            setDead();
        }
    }

    /**
     * Increase the age. This could result in the animal's death.
     */
    protected void incrementAge(int MAX_AGE) {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }

}
