import java.util.Random;


public class Climate {

	private static final int SEASON_LENGTH = 16;

	private static final int MIN_HUMIDITY_CHANGE = 10;

	private static final int MAX_HUMIDITY_CHANGE = 20;

	private static final Random rand = Randomizer.getRandom();

	private Weather currentWeather;

	private Season currentSeason;

	private int humidity;


	public Climate(Weather currentWeather) {
		this.currentWeather = currentWeather;
		currentSeason = Season.SPRING;
	}


	public void updateClimate(int step) {
		updateSeason(step);


		int humidityIncrease = rand.nextInt(MAX_HUMIDITY_CHANGE - MIN_HUMIDITY_CHANGE + 1) + MIN_HUMIDITY_CHANGE;

		if (humidity < 80 && (currentWeather == Weather.SUN || currentWeather == Weather.CLOUD)) {
			humidity += humidityIncrease;
		} else if (humidity < 100 && (currentWeather == Weather.SUN || currentWeather == Weather.CLOUD)) {
			humidity += humidityIncrease;
			currentWeather = Weather.CLOUD;
		} else if (humidity > 100 && (currentWeather == Weather.SUN || currentWeather == Weather.CLOUD) && currentSeason != Season.WINTER) {
			currentWeather = Weather.RAIN;
		} else if (humidity > 80 && currentWeather == Weather.RAIN) {
			humidity -= humidityIncrease;
		} else {
			humidity = 0;
			currentWeather = Weather.SUN;
		}
	}


	private void updateSeason(int step) {
		switch ((step / SEASON_LENGTH) % 4) {
			case 0:
				currentSeason = Season.SPRING;
				break;
			case 1:
				currentSeason = Season.SUMMER;
				break;
			case 2:
				currentSeason = Season.AUTUMN;
				break;
			case 3:
				currentSeason = Season.WINTER;
				break;
		}
	}


	public Weather getCurrentWeather() {
		return currentWeather;
	}


	public void setCurrentWeather(Weather currentWeather) {
		this.currentWeather = currentWeather;
	}


	public Season getCurrentSeason() {
		return currentSeason;
	}


	public int getHumidity() {
		return humidity;
	}

}
