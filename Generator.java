import java.util.ArrayList;
import java.util.Random;


/**
 * Generator generates the players, and disesease for the sim 
 * 
 * @version 6.8.2
 */
class Generator {
    private Random rand = Randomizer.getRandom(); // Random number generator required for generating probabilities


    /**
     * Function which creates all the players for a field 
     * @param field the field for which all the simulatables will be generated
     * @return the list of all simulatable objects
     */
	public ArrayList<Simulatable> createPlayers(Field field) {
		
		ArrayList<Simulatable> simulatables = new ArrayList<>();
		for(int row = 0; row < field.getDepth(); row++) {
            for(int col = 0; col < field.getWidth(); col++) {
                Disease disease = generateDisease(); // Get a disease (could be null) to give the animal 
                if(rand.nextDouble() <= Racoon.POPULATION_CONTROLS.getCreationProbability()) {
                    Location location = new Location(row, col);
                    Racoon racoon = new Racoon(true, field, location);
                    racoon.forceDisease(disease);//Give the animal a disease
                    simulatables.add(racoon);
                } 
                else if(rand.nextDouble() <= Hawk.POPULATION_CONTROLS.getCreationProbability()) {
                    Location location = new Location(row, col);
                    Hawk mouse = new Hawk(true, field, location);
                    mouse.forceDisease(disease);
                    simulatables.add(mouse);
                }
                else if(rand.nextDouble() <= Mouse.POPULATION_CONTROLS.getCreationProbability()) {
                    Location location = new Location(row, col);
                    Mouse mouse = new Mouse(true, field, location);
                    mouse.forceDisease(disease);
                    simulatables.add(mouse);
                }
                else if(rand.nextDouble() <= Rat.POPULATION_CONTROLS.getCreationProbability()) {
                    Location location = new Location(row, col);
                    Rat mouse = new Rat(true, field, location);
                    mouse.forceDisease(disease);
                    simulatables.add(mouse);
                }
                else if(rand.nextDouble() <= Squirrel.POPULATION_CONTROLS.getCreationProbability()) {
                    Location location = new Location(row, col);
                    Squirrel mouse = new Squirrel(true, field, location);
                    mouse.forceDisease(disease);
                    simulatables.add(mouse);
                }
                else if(rand.nextDouble() <= Grass.CREATION_PROBABILITY) {//Generate the grass objects
                    Location location = new Location(row, col);
                    Grass mouse = new Grass(field, location);
                    simulatables.add(mouse); 
                } 

                // else leave the location empty.
            }
        }

        return simulatables;

	}

    /** 
     * Genreates a disease for an animal 
     * @return the generated disease. If no disease, returns null 
     */
    private Disease generateDisease() {
        if (rand.nextDouble() <= Rabies.CREATION_PROBABILITY) {
            return new Rabies();
        }
        return null;
    }

}