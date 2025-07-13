package com.E2ESeleniumAIPOC;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.edge.EdgeDriver;

public class LoginPage {

    public static void main(String[] args) {
        System.setProperty("webdriver.edge.driver", "drivers//msedgedriver.exe");
        WebDriver driver = new EdgeDriver();
        driver.manage().window().maximize();
        driver.get("https://practicetestautomation.com/practice-test-login/");

        driver.findElement(By.id("username")).sendKeys("student");
        driver.findElement(By.id("password")).sendKeys("Password123");
        driver.findElement(By.id("submit")).click();

        String actualText = driver.findElement(By.xpath("//h1[@class='post-title']")).getText();

        if (actualText.equals("Logged In Successfully")) {
            System.out.println("Login successful!");
        } else {
            System.out.println("Login failed.");
        }

        driver.quit();
    }
}