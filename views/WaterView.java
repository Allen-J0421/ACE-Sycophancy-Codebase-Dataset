package views;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import controller.WeatherState;
import field.Block;
import field.Field;


/**
 * Frame containing the view of the water map.
 *
 */
public class WaterView extends JFrame {
    private Field field;
    private FieldView fieldView;
    private WeatherState weatherState;

    private String WEATHER_PREFIX = "Current weather: ";
    private JLabel weatherLabel;
    
    private int MAX_WATER_LEVEL;

    private Color x, y;

    /**
     * Create the view.
     * @param rows how many rows
     * @param cols how many columns
     * @param MAX_WATER_LEVEL the max water level in each block
     */
    public WaterView(int rows, int cols, int MAX_WATER_LEVEL) {
        setTitle("Water map");
        fieldView = new FieldView(rows, cols);
        field = Field.getInstance();
        weatherState = WeatherState.getInstance();
        
        this.MAX_WATER_LEVEL = MAX_WATER_LEVEL;
        
        // https://html-color.codes/css/lightskyblue

        x = new Color(123,201,249);
        y = new Color(40,167,246);
        
        Container contents = getContentPane();

        weatherLabel = new JLabel(WEATHER_PREFIX);

        contents.add(weatherLabel, BorderLayout.PAGE_START);
        contents.add(fieldView, BorderLayout.CENTER);

        pack();

        setLocationRelativeTo(null);
        setVisible(true);
    }

    /**
     * Compute the Colour from x to y using interpolation.
     * Adapted from: https://stackoverflow.com/a/21010385
     * @param fraction the proportion of the way from x to y.
     * @return the new interpolated colour.
     */
    private Color computeColor(double fraction) {
        int r = (int)((y.getRed() - x.getRed()) * fraction + x.getRed());
        int b = (int)((y.getBlue() - x.getBlue()) * fraction + x.getBlue());
        int g = (int)((y.getGreen() - x.getGreen()) * fraction + x.getGreen());

        return new Color(r, g, b);
    }

    /**
     * Show the current status of the field.
     */
    public void showStatus()
    {
        int step = field.getStep();
        if(!isVisible()) {
            setVisible(true);
        }

        weatherLabel.setText(WEATHER_PREFIX + weatherState.getWeather());

        fieldView.preparePaint(fieldView);

        for(int row = 0; row < field.getRows(); row++) {
            for(int col = 0; col < field.getCols(); col++) {
                Block b = field.getBlockAt(row, col);
                int waterLevel = b.getWaterLevel();
                
                // https://stackoverflow.com/questions/13488957/interpolate-from-one-color-to-another

                double fraction = waterLevel/MAX_WATER_LEVEL;
                Color color = computeColor(fraction);

                if (waterLevel == 0) {
                    color = Color.white;
                }

                fieldView.drawMark(col, row, color);
            }
        }

        fieldView.repaint();
    }
}
