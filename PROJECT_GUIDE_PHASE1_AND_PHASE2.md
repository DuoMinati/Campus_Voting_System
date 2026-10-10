# MCA Java & Web Programming Lab — Project Guide (Phase 1 & Phase 2)
## Online Voting System & Lab Demonstration Modules

---

## 1. The Big Picture (What is this project in simple words?)

Imagine a college election where students vote for student council candidates:
- **The Problem**: Traditional paper voting is slow, hard to count, prone to fraud (people voting multiple times), and allows invalid entries (underage voters or malformed IDs).
- **The Solution**: An **Online Voting System** built in Java.
- In your MCA curriculum, this project is developed in **Phases**:
  - **Phase 1: Core Application Setup and Design** — Setting up the foundational building block: the **User entity** (who uses the system), creating it using Object-Oriented Programming (constructors, private variables, getters/setters), and performing **basic string validation** on user inputs.
  - **Phase 2: Structured Programming and Reliability** — Making the application organized, robust, and crash-proof. We organize code into **packages**, define an **interface** (`UserOperations`) that enforces what actions can be done, and implement **custom exception handling** (`InvalidInputException`) so invalid inputs are caught and reported cleanly rather than crashing the program.

---

## 2. Directory Structure

```
OnlineVotingSystem/
│
├── lab_modules/                                 <-- Standalone Lab Submissions
│   ├── phase1/                                  <-- Phase 1 Lab Exercise
│   │   ├── User.java                            (Module 1: User Entity)
│   │   ├── Validation.java                      (Module 2: Validation Logic)
│   │   ├── Main.java                            (Module 3: Console Main)
│   │   └── combined/
│   │       └── UserRegistrationSystemCombined.java (Single-file exam version)
│   └── phase2/                                  <-- Phase 2 Lab Exercise
│       ├── model/User.java                      (Packaged Model Entity)
│       ├── service/UserOperations.java          (Phase 2 Interface)
│       ├── service/UserService.java             (Phase 2 Implementation)
│       ├── exception/InvalidInputException.java (Custom Exception)
│       ├── app/Main.java                        (App Entry Point)
│       └── combined/
│           └── UserAuthenticationSystemCombined.java (Single-file exam version)
│
├── lib/
│   └── mysql-connector-j-8.4.0.jar              <-- MySQL JDBC Driver (auto-downloaded)
│
├── database/
│   └── voting_system.sql                        <-- Database tables & sample data
│
└── src/                                         <-- Main MCA Online Voting System
    ├── Main.java                                <-- App Entry (supports GUI and CLI)
    ├── model/
    │   ├── User.java                            <-- Base User Entity (Phase 1/2)
    │   ├── Voter.java                           <-- Extends User (Inheritance)
    │   └── Candidate.java                       <-- Candidate Entity
    ├── exception/
    │   ├── InvalidInputException.java           <-- Custom Exception (Phase 2)
    │   └── AuthenticationException.java         <-- Custom Auth Exception
    ├── validation/
    │   └── Validation.java                      <-- Pure String Handling (Phase 1)
    ├── service/
    │   ├── UserOperations.java                  <-- Interface (Phase 2)
    │   └── UserService.java                     <-- Business Logic & Validation
    ├── dao/
    │   ├── VoterDAO.java                        <-- Voter Database Access
    │   ├── AdminDAO.java                        <-- Admin Database Access
    │   ├── CandidateDAO.java                    <-- Candidate Database Access
    │   └── VoteDAO.java                         <-- Transactional Vote Persistence
    ├── db/
    │   └── DBConnection.java                    <-- MySQL JDBC Connection Manager
    ├── gui/
    │   ├── LoginFrame.java                      <-- Swing Login Screen
    │   ├── RegisterFrame.java                   <-- Swing Registration Screen
    │   ├── VoterDashboard.java                  <-- Candidate Voting Screen
    │   ├── AdminDashboard.java                  <-- Admin Candidate Management
    │   └── ResultFrame.java                     <-- Live Election Results Screen
    └── app/
        └── ConsoleApp.java                      <-- Interactive CLI for Lab Vivas
```

---

## 3. The WHAT: Components Breakdown

### Phase 1 Components (Core Application Setup & Design)
| Component | Class | What it does |
|-----------|-------|--------------|
| **User Entity** | `model.User` | Encapsulates user attributes: `username`, `password`, `role`. Contains constructors (default & parameterized), getter/setter methods, and `displayUser()` to print user details. |
| **Voter Entity** | `model.Voter` | Inherits from `User` (`extends User`). Adds voter-specific attributes: `voterCardNo`, `fullName`, `age`, `gender`, `hasVoted`. |
| **String Validation** | `validation.Validation` | Validates raw user input using string manipulation: checking null/empty (`trim()`, `isEmpty()`), minimum length (`length()`), numeric parsing, and digit verification (`Character.isDigit()`). |

### Phase 2 Components (Structured Programming & Reliability)
| Component | Class / Interface | What it does |
|-----------|-------------------|--------------|
| **Modular Packages** | `model`, `service`, `exception`, `validation`, `dao`, `gui`, `app` | Groups related classes together for clear separation of concerns, maintainability, and clean naming namespaces. |
| **Contract Interface** | `service.UserOperations` | An interface declaring the operations allowed on users: `validateUser()`, `login()`, `registerVoter()`, `loginVoter()`, `loginAdmin()`. |
| **Service Implementation** | `service.UserService` | Implements `UserOperations`. Validates input by delegating to `Validation`, throws `InvalidInputException` on bad data, and calls the DAO layer for persistence. Includes offline fallback session for vivas. |
| **Custom Checked Exception** | `exception.InvalidInputException` | Extends `java.lang.Exception`. Thrown when any user input violates business rules (e.g., username < 5 chars, password lacking digits, age < 18, passwords mismatch). |
| **Interactive Console App** | `app.ConsoleApp` | Menu-driven terminal runner allowing you or the examiner to test Phase 1, Phase 2, Voter Registration, and Logins directly from the console without launching Swing GUI. |

---

## 4. The WHY: Key Concepts Explained in Simple Words

### Why use Object-Oriented Programming (OOP)?
- **Encapsulation**: Private attributes (e.g. `private String password`) ensure sensitive data cannot be tampered with directly. All access happens through checked public getters and setters.
- **Inheritance (`Voter extends User`)**: Code reuse. Both an Admin and a Voter have credentials (`username`, `password`, `role`). `Voter` inherits these from `User` and adds voter-specific attributes like `voterCardNo` and `age`.
- **Polymorphism**: The `displayUser()` method in `Voter` overrides `displayUser()` in `User` to print voter-specific voting card info.

### Why do we need Packages?
- Without packages, every Java class lives in the same flat folder. As projects grow, class names collide and code becomes a tangled mess.
- Packages create a clean 3-tier architecture:
  - `model`: Holds data objects.
  - `validation` & `service`: Holds business logic & rules.
  - `dao` & `db`: Handles database persistence.
  - `gui` & `app`: Handles user presentation.

### Why use an Interface (`UserOperations`)?
- An interface is a **contract**. It defines *what* operations are possible without forcing *how* they are implemented.
- If tomorrow we change from a MySQL database to an Oracle database or a cloud API, the GUI (`LoginFrame`) doesn't need to change at all! It only interacts with the `UserOperations` interface.

### Why Custom Exceptions (`InvalidInputException`)?
- Without custom exceptions, programs either crash with unhelpful errors (`NullPointerException`, `IndexOutOfBoundsException`) or use messy return values (`return false`, `return -1`) which tell you *that* something failed, but not *why*.
- With `InvalidInputException`, the method throws an informative, typed error (`throw new InvalidInputException("Voter must be at least 18 years old");`). The calling code catches it in a `try-catch` block and displays the exact message to the user!

---

## 5. The HOW: How to Run the Project

### Option A: Running the Standalone Lab Modules (Exact Lab Manual Exercises)

#### 1. Compile all lab modules:
```powershell
# Phase 1
javac -d bin/lab1 lab_modules/phase1/*.java
# Phase 2
javac -d bin/lab2 (Get-ChildItem -Path lab_modules/phase2 -Recurse -Filter *.java -Exclude *Combined* | ForEach-Object { $_.FullName })
```

#### 2. Run Phase 1 Lab Exercise:
```powershell
java -cp bin/lab1 Main
```
*Sample Test:* Enter Username: `sheeja`, Password: `pass1234`.

#### 3. Run Phase 2 Lab Exercise:
```powershell
java -cp bin/lab2 app.Main
```
*Sample Test:* Enter Username: `admin`, Password: `admin123`.

---

### Option B: Running the Online Voting System

#### 1. Compile the entire project:
```powershell
javac -cp "lib/*" -d bin (Get-ChildItem -Path src -Recurse -Filter *.java | ForEach-Object { $_.FullName })
```

#### 2. Run in Interactive Console Mode (Recommended for Lab Viva & Demonstrations):
```powershell
java -cp "bin;lib/*" Main --cli
```
This launches the interactive main menu:
```
-------------------------- MAIN MENU --------------------------
1. [Phase 1 Demo] Core User Entity & String Validation
2. [Phase 2 Demo] Package Architecture, Interface & Exceptions
3. [Voter Portal] Register a New Voter (Console Mode)
4. [Voter Portal] Voter Login & View Profile
5. [Admin Portal] Administrator Login
6. [GUI Mode]     Launch Swing Graphical User Interface
7. Exit
```

#### 3. Run in Desktop GUI Mode (Swing):
```powershell
java -cp "bin;lib/*" Main
```
*(Or simply open `src/Main.java` in VS Code and press **F5**).*

---

## 6. Sample Test Cases (For Demonstrating in Viva)

### Test Case 1: Phase 1 Valid Input
- **Username**: `sheeja`
- **Password**: `pass1234`
- **Result**: `Registration Successful!`, prints User Details.

### Test Case 2: Phase 1 Invalid Username (< 5 characters)
- **Username**: `abc`
- **Password**: `pass1234`
- **Result**: `Username should contain at least 5 characters. Registration Failed.`

### Test Case 3: Phase 1 Invalid Password (No digit)
- **Username**: `sheeja`
- **Password**: `password`
- **Result**: `Password must contain at least one digit. Registration Failed.`

### Test Case 4: Phase 2 Custom Exception Trigger
- **Username**: `ab`
- **Password**: `admin123`
- **Result**: `[Caught Custom Exception] exception.InvalidInputException: Username must contain at least 5 characters.`

### Test Case 5: 6-Digit Voter ID Check (Letters rejected)
- **Voter ID**: `12345a`
- **Result**: `Registration Error: Voter ID must only contain numbers (0-9). Letters like 'a' are not allowed.`

### Test Case 6: Date of Birth Registration (Without 18+ restriction)
- **Voter ID**: `102030`
- **Date of Birth**: `10-05-2009` (Age 17)
- **Result**: `Registration successful! Voter profile created with Date of Birth: 10-05-2009, Age: 17.`

---

## 7. Viva Voce Cheat Sheet (Questions & Answers for MCA Exams)

**Q1: What are the main OOP concepts demonstrated in Phase 1?**
> **Answer**: Classes, objects, data encapsulation (private variables accessible only through getters/setters), constructors (default and parameterized), and string validation.

**Q2: How is inheritance implemented in this project?**
> **Answer**: The `Voter` class extends the `User` class (`class Voter extends User`). `User` holds general credentials (`username`, `password`, `role`), while `Voter` inherits them and adds voting-specific fields (`voterCardNo`, `age`, `gender`, `hasVoted`).

**Q3: What is the purpose of the `UserOperations` interface in Phase 2?**
> **Answer**: It defines the behavioral contract for user actions (`validateUser`, `login`, `registerVoter`) independent of the implementation (`UserService`). This achieves abstraction and loose coupling.

**Q4: Why did you create `InvalidInputException` instead of using standard Java exceptions?**
> **Answer**: It is a custom checked exception specific to our business domain. It allows methods to explicitly signal validation rule violations with clear messages, forcing callers to handle them gracefully using `try-catch` blocks.

**Q5: What string handling methods did you use for input validation?**
> **Answer**:
> - `trim()` to remove leading and trailing whitespace.
> - `isEmpty()` to check for blank input.
> - `length()` to check minimum character requirements.
> - `indexOf('@')` and `lastIndexOf('.')` to validate email structure.
> - `Character.isDigit(char)` to enforce password security (must contain at least one digit).

**Q6: How does the application prevent a voter from voting twice?**
> **Answer**: It has two layers of enforcement:
> 1. In application logic: The `has_voted` flag is checked upon login and immediately disabled in the GUI after casting a vote.
> 2. In the database: The `votes` table defines a `UNIQUE` constraint on `voter_id`, meaning the database engine will reject any duplicate vote attempts.
