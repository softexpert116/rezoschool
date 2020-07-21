package com.ediattah.rezoschool.httpsModule;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;

public class RestClient {
    private static final String HTTPS_STRING = "https";
    /**
     * https://developers.google.com/maps/documentation/geocoding/
     * https://developers.google.com/url-shortener/v1/getting_started?csw=1#shorten
     */

    private RestClient(){
    }
    private static RestClient instance = null;
    public static RestClient getInstance(){
        if(instance == null) {
            instance = new RestClient();
        }
        return instance;
    }

    public String postRequest(String url, JSONObject jsonObject) {
        String responseString = "";
        HttpClient httpClient = HTTPUtils.getNewHttpClient(url.startsWith(HTTPS_STRING));
        HttpResponse response = null;
        InputStream in;
        URI newURI = URI.create(url);
        HttpPost postMethod = new HttpPost(newURI);

        try {
//            JSONObject postJSON = new JSONObject();
//            postJSON.put("longUrl", "http://www.google.com/");

            postMethod.setEntity(new StringEntity(jsonObject.toString(), HTTP.UTF_8));
            postMethod.setHeader("Content-Type", "application/json");
            response = httpClient.execute(postMethod);
            in = response.getEntity().getContent();
            responseString = convertStreamToString(in);
        } catch (Exception e) {}
        return responseString;
    }

    public static String convertStreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        is.close();
        return sb.toString();
    }
//
//    public String getRequest() {
//        String responseString = "";
//        HttpClient httpClient = HTTPUtils.getNewHttpClient(REST_SERVER_HTTPS_GET_URI.startsWith(HTTPS_STRING));
//        HttpResponse response = null;
//        InputStream in;
//        URI newURI = URI.create(REST_SERVER_HTTPS_GET_URI);
//        HttpGet getMethod = new HttpGet(newURI);
//        try {
//            response = httpClient.execute(getMethod);
//            in = response.getEntity().getContent();
//            responseString = convertStreamToString(in);
//        } catch (Exception e) {}
//        return responseString;
//    }
}
