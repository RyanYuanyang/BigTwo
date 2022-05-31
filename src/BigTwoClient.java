import java.io.*;
import java.net.*;

import javax.swing.JOptionPane;
import java.util.ArrayList;
import java.util.Arrays;


/**
 * The BigTwoClient class implements the NetworkGame interface. It is used to model a Big 
 * Two game client that is responsible for establishing a connection and communicating with 
 * the Big Two game server.
 * 
 * @author Tu Ryan Yuanyang
 *
 */
public class BigTwoClient implements NetworkGame{
	private BigTwo game; // a BigTwo object for the Big Two card game.
	private BigTwoUI gui; // a BigTwoGUI object for the Big Two card game.
	private Socket sock; // a socket connection to the game server.
	private ObjectOutputStream oos; // an ObjectOutputStream for sending messages to the server.
	private int playerID; // an integer specifying the playerID (i.e., index) of the local player.
	private String playerName; // a string specifying the name of the local player.
	private String serverIP; // a string specifying the IP address of the game server.
	private int serverPort; // an integer specifying the TCP port of the game server
	private ArrayList<String> defaultNames;

	/**
	 * a constructor for creating a Big Two client
	 * 
	 * @param game A BigTwo object associated with this client
	 * @param gui A BigTwoGUI object associated the BigTwo object
	 */
	BigTwoClient(BigTwo game, BigTwoUI gui){
		defaultNames = new ArrayList<String>();
		defaultNames.add("Fluffy Cookie");
		defaultNames.add("The Real Groot");
		defaultNames.add("Twisted Fate");
		defaultNames.add("Chow Yun-Fat");
		defaultNames.add("Deadshot");
		defaultNames.add("Tornado");
		defaultNames.add("Turbine");
		defaultNames.add("Fireball");
		defaultNames.add("Glazier");
		defaultNames.add("Monsoon");
		defaultNames.add("Onyx");
		defaultNames.add("Barrage");

		this.game = game;
		this.gui = gui;

		String name = JOptionPane.showInputDialog("Please input your name:");
		if (name == null || name.trim().isEmpty() == true) {
			int i = (int)(Math.random() * defaultNames.size());
			name = defaultNames.get(i);

		}
		gui.SetTitle("BigTwo ("+name+")");
		setPlayerName(name);
		connect();
		gui.repaint();
				
	}
	public static void main(String[] args) {
		
	}
	
	/**
	 * a method for getting the playerID (i.e., index) of the local player.
	 * 
	 * @return playerID The player ID of the local player
	 */
	public int getPlayerID() {
		
		return playerID;
	}
	/**
	 * a method for setting the playerID (i.e., index) of the local player. This method is called from the parseMessage() method when a 
	 * message of the type PLAYER_LIST is received from the game server. 
	 */
	public void setPlayerID(int playerID) {
		this.playerID = playerID;
		
	}
	/**
	 * a method for getting the name of the local player
	 * 
	 * @return playerName The name of the local player
	 */
	public String getPlayerName() {
		return playerName;
	}
	/**
	 * a method for setting the name of the local player. 
	 * 
	 * @param playerName The name of the player
	 */
	public void setPlayerName(String playerName) {
		this.playerName = playerName;
		
	}
	/**
	 * a method for getting the IP address of the game server.
	 * 
	 * @return serverIP The IP address of the server
	 */
	public String getServerIP() {
		return serverIP;
	}
	/**
	 * a method for setting the IP address of the game server.
	 * 
	 * @param serverIP The IP address of the server
	 */
	public void setServerIP(String serverIP) {
		this.serverIP = serverIP;
	}
	/**
	 * a method for getting the TCP port of the game server
	 * 
	 * @return serverPort The TCP port of the game server
	 */
	public int getServerPort() {
		return serverPort;
	}
	/**
	 * a method for setting the TCP port of the game server.
	 * 
	 * @param serverPort The TCP port of the game server.
	 */
	public void setServerPort(int serverPort) {
		this.serverPort = serverPort;
	}
	/**
	 * a method for making a socket connection with the game server.
	 * 
	 * @param message
	 *            the specified message received from the server 
	 */
	public void connect() {
		
		serverIP = "127.0.0.1";
		serverPort = 2396;
		try {
			sock = new Socket(serverIP, serverPort);

			this.oos = new ObjectOutputStream(sock.getOutputStream());
			Runnable serverHandler = new ServerHandler();
			Thread receive = new Thread(serverHandler);
			receive.start();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		sendMessage(new CardGameMessage(CardGameMessage.JOIN, -1, playerName));
		sendMessage(new CardGameMessage(CardGameMessage.READY, -1, null));
	}
	/**
	 * a method for parsing the messages received from the game server.
	 */
	public void parseMessage(GameMessage message) {
		setPlayerID(message.getPlayerID());
		switch(message.getType()) {
		case CardGameMessage.PLAYER_LIST:
			gui.DisableConnect();
			gui.setActivePlayer(playerID);

			for(int i = 0; i < 4; i++) {
				game.getPlayerList().get(i).setName(((String[])message.getData())[i]);
			}
			gui.repaint();
			break;	
			
		case CardGameMessage.JOIN:
			game.getPlayerList().get(message.getPlayerID()).setName((String)message.getData());
			gui.repaint();
			gui.printMsg((String)message.getData()+" has joined the game!\n");
			break;
		
		case CardGameMessage.FULL:
			System.out.println("Full received");
			gui.printMsg("Failed to join (the server is full)\nYou can click game->connect to try again\n");
			gui.repaint();
			break;
			
		case CardGameMessage.QUIT:
			System.out.println("Quit received");	
			game.getPlayerList().get(message.getPlayerID()).setName(null);;
			gui.repaint();
			if(!game.endOfGame()) {
				gui.disable();
				sendMessage(new CardGameMessage(CardGameMessage.READY, -1, null));
			}
			break;
			
		case CardGameMessage.READY:
			System.out.println("Ready received");	
			gui.printMsg(game.getPlayerList().get(message.getPlayerID()).getName()+" is ready"+"\n");
			gui.repaint();
			break;
		
		case CardGameMessage.START:
			gui.printMsg("All players are ready, game Start!\n\n");
			game.start((Deck)message.getData());
			gui.repaint();
			break;
		
		case CardGameMessage.MOVE:
			game.checkMove(message.getPlayerID(),(int[])message.getData());
			if (gui.GetActivePlayer() == game.getCurrentPlayerIdx()) {
				gui.enable();
			}
			gui.repaint();
			break;
			
		case CardGameMessage.MSG:
			gui.printChat(game.getPlayerList().get(message.getPlayerID()).getName()+(String)message.getData()+"\n");
			gui.repaint();
			break;
		}
	}
	/**
	 * a method for sending the specified message to the game server.
	 * 
	 * @param message
	 *            the specified message to be sent the server
	 */
	public void sendMessage(GameMessage message) {
		try {
			oos.writeObject(message);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	/**
	 * an inner class that implements the Runnable interface.
	 *
	 */
	private class ServerHandler implements Runnable{
		
		public void run() {
			CardGameMessage msg;
			try {
				ObjectInputStream ois = new ObjectInputStream(sock.getInputStream()); 
				while ((msg = (CardGameMessage) ois.readObject()) != null) {
					parseMessage(msg);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			gui.repaint();

		}
		
	}
	
	
}
