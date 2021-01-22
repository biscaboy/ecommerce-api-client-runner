package com.davidjdickinson.udacity.ecommerce.client;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Random;

public class ApiRunner extends Thread {

    private static Logger logger = LoggerFactory.getLogger(ApiRunner.class);

    private DefaultHttpClient httpClient;
    private String token;
    private String username;
    private String hostUrl = "http://localhost:8080";

    private static final String CREATE_USER_URI = "/api/user/create";
    private static final String LOGIN_USER_URI  = "/login";

    private String createUserJson;
    private String loginUserJson;

    private String [][] apiRequests;


    public ApiRunner (String hostUrl, String username) {
        this.hostUrl = hostUrl;
        this.username = username;
    }

    public void run() {
        logger.info("Started runner for user '" + username + "'");
        load();

        try {
            createUser(CREATE_USER_URI, createUserJson);
            token = getAuthenticationToken(LOGIN_USER_URI, loginUserJson);

            // make 20 random request on the API for this user
            int count = 0;
            int i = 0;
            Random random = new Random();
            while (count < 20) {
                execApi(apiRequests[i][0], apiRequests[i][1], apiRequests[i][2]);
                i = random.nextInt(8);
                Thread.sleep((random.nextInt(10) + 5) * 500);
                count++;
            }
        }
        catch (IOException | InterruptedException e) {
            logger.info("Fatal error: " + e.getMessage());
        }
        finally{
            httpClient.getConnectionManager().shutdown();
        }
    }

    private void execApi(String method, String uri, String json) throws IOException {
        httpClient = new DefaultHttpClient();
        HttpResponse response = (method.equals("POST")) ?
                postApiCall(uri, json) : getApiCall(uri);

        logger.info("Requested: " + uri );
        logger.info("Response Code: " + response.getStatusLine().getStatusCode());

        BufferedReader br = new BufferedReader(
                new InputStreamReader((response.getEntity().getContent())));
        String output = br.readLine();
        if (output != null) {
            while (output != null) {
                logger.info("Response Body: " + output);
                output = br.readLine();
            }
        }
        httpClient.getConnectionManager().shutdown();
    }

    private void createUser(String createUri, String createUserJson) throws IOException{
        httpClient = new DefaultHttpClient();
        HttpResponse response = postApiCall(createUri, createUserJson);

        String msg = ".";
        if (response.getStatusLine().getStatusCode() == 400) {
            msg = ". User exists.";
        }

        logger.info("User Create Status: " + response.getStatusLine().getStatusCode() + msg);
        httpClient.getConnectionManager().shutdown();
    }

    private String getAuthenticationToken(String loginUri, String loginJson) throws IOException {
        httpClient = new DefaultHttpClient();
        HttpResponse response = postApiCall(loginUri, loginJson);
        if (response.getStatusLine().getStatusCode() != 200) {
            throw new RuntimeException("Failed : HTTP error code : "
                    + response.getStatusLine().getStatusCode());
        }
        Header[] headers = response.getHeaders("Authorization");
        token = headers[0].toString().substring("Authorization: Bearer ".length());
        logger.info("Token: " + token);
        httpClient.getConnectionManager().shutdown();
        return token;
    }

    private HttpResponse postApiCall(String url, String json) throws IOException {
        HttpResponse response = null;
        HttpPost postRequest = new HttpPost(hostUrl + url);
        postRequest.setHeader("content-type", "application/json");
        if (token != null) {
            postRequest.setHeader("Authorization", "Bearer " + token);
        }
        if (json != null) {
            StringEntity input = new StringEntity(json);
            input.setContentType("application/json");
            postRequest.setEntity(input);
        }
        response = httpClient.execute(postRequest);
        return response;
    }

    private HttpResponse getApiCall(String url) throws IOException {
        HttpResponse response = null;
        HttpGet getRequest = new HttpGet(hostUrl + url);
        getRequest.addHeader("accept", "application/json");
        getRequest.setHeader("content-type", "application/json");
        if (token != null) {
            getRequest.setHeader("Authorization", "Bearer " + token);
        }

        response = httpClient.execute(getRequest);
        return response;
    }

    private void load() {
        createUserJson = "{\"username\":\"" + username + "\"," +
                "\"password\":\"123!@#asdASD\"," +
                "\"confirmPassword\":\"123!@#asdASD\"}";
        loginUserJson = "{\"username\":\"" + username + "\"," +
                "\"password\":\"123!@#asdASD\"}";
        String[][] array = {
                { "POST", "/api/cart/addToCart", "{\"username\" : \"" + username + "\",\"itemId\": 1,\"quantity\": 1 }" },
                { "POST", "/api/cart/removeFromCart", "{\"username\" : \"" + username + "\",\"itemId\": 1,\"quantity\": 1 }" },
                { "GET", "/api/item/1", null },
                { "GET", "/api/item/name?name=Round+Widget", null },
                { "POST", "/api/order/submit/" + username, null },
                { "GET", "/api/order/history/" + username, null },
                { "GET", "/api/user/id/1", null },
                { "GET", "/api/user/" + username, null },
                { "GET", "/api/user/list", null }
        };
        apiRequests = (array);
    }
}

