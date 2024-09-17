package com.github.luisjaco.tools.server;

import com.github.luisjaco.tools.Menu;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * The tools.MowDataDB class is a class to be used along with a connection. It can perform all queries related to a PostgreSQL database with the
 * proper tables for a MowData database.
 */
public class MowDataDB {
    private final Connection connection;
    private final Scanner input;

    /**
     * Initializes a new tools.MowDataDB instance. When initialized, MowData database tables will be queried and validated. If proper
     * tables aren't present, the user will be prompted to add the required tables.
     * @param connection Connection to PostgreSQL server.
     * @param input Scanner to be used for user input.
     */
    protected MowDataDB(Connection connection, Scanner input){
        this.connection = connection;
        this.input = input;

        //Once database connection from server is established, tables will be checked to ensure they are
        //the proper tables for the MowData database.
        if (!verifyTables()) populateServer();
    }

    public boolean verifyConnection(){
        try {
            if (connection == null){
                //Verify connection object exists.
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
     * Will perform a query [view] and return the ResultSet. Handles errors.
     * @param sql Query to perform.
     * @param errorFrom Used in error message. Will be printed as "[!] Error occurred while attempting to {errorFrom}".
     * @return ResultSet of performed query, or null if error occurred.
     */
    public ResultSet performQuery(String sql, String errorFrom){
        //Won't attempt if there is no connection
        if (!verifyConnection()) return null;

        ResultSet rs = null;
        try {
            Statement st = connection.createStatement();
            rs = st.executeQuery(sql);
            //Statement and ResultSet will close when they are done being used. (Statement must be open for ResultSet to be open).
            return rs;
        } catch (SQLException e) {
            System.out.printf("[!] Error occurred while attempting to %s:\n%s", errorFrom, e);
        }
        return rs;

        /*
        Boilerplate code for method use:
        //Processing results.
        ResultSet rs = performQuery();
        if (rs == null) return;
        try {
            // CODE TO PROCESS RESULT SET.
            rs.close();
        } catch (SQLException e) {
            //Should not occur given ResultSet is not null.
        }
         */
    }

    /**
     * Will perform a query [insert, delete] and return whether the query was successful. Handles errors.
     * @param sql Query to perform.
     * @param errorFrom Used in error message. Will be printed as "[!] Error occurred while attempting to {errorFrom}".
     * @return Query successful.
     */
    public boolean performUpdate(String sql, String errorFrom){
        //Won't attempt if there is no connection
        if (!verifyConnection()) return false;
        boolean result = false;

        try {
            Statement st = connection.createStatement();
            //If a value 'n' of one or more is returned, it means the query altered 'n' rows without error.
            result = (st.executeUpdate(sql) >= 1);
            st.close();
        } catch (SQLException e) {
            System.out.printf("[!] Error occurred while attempting to %s:\n%s", errorFrom, e);
        }
        return result;

        /*
        Boilerplate code for method use:
        if (performUpdate()) {
            //SUCCESS MESSAGE
        }
         */
    }

    /**
     * Will print n rows of the services table as standard, sorted by property, or sorted by date.
     * @param sortingMode "all" for no sorting, "property" for sorted by property, and "date" for sorted
     * by date. Default is "all".
     * @param n Number of rows to display, -1 for all rows.
     */
    public void viewServices(String sortingMode, int n){
        /*
        Table data will return in the format of:
        (row id, column 0) | service_id (column 1) | property_id (column 2)...
         */
        int counter = 0;
        String sql = """
                SELECT
                	services.id as service_id, --id 1
                	property_id,
                	properties.address,
                	cities.name,
                	cities.zip,
                	states.abbreviation,
                	service_date,
                	service_cost,
                	mow, --id 9
                	leaf_blow,
                	seed,
                	fertilizer,
                	mulch,
                	remove_tree,
                	trim_tree,
                	power_wash,
                	snow_plow, --id 17
                	notes
                FROM services
                JOIN properties
                ON property_id = properties.id
                JOIN cities
                ON properties.city_id = cities.id
                JOIN states
                ON cities.state_id = states.id""";
        //Adjusting SQL based on sortingMode.
        switch (sortingMode) {
            case "all" -> sql += ";";
            case "property" -> sql += "\nORDER BY properties.id ASC;";
            case "date" -> sql += "\nORDER BY service_date DESC;";
            default -> {
                System.out.println("Invalid sortingMode given. Defaulting to \"all\".");
                sql += ";";
            }
        }

        //Processing results.
        ResultSet rs = performQuery(sql, "view services table");
        if (rs == null) return;
        try {
            System.out.println("[!] Now displaying service history:");
            while (rs.next() && (counter < n || n == -1)){
                //Convert boolean values to strings which say YES or NO. Booleans are from column id's 9-17.
                ArrayList<String> boolWords = new ArrayList<>();
                for (int i=9; i <= 17; i++){
                    boolWords.add(rs.getBoolean(i) ? "YES" : "NO ");
                }
                System.out.printf("""
                        
                        [SERVICE ID#%d]
                        SERVICE AT %s, %s, %s %d [PROPERTY ID#%d] ON %s
                        MOW..........%s |   LEAF BLOW....%s |   SEED...........%s
                        FERTILIZER...%s |   MULCH........%s |   TREE REMOVAL...%s
                        TREE TRIM....%s |   POWER WASH...%s |   SNOW PLOW......%s
                        NOTES: %s
                        COST......................$%.2f
                        """,
                        rs.getInt(1),
                        rs.getString(3),
                        rs.getString(4),
                        rs.getString(6),
                        rs.getInt(5),
                        rs.getInt(2),
                        rs.getDate(7),
                        boolWords.get(0), boolWords.get(1), boolWords.get(2),
                        boolWords.get(3), boolWords.get(4), boolWords.get(5),
                        boolWords.get(6), boolWords.get(7), boolWords.get(8),
                        rs.getString(18),
                        rs.getDouble(8));
                counter++;
            }
            rs.close();
        } catch (SQLException e) {
            //Should not occur given ResultSet is not null.
        }
    }

    /**
     * Will print n rows of the properties table as standard, sorted by city, or sorted by client.
     * @param sortingMode "all" for no sorting, "city" for sorted by city, "client" for sorted by client. Default is "all".
     * @param n Number of rows to display, -1 for all rows.
     */
    public void viewProperties(String sortingMode, int n){
        /*
        Table will return in form of:
        (row id, id 0) | property_id (id 1) | client_id (id 2) ...
         */
        int counter = 0;
        String sql = """
                SELECT
                	properties.id as property_id, --id 1
                	client_id,
                	clients.first_name,
                	clients.last_name,
                	address,
                	city_id,
                	cities.name,
                	cities.zip,
                	states.abbreviation
                FROM properties
                JOIN clients
                ON client_id = clients.id
                JOIN cities
                ON city_id = cities.id
                JOIN states
                ON cities.state_id = states.id""";

        //Changing SQL for sortingMode.
        switch (sortingMode){
            case "all" -> sql += ";";
            case "city" -> sql += "\nORDER BY city_id ASC;";
            case "client" -> sql += "\nORDER BY client_id ASC;";
            default -> {
                System.out.println("Invalid sortingMode given. Defaulting to \"all\".");
                sql += ";";
            }
        }

        //Processing results.
        ResultSet rs = performQuery(sql, "view properties table");
        if (rs == null) return;
        try {
            System.out.println("[!] Now displaying properties data:");
            while (rs.next() && (counter < n || n == -1)){
                System.out.printf("""
                        
                        [PROPERTY ID#%d]
                        ADDRESS: %s | CITY: %s %d [CITY ID#%d] | STATE: %s
                        OWNER: %s %s [OWNER ID#%d]
                        """,
                        rs.getInt(1),
                        rs.getString(5),
                        rs.getString(7),
                        rs.getInt(8),
                        rs.getInt(6),
                        rs.getString(9),
                        rs.getString(3),
                        rs.getString(4),
                        rs.getInt(2));
                counter++;
            }
            rs.close();
        } catch (SQLException e){
            //Should not occur if ResultSet is not null.
        }
    }

    /**
     * Will print n rows of the cities table as standard or sorted by state.
     * @param sortingMode "all" for no sorting, "state" for sorted by state, "name" for sorted by name. Default is "all".
     * @param n Number of rows to display, -1 for all rows.
     */
    public void viewCities(String sortingMode, int n){
        /*
        Table will look like:
        (row id #0) | city_id (#1) | name (#2)...
         */
        int counter = 0;
        String sql = """
                SELECT
                	cities.id as city_id, --id 1
                	cities.name,
                	zip,
                	states.id as state_id,
                	states.name
                FROM cities
                JOIN states
                ON cities.state_id=states.id""";

        //Adjusting SQL depending on sortingMode.
        switch (sortingMode){
            case "all" -> sql += ";";
            case "state" -> sql += "\nORDER BY state_id;";
            case "name" -> sql += "\n ORDER BY cities.name ASC;";
            default -> {
                System.out.println("Invalid sortingMode given, defaulting to \"all\".");
                sql += ";";
            }
        }

        //Processing results.
        ResultSet rs = performQuery(sql, "view cities table");
        if (rs == null) return;
        try {
            System.out.println("[!] Now displaying cities data:");
            while (rs.next() && (counter < n || n == -1)) {
                System.out.printf("""
                                                
                        [CITY ID#%d]
                        CITY: %s | STATE: %s [STATE ID#%d] | ZIP: %d
                        """,
                        rs.getInt(1),
                        rs.getString(2),
                        rs.getString(5),
                        rs.getInt(4),
                        rs.getInt(3));
                counter++;
            }
            rs.close();
        } catch (SQLException e) {
            //Should not occur given ResultSet is not null.
        }
    }

    /**
     * Will print n rows of the clients table as standard or sorted by name.
     * @param sortingMode "all" for no sorting, "name" for sorted by name. Default is "all".
     * @param n Number of rows to display. -1 for all rows.
     */
    public void viewClients(String sortingMode, int n){
        /*
        Table will return as:
        (row id #0) | client_id (#1) | first_name (#2) | last_name (#3) | phone (#4) | email (#5)
         */
        int counter = 0;
        String sql = "SELECT * FROM clients";

        //Adjusting SQL for sortingMode.
        switch (sortingMode) {
            case "all" -> sql += ";";
            case "name" -> sql += "\nORDER BY first_name ASC, last_name ASC;";
            default -> {
                System.out.println("[!] Invalid sortingMode given. Defaulting to \"all\".");
                sql += ";";
            }
        }

        //Processing results.
        ResultSet rs = performQuery(sql, "view clients table");
        if (rs == null) return;
        try {
            System.out.println("[!] Now displaying clients data:");
            while (rs.next() && (counter < n || n == -1)){
                String phoneNumber = rs.getString(4);
                String formattedPhoneNumber = "("
                        + phoneNumber.substring(0,3) + ") "
                        + phoneNumber.substring(3, 6) + "-"
                        + phoneNumber.substring(6, 10);
                System.out.printf("""
                        
                        [CLIENT ID#%d]
                        NAME: %s %s
                        PHONE: %s
                        EMAIL: %s
                        """,
                        rs.getInt(1),
                        rs.getString(2),
                        rs.getString(3),
                        formattedPhoneNumber,
                        rs.getString(5));
                counter++;
            }
            rs.close();
        } catch (SQLException e) {
            //Should not occur given ResultSet is not null.
        }
    }

    /**
     * Will insert a new service entry into the services table.
     * @param propertyID ID of property used in service.
     * @param date Service date.
     * @param servicesDone A list with boolean values for each service performed: [mow, leaf blow, seed, fertilizer, mulch,
     *                     tree removal, tree trim, power wash, snow plow].
     * @param cost Cost of service.
     * @param notes Notes for services. A value of "" should be used when there are no notes.
     * @param confirm Whether to confirm the service before performing update. Will print the service card to display.
     */
    public void addService(int propertyID, LocalDate date, boolean[] servicesDone, double cost, String notes, boolean confirm){
        //If notes is empty, we will put null as the value for the table.
        if (notes.length() == 0) {
            notes = "null";
        } else {
            notes = "'" + notes + "'";
        }

        //Inserting fields into SQL query.
        String sql = """
                INSERT INTO services (property_id, service_date, service_cost,
                					 mow, leaf_blow, seed,
                					 fertilizer, mulch, remove_tree,
                					 trim_tree, power_wash, snow_plow,
                					 notes)
                VALUES
                	(%d, '%tF', %f,
                	%b, %b, %b,
                	%b, %b, %b,
                	%b, %b, %b,
                	%s);"""
                .formatted(propertyID,
                        date,
                        cost,
                        servicesDone[0],
                        servicesDone[1],
                        servicesDone[2],
                        servicesDone[3],
                        servicesDone[4],
                        servicesDone[5],
                        servicesDone[6],
                        servicesDone[7],
                        servicesDone[8],
                        notes
                );

        //Displays and prompts user to confirm service addition, if applicable.
        if (confirm) {
            ArrayList<String> boolWords = new ArrayList<>();
            for (int i=0; i <= 8; i++){
                boolWords.add(servicesDone[i] ? "YES" : "NO ");
            }

            System.out.printf("""
                     [!] Now displaying service card.
                     
                     [SERVICE AT PROPERTY ID#%d ON %tF]
                     MOW..........%s |   LEAF BLOW....%s |   SEED...........%s
                     FERTILIZER...%s |   MULCH........%s |   TREE REMOVAL...%s
                     TREE TRIM....%s |   POWER WASH...%s |   SNOW PLOW......%s
                     NOTES: %s
                     COST......................$%.2f
                     
                     """,
                    propertyID,
                    date,
                    boolWords.get(0),
                    boolWords.get(1),
                    boolWords.get(2),
                    boolWords.get(3),
                    boolWords.get(4),
                    boolWords.get(5),
                    boolWords.get(6),
                    boolWords.get(7),
                    boolWords.get(8),
                    notes,
                    cost);
            System.out.print("""
                    Would you like to add this service to the services table?:
                    
                    [1] Yes.
                    [0] No.
                    
                    input:""");
            //If user selects 'no' exit method. Else continue and perform query.
            if (Menu.collectInt(0, 1, input) == 0) {
                System.out.println("[!] Service not added.");
                return;
            }
        }

        if (performUpdate(sql, "add service")) {
            System.out.println("[!] Successfully added service.");
        } else {
            System.out.println("[!] Error occurred. Service not added.");
        }
    }

    /**
     * Will insert a new property entry into the properties table.
     * @param clientID Client ID, owner of property.
     * @param address Address of property. Proper format is all lowercase (ex: 123 apple rd).
     * @param cityID City ID.
     * @param confirm Whether to confirm the property before performing update. Will print the property card to user.
     */
    public void addProperty(int clientID, String address, int cityID, boolean confirm){
        String sql = """
                INSERT INTO properties (client_id, address, city_id)
                	VALUES (
                	%d,
                	'%s',
                	%d
                );""".formatted(clientID, address, cityID);

        //Will display property card to user and confirm if they will add the property.
        if (confirm) {
            System.out.printf("""
                    [!] Now displaying property card.
                    
                    [PROPERTY AT %s]
                    OWNED BY CLIENT ID #%d
                    ADDRESS IS IN CITY #%d
                    
                    Would you like to add this property to the properties tables?
                    
                    [1] Yes.
                    [0] No.
                    
                    input:""", address, clientID, cityID);
            int choice = Menu.collectInt(0, 1, input);
            //If user decides no, do not add the property.
            if (choice == 0) return;
        }

        //Execute query.
        if (performUpdate(sql, "add property")) {
            System.out.println("[!] Successfully added property.");
        } else {
            System.out.println("[!] Error occurred. Property not added.");
        }
    }

    /**
     * Will insert a new city entry into the cities table.
     * @param name City name.
     * @param zip City zip code.
     * @param stateID City state id.
     * @param confirm Whether to confirm the city before performing update. Will print city card to user.
     */
    public void addCity(String name, String zip, int stateID, boolean confirm){
        //Ensuring proper format.
        name = name.toLowerCase();

        String sql = """
                INSERT INTO cities (name, zip, state_id)
                VALUES
                ('%s',
                '%s',
                %d);
                """.formatted(name, zip, stateID);

        //Displays city card and verifies with user.
        if (confirm) {
            System.out.printf("""
                    [!] Now displaying city card.
                    
                    [CITY '%s']
                    ZIP %s
                    STATE ID#%d
                    
                    Would you like to add this city to the cities table?
                    
                    [1] Yes.
                    [0] No.
                    
                    input:""",
                    name,
                    zip,
                    stateID);
            //Exit if user inputs no.
            if (Menu.collectInt(0, 1, input) == 0) return;
        }

        //Execute query:
        if (performUpdate(sql, "add city")) {
            System.out.println("[!] Successfully added city.");
        } else {
            System.out.println("[!] Error occurred. City not added.");
        }
    }

    /**
     * Will insert a new client entry into the clients table.
     * @param firstName Client first name. Proper format is all lowercase.
     * @param lastName Client last name. Proper format is all lowercase.
     * @param phoneNumber Client phone number. (ex: 1234566890).
     * @param email Client email. Proper format is all lowercase.
     * @param confirm Whether to confirm client before performing update. Will print client card to user.
     */
    public void addClient(String firstName, String lastName, String phoneNumber, String email, boolean confirm){
        //Ensure correct format
        firstName = firstName.toLowerCase();
        lastName = lastName.toLowerCase();
        email = email.toLowerCase();
        String sql = """
                INSERT INTO clients (first_name, last_name, phone, email)
                VALUES
                	('%s',
                	'%s',
                	'%s',
                	'%s');
                	""".formatted(firstName, lastName, phoneNumber, email);

        //Display city card and verify with user.
        if (confirm) {
            String formattedPhoneNumber = "("
                    + phoneNumber.substring(0,3) + ") "
                    + phoneNumber.substring(3, 6) + "-"
                    + phoneNumber.substring(6, 10);
            System.out.printf("""
                    [!] Now displaying client card.
                    
                    [CLIENT '%s %s']
                    PHONE: %s
                    EMAIL: %s
                    
                    Would you like to add this client?
                    
                    [1] Yes.
                    [0] No.
                    
                    input:""",
                    firstName,
                    lastName,
                    formattedPhoneNumber,
                    email);
            //Exit if user inputs no.
            if (Menu.collectInt(0, 1, input) == 0) return;
        }

        //Execute query:
        if (performUpdate(sql, "add client")) {
            System.out.println("[!] Successfully added client.");
        } else {
            System.out.println("[!] Error occurred. Client not added.");
        }
    }
    public boolean verifyClient(int id){
        boolean result = false;
        String sql = """
                SELECT EXISTS (
                	SELECT 1
                	FROM clients
                	WHERE clients.id=%d
                );""".formatted(id);

        //Processing results.
        ResultSet rs = performQuery(sql, "verify client id");
        if (rs == null) return false;
        try {
            rs.next();
            result = rs.getBoolean(1);
            rs.close();
        } catch (SQLException e) {
            //Should not occur given ResultSet is not null.
        }
        return result;
    }
    public boolean verifyCity(int id){
        boolean result = false;
        String sql = """
                SELECT EXISTS (
                	SELECT 1
                	FROM cities
                	WHERE cities.id=%d
                );""".formatted(id);

        //Processing results.
        ResultSet rs = performQuery(sql, "verify city id");
        if (rs == null) return false;
        try {
            rs.next();
            result = rs.getBoolean(1);
            rs.close();
        } catch (SQLException e) {
            //Should not occur given ResultSet is not null.
        }
        return result;
    }
    public boolean verifyProperty(int id){
        /*
        Table will return as:
        (row id #0) | exists (#1)
         */
        boolean result = false;
        String sql = """
                SELECT EXISTS (
                	SELECT 1
                	FROM properties
                	WHERE properties.id=%d
                );""".formatted(id);

        //Processing results.
        ResultSet rs = performQuery(sql, "verify property id");
        if (rs == null) return false;
        try {
            rs.next();
            result = rs.getBoolean(1);
            rs.close();
        } catch (SQLException e) {
            //Should not occur given ResultSet is not null.
        }
        return result;
    }
    public boolean verifyState(String abbreviation){
        boolean result = false;
        //Ensure state abbreviation is in all uppercase.
        abbreviation = abbreviation.toUpperCase();
        String sql = """
                SELECT EXISTS (
                	SELECT 1
                	FROM states
                	WHERE states.abbreviation='%s'
                );""".formatted(abbreviation);

        //Processing results.
        ResultSet rs = performQuery(sql, "verify state abbreviation");
        if (rs == null) return false;
        try {
            rs.next();
            result = rs.getBoolean(1);
            rs.close();
        } catch (SQLException e) {
            //Should not occur given ResultSet is not null.
        }
        return result;
    }
    public int getStateID(String abbreviation){
        //Ensure abbreviation is all uppercase.
        abbreviation = abbreviation.toUpperCase();
        //Will return -1 in event of an error.
        /*
        Table will return as
        (row id) #0 | id #1
         */
        int id = -1;
        String sql = """
                SELECT id FROM states
                WHERE states.abbreviation = '%s';""".formatted(abbreviation);
        //Processing results.
        ResultSet rs = performQuery(sql, "retrieve state id");
        if (rs == null) return id;
        try {
            //If there is no next row, the state entered does not exist.
            if (rs.next()) {
                id = rs.getInt(1);
            }
            rs.close();
        } catch (SQLException e) {
            //Should not occur given ResultSet is not null.
        }
        return id;
    }

    /**
     * Verifies if all the standard tables are present. (clients, states, cities, properties, services).
     * @return Whether standard tables are present.
     */
    public boolean verifyTables(){
        boolean result = false;
        String sql = """
                SELECT
                  EXISTS (SELECT 1 FROM pg_tables WHERE schemaname = 'public' AND tablename = 'cities') AS cities, --id 1
                  EXISTS (SELECT 1 FROM pg_tables WHERE schemaname = 'public' AND tablename = 'clients') AS clients,
                  EXISTS (SELECT 1 FROM pg_tables WHERE schemaname = 'public' AND tablename = 'properties') AS properties,
                  EXISTS (SELECT 1 FROM pg_tables WHERE schemaname = 'public' AND tablename = 'services') AS services,
                  EXISTS (SELECT 1 FROM pg_tables WHERE schemaname = 'public' AND tablename = 'states') AS states;""";

        //Execute query and process results
        ResultSet rs = performQuery(sql, "verify table existence");
        if (rs == null) return false;
        try {
            //All tables must be present for result to be true.
            rs.next();
            result = (rs.getBoolean(1) &&
                    rs.getBoolean(2) &&
                    rs.getBoolean(3) &&
                    rs.getBoolean(4) &&
                    rs.getBoolean(5));
            rs.close();
        } catch (SQLException e) {
            //Should not occur given ResultSet is not null.
        }
        return result;
    }

    /**
     * Will guide user through server population process. Prompts user whether to add solely tables or sample data as well.
     */
    private void populateServer(){
        int choice;
        System.out.print("""
               [!] Required tables not found. The database must have the required tables to function properly.
               [!] Sample data is recommended to view program functionality.
               What would you like to do?:
               
               [1] Add tables.
               [0] Add tables and sample data.
               
               input:""");
        choice = Menu.collectInt(0,1, input);
        switch (choice) {
            case 1 ->{
                createTables();
                insertStates();
            }
            case 0 ->{
                createTables();
                insertStates();
                insertSampleData();
            }
        }
    }
    private void createTables() {
        //Table sql
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

        //Perform query.
        if (performUpdate(sql, "create database tables")) {
            System.out.println("[!] Successfully created tables.");
        }
    }
    private void insertStates() {
        StringBuilder sql = new StringBuilder();
        //List of all states to be used when populating.
        String[][] statesList = {
                {"AL", "alabama"}, {"AK", "alaska"}, {"AZ", "arizona"}, {"AR", "arkansas"},
                {"CA", "california"}, {"CO", "colorado"}, {"CT", "connecticut"}, {"DE", "delaware"}, {"FL", "florida"},
                {"GA", "georgia"}, {"HI", "hawaii"}, {"ID", "idaho"}, {"IL", "illinois"}, {"IN", "indiana"},
                {"IA", "iowa"}, {"KS", "kansas"}, {"KY", "kentucky"}, {"LA", "louisiana"}, {"ME", "maine"},
                {"MD", "maryland"}, {"MA", "massachusetts"}, {"MI", "michigan"}, {"MN", "minnesota"},
                {"MS", "mississippi"}, {"MO", "missouri"}, {"MT", "montana"}, {"NE", "nebraska"}, {"NV", "nevada"},
                {"NH", "new hampshire"}, {"NJ", "new jersey"}, {"NM", "new mexico"}, {"NY", "new york"},
                {"NC", "north carolina"}, {"ND", "north dakota"}, {"OH", "ohio"}, {"OK", "oklahoma"},
                {"OR", "oregon"}, {"PA", "pennsylvania"}, {"RI", "rhode island"}, {"SC", "south carolina"},
                {"SD", "south dakota"}, {"TN", "tennessee"}, {"TX", "texas"}, {"UT", "utah"}, {"VT", "vermont"},
                {"VA", "virginia"}, {"WA", "washington"}, {"WV", "west virginia"}, {"WI", "wisconsin"}, {"WY", "wyoming"} };

        //Creating a large SQL query containing all states.
        for (String[] strings : statesList) {
            sql.append("""
                        INSERT INTO states (abbreviation, name)
                        VALUES (
                        	'%s',
                        	'%s'
                        );
                        """.formatted(strings[0], strings[1]));
        }
        String sqlString = sql.toString();

        //Perform query.
        if (performUpdate(sqlString, "populate states table")) {
            System.out.println("[!] Successfully populated states table with standard data.");
        }
    }
    private void insertSampleData(){
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

        //Perform query.
        if (performUpdate(sql, "insert sample data")) {
            System.out.println("[!] Successfully inserted sample data to all tables.");
        }
    }
}
