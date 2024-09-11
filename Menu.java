import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.Date;
import java.util.Scanner;
/**
 * The Menu class will handle all the user menus.
 */
public class Menu {
    private Scanner input;
    private Server server;
    protected final static String mowdata = """
            ,---.    ,---.     ,-----.     .--.      .--.  ______         ____     ,---------.     ____
            |    \\  /    |   .'  .-,  '.   |  |_     |  | |    _`''.   .'  __ `.\\ |          |  .'  __ `.
            |  ,  \\/  ,  |  / ,-.|  \\ _ \\  | _( )_   |  | | _ | ) _  \\ /   '  \\  \\ `--.  ---'  /   '  \\  \\
            |  |\\_   /|  | ;  \\  '_ /  | : |(_ o _)  |  | |( ''_'  ) | |___|  /  |    |   |    |___|  /  |
            |  _( )_/ |  | |  _`,/ \\ _/  | | (_,_) \\ |  | | . (_) `. |    _.-`   |    :_ _:       _.-`   |
            | (_ o _) |  | : (  '\\_/ \\   ; |  |/    \\|  | |(_    ._) | .'   _    |    (_I_)    .'   _    |
            |  (_,_)  |  |  \\ `"/  \\  ) /  |  '  /\\  `  | |  (_.\\.'  / |  _( )_  |   (_(=)_)   |  _( )_  |
            |  |      |  |   '. \\_/``".'   |    /  \\    | |       .'   \\ (_ o _) /    (_I_)    \\ (_ o _) /
            '--'      '--'     '-----'     `---'    `---` '-----'`      '.(_,_).'     '---'     '.(_,_).'""";
    public Menu(){
        //Does nothing.
    }
    public void start(){
        this.input = new Scanner(System.in);
        int choice;
        System.out.println(mowdata);
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
                this.server = new Server();
                if (server.establishConnection()){
                    run();
                } else {
                    exit();
                }
            }
            case 0 -> {
                System.out.println(mowdata);
                System.out.println("Thank you for using MowData.");
                input.close();
            }
        }
    }
    public void start(int port, String database, String username, String password){
        this.input = new Scanner(System.in);
        this.server = new Server();
        int choice;
        System.out.println(mowdata);
        System.out.print("""
        Welcome to MowData! A program designed for keeping track of client data, to be used by landscaping businesses.
        [!] This program is designed to be used with a locally hosted PostgreSQL server.
        """);

        if (server.establishConnection(port, database, username, password)){
            run();
        } else {
            exit();
        }
    }
    public void exit(){
        if (server != null && server.verifyConnection()){
            server.closeConnection();
        }
        else {
            System.out.println("[!] No valid connection found, therefore no connection was closed.");
        }
        System.out.println(mowdata);
        System.out.println("Thank you for using MowData.");
        input.close();
    }
    public static double collectDouble(double minNum, double maxNum, Scanner input){
        double result;
        while (true){
            try{
                result = input.nextDouble();
                input.nextLine();
            }
            catch (Exception e){
                System.out.print("[!] Please input a double:");
                input.nextLine();
                continue;
            }

            if ((result > maxNum) || (result < minNum)){
                System.out.print("[!] Invalid value found. Please input a valid number:");
            }
            else {
                return result;
            }
        }
    }
    public static int collectInt(int minNum, int maxNum, Scanner input){
        int result;
        while (true){
            try{
                result = input.nextInt();
                input.nextLine();
            }
            catch (Exception e){
                System.out.print("[!] Please input an integer:");
                input.nextLine();
                continue;
            }

            if ((result > maxNum) || (result < minNum)){
                System.out.print("[!] Invalid value found. Please input a valid number:");
            }
            else {
                return result;
            }
        }
    }
    private int collectInt(int minNum, int maxNum){
        int result;
        while (true){
            try{
                result = input.nextInt();
                input.nextLine();
            }
            catch (Exception e){
                System.out.print("[!] Please input an integer:");
                input.nextLine();
                continue;
            }

            if ((result > maxNum) || (result < minNum)){
                System.out.print("[!] Invalid value found. Please input a valid number:");
            }
            else {
                return result;
            }
        }
    }
    private double collectDouble(double minNum, double maxNum){
        double result;
        while (true){
            try{
                result = input.nextDouble();
                input.nextLine();
            }
            catch (Exception e){
                System.out.print("[!] Please input a double:");
                input.nextLine();
                continue;
            }

            if ((result > maxNum) || (result < minNum)){
                System.out.print("[!] Invalid value found. Please input a valid number:");
            }
            else {
                return result;
            }
        }
    }
    private LocalDate collectDate(){
        LocalDate result;
        while (true) {
            String date = input.nextLine();
            //YYYY-MM-DD
            try {
                if (date.length() != 10) throw new NumberFormatException();

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
    private void run(){
        while (mainMenu()){
            //Runs as long as mainMenu is returning true. (until user decides to exit)
            continue;
        }
        exit();
    }
    private boolean mainMenu(){
        int choice;
        System.out.print("""
                ~~+~~+~~+~~+~~+~~+~~+~~
                Please choose an action:
                
                [2] View data.
                [1] Add data.
                [0] Exit.
                
                input:""");
        choice = collectInt(0,2);
        switch (choice){
            case 2 -> {
                viewMenu();
            }
            case 1 -> {
                addMenu();
            }
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
            case 4 -> {
                viewServicesMenu();
            }
            case 3 -> {
                viewPropertiesMenu();
            }
            case 2 -> {
                viewCitiesMenu();
            }
            case 1 -> {
                viewClientsMenu();
            }
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
            case 3 -> {
                server.viewServices("all", promptForRowCount());
            }
            case 2 -> {
                server.viewServices("property", promptForRowCount());
            }
            case 1 -> {
                server.viewServices("date", promptForRowCount());
            }
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
            case 3 -> {
                server.viewProperties("all", promptForRowCount());
            }
            case 2 -> {
                server.viewProperties("city", promptForRowCount());
            }
            case 1 -> {
                server.viewProperties("client", promptForRowCount());
            }
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
            case 3 -> {
                server.viewCities("all", promptForRowCount());
            }
            case 2 -> {
                server.viewCities("state", promptForRowCount());
            }
            case 1 -> {
                server.viewCities("name", promptForRowCount());
            }
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
            case 2 -> {
                server.viewClients("all", promptForRowCount());
            }
            case 1 -> {
                server.viewClients("name", promptForRowCount());
            }
            case 0 -> {
                //Do nothing. Return to mainMenu.
            }
        }
    }
    private int promptForRowCount(){
        System.out.print("Please enter how many rows you would like to see (-1 for all rows):");
        return collectInt(-1, Integer.MAX_VALUE);
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
            case 4 -> {
                addServiceMenu();
            }
            case 3 -> {
                addPropertyMenu();
            }
            case 2 -> {
                addCityMenu();
            }
            case 1 -> {
                addClientMenu();
            }
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

        //1. Property id
        System.out.print("1. Enter a property id:");
        int propertyID = collectInt(0, Integer.MAX_VALUE);
        if (!server.verifyProperty(propertyID)) {
            System.out.println("[!] Invalid property id. Please try again.");
            return;
        }

        //2. Service date
        System.out.print("2. Enter a service date [YYYY-MM-DD]:");
        LocalDate serviceDate = collectDate();

        //3. Services done
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

        //4. Cost
        System.out.print("4. Enter the service cost:");
        double serviceCost = collectDouble(0, Double.MAX_VALUE);

        //5. Notes
        System.out.print("5. Enter any notes. Leave blank for null:");
        String notes = input.nextLine();

        server.addService(propertyID, serviceDate, servicesDoneArray, serviceCost, notes, true);
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

        //Gathering inputs. Verify client and city IDs.
        System.out.print("1. Enter client id:");
        int clientID = collectInt(0, Integer.MAX_VALUE);
        if (!server.verifyClient(clientID)) {
            System.out.println("[!] Invalid client id. Please try again.");
            return;
        }

        System.out.print("2. Enter address (EX: 123 apple road):");
        String address = input.nextLine().toLowerCase();

        System.out.print("3. Enter city id:");
        int cityID = collectInt(0, Integer.MAX_VALUE);
        if (!server.verifyCity(cityID)) {
            System.out.println("[!] Invalid city id. Please try again.");
            return;
        }

        server.addProperty(clientID, address, cityID, true);
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

        //Get city name.
        System.out.print("1. Enter city name:");
        String cityName = input.nextLine().toLowerCase();

        //Get zip code.
        System.out.print("2. Enter zip code:");
        String zipCode = input.nextLine();
        //Check zip code validity.
        boolean isNumeric;
        try {
            Integer.parseInt(zipCode);
            isNumeric = true;
        } catch (NumberFormatException e){
            isNumeric = false;
        }
        if ((zipCode.length() != 5) || !isNumeric) {
            System.out.println("[!] Invalid zip code entered. Please try again.");
            return;
        }

        //Get state.
        System.out.print("3. Enter state (EX: NY):");
        String state = input.nextLine();
        //Validate state and retrieve state id.
        if (!server.verifyState(state)) {
            System.out.println("[!] Invalid state entered. Please try again.");
            return;
        }
        int stateID = server.getStateID(state);

        server.addCity(cityName, zipCode, stateID, true);
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

        //Get first name.
        System.out.print("1. Enter first name:");
        String firstName = input.nextLine();
        //Get last name.
        System.out.print("2. Enter last name:");
        String lastName = input.nextLine();
        //Get phone number.
        System.out.print("3. Enter phone number (EX: 123-456-7890):");
        String phoneNumber = input.nextLine().replaceAll("-","");
        //Verify phone number is valid.
        boolean isNumeric;
        try {
            Long.parseLong(phoneNumber);
            isNumeric = true;
        } catch (NumberFormatException e){
            isNumeric = false;
        }
        if ((phoneNumber.length() != 10) || !isNumeric) {
            System.out.println("[!] Invalid phone number entered. Please try again.");
            return;
        }
        //Get email.
        System.out.print("4. Enter email:");
        String email = input.nextLine();

        server.addClient(firstName, lastName, phoneNumber, email, true);
    }
}
