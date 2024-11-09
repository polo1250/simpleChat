package edu.seg2105.client.ui;
// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

import java.io.*;
import java.util.Scanner;

import edu.seg2105.client.backend.ChatClient;
import edu.seg2105.client.common.*;

/**
 * This class constructs the UI for a chat client.  It implements the
 * chat interface in order to activate the display() method.
 * Warning: Some of the code here is cloned in ServerConsole 
 *
 * @author Fran&ccedil;ois B&eacute;langer
 * @author Dr Timothy C. Lethbridge  
 * @author Dr Robert Lagani&egrave;re
 */
public class ClientConsole implements ChatIF 
{
  //Class variables *************************************************
  
  /**
   * The default port to connect on.
   */
  final public static int DEFAULT_PORT = 5555;
  
  //Instance variables **********************************************
  
  /**
   * The instance of the client that created this ConsoleChat.
   */
  ChatClient client;
  
  
  
  /**
   * Scanner to read from the console
   */
  Scanner fromConsole; 

  
  //Constructors ****************************************************

  /**
   * Constructs an instance of the ClientConsole UI.
   *
   * @param host The host to connect to.
   * @param port The port to connect on.
   */
  public ClientConsole(String loginId, String host, int port)
  {
    try 
    {
      client = new ChatClient(loginId, host, port, this);
      
    } 
    catch(IOException exception) 
    {
      System.out.println("Error: Can't setup connection!"
                + " Terminating client.");
      System.exit(1);
    }
    
    // Create scanner object to read from console
    fromConsole = new Scanner(System.in); 
  }

  
  //Instance methods ************************************************

  /**
   * This method waits for input from the console. Once it is
   * received, it sends it to the client's message handler.
   */
  public void accept() 
  {
    try
    {

      String message;

      while (true) 
      {
        message = fromConsole.nextLine();
        if (message.startsWith("#")) {
          handleCommand(message);
        } else {
          client.handleMessageFromClientUI(message);
        }
      }
    } 
    catch (Exception ex) 
    {
      System.out.println
        ("Unexpected error while reading from console!");
    }
  }

  /**
   * This method handles commands starting with '#'.
   *
   * @param command The command entered by the user.
   */
  private void handleCommand(String command) {
    if (command.equals("#quit")) {
      client.quit();
    } else if (command.equals("#logoff")) {
      client.logoff();
    } else if (command.startsWith("#sethost")) {
      String[] parts = command.split(" ");
      if (parts.length > 1) {
        client.setHost(parts[1]);
      } else {
        display("Usage: #sethost <host>");
      }
    } else if (command.startsWith("#setport")) {
      String[] parts = command.split(" ");
      if (parts.length > 1) {
        try {
          int port = Integer.parseInt(parts[1]);
          client.setPort(port);
        } catch (NumberFormatException e) {
          display("Invalid port number.");
        }
      } else {
        display("Usage: #setport <port>");
      }
    } else if (command.equals("#login")) {
      client.login();
    } else if (command.equals("#gethost")) {
      display("Current host: " + client.getHost());
    } else if (command.equals("#getport")) {
      display("Current port: " + client.getPort());
    } else {
      display("Unknown command.");
    }
  }

  /**
   * This method overrides the method in the ChatIF interface.  It
   * displays a message onto the screen.
   *
   * @param message The string to be displayed.
   */
  public void display(String message) 
  {
    System.out.println("> " + message);
  }

  
  //Class methods ***************************************************

  /**
   * This method is responsible for the creation of the Client UI.
   *
   * @param args The arguments for the client.
   */
  public static void main(String[] args) 
  {
    if (args.length < 1) {
      System.out.println("ERROR - No login ID specified. Connection aborted.");
      System.exit(1);
    }

    String loginId = args[0];
    String host = "";
    int port = DEFAULT_PORT; // Default port

    try {
      host = args[1];
      if (args.length > 2) {
        port = Integer.parseInt(args[2]);
      }
    } catch (ArrayIndexOutOfBoundsException e) {
      host = "localhost";
    } catch (NumberFormatException e) {
      System.out.println("ERROR - Invalid port number. Using default port " + DEFAULT_PORT);
    }

    ClientConsole chat = new ClientConsole(loginId, host, port);
    chat.accept(); // Wait for console data
  }
}
//End of ConsoleChat class
