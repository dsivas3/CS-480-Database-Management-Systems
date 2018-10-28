package assignment.database.connectivity;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class JDBCConnector {

  private Connection databaseConnection;

  private String databaseName;

  private String databaseUserName;

  private String databasePassword;

  private String databaseConnectionURL;

  public String getDatabaseUserName() {
    return databaseUserName;
  }

  public void setDatabaseUserName(String databaseUserName) {
    this.databaseUserName = databaseUserName;
  }

  public String getDatabaseConnectionURL() {
    return databaseConnectionURL;
  }

  public void setDatabaseConnectionURL(String databaseConnectionURL) {
    this.databaseConnectionURL = databaseConnectionURL;
  }

  public JDBCConnector(String db, String url, String user, String pass) {
    this.databaseConnection = null;
    this.databaseConnectionURL = url;
    this.databaseName = db;
    this.databaseUserName = user;
    this.databasePassword = pass;
  }

  public String getDatabaseName() {
    return databaseName;
  }

  public void setDatabaseName(String databaseName) {
    this.databaseName = databaseName;
  }

  public String getDatabasePassword() {
    return databasePassword;
  }

  public void setDatabasePassword(String databasePassword) {
    this.databasePassword = databasePassword;
  }

  public Connection getDatabaseConnection() {
    return databaseConnection;
  }

  public void setDatabaseConnection(Connection databaseConnection) {
    this.databaseConnection = databaseConnection;
  }

  public Connection getJDBCConnector() throws SQLException {
    this.databaseConnection =
        DriverManager.getConnection(
            this.databaseConnectionURL + "/" + this.databaseName,
            this.databaseUserName,
            this.databasePassword);
    return this.databaseConnection;
  }
}
