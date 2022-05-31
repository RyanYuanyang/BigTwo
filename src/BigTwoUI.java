import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.time.format.DateTimeFormatter;  
import java.time.LocalDateTime;   
/**
 * The BigTwoGUI class implements the CardGameUI interface. It is used to build a GUI for 
 * the Big Two card game and handle all user actions. 
 * 
 * @author Tu Yuanyang
 */
public class BigTwoUI implements CardGameUI{
	//a Big Two card game associates with this GUI
	private BigTwo game;
	//a boolean array indicating which cards are being selected
	private boolean[] selected;
	//an integer specifying the index of the active player.
	private int activePlayer = -1;
	//the main window of the application
	private JFrame frame;
	//a panel for showing the cards of each player and the cards played on the table
	private JPanel bigTwoPanel;
	//a Play button for the active player to play the selected cards
	private JButton playButton;
	//a Pass button for the active player to pass his/her turn to the next player.
	private JButton passButton;
	//a text area for showing the current game status as well as end of game messages
	private JTextArea msgArea;
	//a text area for showing chat messages sent by the players
	private JTextArea chatArea;
	//a text field for players to input chat messages
	private JTextField chatInput;
	//a global variable storing the game title
	static public final String title = "BigTwo";
	//an array of avatar images
	private Image avatars[];
	//an array of card images
	private Image cards[][];
	// the list of players
	private ArrayList<CardGamePlayer> playerList;
	
	public void SetTitle(String title) {
		frame.setTitle(title);
	}
	
	//A subclass of JPanel inside BigTwoUI to design the ChatBox
	private class ChatBox extends JPanel implements ActionListener{
		
		//Create a ChatBox with message area and chat area
		public ChatBox() {

			this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
			chatArea = new JTextArea(20,32);
			msgArea = new JTextArea(20,32);
			chatInput = new JTextField(25);
			
	        Font msgFont = new Font("Monospaced", Font.BOLD, 14);
	        Font chatFont = new Font("Dialog", Font.PLAIN, 14);
	        
	        msgArea.setFont(msgFont);
	        chatArea.setFont(chatFont);
	        
			DefaultCaret caret1 = (DefaultCaret)chatArea.getCaret();
			caret1.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);	
			DefaultCaret caret2 = (DefaultCaret)msgArea.getCaret();
			caret2.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);	
			
		    JScrollPane scroller1 = new JScrollPane(msgArea);
		    scroller1.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		    JScrollPane scroller2 = new JScrollPane(chatArea);
		    scroller2.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
	
			msgArea.setEditable(false);
			chatArea.setEditable(false);

			chatArea.setLineWrap(true);
			msgArea.setLineWrap(true);
			
			chatArea.setForeground(Color.BLUE);
			msgArea.setForeground(Color.BLACK);

			this.add(scroller1);
			this.add(scroller2);
			
			JPanel Message_Panel = new JPanel();
			JLabel label = new JLabel("Message: ");

			Message_Panel.add(label);
			Message_Panel.add(chatInput);
			chatInput.addActionListener(this);
			this.add(Message_Panel);

		}
	
		//An actionPerformed method used to print the chat of players
		public void actionPerformed(ActionEvent e) {
			
			game.GetClient().sendMessage(new CardGameMessage(CardGameMessage.MSG,-1,chatInput.getText()));
			
			chatInput.setText("");
		}
		
	}
	private JMenuItem connect;//a instance variable for the connect button

	/**
	 * a method used to disable the connect button.
	 */
	public void DisableConnect(){
		connect.setEnabled(false);
	}
	//A subclass of JMenuBar used to design the menu, with restart and quit item
	private class MenuBar extends JMenuBar{
		
		//Create a MenuBar
		public MenuBar() {
			JMenu game_M = new JMenu("Game");
			JMenu message_M = new JMenu("Message");

			JMenuItem quit = new JMenuItem("Quit");
			quit.addActionListener(new QuitMenuItemListener());
			connect = new JMenuItem("Connect");
			connect.addActionListener(new ConnectMenuItemListener());
			game_M.add(quit);
			game_M.add(connect);
			
			JMenuItem clearMsg = new JMenuItem("Clear Chat");
			clearMsg.addActionListener(new ClearMsgMenuItemListener());
			message_M.add(clearMsg);
			
			this.add(game_M);
			this.add(message_M);
		}
	}
	private Border border = BorderFactory.createLineBorder(Color.BLACK, 1);//Set a border to separate panels
	private Font font = new Font("Comic Sans MS", Font.BOLD, 14);//The font of player name
	private Image back = new ImageIcon("Images/b.gif").getImage();//Card back image
	
	//A method used to judge whether the mouse is on a card
	private boolean contains(int x, int y, int i) {
		int x_Start = 150;
		int botton, top;
		if (selected[i]) {
			top = 10;
			botton = 105;
		}
		else {
			top = 30;
			botton = 125;
		}
		if(i != (game.getPlayerList().get(activePlayer).getNumOfCards()-1)) {
			if (x > (x_Start + 35 * i) && x < (x_Start + 35 + 35 * i) && y < botton && y > top)
				return true;
			if (!selected[i] && selected[i+1] && x > (x_Start + 35 + 35 * i) && x < (x_Start + 70 + 35 * i) && y < botton && y > (botton - 20)) {
				return true;
			}
			if (selected[i] && !selected[i+1] && x > (x_Start + 35 + 35 * i) && x < (x_Start + 70 + 35 * i) && y < (top + 20) && y > top) {
				return true;
			}
		}
		else if(i == (game.getPlayerList().get(activePlayer).getNumOfCards()-1) && x > (x_Start + 35 * i) && x < (x_Start + 35 * i + 70) && y < botton && y > top){
			return true;
		}
		return false;
	}
	
	
	//a inner class used to specify the MouseListener for clicking the card
	private class MouseClicker implements MouseListener{
		@Override
		public void mouseClicked(MouseEvent e) {
			// TODO Auto-generated method stub
		}
		//override the mouseClicked method to perform selecting and unselecting cards
		public void mousePressed(MouseEvent e) {
			int x = e.getX();
			int y = e.getY();
			int currentPlayer = activePlayer+1;
			
			int currentTable = Character.getNumericValue(e.getSource().getClass().getName().charAt(10));
			
			for (int i = 0; i < game.getPlayerList().get(activePlayer).getNumOfCards(); i++) {
				if (currentPlayer == currentTable && BigTwoUI.this.contains(x,y,i)) {
					selected[i] = !selected[i];
				}
			}
			frame.repaint();
			
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}
		@Override
		public void mouseExited(MouseEvent e) {
			// TODO Auto-generated method stub
		}
	}
	//a class for the panel of player1
	private class P1Table extends JPanel{
		//method used to paint the avatar, cards and player name
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			g.setFont(font);
			if(activePlayer == 0) {
				g.drawImage(avatars[0], 0, 15, this);
				g.setColor(Color.BLUE);
	        	g.drawString("You", 10, 15);
	        	CardList hand = playerList.get(0).getCardsInHand();
	        	
	        	for(int i = 0; i < playerList.get(0).getNumOfCards(); i++) {
	        		if (!selected[i]) {
	        			g.drawImage(cards[hand.getCard(i).getSuit()][hand.getCard(i).getRank()],150+i*35,30,this);
	        		}
	        		else {
	        			g.drawImage(cards[hand.getCard(i).getSuit()][hand.getCard(i).getRank()],150+i*35,10,this);
	        		}
	        	}
			}
			else if(playerList.get(0).getName()!= null){
				g.drawImage(avatars[0], 0, 15, this);
				g.setColor(Color.BLACK);
	        	g.drawString(playerList.get(0).getName(), 10, 17);
	        	for(int i = 0; i < playerList.get(0).getNumOfCards(); i++) {
	        		g.drawImage(back,150+i*35,30,this);
	        	}
			}

		}
		//constructor for P1Table
		P1Table(){
			Color color = new Color(96, 96, 96);
			this.setBorder(border);
			this.setBackground(color);
			this.setVisible(true);
			MouseClicker mouseClicker = new MouseClicker();
			this.addMouseListener(mouseClicker);

		}
	}

	//a class for the panel of player2
	private class P2Table extends JPanel{

		//method used to paint the avatar, cards and player name
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			g.setFont(font);
			if(activePlayer == 1) {
				g.drawImage(avatars[1], 0, 15, this);
				g.setColor(Color.BLUE);
	        	g.drawString("You", 10, 15);
	        	CardList hand = playerList.get(1).getCardsInHand();
	        	for(int i = 0; i < playerList.get(1).getNumOfCards(); i++) {
	        		if (!selected[i]) {
	        			g.drawImage(cards[hand.getCard(i).getSuit()][hand.getCard(i).getRank()],150+i*35,30,this);
	        		}
	        		else {
	        			g.drawImage(cards[hand.getCard(i).getSuit()][hand.getCard(i).getRank()],150+i*35,10,this);

	        		}
	        	}
			}
			else if(playerList.get(1).getName()!= null){
				g.drawImage(avatars[1], 0, 15, this);
				g.setColor(Color.BLACK);
	        	g.drawString(playerList.get(1).getName(), 10, 17);
	        	for(int i = 0; i < playerList.get(1).getNumOfCards(); i++) {
	        		g.drawImage(back,150+i*35,30,this);
	        	}
			}

		}
		//constructor for P2Table
		P2Table(){
			Color color = new Color(206, 90, 87);

			this.setBorder(border);
			this.setBackground(color);
			this.setVisible(true);
			MouseClicker mouseClicker = new MouseClicker();
			this.addMouseListener(mouseClicker);
		}

	}
	//a class for the panel of player2
	private class P3Table extends JPanel{
		//player 3
		CardGamePlayer p3 = game.getPlayerList().get(2);
		//method used to paint the avatar, cards and player name

		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			g.setFont(font);
			if(activePlayer == 2) {
				g.drawImage(avatars[2], 0, 15, this);
				g.setColor(Color.BLUE);
	        	g.drawString("You", 10, 15);
	        	CardList hand = p3.getCardsInHand();
	        	for(int i = 0; i < p3.getNumOfCards(); i++) {
	        		if (!selected[i]) {
	        			g.drawImage(cards[hand.getCard(i).getSuit()][hand.getCard(i).getRank()],150+i*35,30,this);
	        		}
	        		else {
	        			g.drawImage(cards[hand.getCard(i).getSuit()][hand.getCard(i).getRank()],150+i*35,10,this);

	        		}
	        	}
			}
			else if(p3.getName()!= null){
				g.drawImage(avatars[2], 0, 15, this);
				g.setColor(Color.BLACK);
	        	g.drawString(p3.getName(), 10, 17);
	        	for(int i = 0; i < p3.getNumOfCards(); i++) {
	        		g.drawImage(back,150+i*35,30,this);
	        	}
			}

		}
		//constructor for P3Table

		P3Table(){
			Color color = new Color(55, 135, 75);

			this.setBorder(border);
			this.setBackground(color);
			this.setVisible(true);
			MouseClicker mouseClicker = new MouseClicker();
			this.addMouseListener(mouseClicker);
		}

	}
	//a class for the panel of player 4
	private class P4Table extends JPanel{
		//player4
		CardGamePlayer p4 = game.getPlayerList().get(3);
		//method used to paint the avatar, cards and player name

		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			g.setFont(font);
			if(activePlayer == 3) {
				g.drawImage(avatars[3], 0, 15, this);
				g.setColor(Color.BLUE);
	        	g.drawString("You", 10, 15);
	        	CardList hand = p4.getCardsInHand();
	        	for(int i = 0; i < p4.getNumOfCards(); i++) {
	        		if (!selected[i]) {
	        			g.drawImage(cards[hand.getCard(i).getSuit()][hand.getCard(i).getRank()],150+i*35,30,this);
	        		}
	        		else {
	        			g.drawImage(cards[hand.getCard(i).getSuit()][hand.getCard(i).getRank()],150+i*35,10,this);

	        		}
	        	}
			}
			else if(p4.getName()!= null){
				g.drawImage(avatars[3], 0, 15, this);
				g.setColor(Color.BLACK);
	        	g.drawString(p4.getName(), 10, 17);
	        	for(int i = 0; i < p4.getNumOfCards(); i++) {
	        		g.drawImage(back,150+i*35,30,this);
	        	}
			}

		}
		//constructor for P4Table
		P4Table(){
			Color color = new Color(153, 204, 245);

			this.setBorder(border);
			this.setBackground(color);
			this.setVisible(true);
			MouseClicker mouseClicker = new MouseClicker();
			this.addMouseListener(mouseClicker);
		}

	}
	// a class for the panel of the table
	private class CardTable extends JPanel{
		//method used to paint the avatar, cards and player name
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			g.setFont(font);
			g.setColor(Color.BLACK);
			g.drawString("<Table>",10,17);
			
			Hand lastHandOnTable = (game.getHandsOnTable().isEmpty()) ? null : game.getHandsOnTable().get(game.getHandsOnTable().size() - 1);
			if (lastHandOnTable != null) {
				for (int i = 0; i < lastHandOnTable.size(); i++) {
					g.drawImage(cards[lastHandOnTable.getCard(i).getSuit()][lastHandOnTable.getCard(i).getRank()],150+i*35,30,this);
				}
			} 

			
		}
		//a constructor for the table
		CardTable(){
			Color color = new Color(225, 177, 106);

			this.setBorder(border);
			this.setBackground(color);
			this.setVisible(true);
		}
	}
	
	//an inner class that extends the JPanel class and contains table of players and card table
	private class BigTwoPanel extends JPanel{
		
		//create a bigTwoPanel
		BigTwoPanel(){
			this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
			this.setVisible(true);
			this.setBackground(UIManager.getColor("Panel.background"));
			
			P1Table p1Table = new P1Table();
			this.add(p1Table);
			P2Table p2Table = new P2Table();
			this.add(p2Table);
			P3Table p3Table = new P3Table();
			this.add(p3Table);
			P4Table p4Table = new P4Table();
			this.add(p4Table);
			CardTable cardTable = new CardTable();
			this.add(cardTable);

			JPanel buttons = new JPanel();
			buttons.setLayout(new BoxLayout(buttons, BoxLayout.X_AXIS));

			playButton = new JButton("Play");
			passButton = new JButton("Pass");
			playButton.addActionListener(new PlayButtonListener());
			passButton.addActionListener(new PassButtonListener());
			buttons.add(playButton);
			buttons.add(new JLabel("        "));
			buttons.add(passButton);
			buttons.setPreferredSize(new Dimension(0,30));
			this.add(buttons);
		}
		//listener for the play button
		private class PlayButtonListener implements ActionListener{
			// call the makeMove method in the BigTwo
			public void actionPerformed(ActionEvent e) {
				BigTwoUI.this.disable();
				if (getSelected() == null) {
					game.IllegalMove();
				}
				else {
					game.makeMove(activePlayer, getSelected());
				}
			}
		}
		//listener for the pass button
		private class PassButtonListener implements ActionListener{
			// call the makeMove method in the BigTwo with selected hand being null
			public void actionPerformed(ActionEvent e) {
				BigTwoUI.this.disable();
				game.makeMove(activePlayer, null);
			}
		}
	}

	/**
	 * a constructor for creating a BigTwoGUI, it initializes the images array and creates the frame
	 * 
	 * @param game A reference to a Big Two card game associates with this GUI.
	 */
	public BigTwoUI(BigTwo game){
		playerList = game.getPlayerList();
		selected = new boolean[13];
		resetSelected();
		avatars = new Image[4];
		avatars[0] = new ImageIcon("Images/Batman.png").getImage();
		avatars[1] = new ImageIcon("Images/Flash.png").getImage();
		avatars[2] = new ImageIcon("Images/Green.png").getImage();
		avatars[3] = new ImageIcon("Images/Super.png").getImage();
		cards = new Image[4][13];
		char suit[] = {'d','c','h','s'};
		char rank[] = {'a','2','3','4','5','6','7','8','9','t','j','q','k'};
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 13; j++) {
				cards[i][j] = new ImageIcon("Images/" + rank[j] + suit[i] + ".gif").getImage();
			}
		}
		this.game = game;
		
		frame = new JFrame(title);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(new Dimension(1080,800));
		
		ChatBox chatBox = new ChatBox();
		MenuBar menuBar = new MenuBar();
		bigTwoPanel = new BigTwoPanel();
		

		frame.add(bigTwoPanel,BorderLayout.CENTER);
		frame.add(menuBar,BorderLayout.NORTH);
		frame.add(chatBox,BorderLayout.EAST);
		frame.setVisible(true);

	}
	
	/**
	 * Get the index of the active player
	 * 
	 * @return activePlayer the index of the active player (i.e., the player who can make a move)
	 */
	public int GetActivePlayer() {
		return activePlayer;
	}

	/**
	 * Sets the index of the active player.
	 * 
	 * @param activePlayer the index of the active player (i.e., the player who can make a move)
	 *                     
	 */
	public void setActivePlayer(int activePlayer)
	{
		if (activePlayer < 0 || activePlayer >= playerList.size()) {
			this.activePlayer = -1;
		} else {
			this.activePlayer = activePlayer;
		}
	}
	/**
	 * Repaints the user interface.
	 */
	public void repaint() {
		frame.repaint();
	}
	
	/**
	 * Prints the specified string to the message area of the card game user
	 * interface.
	 * 
	 * @param msg the string to be printed to the message area of the card game user
	 *            interface
	 */
	public void printMsg(String msg) {
		msgArea.append(msg);
	}
	/**
	 * Prints the specified string to the chat area of the card game user
	 * interface.
	 * 
	 * @param chat the string to be printed to the chat area of the card game user
	 *            interface
	 */
	public void printChat(String chat) {
		chatArea.append(chat);
	}
	/**
	 * Clears the message area of the card game user interface.
	 */
	public void clearMsgArea() {
		msgArea.setText(null);
		
	}
	/**
	 * Resets the card game user interface.
	 */
	public void reset() {
		frame.setVisible(false);
		game = new BigTwo();
		for (int i = 0; i < game.getPlayerList().size(); i++) {
			game.getPlayerList().get(i).setName("Player " + i);
		}
		BigTwoDeck deck = new BigTwoDeck();
		game.start(deck);
	}
	/**
	 * Enables user interactions.
	 */
	public void enable() {
		bigTwoPanel.setEnabled(true);
		playButton.setEnabled(true);
		passButton.setEnabled(true);		
	}
	/**
	 * Disables user interactions.
	 */
	public void disable() {
		bigTwoPanel.setEnabled(false);
		playButton.setEnabled(false);
		passButton.setEnabled(false);		
	}
	/**
	 * Prompts active player to select cards and make his/her move.
	 */
	public void promptActivePlayer() {
		printMsg(game.getPlayerList().get(game.getCurrentPlayerIdx()).getName() + "'s turn: \n");
		resetSelected();

	}
	
	//Resets the list of selected cards to an empty list.
	private void resetSelected() {
		for (int j = 0; j < selected.length; j++) {
			selected[j] = false;
		}
	}
	
	//Returns an array of indices of the cards selected through the UI.
	private int[] getSelected() {

		int[] cardIdx = null;
		int count = 0;
		for (int j = 0; j < selected.length; j++) {
			if (selected[j]) {
				count++;
			}
		}

		if (count != 0) {
			cardIdx = new int[count];
			count = 0;
			for (int j = 0; j < selected.length; j++) {
				if (selected[j]) {
					cardIdx[count] = j;
					count++;
				}
			}
		}
		return cardIdx;
	}
	//The listener for the connect button
	private class ConnectMenuItemListener implements ActionListener{
		//override the actionPerformed method to create a new game object
		public void actionPerformed(ActionEvent e) {
			game.GetClient().connect();
			BigTwoUI.this.repaint();
		}
	}
	//The listener for the quit button
	private class QuitMenuItemListener implements ActionListener{

		//exit the game
		public void actionPerformed(ActionEvent e) {
			System.exit(0);			
		}	
	}
	//The listener for the quit button
	private class ClearMsgMenuItemListener implements ActionListener{

		//exit the game
		public void actionPerformed(ActionEvent e) {
			chatArea.setText(null);			
		}	
	}
}