package uk.co.amckee.windows.typeperf.analysis.typeperfanalysisservice.model;

import java.util.List;
import java.util.Map;

public class GraphData {

    private List<String> timeValues;
    private Map<String, List<Number>> graphValues;

    public GraphData(final List<String> timeValues, final Map<String, List<Number>> graphValues) {
        this.timeValues = timeValues;
        this.graphValues = graphValues;
    }

    public List<String> getTimeValues() {
        return timeValues;
    }

    public void setTimeValues(List<String> timeValues) {
        this.timeValues = timeValues;
    }

    public Map<String, List<Number>> getGraphValues() {
        return graphValues;
    }

    public void setGraphValues(Map<String, List<Number>> graphValues) {
        this.graphValues = graphValues;
    }
}
