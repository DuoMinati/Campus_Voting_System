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
                // Initialize and seed MySQL database
                try {
                    db.DBInitializer.initializeDatabase();
                } catch (Exception e) {
                    System.err.println("Database initialization warning: " + e.getMessage());
                }

                server = HttpServer.create(new InetSocketAddress(port), 0);
                server.createContext("/api/", new ApiHandler());
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
            byte[] bytes = responseText.getBytes(java.nio.charset.StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", contentType);
            exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
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

    /**
     * REST API Handler connecting Web Portal directly to MySQL database via DAOs.
     */
    static class ApiHandler implements HttpHandler {
        private final dao.CandidateDAO candidateDAO = new dao.CandidateDAO();
        private final dao.VoterDAO voterDAO = new dao.VoterDAO();
        private final dao.VoteDAO voteDAO = new dao.VoteDAO();

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();

            // Set CORS headers
            exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, OPTIONS, DELETE");
            exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type, Authorization");

            if ("OPTIONS".equalsIgnoreCase(method)) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            try {
                String body = "";
                if ("POST".equalsIgnoreCase(method)) {
                    body = new String(exchange.getRequestBody().readAllBytes(), java.nio.charset.StandardCharsets.UTF_8);
                }

                if ("/api/state".equals(path) && "GET".equalsIgnoreCase(method)) {
                    handleGetState(exchange);
                } else if ("/api/register".equals(path) && "POST".equalsIgnoreCase(method)) {
                    handleRegister(exchange, body);
                } else if ("/api/login".equals(path) && "POST".equalsIgnoreCase(method)) {
                    handleLogin(exchange, body);
                } else if ("/api/vote".equals(path) && "POST".equalsIgnoreCase(method)) {
                    handleVote(exchange, body);
                } else if ("/api/delete-ballot".equals(path) && "POST".equalsIgnoreCase(method)) {
                    handleDeleteBallot(exchange, body);
                } else if ("/api/delete-voter".equals(path) && "POST".equalsIgnoreCase(method)) {
                    handleDeleteVoter(exchange, body);
                } else if ("/api/add-candidate".equals(path) && "POST".equalsIgnoreCase(method)) {
                    handleAddCandidate(exchange, body);
                } else if ("/api/reset-votes".equals(path) && "POST".equalsIgnoreCase(method)) {
                    handleResetVotes(exchange);
                } else if ("/api/status".equals(path)) {
                    boolean connected = db.DBConnection.testConnection();
                    sendJson(exchange, 200, "{\"dbConnected\":" + connected + ",\"message\":\"" + (connected ? "Connected to database" : "Database offline") + "\"}");
                } else {
                    sendJson(exchange, 404, "{\"error\":\"Endpoint not found\"}");
                }
            } catch (Exception e) {
                e.printStackTrace();
                sendJson(exchange, 500, "{\"error\":\"" + escapeJson(e.getMessage()) + "\"}");
            }
        }

        private void handleGetState(HttpExchange exchange) throws IOException {
            java.util.List<model.Candidate> candidates = candidateDAO.getAllCandidates();
            java.util.List<model.Voter> voters = voterDAO.getAllVoters();
            java.util.List<java.util.Map<String, Object>> votesAudit = voteDAO.getAllVotesAudit();

            StringBuilder sb = new StringBuilder();
            sb.append("{");
            sb.append("\"isDbConnected\":true,");

            // Candidates array
            sb.append("\"candidates\":[");
            for (int i = 0; i < candidates.size(); i++) {
                model.Candidate c = candidates.get(i);
                if (i > 0) sb.append(",");
                sb.append("{");
                sb.append("\"id\":").append(c.getCandidateId()).append(",");
                sb.append("\"name\":\"").append(escapeJson(c.getCandidateName())).append("\",");
                sb.append("\"party\":\"").append(escapeJson(c.getPartyName())).append("\",");
                String man = c.getManifesto();
                if (man == null || man.trim().isEmpty()) {
                    man = "Advocating for campus welfare, transparent governance, and student community progress.";
                }
                sb.append("\"manifesto\":\"").append(escapeJson(man)).append("\",");
                sb.append("\"votes\":").append(c.getTotalVotes());
                sb.append("}");
            }
            sb.append("],");

            // Voters array
            sb.append("\"voters\":[");
            for (int i = 0; i < voters.size(); i++) {
                model.Voter v = voters.get(i);
                if (i > 0) sb.append(",");
                sb.append("{");
                sb.append("\"id\":\"").append(escapeJson(v.getVoterCardNo())).append("\",");
                sb.append("\"dbId\":").append(v.getVoterId()).append(",");
                sb.append("\"fullName\":\"").append(escapeJson(v.getFullName())).append("\",");
                sb.append("\"email\":\"").append(escapeJson(v.getEmail())).append("\",");
                sb.append("\"dob\":\"").append(escapeJson(v.getDob())).append("\",");
                sb.append("\"age\":").append(v.getAge()).append(",");
                sb.append("\"gender\":\"").append(escapeJson(v.getGender())).append("\",");
                sb.append("\"hasVoted\":").append(v.isHasVoted()).append(",");

                // Lookup whom this voter voted for if hasVoted is true
                Integer votedForId = null;
                if (v.isHasVoted()) {
                    model.Candidate votedCand = voteDAO.getVotedCandidateForVoter(v.getVoterId());
                    if (votedCand != null) {
                        votedForId = votedCand.getCandidateId();
                    }
                }
                sb.append("\"votedFor\":").append(votedForId != null ? votedForId : "null");
                sb.append("}");
            }
            sb.append("],");

            // Ballots / votes array
            sb.append("\"votes\":[");
            for (int i = 0; i < votesAudit.size(); i++) {
                java.util.Map<String, Object> va = votesAudit.get(i);
                if (i > 0) sb.append(",");
                sb.append("{");
                sb.append("\"voteId\":\"").append(escapeJson(String.valueOf(va.get("voteId")))).append("\",");
                sb.append("\"voterId\":\"").append(escapeJson(String.valueOf(va.get("voterId")))).append("\",");
                sb.append("\"voterDbId\":").append(va.get("voterDbId")).append(",");
                sb.append("\"voterName\":\"").append(escapeJson(String.valueOf(va.get("voterName")))).append("\",");
                sb.append("\"candidateId\":").append(va.get("candidateId")).append(",");
                sb.append("\"candidateName\":\"").append(escapeJson(String.valueOf(va.get("candidateName")))).append("\",");
                sb.append("\"candidateParty\":\"").append(escapeJson(String.valueOf(va.get("candidateParty")))).append("\",");
                sb.append("\"candidateSymbol\":\"").append(escapeJson(String.valueOf(va.get("candidateSymbol")))).append("\",");
                sb.append("\"timestamp\":\"").append(escapeJson(String.valueOf(va.get("timestamp")))).append("\"");
                sb.append("}");
            }
            sb.append("],");

            // Admin object
            sb.append("\"admin\":{\"username\":\"admin\"}");
            sb.append("}");

            sendJson(exchange, 200, sb.toString());
        }

        private void handleRegister(HttpExchange exchange, String body) throws IOException {
            String fullName = extractString(body, "fullName");
            String voterId = extractString(body, "voterId");
            String dob = extractString(body, "dob");
            String password = extractString(body, "password");
            String email = extractString(body, "email");
            String gender = extractString(body, "gender");

            if (fullName.isEmpty() || voterId.isEmpty() || password.isEmpty()) {
                sendJson(exchange, 400, "{\"success\":false,\"message\":\"Required fields missing\"}");
                return;
            }

            if (voterId.length() != 6 || !voterId.matches("\\d{6}")) {
                sendJson(exchange, 400, "{\"success\":false,\"message\":\"Voter ID must be exactly 6 numeric digits\"}");
                return;
            }

            if (email.isEmpty()) {
                email = voterId + "@campus.edu";
            }
            if (gender.isEmpty()) {
                gender = "Other";
            }

            // Check if already exists in MySQL
            if (voterDAO.getVoterByCardNo(voterId) != null) {
                sendJson(exchange, 400, "{\"success\":false,\"message\":\"Voter ID " + voterId + " is already enrolled in the database.\"}");
                return;
            }

            // Calculate age from Date of Birth
            int calculatedAge = 18;
            if (dob != null && !dob.trim().isEmpty()) {
                try {
                    java.time.LocalDate birthDate = java.time.LocalDate.parse(dob.trim());
                    calculatedAge = java.time.Period.between(birthDate, java.time.LocalDate.now()).getYears();
                    if (calculatedAge < 18) {
                        sendJson(exchange, 400, "{\"success\":false,\"message\":\"Voters must be at least 18 years of age to register.\"}");
                        return;
                    }
                } catch (Exception e) {
                    calculatedAge = 18;
                }
            }

            model.Voter voter = new model.Voter(fullName, voterId, email, password, dob, calculatedAge, gender);
            boolean inserted = voterDAO.registerVoter(voter);

            if (inserted) {
                sendJson(exchange, 200, "{\"success\":true,\"message\":\"Voter registered successfully\",\"voterId\":\"" + voterId + "\"}");
            } else {
                sendJson(exchange, 500, "{\"success\":false,\"message\":\"Database insertion failed. Email or ID may already exist.\"}");
            }
        }

        private void handleLogin(HttpExchange exchange, String body) throws IOException {
            String role = extractString(body, "role");
            String userId = extractString(body, "userId");
            String password = extractString(body, "password");

            if ("admin".equalsIgnoreCase(role)) {
                if ("admin".equals(userId) && "admin123".equals(password)) {
                    sendJson(exchange, 200, "{\"success\":true,\"user\":{\"role\":\"admin\",\"id\":\"admin\",\"name\":\"System Administrator\",\"hasVoted\":false,\"votedFor\":null}}");
                } else {
                    sendJson(exchange, 401, "{\"success\":false,\"message\":\"Invalid administrative credentials\"}");
                }
            } else {
                model.Voter voter = voterDAO.login(userId, password);
                if (voter != null) {
                    Integer votedForId = null;
                    if (voter.isHasVoted()) {
                        model.Candidate c = voteDAO.getVotedCandidateForVoter(voter.getVoterId());
                        if (c != null) {
                            votedForId = c.getCandidateId();
                        }
                    }
                    StringBuilder resp = new StringBuilder();
                    resp.append("{\"success\":true,\"user\":{");
                    resp.append("\"role\":\"voter\",");
                    resp.append("\"id\":\"").append(escapeJson(voter.getVoterCardNo())).append("\",");
                    resp.append("\"dbId\":").append(voter.getVoterId()).append(",");
                    resp.append("\"name\":\"").append(escapeJson(voter.getFullName())).append("\",");
                    resp.append("\"hasVoted\":").append(voter.isHasVoted()).append(",");
                    resp.append("\"votedFor\":").append(votedForId != null ? votedForId : "null");
                    resp.append("}}");
                    sendJson(exchange, 200, resp.toString());
                } else {
                    sendJson(exchange, 401, "{\"success\":false,\"message\":\"Invalid 6-digit Voter ID or password.\"}");
                }
            }
        }

        private void handleVote(HttpExchange exchange, String body) throws IOException {
            String voterIdStr = extractString(body, "voterId");
            int candidateId = extractInt(body, "candidateId", -1);

            if (voterIdStr.isEmpty() || candidateId == -1) {
                sendJson(exchange, 400, "{\"success\":false,\"message\":\"Invalid voter ID or candidate choice.\"}");
                return;
            }

            // Find voter in database
            model.Voter v = voterDAO.getVoterByCardNo(voterIdStr);
            if (v == null && voterIdStr.matches("\\d+")) {
                try {
                    v = voterDAO.getVoterById(Integer.parseInt(voterIdStr));
                } catch (Exception ignored) {}
            }

            if (v == null) {
                sendJson(exchange, 404, "{\"success\":false,\"message\":\"Voter record not found in database.\"}");
                return;
            }

            if (v.isHasVoted()) {
                sendJson(exchange, 400, "{\"success\":false,\"message\":\"Ballot already submitted. One voter one vote is strictly enforced.\"}");
                return;
            }

            boolean ok = voteDAO.castVote(v.getVoterId(), candidateId);
            if (ok) {
                sendJson(exchange, 200, "{\"success\":true,\"message\":\"Vote recorded successfully.\"}");
            } else {
                sendJson(exchange, 500, "{\"success\":false,\"message\":\"Database transaction failed to record vote.\"}");
            }
        }

        private void handleDeleteBallot(HttpExchange exchange, String body) throws IOException {
            String voterIdStr = extractString(body, "voterId");
            String voteIdStr = extractString(body, "voteId");

            boolean ok = false;
            if (!voterIdStr.isEmpty()) {
                model.Voter v = voterDAO.getVoterByCardNo(voterIdStr);
                int voterDbId = v != null ? v.getVoterId() : (voterIdStr.matches("\\d+") ? Integer.parseInt(voterIdStr) : -1);
                if (voterDbId > 0) {
                    ok = voteDAO.deleteVoteByVoterId(voterDbId);
                }
            } else if (!voteIdStr.isEmpty()) {
                String cleaned = voteIdStr.replaceAll("\\D", "");
                if (!cleaned.isEmpty()) {
                    ok = voteDAO.deleteVoteByVoteId(Integer.parseInt(cleaned));
                }
            }

            if (ok) {
                sendJson(exchange, 200, "{\"success\":true,\"message\":\"Ballot deleted and candidate count updated.\"}");
            } else {
                sendJson(exchange, 400, "{\"success\":false,\"message\":\"Could not locate or delete the ballot record.\"}");
            }
        }

        private void handleDeleteVoter(HttpExchange exchange, String body) throws IOException {
            String voterIdStr = extractString(body, "voterId");
            if (voterIdStr.isEmpty()) {
                sendJson(exchange, 400, "{\"success\":false,\"message\":\"Voter ID required.\"}");
                return;
            }

            boolean ok = voterDAO.deleteVoterByCardNo(voterIdStr);
            if (!ok && voterIdStr.matches("\\d+")) {
                ok = voterDAO.deleteVoterById(Integer.parseInt(voterIdStr));
            }

            if (ok) {
                sendJson(exchange, 200, "{\"success\":true,\"message\":\"Voter profile removed.\"}");
            } else {
                sendJson(exchange, 400, "{\"success\":false,\"message\":\"Could not delete voter.\"}");
            }
        }

        private void handleAddCandidate(HttpExchange exchange, String body) throws IOException {
            String name = extractString(body, "name");
            String party = extractString(body, "party");
            String symbol = extractString(body, "symbol");

            if (name.isEmpty() || party.isEmpty()) {
                sendJson(exchange, 400, "{\"success\":false,\"message\":\"Candidate name and party required.\"}");
                return;
            }

            boolean ok = candidateDAO.addCandidate(name, party, symbol);
            if (ok) {
                sendJson(exchange, 200, "{\"success\":true,\"message\":\"Candidate added successfully.\"}");
            } else {
                sendJson(exchange, 500, "{\"success\":false,\"message\":\"Could not insert candidate into database.\"}");
            }
        }

        private void handleResetVotes(HttpExchange exchange) throws IOException {
            boolean ok = voteDAO.resetAllVotes();
            if (ok) {
                sendJson(exchange, 200, "{\"success\":true,\"message\":\"All votes reset to zero.\"}");
            } else {
                sendJson(exchange, 500, "{\"success\":false,\"message\":\"Failed to reset votes.\"}");
            }
        }

        private void sendJson(HttpExchange exchange, int statusCode, String jsonResponse) throws IOException {
            byte[] bytes = jsonResponse.getBytes(java.nio.charset.StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
            exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
            exchange.sendResponseHeaders(statusCode, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        }

        private static String extractString(String json, String key) {
            if (json == null) return "";
            java.util.regex.Pattern p = java.util.regex.Pattern.compile("\"" + key + "\"\\s*:\\s*\"([^\"]*)\"");
            java.util.regex.Matcher m = p.matcher(json);
            if (m.find()) return m.group(1);
            return "";
        }

        private static int extractInt(String json, String key, int def) {
            if (json == null) return def;
            java.util.regex.Pattern p = java.util.regex.Pattern.compile("\"" + key + "\"\\s*:\\s*([0-9]+)");
            java.util.regex.Matcher m = p.matcher(json);
            if (m.find()) {
                try {
                    return Integer.parseInt(m.group(1));
                } catch (Exception ignored) {}
            }
            java.util.regex.Pattern pQ = java.util.regex.Pattern.compile("\"" + key + "\"\\s*:\\s*\"([0-9]+)\"");
            java.util.regex.Matcher mQ = pQ.matcher(json);
            if (mQ.find()) {
                try {
                    return Integer.parseInt(mQ.group(1));
                } catch (Exception ignored) {}
            }
            return def;
        }

        private static String escapeJson(String s) {
            if (s == null) return "";
            StringBuilder sb = new StringBuilder();
            for (char c : s.toCharArray()) {
                switch (c) {
                    case '"': sb.append("\\\""); break;
                    case '\\': sb.append("\\\\"); break;
                    case '\b': sb.append("\\b"); break;
                    case '\f': sb.append("\\f"); break;
                    case '\n': sb.append("\\n"); break;
                    case '\r': sb.append("\\r"); break;
                    case '\t': sb.append("\\t"); break;
                    default:
                        if (c < ' ') {
                            String t = "000" + Integer.toHexString(c);
                            sb.append("\\u").append(t.substring(t.length() - 4));
                        } else {
                            sb.append(c);
                        }
                }
            }
            return sb.toString();
        }
    }
}
