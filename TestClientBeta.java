// First frame should allow user to connect to server, and enter username
// Second frame allows user to see game board and communicate moves to server


import java.awt.*;
import javax.swing.*;
import java.io.*;
import java.net.*;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

class TestClientBeta {
  
  private JButton sendButton, enterButton, clearButton;
  private JTextField connectServerTypeField, connectPortTypeField, roomTypeField, usernameTypeField;
  private JTextArea msgArea;  
  private JPanel roomPanel;
  private Socket mySocket;
  private BufferedReader input;
  private PrintWriter output;
  private boolean running = true;
  
  private boolean enterClick = false;
  private String server, port, room, username;
  private JFrame connectFrame, window;
  
  // --------------------------------------------------------------------------
  
  public static void main(String[] args) {
    new TestClientBeta().connectScreen();
  }
  
  public void connectScreen() {
    connectFrame = new JFrame("Connect Screen");
    JPanel connectPanel = new JPanel();
    connectPanel.setLayout(new GridLayout(2,0));
    
    enterButton = new JButton("Enter");
    enterButton.addActionListener(new EnterButtonListener());
    
    JLabel errorEnterLabel = new JLabel("");
    
    connectServerTypeField = new JTextField(20);
    connectPortTypeField = new JTextField(10);
    
    connectPanel.add(connectServerTypeField);
    connectPanel.add(errorEnterLabel);
    connectPanel.add(connectPortTypeField);
    connectPanel.add(enterButton);
    
    connectFrame.add(BorderLayout.NORTH,connectPanel);
    
    connectFrame.setSize(400,400);
    connectFrame.setVisible(true);    
  }
  // --------------------------------------------------------------------------
  class EnterButtonListener implements ActionListener { 
    public void actionPerformed(ActionEvent event)  {
      
      try {
        server = connectServerTypeField.getText();
        port = connectPortTypeField.getText();
        if (server != null && !"".equals(server)) {
          connect(server,Integer.parseInt(port));
          connectFrame.dispose();
          new Thread(new Runnable(){
            public void run(){
              new TestClient().go();
            }
          }).start(); 
        } else {
          System.out.println("No IP entered");
        }
      } catch (NumberFormatException e) {
        System.out.println("Not a valid port!");
        e.printStackTrace();
      }       
    }     
  }
  // --------------------------------------------------------------------------
  public Socket connect(String ip, int port) { 
    System.out.println("Attempting to make a connection..");
    
    try {
      
      // change ip address
      mySocket = new Socket(ip, port); //attempt socket connection (local address). This will wait until a connection is made
      
      InputStreamReader stream1= new InputStreamReader(mySocket.getInputStream()); //Stream for network input
      input = new BufferedReader(stream1);     
      output = new PrintWriter(mySocket.getOutputStream()); //assign printwriter to network stream
      
    } catch (IOException e) {  //connection error occured
      System.out.println("Connection to Server Failed");
      e.printStackTrace();
    }
    
    System.out.println("Connection made.");
    return mySocket;
  }
  
  // --------------------------------------------------------------------------
  // --------------------------------------------------------------------------
  // --------------------------------------------------------------------------
  
  public void go() {
    window = new JFrame("Join Room");
    roomPanel = new JPanel();
    roomPanel.setLayout(new GridLayout(2,0));
    
    sendButton = new JButton("Send");
    sendButton.addActionListener(new SendButtonListener());
    
    /*
    clearButton = new JButton("QUIT");
    clearButton.addActionListener(new QuitButtonListener());
    */
    
    JLabel errorLabel = new JLabel("");
    
    roomTypeField = new JTextField(20);
    usernameTypeField = new JTextField(20);
    
   // msgArea = new JTextArea();
    
    roomPanel.add(roomTypeField);
    roomPanel.add(errorLabel);
    roomPanel.add(usernameTypeField);
    roomPanel.add(sendButton);
    
   // southPanel.add(clearButton);
    
   // window.add(BorderLayout.CENTER,msgArea);
    window.add(BorderLayout.NORTH,roomPanel);
    
    window.setSize(400,400);
    window.setVisible(true);
    
    // after connecting loop and keep appending[.append()] to the JTextArea    
    // readConnectFromServer();
    
  }  
  // --------------------------------------------------------------------------
  class SendButtonListener implements ActionListener { 
    public void actionPerformed(ActionEvent event)  {

      room = roomTypeField.getText();
      username = usernameTypeField.getText();
      
      output.println("JOINROOM " + room);
      output.flush();
      String msg;
      do {
        msg = readConnectFromServer();
      } while (msg == null);
      if (msg != null) {
        if ("OK".equalsIgnoreCase(msg)) {
          output.println("CHOOSENAME " + username);
          output.flush();
          do {
            msg = readConnectFromServer();
          } while (msg == null);
          if (msg != null) {
            if ("OK".equalsIgnoreCase((msg))) {
              //we gucci here
              //runGameLoop();
            } else {
              System.out.println(msg);
            }
          } else {
            System.out.println("Error");
          }
        } else {
          System.out.println(msg); //Server error message
        }
      } else {
        System.out.println("Error");
      }
                   
      roomTypeField.setText(""); 
      usernameTypeField.setText(""); 
    }     
  }
  // --------------------------------------------------------------------------
  // --------------------------------------------------------------------------
  // --------------------------------------------------------------------------
  
  
  //Starts a loop waiting for server input and then displays it on the textArea
  public String readConnectFromServer() { 
    
    while(running) {  // loop unit a message is received
      try {
        
        if (input.ready()) { //check for an incoming messge
          String msg;          
          msg = input.readLine(); //read the message
          return msg;
        } else {
          return null;
        }
        
      }catch (IOException e) { 
        System.out.println("Failed to receive msg from the server");
        e.printStackTrace();
        return null;
      }
    }
    
    try {  //after leaving the main loop we need to close all the sockets
      input.close();
      output.close();
      mySocket.close();
    }catch (Exception e) { 
      System.out.println("Failed to close socket");
    }
    
    return null;
    
  }
  
  //****** Inner Classes for Action Listeners ****
  // --------------------------------------------------------------------------
  // --------------------------------------------------------------------------
  // --------------------------------------------------------------------------
  
  
  // --------------------------------------------------------------------------
  /*
  // QuitButtonListener - Quit the program
  class QuitButtonListener implements ActionListener { 
    public void actionPerformed(ActionEvent event)  {
      running=false;
      msgArea.append("Program has quit");
    }     
  }
  */
  
  
}