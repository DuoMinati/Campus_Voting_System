import app.ConsoleApp;
import gui.LoginFrame;
import webserver.EmbeddedWebServer;

import javax.swing.*;
import java.awt.Desktop;
import java.io.File;
import java.net.URI;

/**
 * Entry point of the Online Voting System project.
 * Supports running Phase 1, Phase 2, and Phase 3 together or individually:
 * 1. Default (no arguments): Runs the master Interactive Console Suite (`java -cp "bin;lib/*" Main`)
 * 2. GUI mode (--gui / -g): Launches the Swing Desktop GUI
 * 3. Web mode (--web / -w): Launches the Phase 3 Web Portal directly
 */
public class Main {

    public static void main(String[] args) {
        // Initialize and verify MySQL database
        try {
            db.DBInitializer.initializeDatabase();
        } catch (Exception e) {
            System.err.println("Database initialization notice: " + e.getMessage());
        }

        // Option 1: Direct Web Portal Launch (Phase 3)
        if (args != null && args.length > 0 && (args[0].equalsIgnoreCase("--web") || args[0].equalsIgnoreCase("--browser") || args[0].equalsIgnoreCase("-w"))) {
            launchWebPortal();
            return;
        }

        // Option 2: Direct Desktop Swing GUI Launch
        if (args != null && args.length > 0 && (args[0].equalsIgnoreCase("--gui") || args[0].equalsIgnoreCase("-g"))) {
            launchGui();
            return;
        }

        // DEFAULT: Run the master suite (runs with `java -cp "bin;lib/*" Main` or with `--cli`)
        ConsoleApp.main(args);
    }

    public static void launchWebPortal() {
        System.out.println("Starting Online Voting System Web Portal...");
        int port = EmbeddedWebServer.start("web", 8080);
        if (port != -1) {
            String url = "http://localhost:" + port + "/";
            System.out.println("Web server is running at: " + url);
            openBrowser(url);
        } else {
            File htmlFile = new File("web/index.html");
            openBrowser(htmlFile.toURI().toString());
        }
    }

    public static void launchGui() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.out.println("Could not set system look and feel, using default.");
        }
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }

    private static void openBrowser(String target) {
        try {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(new URI(target));
                System.out.println("Opened in default browser: " + target);
            } else {
                Runtime.getRuntime().exec(new String[]{"cmd", "/c", "start", target});
                System.out.println("Launched via system command: " + target);
            }
        } catch (Exception e) {
            System.out.println("Please open manually in your browser: " + target);
        }
    }
}
