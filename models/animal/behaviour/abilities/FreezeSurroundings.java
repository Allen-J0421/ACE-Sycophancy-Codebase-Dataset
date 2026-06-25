package models.animal.behaviour.abilities;

import controller.WeatherController;
import models.animal.Animal;

/**
 * Creates a cold zone around the caster.
 *
 */
public class FreezeSurroundings extends AbilityDecorator {
    private WeatherController weatherController;
    private Animal animal;
    private int size;

    /**
     * Constructor for FreezeSurroundings
     * @param ability previously chained ability/abilities.
     * @param animal the animal casting this ability.
     * @param size the number of blocks in the zone
     */
    public FreezeSurroundings(Ability ability, Animal animal, int size) {
        super(ability);
        this.animal = animal;
        this.size = size;
        weatherController = WeatherController.getInstance();
    }

    /**
     * Chain the abilities and generate the temperature zone.
     */
    @Override
    public void act() {
        super.act();
        weatherController.generateAbilityZone(animal.getLocation(), size, 0);
    }
}
