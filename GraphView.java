import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.IntStream;


public class GraphView extends JFrame implements SimulationObserver {
	private static final Color LIGHT_GRAY = new Color(0, 0, 0, 40);

	private static JFrame frame;
	private static GraphPanel graph;
	private static JLabel stepLabel;
	private static JLabel countLabel;

	private final Map<Class<?>, Color> colors;

	private final FieldStats stats;


	public GraphView(int width, int height, int startMax) {
		stats = new FieldStats();
		colors = new LinkedHashMap<>();

		if (frame == null) {
			frame = makeFrame(width, height, startMax);
		} else {
			graph.newRun();
		}

	}


	public void setColor(Class<?> animalClass, Color color) {
		colors.put(animalClass, color);
	}


	@Override
	public Set<Class<? extends SimulationEvent>> getSubscribedEventTypes() {
		return Set.of(SimulationResetEvent.class, SimulationStepCompletedEvent.class, PopulationChangedEvent.class);
	}


	@Override
	public void onSimulationEvent(SimulationEvent event) {
		if (event instanceof PopulationChangedEvent) {
			registerColors(event.getSnapshot());
			return;
		}

		SimulationSnapshot snapshot = event.getSnapshot();
		registerColors(snapshot);
		if (event instanceof SimulationResetEvent) {
			reset();
		}
		graph.update(snapshot.getStep(), snapshot.getField(), stats);
	}


	private void reset() {
		stats.reset();
		graph.newRun();
	}


	private JFrame makeFrame(int width, int height, int startMax) {
		JFrame frame = new JFrame("Graph View");
		frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

		Container contentPane = frame.getContentPane();

		graph = new GraphPanel(width, height, startMax);
		contentPane.add(graph, BorderLayout.CENTER);

		JPanel bottom = new JPanel();
		bottom.add(new JLabel("Step:"));
		stepLabel = new JLabel("");
		bottom.add(stepLabel);
		countLabel = new JLabel(" ");
		bottom.add(countLabel);
		contentPane.add(bottom, BorderLayout.SOUTH);

		frame.pack();
		frame.setLocation(20, 600);

		frame.setVisible(true);

		return frame;
	}


	class GraphPanel extends JComponent {
		private static final double SCALE_FACTOR = 0.8;


		private BufferedImage graphImage;
		private int[] lastVal;
		private int yMax;


		public GraphPanel(int width, int height, int startMax) {
			graphImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			clearImage();
			lastVal = new int[getTrackedClassCount()];
			Arrays.fill(lastVal, height);
			yMax = startMax;
		}


		public void newRun() {
			int height = graphImage.getHeight();
			int width = graphImage.getWidth();

			Graphics g = graphImage.getGraphics();
			g.copyArea(4, 0, width - 4, height, -4, 0);
			g.setColor(Color.BLACK);
			g.drawLine(width - 4, 0, width - 4, height);
			g.drawLine(width - 2, 0, width - 2, height);
			lastVal = new int[getTrackedClassCount()];
			Arrays.fill(lastVal, height);
			repaint();
		}


		public void update(int step, FieldEnvironment field, FieldStats stats) {

			Graphics g = graphImage.getGraphics();

			int height = graphImage.getHeight();
			int width = graphImage.getWidth();


			g.copyArea(1, 0, width - 1, height, -1, 0);

			stats.reset();

			List<Class<?>> trackedClasses = new ArrayList<>(colors.keySet());
			IntStream.range(0, trackedClasses.size())
					.forEach(index -> updateGraphLine(g, field, stats, trackedClasses.get(index), index, width, height));

			repaint();

			stepLabel.setText("" + step);
			countLabel.setText(stats.getPopulationDetails(field));

		}


		public void scaleDown() {
			Graphics g = graphImage.getGraphics();
			int height = graphImage.getHeight();
			int width = graphImage.getWidth();

			BufferedImage tmpImage = new BufferedImage(width, (int) (height * SCALE_FACTOR),
					BufferedImage.TYPE_INT_RGB);
			Graphics2D gtmp = (Graphics2D) tmpImage.getGraphics();

			gtmp.scale(1.0, SCALE_FACTOR);
			gtmp.drawImage(graphImage, 0, 0, null);

			int oldTop = (int) (height * (1.0 - SCALE_FACTOR));

			g.setColor(Color.WHITE);
			g.fillRect(0, 0, width, oldTop);
			g.drawImage(tmpImage, 0, oldTop, null);

			yMax = (int) (yMax / SCALE_FACTOR);

			IntStream.range(0, lastVal.length)
					.forEach(index -> lastVal[index] = oldTop + (int) (lastVal[index] * SCALE_FACTOR));

			repaint();
		}


		final public void clearImage() {
			Graphics g = graphImage.getGraphics();
			g.setColor(Color.WHITE);
			g.fillRect(0, 0, graphImage.getWidth(), graphImage.getHeight());
			repaint();
		}


		public Dimension getPreferredSize() {
			return new Dimension(graphImage.getWidth(), graphImage.getHeight());
		}


		public boolean isOpaque() {
			return true;
		}


		public void paintComponent(Graphics g) {
			if (graphImage != null) {
				g.drawImage(graphImage, 0, 0, null);
			}
		}


		private void updateGraphLine(Graphics graphics, FieldEnvironment field, FieldStats stats, Class<?> animalClass,
				int index, int width, int height) {
			int count = stats.getPopulationCount(field, animalClass);
			int y = scalePopulationToY(height, count);
			graphics.setColor(LIGHT_GRAY);
			graphics.drawLine(width - 2, y, width - 2, height);
			graphics.setColor(colors.get(animalClass));
			graphics.drawLine(width - 3, lastVal[index], width - 2, y);
			lastVal[index] = y;
		}


		private int scalePopulationToY(int height, int count) {
			int y = height - ((height * count) / yMax) - 1;
			while (y < 0) {
				scaleDown();
				y = height - ((height * count) / yMax) - 1;
			}
			return y;
		}
	}


	private int getTrackedClassCount() {
		return colors.size();
	}


	private void registerColors(SimulationSnapshot snapshot) {
		snapshot.getField().streamAnimals()
				.forEach(animal -> setColor(animal.getClass(), animal.getObjectColor(snapshot.getClimate())));
	}
}
