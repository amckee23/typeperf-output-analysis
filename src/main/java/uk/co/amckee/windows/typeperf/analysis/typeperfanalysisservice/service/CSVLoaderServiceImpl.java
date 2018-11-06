package uk.co.amckee.windows.typeperf.analysis.typeperfanalysisservice.service;

import com.opencsv.CSVReader;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import uk.co.amckee.windows.typeperf.analysis.typeperfanalysisservice.model.GraphData;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class CSVLoaderServiceImpl implements CSVLoaderService {

    private static final Logger logger = LoggerFactory.getLogger(CSVLoaderServiceImpl.class.getName());

    /**
     * Load graph data from a csv file
     *
     * @return The formatted Chart data
     */
    public GraphData readDataFromCSV(final String csvFileLocation) {
        logger.info("Loading data");
        try {
            final List<String> timeValues = new ArrayList<>();

            final Map<String, List<Number>> charts = new HashMap<>();
            final CSVReader reader = new CSVReader(new FileReader(csvFileLocation));
            final String[] headers = reader.readNext();
            final List<String> chartNames = getColumnHeadings(charts, headers);

            loadChartData(timeValues, charts, reader, chartNames);
            logger.info("Lines read " + reader.getLinesRead());

            return new GraphData(timeValues, charts);
        } catch (IOException e) {
            logger.error("Unable to load chart data from file exiting ", e);
        }
        System.exit(1);
        return null;
    }

    /**
     * Iterates over each row in the loaded csv and adds data to the chart data object
     *
     * @param timeValues The Time series data, always first column
     * @param charts     The chart data store
     * @param reader     The csv reader loading data
     * @param chartNames The column names to use
     * @throws IOException If the file cannot be read from an IOEX is thrown
     */
    private void loadChartData(List<String> timeValues, Map<String, List<Number>> charts, CSVReader reader, List<String> chartNames) throws IOException {
        String[] nextLine = reader.readNext();
        while (nextLine != null) {
            for (int i = 0; i < nextLine.length; i++) {
                if (i == 0) {
                    timeValues.add(nextLine[i]);
                } else if (StringUtils.isBlank(nextLine[i])) {
                    charts.get(chartNames.get(i - 1)).add(0);
                } else {
                    charts.get(chartNames.get(i - 1)).add(Float.valueOf(nextLine[i]));
                }
            }

            nextLine = reader.readNext();
        }
    }

    /**
     * Get the list of column headings from file
     *
     * @param charts  The chart data object to populate
     * @param headers The chart headings to use
     * @return The list of chart headers
     */
    private List<String> getColumnHeadings(Map<String, List<Number>> charts, String[] headers) {
        final List<String> chartNames = new ArrayList<>();
        for (int i = 1; i < headers.length; i++) {
            final String chartName = getChartName(headers[i]);
            charts.put(chartName, new ArrayList<>());
            chartNames.add(chartName);
        }
        return chartNames;
    }

    /**
     * Return a friendly chart name from the Typeperf output
     *
     * @param graphName The non friendly chart name
     * @return The friendly chart name
     */
    private String getChartName(String graphName) {
        return graphName.replace('\\', ' ').replace('/', ' ');
    }
}
