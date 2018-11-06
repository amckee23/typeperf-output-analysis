package uk.co.amckee.windows.typeperf.analysis.typeperfanalysisservice.service;

import org.junit.Test;
import uk.co.amckee.windows.typeperf.analysis.typeperfanalysisservice.model.GraphData;

import static java.lang.Float.valueOf;
import static org.junit.Assert.assertEquals;

public class CSVLoaderServiceImplTest {

    private CSVLoaderServiceImpl csvLoaderService = new CSVLoaderServiceImpl();

    @Test
    public void test_successful_csv_file_load() {
        final GraphData graphData = csvLoaderService.readDataFromCSV("./src/test/resources/testdata.csv");

        assertEquals(1, graphData.getGraphValues().size());
        assertEquals(2, graphData.getTimeValues().size());
        assertEquals(0, graphData.getGraphValues().get("test 1").get(0));
        assertEquals(valueOf("15411.200000000001"), graphData.getGraphValues().get("test 1").get(1));
    }
}
