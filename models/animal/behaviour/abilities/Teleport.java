package models.animal.behaviour.abilities;

import java.util.Random;

import field.Entity;
import field.Field;
import field.Location;
import models.animal.Animal;

/**
 * Teleports the animal to a new random location.
 */
public class Teleport extends AbilityDecorator {
    private Field field;
    private Animal animal;
    private Random random;
    
    /**
     * Constructor for Teleport
     * @param ability previously chained abilities
     * @param animal the animal to teleport
     */
    public Teleport(Ability ability, Animal animal) {
        super(ability);
        this.animal = animal;

        field = Field.getInstance();
        random = new Random();
    }

    /**
     * Pick a random location and transport the animal there.
     */
    @Override
    public void act() {
        super.act();

        int row = random.nextInt(field.getRows());
        int col = random.nextInt(field.getCols());
        
        Location location = new Location(row, col);
        Location temp = animal.getLocation();

        Entity e = field.getBlockAt(location).getEntity();
        
        if (e != null) {
            e.die();
        }

        field.setObjectAt(location, animal);
        field.setObjectAt(temp, null);

        animal.setLocation(location);
    }
}
