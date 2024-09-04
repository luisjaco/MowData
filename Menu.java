import java.util.Scanner;
/**
 * The Menu class will handle all the user menus.
 */
public class Menu {
    private Scanner input;
    private Server server;
    protected final static String mowdata = """
    mowdata {replace with cool ascii}""";
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
    private void exit(){
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
    private void run(){
        while (mainMenu()){
            //Runs as long as mainMenu is returning true. (until user decides to exit)
            System.out.println("~~+~~+~~");
        }
        exit();
    }
    private boolean mainMenu(){
        int choice;
        System.out.print("""
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
                
                [2] View all.
                [1] View sorted by state.
                [0] Return.
                
                input:""");
        choice = collectInt(0,2);
        switch (choice){
            case 2 -> {
                server.viewCities("all", promptForRowCount());
            }
            case 1 -> {
                server.viewCities("state", promptForRowCount());
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
                //TODO
            }
            case 3 -> {
                //TODO
            }
            case 2 -> {
                //TODO
            }
            case 1 -> {
                //TODO
            }
            case 0 -> {
                //Do nothing. Return to mainMenu.
            }
        }
    }
    private void addServiceMenu(){
        //Get property, if no property, prompt for property creation. first must verify if property exists.
        int choice = 0;
        System.out.print("""
                [ADD SERVICE]
                To add a service, you must input the following:
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

        //Property
        while (true) {
            System.out.print("""
                1. Enter a property id. -1 to search for properties.
                
                input:""");
            int property_id = collectInt(-1, Integer.MAX_VALUE);
            //TODO
        }

    }
}
