package views;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import controller.WeatherState;
import field.Block;
import field.Field;

/**
 * Frame holding the map of all the temperatures in each block.
 *
 */
public class TemperatureView extends JFrame {
    private Field field;
    private WeatherState weatherState;
    private FieldView fieldView;

    private String WEATHER_PREFIX;
    private JLabel weatherLabel;

    /**
     * Create the view.
     * @param rows how many rows
     * @param cols how many columns
     */
    public TemperatureView(int rows, int cols) {
        setTitle("Heatmap");
        fieldView = new FieldView(rows, cols);

        field = Field.getInstance();
        weatherState = WeatherState.getInstance();
        Container contents = getContentPane();

        WEATHER_PREFIX = "Current weather: ";
        weatherLabel = new JLabel(WEATHER_PREFIX);

        contents.add(weatherLabel, BorderLayout.PAGE_START);
        contents.add(fieldView, BorderLayout.CENTER);

        pack();

        // Adapted from
        // http://www.java2s.com/Tutorials/Java/Swing_How_to/JFrame/Position_on_Screen_Right_Bottom.htm
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice defaultScreen = ge.getDefaultScreenDevice();
        Rectangle rect = defaultScreen.getDefaultConfiguration().getBounds();
        int x = (int) rect.getMaxX() - this.getWidth();
        int y = (int) rect.getMaxY() - this.getHeight();

        setLocation(x, y-100);
        setVisible(true);
    }

    /**
     * Show the current status of the field.
     */
    public void showStatus() {
        if(!isVisible()) {
            setVisible(true);
        }

        weatherLabel.setText(WEATHER_PREFIX + weatherState.getWeather());

        fieldView.preparePaint(fieldView);

        for(int row = 0; row < field.getRows(); row++) {
            for(int col = 0; col < field.getCols(); col++) {
                Block b = field.getBlockAt(row, col);
                float hue = Math.abs(((b.getTemperature() + 5)/100f - 0.7f));
                fieldView.drawMark(col, row, new Color(Color.HSBtoRGB(hue, 1.0f, 1.0f)));
            }
        }

        fieldView.repaint();
    }
}
