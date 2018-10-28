package assignment.main.application;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;

import assignment.database.connectivity.JDBCConnector;
import assignment.query.processor.DatabaseQueryProcessor;

public class Application {

  public static void main(String args[]) throws IOException, SQLException, FileNotFoundException {
    JDBCConnector jdbc = new JDBCConnector("Homework4", "jdbc:mysql://localhost:3306", "root", "");
    DatabaseQueryProcessor queryProcessor = new DatabaseQueryProcessor(jdbc, "transfile");
    queryProcessor.readQuery();
  }
}
