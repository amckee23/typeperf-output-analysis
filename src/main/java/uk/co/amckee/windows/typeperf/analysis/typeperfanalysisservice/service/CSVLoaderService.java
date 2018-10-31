package uk.co.amckee.windows.typeperf.analysis.typeperfanalysisservice.service;

import com.opencsv.CSVReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.amckee.windows.typeperf.analysis.typeperfanalysisservice.TypeperfAnalysisServiceApplication;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class CSVLoaderService {

    private static final Logger logger = LoggerFactory.getLogger(TypeperfAnalysisServiceApplication.class.getName());

    public List<String[]> readDataFromCSV() {
        logger.info("Loading data");
        try {
            CSVReader reader = new CSVReader(new FileReader("data.csv"), ',', '"', '|');
            logger.info("Lines read" + reader.getLinesRead());
            List<String[]> lines = reader.readAll();
            logger.info("Lines read" + reader.getLinesRead());
            return lines;
        } catch (Exception e) {
            logger.error("Unable to load file ");
        }
        return new ArrayList<>();
    }
}
