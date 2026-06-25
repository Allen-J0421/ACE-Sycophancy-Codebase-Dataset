package models.animal.behaviour.movement;
import java.util.Random;

import field.Location;
import models.animal.Animal;

/**
 * Move the animal to a random adjacent square.
 *
 */
public class RandomMovement extends MovementBehaviour {

    private int[] dRow = new int[]{ 1, -1, 0, 0, 1, -1, 1, -1 };
    private int[] dCol = new int[]{ 0, 0, 1, -1, 1, -1, -1, 1 };

    private Random rand;
    
    /**
     * Constructor for RandomMovement
     * @param animal the animal to be moved
     */
    public RandomMovement(Animal animal) {
        super(animal);
        rand = new Random();
    }
    
    /**
     * Move the animal to a random adjacent square.
     */
    @Override
    public void act() {
        Location animal_location = animal.getLocation();
        int[] lst = new int[8];
        boolean found = false;

        Location location = new Location(animal_location.getRow(), animal_location.getCol());

        for (int i=0; i<8; i++) {
            int j = rand.nextInt(8);
            while (lst[j] != 0) {
                j = rand.nextInt(8);
            }
            
            lst[j] = 1;

            int row = animal_location.getRow() + dRow[j],
                col = animal_location.getCol() + dCol[j];

            location = new Location(row, col);

            if (!field.inBounds(location)) 
                continue;

            if (field.getBlockAt(location).getEntity() == null) {
                found = true;
                break;
            }
        }
        
        if (found) {
            Location temp = animal.getLocation();
            field.setObjectAt(temp, null);
            field.setObjectAt(location, animal);
            animal.setLocation(location);
        } else {
            //animal.die();
        }
    }
}
