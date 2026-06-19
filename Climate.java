import java.util.Random;


public class Climate {

	private static final int SEASON_LENGTH = 16;

	private static final int MIN_HUMIDITY_CHANGE = 10;

	private static final int MAX_HUMIDITY_CHANGE = 20;

	private static final int CLOUD_HUMIDITY_THRESHOLD = 80;

	private static final int RAIN_HUMIDITY_THRESHOLD = 100;

	private static final Random rand = Randomizer.getRandom();

	private Weather currentWeather;

	private Season currentSeason;

	private int humidity;


	public Climate(Weather currentWeather) {
		reset(currentWeather);
	}


	public void updateClimate(int step) {
		updateSeason(step);
		updateWeather(randomHumidityChange());
	}


	private int randomHumidityChange() {
		return rand.nextInt((MAX_HUMIDITY_CHANGE - MIN_HUMIDITY_CHANGE) + 1) + MIN_HUMIDITY_CHANGE;
	}


	private void updateWeather(int humidityChange) {
		if (humidity < CLOUD_HUMIDITY_THRESHOLD && currentWeather.buildsHumidity()) {
			humidity += humidityChange;
		} else if (humidity < RAIN_HUMIDITY_THRESHOLD && currentWeather.buildsHumidity()) {
			humidity += humidityChange;
			currentWeather = Weather.CLOUD;
		} else if (canStartRaining()) {
			currentWeather = Weather.RAIN;
		} else if (humidity > CLOUD_HUMIDITY_THRESHOLD && currentWeather == Weather.RAIN) {
			humidity -= humidityChange;
		} else {
			resetWeather();
		}
	}


	private void updateSeason(int step) {
		currentSeason = Season.forStep(step, SEASON_LENGTH);
	}


	private boolean canStartRaining() {
		return humidity > RAIN_HUMIDITY_THRESHOLD
				&& currentWeather.buildsHumidity()
				&& currentSeason != Season.WINTER;
	}


	private void resetWeather() {
		humidity = 0;
		currentWeather = Weather.SUN;
	}


	public Weather getCurrentWeather() {
		return currentWeather;
	}


	public void reset(Weather weather) {
		currentWeather = weather;
		currentSeason = Season.SPRING;
		humidity = 0;
	}


	public Season getCurrentSeason() {
		return currentSeason;
	}


	public int getHumidity() {
		return humidity;
	}

}
