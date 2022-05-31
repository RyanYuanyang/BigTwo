/**
 * The Flush class is a subclass of hand used to model a Flush hand. 
 * It overrides the isValid and the getType methods.
 * 
 * @author Tu Ryan Yuanyang
 *
 */
public class Flush extends Hand{
	/**
	 * An inherited constructor which calls the constructor of its super class 
	 * 
	 * @param player The player that this hand belongs to
	 * @param cards The card list representing this hand
	 */
	public Flush(CardGamePlayer player, CardList cards) {
		super(player, cards);
	}
	/**
	 * a method for checking if this is a valid hand
	 * 
	 * @return whether this is a valid hand
	 */
	public boolean isValid() {
		if(this.size() != 5) {
			return false;
		}
		int suit = this.getCard(0).getSuit();
		for (int i = 0; i < this.size(); i++) {
			if (this.getCard(i).getSuit() != suit) {
				return false;
			}
		}
		return true;
	}
	/**
	 * a method for returning a string specifying the type of this hand
	 * 
	 * @return a string specifying the type of this hand
	 */
	public String getType() {
		return "Flush";
	}

}
