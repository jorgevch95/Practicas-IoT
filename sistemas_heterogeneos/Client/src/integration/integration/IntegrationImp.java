package integration.integration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

public class IntegrationImp implements Integration {
    private String host;
    private String trainUrl;
    private String fitUrl;
    private String predictUrl;

    public IntegrationImp(String host, String trainUrl, String fitUrl, String predictUrl) {
        this.host = host;
        this.trainUrl = trainUrl;
        this.fitUrl = fitUrl;
        this.predictUrl = predictUrl;
    }

    @Override
    public String sendTrainData(Integer[][] data, String[] columnNames) {
        try {
            for (int i = 0; i < data.length; i++) {
                StringBuilder urlString = new StringBuilder(host + trainUrl + "?");
                for (int j = 0; j < data[i].length; j++) {
                    if (data[i].length == columnNames.length) {
                        urlString.append(columnNames[j]).append("=").append(data[i][j]);
                        if (j != data[i].length - 1) {
                            urlString.append("&");
                        }
                    } else {
                        throw new Exception("Data length don't match Column length");
                    }
                }
                System.out.print("Send request to: " + urlString.toString());
                System.out.println("\tReceived:" + this.sendRequest(urlString.toString()));
            }
            return this.sendRequest(host + fitUrl);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    private String sendRequest(String urlString) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setConnectTimeout(5000);
            con.setReadTimeout(5000);
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            return in.readLine();
        } catch (MalformedURLException e) {
            System.out.println("URL format error.");
        } catch (IOException e) {
            System.out.println("Open connection error.");
        }
        return null;
    }

    @Override
    public String predict(Integer[] data, String[] columnNames) {
        columnNames = Arrays.copyOfRange(columnNames,0,columnNames.length-1);
        try{
            StringBuilder urlString = new StringBuilder(host + predictUrl + "?");
            for (int i = 0; i < data.length; i++) {
                if (data.length == columnNames.length) {
                    urlString.append(columnNames[i]).append("=").append(data[i]);
                    if (i != data.length - 1) {
                        urlString.append("&");
                    }
                } else {
                    throw new Exception("Data length don't match Column length");
                }
            }
            System.out.println("Send request to: " + urlString.toString());
            return this.sendRequest(urlString.toString());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }
}
