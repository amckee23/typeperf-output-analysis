package uk.co.amckee.windows.typeperf.analysis.typeperfanalysisservice;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.image.Image;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import uk.co.amckee.windows.typeperf.analysis.typeperfanalysisservice.service.CSVLoaderService;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@SpringBootApplication
public class TypeperfAnalysisServiceApplication extends Application {
    private static final Logger logger = LoggerFactory.getLogger(TypeperfAnalysisServiceApplication.class.getName());

    private static final String IMAGE_TYPE = "png";
    private static final String WORKING_DIR = System.getProperty("user.dir");
    private final int CHART_SIZE = 1200;

    private CSVLoaderService csvLoaderService = new CSVLoaderService();

    public static void main(String[] args) {
        logger.info("Starting app");
        SpringApplication.run(TypeperfAnalysisServiceApplication.class, args);
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        createChart(csvLoaderService.readDataFromCSV());
        logger.info("Exiting app");
        Platform.exit();
    }

    private NumberAxis getTimeAxis(final String axisName, final Long lowest, final Long highest) {
        final NumberAxis xAxis = new NumberAxis();
        xAxis.setLabel(axisName);
        xAxis.setAutoRanging(false);
        xAxis.setLowerBound(lowest);
        xAxis.setUpperBound(highest);
        xAxis.setTickLabelFormatter(generateFormatConverter());
        xAxis.setTickLabelRotation(90);
        xAxis.setTickUnit(600);
        xAxis.setTickLabelsVisible(true);
        return xAxis;
    }

    private void createChart(List<String[]> csvData) {
        //defining a series

        for (int i = 1; i < csvData.get(0).length; i++) {
            final String chartDataLabel = csvData.get(0)[i].replace('\\', ' ').replace('/', ' ');
            logger.info("Creating chart for {}", chartDataLabel);
            XYChart.Series series = new XYChart.Series();
            series.setName(chartDataLabel);
            Long lowest = Long.MAX_VALUE;
            Long highest = 0L;
            for (String[] strings : csvData) {
                if (strings[0].contains("(PDH-CSV 4.0)")) {
                    continue;
                }
                final Long xValue = getNumberFromDate(strings[0]);

                if (xValue > highest) {
                    highest = xValue;
                }

                if (xValue < lowest) {
                    lowest = xValue;
                }

                series.getData().add(new XYChart.Data<Number, Number>(getNumberFromDate(strings[0]), getNumberFromString(strings[i])));

            }

            // create a chart.
            final NumberAxis xAxis = getTimeAxis(csvData.get(0)[0], lowest, highest);
            final NumberAxis yAxis = new NumberAxis();
            yAxis.setLabel(chartDataLabel);
            //creating the chart
            Parent chart = generateLineChart(chartDataLabel, series, xAxis, yAxis);
            Image image = snapshot(chart);
            exportPng(SwingFXUtils.fromFXImage(image, null), new File(WORKING_DIR + "/images", chartDataLabel + ".png").getPath());
        }
    }

    private LineChart<Number, Number> generateLineChart(String title, XYChart.Series series, NumberAxis xAxis, NumberAxis yAxis) {
        final LineChart<Number, Number> lineChart =
                new LineChart<>(xAxis, yAxis);


        lineChart.setTitle(title);
        lineChart.getData().add(series);
        lineChart.setAnimated(false);
        lineChart.setCreateSymbols(false);
        lineChart.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        lineChart.setPrefSize(CHART_SIZE, CHART_SIZE);
        lineChart.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        return lineChart;
    }


    private StringConverter<Number> generateFormatConverter() {
        return new StringConverter<Number>() {
            @Override
            public String toString(Number object) {
                LocalDateTime dateTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(object.longValue()), ZoneId.systemDefault());
                return dateTime.getDayOfMonth() + "/" + dateTime.getMonthValue() + " " + dateTime.getHour() + ":" + dateTime.getMinute() + ":" + dateTime.getSecond();
            }

            @Override
            public Number fromString(String string) {
                return null;
            }
        };
    }

    private Number getNumberFromString(String input) {
        if (StringUtils.isBlank(input)) {
            return 0;
        }

        return Float.valueOf(input);
    }

    private Long getNumberFromDate(String input) {
        //10/29/2018 20:33:57.130
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss.SSS", Locale.ENGLISH);
        LocalDateTime date = LocalDateTime.parse(input, formatter);
        return date.toEpochSecond(ZoneOffset.MIN);

    }

    private Image snapshot(final Parent sourceNode) {
        // Note: if the source node is not in a scene, css styles will not
        // be applied during a snapshot which may result in incorrect rendering.
        final Scene snapshotScene = new Scene(sourceNode);

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
        }
    }

    private void createImageDirectoryIfNotExists() {
        String directoryName = "images";

        File directory = new File(directoryName);
        if (!directory.exists()) {
            directory.mkdir();
            // If you require it to make the entire directory path including parents,
            // use directory.mkdirs(); here instead.
        }

    }


}
