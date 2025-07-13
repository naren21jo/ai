package com.E2ESeleniumAIPOC;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.edge.EdgeDriver;

public class LoginPage {

    public static void main(String[] args) {
        System.setProperty("webdriver.edge.driver", "drivers//msedgedriver.exe");
        WebDriver driver = new EdgeDriver();
        driver.manage().window().maximize();
        driver.get("https://practicetestautomation.com/practice-test-login/");

        WebElement usernameField = driver.findElement(By.id("username"));
        usernameField.sendKeys("student");

        WebElement passwordField = driver.findElement(By.id("password"));
        passwordField.sendKeys("Password123");

        WebElement submitButton = driver.findElement(By.id("submit"));
        submitButton.click();

        WebElement homepageText = driver.findElement(By.xpath("//h1[contains(text(),'Logged In Successfully')]"));
        homepageText.isDisplayed();

        driver.quit();
    }
}