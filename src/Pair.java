/**
 * The Pair class is a subclass of hand used to model a Pair hand. 
 * It overrides the isValid and the getType methods.
 * 
 * @author Tu Ryan Yuanyang
 *
 */
public class Pair extends Hand{
	/**
	 * An inherited constructor which calls the constructor of its super class 
	 * 
	 * @param player The player that this hand belongs to
	 * @param cards The card list representing this hand
	 */
	public Pair(CardGamePlayer player, CardList cards) {
		super(player, cards);
	}
	/**
	 * a method for checking if this is a valid hand
	 * 
	 * @return whether this is a valid hand
	 */
	public boolean isValid() {
		if (this.size() == 2 && this.getCard(0).getRank() == this.getCard(1).getRank()) {
			return true;
		}
		else {
			return false;
		}
	}
	/**
	 * a method for returning a string specifying the type of this hand
	 * 
	 * @return a string specifying the type of this hand
	 */
	public String getType() {
		return "Pair";
	}

}
