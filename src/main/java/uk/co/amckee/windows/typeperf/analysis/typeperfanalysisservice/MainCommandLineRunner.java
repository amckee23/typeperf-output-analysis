package uk.co.amckee.windows.typeperf.analysis.typeperfanalysisservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import uk.co.amckee.windows.typeperf.analysis.typeperfanalysisservice.service.JavaFXFrameService;

@Component
public class MainCommandLineRunner implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(MainCommandLineRunner.class.getName());

    @Autowired
    private JavaFXFrameService javaFXFrameService;


    @Override
    public void run(String... args) {
        logger.info("Starting graph creation loading JAVAFX Service");
        javaFXFrameService.createGraphs(args);
    }
}
