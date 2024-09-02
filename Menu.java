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
                server = new Server();
                run();
            }
            case 0 -> {
                exit();
            }
        }
    }
    private void exit(){
        System.out.println(mowdata);
        System.out.println("Thank you for using MowData.");
        //TODO close server as well once that is created.
        input.close();
    }
    private int collectInt(int minNum, int maxNum){
        int result;
        while (true){
            try{
                result = input.nextInt();
                input.nextLine();
            }
            catch (Exception e){
                System.out.print("[!] Please input an integer:\n\nNew attempt:");
                input.nextLine();
                continue;
            }

            if ((result > maxNum) || (result < minNum)){
                System.out.print("[!] Please input a valid number.\n\nNew attempt:");
            }
            else {
                System.out.println();
                return result;
            }
        }
    }
    private double collectDouble(double minNum, double maxNum){
        double result;
        while (true){

            try{
                result = this.input.nextDouble();
                this.input.nextLine();
            }
            catch (Exception e){
                System.out.print("[!] Please input a double:\n\nNew attempt:");
                this.input.nextLine();
                continue;
            }

            if ((result > maxNum) || (result < minNum)){
                System.out.print("[!] Please input a valid number.\n\nNew attempt:");
            }
            else {
                System.out.println();
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
        choice = collectInt(0,2);
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
