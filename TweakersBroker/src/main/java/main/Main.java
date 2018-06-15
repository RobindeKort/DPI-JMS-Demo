package main;

import java.util.Scanner;
import javax.jms.JMSException;
import controller.BrokerController;

/**
 *
 * @author Robin
 */
public class Main {

    private static final String activeMqIp = "127.0.0.1";

    private static BrokerController broker;

    public static void main(String[] args) throws JMSException {
        System.out.println("\nBroker is starting. . .\n");
        broker = new BrokerController(activeMqIp);

        showMainMenu();
        Scanner sc = new Scanner(System.in);
        String input = "";
        boolean stopServer = false;
        while (!stopServer) {
            input = sc.nextLine();
            switch (input) {
                case "info":
                    System.out.println("The server is currently ONLINE.\n"
                            + "You were able to enter a command, did you really expect anything else?");
                    break;
                case "stop":
                    System.out.println("\nBroker is stopping. . .");
                    stopServer = true;
                    broker.stop();
                    break;
                case "help":
                    showMainMenu();
                    break;
                default:
                    System.out.println("Invalid input, type 'help' for a list of commands.");
                    break;
            }
        }
    }

    private static void showMainMenu() {
        System.out.println(""
                + "----------------------------------------------------------------------------------------------------\n"
                + "info\t\tShow current status of the Broker\n"
                + "stop\t\tStop the Broker\n"
                + "help\t\tShow this list\n"
                + "----------------------------------------------------------------------------------------------------\n");
    }
}
