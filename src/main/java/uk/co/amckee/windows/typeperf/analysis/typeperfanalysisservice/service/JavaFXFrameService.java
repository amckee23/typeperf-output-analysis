package uk.co.amckee.windows.typeperf.analysis.typeperfanalysisservice.service;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;
import uk.co.amckee.windows.typeperf.analysis.typeperfanalysisservice.TypeperfAnalysisServiceApplication;
import uk.co.amckee.windows.typeperf.analysis.typeperfanalysisservice.model.GraphData;

@Component
public class JavaFXFrameService extends Application {

    private static final Logger logger = LoggerFactory.getLogger(JavaFXFrameService.class.getName());

    private static final String SOURCE_FILE_FLAG = "sourceFile";

    private String sourceFile;

    /**
     * Note due to issues with JavaFX Application and Spring wiring we must init any
     * services directly used by this class manually
     */
    private CSVLoaderService csvLoaderService;
    private ChartRenderService chartRenderService;

    /**
     * Init method to launch the graph creation logic
     *
     * @param args
     */
    public void createGraphs(String[] args) {
        launch(args);
    }

    /**
     * Starts the JavaFX application
     *
     * @param primaryStage The stage (UI View) to load
     */
    @Override
    public void start(Stage primaryStage) {
        validateFileInputParameter();
        initSpringContext();
        final GraphData graphData = csvLoaderService.readDataFromCSV(sourceFile);
        chartRenderService.drawCharts(graphData);
        Platform.exit();
    }

    /**
     * Initialise spring beans used by this service
     */
    private void initSpringContext() {
        AnnotationConfigApplicationContext context
                = new AnnotationConfigApplicationContext(TypeperfAnalysisServiceApplication.class);

        chartRenderService = (ChartRenderService) context.getBean("chartRenderServiceImpl");
        csvLoaderService = (CSVLoaderService) context.getBean("CSVLoaderServiceImpl");
    }

    private void validateFileInputParameter() {
        if (getParameters().getNamed().containsKey(SOURCE_FILE_FLAG)) {
            sourceFile = getParameters().getNamed().get(SOURCE_FILE_FLAG);
        } else {
            logger.error("No --sourceFile flag provided please specify the source data csv file");
            Platform.exit();
            System.exit(1);
        }
    }
}
