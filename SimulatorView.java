import javax.swing.*;
import java.awt.*;


public class SimulatorView extends JFrame {

	private static final Color EMPTY_COLOR = Color.white;

	private static final String STEP_PREFIX = "Step: ";
	private static final String POPULATION_PREFIX = "Population: ";
	private static final String DAYCYCLE_PREFIX = "Day cycle: ";
	private static final String CLIMATE_PREFIX = "Season: ";
	private static final String WEATHER_PREFIX = "Weather: ";
	private static final String INFECTION_PREFIX = "Infection: ";
	private static final String HUMIDITY_PREFIX = "Humidity: ";

	private JLabel stepLabel, populationLabel, dayCycleLabel, climateLabel, infectLabel, weatherLabel, humidityLabel;
	private FieldView fieldView;

	private FieldStats stats;


	public SimulatorView(int height, int width) {
		stats = new FieldStats();

		setTitle("Predator-Prey Simulation");
		stepLabel = new JLabel(STEP_PREFIX, JLabel.CENTER);
		populationLabel = new JLabel(POPULATION_PREFIX, JLabel.CENTER);
		dayCycleLabel = new JLabel(DAYCYCLE_PREFIX, JLabel.CENTER);
		climateLabel = new JLabel(CLIMATE_PREFIX, JLabel.CENTER);
		weatherLabel = new JLabel(WEATHER_PREFIX, JLabel.CENTER);
		infectLabel = new JLabel(INFECTION_PREFIX, JLabel.CENTER);
		humidityLabel = new JLabel(HUMIDITY_PREFIX, JLabel.CENTER);

		setLocation(100, 50);

		fieldView = new FieldView(height, width);

		Container contents = getContentPane();

		JPanel infoPane = new JPanel(new FlowLayout());
		JPanel detailPane = new JPanel(new BorderLayout());

		infoPane.add(stepLabel);
		infoPane.add(dayCycleLabel);
		infoPane.add(climateLabel);
		infoPane.add(weatherLabel);
		infoPane.add(humidityLabel);
		infoPane.add(infectLabel);
		contents.add(infoPane, BorderLayout.NORTH);
		contents.add(fieldView, BorderLayout.CENTER);
		contents.add(detailPane, BorderLayout.SOUTH);
		detailPane.add(populationLabel, BorderLayout.CENTER);

		pack();
		setVisible(true);
	}


	public void showStatus(int step, TimeCycle currentTimeCycle, Field field, Climate climate, int infect) {
		if (!isVisible()) {
			setVisible(true);
		}

		stepLabel.setText(STEP_PREFIX + step);
		dayCycleLabel.setText(DAYCYCLE_PREFIX + currentTimeCycle + " ");
		climateLabel.setText(CLIMATE_PREFIX + climate.getCurrentSeason());
		weatherLabel.setText(WEATHER_PREFIX + climate.getCurrentWeather());
		infectLabel.setText(INFECTION_PREFIX + infect + "%");
		humidityLabel.setText(HUMIDITY_PREFIX + climate.getHumidity() + "%");
		stats.reset();

		fieldView.preparePaint();

		for (int row = 0; row < field.getDepth(); row++) {
			for (int col = 0; col < field.getWidth(); col++) {
				Object animalObj = field.getAnimalAt(row, col);
				Object plantObj = field.getPlantAt(row, col);
				if (animalObj instanceof Animal) {
					Animal animal = (Animal) animalObj;
					stats.incrementCount(animal.getClass());
					fieldView.drawMark(col, row, animal.getObjectColor(climate));
				} else if (plantObj instanceof Plant) {
					fieldView.drawMark(col, row, ((Plant) plantObj).getObjectColor(climate));
				} else {
					fieldView.drawMark(col, row, EMPTY_COLOR);
				}
			}
		}
		stats.countFinished();

		populationLabel.setText(POPULATION_PREFIX + stats.getPopulationDetails(field));
		fieldView.repaint();
	}


	public boolean isViable(Field field) {
		return stats.isViable(field);
	}


	private class FieldView extends JPanel {
		private static final int GRID_VIEW_SCALING_FACTOR = 5;
		private Dimension size;
		private int gridWidth;
		private int gridHeight;
		private int xScale;
		private int yScale;
		private Graphics g;
		private Image fieldImage;


		public FieldView(int height, int width) {
			gridHeight = height;
			gridWidth = width;
			size = new Dimension(0, 0);
		}


		@Override
		public Dimension getPreferredSize() {
			return new Dimension(gridWidth * GRID_VIEW_SCALING_FACTOR,
					gridHeight * GRID_VIEW_SCALING_FACTOR);
		}


		public void preparePaint() {
			if (!size.equals(getSize())) {
				size = getSize();
				fieldImage = fieldView.createImage(size.width, size.height);
				g = fieldImage.getGraphics();

				xScale = size.width / gridWidth;
				if (xScale < 1) {
					xScale = GRID_VIEW_SCALING_FACTOR;
				}
				yScale = size.height / gridHeight;
				if (yScale < 1) {
					yScale = GRID_VIEW_SCALING_FACTOR;
				}
			}
		}


		public void drawMark(int x, int y, Color color) {
			g.setColor(color);
			g.fillRect(x * xScale, y * yScale, xScale - 1, yScale - 1);
			g.drawRect(x * xScale, y * yScale, xScale - 1, yScale - 1);
		}


		@Override
		public void paintComponent(Graphics g) {
			if (fieldImage != null) {
				Dimension currentSize = getSize();
				if (size.equals(currentSize)) {
					g.drawImage(fieldImage, 0, 0, null);
				} else {
					g.drawImage(fieldImage, 0, 0, currentSize.width, currentSize.height, null);
				}
			}
		}
	}
}
