import java.sql.*;
import java.util.Scanner;
/**
 * The server class is used to handle and control all queries and actions regarding a PostgreSQL server and database.
 */
public class Server {
    private Connection connection;
    private Scanner input;

    /**
     * Initializes a Server instance. A Server instance must run the establishConnection method to be functional.
     */
    public Server(){
        this.input = new Scanner(System.in);
    }

    /**
     * Prompt user for inputs to establish a connection to a locally hosted PostgreSQL server. Once connection established,
     * ask the user if they require standard tables or sample data to be inserted.
     * @return false if user decides to quit. true if connection is successful.
     */
    public boolean establishConnection(){
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

                connection = DriverManager.getConnection(url, user, password);
                System.out.printf("\nSuccessfully established connection to '%s' on port %d as user '%s'!\n", databaseName, portNumber, user);
                break;
            } catch (SQLException e) {
                System.out.println("[!] Error found:\n" + e);

                System.out.print("""
                        [1] Try again.
                        [0] Exit.
                        
                        input:""");
                int choice = Menu.collectInt(0,1, input);
                switch (choice){
                    case 1 -> {
                        //Do nothing, loop will continue.
                    }
                    case 0 -> {
                        //Close input as a new server
                        return false;
                    }
                }
            }
        }
        //After connection is established, prompt user if they would like to populate their database.
        populateServer();
        return true;
    }
    public boolean verifyConnection(){
        try {
            if (connection == null){
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
    private void closeStatement(Statement st){
        try {
            if (st != null){
                st.close();
            }
        } catch (SQLException e) {
            System.out.println("[!] Something went wrong when closing a statement:\n" + e);
        }
    }
    public void closeConnection(){
        try {
            if (verifyConnection()){
                connection.close();
                System.out.println("[!] Connection closed.");
            }
        } catch (SQLException e) {
            System.out.println("[!] An error occurred while attempting to close the connection:\n" + e);
        }
    }
    private void populateServer(){
        int choice;
        System.out.print("""
               [!] This program will not function as intended without the required tables.
               If your database is not yet compatible, you must add tables.
               Sample data is recommended to view program functionality.
               What would you like to do?:
               
               [2] Add tables.
               [1] Add tables and sample data.
               [0] Database compatible, continue.
               
               input:""");
        choice = Menu.collectInt(0,2, input);
        switch (choice) {
            case 2 ->{
                createTables();
                insertStates();
            }
            case 1 ->{
                createTables();
                insertStates();
                insertSampleData();
            }
            case 0 ->{
                //Do nothing.
            }
        }
    }
    private void createTables() {
        Statement st = null;
        String sql = """
                CREATE TABLE clients(
                	id SERIAL PRIMARY KEY,
                	first_name VARCHAR(50) NOT NULL,
                	last_name VARCHAR(50) NOT NULL,
                	phone VARCHAR(10) NOT NULL,
                	email VARCHAR(50) NOT NULL,
                 	CHECK (length(phone) = 10)
                );
                CREATE TABLE states(
                	id SERIAL PRIMARY KEY,
                	abbreviation VARCHAR(2) NOT NULL,
                	name VARCHAR(50) NOT NULL,
                	CHECK (length(abbreviation) = 2)
                );
                CREATE TABLE cities(
                    id SERIAL PRIMARY KEY,
                    name VARCHAR(50) NOT NULL,
                    zip VARCHAR(5) NOT NULL,
                    state_id INTEGER NOT NULL,
                    CHECK (length(zip) = 5),
                    FOREIGN KEY (state_id)
                        REFERENCES states(id)
                        ON DELETE RESTRICT
                        ON UPDATE CASCADE
                );
                CREATE TABLE properties(
                	id SERIAL PRIMARY KEY,
                	client_id INTEGER NOT NULL,
                	address VARCHAR(50) NOT NULL,
                	city_id INTEGER NOT NULL,
                	FOREIGN KEY (client_id)
                		REFERENCES clients(id)
                		ON DELETE CASCADE
                		ON UPDATE CASCADE,
                	FOREIGN KEY (city_id)
                		REFERENCES cities(id)
                		ON DELETE CASCADE
                		ON UPDATE CASCADE
                );
                CREATE TABLE services(
                	id SERIAL PRIMARY KEY,
                	property_id INTEGER NOT NULL,
                	service_date DATE NOT NULL,
                	service_cost NUMERIC(5, 2) NOT NULL,
                	mow BOOLEAN NOT NULL,
                	leaf_blow BOOLEAN NOT NULL,
                	seed BOOLEAN NOT NULL,
                	fertilizer BOOLEAN NOT NULL,
                	mulch BOOLEAN NOT NULL,
                	remove_tree BOOLEAN NOT NULL,
                	trim_tree BOOLEAN NOT NULL,
                	power_wash BOOLEAN NOT NULL,
                	snow_plow BOOLEAN NOT NULL,
                	notes TEXT,
                	FOREIGN KEY (property_id)
                		REFERENCES properties(id)
                		ON DELETE SET NULL
                		ON UPDATE CASCADE
                );""";
        try{
            st = connection.createStatement();
            st.executeUpdate(sql);
            System.out.println("[!] Successfully created tables.");
        } catch (SQLException e) {
            System.out.println("[!] Error found when attempting to create database tables:\n" + e);
        }
        finally {
            closeStatement(st);
        }
    }
    private void insertStates() {
        Statement st = null;
        StringBuilder sql = new StringBuilder();
        String[][] statesList = { {"AL", "Alabama"}, {"AK", "Alaska"}, {"AZ", "Arizona"}, {"AR", "Arkansas"},
                {"CA", "California"}, {"CO", "Colorado"}, {"CT", "Connecticut"}, {"DE", "Delaware"}, {"FL", "Florida"},
                {"GA", "Georgia"}, {"HI", "Hawaii"}, {"ID", "Idaho"}, {"IL", "Illinois"}, {"IN", "Indiana"},
                {"IA", "Iowa"}, {"KS", "Kansas"}, {"KY", "Kentucky"}, {"LA", "Louisiana"}, {"ME", "Maine"},
                {"MD", "Maryland"}, {"MA", "Massachusetts"}, {"MI", "Michigan"}, {"MN", "Minnesota"},
                {"MS", "Mississippi"}, {"MO", "Missouri"}, {"MT", "Montana"}, {"NE", "Nebraska"}, {"NV", "Nevada"},
                {"NH", "New Hampshire"}, {"NJ", "New Jersey"}, {"NM", "New Mexico"}, {"NY", "New York"},
                {"NC", "North Carolina"}, {"ND", "North Dakota"}, {"OH", "Ohio"}, {"OK", "Oklahoma"},
                {"OR", "Oregon"}, {"PA", "Pennsylvania"}, {"RI", "Rhode Island"}, {"SC", "South Carolina"},
                {"SD", "South Dakota"}, {"TN", "Tennessee"}, {"TX", "Texas"}, {"UT", "Utah"}, {"VT", "Vermont"},
                {"VA", "Virginia"}, {"WA", "Washington"}, {"WV", "West Virginia"}, {"WI", "Wisconsin"}, {"WY", "Wyoming"}
        };
        for (String[] strings : statesList) {
            sql.append("""
                        INSERT INTO states (abbreviation, name)
                        VALUES (
                        	'%s',
                        	'%s'
                        );
                        """.formatted(strings[0], strings[1]));
        }
        try {
            st = connection.createStatement();
            st.executeUpdate(sql.toString());
            System.out.println("[!] Successfully populated states table with standard data.");
        } catch (SQLException e) {
            System.out.println("[!] Error found when attempting to populate the states table:\n" + e);
        }finally {
            closeStatement(st);
        }
    }
    private void insertSampleData(){
        Statement st = null;
        String sql = """
                INSERT INTO clients (first_name, last_name, phone, email)
                VALUES
                	('luis', 'jaco', '1234567890', 'luisjaco@fake.com'), --id 1
                	('jim', 'bob', '8888888888', 'jimbob@guy.com'); --id 2
                INSERT INTO cities (name, zip, state_id)
                VALUES
                	('hicksville', 11801, 32), --id 1
                	('levittown', 11756, 32);
                INSERT INTO properties (client_id, address, city_id)
                VALUES
                	(1, '57 apple ln', 1), --id 1
                	(1, '38 orange rd', 2),
                	(2, '58 apple ln', 1);
                INSERT INTO services (property_id, service_date, service_cost,
                					 mow, leaf_blow, seed,
                					 fertilizer, mulch, remove_tree,
                					 trim_tree, power_wash, snow_plow,
                					 notes)
                VALUES
                	(1, '2024-08-05', 400.00,
                	true, false, false,
                	true, false, true,
                	true, false, false,
                	'dead tree on property, ask client for removal'),
                	(3, '2024-08-13', 90.00,
                	true, false, false,
                	true, false, false,
                	false, false, false,
                	null);""";
        try {
            st = connection.createStatement();
            st.executeUpdate(sql);
            System.out.println("[!] Successfully inserted sample data to all tables.");
        } catch (SQLException e) {
            System.out.println("[!] An error occured while inserting sample data:\n" + e);
        } finally {
            closeStatement(st);
        }
    }
}
