import java.awt.*;
import java.util.Random;


public class Grass extends Plant {

	private static final Random rand = Randomizer.getRandom();


	public Grass(Field field, Location location) {
		super(field, location);

		double stageRoll = rand.nextDouble();
		if (stageRoll < 0.5) {
			setMaxStage(3);
		} else if (stageRoll < 0.8) {
			setMaxStage(2);
		} else {
			setMaxStage(1);
		}
		setStage(getMaxStage());
	}


	@Override
	protected void increaseStage(Climate climate) {
		super.increaseStage(climate);
		Season season = climate.getCurrentSeason();
		if ((season == Season.SPRING || season == Season.AUTUMN) && getStage() > 2) {
			setStage(2);
		} else if (season == Season.SUMMER && getStage() > 3) {
			setStage(3);
		} else if (season == Season.WINTER && getStage() > 1) {
			setStage(1);
		}
	}


	@Override
	protected Color getObjectColor(Climate climate) {
		switch (climate.getCurrentSeason()) {
			case SPRING:
				if (getStage() == 2) {
					return new Color(34, 140, 61);
				} else if (getStage() == 1) {
					return new Color(68, 201, 104);
				} else {
					return new Color(131, 101, 57);
				}
			case SUMMER:
				if (getStage() == 3) {
					return new Color(11, 102, 35);
				} else if (getStage() == 2) {
					return new Color(34, 140, 61);
				} else if (getStage() == 1) {
					return new Color(68, 201, 104);
				} else {
					return new Color(131, 101, 57);
				}
			case AUTUMN:
				if (getStage() == 2) {
					return new Color(68, 201, 104);
				} else if (getStage() == 1) {
					return new Color(154, 140, 84);
				} else {
					return new Color(131, 101, 57);
				}
			case WINTER:
				if (getStage() == 1) {
					return new Color(214, 233, 202);
				} else {
					return new Color(255, 250, 250);
				}
			default:
				return new Color(0, 0, 0);
		}
	}
}
