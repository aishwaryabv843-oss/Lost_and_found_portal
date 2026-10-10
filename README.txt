==========================================================================
              FINDME – COLLEGE LOST & FOUND PORTAL
                 HOW TO RUN & EXECUTE THE PROJECT
==========================================================================

This project consists of two companion deliverables designed for your 
4th-semester review:
1. A modern, futuristic Frontend Web Prototype with an interactive 
   database simulator.
2. A complete Java Swing Desktop Application connected to a MySQL backend.

Follow the instructions below to run and demonstrate each component.

--------------------------------------------------------------------------
PART 1: RUNNING THE FUTURISTIC WEB PROTOTYPE (HTML5 / CSS3 / JS)
--------------------------------------------------------------------------
The web prototype simulates the complete end-to-end database operations 
directly in your browser using local storage state managers.

1. Open the project folder:
   c:\Users\Aishu\OneDrive\Desktop\lostandfoundportal1

2. Launch the Login Gate:
   Double-click the "login.html" file to open it in any modern web browser 
   (Google Chrome, Microsoft Edge, Mozilla Firefox, Safari, etc.).

3. Choose a Session Role:
   - Click the "🎓 Student Login" tile to pre-populate student credentials 
     (USN: 1RV24CS015).
   - Click the "🛡️ Admin Login" tile to pre-populate administrator credentials 
     (Username: admin).

4. Authenticate & Enter:
   - Click the glowing "Sign In" button.
   - Experience the cybernetic progress ring loading, followed by the 
     green "Authentication Successful" glow popup.
   - You will be redirected directly to the fullscreen "index.html" 
     dashboard.

5. Test Operational Features:
   - [Search Directory]: Type keywords in the search bar or click tags to 
     instantly filter items.
   - [Report Lost Item]: Enter description details, click the file box to 
     upload a real image (file preview is handled instantly by FileReader), 
     and click Submit to sync to the mock database.
   - [Report Found Item]: Enter item details and safe campus coordinates.
   - [Filing Ownership Claims]:
     - If a student files an ownership claim on a "Found" item, a prompt 
       appears. Clicking OK verifies ownership and instantly purges/deletes 
       the item from all searchable catalog tables.
   - [Admin Verification]:
     - Log in as Admin to access the "Oversight Panel".
     - Under "Claim Verification", click "Verify Claim" to execute an 
       atomic mock JDBC delete transaction that purges the claim ticket 
       and matched item completely.
   - [Sign Out]: Click the "Sign Out" button in the header to safely exit.

--------------------------------------------------------------------------
PART 2: RUNNING THE JAVA SWING & MYSQL DATABASE COMPANION
--------------------------------------------------------------------------
The desktop client codebase is fully structured in standard Java Swing and JDBC.

Prerequisites:
- Java Development Kit (JDK 8 or higher) installed.
- MySQL Server installed and running locally.
- A Java IDE (Eclipse, IntelliJ IDEA, or NetBeans) OR command-line tools.
- MySQL JDBC Connector Driver jar (mysql-connector-j-x.x.x.jar) added to 
  your classpath.

Execution Steps:
1. Initialize the MySQL Database:
   - Start your MySQL server.
   - Run the "schema.sql" script inside your MySQL shell or query editor:
     mysql -u your_username -p < schema.sql
   - This database will create the normalized 'users', 'lost_items', 
     and 'claims' tables, and seed them with default user accounts.

2. Configure DB Connection Parameters:
   - Open: src/com/lostandfound/database/DBConnection.java
   - Edit the final string variables to match your local MySQL configuration:
     - private static final String URL = "jdbc:mysql://localhost:3306/lost_and_found_db";
     - private static final String USER = "your_mysql_username";
     - private static final String PASSWORD = "your_mysql_password";

3. Compile & Launch the Desktop Client:
   - Import the project folder into your Java IDE.
   - Locate and run the main entry point:
     src/com/lostandfound/ui/LoginFrame.java
   - Log in using your seed credentials (e.g. USN: 1RV24CS015, Password: password123).
   - Test full CRUD operations and verification claim purges matching 
     your web portal.

==========================================================================
Developed as part of the 4th Sem CSE DBMS & Advanced Java Project.
==========================================================================
