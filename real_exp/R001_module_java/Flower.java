import java.awt.*;
import java.util.Random;


public class Flower extends Plant {

	private static final Random rand = Randomizer.getRandom();

	private Color plantColour;


	public Flower(Field field, Location location) {
		super(field, location);
		setMaxStage(1);
		setStage(getMaxStage());


		if (rand.nextDouble() <= 0.33) {
			plantColour = new Color(236, 0, 0);
		} else if (rand.nextDouble() <= 0.33) {
			plantColour = new Color(236, 98, 2);
		} else {
			plantColour = new Color(122, 0, 236);
		}
	}


	protected Color getObjectColor(Climate climate) {
		if (getStage() == 1) {
			return plantColour;
		} else {
			return new Color(131, 101, 57);
		}
	}
}
