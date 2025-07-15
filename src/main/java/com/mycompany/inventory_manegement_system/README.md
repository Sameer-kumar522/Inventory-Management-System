Inventory Management System
A Java Swing-based GUI application that helps manage products in inventory with role-based access control and a lightweight SQLite database. Built as a desktop application — ideal for learning database integration and GUI design in Java.

 Features
✅ Add, update, and delete products

📋 View inventory in a sortable table

🔐 Role-based login system:

Admin – Full access (Add, Update, Delete)

Manager – Add & Update only (Delete disabled)

Supervisor – View only

 SQLite local database (auto-creates on first run)

 Input validation (no negative prices or quantities)

 Technologies Used
Java (Swing for GUI)

SQLite (via JDBC)

NetBeans IDE (initially built)

Git + GitHub for version control

🖥️ How to Run
Clone the Repository:

bash
Copy
Edit
git clone https://github.com/your-username/Inventory-Management-System.git
cd Inventory-Management-System
Open in Your IDE:

You can use NetBeans, VS Code, or IntelliJ.

Open the src folder and run Inventory_Manegement_System.java.

Login Credentials:

Admin:
Username: admin
Password: admin123

Manager:
Username: manager
Password: mng123

Supervisor:
Username: supervisor
Password: sv123

Database:
No setup needed! The inventory.db file is created automatically.


❗ Note
inventory.db is excluded using .gitignore for privacy and portability.

If the DB doesn't exist, the app will create it with the required products table.

 Author
Sameer Kumar
Java Developer & First-Year CS Student


