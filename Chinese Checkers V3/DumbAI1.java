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

class DumbAI1 {
  
  private JButton sendButton, enterButton, clearButton;
  private JTextField connectServerTypeField, connectPortTypeField, roomTypeField, usernameTypeField;
  private JTextArea msgArea;  
  private JPanel roomPanel, connectPanel;
  private Socket mySocket;
  private BufferedReader input;
  private PrintWriter output;
  private boolean running = true;
  
  private boolean enterClick = false;
  private String server, port, room, username;
  private JFrame connectFrame;
  
  // --------------------------------------------------------------------------  
  public static void main(String[] args) {
    new DumbAI1().connectScreen();
  }
  // --------------------------------------------------------------------------  
  public void connectScreen() {
    connectFrame = new JFrame("Connect Screen");
    connectPanel = new JPanel();
    
    enterButton = new JButton("Enter");
    enterButton.addActionListener(new EnterButtonListener());
    
    connectPanel.setLayout(new BoxLayout(connectPanel, BoxLayout.Y_AXIS));    
    
    JLabel serverLabel = new JLabel ("Please Enter Server:");
    JLabel portLabel = new JLabel ("Please Enter Port:");
    JLabel errorEnterLabel = new JLabel("");
    
    connectServerTypeField = new JTextField(20);
    connectServerTypeField.setMaximumSize(new Dimension(Integer.MAX_VALUE, connectServerTypeField.getPreferredSize().height) );
    connectPortTypeField = new JTextField(10);
    connectPortTypeField.setMaximumSize(new Dimension(Integer.MAX_VALUE, connectPortTypeField.getPreferredSize().height) );
    
    connectPanel.add(serverLabel);
    connectPanel.add(connectServerTypeField);
    connectPanel.add(portLabel);
    connectPanel.add(connectPortTypeField);
    connectPanel.add(enterButton);
    
    connectFrame.add(connectPanel);
    
    connectFrame.setSize(400,400);
    connectFrame.setVisible(true);    
  }
  // --------------------------------------------------------------------------
  class EnterButtonListener implements ActionListener { 
    public void actionPerformed(ActionEvent event)  {
      
      try {
        server = connectServerTypeField.getText();
        port = connectPortTypeField.getText();
        
        if (server != null && !server.equals("")) {
          connect(server,Integer.parseInt(port));
        } else {
          System.out.println("No IP Entered");
        }
        
      } catch (NumberFormatException e) {
        System.out.println("Invalid Port");
        e.printStackTrace();
      }
      
    }     
  }
  // --------------------------------------------------------------------------
  public Socket connect(String ip, int port) { 
    
    System.out.println("Attempting to make a connection..");
    
    try {
      mySocket = new Socket(ip, port);
      
      InputStreamReader stream1= new InputStreamReader(mySocket.getInputStream());
      input = new BufferedReader(stream1);     
      output = new PrintWriter(mySocket.getOutputStream());
      go();
      
    } catch (IOException e) {
      System.out.println("Connection to Server Failed");
      e.printStackTrace();
    }
    
    System.out.println("Connection made.");
    return mySocket;
  }
  // --------------------------------------------------------------------------  
  public void go() {
    roomPanel = new JPanel();
    roomPanel.setLayout(new BoxLayout(roomPanel, BoxLayout.Y_AXIS));    
    
    JLabel roomLabel = new JLabel("Please Enter Room Name:");
    JLabel usernameLabel = new JLabel("Please Enter Username:");
    
    roomTypeField = new JTextField(20);
    roomTypeField.setMaximumSize(new Dimension(Integer.MAX_VALUE, roomTypeField.getPreferredSize().height) );
    usernameTypeField = new JTextField(20);
    usernameTypeField.setMaximumSize(new Dimension(Integer.MAX_VALUE, usernameTypeField.getPreferredSize().height) );
    
    sendButton = new JButton("Send");
    sendButton.addActionListener(new SendButtonListener());
    
    // msgArea = new JTextArea();
    
    roomPanel.add(roomLabel);
    roomPanel.add(roomTypeField);
    roomPanel.add(usernameLabel);
    roomPanel.add(usernameTypeField);
    roomPanel.add(sendButton);
    
    // window.add(BorderLayout.CENTER,msgArea);
    
    connectFrame.remove(connectPanel);
    connectFrame.add(BorderLayout.NORTH,roomPanel);
    connectFrame.setVisible(true);
    connectFrame.repaint();
    
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
            
              connectFrame.dispose();
              int[][] piecePositions = readBoardFromServer();
              int[][] board = new int[26][18];
                      boolean playing = true;
                        int[][] tempPieces = new int[2][10];
                        for (int i = 0; i < 10; i++){
                          tempPieces[0][i] = piecePositions[i][0];
                          tempPieces[1][i] = piecePositions[i][1];
                        }
                      for (int rows = 0; rows < 26; rows++){
                        for (int col = 0; col < 18; col++) {
                          board[rows][col] = -1;
                        }
                      }

                      // Setup the inside of board
                      for (int rows = 13; rows < 17; rows++){
                        for (int col = 5; col < 14; col++){
                          board[rows][col] = 0;
                        }
                      }
                      for (int rows = 17; rows < 25; rows++){
                        for (int col = 5; col < 14; col++){
                          board[rows][col] = 0;
                        }
                      }
                      for (int i = 10; i < 60; i++){
                            board[piecePositions[i][0]][piecePositions[i][1]] = -1;
                      }
                      String moves = "";
                      int i = 1;
                      while(playing){
                      if (board[tempPieces[0][i] + 1][tempPieces[1][i]] == 0) {
                          board[tempPieces[0][i]][tempPieces[1][i]] = board[tempPieces[0][i]][tempPieces[1][i]];
                          board[tempPieces[0][i]][tempPieces[1][i]] = 0;
                          tempPieces[0][i]++;
                          playing = false;
                      } else if (board[tempPieces[0][i] + 2][tempPieces[1][i]] == 0) {
                          board[tempPieces[0][i] + 2][tempPieces[1][i]] = board[tempPieces[0][i]][tempPieces[1][i]];
                          board[tempPieces[0][i]][tempPieces[1][i]] = 0;
                          tempPieces[0][i] += 2;
                          playing = true;
                          }
                      }
                      
            } else {
              System.out.println(msg);
            }
          } else {
            System.out.println("Error");
          }
        } else {
          System.out.println(msg);
        }
      } else {
        System.out.println("Error");
      }
      
      roomTypeField.setText(""); 
      usernameTypeField.setText(""); 
    }     
  }
  // --------------------------------------------------------------------------
  public String readConnectFromServer() { 
    
    //while(running) {
    try {
      
      if (input.ready()) {
        String msg;          
        msg = input.readLine();
        return msg;
      } else {
        return null;
      }
      
    }catch (IOException e) { 
      System.out.println("Failed to receive msg from the server");
      e.printStackTrace();
      return null;
    }
    //}
    
    
    // important code, should close when window is closed/game ends
    
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
  // --------------------------------------------------------------------------
  public int[][] readBoardFromServer() {
    String inputMessage;
    do {
    inputMessage = readConnectFromServer();
    } while (inputMessage == null);
    int activePlayers = Character.getNumericValue(inputMessage.charAt(6));
    int inactivePlayers = Character.getNumericValue(inputMessage.charAt(8));
    String boardArrangement = inputMessage.substring(10);
    
    int[][] piecePositions = new int[60][2];    
    String[] coordinateList = boardArrangement.split(" ");
    
    for (int i = 0; i < 60; i++) {
      
      String[] coordinates = coordinateList[i].split(",");
      
      for (int j = 0; j < 2; j++) {
        if (j==0) {
          String xCoordinate = coordinates[j].substring(1);
          piecePositions[i][j] = Integer.parseInt(xCoordinate);
        } else {
          String yCoordinate = coordinates[j].substring(0,1);
          piecePositions[i][j] = Integer.parseInt(yCoordinate);
        }
      }
    }
    
    return piecePositions;
  }
  // --------------------------------------------------------------------------
  
}