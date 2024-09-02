import java.sql.*;
/**
 * The ___ class is to initialize a server. Can also add sample data to a server.
 */
public class Database {
    public static void main(String[] args) {
        String url = "jdbc:postgresql://localhost:5432/test";
        String user = "postgres";
        String pass = "Diamond2004";
        Connection con = null;

        try{
            con = DriverManager.getConnection(url, user, pass);
            System.out.println("creating tables");
            createTables(con);
            System.out.println("adding samples");
            addSampleRows(con);

        } catch (SQLException e) {
            System.out.println("Uh oh! Error found:\n" + e);
        }
        finally {
            try{
                if (con != null){
                    con.close();
                }
            } catch (SQLException e) {
                System.out.println("Uh oh! Something went wrong when closing this connection:\n" + e);
            }
        }
    }
    private static void createTables(Connection con) {
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
            st = con.createStatement();
            st.executeUpdate(sql);
            System.out.println("Successfully added tables.");
            populateStatesTable(st);
        } catch (SQLException e) {
            System.out.println("Uh oh! Error found:\n" + e);
        }
        finally {
            closeStatement(st);
        }
    }

    private static void closeStatement(Statement st){
        try {
            if (st != null){
                st.close();
            }
        } catch (SQLException e) {
            System.out.println("Uh oh! Something went wrong when closing a statement:\n" + e);
        }
    }

    private static void populateStatesTable(Statement st) {
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

        try {
            String sql = "";
            for (String[] strings : statesList) {
                sql += """
                        INSERT INTO states (abbreviation, name)
                        VALUES (
                        	'%s',
                        	'%s'
                        );
                        """.formatted(strings[0], strings[1]);
            }

            int added = st.executeUpdate(sql);
            System.out.println("Successfully added states data.");
        } catch (SQLException e) {
            System.out.println("Uh oh! Error found:\n" + e);
        }
    }

    /**
     * Adds sample rows to the client, cities, properties, and services tables.
     * @param con Connection to be used for query
     */
    private static void addSampleRows(Connection con){
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
            st = con.createStatement();

            st.executeUpdate(sql);
            System.out.println("Successfully added sample data.");
        } catch (SQLException e) {
            System.out.println("Uh oh! Error found:\n" + e);
        } finally {
            closeStatement(st);
        }
    }
}
