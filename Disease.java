import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Collections;
import java.util.Random;

/**
 * Models a generic disease which can affect organisms and cause them to die
 *
 * @version 2022.02.28
 */
public abstract class Disease implements Actor
{
    // The field of the simulation
    private Field field;
    // A list of the species that the disease affects
    private List<String> affectedSpecies;
    // A hashmap containing organisms currently infected with disease
    // and how long they've been infected for
    private HashMap<Actor, Integer> affected;
    // The probability of the disease randomly occuring in the population
    private double probability;
    // The probability of the disease spreading
    private double infectiousness;
    // How long the disease affects an infected organism for
    private int duration;
    // Randomiser to control random spread of disease
    private Random rand = new Random();
    
    /**
     * Constructor for a disease
     * 
     * @param The field currently occupied
     */
    public Disease(Field field)
    {
        this.field = field;
        affectedSpecies = new ArrayList<>();
        affected = new HashMap<>();
        probability = 0.0001;
    }

    /**
     * Make this actor act - that is: disease spreads and kills any 
     * infected individuals after a certain number of days
     * @param newActors A list to receive any actors relevant to action
     */
    public void act(List<Actor> actorsList)
    {
        // Loops through every infected animal
        Iterator<Actor> it = affected.keySet().iterator();
        // List to hold individuals who are newly infected by disease
        List<Actor> newInfections = new ArrayList<>();
        while (it.hasNext()) {
            Actor infected = it.next();
            newInfections.addAll(spread(infected));
            if (affected.get(infected) == duration) {
                // After certain number of days, infected individual 
                // dies (animal)/ is cured (water)
                it.remove();
                if (infected instanceof Organism) {
                    Organism organism = (Organism) infected;
                    organism.setDead();
                    }
                else {
                    WaterSources water = (WaterSources) infected;
                    water.notInfected();
                }
            }
            else {
                // Increases the number of days an individual has been infected by 1
                int days = affected.get(infected) + 1;
                affected.put(infected, days);
            }
        }
        for (Actor infectee: newInfections) {
            // adds any newly infected individuals to the list and sets days infected to 0
            if (infectee instanceof Organism) {
                Organism organism = (Organism) infectee;
                organism.setInfected();
                affected.put(organism, 0);  
            }
            else if (infectee instanceof WaterSources)  {
                WaterSources water = (WaterSources) infectee;
                water.setInfected();
                affected.put(water, 0);  
            }
        }
    }
    
    /** 
     * Spreads disease to nearby individuals who can be infected
     * 
     * @param The actor which is infected and spreads disease to 
     * neighbouring individuals
     */
    public List<Actor> spread(Actor actor) {
        List<Actor> newlyInfected = new ArrayList<>();
        List<Location> adjacent = null;
        // looks at surrounding individuals and chance of infecting them if possible
        if (actor instanceof Organism) {
            Organism organism = (Organism) actor;
            if (organism.isAlive()) {
                adjacent = field.adjacentLocations(organism.getLocation());
            }
        }
        else if (actor instanceof WaterSources) {
            WaterSources water = (WaterSources) actor;
            if (!water.isEmpty()) {
                adjacent = field.adjacentLocations(water.getLocation());
            }
        }
        if(adjacent != null) {
            Iterator<Location> it = adjacent.iterator();
            while(it.hasNext()) {
                Location where = it.next();
                Object thing = field.getObjectAt(where);
                if (thing != null && affectedSpecies.contains(thing.getClass().getName())) {
                    if (rand.nextDouble() < infectiousness) {
                        if (thing instanceof Organism) {
                            Organism infectee = (Organism) thing;
                            newlyInfected.add(infectee);
                        }
                        else if (thing instanceof WaterSources) {
                            WaterSources infectee = (WaterSources) thing;
                            newlyInfected.add(infectee);
                        }
                    }
                }
            }
        }
        return newlyInfected;
    }
    
    /**
     * Method to add species which can be infected with the disease
     * @param species Varargs so can take any number of species to be infected
     */
    protected void addSpecies(String... species) {
        Collections.addAll(affectedSpecies, species);
    }
    
    /** 
     * @return The  list of species affected by this disease
     */
    public List<String> getSpecies() {
        return affectedSpecies;
    }
    
    /**
     * Adds an infected individual to the list of those currently infected 
     * @actor Actor to be infected
     */
    public void addIndividual(Actor actor) {
        affected.put(actor, 0);
    }
    
    /**
     * Method to set probability of disease randomly occurring
     * @param Probability of disease occurring
     */
    protected void setProbability(double prob) {
        probability = prob;
    }
    
    /**
     * @return The probability of the disease randomly occuring
     */
    public double getProbability() {
        return probability;
    }
    
    /**
     * Method to set probability of disease spreading to nearby individuals
     * @param Probability of disease spreading to neighbours
     */
    protected void setInfectiousness(double prob) {
        infectiousness = prob;
    }
    
    /**
     * Method to set duration of how long a disease infects an individual 
     * before it is cured/dies
     * @param duration The number of days an infection lasts
     */
    protected void setDuration(int duration) {
        this.duration = duration;
    }
}
