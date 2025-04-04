package com.E2ESeleniumAIPOC;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.json.JSONObject;
import org.json.JSONTokener; 
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import java.io.FileReader;
import java.time.Duration;

public class ReadingWebElements {

    private static final String JSON_FILE_PATH = "resources/LoginWebElements.json";
    private static JSONObject webElementsData;

    // Method to load JSON data from the file
    public static void loadWebElementData() throws IOException {
        String jsonContent = new String(Files.readAllBytes(Paths.get(JSON_FILE_PATH)));
        webElementsData = new JSONObject(jsonContent);

       // System.out.println("WebElements Data loaded successfully.");
    }

    public static String getStringData(String key) {
        if (webElementsData == null) {
            throw new IllegalStateException("Test data not loaded. Call loadWebElementData() first.");
        }
        return webElementsData.getString(key);
    }

    // Helper method to get nested JSON object data
    public static JSONObject getObjectData(String key) {
         if (webElementsData == null) {
            throw new IllegalStateException("Test data not loaded. Call loadWebElementData() first.");
        }
        return webElementsData.getJSONObject(key);
    }

}
