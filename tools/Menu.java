package tools;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.Scanner;
/**
 * The tools.Menu class will handle all the user menus and program functions.
 */
public class Menu {
    private Scanner input;
    private Server server;
    private MowDataDB database;
    private final static String mowdata = """
            ,---.    ,---.     ,-----.     .--.      .--.  ______         ____     ,---------.     ____
            |    \\  /    |   .'  .-,  '.   |  |_     |  | |    _`''.   .'  __ `.\\ |          |  .'  __ `.
            |  ,  \\/  ,  |  / ,-.|  \\ _ \\  | _( )_   |  | | _ | ) _  \\ /   '  \\  \\ `--.  ---'  /   '  \\  \\
            |  |\\_   /|  | ;  \\  '_ /  | : |(_ o _)  |  | |( ''_'  ) | |___|  /  |    |   |    |___|  /  |
            |  _( )_/ |  | |  _`,/ \\ _/  | | (_,_) \\ |  | | . (_) `. |    _.-`   |    :_ _:       _.-`   |
            | (_ o _) |  | : (  '\\_/ \\   ; |  |/    \\|  | |(_    ._) | .'   _    |    (_I_)    .'   _    |
            |  (_,_)  |  |  \\ `"/  \\  ) /  |  '  /\\  `  | |  (_.\\.'  / |  _( )_  |   (_(=)_)   |  _( )_  |
            |  |      |  |   '. \\_/``".'   |    /  \\    | |       .'   \\ (_ o _) /    (_I_)    \\ (_ o _) /
            '--'      '--'     '-----'     `---'    `---` '-----'`      '.(_,_).'     '---'     '.(_,_).'""";

    /**
     * Creates a new Menu instance. Use the start() method to begin menu loop.
     */
    public Menu(){
        //Does nothing.
    }

    /**
     * Begin user menu sequence. Prompt user to start server.
     */
    public void start(){
        this.input = new Scanner(System.in);
        int choice;
        System.out.println(mowdata);

        //Prompt user to connect to server.
        System.out.print("""
        Welcome to MowData! A program designed for keeping track of client data, to be used by landscaping businesses.
        [!] This program is designed to be used with a locally hosted PostgreSQL server.
        
        What would you like to do?:
        [1] Connect to server.
        [0] Exit.
        
        input:""");
        choice = collectInt(0,1);
        switch (choice){
            case 1 -> {
                //Initialize new server and establish connection.
                this.server = new Server();
                if (server.establishConnection()){
                    this.database = server.establishDatabase();
                    run();
                } else {
                    exit();
                }
            }
            case 0 -> {
                //Exit loop without checking for connection.
                System.out.println(mowdata);
                System.out.println("Thank you for using MowData.");
                input.close();
            }
        }
    }

    /**
     * Begin menu sequence. Use inputs to establish server.
     * @param port tools.Server port number.
     * @param database tools.Server database name.
     * @param username tools.Server username.
     * @param password User password.
     */
    public void start(int port, String database, String username, String password){
        this.input = new Scanner(System.in);
        this.server = new Server();
        System.out.println(mowdata);
        System.out.print("""
        Welcome to MowData! A program designed for keeping track of client data, to be used by landscaping businesses.
        [!] This program is designed to be used with a locally hosted PostgreSQL server.
        """);

        //Initialize new server and establish connection.
        if (server.establishConnection(port, database, username, password)){
            this.database = server.establishDatabase();
            run();
        } else {
            exit();
        }
    }

    /**
     * Will close the tools.Server and Scanner objects if they are in use. Exits loop.
     */
    public void exit(){
        //If server was established close server, otherwise do nothing.
        if (server != null && server.verifyConnection()){
            server.closeServer();
        }
        else {
            System.out.println("[!] No valid connection found, therefore no connection was closed.");
        }
        System.out.println(mowdata);
        System.out.println("Thank you for using MowData.");
        input.close();
    }


    /**
     * Collect an int within a specific range, range inclusive. minNum <= number <= maxNum.
     * @param minNum Minimum value.
     * @param maxNum Maximum value.
     * @param input Scanner object in case of extra input needed.
     * @return Integer within specified range.
     */
    public static int collectInt(int minNum, int maxNum, Scanner input){
        int result;
        //Loop while a valid int hasn't been found.
        while (true){
            try{
                result = input.nextInt();
                input.nextLine();
            }
            catch (Exception e){
                //Catch invalid type.
                System.out.print("[!] Please input an integer:");
                input.nextLine();
                continue;
            }
            //Verify int is within range.
            if ((result > maxNum) || (result < minNum)){
                System.out.print("[!] Invalid value found. Please input a valid number:");
            }
            else {
                //If both checks are passed, we return result.
                return result;
            }
        }
    }
    private int collectInt(int minNum, int maxNum){
        int result;
        //Loop while a valid int hasn't been found.
        while (true){
            try{
                result = input.nextInt();
                input.nextLine();
            }
            catch (Exception e){
                //Catch invalid type
                System.out.print("[!] Please input an integer:");
                input.nextLine();
                continue;
            }
            //Verify int is within specified integer.
            if ((result > maxNum) || (result < minNum)){
                System.out.print("[!] Invalid value found. Please input a valid number:");
            }
            else {
                //If int passes both checks, return result.
                return result;
            }
        }
    }
    private double collectDouble(double minNum, double maxNum){
        double result;
        //Loop while valid double not found.
        while (true){
            try{
                result = input.nextDouble();
                input.nextLine();
            }
            catch (Exception e){
                //Catch invalid type
                System.out.print("[!] Please input a double:");
                input.nextLine();
                continue;
            }
            //Verify double is in specified range.
            if ((result > maxNum) || (result < minNum)){
                System.out.print("[!] Invalid value found. Please input a valid number:");
            }
            else {
                //If double passes both checks, return result
                return result;
            }
        }
    }

    /**
     * Collect a date from user input. Will expect the string to be in the format YYYY-MM-DD.
     * @return LocalDate of input date.
     */
    private LocalDate collectDate(){
        LocalDate result;
        while (true) {
            String date = input.nextLine();
            //YYYY-MM-DD
            try {
                //Ensure string was proper length before splitting up string.
                if (date.length() != 10) throw new NumberFormatException();
                //Split up string.
                int year = Integer.parseInt(date.substring(0,4));
                int month = Integer.parseInt(date.substring(5,7));
                int day = Integer.parseInt(date.substring(8,10));

                result = LocalDate.of(year, month, day);
                break;
            }
            catch (NumberFormatException e){
                System.out.print("[!] Incorrect formatting found. Enter a date in the format [YYYY-MM-DD]:");
            }
            catch (DateTimeException e){
                System.out.print("[!] Impossible date given. Enter a correct date in the format [YYYY-MM-DD]:");
            }
        }
        return result;
    }

    /**
     * Retrieves an integer to be used in row counts. Uses the range -1 to Integer.MAX_VALUE.
     * @return number of rows user would like to view
     */
    private int promptForRowCount(){
        System.out.print("[!] Please enter how many rows you would like to view (-1 for all):");
        return collectInt(-1, Integer.MAX_VALUE);
    }
    private void run(){
        while (mainMenu()){
            //Runs as long as mainMenu is returning true. (until user decides to exit)
            System.out.println("~~+~~+~~+~~+~~+~~+~~+~~");
        }
        exit();
    }
    private boolean mainMenu(){
        int choice;
        System.out.print("""
                [MAIN]
                Please choose an action:
                
                [2] View data.
                [1] Add data.
                [0] Exit.
                
                input:""");
        choice = collectInt(0,2);
        switch (choice){
            case 2 -> viewMenu();
            case 1 -> addMenu();
            case 0 -> {
                return false;
            }
        }
        //Keep the menu running if user does not explicitly choose to exit.
        return true;
    }
    private void viewMenu(){
        int choice;
        System.out.print("""
                [VIEW]
                Please choose an action:
                
                [4] View service history.
                [3] View properties.
                [2] View cities.
                [1] View clients.
                [0] Return.
                
                input:""");
        choice = collectInt(0,4);
        switch (choice) {
            case 4 -> viewServicesMenu();
            case 3 -> viewPropertiesMenu();
            case 2 -> viewCitiesMenu();
            case 1 -> viewClientsMenu();
            case 0 -> {
                //Do nothing. Return to mainMenu.
            }
        }
    }
    private void viewServicesMenu(){
        int choice;
        System.out.print("""
                [VIEW SERVICES]
                Please choose an action:
                
                [3] View all.
                [2] View sorted by property.
                [1] View sorted by date.
                [0] Return.
                
                input:""");
        choice = collectInt(0, 3);
        switch (choice){
            case 3 -> database.viewServices("all", promptForRowCount());
            case 2 -> database.viewServices("property", promptForRowCount());
            case 1 -> database.viewServices("date", promptForRowCount());
            case 0 -> {
                //Do nothing. Return to mainMenu.
            }
        }
    }
    private void viewPropertiesMenu(){
        int choice;
        System.out.print("""
                [VIEW PROPERTIES]
                Please choose an action:
                
                [3] View all.
                [2] View sorted by city.
                [1] View sorted by client.
                [0] Return.
                
                input:""");
        choice = collectInt(0,3);
        switch (choice){
            case 3 -> database.viewProperties("all", promptForRowCount());
            case 2 -> database.viewProperties("city", promptForRowCount());
            case 1 -> database.viewProperties("client", promptForRowCount());
            case 0 -> {
                //Do nothing. Return to mainMenu.
            }
        }
    }
    private void viewCitiesMenu(){
        int choice;
        System.out.print("""
                [VIEW CITIES]
                Please choose an action:
                
                [3] View all.
                [2] View sorted by state.
                [1] View sorted by name.
                [0] Return.
                
                input:""");
        choice = collectInt(0,3);
        switch (choice){
            case 3 -> database.viewCities("all", promptForRowCount());
            case 2 -> database.viewCities("state", promptForRowCount());
            case 1 -> database.viewCities("name", promptForRowCount());
            case 0 -> {
                //Do nothing. Return to mainMenu.
            }
        }
    }
    private void viewClientsMenu(){
        int choice;
        System.out.print("""
                [VIEW CLIENTS]
                Please choose an action:
                
                [2] View all.
                [1] View sorted by name.
                [0] Return.
                
                input:""");
        choice = collectInt(0,2);
        switch (choice) {
            case 2 -> database.viewClients("all", promptForRowCount());
            case 1 -> database.viewClients("name", promptForRowCount());
            case 0 -> {
                //Do nothing. Return to mainMenu.
            }
        }
    }
    private void addMenu(){
        int choice;
        System.out.print("""
                [ADD]
                Please choose an action:
                
                [4] Add service.
                [3] Add property.
                [2] Add city.
                [1] Add client.
                [0] Return.
                
                input:""");
        choice = collectInt(0, 4);
        switch (choice) {
            case 4 -> addServiceMenu();
            case 3 -> addPropertyMenu();
            case 2 -> addCityMenu();
            case 1 -> addClientMenu();
            case 0 -> {
                //Do nothing. Return to mainMenu.
            }
        }
    }
    private void addServiceMenu(){
        System.out.print("""
                [ADD SERVICE]
                [!] To add a service, you must input the following:
                1. PROPERTY ID
                2. SERVICE DATE
                3. SERVICES DONE
                4. COST
                5. NOTES (optional)
                
                Continue?
                [1] Yes, begin.
                [0] No, return.
                
                input:""");

        if (collectInt(0,1) == 0) return; //Do nothing. Return to mainMenu.

        //1. Property id. Retrieve and verify id.
        System.out.print("1. Enter a property id:");
        int propertyID = collectInt(0, Integer.MAX_VALUE);
        if (!database.verifyProperty(propertyID)) {
            System.out.println("[!] Invalid property id. Please try again.");
            return;
        }

        //2. Service date. Retrieve and verify date.
        System.out.print("2. Enter a service date [YYYY-MM-DD]:");
        LocalDate serviceDate = collectDate();

        //3. Services done. Retrieve and convert a string of services done into a corresponding list.
        System.out.print("""
                3. Please refer to this list:
                MOW..........m |   LEAF BLOW....l |   SEED...........s
                FERTILIZER...f |   MULCH........u |   TREE REMOVAL...r
                TREE TRIM....t |   POWER WASH...w |   SNOW PLOW......p
                
                [!] To add services, simply type all keys of the services done, in any order:""");
        String servicesDoneString = input.nextLine();
        String[] comparisonList = {"m", "l", "s", "f", "u", "r", "t", "w", "p"};
        boolean[] servicesDoneArray = new boolean[9];
        //We will look at the input string and check for each of our set values.
        for (int i=0; i < comparisonList.length; i++){
            servicesDoneArray[i] = servicesDoneString.contains(comparisonList[i]);
        }

        //4. Cost.
        System.out.print("4. Enter the service cost:");
        double serviceCost = collectDouble(0, Double.MAX_VALUE);

        //5. Notes.
        System.out.print("5. Enter any notes. Leave blank for null:");
        String notes = input.nextLine();

        database.addService(propertyID, serviceDate, servicesDoneArray, serviceCost, notes, true);
    }
    private void addPropertyMenu(){
        System.out.print("""
                [ADD PROPERTY]
                [!] To add a property, you must input the following:
                1. CLIENT ID
                2. ADDRESS
                3. CITY ID
                
                Continue?
                [1] Yes, begin.
                [0] No, return.
                
                input:""");

        if (collectInt(0, 1) == 0) return; //User exited.

        //Gathering inputs.

        //1. Verify client id.
        System.out.print("1. Enter client id:");
        int clientID = collectInt(0, Integer.MAX_VALUE);
        if (!database.verifyClient(clientID)) {
            System.out.println("[!] Invalid client id. Please try again.");
            return;
        }

        //2. Retrieve address, ensure address is lowercase.
        System.out.print("2. Enter address (EX: 123 apple road):");
        String address = input.nextLine().toLowerCase();

        //3. Verify city id.
        System.out.print("3. Enter city id:");
        int cityID = collectInt(0, Integer.MAX_VALUE);
        if (!database.verifyCity(cityID)) {
            System.out.println("[!] Invalid city id. Please try again.");
            return;
        }

        database.addProperty(clientID, address, cityID, true);
    }
    private void addCityMenu(){
        System.out.print("""
                [ADD CITY]
                [!] To add a city, you must input the following:
                
                1. CITY NAME
                2. ZIP CODE
                3. STATE
                
                Continue?
                [1] Yes, begin.
                [0] No, return.
                
                input:""");

        //Prompt user.
        if (collectInt(0, 1) == 0) return;

        //1. City, retrieve city name. Ensure the name is lowercase.
        System.out.print("1. Enter city name:");
        String cityName = input.nextLine().toLowerCase();

        //2. Zip code, ensure zip code is in proper format.
        System.out.print("2. Enter zip code:");
        String zipCode = input.nextLine();
        //Check zip code validity.
        boolean isNumeric;
        try {
            //Make sure the inputted string is all numeric.
            Integer.parseInt(zipCode);
            isNumeric = true;
        } catch (NumberFormatException e){
            isNumeric = false;
        }
        //Make sure the length of the code is 5.
        if ((zipCode.length() != 5) || !isNumeric) {
            System.out.println("[!] Invalid zip code entered. Please try again.");
            return;
        }

        //3. State, verify state by its abbreviation then convert to id.
        System.out.print("3. Enter state (EX: NY):");
        String state = input.nextLine();
        //Validate state and retrieve state id.
        if (!database.verifyState(state)) {
            System.out.println("[!] Invalid state entered. Please try again.");
            return;
        }
        int stateID = database.getStateID(state);

        database.addCity(cityName, zipCode, stateID, true);
    }
    private void addClientMenu(){
        System.out.print("""
                [ADD CLIENT]
                [!] To add a client, you must input the following:
                1. FIRST NAME
                2. LAST NAME
                3. PHONE NUMBER
                4. EMAIL
                
                Continue?
                [1] Yes, begin.
                [0] No, return.
                
                input:""");
        //Prompt user.
        if (collectInt(0, 1) == 0) return;

        //1. First name. Ensure lower case.
        System.out.print("1. Enter first name:");
        String firstName = input.nextLine().toLowerCase();
        //2. Get last name. Ensure lower case
        System.out.print("2. Enter last name:");
        String lastName = input.nextLine().toLowerCase();

        //3. Get phone number. Verify phone number is valid.
        System.out.print("3. Enter phone number (EX: 123-456-7890):");
        //Take in string as a standard phone number for human readability, then remove "-"'s to process the rest.
        String phoneNumber = input.nextLine().replaceAll("-","");
        boolean isNumeric;
        try {
            //Ensure string is numeric
            Long.parseLong(phoneNumber);
            isNumeric = true;
        } catch (NumberFormatException e){
            isNumeric = false;
        }
        //If string is all numeric and of length 10, it can be a valid phone number.
        if ((phoneNumber.length() != 10) || !isNumeric) {
            System.out.println("[!] Invalid phone number entered. Please try again.");
            return;
        }

        //4. Get email, ensure lowercase.
        System.out.print("4. Enter email:");
        String email = input.nextLine().toLowerCase();

        database.addClient(firstName, lastName, phoneNumber, email, true);
    }
}
