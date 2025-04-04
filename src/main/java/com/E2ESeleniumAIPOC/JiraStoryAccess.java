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
	                String jira_get_request_url = JsonDataReading.getObjectData("jira_details").getString("jira_get_requrest_url");
	                String taskid = JsonDataReading.getObjectData("jira_details").getString("taskid");
	                String jira_username = JsonDataReading.getObjectData("jira_details").getString("jira_username");
	                String jira_password = JsonDataReading.getObjectData("jira_details").getString("jira_password");
	                
        	  		System.out.println("############# Getting Manual steps from JIRA board ###############");
                    String jiraUrl = jira_get_request_url + taskid + "?fields=description";
                    
                    // Set up basic authentication
                    PreemptiveBasicAuthScheme basicAuth = new PreemptiveBasicAuthScheme();
                    basicAuth.setUserName(jira_username);
                    basicAuth.setPassword(jira_password);
                    RestAssured.authentication = basicAuth;
                    Response response = RestAssured.given().get(jiraUrl);
                    // Extract the response details
                    JsonPath jsonPath = response.jsonPath();
                    // Get the 'description' field from the JSON response
                    String description = jsonPath.getString("fields.description");
                    // Print the description
                    System.out.println(description);
                    return description;

          }
}