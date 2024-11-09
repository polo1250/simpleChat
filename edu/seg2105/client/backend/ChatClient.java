// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

package edu.seg2105.client.backend;

import ocsf.client.*;

import java.io.*;

import edu.seg2105.client.common.*;

/**
 * This class overrides some of the methods defined in the abstract
 * superclass in order to give more functionality to the client.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;
 * @author Fran&ccedil;ois B&eacute;langer
 */
public class ChatClient extends AbstractClient
{
  //Instance variables **********************************************
  
  /**
   * The interface type variable.  It allows the implementation of 
   * the display method in the client.
   */
  ChatIF clientUI;

  /**
   * The login ID of the client.
   */
  private final String loginId;

  /**
   * Flag to indicate if the client is terminating.
   */
  private boolean isTerminating = false;

  
  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the chat client.
   *
   * @param host The server to connect to.
   * @param port The port number to connect on.
   * @param clientUI The interface type variable.
   * @param loginId The login ID of the client.
   */
  
  public ChatClient(String loginId, String host, int port, ChatIF clientUI)
    throws IOException 
  {
    super(host, port); //Call the superclass constructor
    this.clientUI = clientUI;
    this.loginId = loginId;
    openConnection();
  }

  
  //Instance methods ************************************************
    
  /**
   * This method handles all data that comes in from the server.
   *
   * @param msg The message from the server.
   */
  public void handleMessageFromServer(Object msg) 
  {
    clientUI.display(msg.toString());
    
    
  }

  /**
   * This method handles all data coming from the UI            
   *
   * @param message The message from the UI.    
   */
  public void handleMessageFromClientUI(String message)
  {
    try
    {
      sendToServer(message);
    }
    catch(IOException e)
    {
      clientUI.display
        ("Could not send message to server.  Terminating client.");
      quit();
    }
  }
  
  /**
   * This method terminates the client.
   */
  public void quit()
  {
    isTerminating = true;
    try
    {
      closeConnection();
    }
    catch(IOException e) {}
    System.exit(0);
  }

  /**
   * This method is called when the connection to the server is closed.
   */
  @Override
  protected void connectionEstablished() {
    try {
      sendToServer("#login " + loginId);
      clientUI.display(loginId + " has logged on.");
    } catch (IOException e) {
      clientUI.display("Error sending login message to server.");
      quit();
    }
  }

  /**
   * This method is called when the connection to the server is closed.
   * It prints a message indicating the server has shut down and terminates the client.
   */
  @Override
  protected void connectionClosed() {
    if (isTerminating) {
      clientUI.display("Client is terminating.");
      System.exit(0);
    } else {
      clientUI.display("Connection closed.");
    }
  }

  /**
   * This method is called when an exception occurs in the connection to the server.
   * It prints a message indicating the server has shut down due to an exception and terminates the client.
   *
   * @param exception the exception that was raised.
   */
  @Override
  protected void connectionException(Exception exception) {
    clientUI.display("Server has shut down due to an exception. Terminating client.");
    System.exit(0);
  }

  /**
   * Logs off the client from the server.
   */
  public void logoff() {
    if (isConnected()) {
      try {
        closeConnection();
      } catch (IOException e) {
        clientUI.display("Error logging off.");
      }
    } else {
      clientUI.display("Already logged off.");
    }
  }

  /**
   * Logs in the client to the server.
   */
  public void login() {
    if (!isConnected()) {
      try {
        openConnection();
      } catch (IOException e) {
        clientUI.display("Error logging in.");
      }
    } else {
      clientUI.display("Already connected.");
    }
  }
}
//End of ChatClient class
