package webserver;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.file.Files;

/**
 * Embedded HTTP Web Server built using standard Java SE libraries (com.sun.net.httpserver).
 * Serves Phase 3 static web assets (HTML, CSS, JS, JSON) over HTTP without needing external servers.
 */
public class EmbeddedWebServer {

    private static HttpServer server = null;
    private static int currentPort = 8080;

    /**
     * Start the server hosting a specific directory.
     *
     * @param directoryPath Relative or absolute path to web assets folder (e.g. "web").
     * @param preferredPort Port number (e.g. 8080).
     * @return The actual port the server is running on, or -1 if failed.
     */
    public static synchronized int start(String directoryPath, int preferredPort) {
        if (server != null) {
            return currentPort; // Already running
        }

        File webRoot = new File(directoryPath);
        if (!webRoot.exists() || !webRoot.isDirectory()) {
            System.err.println("Web root directory not found: " + webRoot.getAbsolutePath());
            return -1;
        }

        int port = preferredPort;
        while (port < preferredPort + 10) {
            try {
                server = HttpServer.create(new InetSocketAddress(port), 0);
                server.createContext("/", new StaticFileHandler(webRoot));
                server.setExecutor(null); // default single-threaded executor
                server.start();
                currentPort = port;
                return currentPort;
            } catch (IOException e) {
                // Port might be in use, try next
                port++;
            }
        }

        System.err.println("Could not bind HTTP server to ports " + preferredPort + "-" + (port - 1));
        return -1;
    }

    /**
     * Stop the running server.
     */
    public static synchronized void stop() {
        if (server != null) {
            server.stop(0);
            server = null;
            System.out.println("Embedded Web Server stopped.");
        }
    }

    public static boolean isRunning() {
        return server != null;
    }

    public static int getCurrentPort() {
        return currentPort;
    }

    /**
     * Static file handler supporting MIME types for HTML, CSS, JS, JSON, images.
     */
    static class StaticFileHandler implements HttpHandler {
        private final File baseDir;

        public StaticFileHandler(File baseDir) {
            this.baseDir = baseDir;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String requestPath = exchange.getRequestURI().getPath();
            if (requestPath == null || requestPath.equals("/") || requestPath.isEmpty()) {
                requestPath = "/index.html";
            }

            // Normalize path to prevent directory traversal
            File requestedFile = new File(baseDir, requestPath).getCanonicalFile();
            if (!requestedFile.getPath().startsWith(baseDir.getCanonicalPath())) {
                sendResponse(exchange, 403, "Access Denied", "text/plain");
                return;
            }

            if (!requestedFile.exists() || requestedFile.isDirectory()) {
                sendResponse(exchange, 404, "404 Not Found", "text/plain");
                return;
            }

            String mimeType = getMimeType(requestedFile.getName());
            exchange.getResponseHeaders().set("Content-Type", mimeType);
            exchange.getResponseHeaders().set("Cache-Control", "no-cache");
            exchange.sendResponseHeaders(200, requestedFile.length());

            try (OutputStream os = exchange.getResponseBody();
                 FileInputStream fis = new FileInputStream(requestedFile)) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = fis.read(buffer)) != -1) {
                    os.write(buffer, 0, bytesRead);
                }
            }
        }

        private void sendResponse(HttpExchange exchange, int statusCode, String responseText, String contentType) throws IOException {
            byte[] bytes = responseText.getBytes();
            exchange.getResponseHeaders().set("Content-Type", contentType);
            exchange.sendResponseHeaders(statusCode, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        }

        private String getMimeType(String fileName) {
            String lower = fileName.toLowerCase();
            if (lower.endsWith(".html") || lower.endsWith(".htm")) return "text/html; charset=UTF-8";
            if (lower.endsWith(".css")) return "text/css; charset=UTF-8";
            if (lower.endsWith(".js")) return "application/javascript; charset=UTF-8";
            if (lower.endsWith(".json")) return "application/json; charset=UTF-8";
            if (lower.endsWith(".png")) return "image/png";
            if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) return "image/jpeg";
            if (lower.endsWith(".svg")) return "image/svg+xml";
            if (lower.endsWith(".ico")) return "image/x-icon";
            return "text/plain; charset=UTF-8";
        }
    }
}
