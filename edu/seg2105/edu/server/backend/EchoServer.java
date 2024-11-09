package edu.seg2105.edu.server.backend;
// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 


import ocsf.server.*;

import java.io.IOException;

/**
 * This class overrides some of the methods in the abstract 
 * superclass in order to give more functionality to the server.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;re
 * @author Fran&ccedil;ois B&eacute;langer
 * @author Paul Holden
 */
public class EchoServer extends AbstractServer 
{
  //Class variables *************************************************
  
  /**
   * The default port to listen on.
   */
  final public static int DEFAULT_PORT = 5555;
  
  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the echo server.
   *
   * @param port The port number to connect on.
   */
  public EchoServer(int port) 
  {
    super(port);
  }

  
  //Instance methods ************************************************
  
  /**
   * This method handles any messages received from the client.
   *
   * @param msg The message received from the client.
   * @param client The connection from which the message originated.
   */
  public void handleMessageFromClient
    (Object msg, ConnectionToClient client)
  {
    String message = msg.toString();
    if (message.startsWith("#login ")) {
      String loginId = message.substring(7);
      if (client.getInfo("loginId") == null) {
        client.setInfo("loginId", loginId);
        System.out.println("A new client has connected to the server.");
        System.out.println("Message received: " + message + " from null.");
        System.out.println(loginId + " has logged on.");
      } else {
        try {
          client.sendToClient("Error: Already logged in.");
          client.close();
        } catch (IOException e) {
          System.out.println("Error closing client connection.");
        }
      }
    } else {
      String loginId = (String) client.getInfo("loginId");
      if (loginId != null) {
        System.out.println("Message received: " + message + " from " + loginId);
        this.sendToAllClients(loginId + ": " + message);
      } else {
        try {
          client.sendToClient("Error: You must login first.");
          client.close();
        } catch (IOException e) {
          System.out.println("Error closing client connection.");
        }
      }
    }
  }
    
  /**
   * This method overrides the one in the superclass.  Called
   * when the server starts listening for connections.
   */
  protected void serverStarted()
  {
    System.out.println
      ("Server listening for connections on port " + getPort());
  }
  
  /**
   * This method overrides the one in the superclass.  Called
   * when the server stops listening for connections.
   */
  protected void serverStopped()
  {
    System.out.println
      ("Server has stopped listening for connections.");
  }

  /**
   * This method is called when a client connects to the server.
   * It prints a message indicating that a client has connected.
   *
   * @param client The connection to the client that connected.
   */
  @Override
  protected void clientConnected(ConnectionToClient client) {
    System.out.println("Client connected: " + client);
  }

  /**
   * This method is called when a client disconnects from the server.
   * It prints a message indicating that a client has disconnected.
   *
   * @param client The connection to the client that disconnected.
   */
  @Override
  synchronized protected void clientDisconnected(ConnectionToClient client) {
    System.out.println("Client disconnected: " + client);
  }

  /**
   * Sends a message to all clients connected to the server.
   *
   * @param msg The message to be sent.
   */
  public void sendToAllClients(String msg) {
    super.sendToAllClients(msg);
  }

  /**
   * Quits the server gracefully.
   */
  public void quit() {
    try {
      close();
    } catch (IOException e) {
      System.out.println("Error closing the server.");
    }
    System.exit(0);
  }
  
  
  //Class methods ***************************************************
  
  /**
   * This method is responsible for the creation of 
   * the server instance (there is no UI in this phase).
   *
   * @param args[0] The port number to listen on.  Defaults to 5555 
   *          if no argument is entered.
   */
  public static void main(String[] args) 
  {
    int port = 0; //Port to listen on

    try
    {
      port = Integer.parseInt(args[0]); //Get port from command line
    }
    catch(Throwable t)
    {
      port = DEFAULT_PORT; //Set port to 5555
    }
	
    EchoServer sv = new EchoServer(port);
    
    try 
    {
      sv.listen(); //Start listening for connections
    } 
    catch (Exception ex) 
    {
      System.out.println("ERROR - Could not listen for clients!");
    }
  }
}
//End of EchoServer class
