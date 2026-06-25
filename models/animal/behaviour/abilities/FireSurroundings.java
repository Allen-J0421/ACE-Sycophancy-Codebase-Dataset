package models.animal.behaviour.abilities;

import controller.WeatherController;
import models.animal.Animal;

/**
 * Ability which creates a hot temperature zone around the initiator.
 *
 */
public class FireSurroundings extends AbilityDecorator {
    private WeatherController weatherController;
    private Animal animal;
    private int size;

    /**
     * Constructor for FireSurroundings.
     * @param ability previously chained ability/abilities.
     * @param animal the animal calling the ability.
     * @param size the number of blocks in the zone
     */
    public FireSurroundings(Ability ability, Animal animal, int size) {
        super(ability);
        this.animal = animal;
        this.size = size;
        weatherController = WeatherController.getInstance();
    }

    /**
     * Chain the abilities together and generate the temperature zone.
     */
    @Override
    public void act() {
        super.act();
        weatherController.generateAbilityZone(animal.getLocation(), size, 70);
    }
}
