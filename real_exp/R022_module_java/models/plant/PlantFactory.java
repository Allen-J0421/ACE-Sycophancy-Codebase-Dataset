package models.plant;

import field.Location;
import models.disease.DiseasedPlant;
import models.plant.implementations.Cactus;
import models.plant.implementations.FrostFlower;
import models.plant.implementations.Grass;
import models.plant.implementations.TallGrass;

/**
 * Factory to instantiate plants by ID.
 *
 */
public class PlantFactory {
    public static Plant get(String id, Location location) {
        if ("grass".equals(id)) {
            return new Grass(location);
        }
        else if ("tall_grass".equals(id)) {
            return new TallGrass(location);
        }
        else if ("cactus".equals(id)) {
            return new Cactus(location);
        }
        else if ("frost_flower".equals(id)) {
            return new FrostFlower(location);
        }
        else if ("diseased_plant".equals(id)) {
            return new DiseasedPlant(location);
        }

        return null;
    }
}
