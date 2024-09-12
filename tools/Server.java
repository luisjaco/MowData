package tools;

import java.sql.*;
import java.util.Scanner;
/**
 * The tools.Server class handles establishing and closing a connection to a PostgreSQL server. For each connection you must make a new
 * tools.Server instance.
 */
public class Server {
    private Connection connection;
    private final Scanner input;

    /**
     * Initializes a tools.Server instance. A tools.Server instance must run the establishConnection method to be functional.
     */
    public Server(){
        this.input = new Scanner(System.in);
    }

    /**
     * Establishes connection to server. Will prompt user for required values to establish connection. If the required tables are not
     * present the user will be prompted to add tables.
     * @return Whether connection was established.
     */
    public boolean establishConnection(){
        //Retrieve inputs from user until user puts a valid connection or exits.
        while (true){
            try {
                System.out.print("[1] Input the port number for your locally hosted server:");
                int portNumber = Menu.collectInt(1024, 65535, input);
                System.out.print("[2] Input the database name which you would like to access:");
                String databaseName = input.nextLine();
                String url = "jdbc:postgresql://localhost:%d/%s".formatted(portNumber, databaseName);

                System.out.print("[3] Input the user you would like to access this database as:");
                String user = input.nextLine();
                System.out.print("[4] Input the password for this user:");
                String password = input.nextLine();

                //Establish a connection with inputs.
                connection = DriverManager.getConnection(url, user, password);
                System.out.printf("\nSuccessfully established connection to '%s' on port %d as user '%s'!\n", databaseName, portNumber, user);
                break;
            } catch (SQLException e) {
                //Prompt user to try again if connection was not successful.
                System.out.println("[!] Error found when attempting to establish connection:\n" + e);

                System.out.print("""
                        [1] Try again.
                        [0] Exit.
                        
                        input:""");
                int choice = Menu.collectInt(0,1, input);
                if (choice == 0) return false;
            }
        }
        return true;
    }

    /**
     * Establishes connection to server. If the required tables are not present the user will be prompted to add tables.
     * @param port tools.Server port number.
     * @param database Database title.
     * @param username Database username.
     * @param password Password for user.
     * @return Whether connection was established.
     */
    public boolean establishConnection(int port, String database, String username, String password){
        //Create url with parameters.
        String url = "jdbc:postgresql://localhost:%d/%s".formatted(port, database);
        try {
            //Establish connection using url and password.
            connection = DriverManager.getConnection(url, username, password);
            System.out.printf("[!] Successfully established connection to '%s' on port %d as user '%s'!\n", database, port, username);
        } catch (SQLException e) {
            System.out.println("[!] Error found when attempting to establish connection:\n" + e);
            return false;
        }
        return true;
    }

    /**
     * Handles the creation of a tools.MowDataDB instance.
     * @return tools.MowDataDB instance.
     */
    public MowDataDB establishDatabase(){
        //Verify connection before creating tools.MowDataDB instance.
        if (!verifyConnection()) return null;
        return new MowDataDB(connection, input);
    }

    /**
     * Verifies if all the standard tables are present. (clients, states, cities, properties, services).
     * @return Whether standard tables are present.
     */
    public boolean verifyConnection(){
        try {
            if (connection == null){
                //Check if no connection was ever initialized.
                System.out.println("[!] Connection is non-existent.");
                return false;
            }
            else if (connection.isClosed()){
                System.out.println("[!] Connection is closed.");
                return false;
            }
            else {
                return true;
            }
        } catch (SQLException e) {
            System.out.println("[!] An error occurred while verifying the status of the servers connection:\n" + e);
            return false;
        }
    }

    /**
     * Closes all tools.Server objects currently in use (Connection, Scanner).
     */
    public void closeServer(){
        //Close connection is exists.
        try {
            if (verifyConnection()){
                connection.close();
                System.out.println("[!] Connection closed.");
            }
        } catch (SQLException e) {
            System.out.println("[!] An error occurred while attempting to close the connection:\n" + e);
        }
        //Close input Scanner.
        input.close();
    }
}