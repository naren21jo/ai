package com.E2ESeleniumAIPOC;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;

public class LoginPage {

    public static void main(String[] args) {

        System.setProperty("webdriver.edge.driver", "drivers//msedgedriver.exe");
        EdgeOptions options = new EdgeOptions();
        options.addArguments("--remote-allow-origins=*");
        WebDriver driver = new EdgeDriver(options);
        driver.manage().window().maximize();
        driver.get("https://practicetestautomation.com/practice-test-login/");

        driver.findElement(By.id("username")).sendKeys("student");
        driver.findElement(By.id("password")).sendKeys("Password123");
        driver.findElement(By.id("submit")).click();

        String actualText = driver.findElement(By.xpath("//h1[@class='post-title']")).getText();
        String expectedText = "Logged In Successfully";

        if (actualText.equals(expectedText)) {
            System.out.println("Login successful. Home page verified.");
        } else {
            System.out.println("Login failed or home page verification failed.");
        }

        driver.quit();
    }
}