Put the MySQL Connector/J jar file in this folder.

Download link (official):
https://dev.mysql.com/downloads/connector/j/
-> choose "Platform Independent" -> download the .zip/.tar.gz -> take the
   mysql-connector-j-8.x.x.jar file from inside it and drop it right here
   in this "lib" folder.

(Or, if you have Maven/internet access on your own PC, you can just search
"mysql-connector-j" on https://mvnrepository.com and download the jar from there.)

Once the jar is inside this "lib" folder, VS Code will pick it up
automatically (this project's .vscode/settings.json already points to
lib/**/*.jar), and it will also show up in the Java Projects view.

Without this jar, the project will compile fine, but it will throw
"MySQL JDBC Driver not found" the moment it tries to connect to the
database.
