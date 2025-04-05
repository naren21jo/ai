package com.E2ESeleniumAIPOC;

import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class GenerateSeleniumCode {
    private static final String required = "";
    private static final String required_url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=" + required;
    private static final OkHttpClient client = new OkHttpClient();
   
    // Method to generate Selenium code from manual test case
    public static String generateSeleniumCode(String prompt,String manualTestCase, String username_login, String password_login, String submit_login, String className) throws IOException {
        // Set up the request body
        JsonObject requestBody = new JsonObject();
        JsonArray contents = new JsonArray();
        JsonObject content = new JsonObject();
        JsonArray parts = new JsonArray();
        JsonObject part = new JsonObject();
       
        // Modify the request to include the necessary values for selenium code generation
        part.addProperty("text", prompt + manualTestCase);
        
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
