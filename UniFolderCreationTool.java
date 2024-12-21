/* Author: Kyle Arthur
 * Date: 19/12/2024
 * Purpose: This program is designed to create an organised folder structure for university semesters.
 */

import java.util.Scanner; // For taking user input from console
import java.nio.file.Path; // Interface for representing a file/directory path
import java.nio.file.Paths; // Utility class for creating path instances from string paths
import java.nio.file.Files; // Utility class for file/directory operations
import java.util.HashMap; // For storing course names and their corresponding codes

public class UniFolderCreationTool {
    private String degree;
    private HashMap<String, String> courses = new HashMap<>();
    private String major;
    private String year;
    private String semester;
    private int weeks;
    public static void main(String[] args) {
        UniFolderCreationTool toolInstance = new UniFolderCreationTool();
        Scanner console = new Scanner(System.in);
        System.out.println("Kyle's University Folder Creation Tool");
        System.out.println("---------------------------------------------");
        System.out.println("Please enter the following details to create a folder structure for your university semester:");
        String verifyParentFolderResponse;
        String parentFolderName;
        do {
            System.out.println("\nEnter your degree name:");
            toolInstance.degree = console.nextLine();
            System.out.println("\nDo you have a major? (Y/N)");
            String majorYNResponse = console.nextLine();
            if (majorYNResponse.equalsIgnoreCase("Y")) {
                System.out.println("\nEnter your major:");
                toolInstance.major = console.nextLine();
            }
            System.out.println("\nEnter the year you want to create the folder structure for:");
            toolInstance.year = console.nextLine();
            System.out.println("\nEnter the semester:");
            toolInstance.semester = console.nextLine();
            do {
                System.out.println("\nEnter number of weeks in the semester:");
                // Enforce int input for weeks:
                while (!console.hasNextInt()) {
                    System.out.println("Please enter a valid number of weeks (1-52):");
                    console.next(); // Consume the invalid input (otherwise it will infinitely loop)
                }
                toolInstance.weeks = console.nextInt();
                if (toolInstance.weeks < 1 || toolInstance.weeks > 52) {
                    System.out.println("Please enter an appropriate number of weeks (1-52):");
                }
                console.nextLine(); // Consume the newline character
            } while (toolInstance.weeks < 1 || toolInstance.weeks > 52);
            System.out.println("\nSo far, the parent folder will be named as follows:");
            if (majorYNResponse.equalsIgnoreCase("Y")) {
                parentFolderName = toolInstance.year + " - " + toolInstance.degree + " - " + toolInstance.major + " (Semester " + toolInstance.semester + ")";
                System.out.println("\"" + parentFolderName + "\"");
            } else {
                parentFolderName = toolInstance.year + " - " + toolInstance.degree + " (Semester " + toolInstance.semester + ")";
                System.out.println("\"" + parentFolderName + "\"");
            }
            System.out.println("Is this correct? (Y/N)");
            verifyParentFolderResponse = console.nextLine();
        } while (verifyParentFolderResponse.equalsIgnoreCase("N"));

        String verifyCourseFolderResponse;
        do {
            System.out.println("\nEnter the number of courses you are taking this semester:");
            int numCourses = console.nextInt();
            console.nextLine(); // Consume the newline character
            for (int i = 0; i < numCourses; i++) {
                System.out.println("\nEnter the name of course " + (i + 1) + ":");
                String courseName = console.nextLine();
                System.out.println("Enter the course code for " + courseName + ":");
                String courseCode = console.nextLine().toUpperCase();
                toolInstance.courses.put(courseName, courseCode);
            }
            System.out.println("\nThe folders created based on what you have entered will be:");
            for (String courseName : toolInstance.courses.keySet()) {
                System.out.println("\"" + toolInstance.courses.get(courseName) + " - " + courseName + "\"");
            }
            System.out.println("Is this correct? (Y/N)");
            verifyCourseFolderResponse = console.nextLine();
        } while (verifyCourseFolderResponse.equalsIgnoreCase("N"));

        String oneDriveEnvPath = System.getenv("OneDriveCommercial"); // First attempt to find the explicit OneDrive Commercial path
        String userPath = null; // User's manually entered path if OneDrive is not found or preferred
        if (oneDriveEnvPath == null) {
            oneDriveEnvPath = System.getenv("OneDrive"); // Fallback to the generic OneDrive path if the commercial path is not found
            if (oneDriveEnvPath == null) {
                System.out.println("Please enter the path to where you would like to create the folders (i.e., C:/Users/me/OneDrive):");
                userPath = console.nextLine();
            }
        }
        Path rootPath = null; // The path where the parent folder will be created
        String useOneDrivePathResponse;
        if (oneDriveEnvPath != null) {
            System.out.println("\nOneDrive path found at: " + oneDriveEnvPath);
            System.out.println("Would you like to create the folders here? (Y/N)");
            useOneDrivePathResponse = console.nextLine();
            if (useOneDrivePathResponse.equalsIgnoreCase("Y")) {
                rootPath = Paths.get(oneDriveEnvPath); // Set the Path object value from the user's path
            } else if (useOneDrivePathResponse.equalsIgnoreCase("N")) {
                System.out.println("Please enter the path to where you would like to create the folders:");
                userPath = console.nextLine();
                rootPath = Paths.get(userPath); // Set the Path object value from the user's path
            }
            else {
                System.out.println("Invalid response. Please enter Y or N.");
            }
        }
        else {
            rootPath = Paths.get(userPath); // Set the Path object value from the user's path
        }
        Path parentFolderPath = rootPath.resolve(parentFolderName); // Create a Path object for the parent folder
        try {
            // Create the parent folder:
            System.out.println("\nCreating root folder: " + parentFolderPath + "...");
            Files.createDirectories(parentFolderPath);
            // Create each course folder under the parent folder:
            for (String courseName : toolInstance.courses.keySet()) {
                Path courseFolderPath = parentFolderPath.resolve(toolInstance.courses.get(courseName) + " - " + courseName);
                System.out.println("Creating course folder: " + courseFolderPath + "...");
                Files.createDirectories(courseFolderPath);
                // Create week folders under each course folder:
                for (int i = 1; i <= toolInstance.weeks; i++) {
                    if (i == 1) {
                       System.out.println("Creating week folders ...");
                    }
                    String weekFolderName = String.format("Week %02d", i);
                    Path weekFolderPath = courseFolderPath.resolve(weekFolderName);
                    Files.createDirectories(weekFolderPath);
                }
            }
            System.out.println("\nFolder structure created successfully!");
        } catch (Exception e) {
            e.printStackTrace();
        }
        console.close();

        // TODO Convert to a GUI program
        // TODO OneDrive folder colour changing?
    }
}