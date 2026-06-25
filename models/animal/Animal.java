package models.animal;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import field.Actor;
import field.DayState;
import field.Edible;
import field.Entity;
import field.Field;
import field.Location;
import models.animal.behaviour.abilities.Ability;
import models.animal.behaviour.abilities.BlankAbility;
import models.animal.behaviour.breeding.BreedingBehaviour;
import models.animal.behaviour.breeding.Sex;
import models.animal.behaviour.movement.MovementBehaviour;

/**
 * Data model for an animal.
 *
 */
public abstract class Animal extends Actor implements Edible {
    // Magic
    protected int STARTING_ENERGY, MAX_ENERGY;
    // Energy required to breed
    protected int BREEDING_ENERGY;
    protected double BREEDING_PROBABILITY;
    protected int ENERGY_LOSS;
    // The rate at which the animal loses energy in suboptimal temperatures.
    // 0 = most efficient, no upper limit for energy efficiency.
    protected double ENERGY_EFFICIENCY;
    protected int OPTIMAL_TEMPERATURE;
    protected int MAX_LIFETIME;
    protected int MAX_CHILDREN;

    // State
    protected int children;
    protected int energy;
    protected int lifetime;
    protected Sex sex;

    protected MovementBehaviour movementBehaviour;
    protected BreedingBehaviour breedingBehaviour;
    protected Ability aliveAbility;
    protected Ability onDeathAbility;

    protected Field field;
    protected List<String> eats;

    // Utilities
    protected Random rand;

    /**
     * Constructor for Animal.
     * @param ID its ID
     * @param location its starting location
     * @param rgbColour its colour
     */
    public Animal(String ID, Location location, int[] rgbColour) {
        super(ID, location, rgbColour);

        // Initialise utils
        rand = new Random();
        
        // Magic (DEFAULT)
        STARTING_ENERGY = 50;
        MAX_ENERGY = 500;
        BREEDING_ENERGY = (int)(0.8f * MAX_ENERGY);
        BREEDING_PROBABILITY = 0.03;
        ENERGY_LOSS = 1;
        ENERGY_EFFICIENCY = 1;
        OPTIMAL_TEMPERATURE = 32;
        MAX_LIFETIME = 500;
        MAX_CHILDREN = 2;

        // Initialise state
        children = 0;
        lifetime = 0;
        field = Field.getInstance();
        eats = new ArrayList<>();

        aliveAbility = new BlankAbility();
        onDeathAbility = new BlankAbility();

        // Logic
        if (rand.nextDouble() <= 0.5) {
            sex = Sex.MALE;
        } else {
            sex = Sex.FEMALE;
        }
    }

    /**
     * How shoud it breed
     * @param newActors the list to which add new actors to.
     * @param entity the other animal in contact.
     */
    protected abstract void breed(List<Actor> newActors, Entity entity);

    /**
     * Eating behaviour.
     * @param entity the entity to eat.
     */
    private void eat(Entity entity) {
        if (entity instanceof Edible) {
            Edible food = (Edible)entity;
            energy += food.getNutrition();
            energy = Math.min(energy, MAX_ENERGY);
        }

        Location toMove = entity.getLocation();
        entity.die();

        field.setObjectAt(toMove, this);
        field.setObjectAt(location, null);
        setLocation(toMove);
    }

    /**
     * What the animal does every Simulation step.
     * It will enact its movement and its abilities, then it will look for mates.
     * If it is unsuccessful in finding mates, it will try to find food.
     */
    @Override
    public void act(List<Actor> newActors) {
        lifetime++;
        
        if (field.getDayState() == DayState.NIGHT) return;
        
        movementBehaviour.act();
        aliveAbility.act();

        List<Entity> entities = new ArrayList<>();
        List<Location> locations = field.getAdjacentLocations(this.location);
        for (Location location : locations) {
            Entity entity = field.getBlockAt(location).getEntity();
            if (entity != null) {
                entities.add(entity);
            }
        }

        for (Entity entity : entities) {
            if (this.getID().equals(entity.getID())) {
                if (energy >= BREEDING_ENERGY) {
                    breed(newActors, entity);
                    return;
                }
            }
        }

        for (Entity entity : entities) {
            if (eats.contains(entity.getID())) {
                eat(entity);
                return;
            }
        }

        int temperature = field.getBlockAt(location).getTemperature();
        int extra_energy_loss = (int)(ENERGY_EFFICIENCY * Math.abs(temperature - OPTIMAL_TEMPERATURE));
        
        energy = energy - ENERGY_LOSS - extra_energy_loss; 

        if (energy <= 0) {
            die();
        }

        if (lifetime > MAX_LIFETIME) {
            die();
        }
    }

    /**
     * How the animal should die.
     * Activate its on death ability, set the flag to
     * remove it from actors and delete it from its location.
     */
    @Override
    public void die() {
        onDeathAbility.act();
        setDead();
        field.setObjectAt(location, null);
    } 

    public int getAge() {
        return lifetime;
    }

    public int getMaxAge() {
        return MAX_LIFETIME;
    }

    public int getChildren() {
        return children;
    }

    /**
     * Set the number of children that the animal has.
     * @param children the number of children.
     */
    public void setChildren(int children) {
        this.children = children;
    }

    public int getMaxChildren() {
        return MAX_CHILDREN;
    }

    @Override
    public int getNutrition() {
        return energy;
    }
}
