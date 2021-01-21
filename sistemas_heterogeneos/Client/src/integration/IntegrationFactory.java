package integration;

import integration.integration.Integration;

public abstract class IntegrationFactory {
    protected static IntegrationFactory instance;

    public static IntegrationFactory getInstance() {
        if (instance == null) instance = new IntegrationFactoryImp();
        return instance;
    }

    public abstract Integration generaIntegration(String host, String trainUrl, String fitUrl, String predictUrl);
}
