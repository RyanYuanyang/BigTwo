import java.util.ArrayList;

import javax.swing.JOptionPane;

/**
 * The BigTwo class implements the CardGame interface and is used to model a Big Two card game.
 * It has private instance variables for storing the number of players, a deck of cards, a list of players,
 * a list of hands played on the table, an index of the current player, and a user interface.
 * 
 * 
 * @author Tu Ryan Yuanyang
 *
 */
public class BigTwo implements CardGame{
	//an integer specifying the number of players.
	private int numOfPlayers;
	//a deck of cards.
	private Deck deck;
	//a list of players.
	private ArrayList<CardGamePlayer> playerList;
	//a list of hands played on the table
	private ArrayList<Hand> handsOnTable;
	//an integer specifying the index of the current player
	private int currentPlayerIdx;
	//a BigTwoUI object for providing the user interface
	private BigTwoUI ui;
	// a BigTwoClient object for the client of this game
	private BigTwoClient client;
	/**
	 * A constructor for creating a Big Two card game. It initialize the 4 players and the UI
	 */
	public BigTwo(){
		playerList = new ArrayList<CardGamePlayer>();
		playerList.clear();
		CardGamePlayer p1 = new CardGamePlayer();
		CardGamePlayer p2 = new CardGamePlayer();
		CardGamePlayer p3 = new CardGamePlayer();
		CardGamePlayer p4 = new CardGamePlayer();
		p1.setName(null);
		p2.setName(null);
		p3.setName(null);
		p4.setName(null);
		this.playerList.add(p1);
		this.playerList.add(p2);
		this.playerList.add(p3);
		this.playerList.add(p4);

		this.handsOnTable = new ArrayList<Hand>();
		ui = new BigTwoUI(this);
		ui.disable();
		client = new BigTwoClient(this,ui);
	}
	/**
	 * a method for getting the number of players
	 * 
	 * @return numOfPlayers A int value storing the number of players
	 */
	public int getNumOfPlayers() {
		return numOfPlayers;
	}
	/**
	 * a method for retrieving the deck of cards being used
	 * 
	 * @return deck The deck of cards being used
	 */
	public Deck getDeck() {
		return deck;
	}
	/**
	 * a method for retrieving the list of players.
	 * 
	 * @return playerlist An arraylist storing the list of players
	 */
	public ArrayList<CardGamePlayer> getPlayerList(){
		return playerList;
	}
	/**
	 * a method for retrieving the list of hands played on the table.
	 * 
	 * @return handsOnTable The list of hands played on the table
	 */
	public ArrayList<Hand> getHandsOnTable(){
		return handsOnTable;
	}
	
	/**
	 * a method for retrieving the index of the current player.
	 * 
	 * @return currentPlayerIdx A int value storing the current player's index
	 */
	public int getCurrentPlayerIdx() {
		return currentPlayerIdx;
	}
	
	/**
	 * a method for retrieving the client
	 * 
	 * @return client A BigTwoClient object of the client
	 */
	public BigTwoClient GetClient() {
		return client;
	}
	/**
	 * a method for starting/restarting the game with a given shuffled deck of cards
	 * 
	 * @param deck The current deck of cards
	 */
	public void start(Deck deck) {
	
		handsOnTable.clear();
		for (int i = 0; i < playerList.size(); i++) {
			playerList.get(i).removeAllCards();
		}
		
		for (int i = 0; i < playerList.size();i++) {
			for(int j = 0; j < 13; j++) {
				playerList.get(i).addCard(deck.getCard(i*13 + j));
			}
			playerList.get(i).sortCardsInHand();
		}
		
		for (int i = 0; i < playerList.size(); i++) {
			BigTwoCard d3 = new BigTwoCard(0,2);
			if(playerList.get(i).getCardsInHand().contains(d3)) {
				currentPlayerIdx = i;
			}
		}
		
		if (ui.GetActivePlayer() == this.getCurrentPlayerIdx()) {
			ui.enable();
		}
		ui.promptActivePlayer();

	}
	/**
	 * a method for making a move by a player with the specified index using the cards specified by the list of indices.
	 *  
	 * @param playerIdx The index of the player to make the move
	 * @param cardIdx An array storing the input of the player
	 */
	public void makeMove(int playerIdx, int[] cardIdx) {
		client.sendMessage(new CardGameMessage(CardGameMessage.MOVE, -1, cardIdx));
		
	}
	
	//a string private instance variable specifying the restraint to the next hand
	private static String restraint;
	//a integer specifying the index of the last player
	private static int lastPlayerIdx;
	/**
	 * a method for checking a move made by a player
	 * 
	 * @param playerIdx The player that makes the move
	 * @param cardIdx The index of card the player played
	 */
	public void checkMove(int playerIdx, int[] cardIdx) {
		if (handsOnTable.isEmpty()) {
			restraint = "ComWithDia3";
		}
		else if (playerIdx == lastPlayerIdx) {
			restraint = "Any";
		}
		
		CardList played = playerList.get(currentPlayerIdx).play(cardIdx);
		Hand lastHandOnTable = (handsOnTable.isEmpty()) ? null : handsOnTable.get(handsOnTable.size() - 1);
		Hand legalHand = (played == null ? null: composeHand(playerList.get(currentPlayerIdx) , played));

		
		if(restraint == "ComWithDia3") {
			
			BigTwoCard d3 = new BigTwoCard(0,2);
			if (played == null |  legalHand == null) {
				IllegalMove();
			}
			else {
				
				if(legalHand.contains(d3)) {
					lastPlayerIdx = playerIdx;
					NextRound(legalHand,false);
				}
				else {
					IllegalMove();
				}
				
			}
		}
		else if(restraint == "Any") {
			if(legalHand != null && played != null) {
				NextRound(legalHand,false);
			}
			else {
				IllegalMove();
			}
		}
		else if(played == null) {
			NextRound(legalHand,true);
		}
		else if(legalHand != null && legalHand.beats(lastHandOnTable)){
			handsOnTable.add(legalHand);
			lastPlayerIdx = playerIdx;
			NextRound(legalHand,false);
		}
		else {
			IllegalMove();
		}
			
	}
	
	/**
	 * print the message when the move is illegal
	 */
	public void IllegalMove() {
		ui.printMsg("Not a legal move!!!\n-----\n");
		ui.repaint();
		ui.promptActivePlayer();
	}
	
	//a method to update the instance variables and the game interface when goes to the next round
	private void NextRound(Hand hand, boolean pass) {

		if (hand != null) {
			ui.printMsg("{" + hand.getType() + "} ");
			hand.print(true, false, ui);
			ui.printMsg("\n-----\n");
		} else {
			ui.printMsg("{Pass}\n-----\n");
		}
		if (pass != true) {
			handsOnTable.add(hand);
			restraint = hand.getType();
			playerList.get(currentPlayerIdx).removeCards(hand);
		}
		if(endOfGame()) {
			String endMsg = "";
			endMsg = endMsg + "Game ends\n";
			
			for (int i=0; i<playerList.size(); i++) {
				
				if (i==getCurrentPlayerIdx()) {
					endMsg = endMsg + "Player " + getCurrentPlayerIdx() + " wins the game.\n";
				}
				else {
					endMsg = endMsg + ("Player " + i + " has "+ playerList.get(i).getNumOfCards() + " cards in hand.\n");
				}
			}
			JOptionPane.showMessageDialog(null,endMsg); 
			ui.disable();
			return;
		}
		currentPlayerIdx++;
		if (currentPlayerIdx==4) {
			currentPlayerIdx=0;
		}
		ui.repaint();
		ui.promptActivePlayer();	
	}
	/**
	 * a method for checking if the game ends
	 */
	public boolean endOfGame() {
		if(playerList.get(currentPlayerIdx).getCardsInHand().size()==0) {
			return true;
		}
		else
			return false;
	}
	/**
	 * a method for starting a Big Two card game
	 * 
	 * @param args Unused
	 */
	public static void main(String[] args) {
		BigTwo bigTwo = new BigTwo();
		BigTwoDeck gameDeck = new BigTwoDeck();
	}
	/**
	 * a method for returning a valid hand from the specified list of cards of the player
	 * 
	 * @param player The player of the hand
	 * @param cards The list of cards of the player
	 * @return hand The specific hand the player played
	 */
	public static Hand composeHand(CardGamePlayer player, CardList cards) {
		Hand hand = new Single(player,cards);
		if(hand.isValid()) {
			return hand;
		}
		hand = new Pair(player,cards);
		if(hand.isValid()) {
			return hand;
		}
		hand = new Triple(player,cards);
		if(hand.isValid()) {
			return hand;
		}		
		hand = new Straight(player,cards);
		if(hand.isValid()) {
			return hand;
		}
		hand = new Flush(player,cards);
		if(hand.isValid()) {
			return hand;
		}
		hand = new StraightFlush(player,cards);
		if(hand.isValid()) {
			return hand;
		}
		hand = new Quad(player,cards);
		if(hand.isValid()) {
			return hand;
		}
		hand = new FullHouse(player,cards);
		if(hand.isValid()) {
			return hand;
		}
		return null;
	}
}
