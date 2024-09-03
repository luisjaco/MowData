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
        choice = collectInt(0,1, input);

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
            System.out.println();
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
        choice = collectInt(0,2, input);
        switch (choice){
            case 2 -> {
                //TODO view menus
            }
            case 1 -> {
                //TODO add menus
            }
            case 0 -> {
                return false;
            }
        }
        //Keep the menu running if user does not explicitly choose to exit.
        return true;
    }

}
