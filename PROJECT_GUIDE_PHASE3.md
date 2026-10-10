# MCA Java & Web Programming Lab — Project Guide (Phase 3)
## Web Interface, Client Interaction & JSON Data Modeling
### Online Voting System & Lab Demonstration Modules

---

## 1. The Big Picture (What is Phase 3 in simple words?)

In **Phase 1** and **Phase 2**, we built the core Java foundations:
- **Phase 1**: Object-Oriented design, the `User` class, constructors, getters/setters, and string validation.
- **Phase 2**: Packages, interfaces (`UserOperations`), service layer (`UserService`), and custom exceptions (`InvalidInputException`).

In **Phase 3: Web Interface and Client Interaction**, we transition from console/desktop Java applications to a **web-based client layer**:
- **The Objective**: As specified in the MCA Lab Manual, build a responsive web interface using **HTML5**, **CSS3**, **JavaScript**, and **JSON** to provide an interactive user experience for voter registration, authentication, ballot submission, live electoral standings, and administrative governance.
- **The Role of the Web Layer**:
  - The browser acts as the **presentation layer (Client)**.
  - HTML defines the **structure** (forms, tables, ballot cards).
  - CSS provides the **styling & branding** (official Campus Election Commission design).
  - JavaScript provides the **behavior** (real-time validation, keystroke blocking, dynamic DOM rendering).
  - **JSON (JavaScript Object Notation)** acts as the **universal data exchange format**, mirroring Java backend OOP models (`model.User`, `model.Voter`, `model.Candidate`).

---

## 2. Directory Structure

```
OnlineVotingSystem/
│
├── lab_modules/
│   ├── phase1/                                  <-- Phase 1 Java Lab (OOP & Strings)
│   ├── phase2/                                  <-- Phase 2 Java Lab (Packages, Interfaces, Exceptions)
│   └── phase3/                                  <-- Standalone Lab Submission (Exact Lab Manual Specs)
│       └── UserWebProject/
│           ├── index.html                       (Lab User Registration & Display UI)
│           ├── style.css                        (Clean lab styling)
│           └── script.js                        (Client-side validation & JSON simulation)
│
├── web/                                         <-- Full MCA Online Voting System Web Portal
│   ├── index.html                               (Official Election Commission portal)
│   ├── style.css                                (Campus Electoral Commission theme)
│   └── app.js                                   (State management, ballot casting & JSON inspector)
│
├── src/                                         <-- Java Backend & Desktop Application
│   ├── model/ (User.java, Voter.java, Candidate.java)
│   ├── service/ (UserService.java, UserOperations.java)
│   ├── dao/ (VoterDAO.java, CandidateDAO.java)
│   └── exception/ (InvalidInputException.java)
│
└── PROJECT_GUIDE_PHASE3.md                      <-- This Guide
```

---

## 3. Key Components & Implementation Details

### A. Standalone Reference Module (`lab_modules/phase3/UserWebProject/`)
Built to directly fulfill the MCA lab manual's Phase 3 specifications:
- **`index.html`**: Contains the standard User Registration form (`Name`, `Email`, `Password`) and a live Registered Users display table.
- **`style.css`**: Professional form layout with clear focus indicators and responsive design.
- **`script.js`**:
  - Implements client-side input validation.
  - Generates JSON data objects dynamically upon submission.
  - Updates the DOM dynamically without page reloads.

### B. Full MCA Project Web Portal (`web/`)
Designed specifically for the **Online Voting System**:
1. **Campus Election Commission Branding**:
   - Deep Navy (`#182B49`), Royal Blue (`#2563EB`), Emerald Green (`#059669`), and Slate Light background (`#F1F5F9`).
   - High-contrast, prominent buttons (solid fills, bold white text) to ensure total visual clarity.
2. **Strict 6-Digit Numeric Voter ID Filter**:
   - Actively intercepts `keydown` events. If a user presses letters like `'a'`, special symbols, or characters outside `0-9`, the event is blocked (`e.preventDefault()`).
   - Backed by an `input` regex sanitizer (`this.value.replace(/\D/g, '').slice(0, 6)`) to prevent paste bypasses.
3. **Date of Birth Calendar Picker**:
   - Utilizes semantic HTML5 `<input type="date">` providing a native calendar picker.
   - No arbitrary 18+ limitation, ensuring smooth academic registration.
4. **Secure Authentication & Hygiene**:
   - Role-based login (Voter vs. Admin).
   - Default administrator account (`admin` / `admin123`) is supported securely in code without revealing credentials in public display text or hints.
5. **Official Ballot & "One-Voter-One-Vote" Rule**:
   - Renders candidate cards with photos/symbols, names, and manifestos.
   - When a logged-in voter casts a vote, a confirmation modal verifies their choice.
   - Upon confirmation, the voter's record is marked `hasVoted = true`, candidate tally is incremented, and the ballot disables further voting attempts for that student.
6. **Live Standings & Voter Turnout**:
   - Real-time computation of total ballots cast, turnout percentage, and current leader.
   - Animated visual progress bars for each candidate.
7. **Admin Console**:
   - Allows nominating new candidates, deleting candidates, resetting ballot tallies, and auditing voter participation.
8. **Interactive JSON Data Inspector**:
   - An on-screen inspector showing how JavaScript state models map directly to Java backend classes:
     - `model.User` & `model.Voter` (`voterId`, `fullName`, `dob`, `hasVoted`)
     - `model.Candidate` (`candidateId`, `candidateName`, `partyAffiliation`, `voteCount`)
     - `model.Vote` (`voteId`, `candidateId`, `maskedVoterId`, `timestamp`)

---

## 4. Client-Side vs. Server-Side Validation

A critical theoretical concept in Phase 3 is understanding why both client-side and server-side validation are required:

| Feature | Client-Side Validation (JavaScript) | Server-Side Validation (Java Backend) |
| :--- | :--- | :--- |
| **Execution Location** | Runs inside the user's browser (Edge, Chrome, Firefox). | Runs on the server (Java JVM / Servlet / Spring / Database). |
| **Primary Purpose** | **User Experience (UX)** & Immediate Feedback. | **Security, Data Integrity & System Trust**. |
| **Speed** | Instantaneous; no network round-trip needed. | Requires HTTP request/response latency. |
| **Bypassable?** | **Yes** (a user can disable JS, edit DOM, or use Postman/curl). | **No** (all incoming network payloads must pass through Java checks). |
| **Our Implementation** | `app.js` blocks non-digits in Voter ID and validates matching passwords. | Java `Validation.java` and `InvalidInputException` protect the database. |

> **Viva Tip**: Always state that **client-side validation is for convenience and speed, but server-side validation is mandatory for security**.

---

## 5. How JSON Bridges Frontend (JS) and Backend (Java)

JSON (JavaScript Object Notation) is a lightweight text-based format for data interchange:
1. When a student registers on the web portal, JavaScript collects form fields into a plain object:
   ```javascript
   const voterPayload = {
     voterId: "101001",
     name: "Vikram Malhotra",
     dateOfBirth: "2001-08-14",
     hasVoted: false
   };
   ```
2. In a full REST architecture, this is serialized via `JSON.stringify(voterPayload)` and sent via HTTP `POST /api/voters`.
3. The Java server receives the string and deserializes it into the `model.Voter` class:
   ```java
   public class Voter extends User {
       private String voterId;
       private String dateOfBirth;
       private boolean hasVoted;
       // Getters, setters, and business methods
   }
   ```
4. In our web portal, the **JSON Inspector** button lets examiners view and copy these live JSON representations at any moment.

---

## 6. How to Run the Project (Same Way as Phase 1 & Phase 2)

### Method 1: The Unified Interactive Lab Menu (Terminal Command)
Run this command in your VS Code Terminal or PowerShell:
```powershell
java -cp "bin;lib/*" Main
```
You will see the master lab suite menu:
```
=================================================
   ONLINE VOTING SYSTEM - MCA LAB SUITE
          Phases 1, 2, and 3 Master Runner
=================================================

--- MAIN MENU ---
1. [RECOMMENDED] Run All 3 Phases Together (Full Demonstration)
2. [Phase 1 Demo] User Class & String Validation
3. [Phase 2 Demo] Packages, Interface & Custom Exception
4. [Phase 3 Demo] Web Interface, Client Interaction & JSON
5. Launch Web Portal in Browser (Phase 3 Web App)
6. Launch Desktop GUI Window (Swing)
7. Register New Voter (Console)
8. Voter Login (Console)
9. Admin Login (Console)
10. View Database Tables (SQL Viewer)
11. Exit
```
- Select **`1`** to run **All Three Phases Together** in sequence (demonstrates Phase 1 OOP validation, Phase 2 interface and custom exceptions, Phase 3 JSON models, and boots the live web portal in your browser!).
- Select **`2`** to run **Phase 1 Demo**.
- Select **`3`** to run **Phase 2 Demo**.
- Select **`4`** to run **Phase 3 Demo** individually.
- Select **`5`** to launch the **Web Portal** directly on `http://localhost:8080/`.
- Select **`6`** to launch the **Desktop GUI (Swing)** window.

---

### Method 2: One-Click Windows Batch Script
In VS Code terminal or Windows File Explorer:
- Double-click [`run.bat`](file:///d:/Projects/OnlineVotingSystem/run.bat) to launch the unified menu.
- Or double-click [`run-web.bat`](file:///d:/Projects/OnlineVotingSystem/run-web.bat) to launch the Web Portal directly in your browser.

---

### Method 3: VS Code Run (F5)
1. Open VS Code.
2. Press **F5** (or go to **Run -> Start Debugging**).
3. Select **"Interactive Menu (Phases 1, 2 & 3)"** or **"Launch Phase 3 Web Portal"**.

---

### Method 4: Direct Browser / Live Server
1. Navigate to [`web/index.html`](file:///d:/Projects/OnlineVotingSystem/web/index.html) and double-click to open in Microsoft Edge or Google Chrome.
2. Or in VS Code, right-click `web/index.html` -> **"Open with Live Server"**.

---

## 7. Viva Voce Questions & Answers (Phase 3)

### Q1: What are the main objectives of Phase 3 in our lab syllabus?
**Answer**: Phase 3 focuses on developing the presentation tier using HTML5, CSS3, JavaScript, and JSON. It enables user registration, input validation directly in the browser, dynamic DOM manipulation without full page reloads, and data serialization into JSON objects.

### Q2: What is the DOM (Document Object Model)?
**Answer**: The DOM is an in-memory tree representation of the HTML document created by the browser. JavaScript uses DOM methods like `document.getElementById()`, `querySelector()`, and `addEventListener()` to read user inputs, modify element styles, and update HTML content dynamically.

### Q3: Why is JSON preferred over XML for modern web applications?
**Answer**:
1. **Lightweight**: JSON has far less syntactic overhead compared to XML opening/closing tags.
2. **Native Parsing**: In JavaScript, JSON is natively parsed using `JSON.parse()` and converted to string using `JSON.stringify()` without requiring third-party XML parsers.
3. **Readable**: It maps directly to key-value dictionaries and OOP class structures.

### Q4: How is the strict 6-digit numeric filter implemented for Voter ID?
**Answer**: We implement a dual-layer client strategy:
1. `keydown` event listener: Checks `e.key`. If the key is not in `0-9` (and not a navigational key like Backspace or Arrow keys), `e.preventDefault()` is invoked to prevent the character (such as `'a'`) from even appearing in the box.
2. `input` event listener: Acts as a safeguard against clipboard pasting by cleaning the value with regex: `this.value = this.value.replace(/\D/g, '').slice(0, 6)`.

### Q5: What is the purpose of `localStorage` in our Phase 3 web portal?
**Answer**: `localStorage` is an HTML5 Web Storage API that allows storing key-value pairs (up to ~5MB) in the user's browser across page reloads. We use it to persist the candidates list, voter registry, vote logs, and session status so testing is smooth and realistic.

### Q6: How does the system enforce "One Voter, One Vote"?
**Answer**: Each voter object has a boolean property `hasVoted`. When a voter logs in and confirms their choice on the ballot, `hasVoted` is set to `true`. On all subsequent visits, the candidate vote buttons are disabled, and an informative banner notifies the user that their ballot has already been sealed.

### Q7: Why do we use CSS Custom Properties (variables) in `style.css`?
**Answer**: CSS variables (like `--primary-navy: #182B49;`, `--accent-blue: #2563EB;`) ensure visual consistency across all components, make thematic changes effortless, and improve code maintainability across large front-end applications.

### Q8: What does `e.preventDefault()` do in form submissions?
**Answer**: By default, submitting an HTML form sends an HTTP GET/POST request and triggers a full page refresh. Calling `e.preventDefault()` intercepts this default browser action, allowing JavaScript to validate inputs, update state, and render UI changes asynchronously (Single Page Application behavior).

### Q9: How is voter anonymity preserved while still preventing duplicate votes?
**Answer**: The system separates voter identity from the cast ballot. The voter registry tracks *that* a student has voted (`hasVoted = true`), but the audit log (`model.Vote`) stores a masked voter ID (`10****`) and the candidate ID, ensuring no observer can link a specific candidate vote back to an individual student.

### Q10: How do the JavaScript models connect to our Phase 1 & 2 Java classes?
**Answer**: The attributes in JavaScript (`id`, `fullName`, `dob`, `hasVoted`, `votes`) correspond 1-to-1 with fields in Java's `model.User`, `model.Voter`, `model.Candidate`, and `model.Vote`. When connecting via a REST API, these JSON payloads are directly mapped to Java POJOs (Plain Old Java Objects) using libraries such as Jackson or Gson.
