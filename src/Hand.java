import java.util.ArrayList;

/**
 * The Hand class is a subclass of the CardList class and is used to model a hand of cards. It has 
 * a private instance variable for storing the player who plays this hand. It also has methods for 
 * getting the player of this hand, checking if it is a valid hand, getting the type of this hand, 
 * getting the top card of this hand, and checking if it beats a specified hand. 
 * 
 * @author Tu Ryan Yuanyang
 *
 */
public abstract class Hand extends CardList{

	//the player who plays this hand.
	private CardGamePlayer player;
	/**
	 * a method for retrieving the player of this hand.
	 * 
	 * @return player The player of this hand
	 */
	public CardGamePlayer getPlayer() {
		return player;
	}
	/**
	 * a method for retrieving the top card of this hand
	 * 
	 * @return Card The top card of this hand
	 */
	public Card getTopCard() {
		if (getType() == "Single") {
			return getCard(0);
		}
		else if (getType() == "Pair"){
			this.sort();
			return getCard(1);
		}
		else if (getType() == "Triple"){
			this.sort();
			return getCard(2);
		}
		else if (getType() == "Straight" && getType() == "Flush" && getType() == "StraightFlush"){
			this.sort();
			return getCard(4);
		}
		else if (getType() == "Full House"){
			this.sort();
			if (this.getCard(1).getRank() == this.getCard(2).getRank()) {
				return this.getCard(2);
			}
			else {
				return this.getCard(4);
			}
		}
		else{
			this.sort();
			if (this.getCard(3).getRank() == this.getCard(4).getRank()) {
				return this.getCard(4);
			}
			else {
				return this.getCard(3);
			}		
		}

	}
	/**
	 * a method for checking if this hand beats a specified hand.
	 * 
	 * @param hand The target hand to compared
	 * @return Whether this hand beats the specified hand
	 */
	public boolean beats(Hand hand) {

		if (this.size() == hand.size()) {
			if (this.getType() != hand.getType()) {
				ArrayList<String> combinations = new ArrayList<String>();
				combinations.add("Straight");
				combinations.add("Flush");
				combinations.add("Full House");
				combinations.add("Quad");
				combinations.add("StraightFlush");
				
				return combinations.indexOf(this.getType()) > combinations.indexOf(hand.getType());
			}
			else {
				if (this.getTopCard().compareTo(hand.getTopCard()) == 1) {
					return true;
				}
				else{
					return false;
				}
			}
		}
		else {
			return false;
		}

	}
	/**
	 * an abstract method for checking if this is a valid hand
	 * 
	 * @return null
	 */
	public abstract boolean isValid();
	/**
	 * an abstract method for returning a string specifying the type of this hand	
	 * 
	 * @return null
	 */
	public abstract String getType();
	
	/**
	 * a constructor for building a hand with the specified player and list of cards
	 * 
	 * @param player The player that this hand belongs to
	 * @param cards The card list representing this hand
	 */
	public Hand(CardGamePlayer player, CardList cards) {
		this.player = player;
		for (int i = 0; i < cards.size(); i++){
			this.addCard(cards.getCard(i));
		}
	}
	/**
	 * Override the print method to print the cards in this list to the GUI.
	 * 
	 * @param printFront a boolean value specifying whether to print the face (true)
	 *                   or the black (false) of the cards
	 * @param printIndex a boolean value specifying whether to print the index in
	 *                   front of each card
	 * @param ui the target GUI to print message on
	 * 
	 */
	public void print(boolean printFront, boolean printIndex, BigTwoUI ui) {
		if (this.size() > 0) {
			for (int i = 0; i < this.size(); i++) {
				String string = "";
				if (printIndex) {
					string = i + " ";
				}
				if (printFront) {
					string = string + "[" + this.getCard(i) + "]";
				} else {
					string = string + "[  ]";
				}
				if (i % 13 != 0) {
					string = " " + string;
				}
				ui.printMsg(string);
				if (i % 13 == 12 || i == this.size() - 1) {
					ui.printMsg("");
				}
			}
		} else {
			ui.printMsg("[Empty]");
		}
	}
}
