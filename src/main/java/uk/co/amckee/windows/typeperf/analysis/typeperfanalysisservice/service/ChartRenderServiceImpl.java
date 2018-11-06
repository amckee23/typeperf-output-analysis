package uk.co.amckee.windows.typeperf.analysis.typeperfanalysisservice.service;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.image.Image;
import javafx.scene.layout.Region;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import uk.co.amckee.windows.typeperf.analysis.typeperfanalysisservice.model.GraphData;
import uk.co.amckee.windows.typeperf.analysis.typeperfanalysisservice.util.DateToStringConverter;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import static uk.co.amckee.windows.typeperf.analysis.typeperfanalysisservice.util.DateTimeUtils.getDateFromNumber;
import static uk.co.amckee.windows.typeperf.analysis.typeperfanalysisservice.util.DateTimeUtils.getNumberFromStringDate;

@Component
public class ChartRenderServiceImpl implements ChartRenderService {

    private static final Logger logger = LoggerFactory.getLogger(ChartRenderService.class.getName());

    //TODO These consts should be configurable
    private static final String IMAGE_TYPE = "png";
    private static final String WORKING_DIR = System.getProperty("user.dir");
    private static final int CHART_SIZE = 1200;
    private static final String X_AXIS_TITLE = "Time";
    private static final String OUTPUT_DIRECTORY = "images";

    /**
     * Iterates over each provided chart series in graph data and draws the chart to file
     *
     * @param graphData The graph data to render
     */
    public void drawCharts(final GraphData graphData) {
        /*
            First we draw the X Axis Once (as it should remain the same for each graph.
            It should be noted while this adds an additional for loop over the entire
            X Series it is marginally more performant when rendering multiple graphs
            to avoid the overhead of multiple calculations.
        */
        final NumberAxis xAxis = getFormattedXAxis(graphData);

        //Iterate over each graph and draw it to file
        for (String graphName : graphData.getGraphValues().keySet()) {
            final NumberAxis yAxis = new NumberAxis();
            yAxis.setLabel(graphName);
            logger.info("Creating Chart: {}", yAxis.getLabel());
            drawChart(graphData.getTimeValues(), graphData.getGraphValues().get(graphName), xAxis, yAxis);
        }
    }

    private NumberAxis getFormattedXAxis(GraphData graphData) {
        Long lowest = Long.MAX_VALUE;
        Long highest = 0L;
        for (int i = 0; i < graphData.getTimeValues().size(); i++) {

            final Long xValue = getNumberFromStringDate(graphData.getTimeValues().get(i));

            if (xValue > highest) {
                highest = xValue;
            }

            if (xValue < lowest) {
                lowest = xValue;
            }
        }
        logger.info("Earliest Value {}", getDateFromNumber(lowest));
        logger.info("Latest Value {}", getDateFromNumber(highest));
        return getTimeAxis(lowest, highest);
    }

    private void drawChart(final List<String> xAxisValues, final List<Number> yAxisValues, final NumberAxis xAxis, final NumberAxis yAxis) {

        final XYChart.Series series = new XYChart.Series();
        series.setName(xAxis.getLabel());

        for (int i = 0; i < xAxisValues.size(); i++) {
            series.getData().add(new XYChart.Data<Number, Number>(getNumberFromStringDate(xAxisValues.get(i)), yAxisValues.get(i)));

        }

        final Parent chart = generateLineChart(series, xAxis, yAxis);
        final Image image = snapshot(chart);
        exportPng(SwingFXUtils.fromFXImage(image, null), new File(WORKING_DIR + "/" + OUTPUT_DIRECTORY, yAxis.getLabel() + "." + IMAGE_TYPE).getPath());
    }

    private LineChart<Number, Number> generateLineChart(XYChart.Series series, NumberAxis xAxis, NumberAxis yAxis) {
        final LineChart<Number, Number> lineChart =
                new LineChart<>(xAxis, yAxis);


        lineChart.setTitle(yAxis.getLabel());
        lineChart.getData().add(series);
        lineChart.setAnimated(false);
        lineChart.setCreateSymbols(false);
        lineChart.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        lineChart.setPrefSize(CHART_SIZE, CHART_SIZE);
        lineChart.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        return lineChart;
    }


    private NumberAxis getTimeAxis(final Long lowest, final Long highest) {
        final NumberAxis xAxis = new NumberAxis();
        xAxis.setLabel(X_AXIS_TITLE);
        xAxis.setAutoRanging(false);
        xAxis.setLowerBound(lowest);
        xAxis.setUpperBound(highest);
        xAxis.setTickLabelFormatter(new DateToStringConverter());
        xAxis.setTickLabelRotation(90);
        xAxis.setTickUnit(600);
        xAxis.setTickLabelsVisible(true);
        return xAxis;
    }

    private Image snapshot(final Parent sourceNode) {
        // Note: if the source node is not in a scene, css styles will not
        // be applied during a snapshot which may result in incorrect rendering.
        new Scene(sourceNode);
        return sourceNode.snapshot(
                new SnapshotParameters(),
                null
        );
    }

    private void exportPng(BufferedImage image, String filename) {
        createImageDirectoryIfNotExists();
        try {
            ImageIO.write(image, IMAGE_TYPE, new File(filename));

        } catch (IOException ex) {
            logger.error("Unable to write chart: {}", filename);
        }
    }

    private void createImageDirectoryIfNotExists() {

        File directory = new File(OUTPUT_DIRECTORY);
        if (!directory.exists()) {
            directory.mkdir();
        }

    }
}
