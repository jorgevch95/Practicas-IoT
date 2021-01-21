import gui.MainFrame;
import integration.IntegrationFactory;

public class Main {
    private final static String[] columnNames = {"x1", "x2","y"};
    private final static String host = "http://localhost:8080";
    private final static String trainUrl = "/train_data";
    private final static String fitUrl = "/fit";
    private final static String predictUrl = "/predict";

    public static void main(String[] args) {
        new MainFrame(columnNames, IntegrationFactory.getInstance().generaIntegration(host, trainUrl, fitUrl, predictUrl));
    }
}
