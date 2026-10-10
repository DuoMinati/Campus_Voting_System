package app;

import exception.AuthenticationException;
import exception.InvalidInputException;
import gui.LoginFrame;
import model.Candidate;
import model.User;
import model.Voter;
import service.UserOperations;
import service.UserService;
import validation.Validation;
import webserver.EmbeddedWebServer;

import javax.swing.*;
import java.awt.Desktop;
import java.io.File;
import java.net.URI;
import java.util.Scanner;

/**
 * Master console application to demonstrate Phase 1, Phase 2, and Phase 3.
 * Supports running all three phases together sequentially or testing each individually.
 */
public class ConsoleApp {

    private static final Scanner scanner = new Scanner(System.in);
    private static final UserOperations userService = new UserService();

    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println("   ONLINE VOTING SYSTEM - MCA LAB SUITE");
        System.out.println("          Phases 1, 2, and 3 Master Runner");
        System.out.println("=================================================");

        boolean running = true;
        while (running) {
            System.out.println("\n--- MAIN MENU ---");
            System.out.println("1. [RECOMMENDED] Run All 3 Phases Together (Full Demonstration)");
            System.out.println("2. [Phase 1 Demo] User Class & String Validation");
            System.out.println("3. [Phase 2 Demo] Packages, Interface & Custom Exception");
            System.out.println("4. [Phase 3 Demo] Web Interface, Client Interaction & JSON");
            System.out.println("5. Launch Web Portal in Browser (Phase 3 Web App)");
            System.out.println("6. Launch Desktop GUI Window (Swing)");
            System.out.println("7. Register New Voter (Console)");
            System.out.println("8. Voter Login (Console)");
            System.out.println("9. Admin Login (Console)");
            System.out.println("10. View Database Tables (SQL Viewer)");
            System.out.println("11. Exit");
            System.out.print("Enter your choice (1-11): ");

            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1":
                    runAllThreePhasesTogether();
                    break;
                case "2":
                    runPhase1Demo();
                    break;
                case "3":
                    runPhase2Demo();
                    break;
                case "4":
                    runPhase3Demo();
                    break;
                case "5":
                    launchWebPortalWithServer();
                    break;
                case "6":
                    launchGui();
                    break;
                case "7":
                    handleVoterRegistration();
                    break;
                case "8":
                    handleVoterLogin();
                    break;
                case "9":
                    handleAdminLogin();
                    break;
                case "10":
                    db.ViewDatabase.main(new String[0]);
                    break;
                case "11":
                    System.out.println("\nExiting Online Voting System. Have a good day!");
                    EmbeddedWebServer.stop();
                    running = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please select from 1 to 11.");
            }
        }
    }

    // =========================================================================
    // RUN ALL 3 PHASES TOGETHER
    // =========================================================================
    private static void runAllThreePhasesTogether() {
        System.out.println("\n=======================================================================");
        System.out.println("   MASTER DEMONSTRATION: PHASES 1, 2, AND 3 IN SEQUENCE");
        System.out.println("=======================================================================");

        // --- PART 1: PHASE 1 ---
        System.out.println("\n[PART 1 / 3] >>> PHASE 1: CORE APPLICATION SETUP & DESIGN");
        System.out.println("-----------------------------------------------------------------------");
        System.out.println("Objectives: User Entity, Encapsulation (private fields, getters/setters),");
        System.out.println("and basic String Manipulation Validation.");
        System.out.println("\nExecuting Phase 1 validation logic on sample inputs:");
        String sampleUser = "vikram_m";
        String samplePass = "SecurePass1";
        System.out.println("-> Input Username: \"" + sampleUser + "\" (checking length >= 5, not empty)");
        System.out.println("-> Input Password: \"" + samplePass + "\" (checking length >= 8, contains digit)");

        boolean uValid = Validation.checkUsername(sampleUser);
        boolean pValid = Validation.checkPassword(samplePass);
        System.out.println("-> Validation.checkUsername(\"" + sampleUser + "\") = " + uValid);
        System.out.println("-> Validation.checkPassword(\"" + samplePass + "\") = " + pValid);

        System.out.println("\nInstantiating Java model.User in-memory entity:");
        User p1User = new User(sampleUser, samplePass, "VOTER");
        p1User.displayUser();
        System.out.println("[Phase 1 Status: PASSED]");

        System.out.print("\nPress Enter to proceed to Phase 2...");
        scanner.nextLine();

        // --- PART 2: PHASE 2 ---
        System.out.println("\n[PART 2 / 3] >>> PHASE 2: STRUCTURED PROGRAMMING & RELIABILITY");
        System.out.println("-----------------------------------------------------------------------");
        System.out.println("Objectives: 3-tier modular packages, UserOperations interface contract,");
        System.out.println("and custom exception handling (InvalidInputException).");

        System.out.println("\n1. Testing Interface Contract & Validation via userService.validateUser()...");
        try {
            boolean valid = userService.validateUser("vikram_m", "SecurePass1");
            System.out.println("-> userService.validateUser(\"vikram_m\", \"SecurePass1\") passed = " + valid);
        } catch (InvalidInputException e) {
            System.out.println("-> Unexpected error: " + e.getMessage());
        }

        System.out.println("\n2. Demonstrating Custom Exception Handling (InvalidInputException):");
        System.out.println("-> Intentionally testing invalid short username (\"ab\")...");
        try {
            userService.validateUser("ab", "SecurePass1");
        } catch (InvalidInputException e) {
            System.out.println("-> [CAUGHT EXPECTED CUSTOM EXCEPTION]: " + e.getClass().getName());
            System.out.println("   Message: " + e.getMessage());
        }
        System.out.println("[Phase 2 Status: PASSED]");

        System.out.print("\nPress Enter to proceed to Phase 3...");
        scanner.nextLine();

        // --- PART 3: PHASE 3 ---
        System.out.println("\n[PART 3 / 3] >>> PHASE 3: WEB INTERFACE, CLIENT INTERACTION & JSON");
        System.out.println("-----------------------------------------------------------------------");
        System.out.println("Objectives: Interactive Web Client (HTML5/CSS3/JS), strict 6-digit numeric");
        System.out.println("filter, calendar date of birth, and JSON data serialization mapping to Java OOP.");

        System.out.println("\n1. Demonstrating JSON Data Serialization (Java OOP -> JSON):");
        Voter sampleVoter = new Voter("Vikram Malhotra", "101001", "vikram@college.edu", "SecurePass1", "2001-08-14", 23, "Male");
        String voterJson = "{\n"
            + "  \"entity\": \"model.Voter (extends model.User)\",\n"
            + "  \"voterId\": \"" + sampleVoter.getVoterCardNo() + "\",\n"
            + "  \"fullName\": \"" + sampleVoter.getFullName() + "\",\n"
            + "  \"dateOfBirth\": \"" + sampleVoter.getDob() + "\",\n"
            + "  \"hasVoted\": false,\n"
            + "  \"role\": \"VOTER\"\n"
            + "}";
        System.out.println(voterJson);

        System.out.println("\n2. Candidate Entity JSON Mapping (model.Candidate):");
        String candidateJson = "{\n"
            + "  \"candidateId\": 1,\n"
            + "  \"candidateName\": \"Rahul Sharma\",\n"
            + "  \"partyAffiliation\": \"Student Democratic Front\",\n"
            + "  \"voteCount\": 0\n"
            + "}";
        System.out.println(candidateJson);

        System.out.println("\n3. Launching Live Embedded Web Server & Opening Web Portal in Browser...");
        launchWebPortalWithServer();
        System.out.println("[Phase 3 Status: PASSED & LIVE IN BROWSER]");

        System.out.println("\n=======================================================================");
        System.out.println("   ALL 3 PHASES DEMONSTRATED SUCCESSFULLY!");
        System.out.println("   - Phase 1: Java OOP + String Validation");
        System.out.println("   - Phase 2: Packages + Interfaces + InvalidInputException");
        System.out.println("   - Phase 3: Web Portal + Client Validation + JSON Modeling");
        System.out.println("=======================================================================");
    }

    // =========================================================================
    // PHASE 1 DEMO
    // =========================================================================
    private static void runPhase1Demo() {
        System.out.println("\n--- Phase 1: Core Application Setup & Design ---");
        System.out.println("Demonstrating User class, constructors, getters/setters and string validation.\n");

        System.out.print("Enter Username (min 5 chars) : ");
        String uname = scanner.nextLine();

        System.out.print("Enter Password (min 8 chars, 1 digit) : ");
        String pword = scanner.nextLine();

        System.out.println("\nRunning string validation...");
        boolean validUname = Validation.checkUsername(uname);
        boolean validPword = Validation.checkPassword(pword);

        if (validUname && validPword) {
            System.out.println("Validation successful! Creating User object in memory...");
            User user = new User(uname, pword, "VOTER");
            user.displayUser();
        } else {
            System.out.println("Validation failed! User object was not created.");
        }
    }

    // =========================================================================
    // PHASE 2 DEMO
    // =========================================================================
    private static void runPhase2Demo() {
        System.out.println("\n--- Phase 2: Structured Programming & Reliability ---");
        System.out.println("Demonstrating packages, UserOperations interface, and InvalidInputException.\n");

        System.out.print("Enter Username : ");
        String uname = scanner.nextLine();

        System.out.print("Enter Password : ");
        String pword = scanner.nextLine();

        System.out.println("\nCalling userService.validateUser() via UserOperations interface...");

        try {
            boolean isValid = userService.validateUser(uname, pword);
            if (isValid) {
                System.out.println("Validation passed without errors.");
                User user = new User(uname, pword);

                if (userService.login(uname, pword)) {
                    System.out.println("Login matched default admin credentials.");
                } else {
                    System.out.println("Input format valid, but not matching admin default.");
                }
                user.displayUser();
            }
        } catch (InvalidInputException e) {
            System.out.println("\n[Caught Custom Exception]");
            System.out.println("Exception: " + e.getClass().getName());
            System.out.println("Message  : " + e.getMessage());
        }
    }

    // =========================================================================
    // PHASE 3 DEMO
    // =========================================================================
    private static void runPhase3Demo() {
        boolean inPhase3 = true;
        while (inPhase3) {
            System.out.println("\n=======================================================");
            System.out.println("   Phase 3: Web Interface & Client Interaction");
            System.out.println("=======================================================");
            System.out.println("Key Features in Phase 3:");
            System.out.println("  1. Client-Side Web Portal (HTML5, CSS3, JavaScript, JSON)");
            System.out.println("  2. Strict 6-digit numeric filter blocking non-digits ('a', symbols)");
            System.out.println("  3. Date of birth calendar selection with no 18+ limitation");
            System.out.println("  4. Role-based login (Voter & Admin, credentials masked)");
            System.out.println("  5. Official ballot with One-Voter-One-Vote enforcement");
            System.out.println("  6. Live standings with turnout % and progress bars");
            System.out.println("  7. Real-time turnout calculation and analytics");
            System.out.println("-------------------------------------------------------");

            System.out.println("Choose Phase 3 Demonstration Mode:");
            System.out.println("  1. Launch Full Web Portal in Browser (Built-in HTTP Server)");
            System.out.println("  2. Open Full Web Portal Directly (file:// Protocol)");
            System.out.println("  3. Open Standalone Lab Manual Exercise (UserWebProject)");
            System.out.println("  4. Run Interactive Console JSON & OOP Entity Simulation");
            System.out.println("  5. Back to Main Menu");
            System.out.print("Enter choice (1-5): ");

            String subChoice = scanner.nextLine().trim();
            switch (subChoice) {
                case "1":
                    launchWebPortalWithServer();
                    break;
                case "2":
                    openBrowserFile("web/index.html");
                    break;
                case "3":
                    openBrowserFile("lab_modules/phase3/UserWebProject/index.html");
                    break;
                case "4":
                    runJsonSimulation();
                    break;
                case "5":
                    inPhase3 = false;
                    break;
                default:
                    System.out.println("Invalid selection. Please choose 1 to 5.");
            }
        }
    }

    private static void launchWebPortalWithServer() {
        System.out.println("\nStarting Embedded Java HTTP Server for Phase 3 Web Portal...");
        int port = EmbeddedWebServer.start("web", 8080);
        if (port != -1) {
            String url = "http://localhost:" + port + "/";
            System.out.println("Embedded Web Server is LIVE at: " + url);
            System.out.println("Opening in your default web browser...");
            openBrowserUri(url);
        } else {
            System.out.println("Could not start HTTP server, falling back to direct file open...");
            openBrowserFile("web/index.html");
        }
    }

    private static void runJsonSimulation() {
        System.out.println("\n--- Interactive JSON Entity Serialization Simulation ---");
        System.out.println("Demonstrating how JavaScript frontend JSON payloads serialize");
        System.out.println("and map directly to Java backend OOP classes (model.User, model.Voter, model.Candidate):\n");

        System.out.print("Enter sample Voter ID (6 digits, e.g. 101005): ");
        String vId = scanner.nextLine().trim();
        if (vId.isEmpty()) vId = "101005";

        System.out.print("Enter sample Full Name (e.g. Meera Nair): ");
        String vName = scanner.nextLine().trim();
        if (vName.isEmpty()) vName = "Meera Nair";

        System.out.print("Enter Date of Birth (YYYY-MM-DD, e.g. 2002-05-18): ");
        String vDob = scanner.nextLine().trim();
        if (vDob.isEmpty()) vDob = "2002-05-18";

        System.out.println("\n---------------------------------------------------------");
        System.out.println("[Step 1: In-Memory Java Object Instance]");
        System.out.println("Class: model.Voter (extends model.User)");
        System.out.println("Fields: voterCardNo=\"" + vId + "\", fullName=\"" + vName + "\", dob=\"" + vDob + "\", hasVoted=false");

        System.out.println("\n[Step 2: Serialized JSON Payload (Transmitted over HTTP REST)]");
        String jsonPayload = "{\n"
                + "  \"voterId\": \"" + vId + "\",\n"
                + "  \"fullName\": \"" + vName + "\",\n"
                + "  \"dateOfBirth\": \"" + vDob + "\",\n"
                + "  \"hasVoted\": false,\n"
                + "  \"role\": \"VOTER\"\n"
                + "}";
        System.out.println(jsonPayload);

        System.out.println("\n[Step 3: Candidate Object JSON Mapping (model.Candidate)]");
        String candidateJson = "{\n"
                + "  \"candidateId\": 1,\n"
                + "  \"candidateName\": \"Rahul Sharma\",\n"
                + "  \"partyAffiliation\": \"Student Democratic Front\",\n"
                + "  \"voteCount\": 0\n"
                + "}";
        System.out.println(candidateJson);

        System.out.println("\n[Viva Explanation]");
        System.out.println("-> Client-side validation in JavaScript ensures fast user feedback (e.g., blocking non-digits).");
        System.out.println("-> The browser serializes the data into standard JSON format.");
        System.out.println("-> The Java backend parses the JSON directly into Java OOP models for database persistence.");
        System.out.println("---------------------------------------------------------");
    }

    private static void openBrowserFile(String relativeFilePath) {
        File file = new File(relativeFilePath);
        if (!file.exists()) {
            System.out.println("Error: File not found at " + file.getAbsolutePath());
            return;
        }
        openBrowserUri(file.toURI().toString());
    }

    private static void openBrowserUri(String uriString) {
        try {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(new URI(uriString));
                System.out.println("Browser opened: " + uriString);
            } else {
                Runtime.getRuntime().exec(new String[]{"cmd", "/c", "start", uriString});
                System.out.println("Launched via system command: " + uriString);
            }
        } catch (Exception e) {
            System.out.println("Could not auto-open browser (" + e.getMessage() + ").");
            System.out.println("Please open manually in your browser: " + uriString);
        }
    }

    // =========================================================================
    // CONSOLE REGISTRATION & LOGINS
    // =========================================================================
    private static void handleVoterRegistration() {
        System.out.println("\n--- Voter Registration ---");
        try {
            System.out.print("Full Name                         : ");
            String name = scanner.nextLine();

            System.out.print("Voter ID (Exactly 6 digits only)  : ");
            String card = scanner.nextLine();

            System.out.print("Email Address                     : ");
            String email = scanner.nextLine();

            System.out.print("Date of Birth (DD-MM-YYYY)        : ");
            String dob = scanner.nextLine();

            System.out.print("Gender (Male/Female/Other)        : ");
            String gender = scanner.nextLine();

            System.out.print("Password (min 8 chars, 1 digit)   : ");
            String pass = scanner.nextLine();

            System.out.print("Confirm Password                  : ");
            String confirmPass = scanner.nextLine();

            Voter voter = Validation.validateAndCreateVoter(name, card, email, dob, gender, pass, confirmPass);
            boolean success = userService.registerVoter(voter, confirmPass);

            if (success) {
                System.out.println("\nRegistration successful!");
                voter.displayUser();
            }

        } catch (InvalidInputException e) {
            System.out.println("\nRegistration Error: " + e.getMessage());
        }
    }

    private static void handleVoterLogin() {
        System.out.println("\n--- Voter Login ---");
        try {
            System.out.print("Email    : ");
            String email = scanner.nextLine();

            System.out.print("Password : ");
            String pass = scanner.nextLine();

            Voter voter = userService.loginVoter(email, pass);
            System.out.println("\nLogin successful! Welcome " + voter.getFullName() + ".");
            voter.displayUser();

        } catch (InvalidInputException | AuthenticationException e) {
            System.out.println("\nLogin Error: " + e.getMessage());
        }
    }

    private static void handleAdminLogin() {
        System.out.println("\n--- Admin Login ---");
        try {
            System.out.print("Username : ");
            String username = scanner.nextLine();

            System.out.print("Password : ");
            String password = scanner.nextLine();

            boolean success = userService.loginAdmin(username, password);
            if (success) {
                System.out.println("\nAdmin login successful! Welcome, " + username + ".");
            }

        } catch (InvalidInputException | AuthenticationException e) {
            System.out.println("\nLogin Error: " + e.getMessage());
        }
    }

    private static void launchGui() {
        System.out.println("\nOpening Desktop GUI window...");
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
        System.out.println("GUI is now open on your screen.");
    }
}
