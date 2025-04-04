package com.E2ESeleniumAIPOC;

import java.io.IOException;
import java.util.Scanner;

import io.restassured.RestAssured;
import io.restassured.authentication.PreemptiveBasicAuthScheme;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

public class JiraStoryAccess {
	
          public static String main(String args[]) throws IOException {

     
        	  		JsonDataReading.loadTestData();
	                String j_get_request_url = JsonDataReading.getObjectData("j_details").getString("j_get_request_url");
	                String taskid = JsonDataReading.getObjectData("j_details").getString("taskid");
	                String j_u = JsonDataReading.getObjectData("j_details").getString("j_u");
	                String j_p = JsonDataReading.getObjectData("j_details").getString("j_p");
	                
        	  		System.out.println("############# Getting Manual steps from JIRA board ###############");
                    String jUrl = j_get_request_url + taskid + "?fields=description";
                    
                    // Set up basic authentication
                    PreemptiveBasicAuthScheme basicAuth = new PreemptiveBasicAuthScheme();
                    basicAuth.setUserName(j_u);
                    basicAuth.setPassword(j_p);
                    RestAssured.authentication = basicAuth;
                    Response response = RestAssured.given().get(jUrl);
                    // Extract the response details
                    JsonPath jsonPath = response.jsonPath();
                    // Get the 'description' field from the JSON response
                    String description = jsonPath.getString("fields.description");
                    // Print the description
                    System.out.println(description);
                    return description;

          }
}