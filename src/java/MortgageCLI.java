import java.util.Scanner;

public class MortgageCLI {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        System.out.println("Welcome to the Mortgage Application.\n");
        while(running) {
            System.out.println(
                "Please choose an option:\n"
                + "1. Add Filter\n"
                + "2. Delete Filter\n"
                + "3. Calculate Rate\n"
                + "4. Exit\n"
            );

            int option = scanner.nextInt();
            switch(option) {
                case 1:
                    System.out.println("Add Filter\n");
                    break;
                case 2:
                    System.out.println("Delete Filter\n");
                    break;
                case 3:
                    System.out.println("Calculate Rate\n");
                    break;
                case 4:
                    System.out.println("Exiting...\n");
                    running = false;
                    break;
                default:
                    System.out.println("Invalid option\n");
            }
        }
    }
}
