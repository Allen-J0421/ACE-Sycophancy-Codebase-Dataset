package models.animal;

import field.Location;
import models.animal.implementations.Dragon;
import models.animal.implementations.Elk;
import models.animal.implementations.Gnome;
import models.animal.implementations.Slime;
import models.animal.implementations.Phoenix;
import models.animal.implementations.Wolf;
import models.disease.DiseasedAnimal;

/**
 * Factory to instantiate animals by ID.
 *
 */
public class AnimalFactory {
    /** 
     * Factory method to instantiate animals by ID.
     * @param id the ID of the animal
     * @param location the initial location of the animal
     */
    public static Animal get(String id, Location location) {        
        if ("wolf".equalsIgnoreCase(id)) {
            return new Wolf(location);
        }
        else if ("slime".equalsIgnoreCase(id)) {
            return new Slime(location);
        }
        else if ("elk".equalsIgnoreCase(id)) {
            return new Elk(location);
        } 
        else if ("gnome".equalsIgnoreCase(id)) {
            return new Gnome(location);
        }
        else if ("phoenix".equalsIgnoreCase(id)) {
            return new Phoenix(location);
        }
        else if ("dragon".equalsIgnoreCase(id)) {
            return new Dragon(location);
        }
        else if ("diseased_animal".equals(id)) {
            return new DiseasedAnimal(location);
        }
        return null;
    }
}
