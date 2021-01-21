package integration.integration;

public interface Integration {
    String sendTrainData(Integer[][] data, String[] columnNames);
    String predict(Integer[] data, String[] columnNames);
}
