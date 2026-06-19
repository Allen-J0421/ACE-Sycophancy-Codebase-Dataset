import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;


public class GraphView extends JFrame {
	private static final Color LIGHT_GRAY = new Color(0, 0, 0, 40);

	private final GraphPanel graph;

	private final JLabel stepLabel;

	private final JLabel countLabel;

	private final Map<Class<?>, Color> colors;

	private final FieldStats stats;


	public GraphView(int width, int height, int startMax) {
		super("Graph View");
		stats = new FieldStats();
		colors = new LinkedHashMap<>();
		graph = new GraphPanel(width, height, startMax);
		stepLabel = new JLabel("");
		countLabel = new JLabel(" ");
		configureFrame();
	}


	public void setColor(Class<?> animalClass, Color color) {
		colors.put(animalClass, color);
	}


	public void showStatus(int step, Field field) {
		graph.update(step, field, stats);
	}


	public boolean isViable(Field field) {
		return stats.isViable(field);
	}


	public void reset() {
		stats.reset();
		graph.newRun();
	}


	private void configureFrame() {
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

		Container contentPane = getContentPane();

		contentPane.add(graph, BorderLayout.CENTER);

		JPanel bottom = new JPanel();
		bottom.add(new JLabel("Step:"));
		bottom.add(stepLabel);
		bottom.add(countLabel);
		contentPane.add(bottom, BorderLayout.SOUTH);

		pack();
		setLocation(20, 600);

		setVisible(true);
	}


	private class GraphPanel extends JComponent {
		private static final double SCALE_FACTOR = 0.8;


		private BufferedImage graphImage;
		private int[] lastVal;
		private int yMax;


		public GraphPanel(int width, int height, int startMax) {
			graphImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			clearImage();
			lastVal = new int[colors.size()];
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
			lastVal = new int[colors.size()];
			Arrays.fill(lastVal, height);
			repaint();
		}


		public void update(int step, Field field, FieldStats stats) {

			Graphics g = graphImage.getGraphics();

			int height = graphImage.getHeight();
			int width = graphImage.getWidth();


			g.copyArea(1, 0, width - 1, height, -1, 0);

			stats.reset();
			ensureSeriesCount(height);

			int i = 0;
			for (Class<?> nextClass : colors.keySet()) {
				int count = stats.getPopulationCount(field, nextClass);


				int y = height - ((height * count) / yMax) - 1;
				while (y < 0) {
					scaleDown();
					y = height - ((height * count) / yMax) - 1;
				}
				g.setColor(LIGHT_GRAY);
				g.drawLine(width - 2, y, width - 2, height);
				g.setColor(colors.get(nextClass));
				g.drawLine(width - 3, lastVal[i], width - 2, y);
				lastVal[i] = y;
				i++;
			}

			repaint();

			stepLabel.setText("" + step);
			countLabel.setText(stats.getPopulationDetails(field));

		}


		private void scaleDown() {
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

			for (int i = 0; i < lastVal.length; i++) {
				lastVal[i] = oldTop + (int) (lastVal[i] * SCALE_FACTOR);
			}

			repaint();
		}


		private void ensureSeriesCount(int defaultValue) {
			if (lastVal.length == colors.size()) {
				return;
			}

			int[] updated = new int[colors.size()];
			Arrays.fill(updated, defaultValue);
			System.arraycopy(lastVal, 0, updated, 0, Math.min(lastVal.length, updated.length));
			lastVal = updated;
		}


		private void clearImage() {
			Graphics g = graphImage.getGraphics();
			g.setColor(Color.WHITE);
			g.fillRect(0, 0, graphImage.getWidth(), graphImage.getHeight());
			repaint();
		}


		@Override
		public Dimension getPreferredSize() {
			return new Dimension(graphImage.getWidth(), graphImage.getHeight());
		}


		@Override
		public boolean isOpaque() {
			return true;
		}


		@Override
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			if (graphImage != null) {
				g.drawImage(graphImage, 0, 0, null);
			}
		}
	}
}
