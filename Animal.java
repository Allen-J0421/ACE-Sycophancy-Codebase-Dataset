import java.util.List;
import java.lang.Math;
import java.util.Random;

/**
 * A class representing shared characteristics of animals.
 *
 * @version 2022/03/02
 */
public class Animal extends Creature
{   
    // A shared random number generator to control aging, hunger and breeding.
    private static final Random rand = Randomizer.getRandom();

    // sex of an animal, 0 = female and 1 = male
    private int sex;
    // The animal's age.
    private int age;
    // The animal's food level.
    private int foodLevel;
    // Species-specific lifecycle and feeding behavior.
    private final AnimalSpecies species;

    // The amount of oxygen an animal need to survive
    protected static final double ANIMAL_OXYGEN_REQUIRED = 0.0000009;

    // If the animal is infected by disease.
    private boolean isInfected;
    // If the animal is immuned from the diease.
    private boolean isImmuned;

    // Track the first step at which the animal is infected;
    private int infectionStartStep;

    // total population that is die of disease.
    public static int populationDieOfDisease = 0;

    public Animal(boolean randomAge, Field field, Location location, AnimalSpecies species){
        super(field, location);
        sex = (int)(Math.round(Math.random()));
        this.species = species;
        if(randomAge) {
            age = rand.nextInt(species.getMaxAge());
            foodLevel = rand.nextInt(species.getInitialFoodValue());
        }
        else {
            age = 0;
            foodLevel = species.getInitialFoodValue();
        }
        isInfected = false;
        isImmuned = false;
        // Track the first step at which the animal is infected;
        infectionStartStep = 0;

    }

    /**
     * This is what animals do most of the time: age, get hungry, breed,
     * search for food, move and consume oxygen.
     *
     * @param newAnimals A list to return newly born animals.
     * @param atDayTime true if current step is daytime false otherwise.
     * @param oxygenLevel The inital level of dissolved oxygen in the water.
     * @param disease The disease may happened during simulation.
     * @param step current step.
     *
     * @return the oxygen level the animal consumed after action.
     */
    public double act(List<Creature> newAnimals, boolean atDayTime, double oxygenLevel, Disease disease, int step)
    {
        if(oxygenLevel < ANIMAL_OXYGEN_REQUIRED){
            setDead();
            return 0;
        }

        // If the animal dies of disease, it will consume no oxygen.
        if(disease.progressInfection(this, step))
            return 0;

        incrementAge();
        incrementHunger();

        if(isAlive() && !needSleep(atDayTime)) {
            giveBirth(newAnimals);

            Location newLocation = search(disease, step);
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

        return -ANIMAL_OXYGEN_REQUIRED;
    }

    /**
     * Increase the age. This could result in the animal's death.
     */
    private void incrementAge()
    {
        age++;
        if(age > species.getMaxAge()) {
            setDead();
        }
    }

    /**
     * Make this animal more hungry. This could result in the animal's death.
     */
    private void incrementHunger()
    {
        foodLevel--;
        if(foodLevel <= 0) {
            setDead();
        }
    }

    /**
     * Check whether or not this animal is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newAnimals A list to return newly born animals.
     */
    private void giveBirth(List<Creature> newAnimals)
    {
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed();
        for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            Animal young = species.createYoung(field, loc);
            newAnimals.add(young);
        }
    }

    /**
     * Generate a number representing the number of births,
     * if it can breed.
     * @return The number of births (may be zero).
     */
    private int breed()
    {
        int births = 0;
        if(canBreed() && rand.nextDouble() <= species.getBreedingProbability()) {
            births = rand.nextInt(species.getMaxLitterSize()) + 1;
        }
        return births;
    }

    /**
     * Check if this animal can breed.
     * @return true if this animal can breed, false otherwise.
     */
    private boolean canBreed()
    {
        return age >= species.getBreedingAge() &&
               (!species.requiresDifferentSexToBreed() || encounterWithDiffSex());
    }

    /**
     * Look for food adjacent to the current location.
     * Only the first live food source is eaten. If a nearby animal is
     * infected, then this animal also may be infected.
     * @param disease disease.
     * @param step int current step.
     * @return Where food was found, or null if it wasn't.
     */
    public Location search(Disease disease, int step)
    {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation(), 1);
        for(Location loc: adjacent) {
            Creature creature = field.getCreatureAt(loc);
            if(creature == null) {
                continue;
            }
            if(creature.canTransmitDisease()) {
                disease.expose(this, step);
            }

            int foodValue = creature.getFoodValueFor(this);
            if(foodValue > 0 && creature.isAlive()) {
                creature.setDead();
                foodLevel = foodValue;
                return loc;
            }
        }
        return null;
    }

    /**
     * get the gender of an animal.
     * @return sex  0 = female and 1 = male.
     */
    public int getSex(){
        return sex;
    }

    /**
     * Check whether this animal has encountered a compatible different-sex mate.
     * @return true if a compatible different-sex mate is nearby, false otherwise.
     */
    public boolean encounterWithDiffSex()
    {
        return encounterWithDiffSex(species.getMateSearchDistance());
    }

    /**
     * Look for a nearby animal of the given class with a different sex.
     * @param animalClass The class of animal to find.
     * @param adjacentDistance Controls the search range.
     * @return true if a matching nearby animal has a different sex.
     */
    protected boolean encounterWithDiffSex(int adjacentDistance)
    {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation(), adjacentDistance);
        for(Location where: adjacent) {
            Creature creature = field.getCreatureAt(where);
            if(creature != null && creature.isPotentialMateFor(this)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Return the food value of a cod for this animal.
     */
    protected int getFoodValueFrom(Cod cod)
    {
        return AnimalInteractionRegistry.getFoodValue(species, cod.getSpecies());
    }

    /**
     * Return the food value of a salmon for this animal.
     */
    protected int getFoodValueFrom(Salmon salmon)
    {
        return AnimalInteractionRegistry.getFoodValue(species, salmon.getSpecies());
    }

    /**
     * Return the food value of seaweed for this animal.
     */
    protected int getFoodValueFrom(Seaweed seaweed)
    {
        return AnimalInteractionRegistry.getFoodValueFromSeaweed(species);
    }

    /**
     * @return true if this animal could mate with the given cod.
     */
    protected boolean canMateWith(Cod cod)
    {
        return AnimalInteractionRegistry.canMateWith(species, cod.getSpecies());
    }

    /**
     * @return true if this animal could mate with the given salmon.
     */
    protected boolean canMateWith(Salmon salmon)
    {
        return AnimalInteractionRegistry.canMateWith(species, salmon.getSpecies());
    }

    protected AnimalSpecies getSpecies()
    {
        return species;
    }

    /**
     * identify whether a creature need to sleep
     * 
     * @param atDayTime true if it is at day time false if it is at night time.
     * @return true if currently it is night.
     */
    public boolean needSleep(boolean atDayTime){
        return !atDayTime;
    }

    
    /**
     * get whether an animal is infected.
     * 
     * @return true if an animal is infected, false otherwise. 
     */
    public boolean getIsInfected(){
        return isInfected;
    }

    protected boolean canTransmitDisease()
    {
        return isInfected;
    }

    protected boolean isPotentialMateFor(Animal animal)
    {
        return false;
    }

    protected boolean hasDifferentSex(Animal animal)
    {
        return getSex() != animal.getSex();
    }

    /**
     * get if an animal is immuned.
     * 
     * @return true if an animal is immuned, false otherwise.
     */
    public boolean getIsImmuned(){
        return isImmuned;
    }

    boolean hasActiveInfection()
    {
        return isInfected && !isImmuned;
    }

    boolean isImmuneToDisease()
    {
        return isImmuned;
    }

    int getInfectionStartStep()
    {
        return infectionStartStep;
    }

    boolean infect(int step)
    {
        if(!isImmuned && !isInfected) {
            isInfected = true;
            infectionStartStep = step;
            return true;
        }
        return false;
    }

    void grantDiseaseImmunity()
    {
        isImmuned = true;
        isInfected = false;
    }

    void dieOfDisease()
    {
        setDead();
        populationDieOfDisease++;
    }

    
}
