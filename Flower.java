import java.awt.*;
import java.util.Random;


public class Flower extends Plant {

	private static final Random rand = Randomizer.getRandom();

	private Color plantColour;


	public Flower(Field field, Location location) {
		super(field, location);
		setMaxStage(1);
		setStage(getMaxStage());

		double colorRoll = rand.nextDouble();
		if (colorRoll < 0.33) {
			plantColour = new Color(236, 0, 0);
		} else if (colorRoll < 0.66) {
			plantColour = new Color(236, 98, 2);
		} else {
			plantColour = new Color(122, 0, 236);
		}
	}


	@Override
	protected Color getObjectColor(Climate climate) {
		return getStage() == 1 ? plantColour : new Color(131, 101, 57);
	}
}
