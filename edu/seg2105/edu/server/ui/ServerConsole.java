package edu.seg2105.edu.server.ui;

import java.io.*;
import java.util.Scanner;
import edu.seg2105.client.common.ChatIF;
import edu.seg2105.edu.server.backend.EchoServer;

/**
 * This class constructs the UI for a chat server. It implements the
 * chat interface in order to activate the display() method.
 */
public class ServerConsole implements ChatIF {
    EchoServer server;
    Scanner fromConsole;

    /**
     * Constructs an instance of the ServerConsole UI.
     *
     * @param port The port to connect on.
     */
    public ServerConsole(int port) {
        server = new EchoServer(port);
        fromConsole = new Scanner(System.in);
    }

    /**
     * This method waits for input from the console. Once it is
     * received, it sends it to the server's message handler.
     */
    public void accept() {
        try {
            String message;
            while (true) {
                message = fromConsole.nextLine();
                handleMessageFromConsole(message);
            }
        } catch (Exception ex) {
            System.out.println("Unexpected error while reading from console!");
        }
    }

    /**
     * This method handles messages from the console.
     *
     * @param message The message from the console.
     */
    private void handleMessageFromConsole(String message) throws IOException {
        if (message.startsWith("#")) {
            handleCommand(message);
        } else {
            server.sendToAllClients("SERVER MSG> " + message);
            display("SERVER MSG> " + message);
        }
    }

    /**
     * This method handles commands starting with '#'.
     *
     * @param command The command entered by the user.
     */
    private void handleCommand(String command) throws IOException {
        if (command.equals("#quit")) {
            server.quit();
        } else if (command.equals("#stop")) {
            server.stopListening();
        } else if (command.equals("#close")) {
            server.close();
        } else if (command.startsWith("#setport")) {
            String[] parts = command.split(" ");
            if (parts.length > 1) {
                try {
                    int port = Integer.parseInt(parts[1]);
                    server.setPort(port);
                } catch (NumberFormatException e) {
                    display("Invalid port number.");
                }
            } else {
                display("Usage: #setport <port>");
            }
        } else if (command.equals("#start")) {
            try {
                server.listen();
            } catch (IOException e) {
                display("Error starting the server.");
            }
        } else if (command.equals("#getport")) {
            display("Current port: " + server.getPort());
        } else {
            display("Unknown command.");
        }
    }

    /**
     * This method overrides the method in the ChatIF interface. It
     * displays a message onto the screen.
     *
     * @param message The string to be displayed.
     */
    @Override
    public void display(String message) {
        System.out.println("> " + message);
    }

    public static void main(String[] args) {
        int port = EchoServer.DEFAULT_PORT; // Default port

        try {
            if (args.length > 0) {
                port = Integer.parseInt(args[0]);
            }
        } catch (NumberFormatException e) {
            System.out.println("ERROR - Invalid port number. Using default port " + EchoServer.DEFAULT_PORT);
        }

        ServerConsole serverConsole = new ServerConsole(port);
        try {
            serverConsole.server.listen();
        } catch (IOException e) {
            System.out.println("ERROR - Could not listen for clients!");
        }
        serverConsole.accept(); // Wait for console data
    }
}