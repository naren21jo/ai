package com.E2ESeleniumAIPOC;


import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import okhttp3.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;

public class GenerateXPath {


    
    private static final String required = ""; 
    
    private static final String required_url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=" + required;

    
    private static final OkHttpClient client = new OkHttpClient();
  
    public static String genXPath(String pageSource, String username_login, String password_login, String submit_login) throws IOException {
        // Set up the request body
        JsonObject requestBody = new JsonObject();
        JsonArray contents = new JsonArray();
        JsonObject content = new JsonObject();
        JsonArray parts = new JsonArray();
        JsonObject part = new JsonObject();        

        // Prepare the prompt text with the passed variables
        part.addProperty("text", "In given web Page Source, generate the Accurate and unique Xpath for the username take the variable name from "+username_login+" , password take the variable name from "+password_login+" and submit button field take the variable name from "+submit_login+". Dont generate anything apart from xpaths and variables and give output in this format variablename='xpath' :\n" + pageSource);
        parts.add(part);
        content.add("parts", parts);
        contents.add(content);
        requestBody.add("contents", contents);

        // Create a POST request with the API key
        RequestBody body = RequestBody.create(requestBody.toString(), MediaType.get("application/json"));
        Request request = new Request.Builder()
                .url(required_url)
                .post(body)
                .build();

        // Send the request and receive the response
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            // Extract the generated code from the response
            JsonObject jsonResponse = new Gson().fromJson(response.body().string(), JsonObject.class);
            JsonArray candidates = jsonResponse.getAsJsonArray("candidates");
            if (candidates != null && candidates.size() > 0) {
                JsonObject candidate = candidates.get(0).getAsJsonObject();
                JsonArray contentArray = candidate.getAsJsonObject("content").getAsJsonArray("parts");
                if (contentArray != null && contentArray.size() > 0) {
                    return contentArray.get(0).getAsJsonObject().get("text").getAsString().trim();
                }
            }
            return null; // Return null if the expected data is not found.
        }
    }

}
