import java.sql.*;

public class Demo {
    public static void main(String[] args) {
        // Prepare a query to send to DB.
        String sql = "SELECT * FROM clients;";

        // Prepare connection to use in program. Must use DB url, username, and password.

        // url must be formatted like jdbc:postgresql://localhost:PORTNUMBER/DBNAME when on local
        String url = "jdbc:postgresql://localhost:5432/mowdata";
        String username = "postgres";
        String password = "Diamond2004";

        try{
            Connection con = DriverManager.getConnection(url, username, password);

            // Create statement (query). This will be returned as a result set.
            Statement statement = con.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);

            // Now imagine this output as a type of table. The first row is a row displaying the column titles.
            // To get to the next row, we must use .next()
            resultSet.next();

            // Now we can see our data using our desired column number. Column 0 is the given index
            System.out.print(resultSet.getString(2) + " " + resultSet.getString(3));
            // We must always close the connection.
            con.close();
        }
         catch (SQLException e) {
             System.out.println("UH OH!");
        }
    }
    /*
    Other notes:
    To change data (perform an INSERT , UPDATE , or DELETE), use the executeUpdate() method.
    This returns the amount of rows affected

    For things like time objects, you may use resultSet.getObject() to convert to a time.
     */
}
