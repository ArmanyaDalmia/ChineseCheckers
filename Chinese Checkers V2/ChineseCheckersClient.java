/*
 * ChineseCheckersClient.java
 * Author: Armanya Dalmia
 * Date: May.7, 2019
 * Purpose: User Interface to connect to server and run Chinese Checkers AI
 */

// Necessary Imports
import java.awt.Dimension;
import java.awt.BorderLayout;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.Socket;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JTextArea;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.BoxLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

// Class Declaration
class ChineseCheckersClient {
  
  // Variables
  private JButton sendButton, enterButton;
  private JTextField connectServerTypeField, connectPortTypeField, roomTypeField, usernameTypeField;
  private JPanel connectPanel, roomPanel;
  private Socket mySocket;
  private BufferedReader input;
  private PrintWriter output;
  private String server, port, room, username;
  private JFrame window;

  // Main method
  public static void main(String[] args) {
    new ChineseCheckersClient().connectScreen();
  }

  /**
   * connectScreen
   * Creates initial screen to connect with server
   */
  public void connectScreen() {
    
    // Creates the frame and panel
    window = new JFrame("Connect Screen");
    connectPanel = new JPanel();
    
    // Creates the enter button and adds actionlistener
    enterButton = new JButton("Enter");
    enterButton.addActionListener(new EnterButtonListener());
    
    // Sets Layout for the panel
    connectPanel.setLayout(new BoxLayout(connectPanel, BoxLayout.Y_AXIS));    
    
    // Creates necessary JLabels
    JLabel serverLabel = new JLabel ("Please Enter Server:");
    JLabel portLabel = new JLabel ("Please Enter Port:");
    
    // Creates and formats the typefields for server name and port number
    connectServerTypeField = new JTextField(20);
    connectServerTypeField.setMaximumSize(new Dimension(Integer.MAX_VALUE, connectServerTypeField.getPreferredSize().height) );
    connectPortTypeField = new JTextField(10);
    connectPortTypeField.setMaximumSize(new Dimension(Integer.MAX_VALUE, connectPortTypeField.getPreferredSize().height) );
    
    // Adds all labels and typefields to the panel
    connectPanel.add(serverLabel);
    connectPanel.add(connectServerTypeField);
    connectPanel.add(portLabel);
    connectPanel.add(connectPortTypeField);
    connectPanel.add(enterButton);
    
    // Adds panel to the frame
    window.add(connectPanel);
    
    // Sets size and visibility of the frame
    window.setSize(400,400);
    window.setVisible(true);    
  }
  
  /**
   * connect
   * The thread for the connection between client and server
   * @param ip The local ip address of the server to connect 
   * @param port The port to connect 
   * @return Socket returns the connection
   */
  public Socket connect(String ip, int port) { 
    
    // Text running to indicate method is running
    System.out.println("Attempting to make a connection..");
    
    // Try-catch statement to create new socket use parameters and create input and output
    try {
      
      // Creates socket object with corresponding parameters as those that wer passed in
      mySocket = new Socket(ip, port);
      
      // Creates and initializes new inputstream and printwriter
      InputStreamReader stream1= new InputStreamReader(mySocket.getInputStream());
      input = new BufferedReader(stream1);     
      output = new PrintWriter(mySocket.getOutputStream());
      
      // Runs program to create new panel and connect to game room
      connectRoom();
      
    } catch (IOException e) {
      
      // Indicates connection failed and shows where error occurred
      System.out.println("Connection to Server Failed");
      e.printStackTrace();
    }
    
    // Shows connection has been made and returns the socket
    System.out.println("Connection made.");
    return mySocket;
  }

  /**
   * connectRoom
   * Once connected, presents the screen to join an actual game room
   */
  public void connectRoom() {
    
    // Creates new panel and sets layout
    roomPanel = new JPanel();
    roomPanel.setLayout(new BoxLayout(roomPanel, BoxLayout.Y_AXIS));    
    
    // Creates necessary JLabels
    JLabel roomLabel = new JLabel("Please Enter Room Name:");
    JLabel usernameLabel = new JLabel("Please Enter Username:");
    
    // Creates and formats the typefields for room name and username
    roomTypeField = new JTextField(20);
    roomTypeField.setMaximumSize(new Dimension(Integer.MAX_VALUE, roomTypeField.getPreferredSize().height) );
    usernameTypeField = new JTextField(20);
    usernameTypeField.setMaximumSize(new Dimension(Integer.MAX_VALUE, usernameTypeField.getPreferredSize().height) );
    
    // Creates new JButton and associates corresponding actionlistener
    sendButton = new JButton("Send");
    sendButton.addActionListener(new SendButtonListener());
    
    // Adds labels and typefields to panel
    roomPanel.add(roomLabel);
    roomPanel.add(roomTypeField);
    roomPanel.add(usernameLabel);
    roomPanel.add(usernameTypeField);
    roomPanel.add(sendButton);
    
    // Removes previous panel, adds new panel, sets frame as visible and repaints
    window.remove(connectPanel);
    window.add(BorderLayout.NORTH,roomPanel);
    window.setVisible(true);
    window.repaint();    
  }  

  /**
   * readFromServer
   * Reads message sent from the server and returns the string
   * @return String returns the message sent from the server
   */
  public String readFromServer() { 
    
    // Try-catch statement that reads string from server when a message has been sent from the server
    try {
      
      // If there is an avialable input, stores inpit as a string and returns the string
      if (input.ready()) {
        String message;          
        message = input.readLine();
        return message;
      } else {
        return null;
      }
      
    }catch (IOException e) { 
      
      // Indicates exception and indicates where the exception occurred
      System.out.println("Failed to receive message from the server");
      e.printStackTrace();
      return null;
    }
    
    // important code, should close when window is closed/game ends
    // needs to be moved and added to correct place
    // not sure where to add
    /*
     try {
     input.close();
     output.close();
     mySocket.close();
     }catch (Exception e) { 
     System.out.println("Failed to close socket");
     }
     */    
  }  

  /**
   * readBoardFromServer
   * Reads string that indicates piece positions on the board and converts coordinates into a 2D array
   * @return int[][] returns 2D array of coordinates
   */
  public int[][] readBoardFromServer() {
    
    // Input message sent from server that indicates piece positions
    String inputMessage;
    
    // Keeps running until there is a message
    do {
    inputMessage = readFromServer();
    } while (inputMessage == null);
    
    // Reads string and stores number of active & inactive players and the actual piece positions
    int activePlayers = Character.getNumericValue(inputMessage.charAt(6));
    int inactivePlayers = Character.getNumericValue(inputMessage.charAt(8));
    String boardArrangement = inputMessage.substring(10);
    
    // Initializes 2D array for piece positions
    // 60 rows for 60 pieces
    // 2 rows for x,y coordinates
    int[][] piecePositions = new int[60][2];    
    
    // Initializes String array composed of individual coordinates in form of (a,b)
    String[] coordinateList = boardArrangement.split(" ");
    
    // Runs through all 60 coordinates in coordinate notation (60 pieces) and stores each coordinate into a 2D array
    // The x-coordinate is stored in one column, the y-coordinate is stored in the other
    for (int i = 0; i < 60; i++) {
      
      // Takes each pair of coordinates and stores the 2 values in a string array
      String[] coordinates = coordinateList[i].split(",");
      
      // Stores the first value in the string array as the x-coordinate and the second value as the y-coordinate
      for (int j = 0; j < 2; j++) {
        
        // First value would be the x-coordinate, otherwise would be the y-cordinate
        if (j==0) {
          
          // String include leftost bracket so creates a string that only contains the number
          // Parses said number and stores it as an integer in the 2D array
          String xCoordinate = coordinates[j].substring(1);
          piecePositions[i][j] = Integer.parseInt(xCoordinate);
          
        } else {
          
          // String include rightmost bracket so creates a string that only contains the number
          // Parses said number and stores it as an integer in the 2D array
          String yCoordinate = coordinates[j].substring(0,1);
          piecePositions[i][j] = Integer.parseInt(yCoordinate);
        }
      }
      
    }
    
    // Returns the 2D array of coordinates
    return piecePositions;
  }  

  // --------------------------------------INNER CLASSES------------------------------------
  /**
   * EnterButtonListener.java
   * Author: Armanya Dalmia
   * Date: May.7, 2019
   * Determines when button is clicked and attempts to connect with the server
   */
  private class EnterButtonListener implements ActionListener { 
    public void actionPerformed(ActionEvent event)  {
      
      // Try-catch statement to try and connect with server
      try {
        
        // Takes user input and stores as serevr and port
        server = connectServerTypeField.getText();
        port = connectPortTypeField.getText();
        
        // If server has been entered, attempts to connect
        if (server != null && !server.equals("")) {
          connect(server,Integer.parseInt(port));
        } else {
          System.out.println("No IP Entered");
        }
        
      } catch (NumberFormatException e) {
        
        // Displays that error occurred and indicates where
        System.out.println("Invalid Port");
        e.printStackTrace();
      }
      
    }     
  }

  /**
   * SendButtonListener.java
   * Author: Armanya Dalmia
   * Date: May.7, 2019
   * Determines when button is clicked and attempts to join room
   */
  class SendButtonListener implements ActionListener { 
    public void actionPerformed(ActionEvent event)  {
      
      // Sets the room name and username according to what user-input
      room = roomTypeField.getText();
      username = usernameTypeField.getText();
      
      // Sends room-name to server
      output.println("JOINROOM " + room);
      output.flush();
      
      // Initializes message to be received from the server
      String message;
      
      // Keeps running until there is input from the server
      do {
        message = readFromServer();
      } while (message == null);
      
      // If the message is "OK" sends username
      if (message.equals("OK")) {
        
        // Sends username to server
        output.println("CHOOSENAME " + username);
        output.flush();
        
        // Keeps running until there is input from the server
        do {
          message = readFromServer();
        } while (message == null);
        
        // If the message is "OK" then runs disposes of window and runs game code
        if (message.equals(("OK"))) {
          
          // Run game code in here
          
          window.dispose();
          
          // If message is not "OK" then displays the error according to the server and resets the typefields
        } else {
          System.out.println(message);
          roomTypeField.setText(""); 
          usernameTypeField.setText(""); 
        }
        
        // If message is not "OK" then displays the error according to the server and resets the typefields
      } else {
        System.out.println(message);
        roomTypeField.setText(""); 
        usernameTypeField.setText(""); 
      }
      
    }     
  }
  // ---------------------------------------------------------------------------------------
  
}