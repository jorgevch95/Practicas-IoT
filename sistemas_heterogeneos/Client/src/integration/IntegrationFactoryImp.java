package integration;

import integration.integration.Integration;
import integration.integration.IntegrationImp;

public class IntegrationFactoryImp extends IntegrationFactory {
    @Override
    public Integration generaIntegration(String host, String trainUrl, String fitUrl, String predictUrl) {
        return new IntegrationImp(host, trainUrl, fitUrl, predictUrl);
    }
}
