import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

/**
 * A class representing shared characteristics of animals.
 *
 * @version 15/03/2022
 */
public abstract class Animal extends Organism
{
    // The animal's gender - true/male & false/female.
    protected boolean gender;
    // The animal's food level, which is increased by eating food.
    protected int foodLevel;
    // An indiactor used to flag if an animal has a disease.
    protected boolean diseaseActive;
    // The time in minutes when a diseased animal dies - initilaised to -1 to represent specific no date of death.
    protected int timeOfDeath = -1;
    
    /**
     * Create a new animal at location in field.
     * 
     * @param field The field currently occupied.
     * @param location The location within the field.
     * @param randomAge If true, the animal will have a random age and hunger level.
     */
    protected Animal(Field field, Location location, boolean randomAge)
    {
        super(field, location);
        
        // Give the animal a gender.
        gender = (Math.random() < 0.5); // There is a 50% chance of the animal being male or female.
        if(randomAge) {
            age = rand.nextInt(getMaxAge());
            foodLevel = rand.nextInt(getFoodLevel());
        }
        else {
            age = 0;
            foodLevel = getFoodLevel();
        }
    }
    
    /**
     * This is what the animal does most of the time: it eats other
     * organisms. In the process, it might breed, behave differently or 
     * die of hunger or old age.
     * @param newOrganism A list to return newly born animals.
     */
    public void act(List<Organism> newOrganism)
    {
        incrementAge();
        incrementHunger();
        if(isAlive()) {
            // Get adjacent locations to the animal's current location.
            List<Location> adjacentLocations = getField().adjacentLocations(getLocation());
            // Create an ArrayList of adjacent animals.
            List<Object> adjacentAnimals = new ArrayList<>();
            for (int x = 0; x < adjacentLocations.size(); x++) {
                // Check an adjacent location has an organism in it.
                Object object =  getField().getObjectAt(adjacentLocations.get(x));
                if (object != null) {
                    // Check that the adjacent organism is an animal.                
                    if (object instanceof Animal) {
                        // Append the animal to a list of adjacent animals.
                        adjacentAnimals.add(object);
                    }
                }
            }
            
            // Check if the animal is carrying a disease.
            if (hasDisease()) {
                if (timeOfDeath == -1) {
                    // Calculate the remaining life of the animal.
                    remainingLife(getTime());
                }
                
                // Check if the time of death has arrived.
                if (getTime() == timeOfDeath) {
                    setDead();
                    return;
                }
                
                // Iterate through ArrayList of adjacent animals.
                for(Iterator<Object> it = adjacentAnimals.iterator(); it.hasNext();) {
                    Object adjacentAnimal = it.next();
                    Animal animal = Animal.class.cast(adjacentAnimal);
                    // There is a 1% chance of spreading the disease onto adjacent animals.
                    if (Math.random() < 0.01) {
                        animal.setDisease(true);   
                    }
                }
            }
            
            // Check if the animal is a predator.
            if (Predator.class.isAssignableFrom(this.getClass())) {
                if ((getWeather() == "Snowing") || (getWeather() == "Stormy")) {
                    // A predator has a 1% chance of dying in snow or stormy weather.
                    if (Math.random() < 0.01) {
                        setDead();
                        return;
                    }
                }
            }
            
            // Check if the animal is awake.
            // Iterate through ArrayList of adjacent animals.
            if (!isAsleep()) {
                for(Iterator<Object> it = adjacentAnimals.iterator(); it.hasNext();) {
                    Object object = it.next();
                    Animal animal = Animal.class.cast(object);
                    // Check if the animal and it's neighbour are of the same species.
                    if (animal.getClass().equals(this.getClass())){
                        // If the genders of both animals are opposite, then give birth.
                        if (gender != animal.isMale()) {
                            giveBirth(newOrganism);
                            break;
                        }
                    }
                }
            }
            
            // Check if the animal is a predator.
            if (Predator.class.isAssignableFrom(this.getClass())) {
                // Check if the animal is a panther or a bear.
                if (Panther.class.isAssignableFrom(this.getClass())) {
                    if (isHungry("Panther")) {
                        if (canEat(0.95)) { // A panther will only find food 95% of the time due to poor visibility.
                            eat();
                        }
                    }
                }
                else if (Bear.class.isAssignableFrom(this.getClass())) {
                     if (!isAsleep()) {
                        if (isHungry("Bear")) {
                            if (canEat(0.9)) { // A bear will only find food 90% of the time due to poor visibility.
                                eat();
                            }
                        }
                    }
                }
            }
            else {
                if (canEat(0.995)) { // A prey animal will only find food 99.5% of the time due to poor visibility.
                    eat();
                }
            }
        }
    }
    
    /**
     * Return the animal's gender.
     * @return The animal's gender.
     */
    protected boolean isMale() {
        return gender;
    }
    
    /**
     * Checks if the animal is asleep or not.
     */
    abstract protected boolean isAsleep();
    
    /**
     * Returns the maximum possible age of the animal.
     */
    abstract protected int getMaxAge();
    
    /**
     * Sets the disease status of the animal to true/false.
     * @param disease Indicates if the animal has a disease.
     */
    protected void setDisease(boolean disease) {
        diseaseActive = disease;
    }
    
    /**
     * Indicates if the animal has a disease.
     * @return Indicates if the animal has a disease.
     */
    protected boolean hasDisease() {
        return diseaseActive;
    }
    
    /**
     * Ensures that the animal, if diseased, will
     * not live longer than 10 more minutes/steps.
     * @param minutes The current time in minutes. 
     */
    protected void remainingLife(int minutes) {
        timeOfDeath = minutes + 10;
    }
    
    /**
     * Returns true or false to indicate if the animal can eat or not.
     * @param probability The probability that an animal can eat if
     * the current weather is foggy or stormy.
     * @return true/false to indicate if the animal can eat or not.
     */
    protected boolean canEat(double probability) {
        boolean canEat = true;
        if ((getWeather() == "Foggy") || (getWeather() == "Stormy")) {
            if (Math.random() > probability) {
                canEat = false;
            }
        }
        return canEat;
    }
    
    /**
     * Makes the animal eat.
     */
    protected void eat() {
        // Move towards a source of food if found.
        Location newLocation = findFood();
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
    
    /**
     * Look for the animals adjacent to the current location.
     * Only the first live animal is eaten.
     * @return Where food was found, or null if it wasn't.
     */
    protected Location findFood()
    {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object organism = field.getObjectAt(where);
            if(organism instanceof Animal) {
                Animal animal = (Animal) organism;
                if(animal.isAlive() && isFood(animal)){
                    animal.setDead();
                    foodLevel = getFoodValue(animal);
                    return where;
                }
            }
            
            if(organism instanceof Plant) {
                Plant plant = (Plant) organism;
                if(plant.isAlive() && isFood(plant)){
                    plant.setDead();
                    foodLevel = getFoodValue(plant);
                    return where;
                }
            }
        }
        return null;
    }
    
    /**
     * Checks if the animal is hungry or not.
     * @param animal The name of the animal's class.
     */
    abstract protected boolean isHungry(String animal);
    
    /**
     * Returns food level of the animal.
     */
    abstract protected int getFoodLevel();
    
    /**
     * Checks if the parameter is food for the animal.
     * @param animal The animal instance.
     */
    abstract protected boolean isFood(Animal animal);
    
    /**
     * Checks if the parameter is food for the animal.
     * @param plant The plant instance.
     */
    abstract protected boolean isFood(Plant plant);
    
    /**
     * If the animal eats animals, this
     * returns the food value of the prey.
     * @param animal The animal instance.
     */
    abstract protected int getFoodValue(Animal animal);
    
    /**
     * If the animal eats plants, this
     * returns the food value of the plant.
     * @param plant The plant instance.
     */
    abstract protected int getFoodValue(Plant plant);
    
    /**
     * Make this animal more hungry. This could result in the animal's death.
     */
    protected void incrementHunger()
    {
        foodLevel--;
        if(foodLevel <= 0) {
            setDead();
        }
    }
}
