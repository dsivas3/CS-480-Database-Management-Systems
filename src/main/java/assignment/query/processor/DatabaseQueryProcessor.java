package assignment.query.processor;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import assignment.database.connectivity.JDBCConnector;
import assignment.file.reader.DataFileParser;

public class DatabaseQueryProcessor {

  private Connection databaseConnection;

  private DataFileParser dataFileParser;

  public DatabaseQueryProcessor(JDBCConnector conn, String fileName)
      throws FileNotFoundException, SQLException {
    this.databaseConnection = conn.getJDBCConnector();
    this.dataFileParser = new DataFileParser(fileName);
  }

  public void readQuery() throws IOException, SQLException {
    String instruction = null;
    this.startUp();
    while ((instruction = this.dataFileParser.getFileReader().readLine()) != null) {
      this.executeQuery(instruction);
    }
    this.shutDown();
  }

  private void executeQuery(String input) throws SQLException {
    Statement statement = databaseConnection.createStatement();
    StringTokenizer st = new StringTokenizer(input);
    int operationNumber = Integer.parseInt(st.nextToken());
    if (operationNumber == 1) {
      int eid = Integer.parseInt(st.nextToken());
      if (checkManagerIsAnEmployee(eid)) {
        statement.executeUpdate("delete from worksfor where eid=" + eid);
        statement.executeUpdate("delete from worksfor where mid=" + eid);
        statement.executeUpdate("delete from employee where eid=" + eid);
        System.out.println("done");
      } else {
        System.out.println("error");
      }
    } else if (operationNumber == 2) {
      int eid = Integer.parseInt(st.nextToken());
      String name = st.nextToken();
      int salary = Integer.parseInt(st.nextToken());
      int mid[] = new int[st.countTokens()];
      ArrayList<String> queries = new ArrayList<String>();
      queries.add("INSERT INTO employee VALUES(" + eid + ",'" + name + "'," + salary + ")");
      int arrayPos = 0;
      boolean errorFlag = false;
      while (st.hasMoreTokens() && !errorFlag) {
        mid[arrayPos] = Integer.parseInt(st.nextToken());
        if (!checkManagerIsAnEmployee(mid[arrayPos])) {
          System.out.println("error");
          errorFlag = true;
        } else {
          if (mid[arrayPos] != 0) queries.add(generateQueryForManager(eid, mid[arrayPos]));
        }
        arrayPos++;
      }

      if (!errorFlag) {
        for (String query : queries) {
          executeUpdateQueries(query);
        }
        System.out.println("done");
      }
    } else if (operationNumber == 3) {
      String query = "SELECT AVG(SALARY) from employee";
      ResultSet rs = executeQueries(query);
      rs.next();
      System.out.println(Math.floor(Double.parseDouble(rs.getString(1))));
    } else if (operationNumber == 4) {

      int mid = Integer.parseInt(st.nextToken());
      HashSet<String> managerReports = (HashSet<String>) findDirectAndIndeirectReports(mid);
      if (managerReports == null) {
        System.out.println("error");
      } else {
        for (String employeeName : managerReports) {
          System.out.println(employeeName);
        }
      }
    } else if (operationNumber == 5) {
      int mid = Integer.parseInt(st.nextToken());
      String query =
          "SELECT AVG(salary) AS AG FROM employee e1, worksfor w1 where w1.mid="
              + mid
              + " AND w1.mid=e1.eid GROUP BY w1.mid";
      ResultSet rs = executeQueries(query);
      if (!rs.next()) System.out.println("error");
      else System.out.println(Math.floor(Double.parseDouble(rs.getString(1))));

    } else if (operationNumber == 6) {
      String query =
          "SELECT e1.name from employee e1,worksfor w1 where e1.eid=w1.eid group by w1.eid having count(*)>1";
      ResultSet rs = executeQueries(query);
      if (!rs.next()) System.out.println("error");
      else {
        while (rs.next()) {
          System.out.println(rs.getString("name"));
        }
      }
    }
  }

  private boolean checkManagerIsAnEmployee(int mid) throws SQLException {
    PreparedStatement statement =
        databaseConnection.prepareStatement("select eid from employee where eid=" + mid);
    ResultSet result = statement.executeQuery();
    if (result.next() || mid == 0) return true;
    return false;
  }

  private void startUp() throws SQLException {
    executeUpdateQueries(
        "CREATE table employee(eid INTEGER(10), name CHARACTER(20), salary INTEGER(5), PRIMARY KEY(eid))");
    executeUpdateQueries(
        "CREATE table worksfor(eid INTEGER(10),mid INTEGER(10), FOREIGN KEY(eid) references employee(eid))");
  }

  private void shutDown() {
    executeUpdateQueries("drop table worksfor");
    executeUpdateQueries("drop table employee");
  }

  private String generateQueryForManager(int eid, int mid) {
    return "INSERT INTO worksfor values(" + eid + "," + mid + ")";
  }

  private void executeUpdateQueries(String query) {
    try {
      PreparedStatement statement = databaseConnection.prepareStatement(query);
      statement.executeUpdate(query);
    } catch (SQLException s) {
      System.out.println("error");
    }
  }

  private ResultSet executeQueries(String query) {
    ResultSet rs = null;
    try {
      PreparedStatement statement = databaseConnection.prepareStatement(query);
      rs = statement.executeQuery();
    } catch (SQLException s) {
      System.out.println("error");
    }
    return rs;
  }

  private Set<String> findDirectAndIndeirectReports(int mid) throws SQLException {
    if (!checkManagerIsAnEmployee(mid)) {
      return null;
    }
    List<Integer> managerList = new ArrayList<Integer>();
    managerList.add(mid);
    Set<String> employeeSet = new HashSet<String>();
    while (managerList.size() != 0) {
      PreparedStatement statement =
          databaseConnection.prepareStatement(
              "select eid from worksfor where mid=" + managerList.get(0));
      ResultSet rs = statement.executeQuery();
      managerList.remove(0);
      while (rs.next()) {
        managerList.add(rs.getInt(1));
        PreparedStatement s =
            databaseConnection.prepareStatement(
                "select name from employee where eid=" + rs.getInt(1));
        ResultSet r = s.executeQuery();
        r.next();
        employeeSet.add(r.getString(1));
      }
    }
    return employeeSet;
  }
}
