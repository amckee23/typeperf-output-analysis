package uk.co.amckee.windows.typeperf.analysis.typeperfanalysisservice.service;

import uk.co.amckee.windows.typeperf.analysis.typeperfanalysisservice.model.GraphData;

public interface CSVLoaderService {

    GraphData readDataFromCSV(final String csvFileLocation);

}
