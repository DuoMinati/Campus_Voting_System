# Campus Election Commission - Online Voting System
### Enterprise-Grade Electronic Voting & Election Management Platform (Java & Web)

A comprehensive digital election system engineered with Java (Swing & Embedded HTTP Server), MySQL, and a modern Web Portal. Supports student voter registration, identity validation, secret ballot casting, real-time vote tallying, individual ballot revocation, and live analytical standings.

---

## 1. Modules

The project is split into two clear modules, just like a real voting
app would be:

### Admin Module
- Secure login (`admin` / `admin123` by default)
- Add new candidates (name, party, symbol)
- Delete a candidate
- See total registered voters and total votes cast so far
- Open the live results screen

### Voter (User) Module
- Register as a new voter (name, voter card no., email, age, gender, password)
- Age check - only 18+ can register
- Login with email + password
- See the list of candidates and cast exactly ONE vote
- Once a vote is cast, the voter cannot vote again (this is enforced
  both in the GUI and in the database itself)

---

## 2. Tech used

| Layer        | Technology                     |
|--------------|---------------------------------|
| Language     | Java (JDK 17 or higher)         |
| GUI          | Java Swing                      |
| Database     | MySQL 8                         |
| DB access    | JDBC (mysql-connector-j)        |
| Editor       | Visual Studio Code              |

---

## 3. Project structure

```
OnlineVotingSystem/
├── .vscode/                 -> VS Code run config (already set up)
│   ├── launch.json
│   ├── settings.json
│   └── extensions.json
├── database/
│   └── voting_system.sql    -> run this first in MySQL
├── lib/
│   └── (put mysql-connector-j jar here - see lib/README_PUT_JAR_HERE.txt)
├── src/
│   ├── Main.java            -> entry point
│   ├── db/DBConnection.java -> JDBC connection helper
│   ├── model/                -> Voter.java, Candidate.java
│   ├── dao/                  -> VoterDAO, CandidateDAO, VoteDAO, AdminDAO
│   └── gui/                  -> LoginFrame, RegisterFrame, VoterDashboard,
│                                 AdminDashboard, ResultFrame
└── README.md
```

This is the standard layout the VS Code Java extension expects
(source in `src`, compiled `.class` files go into `bin`, jars in `lib`),
so it works right out of the box.

---

## 4. Setup - step by step

### Step 1: Install requirements on your PC
1. **JDK 17+** - https://adoptium.net
2. **MySQL Server** (MySQL Workbench or XAMPP, either is fine)
3. **VS Code** with the **Extension Pack for Java** (by Microsoft).
   VS Code will actually prompt you to install this automatically the
   first time you open a `.java` file - just click Install.

### Step 2: Create the database
1. Open MySQL Workbench (or `mysql` command line).
2. Open `database/voting_system.sql` from this project.
3. Run the whole script. It will:
   - create the `voting_system` database
   - create all 5 tables (admin, voters, candidates, votes, election_status)
   - insert a default admin login and 3 sample candidates

### Step 3: Add the MySQL JDBC driver jar
1. Download `mysql-connector-j` (the jar) from
   https://dev.mysql.com/downloads/connector/j/  (or mvnrepository.com)
2. Drop the `mysql-connector-j-x.x.x.jar` file into the `lib` folder
   of this project.
3. That's it - `.vscode/settings.json` already tells VS Code to pick up
   any jar inside `lib/`.

### Step 4: Set your MySQL password
Open `src/db/DBConnection.java` and change this line to match your own
MySQL login:
```java
private static final String DB_PASSWORD = "root"; // <-- put your own MySQL password here
```

### Step 5: Open the folder in VS Code and run
1. `File -> Open Folder...` -> select the `OnlineVotingSystem` folder.
2. Wait a few seconds for the Java extension to finish loading the project
   (you'll see "Java: Ready" in the bottom status bar).
3. Open `src/Main.java`.
4. Press **F5** (or click the "Run" ▷ button above `public static void main`).
5. The login window should pop up.

If F5 doesn't work the first time, just click the ▷ "Run" code-lens that
appears right above `public static void main(String[] args)` in `Main.java`.

---

## 5. Default login

**Admin**
- Username: `admin`
- Password: `admin123`

**Voter**
- Register a new account from the login screen first, then log in with
  the email + password you used.

---

## 6. How the "vote only once" rule works

There are actually two layers of protection, on purpose (this is the
kind of detail that's good to mention in a viva/project defense):

1. The `voters` table has a `has_voted` flag. The GUI checks this at
   login and again disables the vote button after voting.
2. The `votes` table has a `UNIQUE` constraint on `voter_id`. So even if
   somehow the GUI check was bypassed, the database itself would refuse
   to insert a second vote row for the same voter.

Casting a vote also updates 3 things together (the `votes` table, the
candidate's vote count, and the voter's `has_voted` flag) inside a single
JDBC transaction in `VoteDAO.java`, so none of them can end up out of
sync with the others.

---

## 7. Notes

- This is a learning/college project, so the password storage is kept
  simple (plain text) to keep the SQL and JDBC code easy to read and
  explain. In a real production system you would hash passwords
  (e.g. with BCrypt) before saving them.
- Feel free to change the 3 sample candidates in the SQL file, or add
  your own straight from the Admin Dashboard once the app is running.
