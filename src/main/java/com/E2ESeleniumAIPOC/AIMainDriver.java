package com.E2ESeleniumAIPOC;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import okhttp3.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;

public  class AIMainDriver {


    // Main function to test the implementation
    public static void main(String[] args) throws IOException, IllegalAccessException, InvocationTargetException {

        JsonDataReading.loadTestData();
        String web_url = JsonDataReading.getObjectData("web_details").getString("web_url");

        ReadingWebElements.loadWebElementData();
        String username_login = ReadingWebElements.getObjectData("webelements").getString("username_login");
        String password_login = ReadingWebElements.getObjectData("webelements").getString("password_login");
        String submit_login = ReadingWebElements.getObjectData("webelements").getString("submit_login");
        String className = ReadingWebElements.getObjectData("classNames").getString("className");
        String prompt = ReadingWebElements.getObjectData("aiprompt").getString("prompt");
        String packageName = "com.E2ESeleniumAIPOC"; // Define the package name

        System.setProperty("webdriver.edge.driver", "drivers//msedgedriver.exe");
        EdgeOptions options = new EdgeOptions();
        //options.addArguments("headless"); 
        WebDriver driver = new EdgeDriver();
        driver.get(web_url);

        String pageSource = driver.getPageSource();
        driver.quit();
        try {
            
            String welements = GenerateXPath.genXPath(pageSource, username_login, password_login, submit_login);
            System.out.println("############# Generating XPaths from PageSource ###############");
            System.out.println(welements);

            String manualTestCase = JiraStoryAccess.main(args); // Assuming this returns the test steps string
            try {
                // Assuming GenerateSeleniumCode is static or create an instance
                String seleniumCode = GenerateSeleniumCode.generateSeleniumCode(prompt,manualTestCase, username_login, password_login, submit_login, className);
                System.out.println("############# Generating Selenium Automation Code using Manual steps ############");
                System.out.println(seleniumCode);

                // Define source file path
                String sourceFilePath = "src/main/java/" + packageName.replace('.', '/') + "/" + className + ".java";
                File outputFile = new File(sourceFilePath);

                // Ensure parent directories exist
                outputFile.getParentFile().mkdirs();

                // Write the generated code to the file
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
                    writer.write(seleniumCode);
                }
                System.out.println("Code has been generated and saved to: " + outputFile.getAbsolutePath());

                // --- START: Code to Compile and Run the Generated Class ---

                System.out.println("\n############# Compiling Generated Code ###############");

                File sourceFile = new File(sourceFilePath);
                if (!sourceFile.exists()) {
                    System.err.println("Error: Source file not found: " + sourceFile.getAbsolutePath());
                    return;
                }

                // Prepare output directory for .class files
                Path outputDir = Paths.get("target/generated-classes");
                Files.createDirectories(outputDir); // Create directory if it doesn't exist

                JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
                if (compiler == null) {
                    System.err.println("Error: No Java compiler found. Make sure you are running with a JDK, not just a JRE.");
                    System.err.println("JAVA_HOME=" + System.getProperty("java.home"));
                    return;
                }

                DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
                StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, null, null);

                // Set compiler options (classpath, output directory)
                List<String> optionList = new ArrayList<>();
                optionList.add("-classpath");
                // Include current classpath + output directory
                String currentClasspath = System.getProperty("java.class.path");
                optionList.add(outputDir.toAbsolutePath().toString() + File.pathSeparator + currentClasspath);
                optionList.add("-d"); // Option for output directory
                optionList.add(outputDir.toAbsolutePath().toString()); // Specify output directory

                System.out.println("Compiler Classpath: " + optionList.get(1));
                System.out.println("Compiler Output Directory: " + optionList.get(3));


                Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjectsFromFiles(Arrays.asList(sourceFile));

                JavaCompiler.CompilationTask task = compiler.getTask(
                        new PrintWriter(System.err), // Use System.err for compiler messages
                        fileManager,
                        diagnostics,
                        optionList,
                        null,
                        compilationUnits
                );

                boolean success = task.call();
                fileManager.close();

                if (success) {
                    System.out.println("Compilation successful.");
                    System.out.println("\n############# Running Generated Code ###############");

                    try {
                        // Create a URLClassLoader to load the compiled class and its dependencies
                        List<URL> urls = new ArrayList<>();
                        // Add the output directory
                        urls.add(outputDir.toUri().toURL());
                        // Add jars from the current classpath
                        String[] classpathEntries = currentClasspath.split(File.pathSeparator);
                        for (String entry : classpathEntries) {
                            try {
                                urls.add(new File(entry).toURI().toURL());
                            } catch (MalformedURLException | SecurityException e) {
                                System.err.println("Warning: Skipping classpath entry: " + entry + " (" + e.getMessage() + ")");
                            }
                        }

                        URLClassLoader classLoader = new URLClassLoader(urls.toArray(new URL[0]), AIMainDriver.class.getClassLoader());

                        // Load the generated class
                        String fullyQualifiedClassName = packageName + "." + className;
                        System.out.println("Loading class: " + fullyQualifiedClassName);
                        Class<?> loadedClass = classLoader.loadClass(fullyQualifiedClassName);

                        // Find the main method
                        Method mainMethod = loadedClass.getMethod("main", String[].class);

                        // Invoke the main method (statically)
                        System.out.println("Invoking " + fullyQualifiedClassName + ".main()...");
                        Object[] methodArgs = { new String[0] }; // Pass empty args array to main
                        mainMethod.invoke(null, methodArgs); // First arg is null for static methods

                        System.out.println("\n############# Generated Code Execution Finished ###############");
                        classLoader.close(); // Close classloader if possible (available in Java 7+)


                    } catch (ClassNotFoundException e) {
                        System.err.println("Error: Could not find the compiled class: " + packageName + "." + className);
                        e.printStackTrace();
                    } catch (NoSuchMethodException e) {
                        System.err.println("Error: Could not find the main(String[] args) method in the generated class.");
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        System.err.println("Error: Could not access the main method (check visibility).");
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        System.err.println("Error: An exception occurred while executing the generated code's main method:");
                        e.getTargetException().printStackTrace(); // Print the actual exception thrown by the generated code
                    } catch (MalformedURLException e) {
                         System.err.println("Error creating URL for class loading.");
                         e.printStackTrace();
                    } catch (IOException e) {
                        System.err.println("Error closing URLClassLoader.");
                        e.printStackTrace();
                    }

                } else {
                    System.err.println("Compilation failed.");
                    for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics.getDiagnostics()) {
                        System.err.format("Error on line %d in %s%n%s%n",
                                diagnostic.getLineNumber(),
                                diagnostic.getSource() != null ? diagnostic.getSource().toUri() : "unknown source",
                                diagnostic.getMessage(null));
                    }
                }

                // --- END: Code to Compile and Run ---


            } catch (IOException e) {
                System.err.println("Error generating code: " + e.getMessage());
                e.printStackTrace();
            }

        } catch (IOException e) { // Catch for GenerateXPath or JiraStoryAccess
            System.err.println("Error during setup or generation phase: " + e.getMessage());
            e.printStackTrace();
        } finally {
             // Optional: Consider cleanup like deleting generated .class files or the output directory
             // File classFile = new File(outputDir.toFile(), packageName.replace('.', '/') + "/" + className + ".class");
             // if (classFile.exists()) classFile.delete();
             // outputFile.delete(); // Optionally delete source file too
             // Files.deleteIfExists(outputDir); // Careful if other things use this dir
        }
    }
}