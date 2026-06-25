package models.plant;

import java.util.List;

import field.Actor;
import field.Block;
import field.Edible;
import field.Location;
import field.Field;
import models.plant.behaviour.abilities.Behaviour;
import models.plant.behaviour.abilities.BlankBehaviour;
import models.plant.behaviour.breeding.BreedingBehaviour;
import models.plant.behaviour.breeding.bfs.FullBfsBreeding;

/**
 * Data model for a Plant. 
 *
 */
public abstract class Plant extends Actor implements Edible {
    
    protected int[] rgbColour;
    protected int nutrition;
    protected Field field;

    protected int WATER_INTAKE;
    protected int OPTIMAL_TEMPERATURE;
    protected int STARTING_NUTRITION, MAX_NUTRITION;
    protected int ENERGY_LOSS;
    protected double ENERGY_EFFICIENCY;
    protected double BREEDING_PROBABILITY;

    protected BreedingBehaviour breedingBehaviour;
    protected Behaviour aliveBehaviour;
    protected Behaviour onDeathBehaviour;

    /**
     * Constructor for Plant.
     * @param ID the ID of the species
     * @param location the initial location of the plant
     * @param rgbColour the colour of the plant in RGB
     */
    public Plant(String ID, Location location, int[] rgbColour) {
        super(ID, location, rgbColour);
        
        field = Field.getInstance();

        // Magic (DEFAULT)
        WATER_INTAKE = 2;
        OPTIMAL_TEMPERATURE = 32;
        STARTING_NUTRITION = 50;
        MAX_NUTRITION = 100;
        ENERGY_EFFICIENCY = 0;
        ENERGY_LOSS = 1;
        BREEDING_PROBABILITY = 0.015;

        // State
        nutrition = STARTING_NUTRITION;
        breedingBehaviour = new FullBfsBreeding(location.getRow(), location.getCol());
        aliveBehaviour = new BlankBehaviour();
        onDeathBehaviour = new BlankBehaviour();
    }

    /**
     * What the plant does every step of the simulation.
     */
    @Override
    public abstract void act(List<Actor> newActors);

    /**
     * Enact the plant's on death behaviour and remove it from the simulator.
     */
    @Override
    public void die() {
        onDeathBehaviour.act();
        setDead();
        field.getBlockAt(location).setEntity(null);
    }

    /**
     * Increase the nutrition the plant gives according to the water level available on the block it's on.
     * Decrease energy according to the parameters set.
     */
    protected void grow() {
        Block block = field.getBlockAt(location);
        int waterLevel = block.getWaterLevel();
        int temperature = block.getTemperature();
        
        int extra_energy_loss = 0;

        if (waterLevel >= WATER_INTAKE && nutrition < MAX_NUTRITION) {
            waterLevel -= WATER_INTAKE;
            nutrition = Math.min(MAX_NUTRITION, nutrition + WATER_INTAKE);
            block.setWaterLevel(waterLevel);
        }

        extra_energy_loss = (int)(ENERGY_EFFICIENCY * Math.abs(temperature - OPTIMAL_TEMPERATURE));
        
        nutrition = nutrition - ENERGY_LOSS - extra_energy_loss; 

        if (nutrition <= 0) {
            die();
        }
    }

    /* Getters */

    public int getNutrition() {
        return nutrition;
    }

    public int getOptimalTemperature() {
        return OPTIMAL_TEMPERATURE;
    }
}
